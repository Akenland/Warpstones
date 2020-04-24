package com.kylenanakdewa.warpstones.items.shards.charged;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.warpstones.items.heart.WarpHeart;
import com.kylenanakdewa.warpstones.items.shards.WarpShard;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.plugin.Plugin;

/**
 * Charged Warp Shard custom item. Allows players to link and warp to any
 * location.
 *
 * @author Kyle Nanakdewa
 */
public class ChargedWarpShard extends WarpShard {

    public ChargedWarpShard() {
        super();
        itemName = "Charged Warp Shard";
    }

    @Override
    public ShapelessRecipe getRecipe(Plugin plugin) {
        // Can't register a single recipe
        return null;
    }

    /**
     * Gets a recipe for crafting a Charged Warp Shard out of a Warp Heart with a
     * charge level within the specified range.
     */
    private ShapelessRecipe getRecipe(NamespacedKey key, int minChargeLevel, int maxChargeLevel, int shardAmount) {
        ItemStack result = getNewItem();
        result.setAmount(shardAmount);

        ShapelessRecipe recipe = new ShapelessRecipe(key, result);

        List<ItemStack> ingredients = new ArrayList<ItemStack>();
        for (int i = minChargeLevel; i <= maxChargeLevel; i++) {
            // Create Hearts with different charge levels
            WarpHeart heart = new WarpHeart();
            ItemStack item = heart.getNewItem();

            item = heart.setChargeLevel(item, i);

            ingredients.add(item);
        }

        // Ingredient must be exact match for Warp Heart - uses new Bukkit draft API
        @SuppressWarnings("deprecation")
        ExactChoice ingredient = new ExactChoice(ingredients);

        recipe.addIngredient(ingredient);
        recipe.addIngredient(Material.ENDER_PEARL);

        // Set group for recipe book
        recipe.setGroup("charged_warp_shards");

        return recipe;
    }

    /**
     * Gets the four different recipes for crafting Charged Warp Shards.
     */
    public Set<ShapelessRecipe> getRecipes(Plugin plugin) {
        Set<ShapelessRecipe> recipes = new HashSet<ShapelessRecipe>();

        NamespacedKey key25 = new NamespacedKey(plugin, "recipes/charged_warp_shard_25");
        recipes.add(getRecipe(key25, 25, 49, 8));

        NamespacedKey key50 = new NamespacedKey(plugin, "recipes/charged_warp_shard_50");
        recipes.add(getRecipe(key50, 50, 74, 16));

        NamespacedKey key75 = new NamespacedKey(plugin, "recipes/charged_warp_shard_75");
        recipes.add(getRecipe(key75, 75, 99, 24));

        NamespacedKey key100 = new NamespacedKey(plugin, "recipes/charged_warp_shard_100");
        recipes.add(getRecipe(key100, 100, 100, 32));

        return recipes;
    }

    /**
     * Gets the Location that a Charged Warp Shard is linked to. If the item is not
     * a linked Charged Warp Shard, or the World does not exist, returns null.
     */
    public Location getLinkedLocation(ItemStack item) {
        String target = getLinked(item);

        if (target == null) {
            return null;
        }

        // Break target string into World, X, Y, Z
        String[] coords = target.split(" ");
        String worldName = coords[0];
        int x = Integer.parseInt(coords[1]);
        int y = Integer.parseInt(coords[2]);
        int z = Integer.parseInt(coords[3]);

        // Check that world exists
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Utils.notifyAdminsError("Charged Warp Shard has an invalid world: " + target);
            return null;
        }

        Location location = new Location(world, x, y, z);

        return location;
    }

    @Override
    public Warpstone getLinkedWarpstone(ItemStack item) {
        // Not valid for this type of Warp Shard
        return null;
    }

    /**
     * Links a Charged Warp Shard to the specified Location. If the item is not an
     * unlinked Charged Warp Shard, it will be returned as-is.
     */
    public ItemStack link(ItemStack item, Location location) {
        // Break out location data
        String worldName = location.getWorld().getName();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        String coords = x + " " + y + " " + z;

        // Use location data as target string
        String target = worldName + " " + coords;

        // Set data on item
        item = link(item, target);

        // Add X Y Z co-ords
        item = appendLore(item, ChatColor.GRAY + coords);

        // TODO Add Story Location name

        // TODO Add farthing, with colour

        return item;
    }

}