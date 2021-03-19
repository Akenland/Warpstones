package com.kylenanakdewa.warpstones.warpstone.gui;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.warpstones.WarpstonesPlayerData;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;
import com.kylenanakdewa.warpstones.warpstone.WarpstoneManager;
import com.kylenanakdewa.warpstones.warpstone.events.PlayerWarpEvent.WarpCause;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;

/**
 * Listens for when a player clicks in a Warpstone GUI menu.
 *
 * @author Kyle Nanakdewa
 */
public class WarpGuiListener implements Listener {

    /**
     * Returns true if the specified Inventory GUI is a Warpstone GUI.
     */
    private boolean isWarpGui(InventoryView transaction) {
        String titlePrefix = ChatColor.BLUE + ChatColor.GRAY.toString() + ChatColor.BLUE;

        return transaction.getTitle().startsWith(titlePrefix);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // Make sure its a Warp GUI
        if (!isWarpGui(event.getView())) {
            return;
        }

        // Cancel event
        event.setCancelled(true);

        // Make sure top inventory was clicked, and clicked slot has item
        if (!event.getClickedInventory().equals(event.getView().getTopInventory()) || event.getCurrentItem() == null) {
            return;
        }

        WarpstonesPlayerData playerData = new WarpstonesPlayerData((Player) event.getWhoClicked());
        Warpstone warpstone;

        switch (event.getSlot()) {
            // Home Bed item
            case 19:
                playerData.teleportHome(false);
                break;

            // Farthing Hub item
            case 20:
                // TODO
                event.getWhoClicked().sendMessage(CommonColors.ERROR
                        + "You haven't yet chosen a Farthing. Enter the Tree of Harmony at spawn to choose a Farthing.");
                break;

            // Spawn item
            case 24:
                playerData.teleportSpawn(false);
                break;

            // Events Hub item
            case 25:
                // TODO
                warpstone = WarpstoneManager.get().getWarpstone("events_hub");
                if(warpstone==null){
                    event.getWhoClicked().sendMessage(CommonColors.ERROR + "No event is currently active.");
                } else {
                    playerData.warp(warpstone, WarpCause.WARPSTONE, false);
                }
                break;

            // Recent items
            case 39:
            case 40:
            case 41:
                String target = event.getCurrentItem().getItemMeta().getLore().get(0);
                target = ChatColor.stripColor(target);
                warpstone = WarpstoneManager.get().getWarpstone(target);
                if(warpstone!=null){
                    playerData.warp(warpstone, WarpCause.WARPSTONE, false);
                }
                break;

            default:
                break;
        }
    }

}