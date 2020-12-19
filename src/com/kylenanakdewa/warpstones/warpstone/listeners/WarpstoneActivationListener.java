package com.kylenanakdewa.warpstones.warpstone.listeners;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.warpstones.WarpstonesPlayerData;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;
import com.kylenanakdewa.warpstones.warpstone.events.WarpstoneActivateEvent;
import com.kylenanakdewa.warpstones.warpstone.events.WarpstoneSaveEvent;
import com.kylenanakdewa.warpstones.warpstone.gui.WarpGui;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Handles everything that happens when a player activates a Warpstone.
 *
 * <ol>
 * <li>Check if Warpstone is disabled
 * <li>Check if player is allowed to use Warpstone
 * <li>Save Warpstone in player's recent list
 * <li>Show a GUI to allow the player to warp
 * </ol>
 *
 * For WarpstoneSaveEvents, the last step is skipped.
 *
 * @author Kyle Nanakdewa
 */
public class WarpstoneActivationListener implements Listener {

    /**
     * Called first. Checks if Warpstone is disabled, or if player does not meet
     * requirements, and cancels the event accordingly.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void checkWarpstone(WarpstoneActivateEvent event) {
        Player player = event.getPlayer();
        Warpstone warpstone = event.getWarpstone();

        // Disabled state
        if (warpstone.isDisabled()) {
            event.setCancelled(true);

            String disabledMsg = warpstone.getDisabledMsg();
            if (disabledMsg == null) {
                disabledMsg = "This Warpstone is inactive.";
            }
            Utils.sendActionBar(player, CommonColors.ERROR + disabledMsg);
        }

        // Permission
        if (warpstone.requiresPermission()
                && !player.hasPermission("warpstones.activate." + warpstone.getIdentifier())) {
            event.setCancelled(true);

            String disabledMsg = warpstone.getDisabledMsg();
            if (disabledMsg == null) {
                disabledMsg = "You can't use this Warpstone.";
            }
            Utils.sendActionBar(player, CommonColors.ERROR + disabledMsg);
        }

        // TODO Condition
    }

    /**
     * Called after initial checks, and after other listeners. Checks if this
     * Warpstone allows saving, and if so, saves this Warpstone to the player's list
     * of recent Warpstones.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void saveWarpstone(WarpstoneActivateEvent event) {
        // Check if event was cancelled earlier
        if (event.isCancelled() || event.isSpawnWarpstone()) {
            return;
        }

        Player player = event.getPlayer();
        Warpstone warpstone = event.getWarpstone();

        if (!warpstone.isSaveBlocked()) {
            new WarpstonesPlayerData(player).addRecentWarpstone(warpstone, 4);
            Utils.sendActionBar(player, ChatColor.BLUE + "Location saved.");
        }
    }

    /**
     * Called last. Shows the Warp GUI to the player.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void activateWarpstone(WarpstoneActivateEvent event) {
        // Check if event was cancelled earlier
        if (event.isCancelled() || event instanceof WarpstoneSaveEvent) {
            return;
        }

        Player player = event.getPlayer();
        Warpstone warpstone = event.getWarpstone();

        // If Warpstone has a forced destination, warp the player there
        Location forcedDestination = warpstone.getForcedDestination();
        if(forcedDestination!=null){
            new WarpstonesPlayerData(player).teleport(forcedDestination, true);
            Utils.sendActionBar(player, ChatColor.BLUE+"Warping...");
            return;
        }

        // Warp GUI
        new WarpGui(player, warpstone).display();
    }

}