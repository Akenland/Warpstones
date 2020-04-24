package com.kylenanakdewa.warpstones.teleports.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * PlayerTeleportToPlayerEvent
 */
public class PlayerTeleportToPlayerEvent extends PlayerTeleportEvent {

    private Player destinationPlayer;

    public PlayerTeleportToPlayerEvent(Player teleportingPlayer, Player destinationPlayer) {
        super(teleportingPlayer);
        this.destinationPlayer = destinationPlayer;
    }

    @Override
    public Location getDestination() {
        return destinationPlayer.getLocation();
    }

    @Override
    protected boolean enableDistanceCheck() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public HandlerList getHandlers() {
        // TODO Auto-generated method stub
        return null;
    }


}