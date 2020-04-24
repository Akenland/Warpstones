package com.kylenanakdewa.warpstones.teleports.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;

/**
 * Represents an event involving a teleporting Player.
 *
 * @author Kyle Nanakdewa
 */
public abstract class PlayerTeleportEvent extends PlayerEvent implements Cancellable {

    /**
     * The cancellation state of this event. A cancelled event will not be executed
     * in the server, but will still pass to other plugins
     */
    private boolean cancelled;

    /**
     * Creates the event.
     *
     * @param player the player that is being teleported
     */
    protected PlayerTeleportEvent(Player player) {
        super(player);
    }

    /**
     * Gets the destination that the player will be teleported to.
     * <p>
     * May be null if a destination is not set.
     *
     * @return the destination, or null
     */
    public abstract Location getDestination();

    /**
     * Gets whether the distance between the player and the destination should be
     * checked.
     *
     * @return true if distance should be checked
     */
    protected abstract boolean enableDistanceCheck();

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}