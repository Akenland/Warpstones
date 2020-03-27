package com.kylenanakdewa.warpstones.items.shards;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.warpstones.WarpPlayer;
import com.kylenanakdewa.warpstones.events.WarpstoneActivateEvent;
import com.kylenanakdewa.warpstones.events.PlayerWarpEvent.WarpCause;
import com.kylenanakdewa.warpstones.items.dust.WarpDust;
import com.kylenanakdewa.warpstones.items.shards.events.WarpShardLinkEvent;
import com.kylenanakdewa.warpstones.items.shards.events.WarpShardUseEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * Handles linking and usage of Warp Shards.
 *
 * @author Kyle Nanakdewa
 */
public class WarpShardListener implements Listener {

    @EventHandler
    public void onWarpstoneActivate(WarpstoneActivateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        WarpShard shard = new WarpShard();

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        // If player is holding a shard
        if (item != null && shard.matchesItem(item)) {

            // Cancel event, so no further processing is performed
            event.setCancelled(true);

            // Check if shard is linked
            if (shard.isLinked(item)) {
                // Call event to allow other plugins to take actions, as well as actual warp
                WarpShardUseEvent shardEvent = new WarpShardUseEvent(event, item);
                Bukkit.getPluginManager().callEvent(shardEvent);

                // Update the shard in the player's hand
                if (!shardEvent.isCancelled()) {
                    item = shardEvent.getItem();
                    event.getPlayer().getEquipment().setItemInMainHand(item);
                }
            }

            else {
                // Call event to allow other plugins to take actions, as well as actual link
                WarpShardLinkEvent shardEvent = new WarpShardLinkEvent(event, item);
                Bukkit.getPluginManager().callEvent(shardEvent);

                // Update the shard in the player's hand
                if (!shardEvent.isCancelled()) {
                    item = shardEvent.getItem();
                    event.getPlayer().getEquipment().setItemInMainHand(item);
                }
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWarpShardUse(WarpShardUseEvent event) {
        // Make sure warpstone exists
        if (event.getDestinationWarpstone() == null) {
            event.getPlayer().sendMessage(
                    CommonColors.ERROR + "Your warp shard is linked to an unknown Warpstone. Ask an Admin for help.");
            event.setCancelled(true);
        }

        // If shard is linked to this stone, warn player, and cancel event
        else if (event.getDestinationWarpstone().equals(event.getWarpstoneActivateEvent().getWarpstone())) {
            event.getPlayer().sendMessage(CommonColors.INFO
                    + "Your warp shard is linked to this Warpstone. Hold it while approaching another Warpstone, to return here.");
            event.setCancelled(true);
        }

        // If event was not cancelled, warp the player
        if (!event.isCancelled()) {
            Utils.sendActionBar(event.getPlayer(), ChatColor.BLUE + "Warping to shard's stored destination...");

            new WarpPlayer(event.getPlayer()).warp(event.getDestinationWarpstone(), false, WarpCause.SHARD);

            // Consume the Warp Shard
            if (event.willConsumeShard()) {
                event.getItem().setAmount(event.getItem().getAmount() - 1);

                // If shard breaks, drop some dust
                if (event.getItem().getAmount() == 0) {
                    ItemStack dust = new WarpDust().getRandomWarpDust(20, 4);
                    if (!dust.getType().equals(Material.AIR)) {
                        event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), dust);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWarpShardLink(WarpShardLinkEvent event) {
        // Make sure warpstone exists
        if (event.getWarpstone() == null) {
            event.setCancelled(true);
        }

        // If event was not cancelled, link the shard
        if (!event.isCancelled()) {
            event.getPlayer().sendMessage(CommonColors.MESSAGE
                    + "Your warp shard was linked to this Warpstone. Hold it while approaching another Warpstone, to return here.");

            event.link(event.getWarpstone());

            // Give some warp dust
            ItemStack dust = new WarpDust().getRandomWarpDust(60, 2);
            if (!dust.getType().equals(Material.AIR)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), dust);
            }
        }
    }

}