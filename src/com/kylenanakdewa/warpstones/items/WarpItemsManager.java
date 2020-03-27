package com.kylenanakdewa.warpstones.items;

import com.kylenanakdewa.warpstones.WarpstonesPlugin;
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

    public WarpItemsManager(WarpstonesPlugin plugin) {
        registerListeners(plugin);
        registerRecipes(plugin);
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
    }

}