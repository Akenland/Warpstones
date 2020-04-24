package com.kylenanakdewa.warpstones.warpstone.events;

import com.kylenanakdewa.warpstones.WarpstonesPlayerData;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;
import com.kylenanakdewa.warpstones.warpstone.WarpstoneManager;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

/**
 * Represents an event involving a Player and a Warpstone.
 *
 * @author Kyle Nanakdewa
 */
public abstract class PlayerWarpstoneEvent extends PlayerEvent {

	/** The Warpstone involved in this event. */
	private final Warpstone warpstone;
	/** The player save data. */
	private final WarpstonesPlayerData playerData;

	/**
	 * Creates the event.
	 *
	 * @param player    the player involved in the event
	 * @param warpstone the warpstone involved in the event
	 */
	protected PlayerWarpstoneEvent(Player player, Warpstone warpstone) {
		super(player);
		this.warpstone = warpstone;
		playerData = new WarpstonesPlayerData(player);
	}

	/**
	 * Gets the Warpstone involved in this event.
	 *
	 * @return the Warpstone involved in this event
	 */
	public Warpstone getWarpstone() {
		return warpstone;
	}

	/**
	 * Returns true if event involves a player's recently activated Warpstone.
	 *
	 * @return true if it's one of the player's recent Warpstones, otherwise false
	 */
	public boolean isRecentWarpstone() {
		return playerData.getRecentWarpstones().keySet().contains(warpstone);
	}

	/**
	 * Returns true if event involves the player's most recently activated
	 * Warpstone.
	 *
	 * @return true if it's one of the player's recent Warpstones, otherwise false
	 */
	public boolean isMostRecentWarpstone() {
		return playerData.getMostRecentWarpstone().equals(warpstone);
	}

	/**
	 * Returns true if event involves the server spawn Warpstone.
	 *
	 * @return true if it's the spawn Warpstone, otherwise false
	 */
	public boolean isSpawnWarpstone() {
		Warpstone spawn = WarpstoneManager.get().getSpawnWarpstone();
		return spawn != null ? spawn.equals(warpstone) : false;
	}

}