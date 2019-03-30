package com.kylenanakdewa.warpstones.quicksave;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.warpstones.events.WarpstoneActivateEvent;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

/**
 * QuickSaveListener
 * @author Kyle Nanakdewa
 */
public class QuickSaveListener implements Listener {

    /**
     * Quicksaves when a warpstone is activated.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onWarpstoneActivate(WarpstoneActivateEvent event) {
        if(event.isCancelled() || !event.getPlayer().hasPermission("warpstones.quicksave") || event.isSpawnWarpstone()) return;

        new PlayerQuickSaveData(event.getPlayer()).quickSave();
    }


    /** The players who should recover their quicksaved items on respawn, and the items they will recover. */
    private static final Map<UUID,List<ItemStack>> playersToRecover = new HashMap<UUID,List<ItemStack>>();

    /**
     * Checks if players should recover their quicksaved items on next respawn.
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        EntityDamageEvent lastDamageEvent = event.getEntity().getLastDamageCause();

        // If player falls into void, keep inventory, and notify admins of the location
        if(lastDamageEvent.getCause().equals(DamageCause.VOID)) {
            event.setKeepInventory(true);
            Location deathLoc = event.getEntity().getLocation();
            Utils.notifyAdminsError(event.getEntity().getDisplayName()+CommonColors.ERROR+" fell into the void at "+deathLoc.getBlockX()+" "+deathLoc.getBlockZ());
            return;
        }

        if(!event.getEntity().hasPermission("warpstones.quicksave")) return;

        // If death was not PvP (or player has permission), mark player to load their quicksave on respawn
        if(!QuickSaveUtils.wasPvPDeath(event) || event.getEntity().hasPermission("warpstones.quicksave.all-deaths")){
            Utils.notifyAdmins(event.getEntity().getDisplayName()+CommonColors.INFO+" will load their quicksave on respawn.");

            List<ItemStack> savedItems = new PlayerQuickSaveData(event.getEntity()).getSavedItems();
            List<ItemStack> droppedItems = event.getDrops();
            Utils.notifyAdmins(event.getEntity().getDisplayName()+CommonColors.INFO+" has "+savedItems.size()+" saved items, "+droppedItems.size()+" drops.");

            savedItems.removeIf(item -> !droppedItems.contains(item));
            droppedItems.removeIf(item -> savedItems.contains(item));
            Utils.notifyAdmins(event.getEntity().getDisplayName()+CommonColors.INFO+" will recover "+savedItems.size()+" items, drop "+droppedItems.size()+".");

            playersToRecover.put(event.getEntity().getUniqueId(), savedItems);
        }
    }

    /**
     * Loads the quicksave on respawn.
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if(playersToRecover.containsKey(event.getPlayer().getUniqueId())){
            List<ItemStack> items = playersToRecover.get(event.getPlayer().getUniqueId());
            Utils.notifyAdmins(event.getPlayer().getDisplayName()+CommonColors.INFO+" recovered "+items.size()+" items.");
            event.getPlayer().getInventory().addItem(items.toArray(new ItemStack[0]));
            playersToRecover.remove(event.getPlayer().getUniqueId());
        }
    }
}