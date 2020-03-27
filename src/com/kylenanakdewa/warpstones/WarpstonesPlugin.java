package com.kylenanakdewa.warpstones;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

import com.kylenanakdewa.core.common.ConfigAccessor;
import com.kylenanakdewa.warpstones.items.WarpItemsManager;
import com.kylenanakdewa.warpstones.teleports.TeleportCommands;

/**
 * Warpstones for Bukkit
 * <p>
 * A uniquely immersive warping system, based around floating structures known
 * as Warpstones.
 *
 * @author Kyle Nanakdewa
 */
public final class WarpstonesPlugin extends JavaPlugin {

	@Deprecated
	public static WarpstonesPlugin plugin;

	/** The Warpstones available on this server. */
	private static final Map<String, Warpstone> warpstones = new HashMap<String, Warpstone>();

	/** ConfigAccessor for warpstones.yml */
	@Deprecated
	private static ConfigAccessor warpstonesConfigAccessor;

	/** Compass task ID */
	@Deprecated
	private static int compassTaskId = -1;

	@Override
	public void onEnable() {
		plugin = this;
		warpstonesConfigAccessor = new ConfigAccessor("warpstones.yml", this);

		reload();

		registerCommands();

		// Register event listeners
		getServer().getPluginManager().registerEvents(new EventListener(), this);

		// Set up Warp Items
		new WarpItemsManager(this);
	}

	@Override
	public void onDisable() {
		saveWarpstones();
		// HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
	}

	/**
	 * Reloads the Warpstones plugin.
	 */
	public void reload() {
		onDisable();

		// Load default config
		saveDefaultConfig();

		// Compass task
		// if (WarpstonesConfig.compassesShowDistances)
		setupCompassTask();

		loadWarpstones();
	}

	/**
	 * Registers Warpstones plugin commands.
	 */
	private void registerCommands() {
		getCommand("warpstones").setExecutor(new WarpstoneCommands(this));
		getCommand("tp").setExecutor(new TeleportCommands());
		getCommand("tphere").setExecutor(new TeleportCommands());
	}

	/**
	 * Sets up the compass task.
	 */
	@Deprecated
	private void setupCompassTask() {
		if (compassTaskId > 0)
			return;
		compassTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				// Skip players more than 25k blocks from origin
				if (player.getLocation().getX() > 25000 || player.getLocation().getX() < -25000
						|| player.getLocation().getZ() > 25000 || player.getLocation().getZ() < -25000)
					return;

				if (((player.getInventory().getItemInMainHand() != null
						&& player.getInventory().getItemInMainHand().getType().equals(Material.COMPASS))
						|| (player.getInventory().getItemInOffHand() != null
								&& player.getInventory().getItemInOffHand().getType().equals(Material.COMPASS)))
						&& player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {

					// Create the scoreboard
					Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
					Objective obj = board.registerNewObjective("ws_distances", "dummy", "Distance to");
					obj.setDisplaySlot(DisplaySlot.SIDEBAR);

					// Add entries
					WarpPlayer warpData = new WarpPlayer(player);
					Location pLoc = player.getLocation();
					Warpstone nearestStone = Warpstone.getNearest(player.getLocation(), 100, false);
					if (nearestStone != null) {
						// Admins see ID of nearest stone
						String nearestName = !player.hasPermission("warpstones.manage") ? "Nearest Warpstone"
								: "Nearest: " + nearestStone.getIdentifier();
						Score nearest = obj.getScore(nearestName);
						nearest.setScore((int) nearestStone.getLocation().distance(pLoc));
					}
					Warpstone homeStone = warpData.getHome();
					if (homeStone != null && homeStone.getLocation().getWorld().equals(pLoc.getWorld())) {
						Score home = obj.getScore("Home");
						home.setScore((int) homeStone.getLocation().distance(pLoc));
					}
					Warpstone lastStone = warpData.getLast();
					if (lastStone != null && lastStone.getLocation().getWorld().equals(pLoc.getWorld())) {
						Score last = obj.getScore("Last Warpstone");
						last.setScore((int) lastStone.getLocation().distance(pLoc));
					}
					Warpstone spawnStone = Warpstone.getSpawn();
					if (spawnStone != null && spawnStone.getLocation().getWorld().equals(pLoc.getWorld())) {
						Score spawn = obj.getScore("Spawn");
						spawn.setScore((int) spawnStone.getLocation().distance(pLoc));
					}

					player.setScoreboard(board);
				} else {
					player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
				}
			}
		}, 10, 10);
	}

	/**
	 * Gets the ConfigurationSection for the warpstones file.
	 */
	static ConfigurationSection getWarpstonesFile() {
		ConfigurationSection config = warpstonesConfigAccessor.getConfig().getConfigurationSection("warpstones");
		if (config == null)
			config = warpstonesConfigAccessor.getConfig().createSection("warpstones");
		return config;
	}

	/**
	 * Gets a Map of Warpstones on the server, sorted by their identifier.
	 */
	public static Map<String, Warpstone> getWarpstones() {
		return warpstones;
	}

	/**
	 * Deletes a Warpstone. This action is permanent.
	 *
	 * @param warpstone the identifier of the Warpstone to delete
	 */
	static void deleteWarpstone(String identifier) {
		warpstones.remove(identifier);
		getWarpstonesFile().set(identifier, null);
	}

	/**
	 * Loads Warpstones from file.
	 */
	private void loadWarpstones() {
		ConfigurationSection warpstoneFile = getWarpstonesFile();
		warpstoneFile.getKeys(false).forEach(warpstoneName -> warpstones.put(warpstoneName,
				Warpstone.loadFromConfig(warpstoneName, warpstoneFile.getConfigurationSection(warpstoneName))));
	}

	/**
	 * Saves changes to the Warpstones list.
	 */
	public void saveWarpstones() {
		warpstones.forEach(
				(warpstoneName, warpstone) -> getWarpstonesFile().set(warpstoneName, warpstone.saveToConfig()));
		warpstonesConfigAccessor.saveConfig();
	}

}
