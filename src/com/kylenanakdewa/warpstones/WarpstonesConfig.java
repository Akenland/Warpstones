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

        COLOR = ChatColor.valueOf(config.getString("general.color"));
        WARPSTONES_NAME = config.getString("general.warpstones-name");

        MAX_TP_DISTANCE = config.getInt("teleportation.max-tp-distance");
        TP_FULL_DELAY = config.getInt("teleportation.full-delay");
        TP_REDUCED_DELAY = config.getInt("teleportation.reduced-delay");
        TP_REDUCED_DELAY_HEALTH = config.getInt("teleportation.reduced-delay-health");
        TP_PARTICLE_DENSITY = config.getInt("teleportation.particle-density");
        TP_SOUND_VOLUME = (float) config.getDouble("teleportation.sound-volume");

        SPAWN_WARPSTONE = config.getString("warpstones.spawn-warpstone");
        FIRST_JOIN_WARPSTONE = config.getString("warpstones.first-join-warpstone");
        BIOME_WARPSTONES = config.getBoolean("warpstones.generate-biome-warpstones");

        WARP_ITEMS_ENABLED = config.getBoolean("items.enabled");
        WARP_ITEMS_CRAFTABLE = config.getBoolean("items.craftable");
        COMPASSES_SHOW_DISTANCES = config.getBoolean("items.compasses-show-distances");
    }

    // General

    /** The accent color to use for warpstones messages. Default BLUE. */
    public final ChatColor COLOR;

    /** The name for a warpstone. */
    public final String WARPSTONES_NAME;

    // Teleportation

    /**
     * The maximum distance players can teleport using /tp and /tphere. Players with
     * permission warpstones.tp.nolimits can bypass this limit. Default 750 blocks.
     */
    public final double MAX_TP_DISTANCE;

    /**
     * The delay before players teleport. Players with permission
     * warpstones.tp.instant are not delayed. Default 5 seconds.
     */
    public final int TP_FULL_DELAY;

    /**
     * A reduced delay, for players who are above the health value specified below.
     * This can allow players not in combat to teleport quicker. Default 2 seconds.
     */
    public final int TP_REDUCED_DELAY;

    /**
     * The minimum amount of health needed to get a reduced delay. Default 20
     * health.
     */
    public final int TP_REDUCED_DELAY_HEALTH;

    /**
     * The density of the particle effect used when teleporting. Reduce to improve
     * client performance. Players with permission warpstones.tp.silent do not get
     * effects. Default 200.
     */
    public final int TP_PARTICLE_DENSITY;

    /**
     * Volume of sound effect used when teleporting. Increasing will allow it to be
     * heard from further away. Default 0.5.
     */
    public final float TP_SOUND_VOLUME;

    // Warpstones

    /**
     * The name of the spawn warpstone on your server. Used when players type
     * /spawn. Default "spawn".
     */
    public final String SPAWN_WARPSTONE;

    /**
     * The name of the first-join warpstone on your server. New players will spawn
     * here. Default "spawn".
     */
    public final String FIRST_JOIN_WARPSTONE;

    /**
     * Whether generated warpstones should use designs matching the biome
     * temperature. Default true.
     */
    public final boolean BIOME_WARPSTONES;

    // Items

    /** Whether Warp items are enabled. Default true. */
    public final boolean WARP_ITEMS_ENABLED;

    /** Whether Warp items can be crafted by players. Default true. */
    public final boolean WARP_ITEMS_CRAFTABLE;

    /** Whether compasses show distances to warpstones when held. Default true. */
    public final boolean COMPASSES_SHOW_DISTANCES;
}