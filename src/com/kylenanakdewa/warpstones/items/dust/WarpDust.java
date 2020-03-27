package com.kylenanakdewa.warpstones.items.dust;

import java.util.Random;

import com.kylenanakdewa.warpstones.items.LapisItem;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Warp Dust custom item. Crafting ingredient.
 *
 * @author Kyle Nanakdewa
 */
public class WarpDust extends LapisItem {

    public WarpDust() {
        super("Warp Dust");
    }

    /**
     * Gets a random amount of Warp Dust.
     *
     * @param noDustChance the percentage chance to get 0 dust (0 to always get
     *                     dust, 100 to never get dust)
     * @param maxDust      the maximum amount of dust
     */
    public ItemStack getRandomWarpDust(int noDustChance, int maxDust) {
        Random random = new Random();

        if (random.nextInt(100) > noDustChance) {
            int count = random.nextInt(maxDust) + 1;

            ItemStack dust = getNewItem();
            dust.setAmount(count);

            return dust;
        }

        return new ItemStack(Material.AIR);
    }

}