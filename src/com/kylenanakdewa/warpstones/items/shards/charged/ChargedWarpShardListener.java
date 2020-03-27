package com.kylenanakdewa.warpstones.items.shards.charged;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.warpstones.WarpPlayer;
import com.kylenanakdewa.warpstones.events.WarpstoneActivateEvent;
import com.kylenanakdewa.warpstones.items.dust.WarpDust;
import com.kylenanakdewa.warpstones.items.shards.charged.events.ChargedWarpShardUseEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handles crafting and usage of Charged Warp Shards.
 *
 * @author Kyle Nanakdewa
 */
public class ChargedWarpShardListener implements Listener {

    @EventHandler
    public void onWarpstoneActivate(WarpstoneActivateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        ChargedWarpShard shard = new ChargedWarpShard();

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        // If player is holding a linked shard
        if (item != null && shard.isLinked(item)) {

            // Cancel event, so no further processing is performed
            event.setCancelled(true);

            // Call event to allow other plugins to take actions, as well as actual warp
            ChargedWarpShardUseEvent shardEvent = new ChargedWarpShardUseEvent(event, item);
            Bukkit.getPluginManager().callEvent(shardEvent);

            // Update the shard in the player's hand
            if (!shardEvent.isCancelled()) {
                item = shardEvent.getItem();
                event.getPlayer().getEquipment().setItemInMainHand(item);
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWarpShardUse(ChargedWarpShardUseEvent event) {
        // Make sure warpstone exists
        if (event.getDestinationLocation() == null) {
            event.getPlayer().sendMessage(
                    CommonColors.ERROR + "Your warp shard is linked to an unknown location. Ask an Admin for help.");
            event.setCancelled(true);
        }

        // If event was not cancelled, warp the player
        if (!event.isCancelled()) {
            Utils.sendActionBar(event.getPlayer(), ChatColor.BLUE + "Warping to shard's stored destination...");

            new WarpPlayer(event.getPlayer()).teleport(event.getDestinationLocation(), false);

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

    @EventHandler
    public void onChargedWarpShardCraft(CraftItemEvent event) {
        if (event.isCancelled()) {
            return;
        }

        ChargedWarpShard shard = new ChargedWarpShard();

        ItemStack item = event.getInventory().getResult();

        if (item != null && shard.matchesItem(item)) {
            item = shard.link(item, event.getWhoClicked().getLocation());
            event.getInventory().setResult(item);
        }
    }

}