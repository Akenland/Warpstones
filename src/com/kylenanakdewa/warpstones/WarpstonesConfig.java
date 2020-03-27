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

    public WarpstonesConfig(WarpstonesPlugin plugin) {
        config = plugin.getConfig();
    }

    // General

    /** The accent color to use for warpstones messages. Default BLUE. */
    public final ChatColor color = ChatColor.valueOf(config.getString("general.color"));

    /** The name for a warpstone. */
    public final String warpstoneName = config.getString("general.warpstone-name");

    // Teleportation

    /**
     * The maximum distance players can teleport using /tp and /tphere. Players with
     * permission warpstones.tp.nolimits can bypass this limit. Default 750 blocks.
     */
    public final double tpDistance = Math.pow(config.getInt("teleportation.max-tp-distance"), 2);

    /**
     * The delay before players teleport. Players with permission
     * warpstones.tp.instant are not delayed. Default 5 seconds.
     */
    public final int tpFullDelay = config.getInt("teleportation.full-delay");

    /**
     * A reduced delay, for players who are above the health value specified below.
     * This can allow players not in combat to teleport quicker. Default 2 seconds.
     */
    public final int tpReducedDelay = config.getInt("teleportation.reduced-delay");

    /**
     * The minimum amount of health needed to get a reduced delay. Default 20
     * health.
     */
    public final int tpReducedDelayHealth = config.getInt("teleportation.reduced-delay-health");

    /**
     * The density of the particle effect used when teleporting. Reduce to improve
     * client performance. Players with permission warpstones.tp.silent do not get
     * effects. Default 200.
     */
    public final int tpParticleCount = config.getInt("teleportation.particle-density");

    /**
     * Volume of sound effect used when teleporting. Increasing will allow it to be
     * heard from further away. Default 0.5.
     */
    public final float tpSoundVolume = (float) config.getDouble("teleportation.sound-volume");

    // Warpstones

    /**
     * The name of the spawn warpstone on your server. Used when players type
     * /spawn. Default "spawn".
     */
    public final String warpstoneSpawn = config.getString("warpstones.spawn-warpstone");

    /**
     * Whether generated warpstones should use designs matching the biome
     * temperature. Default true.
     */
    public final boolean generateBiomeWarpstones = config.getBoolean("warpstones.generate-biome-warpstones");

    // Items

    /**
     * Chance of players receiving a warp dust when activating a warpstone. Default
     * 70%.
     */
    public final int warpDustChance = config.getInt("items.warp-dust-chance");

    /**
     * Whether Warp Shards can be crafted by players (from 4 warp dust). Default
     * true.
     */
    public final boolean warpShardsCraftable = config.getBoolean("items.warp-shards-craftable");

    /** Whether Warp Shards can be linked and used at warpstones. Default true. */
    public final boolean warpShardsUsable = config.getBoolean("items.warp-shards-usable");

    /** Whether compasses show distances to warpstones when held. Default true. */
    public final boolean compassesShowDistances = config.getBoolean("items.compasses-show-distances");
}