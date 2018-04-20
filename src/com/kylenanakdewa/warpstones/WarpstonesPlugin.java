package com.kylenanakdewa.warpstones;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

import com.kylenanakdewa.core.common.ConfigAccessor;
import com.kylenanakdewa.warpstones.items.ItemListener;
import com.kylenanakdewa.warpstones.items.WarpItems;
import com.kylenanakdewa.warpstones.teleports.TeleportCommands;

/**
 * Warpstones for Bukkit
 * <p>
 * A uniquely immersive warping system, based around floating structures known as Warpstones.
 * @author Kyle Nanakdewa
 */
public final class WarpstonesPlugin extends JavaPlugin {

	//@Deprecated
	public static WarpstonesPlugin plugin;

	/** The Warpstones available on this server. */
	private static final Map<String,Warpstone> warpstones = new HashMap<String,Warpstone>();

	/** ConfigAccessor for warpstones.yml */
	private static ConfigAccessor warpstonesConfigAccessor;


	@Override
	public void onEnable(){
		plugin = this;
		warpstonesConfigAccessor = new ConfigAccessor("warpstones.yml", this);

		reload();

		registerCommands();

		if(ConfigValues.warpShardsCraftable) getServer().addRecipe(WarpItems.getWarpShardRecipe());
	}

	@Override
	public void onDisable(){
		saveWarpstones();
		HandlerList.unregisterAll(this);
	}

	/**
	 * Reloads the Warpstones plugin.
	 */
	public void reload(){
		// Load default config
		ConfigValues.saveDefaultConfig();

		// Register event listeners
		getServer().getPluginManager().registerEvents(new EventListener(), this);

		// Register recipes (and event listener)
		if(ConfigValues.warpShardsCraftable) getServer().getPluginManager().registerEvents(new ItemListener(), this);

		loadWarpstones();

		setupDynmapMarkers();
	}

	/**
	 * Registers Warpstones plugin commands.
	 */
	private void registerCommands(){
		getCommand("warpstones").setExecutor(new WarpstoneCommands(this));
		getCommand("tp").setExecutor(new TeleportCommands());
		getCommand("tphere").setExecutor(new TeleportCommands());
	}

	/**
	 * Sets up Dynmap markers.
	 */
	private void setupDynmapMarkers(){
		if(getServer().getPluginManager().isPluginEnabled("dynmap")){
			new DynmapWarpstones(this);
			getLogger().info("Adding Warpstone markers to Dynmap!");
		}
	}


	/**
	 * Gets the ConfigurationSection for the warpstones file.
	 */
	static ConfigurationSection getWarpstonesFile(){
		ConfigurationSection config = warpstonesConfigAccessor.getConfig().getConfigurationSection("warpstones");
		if(config==null) config = warpstonesConfigAccessor.getConfig().createSection("warpstones");
		return config;
	}


	/**
	 * Gets a Map of Warpstones on the server, sorted by their identifier.
	 */
	static Map<String,Warpstone> getWarpstones(){
		return warpstones;
	}

	/**
	 * Loads Warpstones from file.
	 */
	private void loadWarpstones(){
		ConfigurationSection warpstoneFile = getWarpstonesFile();
		warpstoneFile.getKeys(false).forEach(warpstoneName ->
			warpstones.put(warpstoneName, Warpstone.loadFromConfig(warpstoneName, warpstoneFile.getConfigurationSection(warpstoneName)))
		);
	}
	/**
	 * Saves changes to the Warpstones list.
	 */
	public void saveWarpstones(){
		warpstones.forEach((warpstoneName,warpstone) ->
			getWarpstonesFile().createSection(warpstoneName, warpstone.saveToConfig().getValues(true))
		);
		warpstonesConfigAccessor.saveConfig();
	}

}
