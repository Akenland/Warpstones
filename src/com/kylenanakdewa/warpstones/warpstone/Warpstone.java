package com.kylenanakdewa.warpstones.warpstone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.Random;

import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.core.common.savedata.SaveDataSection;

/**
 * Represents a location that can be warped to, and is often marked by a lapis
 * and stone structure.
 *
 * @author Kyle Nanakdewa
 */
public class Warpstone {

	/**
	 * The unique name that identifies this Warpstone.
	 */
	private final String identifier;

	/**
	 * The location of this Warpstone. This should be the centerpoint of the stone,
	 * just above the ground.
	 */
	private Location location;

	/**
	 * The display/friendly name of this Warpstone.
	 */
	private String displayName;

	/**
	 * Message to display to players who activate this Warpstone while it is
	 * disabled. Optional.
	 */
	private String disabledMsg;
	/**
	 * Whether this Warpstone is disabled. When true, players cannot activate the
	 * Warpstone.
	 */
	private boolean disabled;
	/**
	 * Whether permission is required to activate this Warpstone. If true, players
	 * must have "warpstones.activate.warpstone_name".
	 */
	private boolean requirePermission;
	/**
	 * A condition that must be met to activate this Warpstone. Optional.
	 */
	private String condition;

	/**
	 * Whether saving at this Warpstone is allowed.
	 */
	private boolean savingBlocked;

	/**
	 * A Warpstone that players will be taken to upon activating this Warpstone.
	 */
	private Location forcedDestination;

	/**
	 * A data section for other plugins to store extra data for this Warpstone.
	 */
	private ConfigurationSection data;

	/**
	 * Creates a new Warpstone instance.
	 * <p>
	 * This generally should not be used - use WarpstoneManager to create or load
	 * Warpstones for the server.
	 *
	 * @param identifier the unique name of the Warpstone
	 */
	Warpstone(String identifier) {
		this.identifier = identifier.toLowerCase();
	}

	/**
	 * Gets the unique name of this Warpstone.
	 *
	 * @return the unique identifier of this Warpstone
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Gets the location of this Warpstone.
	 * <p>
	 * This should be the centerpoint of the stone, just above the ground.
	 *
	 * @return the location of this Warpstone
	 */
	public Location getLocation() {
		return location.clone();
	}

	/**
	 * Sets the location of this Warpstone.
	 * <p>
	 * This should be the centerpoint of the stone, just above the ground.
	 *
	 * @param location the new location of this Warpstone
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * Gets the display (friendly) name of this Warpstone.
	 *
	 * @return the display name of this Warpstone, or null if one was not set
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Sets the display (friendly) name of this Warpstone.
	 *
	 * @param name the new display name for this Warpstone, or null to clear
	 */
	public void setDisplayName(String name) {
		displayName = name;
	}

	/**
	 * Gets the message displayed to players who attempt to activate this Warpstone
	 * while it is disabled, they don't have permission, or they don't meet the
	 * condition.
	 *
	 * @return the disabled message for this Warpstone, or null if one was not set
	 */
	public String getDisabledMsg() {
		return disabledMsg;
	}

	/**
	 * Sets the message displayed to players who attempt to activate this Warpstone
	 * while it is disabled, they don't have permission, or they don't meet the
	 * condition.
	 *
	 * @param message the new disabled message, or null to clear
	 */
	public void setDisabledMsg(String message) {
		disabledMsg = message;
	}

	/**
	 * Gets whether this Warpstone is disabled. Disabled Warpstones cannot be
	 * activated by players, but otherwise function normally.
	 * <p>
	 * When a player attempts to activate a disabled Warpstone, they see
	 * {@link #getDisabledMsg()}, if set.
	 *
	 * @return true if this Warpstone is disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * Sets whether this Warpstone is disabled. Disabled Warpstones cannot be
	 * activated by players, but otherwise function normally.
	 * <p>
	 * When a player attempts to activate a disabled Warpstone, they see
	 * {@link #getDisabledMsg()}, if set.
	 *
	 * @param disable true to disable the Warpstone, false to enable the Warpstone
	 */
	public void setDisabled(boolean disable) {
		disabled = disable;
	}

