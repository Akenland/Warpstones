package com.kylenanakdewa.warpstones.events;

import com.kylenanakdewa.warpstones.WarpPlayer;
import com.kylenanakdewa.warpstones.Warpstone;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

/**
 * Represents an event involving a Player and a Warpstone.
 * @author Kyle Nanakdewa
 */
public abstract class PlayerWarpstoneEvent extends PlayerEvent {

	/** The Warpstone involved in this event. */
	private final Warpstone warpstone;
	/** The player save data. */
	private final WarpPlayer playerData;

	/**
	 * Creates the event.
	 * @param player the player involved in the event
	 * @param warpstone the warpstone involved in the event
	 */
	protected PlayerWarpstoneEvent(Player player, Warpstone warpstone){
		super(player);
		this.warpstone = warpstone;
		playerData = new WarpPlayer(player);
	}

	/**
	 * Gets the Warpstone involved in this event.
	 * @return the Warpstone involved in this event
	 */
	public Warpstone getWarpstone(){
		return warpstone;
	}


	/**
	 * Returns true if event involves the player's home warpstone.
	 * @return true if it's the player's home warpstone, otherwise false
	 */
	public boolean isHomeWarpstone(){
		Warpstone home = playerData.getHome();
		return home!=null ? home.equals(warpstone) : false;
	}
	/**
	 * Returns true if event involves the player's last warpstone.
	 * @return true if it's the player's last warpstone, otherwise false
	 */
	public boolean isLastWarpstone(){
		Warpstone last = playerData.getLast();
		return last!=null ? last.equals(warpstone) : false;
	}
	/**
	 * Returns true if event involves the server spawn warpstone.
	 * @return true if it's the spawn warpstone, otherwise false
	 */
	public boolean isSpawnWarpstone(){
		Warpstone spawn = Warpstone.getSpawn();
		return spawn!=null ? spawn.equals(warpstone) : false;
	}

}