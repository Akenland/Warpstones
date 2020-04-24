package com.kylenanakdewa.warpstones.teleports.listeners;

import java.util.HashMap;
import java.util.Map;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.warpstones.WarpstonesPlayerData;
import com.kylenanakdewa.warpstones.WarpstonesPlugin;
import com.kylenanakdewa.warpstones.teleports.events.PlayerTeleportDelayBeginEvent;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handles everything for player teleport delays.
 * <p>
 * To create a delayed teleport, call the event
 * {@link PlayerTeleportDelayBeginEvent}
 *
 * @author Kyle Nanakdewa
 */
public class PlayerTeleportDelayListener implements Listener {

    /** The Warpstones plugin instance. */
    private final WarpstonesPlugin plugin;

    /** The players that should be tracked for movement. */
    private final Map<Player, Integer> trackedPlayers;

    public PlayerTeleportDelayListener(WarpstonesPlugin plugin) {
        this.plugin = plugin;
        trackedPlayers = new HashMap<Player, Integer>();
    }

    /**
     * Starts the delay for a player.
     */
    private void startDelay(Player player, Location destination, int delayLength) {
        // If a teleport is already scheduled, cancel it
        cancelDelay(player);

        // Schedule the teleport
        int taskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {

            // If set still contains player, they didn't move
            if (trackedPlayers.containsKey(player)) {
                new WarpstonesPlayerData(player).teleport(destination, false);
                trackedPlayers.remove(player);
            }

        }, delayLength * 20);

        trackedPlayers.put(player, taskId);
    }

    /**
     * Cancels a delayed teleport for a player, if scheduled.
     */
    private void cancelDelay(Player player) {
        Integer taskId = trackedPlayers.get(player);
        if (taskId != null) {
            plugin.getServer().getScheduler().cancelTask(taskId);
            trackedPlayers.remove(player);

            Utils.sendActionBar(player, CommonColors.ERROR + "You cannot teleport while moving.");
        }
    }

    /**
     * Called when a player teleport delay should begin.
     * <p>
     * Starts the delay.
     */
    @EventHandler
    public void onDelayBegin(PlayerTeleportDelayBeginEvent event) {
        startDelay(event.getPlayer(), event.getDestination(), event.getDelayLength());
    }

    /**
     * Called when a player moves.
     * <p>
     * If the player has a delayed teleport, cancels it.
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