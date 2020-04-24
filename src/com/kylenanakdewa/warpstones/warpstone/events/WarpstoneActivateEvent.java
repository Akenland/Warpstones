package com.kylenanakdewa.warpstones.warpstone.events;

import com.kylenanakdewa.warpstones.warpstone.Warpstone;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when a Warpstone is activated by a player.
 * <p>
 * This event will fire as cancelled if the Warpstone is disabled, or if the
 * player does not meet requirements for activating it.
 *
 * @author Kyle Nanakdewa
 */
public class WarpstoneActivateEvent extends PlayerWarpstoneEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;

	/**
	 * Creates a WarpstoneActivateEvent.
	 *
	 * @param player    the player who activated the Warpstone
	 * @param warpstone the Warpstone involved in the event
	 */
	public WarpstoneActivateEvent(Player player, Warpstone warpstone) {
		super(player, warpstone);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

}