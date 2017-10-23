package com.KyleNecrowolf.Warpstones;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.KyleNecrowolf.RealmsCore.Common.Utils;
import com.KyleNecrowolf.RealmsCore.Player.PlayerData;

// Represents a player that can teleport and use warpstones
public class WarpPlayer extends PlayerData {
	
	//// Constructor
	public WarpPlayer(OfflinePlayer player){
		super(player);
	}


	//// TELEPORTATION
	// Teleport this player to a location, with an optional delay
	public boolean teleport(Location destination, boolean delay){
		// Make sure player is online
		if(!isOnline()){
			Utils.notifyAdminsError("Cannot teleport "+getName()+", they are offline.");
			return false;
		}

		Player player = (Player) getPlayer();

		// If delaying, calculate the delay
		int delayLength = 0;
		if(delay && !player.hasPermission("warpstones.tp.instant")){
			// If player health is at or above the threshold (default 20 health), reduced delay (default 2s), otherwise full delay (default 5s)
			delayLength = (player.getHealth() >= ConfigValues.tpReducedDelayHealth) ? ConfigValues.tpReducedDelay : ConfigValues.tpFullDelay;
		}

		// Start monitoring player's movement
		EventListener.hasPlayerMoved.put(player.getUniqueId(), false);

		// Schedule the teleport
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
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
	// Get player's home warpstone
	public Warpstone getHome(){
		String warpName = getFile().getString("warpstones.home");
		if(warpName == null) return null;
		else return new Warpstone(warpName);
	}
	// Set player's home warpstone
	public void setHome(Warpstone warpstone){
		save("warpstones.home", warpstone.getName());
	}
	
	// Get player's last warpstone
	public Warpstone getLast(){
		String warpName = getFile().getString("warpstones.last");
		if(warpName == null) return null;
		else return new Warpstone(warpName);
	}
	// Set player's last warpstone
	public void setLast(Warpstone warpstone){
		save("warpstones.last", warpstone.getName());
	}
	
	
	//// WARPING
	// Warp to a warpstone
	public boolean warp(Warpstone warpstone, boolean delay){		
		// Make sure player is online
		if(!isOnline()){
			Utils.notifyAdminsError("Cannot warp "+getName()+" to "+warpstone.getName()+", they are offline.");
			return false;
		}

		Player player = (Player) getPlayer();
		
		// If this warpstone doens't have a location, give an error and return
		if(warpstone.getLocation()==null){
			Utils.sendActionBar(player, Utils.errorText+"Warpstone not found");
			return false;
		}
		
		return teleport(warpstone.getLocation(), delay);
	}

	// Send player to home warp
	public boolean warpHome(boolean delay){
		// Make sure player is online
		if(!isOnline()){
			Utils.notifyAdminsError("Cannot warp "+getName()+" to home warpstone, they are offline.");
			return false;
		}

		Player player = (Player) getPlayer();
		Warpstone home = getHome();
		
		// If home warp does not exist, send them to spawn, otherwise send them home
		if(home == null){
			player.sendMessage(Utils.messageText+"You haven't yet chosen a home warpstone with "+ConfigValues.color+"/sethome"+Utils.messageText+". Warping to spawn instead.");
			return warpSpawn(delay);
		}

		Utils.sendActionBar(player, Utils.messageText+"Warping home...");
		return warp(home, delay);
	}
	// Send player to last warp
	public boolean warpLast(boolean delay){
		// Make sure player is online
		if(!isOnline()){
			Utils.notifyAdminsError("Cannot warp "+getName()+" to last warpstone, they are offline.");
			return false;
		}

		Player player = (Player) getPlayer();
		Warpstone last = getLast();
		
		// If last warpstone does not exist, show instructions to player
		if(last == null){
			Utils.sendActionBar(player, Utils.messageText+"Activate a warpstone to save its location, so you can return to it later");
			return false;
		}

		Utils.sendActionBar(player, Utils.messageText+"Warping to last warpstone...");
		return warp(last, delay);
	}
	// Send player to spawn
	public boolean warpSpawn(boolean delay){
		// Make sure player is online
		if(!isOnline()){
			Utils.notifyAdminsError("Cannot warp "+getName()+" to spawn, they are offline.");
			return false;
		}

		Player player = (Player) getPlayer();
		Warpstone spawn = ConfigValues.warpstoneSpawn;

		Utils.sendActionBar(player, Utils.messageText+"Warping to spawn...");
		return warp(spawn, delay);
	}

	
	// Set a player's home warp to the next warpstone they activate
	public void setNextHome(){
		// Make sure player is online
		if(!isOnline()) return;
		Player player = (Player)getPlayer();

		Utils.sendActionBar(player, Utils.messageText+"Activate a warpstone to set it as your home");
		WarpUtils.playersSettingHome.add(player.getName());
	}
}
