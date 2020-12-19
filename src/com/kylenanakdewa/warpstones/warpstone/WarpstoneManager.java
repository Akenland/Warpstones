package com.kylenanakdewa.warpstones.warpstone;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.kylenanakdewa.core.common.ConfigAccessor;
import com.kylenanakdewa.warpstones.WarpstonesPlugin;
import com.kylenanakdewa.warpstones.warpstone.gui.WarpGuiListener;
import com.kylenanakdewa.warpstones.warpstone.listeners.WarpstoneActivationListener;
import com.kylenanakdewa.warpstones.warpstone.listeners.WarpstoneApproachListener;
import com.kylenanakdewa.warpstones.warpstone.listeners.WarpstoneDelayListener;
import com.kylenanakdewa.warpstones.warpstone.listeners.WarpstonePromptActionListener;
import com.kylenanakdewa.warpstones.warpstone.listeners.WarpstoneProtectionListener;
import com.kylenanakdewa.warpstones.warpstone.listeners.WarpstoneSaveListener;
import com.kylenanakdewa.warpstones.warpstone.listeners.WarpstonesWorldSaveListener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Manages all Warpstones.
 *
 * @author Kyle Nanakdewa
 */
public class WarpstoneManager {

    /** The current Warpstone Manager instance. */
    private static WarpstoneManager managerInstance;

    /**
     * Gets the server's Warpstone Manager.
     * <p>
     * The Warpstone Manager is responsible for loading and saving Warpstones.
     * <p>
     * You can retrieve Warpstones from the Warpstone Manager.
     *
     * @return the Warpstone manager onstance
     */
    public static WarpstoneManager get() {
        return managerInstance;
    }

    /** The Warpstones plugin instance. */
    private final WarpstonesPlugin plugin;

    /** The Warpstones available on this server. */
    private final Map<String, Warpstone> warpstones;

    /** The data file. This file holds all saved data for every Warpstone on the server. */
    private ConfigAccessor dataFile;

    public WarpstoneManager(WarpstonesPlugin plugin) {
        managerInstance = this;

        this.plugin = plugin;

        warpstones = new HashMap<String, Warpstone>();
    }

    public void onEnable() {
        dataFile = new ConfigAccessor("wsdata.yml", plugin);
        loadAllWarpstones();
        registerListeners();
    }

    public void onDisable() {
        //saveAllWarpstones(); - this is called on enable, before anything can be loaded
    }

    /**
     * Registers event listeners for Warpstones.
     */
    private void registerListeners() {
        plugin.getServer().getPluginManager().registerEvents(new WarpstoneActivationListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new WarpstoneApproachListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new WarpstoneDelayListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new WarpstonePromptActionListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new WarpstoneProtectionListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new WarpstoneSaveListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new WarpstonesWorldSaveListener(), plugin);

        plugin.getServer().getPluginManager().registerEvents(new WarpGuiListener(), plugin);
    }

    /**
     * Gets the data file for Warpstones. This file holds all saved data for every
     * Warpstone on the server.
     */
    private FileConfiguration getDataFile() {
        return dataFile.getConfig();
    }

