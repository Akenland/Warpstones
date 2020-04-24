package com.kylenanakdewa.warpstones.warpstone.events;

import com.kylenanakdewa.warpstones.warpstone.Warpstone;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Fired when a Warpstone is approached by a Player. This happens when a player
 * moves within 5m of a Warpstone.
 *
 * @author Kyle Nanakdewa
 */
public class WarpstoneApproachEvent extends PlayerWarpstoneEvent {
	private static final HandlerList handlers = new HandlerList();

	/**
	 * Creates a WarpstoneApproachEvent.
	 *
	 * @param player    the player who approached the Warpstone
	 * @param warpstone the Warpstone involved in the event
	 */
	public WarpstoneApproachEvent(Player player, Warpstone warpstone) {
		super(player, warpstone);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}