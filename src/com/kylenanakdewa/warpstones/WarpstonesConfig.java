package com.kylenanakdewa.warpstones;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Config values for the Warpstones plugin.
 *
 * @author Kyle Nanakdewa
 */
public final class WarpstonesConfig {

    /** The plugin config. */
    private FileConfiguration config;

    WarpstonesConfig(WarpstonesPlugin plugin) {
        config = plugin.getConfig();
    }

    // General

    /** The accent color to use for warpstones messages. Default BLUE. */
    public final ChatColor COLOR = ChatColor.valueOf(config.getString("general.color"));

    /** The name for a warpstone. */
    public final String WARPSTONES_NAME = config.getString("general.warpstones-name");

    // Teleportation

    /**
     * The maximum distance players can teleport using /tp and /tphere. Players with
     * permission warpstones.tp.nolimits can bypass this limit. Default 750 blocks.
     */
    public final double MAX_TP_DISTANCE = config.getInt("teleportation.max-tp-distance");

    /**
     * The delay before players teleport. Players with permission
     * warpstones.tp.instant are not delayed. Default 5 seconds.
     */
    public final int TP_FULL_DELAY = config.getInt("teleportation.full-delay");

    /**
     * A reduced delay, for players who are above the health value specified below.
     * This can allow players not in combat to teleport quicker. Default 2 seconds.
     */
    public final int TP_REDUCED_DELAY = config.getInt("teleportation.reduced-delay");

    /**
     * The minimum amount of health needed to get a reduced delay. Default 20
     * health.
     */
    public final int TP_REDUCED_DELAY_HEALTH = config.getInt("teleportation.reduced-delay-health");

    /**
     * The density of the particle effect used when teleporting. Reduce to improve
     * client performance. Players with permission warpstones.tp.silent do not get
     * effects. Default 200.
     */
    public final int TP_PARTICLE_DENSITY = config.getInt("teleportation.particle-density");

    /**
     * Volume of sound effect used when teleporting. Increasing will allow it to be
     * heard from further away. Default 0.5.
     */
    public final float TP_SOUND_VOLUME = (float) config.getDouble("teleportation.sound-volume");

    // Warpstones

    /**
     * The name of the spawn warpstone on your server. Used when players type
     * /spawn. Default "spawn".
     */
    public final String SPAWN_WARPSTONE = config.getString("warpstones.spawn-warpstone");

    /**
     * The name of the first-join warpstone on your server. New players will spawn
     * here. Default "spawn".
     */
    public final String FIRST_JOIN_WARPSTONE = config.getString("warpstones.first-join-warpstone");

    /**
     * Whether generated warpstones should use designs matching the biome
     * temperature. Default true.
     */
    public final boolean BIOME_WARPSTONES = config.getBoolean("warpstones.generate-biome-warpstones");

    // Items

    /** Whether Warp items are enabled. Default true. */
    public final boolean WARP_ITEMS_ENABLED = config.getBoolean("items.enabled");

    /** Whether Warp items can be crafted by players. Default true. */
    public final boolean WARP_ITEMS_CRAFTABLE = config.getBoolean("items.craftable");

    /** Whether compasses show distances to warpstones when held. Default true. */
    public final boolean COMPASSES_SHOW_DISTANCES = config.getBoolean("items.compasses-show-distances");
}