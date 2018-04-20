package com.kylenanakdewa.warpstones.items;

import java.util.concurrent.ThreadLocalRandom;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.warpstones.ConfigValues;
import com.kylenanakdewa.warpstones.WarpPlayer;
import com.kylenanakdewa.warpstones.Warpstone;
import com.kylenanakdewa.warpstones.events.WarpstoneActivateEvent;
import com.kylenanakdewa.warpstones.events.PlayerWarpEvent.WarpCause;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public final class ItemListener implements Listener {
    
    //// Make sure that warp shards can only be crafted from warp dust
	@EventHandler
	public void onWarpShardCraft(CraftItemEvent event){
		CraftingInventory inv = event.getInventory();
		// If the item being crafted is a warp shard
		if(inv.getResult().isSimilar(WarpItems.WARP_SHARD)){
			//Utils.notifyAdmins("A warp shard is being crafted.");

			// Make sure every item is a warp dust, if not, cancel the crafting
			for(ItemStack item : inv.getMatrix()){
				if(item!=null && !item.isSimilar(WarpItems.WARP_DUST)){
					inv.setResult(null);
					event.setCancelled(true);
					//Utils.notifyAdmins("Blocked warp shard crafting, incorrect items.");
				}
			}
		}
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void onWarpstoneActivation(WarpstoneActivateEvent event){
		if(event.isCancelled()) return;

		// If player is holding a warp shard, attempt to link it
		ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
		if(ConfigValues.warpShardsUsable && WarpItems.isWarpShard(itemInHand)){
			// If shard is linked, take the shard and warp the player
			if(WarpItems.isWarpShardLinked(itemInHand)){
				Warpstone dest = WarpItems.getLinkedShardWarpstone(itemInHand);

				// If the shard is linked to this warpstone, tell the player
				if(dest.equals(event.getWarpstone())){
					event.getPlayer().sendMessage(CommonColors.INFO+"Your warp shard is linked to this warpstone. Use it at any other warpstone to return here.");
					return;
				}

				Utils.sendActionBar(event.getPlayer(), "Warping to shard's stored destination...");
				itemInHand.setAmount(itemInHand.getAmount()-1);
				event.getPlayer().getEquipment().setItemInMainHand(itemInHand);

				new WarpPlayer(event.getPlayer()).warp(dest, false, WarpCause.SHARD);
				return;
			}

			// Otherwise, attempt to link it
			else {
				event.getPlayer().getEquipment().setItemInMainHand(WarpItems.getLinkedWarpShard(event.getWarpstone(), itemInHand));
				event.getPlayer().sendMessage(CommonColors.MESSAGE+"Your warp shard was linked to this warpstone. Use it at any other warpstone to return here.");
			}
		}


		// If this isn't the last warpstone they visited, get a random number to see if they get a warp dust
		if(!event.isLastWarpstone() && !event.isHomeWarpstone() && !event.isSpawnWarpstone() && ThreadLocalRandom.current().nextInt(100) > 100-ConfigValues.warpDustChance){
			event.getPlayer().getInventory().addItem(WarpItems.WARP_DUST);
		}
	}

}