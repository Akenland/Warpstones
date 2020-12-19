package com.kylenanakdewa.warpstones.warpstone.listeners;

import java.util.HashMap;
import java.util.Map;

import com.kylenanakdewa.warpstones.warpstone.Warpstone;
import com.kylenanakdewa.warpstones.warpstone.WarpstoneManager;
import com.kylenanakdewa.warpstones.warpstone.events.WarpstoneApproachEvent;
import com.kylenanakdewa.warpstones.warpstone.events.WarpstoneInteractEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Tracks when players approach or interact with Warpstones.
 *
 * @author Kyle Nanakdewa
 */
public class WarpstoneApproachListener implements Listener {

    private final Map<Player, Warpstone> approachedWarpstone = new HashMap<Player, Warpstone>();

    /**
     * Gets the Warpstone the specified player has approached. If this player is not
     * at a Warpstone, this will return null.
     */
    private Warpstone getApproachedWarpstone(Player player) {
        return approachedWarpstone.get(player);
    }

    /**
     * Sets the Warpstone the specified player is approaching. Should be called when
     * a player approaches a Warpstone.
     */
    private void setApproachedWarpstone(Player player, Warpstone warpstone) {
        approachedWarpstone.put(player, warpstone);
    }

    /**
     * Clears the approached Warpstone for the specified player. Should be called
     * when a player leaves a Warpstone.
     */
    private void clearApproachedWarpstone(Player player) {
        approachedWarpstone.remove(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // If they moved more than 1m, check if they're near a Warpstone
        double distanceSquaredMoved = event.getFrom().distanceSquared(event.getTo());
        if (distanceSquaredMoved > 0.02) {

            // Look for Warpstones within 5m
            Warpstone warpstone = WarpstoneManager.get().getNearestWarpstone(event.getTo(), 5, false);
            if (warpstone != null) {

                // Only fire event if player wasn't already at this Warpstone
                if (getApproachedWarpstone(event.getPlayer()) == null) {
                    // Record the Warpstone, so we don't continously fire the event
                    setApproachedWarpstone(event.getPlayer(), warpstone);

                    // Fire approach event
                    WarpstoneApproachEvent approachEvent = new WarpstoneApproachEvent(event.getPlayer(), warpstone);
                    Bukkit.getPluginManager().callEvent(approachEvent);
                }

            }

            // If not near a Warpstone, clear the approached Warpstone
            else {
                clearApproachedWarpstone(event.getPlayer());
            }

        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Make sure event involves right-clicking a block with main hand
        if (!event.hasBlock() || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                || !event.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }

        // See if this player has approached a Warpstone
        Warpstone warpstone = getApproachedWarpstone(event.getPlayer());
        if (warpstone != null) {
            // Make sure clicked block is above the location (not the ground)
            if (event.getClickedBlock().getLocation().getBlockY() < warpstone.getLocation().getBlockY()) {
                return;
            }

            // Fire interact event
            WarpstoneInteractEvent interactEvent = new WarpstoneInteractEvent(event.getPlayer(), warpstone, event);
            Bukkit.getPluginManager().callEvent(interactEvent);
        }
    }

}