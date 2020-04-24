package com.kylenanakdewa.warpstones.warpstone.listeners;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;
import com.kylenanakdewa.warpstones.warpstone.WarpstoneManager;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Protects Warpstones from being damaged, by preventing block placement/removal
 * and explosions.
 *
 * @author Kyle Nanakdewa
 */
public class WarpstoneProtectionListener implements Listener {

    /**
     * Returns true if the specified location is near a Warpstone.
     */
    private boolean isNearWarpstone(Location location) {
        Warpstone warpstone = WarpstoneManager.get().getNearestWarpstone(location, 12, true);
        return warpstone != null;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasPermission("warpstones.manage.blocks")) {
            return;
        }

        Location location = event.getBlock().getLocation();

        // If Warpstone is found, cancel event and warn player
        if (isNearWarpstone(location)) {
            Utils.sendActionBar(event.getPlayer(), CommonColors.ERROR + "You cannot break blocks near a Warpstone.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().hasPermission("warpstones.manage.blocks")) {
            return;
        }

        Location location = event.getBlock().getLocation();

        // If Warpstone is found, cancel event and warn player
        if (isNearWarpstone(location)) {
            Utils.sendActionBar(event.getPlayer(), CommonColors.ERROR + "You cannot place blocks near a Warpstone.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        if (event.getPlayer().hasPermission("warpstones.manage.blocks")) {
            return;
        }

        Location location = event.getBlock().getLocation();

        // If Warpstone is found, cancel event and warn player
        if (isNearWarpstone(location)) {
            Utils.sendActionBar(event.getPlayer(), CommonColors.ERROR + "You cannot break blocks near a Warpstone.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplosion(BlockExplodeEvent event) {
        Location location = event.getBlock().getLocation().subtract(0, 3, 0);
        Warpstone warpstone = WarpstoneManager.get().getNearestWarpstone(location, 12, true);

        // If Warpstone is found, cancel event and warn player
        if (warpstone != null) {
            Utils.notifyAdminsError(CommonColors.ERROR + "Warpstone " + warpstone + " was blown up by block "
                    + event.getBlock().getType().getKey().getKey());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplosion(EntityExplodeEvent event) {
        Location location = event.getLocation().subtract(0, 3, 0);
        Warpstone warpstone = WarpstoneManager.get().getNearestWarpstone(location, 12, true);

        // If Warpstone is found, cancel event and warn player
        if (warpstone != null) {
            Utils.notifyAdminsError(CommonColors.ERROR + "Warpstone " + warpstone + " was blown up by entity "
                    + event.getEntityType().toString());
            event.setCancelled(true);
        }
    }

}