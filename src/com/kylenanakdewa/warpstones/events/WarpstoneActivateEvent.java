package com.kylenanakdewa.warpstones.events;

import com.kylenanakdewa.warpstones.Warpstone;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when a Warpstone is activated by a Player.
 * <p>
 * This event will fire as cancelled if the Warpstone is disabled, or if the Player does not meet requirements for activating it.
 * @author Kyle Nanakdewa
 */
public class WarpstoneActivateEvent extends PlayerWarpstoneEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;

	/**
	 * Creates a WarpstoneActivateEvent.
	 * @param player the player who activated the warpstone
	 * @param warpstone the warpstone involved in the event
	 */
	public WarpstoneActivateEvent(Player player, Warpstone warpstone){
		super(player, warpstone);
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

}