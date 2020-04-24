package com.kylenanakdewa.warpstones;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.core.common.savedata.PlayerSaveDataSection;
import com.kylenanakdewa.warpstones.teleports.events.PlayerTeleportDelayBeginEvent;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;
import com.kylenanakdewa.warpstones.warpstone.WarpstoneManager;
import com.kylenanakdewa.warpstones.warpstone.events.PlayerWarpEvent;
import com.kylenanakdewa.warpstones.warpstone.events.PlayerWarpEvent.WarpCause;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.kylenanakdewa.core.characters.players.PlayerCharacter;

/**
 * Holds Warpstones data and methods for Player Characters.
 *
 * @author Kyle Nanakdewa
 */
public class WarpstonesPlayerData extends PlayerSaveDataSection {

	public WarpstonesPlayerData(PlayerCharacter player) {
		super(player, Bukkit.getPluginManager().getPlugin("Warpstones"));
	}

	public WarpstonesPlayerData(OfflinePlayer player) {
		super(player, Bukkit.getPluginManager().getPlugin("Warpstones"));
	}

	/**
	 * Gets the Warpstones that this player has visited recently, along with the
	 * time that the Warpstone was visited.
	 * <p>
	 * The time will be milliseconds since midnight, January 1, 1970 UTC.
	 *
	 * @return a map of recent Warpstones, and the times they were visited
	 */
	public LinkedHashMap<Warpstone, Long> getRecentWarpstones() {
		LinkedHashMap<Warpstone, Long> map = new LinkedHashMap<Warpstone, Long>();

		ConfigurationSection recentData = data.getConfigurationSection("recent-warpstones");
		if (recentData != null) {
			// Load each entry
			for (Entry<String, Object> entry : recentData.getValues(false).entrySet()) {
				String wsIdentifier = entry.getKey();
				long timestamp = (long) entry.getValue();

				Warpstone warpstone = WarpstoneManager.get().getWarpstone(wsIdentifier);
				if (warpstone != null) {
					map.put(warpstone, timestamp);
				} else {
					// Warn admins if Warpstone is null
					Utils.notifyAdminsError("Recent Warpstone " + wsIdentifier + " for player " + character.getName()
							+ CommonColors.ERROR + " was not found.");
				}
			}
		}

		return map;
	}

	/**
	 * Sets the Warpstones that this player has visited recently, along with the
	 * time that the Warpstone was visited.
	 * <p>
	 * The time should be milliseconds since midnight, January 1, 1970 UTC.
	 *
	 * @param map a map of recent Warpstones, and the times they were visited
	 */
	public void setRecentWarpstones(LinkedHashMap<Warpstone, Long> map) {
		ConfigurationSection recentData = data.createSection("recent-warpstones");

		// Save each entry
		for (Entry<Warpstone, Long> entry : map.entrySet()) {
			String wsIdentifier = entry.getKey().getIdentifier();
			long timestamp = entry.getValue();

			recentData.set(wsIdentifier, timestamp);
		}
	}

	/**
	 * Saves a Warpstone that this player has visited recently.
	 *
	 * @param warpstone the Warpstone that this player visited
	 * @param timestamp the time that they visited the Warpstone, in milliseconds
	 *                  since midnight, January 1, 1970 UTC.
	 * @param limit     the total number of recent Warpstones that should be saved
	 */
	public void addRecentWarpstone(Warpstone warpstone, long timestamp, int limit) {
		LinkedHashMap<Warpstone, Long> map = getRecentWarpstones();
		map.put(warpstone, timestamp);

		// If map is over the limit, remove entries from the top
		while (map.size() > limit) {
			Warpstone entry = map.keySet().iterator().next();
			map.remove(entry);
		}

		setRecentWarpstones(map);
	}

	/**
	 * Saves a Warpstone that this player has visited recently. The current time
	 * will be recorded.
	 *
	 * @param warpstone the Warpstone that this player visited
	 * @param limit     the total number of recent Warpstones that should be saved
	 */
	public void addRecentWarpstone(Warpstone warpstone, int limit) {
		long timestamp = System.currentTimeMillis();
		addRecentWarpstone(warpstone, timestamp, limit);
	}

	/**
	 * Gets the last Warpstone that this player visited.
	 * <p>
	 * If this player has not visited any Warpstones, this will return null.
	 *
	 * @return the most recent Warpstone
	 */
	public Warpstone getMostRecentWarpstone() {
		Warpstone[] recentWarpstones = getRecentWarpstones().keySet().toArray(new Warpstone[0]);

		// Make sure player has at least one recent Warpstone
		if (recentWarpstones.length == 0) {
			return null;
		}

		// Get the last element of the array
		Warpstone mostRecentWs = recentWarpstones[recentWarpstones.length - 1];
		return mostRecentWs;
	}

