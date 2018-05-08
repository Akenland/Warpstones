package com.kylenanakdewa.warpstones.items;

import com.kylenanakdewa.warpstones.Warpstone;
import com.kylenanakdewa.warpstones.WarpstonesPlugin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

public final class WarpItems {

	// Getting the renamed lapis item
	private static ItemStack getRenamedLapis(String name){
		// Get a blue dye
		ItemStack item = new ItemStack(Material.INK_SACK, 1, (short) 4);

		// Rename it
		ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(Material.INK_SACK);
		itemMeta.setDisplayName(ChatColor.BLUE+name);
		item.setItemMeta(itemMeta);

		return item;
	}

	//// Items
	// Warp Dust
	public static ItemStack WARP_DUST = getRenamedLapis("Warp Dust");

	// Warp Shard
	public static ItemStack WARP_SHARD = getRenamedLapis("Warp Shard");


	//// Warp Shards
	// Get a Warp Shard that is linked to a warpstone. If an existing warp shard is specified, it is converted to a linked warp shard.
	public static ItemStack getLinkedWarpShard(Warpstone warpstone, ItemStack existingWarpShard){
		// Get a normal warp shard
		ItemStack linkedShard = existingWarpShard;

		// If it's a warp shard, add the warpstone name in the lore, hidden with magic text (&k), and the X and Z co-ords
		if(linkedShard.isSimilar(WARP_SHARD)){
			ItemMeta itemMeta = linkedShard.getItemMeta();
			List<String> loreText = Arrays.asList(
				ChatColor.BLUE.toString()+ChatColor.MAGIC.toString()+warpstone.getIdentifier(),
				ChatColor.GRAY.toString()+warpstone.getLocation().getBlockX()+" "+warpstone.getLocation().getBlockZ()
				);
			if(warpstone.getDisplayName()!=null) loreText.add(ChatColor.GRAY+warpstone.getDisplayName());
			itemMeta.setLore(loreText);
			linkedShard.setItemMeta(itemMeta);
		}

		return linkedShard;
	}
	public static ItemStack getLinkedWarpShard(Warpstone warpstone){
		return getLinkedWarpShard(warpstone, new ItemStack(WARP_SHARD));
	}

	// Check if an itemstack is a warp shard, linked or not
	public static boolean isWarpShard(ItemStack item){
		if(item==null || !item.getType().equals(Material.INK_SACK)) return false;
		return item.getItemMeta().getDisplayName().equals(ChatColor.BLUE+"Warp Shard");
	}

	// Check if a shard is linked to a warpstone
	public static boolean isWarpShardLinked(ItemStack warpShard){
		// Check if it's a warp shard
		if(!isWarpShard(warpShard)) return false;

		// Check if it has lore text
		return warpShard.getItemMeta().hasLore();
	}

	// Get the warpstone that a warp shard is linked to
	public static Warpstone getLinkedShardWarpstone(ItemStack warpShard){
		// If item is not a linked warp shard, return null
		if(!isWarpShardLinked(warpShard)) return null;

		// Get the lore text, and strip format codes to get the warpstone name
		String warpstoneName = ChatColor.stripColor(warpShard.getItemMeta().getLore().get(0));

		// Return the warpstone
		return Warpstone.get(warpstoneName);
	}


	//// Recipes
	// Warp Shard
	public static final ShapedRecipe getWarpShardRecipe(){
		// Prepare a shaped recipe with a key called warp_shard and a result of WARP_SHARD
		ShapedRecipe warpShardRecipe = new ShapedRecipe(new NamespacedKey(WarpstonesPlugin.plugin, "warp_shard"), WARP_SHARD);

		// Set the ingredients to 2x2 warp dust
		warpShardRecipe.shape("DD","DD");
		warpShardRecipe.setIngredient('D', new Dye(DyeColor.BLUE));

		return warpShardRecipe;
	}
}