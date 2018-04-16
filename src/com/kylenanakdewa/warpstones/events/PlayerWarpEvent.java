package com.kylenanakdewa.warpstones.events;

import com.kylenanakdewa.warpstones.Warpstone;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when a Player warps to a Warpstone.
 * @author Kyle Nanakdewa
 */
public class PlayerWarpEvent extends PlayerWarpstoneEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /**
     * The cause of this warp.
     */
    public enum WarpCause {
        /** Caused by a command. */
        COMMAND,
        /** Caused by activating a Warpstone and choosing a destination. */
        WARPSTONE,
        /** Caused by using a linked Warp Shard. */
        SHARD;
    }

	private boolean cancelled;
	private final WarpCause cause;

	/**
	 * Creates a PlayerWarpEvent.
	 * @param player the player who is warping
	 * @param warpstone the destination warpstone
     * @param cause the cause of this warp
	 */
	public PlayerWarpEvent(Player player, Warpstone warpstone, WarpCause cause){
		super(player, warpstone);
		this.cause = cause;
	}


	public HandlerList getHandlers(){
		return handlers;
	}
	public static HandlerList getHandlerList(){
		return handlers;
	}

	public boolean isCancelled(){
		return cancelled;
	}
	public void setCancelled(boolean cancel){
		cancelled = cancel;
	}


	/**
	 * Gets the cause of this warp.
	 * @return whether this warp was caused by a command, another warpstone, or a Warp Shard
	 */
	public WarpCause getCause(){
		return cause;
	}
}