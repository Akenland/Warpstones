package com.KyleNecrowolf.Warpstones;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.KyleNecrowolf.RealmsCore.Common.Utils;

public final class EventListener implements Listener {
	

	//// Track player movement for teleport delays
	static HashMap<UUID, Boolean> hasPlayerMoved = new HashMap<UUID, Boolean>();
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		
		// If player is teleporting, check if they've moved
		if(hasPlayerMoved.containsKey(player.getUniqueId()) && !hasPlayerMoved.get(player.getUniqueId())){
			// If they moved more than 0.5 in X or Z axis, set the hashmap value to true
			double distanceSquaredMoved = event.getFrom().distanceSquared(event.getTo());
			if(distanceSquaredMoved>0.02 && !player.hasPermission("warpstones.tp.instant")){
				hasPlayerMoved.put(player.getUniqueId(), true);
				Utils.sendActionBar(player, Utils.errorText+"You cannot teleport while moving.");
			}
		}
	}
	
	
	//// Set command blocks for warpstone when player steps on them
	static int timesSetCmd;
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		// If this is a pressure plate, check if they're setting a command block
		if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.STONE_PLATE){
			Player player = event.getPlayer();
			if(WarpUtils.warpstoneCmdSet.containsKey(player.getName())){
				String warpName = WarpUtils.warpstoneCmdSet.get(player.getName());
				// Check that the player is actually on the plate
				if(player.getLocation().getBlock().getType() == Material.STONE_PLATE){
					// Set the command block
					Block cmdBlock = player.getLocation().subtract(0, 2, 0).getBlock();
					cmdBlock.setType(Material.COMMAND);
					BlockState cmdBlockState = cmdBlock.getState();

					// Delay
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getProvidingPlugin(EventListener.class), new Runnable() {
						@Override
						public void run(){
							// Update the command block after five seconds so the player has time to move
							((CommandBlock) cmdBlockState).setCommand("/warpstone enter "+warpName+" @p[r=2]");
							cmdBlockState.update(true);
						}
					}, 20 * 3);

					// If they've done this four times, automatically exit
					timesSetCmd++;
					Utils.sendActionBar(player, Utils.messageText+""+timesSetCmd+" warpstone command blocks set");
					if(timesSetCmd>=4){
						timesSetCmd = 0;
						WarpUtils.warpstoneCmdSet.remove(player.getName());
					}
				}
			}
		}
	}
	
}
