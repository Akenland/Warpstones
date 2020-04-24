package com.kylenanakdewa.warpstones.warpstone.events;

import com.kylenanakdewa.warpstones.warpstone.Warpstone;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Fired when a player interacts with (right-clicks) a Warpstone.
 *
 * @author Kyle Nanakdewa
 */
public class WarpstoneInteractEvent extends PlayerWarpstoneEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;

	/** The PlayerInteractEvent that triggered this event. */
	private final PlayerInteractEvent playerInteractEvent;

	/**
	 * Creates a WarpstoneInteractEvent.
	 *
	 * @param player              the player who activated the Warpstone
	 * @param warpstone           the Warpstone involved in the event
	 * @param playerInteractEvent the PlayerInteractEvent that triggered this event
	 */
	public WarpstoneInteractEvent(Player player, Warpstone warpstone, PlayerInteractEvent playerInteractEvent) {
		super(player, warpstone);
		this.playerInteractEvent = playerInteractEvent;
	}

	/**
	 * Gets the PlayerInteractEvent that triggered this event.
	 *
	 * @return the root event
	 */
	public PlayerInteractEvent getPlayerInteractEvent() {
		return playerInteractEvent;
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