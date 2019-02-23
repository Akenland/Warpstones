package com.kylenanakdewa.warpstones.items;

import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.warpstones.ConfigValues;
import com.kylenanakdewa.warpstones.WarpPlayer;
import com.kylenanakdewa.warpstones.Warpstone;
import com.kylenanakdewa.warpstones.events.WarpstoneActivateEvent;
import com.kylenanakdewa.warpstones.events.PlayerWarpEvent.WarpCause;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public final class ItemListener implements Listener {

	//// Make sure that warp items can only be crafted from the appropriate item
	@EventHandler
	public void onWarpItemCraft(CraftItemEvent event) {
		CraftingInventory inv = event.getInventory();
		// If the item being crafted is a warp shard
		if (inv.getResult() != null && inv.getResult().isSimilar(WarpItems.WARP_SHARD)) {
			// Make sure every item is a warp dust, if not, cancel the crafting
			for (ItemStack item : inv.getMatrix()) {
				if (item != null && !item.isSimilar(WarpItems.WARP_DUST)) {
					inv.setResult(null);
					event.setCancelled(true);
				}
			}
		}
		// If the item being crafted is a warp heart
		if (inv.getResult() != null && inv.getResult().isSimilar(WarpItems.WARP_HEART)) {
			// Make sure every item is a warp dust, if not, cancel the crafting
			if (inv.getMatrix().length != 9
					|| (inv.getMatrix()[0] != null && !inv.getMatrix()[0].isSimilar(WarpItems.WARP_DUST))
					|| (inv.getMatrix()[1] != null && !inv.getMatrix()[1].isSimilar(WarpItems.WARP_SHARD))
					|| (inv.getMatrix()[2] != null && !inv.getMatrix()[2].isSimilar(WarpItems.WARP_DUST))
					|| (inv.getMatrix()[3] != null && !inv.getMatrix()[3].isSimilar(WarpItems.WARP_SHARD))
					|| (inv.getMatrix()[4] != null && !inv.getMatrix()[4].getType().equals(Material.EYE_OF_ENDER))
					|| (inv.getMatrix()[5] != null && !inv.getMatrix()[5].isSimilar(WarpItems.WARP_SHARD))
					|| (inv.getMatrix()[6] != null && !inv.getMatrix()[6].isSimilar(WarpItems.WARP_DUST))
					|| (inv.getMatrix()[7] != null && !inv.getMatrix()[7].isSimilar(WarpItems.WARP_SHARD))
					|| (inv.getMatrix()[8] != null && !inv.getMatrix()[8].isSimilar(WarpItems.WARP_DUST))) {
				inv.setResult(null);
				event.setCancelled(true);
			}
		}
		// If the item being crafted is a charged warp shard
		if (inv.getResult() != null && WarpItems.isChargedWarpShard(inv.getResult())) {
			// Make sure item is a warp heart, if not, cancel the crafting
			for (ItemStack item : inv.getMatrix()) {
				if (item!=null && item.getType().equals(Material.INK_SACK) && !WarpItems.isWarpHeart(item)) {
					inv.setResult(null);
					event.setCancelled(true);
				}
				// Use warp heart count to determine result amount
				else if (WarpItems.isWarpHeart(item)) {
					ItemStack newResult = inv.getResult();
					newResult.setAmount(WarpItems.getWarpHeartCount(item) / 10);
					inv.setResult(newResult);
				}
			}
			// If crafting was not cancelled, add the location
			if (inv.getResult() != null && !event.isCancelled()) {
				inv.setResult(WarpItems.getLinkedChargedWarpShard(event.getWhoClicked().getLocation(), inv.getResult()));
			}

		}
	}

	/**
	 * Gives a random amount of Warp Dust to the specified player.
	 */
	public static ItemStack getRandomWarpDust() {
		if (ThreadLocalRandom.current().nextInt(100) > 100 - ConfigValues.warpDustChance) {
			int count = ThreadLocalRandom.current().nextInt(5);
			ItemStack dust = new ItemStack(WarpItems.WARP_DUST);
			dust.setAmount(count);
			return dust;
		}
		return new ItemStack(Material.AIR);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWarpstoneActivation(WarpstoneActivateEvent event) {
		if (event.isCancelled())
			return;

		// If player is holding a warp shard, attempt to link it
		ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
		if (ConfigValues.warpShardsUsable && WarpItems.isWarpShard(itemInHand)) {
			event.setCancelled(true);

			// If shard is linked, take the shard and warp the player
			if (WarpItems.isWarpShardLinked(itemInHand)) {
				Warpstone dest = WarpItems.getLinkedShardWarpstone(itemInHand);

				// If the shard is linked to this warpstone, tell the player
				if (dest.equals(event.getWarpstone())) {
					event.getPlayer().sendMessage(CommonColors.INFO
							+ "Your warp shard is linked to this warpstone. Use it at any other warpstone to return here.");
					return;
				}

				Utils.sendActionBar(event.getPlayer(), "Warping to shard's stored destination...");

				// Take shard, if unbreaking chance is too low
				if ((itemInHand.getEnchantmentLevel(Enchantment.DURABILITY) * 2) < ThreadLocalRandom.current()
						.nextInt(1, 10)) {
					itemInHand.setAmount(itemInHand.getAmount() - 1);

					// If shard breaks, drop some dust
					if (itemInHand.getAmount() == 0)
						event.getPlayer().getInventory().addItem(getRandomWarpDust());
				}
				event.getPlayer().getEquipment().setItemInMainHand(itemInHand);

				new WarpPlayer(event.getPlayer()).warp(dest, false, WarpCause.SHARD);
				return;
			}

			// Otherwise, attempt to link it
			else {
				event.getPlayer().getEquipment()
						.setItemInMainHand(WarpItems.getLinkedWarpShard(event.getWarpstone(), itemInHand));
				event.getPlayer().sendMessage(CommonColors.MESSAGE
						+ "Your warp shard was linked to this warpstone. Use it at any other warpstone to return here.");

				// Give some warp dust
				event.getPlayer().getInventory().addItem(getRandomWarpDust());
			}
		}

		else if(ConfigValues.warpShardsUsable && WarpItems.isChargedWarpShard(itemInHand)){
			event.setCancelled(true);

			// If shard is linked, take the shard and warp the player
			if(WarpItems.isChargedWarpShardLinked(itemInHand)){
				Location location = WarpItems.getLinkedChargedShardLocation(itemInHand);
				if(location==null){
					Utils.sendActionBar(event.getPlayer(), CommonColors.ERROR+"Invalid destination. Ask an admin for help.");
				}

				Utils.sendActionBar(event.getPlayer(), "Warping to shard's stored destination...");

				// Take shard, if unbreaking chance is too low
				if ((itemInHand.getEnchantmentLevel(Enchantment.DURABILITY) * 2) < ThreadLocalRandom.current()
						.nextInt(1, 10)) {
					itemInHand.setAmount(itemInHand.getAmount() - 1);

					// If shard breaks, drop some dust
					if (itemInHand.getAmount() == 0)
						event.getPlayer().getInventory().addItem(getRandomWarpDust());
				}
				event.getPlayer().getEquipment().setItemInMainHand(itemInHand);

				new WarpPlayer(event.getPlayer()).teleport(location, false);
				return;
			}
		}

		// If this isn't the last warpstone they visited, get a random number to see if they get a warp dust
		else if (!event.isLastWarpstone() && !event.isHomeWarpstone() && !event.isSpawnWarpstone()) {
			ItemStack dust = getRandomWarpDust();
			dust.setAmount(dust.getAmount() * 2);
			event.getPlayer().getInventory().addItem(dust);

			// Increment warp heart, if they have any
			for(Entry<Integer,? extends ItemStack> item : event.getPlayer().getInventory().all(Material.INK_SACK).entrySet()){
				if(WarpItems.isWarpHeart(item.getValue())){
					event.getPlayer().getInventory().setItem(item.getKey(), WarpItems.incrementWarpHeartCounter(item.getValue()));
				}
			}
		}
	}

	@EventHandler
	public void onLapisOreBreak(BlockBreakEvent event){
		// If player is breaking lapis ore, event drops items, item used does not have silk touch, and player not in creative
		if(event.getBlock().getType().equals(Material.LAPIS_ORE) && event.isDropItems() && !event.getPlayer().getEquipment().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)){
			ItemStack dust = getRandomWarpDust();
			dust.setAmount(dust.getAmount()*2);

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
		if(WarpItems.isWarpShard(target) && book!=null && book.getType().equals(Material.ENCHANTED_BOOK)){
			target = new ItemStack(target);

			// Unbreaking
			int unbreakingLvl = book.getEnchantmentLevel(Enchantment.DURABILITY);
			if(unbreakingLvl>0) target.addUnsafeEnchantment(Enchantment.DURABILITY, unbreakingLvl);
			// Vanishing
			if(book.containsEnchantment(Enchantment.VANISHING_CURSE)) target.addEnchantment(Enchantment.VANISHING_CURSE, 1);

			event.getInventory().setRepairCost(5*target.getEnchantmentLevel(Enchantment.DURABILITY) + 5*target.getEnchantmentLevel(Enchantment.VANISHING_CURSE));
			event.setResult(target);
		}
	}


	/**
	 * Allow warp shards to be enchanted.
	 */
	@EventHandler
	public void onPrepareWarpShardEnchant(PrepareItemEnchantEvent event){
		ItemStack target = event.getItem();
		if(WarpItems.isWarpShard(target)){
			for(int i=0; i<event.getOffers().length; i++){
				if(ThreadLocalRandom.current().nextBoolean()){
					int level = ThreadLocalRandom.current().nextInt(event.getEnchantmentBonus()*2);
					event.getOffers()[i] = new EnchantmentOffer(Enchantment.DURABILITY, Math.max(level/3, 10), level+5);
				} else {
					event.getOffers()[i] = new EnchantmentOffer(Enchantment.VANISHING_CURSE, 1, ThreadLocalRandom.current().nextInt(event.getEnchantmentBonus()*2+5));
				}
			}
			event.setCancelled(false);
		}
	}
	@EventHandler
	public void onWarpShardEnchant(EnchantItemEvent event){
		if(WarpItems.isWarpShard(event.getItem())){
			// Chance to reduce Unbreaking level
			if(event.getEnchantsToAdd().containsKey(Enchantment.DURABILITY) && ThreadLocalRandom.current().nextBoolean()){
				int newLvl = Math.min(1, event.getEnchantsToAdd().get(Enchantment.DURABILITY) - ThreadLocalRandom.current().nextInt(10));
				event.getEnchantsToAdd().put(Enchantment.DURABILITY, newLvl);
			}

			event.setCancelled(false);
		}
	}

}