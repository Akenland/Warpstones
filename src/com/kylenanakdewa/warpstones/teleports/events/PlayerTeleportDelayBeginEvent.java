package com.kylenanakdewa.warpstones.teleports.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a player is delayed, before a teleport.
 *
 * @author Kyle Nanakdewa
 */
public class PlayerTeleportDelayBeginEvent extends PlayerTeleportEvent {
    private static final HandlerList handlers = new HandlerList();

    /** The destination that the player will be teleported to. */
    private final Location destination;

    /** The length of the delay, in seconds. */
    private int delayLength;

    /**
     * Creates the event.
     *
     * @param player      the player that will be teleported
     * @param destination the location that the player will be teleported to
     * @param delayLength the length of the delay, before the player is teleported
     */
    public PlayerTeleportDelayBeginEvent(Player player, Location destination, int delayLength) {
        super(player);
        this.destination = destination;
        this.delayLength = delayLength;
    }

    @Override
    public Location getDestination() {
        return destination;
    }

    /**
     * Gets the length of the delay, before the player will be teleported.
     *
     * @return the length of the delay, in seconds
     */
    public int getDelayLength() {
        return delayLength;
    }

    /**
     * Sets the length of the delay, before the player will be teleported.
     *
     * @param delayLength the length of the delay, in seconds
     */
    public void setDelayLength(int delayLength) {
        this.delayLength = delayLength;
    }

    @Override
    protected boolean enableDistanceCheck() {
        // TODO Auto-generated method stub
        return false;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}