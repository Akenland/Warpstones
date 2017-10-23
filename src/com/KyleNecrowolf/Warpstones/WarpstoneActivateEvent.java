package com.KyleNecrowolf.Warpstones;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

// Fired when a warpstone is activated by a player. Will fire as cancelled if warpstone is disabled or player does not have permission.
public final class WarpstoneActivateEvent extends Event implements Cancellable {    
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Warpstone warpstone;
    private boolean cancelled;

    //// Constructor
    WarpstoneActivateEvent(Player player, Warpstone warpstone){
        this.player = player;
        this.warpstone = warpstone;
        
    }

    //// Bukkit Event Handler List
    public HandlerList getHandlers(){
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }

    //// Cancellable
    public boolean isCancelled(){
        return cancelled;
    }
    public void setCancelled(boolean cancel){
        cancelled = cancel;
    }


    //// Getting event data
    // Get the player that activated the warpstone
    public Player getPlayer(){
        return player;
    }
    // Get the player's data and warpstones player object
    public WarpPlayer getWarpPlayer(){
        return new WarpPlayer(player);
    }
    // Get the warpstone that was activated
    public Warpstone getWarpstone(){
        return warpstone;
    }
}