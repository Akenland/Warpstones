package com.kylenanakdewa.warpstones.warpstone.events;

import com.kylenanakdewa.warpstones.warpstone.Warpstone;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when a Warpstone's data is edited.
 *
 * @author Kyle Nanakdewa
 */
@Deprecated
public class WarpstoneEditEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private final Warpstone warpstone;

	/**
	 * Creates a WarpstoneActivateEvent.
	 *
	 * @param player    the player who activated the warpstone
	 * @param warpstone the warpstone involved in the event
	 */
	public WarpstoneEditEvent(Warpstone warpstone) {
		this.warpstone = warpstone;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * Gets the Warpstone that was edited.
	 *
	 * @return the warpstone
	 */
	public Warpstone getWarpstone() {
		return warpstone;
	}
}