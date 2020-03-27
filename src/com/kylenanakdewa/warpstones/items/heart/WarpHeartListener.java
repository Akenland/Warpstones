package com.kylenanakdewa.warpstones.items.heart;

import java.util.Map;
import java.util.Random;

import com.kylenanakdewa.warpstones.events.WarpstoneActivateEvent;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * Increments the charge level of Warp Hearts.
 *
 * @author Kyle Nanakdewa
 */
public class WarpHeartListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWarpstoneActivate(WarpstoneActivateEvent event) {
        if (event.isCancelled() || event.isLastWarpstone() || event.isHomeWarpstone() || event.isSpawnWarpstone()) {
            return;
        }

        WarpHeart heart = new WarpHeart();

        // Get all possible Warp Hearts
        Map<Integer, ? extends ItemStack> items = event.getPlayer().getInventory().all(Material.LAPIS_LAZULI);

        if (items.size() >= 1) {
            // Eliminate any which are not hearts
            items.values().removeIf(item -> !heart.matchesItem(item));

            // If any hearts found, choose one at random
            if (items.size() >= 1) {
                // Pick an item at random
                Integer[] itemSlotArray = items.values().toArray(new Integer[0]);
                int itemIndex = itemSlotArray.length > 1 ? new Random().nextInt(itemSlotArray.length) : 0;
                int chosenItem = itemSlotArray[itemIndex];

                ItemStack item = items.get(chosenItem);

                // Increment the charge level
                item = heart.incrementChargeLevel(item);

                // Put the item back into the inventory
                event.getPlayer().getInventory().setItem(chosenItem, item);
            }
        }

    }

}