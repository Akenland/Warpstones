package com.kylenanakdewa.warpstones.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * A custom item, based on Lapis Lazuli.
 *
 * @author Kyle Nanakdewa
 */
public abstract class LapisItem extends CustomItem {

    public LapisItem(String itemName) {
        material = Material.LAPIS_LAZULI;
        this.itemName = ChatColor.BLUE + itemName;
    }

}