	/**
	 * Gets whether a permission is required to activate this Warpstone. If enabled,
	 * players must have "warpstones.activate.warpstone_name" to activate the
	 * Warpstone.
	 * <p>
	 * If a player attempts to activate a Warpstone without permission, they see
	 * {@link #getDisabledMsg()}, if set.
	 *
	 * @return true if permission is required to activate this Warpstone
	 */
	public boolean requiresPermission() {
		return requirePermission;
	}

	/**
	 * Sets whether a permission is required to activate this Warpstone. If enabled,
	 * players must have "warpstones.activate.warpstone_name" to activate the
	 * Warpstone.
	 * <p>
	 * If a player attempts to activate a Warpstone without permission, they see
	 * {@link #getDisabledMsg()}, if set.
	 *
	 * @param require true to require permission
	 */
	public void setRequirePerm(boolean require) {
		requirePermission = require;
	}

	/**
	 * Gets the condition required to activate this Warpstone. If set, players must
	 * meet the Condition to activate the Warpstone.
	 * <p>
	 * If a player attempts to activate a Warpstone without meeting the condition,
	 * they see {@link #getDisabledMsg()}, if set.
	 *
	 * @return the condition, or null if one was not set
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * Sets the condition required to activate this Warpstone. If set, players must
	 * meet the Condition to activate the Warpstone.
	 * <p>
	 * If a player attempts to activate a Warpstone without meeting the condition,
	 * they see {@link #getDisabledMsg()}, if set.
	 *
	 * @param condition the new condition, or null to clear
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}

	/**
	 * Gets whether saving at this Warpstone is allowed.
	 * <p>
	 * If true, when players activate this Warpstone, their location will not be
	 * saved, preventing them from returning here.
	 * <p>
	 * This also prevents Warp Shards from being linked to this Warpstone.
	 *
	 * @return true if this Warpstone does not allow saving location
	 */
	public boolean isSaveBlocked() {
		return savingBlocked;
	}

	/**
	 * Sets whether saving at this Warpstone is allowed.
	 * <p>
	 * If true, when players activate this Warpstone, their location will not be
	 * saved, preventing them from returning here.
	 * <p>
	 * This also prevents Warp Shards from being linked to this Warpstone.
	 *
	 * @param block true if this Warpstone should not allow saving location
	 */
	public void setSaveBlocked(boolean block) {
		savingBlocked = block;
	}

	/**
	 * Gets the custom destination that players will be taken to when they activate
	 * this Warpstone. If set, players will be warped to this location, instead of
	 * being shown the Warp menu.
	 *
	 * @return the forced destination, or null if not set
	 */
	public Location getForcedDestination() {
		return forcedDestination;
	}

	/**
	 * Sets the custom destination that players will be taken to when they activate
	 * this Warpstone. If set, players will be warped to this location, instead of
	 * being shown the Warp menu.
	 *
	 * @param destination the new forced destination, or null to clear
	 */
	public void setForcedDestination(Location destination) {
		forcedDestination = destination;
	}

	/**
	 * Gets a plugin's data section for this Warpstone. Plugins can use this to save
	 * extra data for this Warpstone.
	 *
	 * @param plugin the plugin to get data for
	 * @return the data section
	 */
	public SaveDataSection getData(Plugin plugin) {
		ConfigurationSection pluginData = data.getConfigurationSection(plugin.getName());
		if (pluginData == null)
			pluginData = data.createSection(plugin.getName());
		return new SaveDataSection(pluginData, plugin);
	}

	/**
	 * Gets all plugin data for this Warpstone. Plugins can store extra data for
	 * this Warpstone here.
	 * <p>
	 * Internal use only. Intended for the Warpstone Manager to save this data to
	 * file.
	 */
	ConfigurationSection getPluginData() {
		return data;
	}

