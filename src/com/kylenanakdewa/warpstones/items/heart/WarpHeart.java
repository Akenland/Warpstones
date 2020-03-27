package com.kylenanakdewa.warpstones.items.heart;

import java.util.ArrayList;
import java.util.List;

import com.kylenanakdewa.warpstones.items.LapisItem;
import com.kylenanakdewa.warpstones.items.dust.WarpDust;
import com.kylenanakdewa.warpstones.items.shards.WarpShard;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

/**
 * Warp Heart custom item. Crafting ingredient, can be charged.
 *
 * @author Kyle Nanakdewa
 */
public class WarpHeart extends LapisItem {

    public WarpHeart() {
        super("Warp Heart");
    }

    @Override
    public boolean matchesItem(ItemStack item) {
        // Check exact match
        if (super.matchesItem(item)) {
            return true;
        }

        // Check that item material and name matches
        if (item != null && item.getType().equals(material) && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            return meta.hasDisplayName() && meta.getDisplayName().equals(itemName);
        }

        return false;
    }

    @Override
    public ShapedRecipe getRecipe(Plugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "recipes/warp_heart");
        ShapedRecipe recipe = new ShapedRecipe(key, getNewItem());

        // Ingredient must be exact match for Warp Dust and Shard - uses new Bukkit
        // draft API
        @SuppressWarnings("deprecation")
        ExactChoice dustIngredient = new ExactChoice(new WarpDust().getNewItem());
        @SuppressWarnings("deprecation")
        ExactChoice shardIngredient = new ExactChoice(new WarpShard().getNewItem());

        recipe.shape("DSD", "SES", "DSD");
        recipe.setIngredient('D', dustIngredient);
        recipe.setIngredient('S', shardIngredient);
        recipe.setIngredient('E', Material.ENDER_EYE);

        return recipe;
    }

    /**
     * Gets the charge level of a Warp Heart, from 0-100. If the item is not a Warp
     * Heart, returns -1.
     */
    public int getChargeLevel(ItemStack item) {
        // If item is not a Heart, return null
        if (!matchesItem(item)) {
            return -1;
        }

        // If Heart does not yet have a charge level, return 0
        if (!item.getItemMeta().hasLore()) {
            return 0;
        }

        // Get first line of lore
        String target = item.getItemMeta().getLore().get(0);

        // Strip colours and formatting
        target = ChatColor.stripColor(target);

        // Get the substring before %
        target = target.split("%")[0];

        // Parse as integer
        int chargeLevel = Integer.parseInt(target);

        return chargeLevel;
    }

    /**
     * Sets the charge level of a Warp Heart. If the item is not a Warp Heart, it
     * will be returned as-is. If chargeLevel is not between 0-100, an exception
     * will be thrown.
     */
    public ItemStack setChargeLevel(ItemStack item, int chargeLevel) {
        if(chargeLevel<1 || chargeLevel>100){
            throw new IllegalArgumentException("Warp Heart charge level must be between 0-100");
        }

        if (matchesItem(item)) {
            ItemMeta meta = item.getItemMeta();

            // Add blue + magic format codes, to obscure target string
            String target = ChatColor.GRAY.toString() + chargeLevel + "% charged";

            // Create variable-size array to store lore text
            List<String> lore = new ArrayList<String>();
            lore.add(target);

            // Set lore text on item
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Increments the charge level of a Warp Heart. If the item is not a Warp Heart,
     * it will be returned as-is.
     */
    public ItemStack incrementChargeLevel(ItemStack item) {
        int chargeLevel = getChargeLevel(item);

        if (chargeLevel >= 0) {
            chargeLevel++;
            item = setChargeLevel(item, chargeLevel);
        }

        return item;
    }

}