    /**
     * Loads a Warpstone from the data file.
     *
     * @param identifier the unique name of the Warpstone in the data file, cannot
     *                   be null
     */
    private Warpstone getWarpstoneFromDataFile(String identifier) {
        ConfigurationSection data = getDataFile().getConfigurationSection("warpstones." + identifier);

        Warpstone warpstone = new Warpstone(identifier);

        // Location
        if (data.contains("location")) {
            // World
            String worldName = data.getString("location.world");
            World world = plugin.getServer().getWorld(worldName);
            if (world == null) {
                plugin.getLogger().warning("Could not find world " + worldName + "for Warpstone " + identifier);
            } else {
                // Centerpoint of Warpstone
                int x = data.getInt("location.x");
                int y = data.getInt("location.y");
                int z = data.getInt("location.z");

                Location location = new Location(world, x, y, z);
                warpstone.setLocation(location);
            }
        } else {
            plugin.getLogger().warning("Warpstone " + warpstone.getIdentifier() + " has no location.");
        }

        // Display name
        warpstone.setDisplayName(data.getString("display-name"));

        // Disabled data
        warpstone.setDisabledMsg(data.getString("disabled-msg"));
        warpstone.setDisabled(data.getBoolean("disabled"));
        warpstone.setRequirePerm(data.getBoolean("require-permission"));
        warpstone.setCondition(data.getString("condition"));

        // Saving blocked
        warpstone.setSaveBlocked(data.getBoolean("saving-blocked"));

        // Forced destination
        if (data.contains("forced-destination")) {
            // World
            String worldName = data.getString("forced-destination.world");
            World world = plugin.getServer().getWorld(worldName);
            if (world == null) {
                plugin.getLogger().warning("Could not find world " + worldName + "for Warpstone " + identifier);
            } else {
                int x = data.getInt("forced-destination.x");
                int y = data.getInt("forced-destination.y");
                int z = data.getInt("forced-destination.z");

                Location location = new Location(world, x, y, z);
                warpstone.setForcedDestination(location);
            }
        }

        // Other plugin data
        if (data.contains("plugin-data")) {
            warpstone.setPluginData(data.getConfigurationSection("plugin-data"));
        }

        return warpstone;
    }

    /**
     * Saves a Warpstone to the data file.
     *
     * @param warpstone the Warpstone to save to file
     */
    private void saveWarpstoneToDataFile(Warpstone warpstone) {
        ConfigurationSection data = getDataFile().createSection("warpstones." + warpstone.getIdentifier());

        // Location
        Location location = warpstone.getLocation();
        if (location != null) {
            // World
            data.set("location.world", location.getWorld().getName());

            // Centerpoint of Warpstone
            data.set("location.x", location.getBlockX());
            data.set("location.y", location.getBlockY());
            data.set("location.z", location.getBlockZ());
        } else {
            plugin.getLogger().warning("Warpstone " + warpstone.getIdentifier() + " has no location.");
        }

        // Display name
        data.set("display-name", warpstone.getDisplayName());

        // Disabled data
        data.set("disabled-msg", warpstone.getDisabledMsg());
        data.set("disabled", warpstone.isDisabled());
        data.set("require-permission", warpstone.requiresPermission());
        data.set("condition", warpstone.getCondition());

        // Saving blocked
        data.set("saving-blocked", warpstone.isSaveBlocked());

        // Forced destination
        Location forcedDestination = warpstone.getLocation();
        if (forcedDestination != null) {
            // World
            data.set("forced-destination.world", forcedDestination.getWorld().getName());

            // Centerpoint of Warpstone
            data.set("forced-destination.x", forcedDestination.getBlockX());
            data.set("forced-destination.y", forcedDestination.getBlockY());
            data.set("forced-destination.z", forcedDestination.getBlockZ());
        }

        // Other plugin data
        ConfigurationSection pluginData = warpstone.getPluginData();
        if (pluginData != null) {
            pluginData.getValues(true).forEach((key, value) -> data.set("plugin-data." + key, value));
        }

    }

    /**
     * Loads all Warpstones from the data file, and makes them available on the
     * server.
     */
    public void loadAllWarpstones() {
        // Clear out existing data on the server
        warpstones.clear();

        ConfigurationSection data = getDataFile().getConfigurationSection("warpstones");

        if (data != null) {
            // Iterate through all keys
            for (String identifier : data.getKeys(false)) {
                identifier = identifier.toLowerCase();

                // Load Warpstone from file
                Warpstone warpstone = getWarpstoneFromDataFile(identifier);

                // Store Warpstone in this manager
                warpstones.put(identifier, warpstone);
            }
        } else {
            plugin.getLogger().info("No Warpstone data found.");
        }

        plugin.getLogger().info("Loaded " + warpstones.size() + " Warpstones.");
    }

    /**
     * Saves all Warpstones on this server to the data file.
     */
    public void saveAllWarpstones() {
        getDataFile().createSection("warpstones");

        for (Warpstone warpstone : warpstones.values()) {
            saveWarpstoneToDataFile(warpstone);
        }

        dataFile.saveConfig();

        plugin.getLogger().info("Saved " + warpstones.size() + " Warpstones.");
    }

