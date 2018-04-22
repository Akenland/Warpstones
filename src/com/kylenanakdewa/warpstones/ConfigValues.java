package com.kylenanakdewa.warpstones;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import com.kylenanakdewa.core.common.ConfigAccessor;

public final class ConfigValues {

    //// Config file
    private static final ConfigAccessor config = new ConfigAccessor("warpstonesconfig.yml", WarpstonesPlugin.plugin);
    private static final FileConfiguration configFile = config.getConfig();

    //// Saving default config file
    static final void saveDefaultConfig(){
        config.saveDefaultConfig();
    }


    //// General
    // Colour used for Warpstones messages (default BLUE)
    public static ChatColor color = ChatColor.valueOf(configFile.getString("general.color"));


    //// Teleportation
    // Max distance for TP/TPHere (default 750)
    public static double tpDistance = Math.pow(configFile.getInt("teleportation.max-tp-distance"),2);

    // The delay when below the set amount of health (default 5)
    public static int tpFullDelay = configFile.getInt("teleportation.full-delay");

    // The delay when above the set amount of health (default 2)
    public static int tpReducedDelay = configFile.getInt("teleportation.reduced-delay");

    // The amount of health needed for a reduced delay (default 20)
    public static int tpReducedDelayHealth = configFile.getInt("teleportation.reduced-delay-health");

    // Density of particles displayed when teleporting (default 200)
    public static int tpParticleCount = configFile.getInt("teleportation.particle-density");

    // Volume of sound used during teleports (default 0.5f)
    public static float tpSoundVolume = (float) configFile.getDouble("teleportation.sound-volume");


    //// Warpstones
    // Spawn warpstone - used for /spawn and default home
    public static String warpstoneSpawn = configFile.getString("warpstones.spawn-warpstone");

    // Whether to generate biome/temperature-specific warpstones
    public static boolean generateBiomeWarpstones = configFile.getBoolean("warpstones.generate-biome-warpstones");


    //// Items
    // The chance of players receiving a warp dust when activating a warpstone
    public static int warpDustChance = configFile.getInt("items.warp-dust-chance");

    // Whether Warp Shards can be crafted
    public static boolean warpShardsCraftable = configFile.getBoolean("items.warp-shards-craftable");

    // Whether Warp Shards can be used at warpstones
    public static boolean warpShardsUsable = configFile.getBoolean("items.warp-shards-usable");
}