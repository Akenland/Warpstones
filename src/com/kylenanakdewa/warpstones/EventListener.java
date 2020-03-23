package com.kylenanakdewa.warpstones;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.kylenanakdewa.core.characters.players.PlayerCharacter;
import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.core.common.prompts.Prompt;
import com.kylenanakdewa.core.common.prompts.PromptActionEvent;
import com.kylenanakdewa.warpstones.events.PlayerWarpEvent.WarpCause;

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
				Utils.sendActionBar(player, CommonColors.ERROR+"You cannot teleport while moving.");
			}
		}
	}


	//// Set command blocks for warpstone when player steps on them
	static int timesSetCmd;
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		// If this is a pressure plate, check if they're setting a command block
		if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.STONE_PRESSURE_PLATE){
			Player player = event.getPlayer();
			if(WarpUtils.warpstoneCmdSet.containsKey(player.getName())){
				String warpName = WarpUtils.warpstoneCmdSet.get(player.getName());
				// Check that the player is actually on the plate
				if(player.getLocation().getBlock().getType() == Material.STONE_PRESSURE_PLATE){
					// Set the command block
					Block cmdBlock = player.getLocation().subtract(0, 2, 0).getBlock();
					cmdBlock.setType(Material.COMMAND_BLOCK);
					BlockState cmdBlockState = cmdBlock.getState();

					// Delay
					Bukkit.getScheduler().scheduleSyncDelayedTask(WarpstonesPlugin.getProvidingPlugin(EventListener.class), new Runnable() {
						@Override
						public void run(){
							// Update the command block after five seconds so the player has time to move
							((CommandBlock) cmdBlockState).setCommand("/warpstone enter "+warpName+" @p[r=2]");
							cmdBlockState.update(true);
						}
					}, 20 * 3);

					// If they've done this four times, automatically exit
					timesSetCmd++;
					Utils.sendActionBar(player, CommonColors.MESSAGE+""+timesSetCmd+" warpstone command blocks set");
					if(timesSetCmd>=4){
						timesSetCmd = 0;
						WarpUtils.warpstoneCmdSet.remove(player.getName());
					}
				}
			}
		}
	}


	// Prompt actions
	@EventHandler
	public void onPrompt(PromptActionEvent event){
		if(event.isType("warp")){
			WarpPlayer player = new WarpPlayer(event.getPlayer());
			switch(event.getAction()){
				case "home":
					player.warpHome(true, WarpCause.WARPSTONE);
					break;
				case "last":
					player.warpLast(true, WarpCause.WARPSTONE);
					break;
				case "spawn":
					player.warpSpawn(true, WarpCause.WARPSTONE);
					break;
				default:
					player.warp(Warpstone.get(event.getAction()), true, WarpCause.WARPSTONE);
					break;
			}
		}
	}


	// Death handler
	@EventHandler
	public void showLocOnDeath(PlayerDeathEvent event){
		PlayerCharacter character = PlayerCharacter.getCharacter(event.getEntity());

		Location loc = event.getEntity().getLocation();
		String locString = loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ();

		Warpstone nearestWarp = Warpstone.getNearest(loc, 100, false);
		String nearestWarpString = "";
		if(nearestWarp!=null){
			nearestWarpString += " (Nearest Warpstone: ";
			if(nearestWarp.getDisplayName()!=null) nearestWarpString += nearestWarp.getDisplayName()+ ", ";
			nearestWarpString +=  Math.round(nearestWarp.getLocation().distance(loc)) + "m away)";
		}

		// Prompt shown to player
		Prompt deathPrompt = new Prompt();
		deathPrompt.addQuestion(CommonColors.INFO+"You died at "+locString+nearestWarpString);

		if(event.getEntity().hasPermission("warpstones.death.teleport")){
			deathPrompt.addAnswer("Teleport to death location", "command_tp "+locString);
			if(nearestWarp!=null) deathPrompt.addAnswer("Warp to nearest warpstone", "command_warp to "+nearestWarp.getIdentifier());
		}

		deathPrompt.display(event.getEntity());

		// Message shown to realm members
		if(character.getRealm()!=null){
			Collection<Player> realmMembers = character.getRealm().getOnlinePlayers();
			realmMembers.remove(event.getEntity());
			for(Player member : realmMembers){
				member.sendMessage(character.getName()+CommonColors.INFO+" died at "+locString+nearestWarpString);
			}
		}
	}
}
