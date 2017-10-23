package com.KyleNecrowolf.Warpstones.Items;

import org.bukkit.event.EventHandler;
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
}