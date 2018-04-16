package com.kylenanakdewa.warpstones;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.core.common.savedata.PlayerSaveDataSection;
import com.kylenanakdewa.warpstones.events.PlayerWarpEvent;
import com.kylenanakdewa.warpstones.events.PlayerWarpEvent.WarpCause;
import com.kylenanakdewa.core.characters.players.PlayerCharacter;

/**
 * Represents a player that can teleport and use warpstones.
 * @author Kyle Nanakdewa
 */
public class WarpPlayer extends PlayerSaveDataSection {


	public WarpPlayer(PlayerCharacter player){
		super(player, WarpstonesPlugin.plugin);
	}
	public WarpPlayer(OfflinePlayer player){
		super(player, WarpstonesPlugin.plugin);
	}


	//// TELEPORTATION
	// Teleport this player to a location, with an optional delay
	public boolean teleport(Location destination, boolean delay){
		// Make sure player is online
		if(!character.isOnline()){
			Utils.notifyAdminsError("Cannot teleport "+character.getName()+", they are offline.");
			return false;
		}

		Player player = (Player)character.getPlayer();

		// If delaying, calculate the delay
		int delayLength = 0;
		if(delay && !player.hasPermission("warpstones.tp.instant")){
			// If player health is at or above the threshold (default 20 health), reduced delay (default 2s), otherwise full delay (default 5s)
			delayLength = (player.getHealth() >= ConfigValues.tpReducedDelayHealth) ? ConfigValues.tpReducedDelay : ConfigValues.tpFullDelay;
		}

		// Start monitoring player's movement
		EventListener.hasPlayerMoved.put(player.getUniqueId(), false);

		// Schedule the teleport
		Bukkit.getScheduler().scheduleSyncDelayedTask(WarpstonesPlugin.plugin, () -> {
			// If player has not moved, teleport them, and then stop monitoring their movement
			if(!EventListener.hasPlayerMoved.get(player.getUniqueId())){
				// If player has permission warpstones.tp.silent, do not play effects
				if(!player.hasPermission("warpstones.tp.silent")){
					WarpUtils.playTPEffects(player.getLocation());
					WarpUtils.playTPEffects(destination);
				}
				// Complete the teleport
				player.teleport(destination);
			}

			// Stop monitoring their movement
			EventListener.hasPlayerMoved.remove(player.getUniqueId());

		}, 20 * delayLength);

		return true;
	}


	//// HOME / LAST WARPSTONES
	/**
	 * Gets the player's home Warpstone.
	 * @return the player's home warpstone, or null if it does not exist
	 */
	public Warpstone getHome(){
		return Warpstone.get(getString("home"));
	}
	/**
	 * Sets the player's home Warpstone.
	 * @param warpstone the new home warpstone, or null to clear
	 */
	public void setHome(Warpstone warpstone){
		set("home", warpstone.getIdentifier());
		save();
	}

	/**
	 * Gets the player's last Warpstone.
	 * @return the player's last warpstone, or null if it does not exist
	 */
	public Warpstone getLast(){
		return Warpstone.get(getString("last"));
	}
	/**
	 * Sets the player's last Warpstone.
	 * @param warpstone the new last warpstone, or null to clear
	 */
	public void setLast(Warpstone warpstone){
		set("last", warpstone.getIdentifier());
		save();
	}


	//// WARPING
	// Warp to a warpstone
	public boolean warp(Warpstone warpstone, boolean delay, WarpCause cause){
		// Make sure player is online
		if(!character.isOnline()){
			Utils.notifyAdminsError("Cannot warp "+character.getName()+" to "+warpstone.getIdentifier()+", they are offline.");
			return false;
		}

		Player player = (Player)character.getPlayer();

		// If this warpstone doens't have a location, give an error and return
		if(warpstone.getLocation()==null){
			Utils.sendActionBar(player, CommonColors.ERROR+"Warpstone not found");
			return false;
		}

		PlayerWarpEvent event = new PlayerWarpEvent(player, warpstone, cause);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if(event.isCancelled()) return false;

		return teleport(warpstone.getLocation(), delay);
	}

	// Send player to home warp
	public boolean warpHome(boolean delay, WarpCause cause){
		// Make sure player is online
		if(!character.isOnline()){
			Utils.notifyAdminsError("Cannot warp "+character.getName()+" to home warpstone, they are offline.");
			return false;
		}

		Player player = (Player) character.getPlayer();
		Warpstone home = getHome();

		// If home warp does not exist, send them to spawn, otherwise send them home
		if(home == null){
			player.sendMessage(CommonColors.MESSAGE+"You haven't yet chosen a home warpstone with "+ConfigValues.color+"/sethome"+CommonColors.MESSAGE+". Warping to spawn instead.");
			return warpSpawn(delay, cause);
		}

		Utils.sendActionBar(player, CommonColors.MESSAGE+"Warping home...");
		return warp(home, delay, cause);
	}
	// Send player to last warp
	public boolean warpLast(boolean delay, WarpCause cause){
		// Make sure player is online
		if(!character.isOnline()){
			Utils.notifyAdminsError("Cannot warp "+character.getName()+" to last warpstone, they are offline.");
			return false;
		}

		Player player = (Player)character.getPlayer();
		Warpstone last = getLast();

		// If last warpstone does not exist, show instructions to player
		if(last == null){
			Utils.sendActionBar(player, CommonColors.MESSAGE+"Activate a warpstone to save its location, so you can return to it later");
			return false;
		}

		Utils.sendActionBar(player, CommonColors.MESSAGE+"Warping to last warpstone...");
		return warp(last, delay, cause);
	}
	// Send player to spawn
	public boolean warpSpawn(boolean delay, WarpCause cause){
		// Make sure player is online
		if(!character.isOnline()){
			Utils.notifyAdminsError("Cannot warp "+character.getName()+" to spawn, they are offline.");
			return false;
		}

		Player player = (Player)character.getPlayer();
		Warpstone spawn = ConfigValues.warpstoneSpawn;

		Utils.sendActionBar(player, CommonColors.MESSAGE+"Warping to spawn...");
		return warp(spawn, delay, cause);
	}


	// Set a player's home warp to the next warpstone they activate
	public void setNextHome(){
		// Make sure player is online
		if(!character.isOnline()) return;
		Player player = (Player)character.getPlayer();

		Utils.sendActionBar(player, CommonColors.MESSAGE+"Activate a warpstone to set it as your home");
		WarpUtils.playersSettingHome.add(player.getName());
	}
}
