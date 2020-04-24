package com.kylenanakdewa.warpstones.warpstone.listeners;

import java.util.HashMap;
import java.util.Map;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.warpstones.WarpstonesPlayerData;
import com.kylenanakdewa.warpstones.WarpstonesPlugin;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;
import com.kylenanakdewa.warpstones.warpstone.events.WarpstoneActivateEvent;
import com.kylenanakdewa.warpstones.warpstone.events.WarpstoneInteractEvent;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handles everything for Warpstone delays.
 *
 * @author Kyle Nanakdewa
 */
public class WarpstoneDelayListener implements Listener {

    /** The Warpstones plugin instance. */
    private final WarpstonesPlugin plugin;

    /** The players that should be tracked for movement. */
    private final Map<Player, Integer> trackedPlayers;

    public WarpstoneDelayListener(WarpstonesPlugin plugin) {
        this.plugin = plugin;
        trackedPlayers = new HashMap<Player, Integer>();
    }

    /**
     * Fires the Warpstone Activation event.
     */
    private void callActivateEvent(Player player, Warpstone warpstone) {
        WarpstoneActivateEvent event = new WarpstoneActivateEvent(player, warpstone);
        plugin.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Starts the delay for a player.
     */
    private void startDelay(Player player, Warpstone warpstone, int delayLength) {
        // If a teleport is already scheduled, cancel it
        cancelDelay(player);

        // Schedule the teleport
        int taskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {

            // If set still contains player, they didn't move
            if (trackedPlayers.containsKey(player)) {
                callActivateEvent(player, warpstone);

                trackedPlayers.remove(player);
            }

        }, delayLength * 20);

        trackedPlayers.put(player, taskId);
    }

    /**
     * Cancels a Warpstone activation for a player, if scheduled.
     */
    private void cancelDelay(Player player) {
        Integer taskId = trackedPlayers.get(player);
        if (taskId != null) {
            plugin.getServer().getScheduler().cancelTask(taskId);
            trackedPlayers.remove(player);

            Utils.sendActionBar(player, CommonColors.ERROR + "You must stand still to activate this Warpstone.");
        }
    }

    /**
     * Called when a player interacts with a Warpstone.
     * <p>
     * Starts the delay.
     */
    @EventHandler
    public void onDelayBegin(WarpstoneInteractEvent event) {
        // Only start delay if player has health below threshold
        if (playerGetsDelay(event.getPlayer())) {
            startDelay(event.getPlayer(), event.getWarpstone(), 5);
            Utils.sendActionBar(event.getPlayer(), ChatColor.BLUE + "Stand still to activate Warpstone...");
        } else {
            callActivateEvent(event.getPlayer(), event.getWarpstone());
        }
    }

    /**
     * Checks if a player should get a delay.
     */
    private boolean playerGetsDelay(Player player) {
        if (player.hasPermission("warpstones.tp.instant")) {
            return false;
        }

        return !(new WarpstonesPlayerData(player).isAboveHealthThreshold());
    }

    /**
     * Called when a player moves.
     * <p>
     * If the player has a pending activation, cancels it.
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // If they moved more than 0.5m, cancel the delayed teleport
        double distanceSquaredMoved = event.getFrom().distanceSquared(event.getTo());
        if (distanceSquaredMoved > 0.25) {
            cancelDelay(player);
        }
    }

}