    /**
     * Gets all Warpstones on the server.
     * <p>
     * Removing a Warpstone from this collection will remove it from the server. You
     * cannot add Warpstones to this collection.
     *
     * @return a collection containing every Warpstone on the server
     */
    public Collection<Warpstone> getAllWarpstones() {
        return warpstones.values();
    }

    /**
     * Gets the unique name of every Warpstone on the server.
     * <p>
     * Removing a Warpstone identifier from this collection will remove the
     * Warpstone from the server. You cannot add Warpstones to this collection.
     *
     * @return a set containing the identifier of every Warpstone on the server
     */
    public Set<String> getAllWarpstoneIdentifiers() {
        return warpstones.keySet();
    }

    /**
     * Gets a Warpstone on the server.
     *
     * @param identifier the unique name of the Warpstone to retrieve
     * @return the Warpstone, or null if it does not exist
     */
    public Warpstone getWarpstone(String identifier) {
        identifier = identifier.toLowerCase();

        return warpstones.get(identifier);
    }

    /**
     * Gets the server's spawn Warpstone. This Warpstone is used when players warp
     * to spawn.
     *
     * @return the spawn Warpstone, or null if it does not exist
     */
    public Warpstone getSpawnWarpstone() {
        String spawnIdentifier = plugin.getWarpstonesConfig().SPAWN_WARPSTONE;
        return getWarpstone(spawnIdentifier);
    }

    /**
     * Gets the server's first-join Warpstone. New players will initially spawn at
     * this Warpstone.
     *
     * @return the first join Warpstone, or null if it does not exist
     */
    public Warpstone getFirstJoinWarpstone() {
        String spawnIdentifier = plugin.getWarpstonesConfig().FIRST_JOIN_WARPSTONE;
        return getWarpstone(spawnIdentifier);
    }

    /**
     * Gets the Warpstone nearest the specified location.
     *
     * @param location        the location to search near
     * @param radius          the maximum search radius
     * @param includeDisabled whether to include disabled warpstones
     * @return the nearest Warpstone, or null if one was not found
     */
    public Warpstone getNearestWarpstone(Location location, double radius, boolean includeDisabled) {
        double radiusSquared = Math.pow(radius, 2);

        // The current closest Warpstone found
        Warpstone nearestStone = null;
        // The current shortest distance found
        double nearestDistanceSquared = radiusSquared;

        // Iterate through all Warpstones
        for (Warpstone testWarpstone : getAllWarpstones()) {
            // Check that Warpstone is not disabled
            if (includeDisabled || !testWarpstone.isDisabled()) {
                // Check that Warpstone has a location in the target world
                if (testWarpstone.getLocation() != null
                        && testWarpstone.getLocation().getWorld().equals(location.getWorld())) {

                    double testDistance = testWarpstone.getLocation().distanceSquared(location);

                    // If this Warpstone is closer than any previous tested Warpstone, store it
                    if (testDistance <= nearestDistanceSquared) {
                        nearestStone = testWarpstone;
                        nearestDistanceSquared = testDistance;
                    }
                }
            }
        }

        return nearestStone;
    }

    /**
     * Creates a new Warpstone, and saves it on the server.
     *
     * @param identifier a unique name for the new Warpstone
     * @param location   the location of the new Warpstone's center
     * @return the new Warpstone
     * @throws IllegalArgumentException if a Warpstone with this identifier already
     *                                  exists
     */
    public Warpstone createWarpstone(String identifier, Location location) {
        identifier = identifier.toLowerCase();

        // Check that Warpstone does not already exist
        if (warpstones.containsKey(identifier)) {
            throw new IllegalArgumentException("Warpstone " + identifier + " already exists");
        }

        // Create Warpstone and set location
        Warpstone warpstone = new Warpstone(identifier);
        warpstone.setLocation(location);

        // Store Warpstone on the server
        warpstones.put(identifier, warpstone);

        return warpstone;
    }

    /**
     * Deletes a Warpstone from the server.
     */
    public void deleteWarpstone(Warpstone warpstone) {
        warpstones.remove(warpstone.getIdentifier());
    }

}