package com.kylenanakdewa.warpstones.warpstone.events;

import com.kylenanakdewa.warpstones.warpstone.Warpstone;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player saves at a Warpstone.
 * <p>
 * This event will fire as cancelled if the Warpstone is disabled, blocks
 * saving, or if the player does not meet requirements for activating it.
 * <p>
 * The main difference between this and WarpstoneActivateEvent is that this
 * event will hide the Warp GUI.
 *
 * @author Kyle Nanakdewa
 */
public class WarpstoneSaveEvent extends WarpstoneActivateEvent {
	private static final HandlerList handlers = new HandlerList();

	/**
	 * Creates a WarpstoneSaveEvent.
	 *
	 * @param player    the player who activated the Warpstone
	 * @param warpstone the Warpstone involved in the event
	 */
	public WarpstoneSaveEvent(Player player, Warpstone warpstone) {
		super(player, warpstone);
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}