	/**
	 * Gets the last three Warpstones that this player visited, with the most recent
	 * Warpstone listed first.
	 *
	 * @return the three most recent Warpstones
	 */
	public LinkedHashMap<Warpstone, Long> getRecentThreeWarpstones() {
		LinkedHashMap<Warpstone, Long> recentWarpstones = getRecentWarpstones();

		Warpstone[] lastThree = new Warpstone[3];
		Warpstone[] allStones = recentWarpstones.keySet().toArray(new Warpstone[0]);
		if (allStones.length >= 1) {
			lastThree[0] = allStones[allStones.length - 1];
		}
		if (allStones.length >= 2) {
			lastThree[1] = allStones[allStones.length - 2];
		}
		if (allStones.length >= 3) {
			lastThree[2] = allStones[allStones.length - 3];
		}

		LinkedHashMap<Warpstone, Long> recentThree = new LinkedHashMap<Warpstone, Long>();

		for (int i = 0; i < lastThree.length; i++) {
			Warpstone warpstone = lastThree[i];
			if (warpstone != null) {
				long time = recentWarpstones.get(warpstone);
				recentThree.put(warpstone, time);
			}
		}

		return recentThree;
	}

	/**
	 * Teleports this player to the specified location, with an optional delay.
	 *
	 * @param destination the location that this player should be teleported to
	 * @param cause       the cause of the teleportation
	 * @param delay       true to have a delay before the player is teleported
	 * @return true if the teleport was successful
	 */
	public boolean teleport(Location destination, TeleportCause cause, boolean delay) {
		// Make sure player is online
		if (!character.isOnline()) {
			Utils.notifyAdminsError(
					"Cannot teleport " + character.getName() + CommonColors.ERROR + ", they are offline.");
			return false;
		}

		Player player = character.getPlayer().getPlayer();

		// If delaying, calculate the delay, and schedule teleport
		if (delay) {
			int delayLength = getTeleportDelay();

			PlayerTeleportDelayBeginEvent event = new PlayerTeleportDelayBeginEvent(player, destination, delayLength);
			plugin.getServer().getPluginManager().callEvent(event);

			// This method will be re-called when delay completes, nothing else to do here
			return !event.isCancelled();
		}

		else {
			// Complete the teleport
			return player.teleport(destination, cause);
		}

	}

	/**
	 * Teleports this player to the specified location, with an optional delay.
	 *
	 * @param destination the location that this player should be teleported to
	 * @param delay       true to have a delay before the player is teleported
	 * @return true if the teleport was successful
	 */
	public boolean teleport(Location destination, boolean delay) {
		return teleport(destination, TeleportCause.PLUGIN, delay);
	}

	/**
	 * Returns true if this player is above the health threshold needed for reduced
	 * teleport delays, and Warpstone auto-saves.
	 * <p>
	 * If the player's health is at or above the threshold (default 20 health), this
	 * will return true. If their health is below the threshold, this will return
	 * false.
	 * <p>
	 * If the player is offline, an exception will be thrown.
	 *
	 * @return true if this player gets auto-save and reduced delays
	 */
	public boolean isAboveHealthThreshold() {
		Player player = character.getPlayer().getPlayer();
		double health = player.getHealth();

		WarpstonesConfig config = ((WarpstonesPlugin) plugin).getWarpstonesConfig();
		double healthThreshold = config.TP_REDUCED_DELAY_HEALTH;

		return health >= healthThreshold;
	}

	/**
	 * Gets the appropriate delay length before this player is teleported.
	 * <p>
	 * If the player has the permission "warpstones.tp.instant", this will return 0.
	 * <p>
	 * If the player's health is above the threshold (default 20 health), they get a
	 * reduced delay (default 2s). If their health is below the threshold, they get
	 * a longer delay (default 5s).
	 * <p>
	 * If the player is offline, an exception will be thrown.
	 *
	 * @return the delay length, in seconds
	 */
	public int getTeleportDelay() {
		Player player = character.getPlayer().getPlayer();

		if (player.hasPermission("warpstones.tp.instant")) {
			return 0;
		}

		WarpstonesConfig config = ((WarpstonesPlugin) plugin).getWarpstonesConfig();

		if (isAboveHealthThreshold()) {
			return config.TP_REDUCED_DELAY;
		} else {
			return config.TP_FULL_DELAY;
		}
	}