	/**
	 * Sets all plugin data for this Warpstone. Plugins can store extra data for
	 * this Warpstone here.
	 * <p>
	 * Internal use only. Intended for the Warpstone Manager to load this data from
	 * file.
	 */
	void setPluginData(ConfigurationSection data) {
		this.data = data;
	}

	/**
	 * Gets a valid exit location for this Warpstone. This is the exact location
	 * that players should be placed, when warping to this Warpstone.
	 * <p>
	 * This will attempt to return a random location near the Warpstone. The
	 * location will be an empty space, above a block.
	 * <p>
	 * Up to 3 attempts will be made to find a valid exit location. If a valid
	 * location is not found, this will return null.
	 * <p>
	 * If this Warpstone does not have a location, or is in an unloaded world, this
	 * will return null.
	 */
	public Location getExitLocation() {
		// Make sure Warpstone has a valid location
		if (getLocation() == null) {
			Utils.notifyAdminsError("Warpstone " + getIdentifier()
					+ " does not have a valid location, cannot determine exit location.");
			return null;
		}

		// Make sure world is loaded
		if (!getLocation().isWorldLoaded()) {
			Utils.notifyAdminsError(
					"Warpstone " + getIdentifier() + " is in an unloaded world, cannot determine exit location.");
			return null;
		}

		// Max 3 attempts to find valid exit location
		int attempts = 0;
		Location exitLocation = null;

		while (exitLocation == null && attempts < 3) {
			attempts++;

			World world = getLocation().getWorld();
			int xbase = getLocation().getBlockX();
			int yBase = getLocation().getBlockY();
			int zBase = getLocation().getBlockZ();

			Random random = new Random();

			// Random XZ offset
			int xModifier = random.nextInt(4);
			int zModifier = random.nextInt(4);

			// Shift away from center of stone structure
			xModifier += 3;
			zModifier += 3;

			// Choose + or - at random
			xModifier = random.nextBoolean() ? xModifier : -xModifier;
			zModifier = random.nextBoolean() ? zModifier : -zModifier;

			// Get location
			Location testLocation = new Location(world, xbase + xModifier, yBase, zBase + zModifier);

			// Check for block below, and air
			Block feetBlock = world.getBlockAt(testLocation);
			Block headBlock = feetBlock.getRelative(0, 1, 0);
			Block floorBlock = feetBlock.getRelative(0, -1, 0);
			if (headBlock.isEmpty() && feetBlock.isPassable() && floorBlock.getType().isSolid()) {
				exitLocation = testLocation;
			}
		}

		if (exitLocation == null) {
			Utils.notifyAdminsError("Could not find a valid exit location for Warpstone " + getIdentifier()
					+ " - check that there is clear space around the Warpstone.");
		}

		return exitLocation;
	}

	/**
	 * Generates this Warpstone's structure, using the specified biome design and
	 * size.
	 * <p>
	 * Requires WorldEdit.
	 *
	 * @param design the biome design to use
	 * @param size   the size of the Warpstone, 1/2/3
	 * @throws IllegalArgumentException if size is not 1/2/3
	 */
	public void generateWarpstoneStructure(WarpstoneDesign design, int size) {
		// Let WE generate the actual stone
		if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
			WarpstoneWEGeneration.generateWarpstone(this, design, size);
		} else {
			Utils.notifyAdminsError("Unable to generate Warpstone structure, WorldEdit not installed.");
		}
	}

	/**
	 * Generates this Warpstone's structure, using the specified size. The biome
	 * design will be determined based on the biome this Warpstone is located in.
	 * <p>
	 * Requires WorldEdit.
	 *
	 * @param design the biome design to use
	 * @param size   the size of the Warpstone, 1/2/3
	 * @throws IllegalArgumentException if size is not 1/2/3
	 */
	public void generateWarpstoneStructure(int size) {
		// Let WE generate the actual stone
		if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
			WarpstoneWEGeneration.generateWarpstone(this, size);
		} else {
			Utils.notifyAdminsError("Unable to generate Warpstone structure, WorldEdit not installed.");
		}
	}

}