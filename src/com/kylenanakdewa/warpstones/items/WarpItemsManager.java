package com.kylenanakdewa.warpstones.items;

import com.kylenanakdewa.warpstones.WarpstonesPlugin;
import com.kylenanakdewa.warpstones.items.compass.CompassHUD;
import com.kylenanakdewa.warpstones.items.dust.WarpDustListener;
import com.kylenanakdewa.warpstones.items.heart.WarpHeart;
import com.kylenanakdewa.warpstones.items.heart.WarpHeartListener;
import com.kylenanakdewa.warpstones.items.shards.WarpShard;
import com.kylenanakdewa.warpstones.items.shards.WarpShardListener;
import com.kylenanakdewa.warpstones.items.shards.charged.ChargedWarpShard;
import com.kylenanakdewa.warpstones.items.shards.charged.ChargedWarpShardListener;

import org.bukkit.inventory.Recipe;

/**
 * Manages Warp Items.
 *
 * @author Kyle Nanakdewa
 */
public final class WarpItemsManager {

    /** The Warpstones plugin instance. */
    private final WarpstonesPlugin plugin;

    /** Whether recipes have already been registered. */
    private boolean recipesRegistered;

    public WarpItemsManager(WarpstonesPlugin plugin) {
        this.plugin = plugin;

    }

    public void onEnable() {
        if (plugin.getWarpstonesConfig().WARP_ITEMS_ENABLED) {
            registerListeners(plugin);

            if (!recipesRegistered && plugin.getWarpstonesConfig().WARP_ITEMS_CRAFTABLE) {
                registerRecipes(plugin);
            }

            if (plugin.getWarpstonesConfig().COMPASSES_SHOW_DISTANCES) {
                new CompassHUD(plugin);
            }
        }
    }

    public void onDisable() {

    }

    private void registerListeners(WarpstonesPlugin plugin) {
        // Dust
        plugin.getServer().getPluginManager().registerEvents(new WarpDustListener(), plugin);

        // Shards
        plugin.getServer().getPluginManager().registerEvents(new WarpShardListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ChargedWarpShardListener(), plugin);

        // Hearts
        plugin.getServer().getPluginManager().registerEvents(new WarpHeartListener(), plugin);
    }

    private void registerRecipes(WarpstonesPlugin plugin) {
        // Shards
        plugin.getServer().addRecipe(new WarpShard().getRecipe(plugin));

        // Charged Shards
        for (Recipe recipe : new ChargedWarpShard().getRecipes(plugin)) {
            plugin.getServer().addRecipe(recipe);
        }

        // Hearts
        plugin.getServer().addRecipe(new WarpHeart().getRecipe(plugin));

        recipesRegistered = true;
    }

}