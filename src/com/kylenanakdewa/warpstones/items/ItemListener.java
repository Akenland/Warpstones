package com.kylenanakdewa.warpstones.items;

import java.util.concurrent.ThreadLocalRandom;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.warpstones.ConfigValues;
import com.kylenanakdewa.warpstones.WarpPlayer;
import com.kylenanakdewa.warpstones.Warpstone;
import com.kylenanakdewa.warpstones.events.WarpstoneActivateEvent;
import com.kylenanakdewa.warpstones.events.PlayerWarpEvent.WarpCause;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
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


	/**
	 * Gives a random amount of Warp Dust to the specified player.
	 */
	public static ItemStack getRandomWarpDust(){
		if(ThreadLocalRandom.current().nextInt(100) > 100-ConfigValues.warpDustChance){
			int count = ThreadLocalRandom.current().nextInt(6);
			ItemStack dust = new ItemStack(WarpItems.WARP_DUST);
			dust.setAmount(count);
			return dust;
		}
		return new ItemStack(Material.AIR);
	}


	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWarpstoneActivation(WarpstoneActivateEvent event){
		if(event.isCancelled()) return;

		// If player is holding a warp shard, attempt to link it
		ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
		if(ConfigValues.warpShardsUsable && WarpItems.isWarpShard(itemInHand)){
			event.setCancelled(true);

			// If shard is linked, take the shard and warp the player
			if(WarpItems.isWarpShardLinked(itemInHand)){
				Warpstone dest = WarpItems.getLinkedShardWarpstone(itemInHand);

				// If the shard is linked to this warpstone, tell the player
				if(dest.equals(event.getWarpstone())){
					event.getPlayer().sendMessage(CommonColors.INFO+"Your warp shard is linked to this warpstone. Use it at any other warpstone to return here.");
					return;
				}

				Utils.sendActionBar(event.getPlayer(), "Warping to shard's stored destination...");

				// Take shard, if unbreaking chance is too low
				if(itemInHand.getEnchantmentLevel(Enchantment.DURABILITY)<ThreadLocalRandom.current().nextInt(1,10)){
					itemInHand.setAmount(itemInHand.getAmount()-1);
				}
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
		if(!event.isLastWarpstone() && !event.isHomeWarpstone() && !event.isSpawnWarpstone()){
			event.getPlayer().getInventory().addItem(getRandomWarpDust());
		}
	}

	@EventHandler
	public void onLapisOreBreak(BlockBreakEvent event){
		// If player is breaking lapis ore, event drops items, and item used does not have silk touch
		if(event.getBlock().getType().equals(Material.LAPIS_ORE) && event.isDropItems() && !event.getPlayer().getEquipment().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)){
			ItemStack dust = getRandomWarpDust();

			// Fortune boost
			if(event.getPlayer().getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) > (ThreadLocalRandom.current().nextInt(10)+1)){
				dust.setAmount(dust.getAmount()*2);
			}

			// getRandomWarpDust() can return air, and Bukkit doesn't allow air to be dropped
			if(!dust.getType().equals(Material.AIR)){
				event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), dust);
			}
		}
	}


	/**
	 * Fix warp shards when they're modified at an anvil.
	 */
	@EventHandler
	public void onWarpShardAnvil(PrepareAnvilEvent event){
		// If slot 0 is shard, and slot 1 is enchanted book
		ItemStack target = event.getInventory().getItem(0);
		ItemStack book = event.getInventory().getItem(1);
		if(WarpItems.isWarpShard(target) && book.getType().equals(Material.ENCHANTED_BOOK)){
			target.addEnchantment(Enchantment.DURABILITY, book.getEnchantmentLevel(Enchantment.DURABILITY));
			target.addEnchantment(Enchantment.VANISHING_CURSE, book.getEnchantmentLevel(Enchantment.VANISHING_CURSE));
			event.getInventory().setRepairCost(5*target.getEnchantmentLevel(Enchantment.DURABILITY) + 5*target.getEnchantmentLevel(Enchantment.VANISHING_CURSE));
			event.setResult(target);
		}
	}


	/**
	 * Allow warp shards to be enchanted.
	 */
	@EventHandler
	public void onWarpShardEnchant(PrepareItemEnchantEvent event){
		ItemStack target = event.getItem();
		if(WarpItems.isWarpShard(target)){
			for(EnchantmentOffer offer : event.getOffers()){
				if(ThreadLocalRandom.current().nextBoolean()){
					offer.setEnchantment(Enchantment.DURABILITY);
					int level = ThreadLocalRandom.current().nextInt(event.getEnchantmentBonus()*2);
					offer.setEnchantmentLevel(level/3);
					offer.setCost(level);
				} else {
					offer.setEnchantment(Enchantment.VANISHING_CURSE);
					offer.setEnchantmentLevel(1);
					offer.setCost(ThreadLocalRandom.current().nextInt(event.getEnchantmentBonus()*2));
				}
			}
			event.setCancelled(false);
		}
	}

}