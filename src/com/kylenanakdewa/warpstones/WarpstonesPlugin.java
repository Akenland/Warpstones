package com.kylenanakdewa.warpstones;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.kylenanakdewa.warpstones.commands.HomeCommand;
import com.kylenanakdewa.warpstones.commands.SpawnCommand;
import com.kylenanakdewa.warpstones.commands.WarpCommand;
import com.kylenanakdewa.warpstones.commands.WarpstonesCommand;
import com.kylenanakdewa.warpstones.items.WarpItemsManager;
import com.kylenanakdewa.warpstones.teleports.TeleportCommands;
import com.kylenanakdewa.warpstones.teleports.listeners.PlayerTeleportDelayListener;
import com.kylenanakdewa.warpstones.teleports.listeners.PlayerTeleportEffectListener;
import com.kylenanakdewa.warpstones.warpstone.WarpstoneManager;

/**
 * Warpstones for Bukkit
 * <p>
 * A uniquely immersive warping system, based around floating structures known
 * as Warpstones.
 *
 * @author Kyle Nanakdewa
 */
public final class WarpstonesPlugin extends JavaPlugin {

	/** The config. */
	private WarpstonesConfig config;

	/** The Warpstones Manager. */
	private final WarpstoneManager warpstoneManager = new WarpstoneManager(this);

	/** The Warp Items Manager. */
	private final WarpItemsManager warpItemsManager = new WarpItemsManager(this);

	@Override
	public void onEnable() {
		reload();

		registerCommands();
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);

		warpstoneManager.onDisable();
		warpItemsManager.onDisable();
	}

	/**
	 * Reloads the Warpstones plugin.
	 */
	public void reload() {
		onDisable();

		// Load config
		saveDefaultConfig();
		config = new WarpstonesConfig(this);

		// Load Warpstones
		warpstoneManager.onEnable();

		// Set up Warp Items
		warpItemsManager.onEnable();

		// Register event listeners
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerTeleportDelayListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerTeleportEffectListener(), this);
	}

	/**
	 * Registers Warpstones plugin commands.
	 */
	private void registerCommands() {
		getCommand("warpstones").setExecutor(new WarpstonesCommand(this));
		getCommand("warp").setExecutor(new WarpCommand());
		getCommand("home").setExecutor(new HomeCommand());
		getCommand("spawn").setExecutor(new SpawnCommand());
		getCommand("tp").setExecutor(new TeleportCommands());
		getCommand("tphere").setExecutor(new TeleportCommands());
	}

	/**
	 * Gets the Warpstones config.
	 */
	public WarpstonesConfig getWarpstonesConfig() {
		return config;
	}

}