package com.kylenanakdewa.warpstones.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

/**
 * A custom item.
 *
 * @author Kyle Nanakdewa
 */
public abstract class CustomItem {

    /** The material of this custom item. */
    protected Material material;

    /** The display name of this custom item. */
    protected String itemName;

    /**
     * Gets the ItemMeta for this custom item.
     */
    private ItemMeta getItemMeta() {
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);

        meta.setDisplayName(itemName);

        return meta;
    }

    /**
     * Gets an ItemStack for this custom item, with a stack size of 1.
     */
    public ItemStack getNewItem() {
        ItemStack itemStack = new ItemStack(material);

        itemStack.setItemMeta(getItemMeta());

        return itemStack;
    }

    /**
     * Checks if an ItemStack matches this custom item.
     */
    public boolean matchesItem(ItemStack item) {
        return getNewItem().isSimilar(item);
    }

    /**
     * Gets the recipe for this custom item. If a recipe does not exist, returns null.
     */
    public Recipe getRecipe(Plugin plugin) {
        return null;
    }

}