	/**
	 * Teleports this player to their home bed, with an optional delay.
	 * <p>
	 * If the player does not have a home bed, they are notified, and not
	 * teleported.
	 *
	 * @param cause the cause of the teleportation
	 * @param delay true to have a delay before the player is teleported
	 * @return true if the teleport was successful
	 */
	public boolean teleportHome(TeleportCause cause, boolean delay) {
		// Make sure player is online
		if (!character.isOnline()) {
			Utils.notifyAdminsError(
					"Cannot teleport " + character.getName() + CommonColors.ERROR + " home, they are offline.");
			return false;
		}

		Player player = character.getPlayer().getPlayer();

		// Get bed spawn location
		Location destination = player.getBedSpawnLocation();
		if (destination == null) {
			Utils.sendActionBar(player,
					CommonColors.ERROR + "You have no home bed or respawn anchor, or it was obstructed.");
			return false;
		}

		// Complete teleport
		Utils.sendActionBar(player, ChatColor.BLUE + "Warping home...");
		return teleport(destination, cause, delay);
	}

	/**
	 * Teleports this player to their home bed, with an optional delay.
	 * <p>
	 * If the player does not have a home bed, they are notified, and not
	 * teleported.
	 *
	 * @param delay true to have a delay before the player is teleported
	 * @return true if the teleport was successful
	 */
	public boolean teleportHome(boolean delay) {
		return teleportHome(TeleportCause.PLUGIN, delay);
	}

	/**
	 * Teleports this player to spawn, with an optional delay.
	 * <p>
	 * If the server has a Spawn Warpstone set, they will be warped there.
	 * <p>
	 * If the server does not have a Spawn Warpstone, they will be teleported to the
	 * spawn location of their current world.
	 *
	 * @param cause the cause of the teleportation
	 * @param delay true to have a delay before the player is teleported
	 * @return true if the teleport was successful
	 */
	public boolean teleportSpawn(TeleportCause cause, boolean delay) {
		// Make sure player is online
		if (!character.isOnline()) {
			Utils.notifyAdminsError(
					"Cannot teleport " + character.getName() + CommonColors.ERROR + " to spawn, they are offline.");
			return false;
		}

		Player player = character.getPlayer().getPlayer();
		Location destination;

		// Get Warpstone spawn location
		Warpstone spawnWarpstone = WarpstoneManager.get().getSpawnWarpstone();
		if (spawnWarpstone != null) {
			destination = spawnWarpstone.getExitLocation();
			if (destination == null) {
				Utils.sendActionBar(player, CommonColors.ERROR + "Warpstone not found.");
				return false;
			}
		} else {
			destination = player.getWorld().getSpawnLocation();
		}
		destination.setDirection(player.getLocation().getDirection());

		// Complete teleport
		Utils.sendActionBar(player, ChatColor.BLUE + "Warping to spawn...");
		return teleport(destination, cause, delay);
	}

	/**
	 * Teleports this player to spawn, with an optional delay.
	 * <p>
	 * If the server has a Spawn Warpstone set, they will be warped there.
	 * <p>
	 * If the server does not have a Spawn Warpstone, they will be teleported to the
	 * spawn location of their current world.
	 *
	 * @param delay true to have a delay before the player is teleported
	 * @return true if the teleport was successful
	 */
	public boolean teleportSpawn(boolean delay) {
		return teleportSpawn(TeleportCause.PLUGIN, delay);
	}

	/**
	 * Teleports this player to the specified Warpstone, with an optional delay.
	 * <p>
	 * If the Warpstone does not exist, they are notified, and not teleported.
	 *
	 * @param warpstone the Warpstone to teleport to
	 * @param cause     the cause of the warp
	 * @param delay     true to have a delay before the player is teleported
	 * @return true if the teleport was successful
	 */
	public boolean warp(Warpstone warpstone, WarpCause cause, boolean delay) {
		// Make sure player is online
		if (!character.isOnline()) {
			Utils.notifyAdminsError("Cannot warp " + character.getName() + CommonColors.ERROR + " to Warpstone"
					+ warpstone.getIdentifier() + ", they are offline.");
			return false;
		}

		Player player = character.getPlayer().getPlayer();

		// Get Warpstone location
		Location destination = warpstone.getExitLocation();
		if (destination == null) {
			Utils.sendActionBar(player, CommonColors.ERROR + "Warpstone not found.");
			return false;
		}
		destination.setDirection(player.getLocation().getDirection());

		// Fire event
		PlayerWarpEvent event = new PlayerWarpEvent(player, warpstone, cause);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return false;
		}

		// Complete teleport
		String wsDisplayName = warpstone.getDisplayName();
		if (wsDisplayName != null) {
			Utils.sendActionBar(player, ChatColor.BLUE + "Warping to " + wsDisplayName + ChatColor.BLUE + "...");
		} else {
			Utils.sendActionBar(player, ChatColor.BLUE + "Warping...");
		}
		return teleport(destination, TeleportCause.PLUGIN, delay);
	}

}