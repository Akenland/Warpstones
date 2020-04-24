package com.kylenanakdewa.warpstones.warpstone.listeners;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.warpstones.WarpstonesPlayerData;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;
import com.kylenanakdewa.warpstones.warpstone.events.WarpstoneApproachEvent;
import com.kylenanakdewa.warpstones.warpstone.events.WarpstoneSaveEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Saves a Warpstone to a player's list of recent Warpstones when they approach
 * it.
 *
 * @author Kyle Nanakdewa
 */
public class WarpstoneSaveListener implements Listener {

    @EventHandler
    public void onApproachWarpstone(WarpstoneApproachEvent event) {
        Player player = event.getPlayer();
        Warpstone warpstone = event.getWarpstone();

        // Popup Warpstone name
        if (warpstone.getDisplayName() != null) {
            String wsName = ChatColor.BLUE + warpstone.getDisplayName();
            player.sendTitle("", wsName, -1, -1, -1);
        }

        // If Spawn Warpstone, or saving blocked, don't need to save
        if (event.isSpawnWarpstone() || warpstone.isSaveBlocked()) {
            return;
        }

        // If player is allowed to auto-save, fire the save event
        if (playerCanAutoSave(player)) {
            WarpstoneSaveEvent saveEvent = new WarpstoneSaveEvent(player, warpstone);
            Bukkit.getPluginManager().callEvent(saveEvent);
        }
        // Otherwise, prompt the player to save manually
        else {
            Utils.sendActionBar(player, CommonColors.ERROR + "Activate Warpstone to save location.");
        }

    }

    /**
     * Checks if a player should save immediately. If not, they must right-click the
     * Warpstone to save.
     */
    private boolean playerCanAutoSave(Player player) {
        if (player.hasPermission("warpstones.autosave.always")) {
            return true;
        }
        if (player.hasPermission("warpstones.autosave")) {
            return new WarpstonesPlayerData(player).isAboveHealthThreshold();
        }
        return false;
    }
}