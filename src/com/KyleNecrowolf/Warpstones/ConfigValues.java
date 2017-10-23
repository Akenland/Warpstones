package com.KyleNecrowolf.Warpstones;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import com.KyleNecrowolf.RealmsCore.Common.ConfigAccessor;

public final class ConfigValues {

    //// Config file
    private static final ConfigAccessor config = new ConfigAccessor("warpstonesconfig.yml", Main.plugin);
    private static final FileConfiguration configFile = config.getConfig();

    //// Saving default config file
    static final void saveDefaultConfig(){
        config.saveDefaultConfig();
    }


    //// General
    // Colour used for Warpstones messages (default BLUE)
    public static final ChatColor color = ChatColor.valueOf(configFile.getString("general.color"));


    //// Teleportation
    // Max distance for TP/TPHere (default 750)
    public static final double tpDistance = Math.pow(configFile.getInt("teleportation.max-tp-distance"),2);

    // The delay when below the set amount of health (default 5)
    public static final int tpFullDelay = configFile.getInt("teleportation.full-delay");

    // The delay when above the set amount of health (default 2)
    public static final int tpReducedDelay = configFile.getInt("teleportation.reduced-delay");

    // The amount of health needed for a reduced delay (default 20)
    public static final int tpReducedDelayHealth = configFile.getInt("teleportation.reduced-delay-health");

    // Density of particles displayed when teleporting (default 200)
    public static final int tpParticleCount = configFile.getInt("teleportation.particle-density");

    // Volume of sound used during teleports (default 0.5f)
    public static final float tpSoundVolume = (float) configFile.getDouble("teleportation.sound-volume");


    //// Warpstones
    // Spawn warpstone - used for /spawn and default home
    public static final Warpstone warpstoneSpawn = new Warpstone(configFile.getString("warpstones.spawn-warpstone"));

    // Whether to generate biome/temperature-specific warpstones
    public static final boolean generateBiomeWarpstones = configFile.getBoolean("warpstones.generate-biome-warpstones");


    //// Items
    // The chance of players receiving a warp dust when activating a warpstone
    public static final int warpDustChance = configFile.getInt("items.warp-dust-chance");

    // Whether Warp Shards can be crafted
    public static final boolean warpShardsCraftable = configFile.getBoolean("items.warp-shards-craftable");

    // Whether Warp Shards can be used at warpstones
    public static final boolean warpShardsUsable = configFile.getBoolean("items.warp-shards-usable");
}