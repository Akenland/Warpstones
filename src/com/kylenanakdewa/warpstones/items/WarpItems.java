package com.kylenanakdewa.warpstones.items;

import com.kylenanakdewa.warpstones.Warpstone;
import com.kylenanakdewa.warpstones.WarpstonesPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
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
	public static ItemStack WARP_DUST = new ItemStack(getRenamedLapis("Warp Dust"));

	// Warp Shard
	public static ItemStack WARP_SHARD = new ItemStack(getRenamedLapis("Warp Shard"));

	// Warp Heart
	public static ItemStack WARP_HEART = new ItemStack(getRenamedLapis("Warp Heart"));
	
	// Warp Heart
	public static ItemStack CHARGED_WARP_SHARD = new ItemStack(getRenamedLapis("Charged Warp Shard"));


	//// Warp Shards
	// Get a Warp Shard that is linked to a warpstone. If an existing warp shard is specified, it is converted to a linked warp shard.
	public static ItemStack getLinkedWarpShard(Warpstone warpstone, ItemStack existingWarpShard){
		// Get a normal warp shard
		ItemStack linkedShard = existingWarpShard;

		// If it's a warp shard, add the warpstone name in the lore, hidden with magic text (&k), and the X and Z co-ords
		if(isWarpShard(linkedShard)){
			ItemMeta itemMeta = linkedShard.getItemMeta();
			List<String> loreText = new ArrayList<String>(Arrays.asList(
				ChatColor.BLUE.toString()+ChatColor.MAGIC.toString()+warpstone.getIdentifier(),
				ChatColor.GRAY.toString()+warpstone.getLocation().getBlockX()+" "+warpstone.getLocation().getBlockZ()
				));
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


	//// Warp Hearts
	// Check if an itemstack is a warp heart
	public static boolean isWarpHeart(ItemStack item){
		if(item==null || !item.getType().equals(Material.INK_SACK)) return false;
		return item.getItemMeta().getDisplayName().equals(ChatColor.BLUE+"Warp Heart");
	}

	/** Get the count on a heart. */
	public static int getWarpHeartCount(ItemStack warpHeart){
		// Check if it's a warp shard
		if(!isWarpHeart(warpHeart) || !warpHeart.getItemMeta().hasLore()) return 0;

		// Get the percentage charge in the lore text.
		return Integer.parseInt(ChatColor.stripColor(warpHeart.getItemMeta().getLore().get(0)).split("%")[0]);
	}

	/** Increment the counter on the heart. */
	public static ItemStack incrementWarpHeartCounter(ItemStack warpHeart){
		// Check if it's a warp shard
		if(!isWarpHeart(warpHeart)) return warpHeart;

		// Get the original count
		int count = getWarpHeartCount(warpHeart) + 1;
		if(count>100) return warpHeart;

		// Set the new lore line
		ItemMeta itemMeta = warpHeart.getItemMeta();
		List<String> loreText = new ArrayList<String>(Arrays.asList(ChatColor.GRAY.toString()+count+"% charged"));
		itemMeta.setLore(loreText);
		warpHeart.setItemMeta(itemMeta);

		return warpHeart;
	}


	//// Charged Warp Shards
	// Get a Charged Warp Shard that is linked to a location. If an existing warp shard is specified, it is converted to a linked warp shard.
	public static ItemStack getLinkedChargedWarpShard(Location location, ItemStack existingWarpShard){
		// Get a normal warp shard
		ItemStack linkedShard = existingWarpShard;

		// If it's a warp shard, add the warpstone name in the lore, hidden with magic text (&k), and the X and Z co-ords
		if(isWarpShard(linkedShard)){
			String locString = location.getBlockX()+" "+location.getBlockY()+" "+location.getBlockZ();
			ItemMeta itemMeta = linkedShard.getItemMeta();
			List<String> loreText = new ArrayList<String>(Arrays.asList(
				ChatColor.BLUE.toString()+ChatColor.MAGIC.toString()+location.getWorld()+" "+locString,
				ChatColor.GRAY.toString()+locString
				));
			itemMeta.setLore(loreText);
			linkedShard.setItemMeta(itemMeta);
		}

		return linkedShard;
	}
	public static ItemStack getLinkedChargedWarpShard(Location location){
		return getLinkedChargedWarpShard(location, new ItemStack(CHARGED_WARP_SHARD));
	}

	// Check if an itemstack is a warp shard, linked or not
	public static boolean isChargedWarpShard(ItemStack item){
		if(item==null || !item.getType().equals(Material.INK_SACK)) return false;
		return item.getItemMeta().getDisplayName().equals(ChatColor.BLUE+"Charged Warp Shard");
	}

	// Check if a shard is linked to a warpstone
	public static boolean isChargedWarpShardLinked(ItemStack warpShard){
		// Check if it's a warp shard
		if(!isChargedWarpShard(warpShard)) return false;

		// Check if it has lore text
		return warpShard.getItemMeta().hasLore();
	}

	// Get the location that a charged warp shard is linked to
	public static Location getLinkedChargedShardLocation(ItemStack warpShard){
		// If item is not a linked warp shard, return null
		if(!isChargedWarpShardLinked(warpShard)) return null;

		// Get the lore text, and strip format codes to get the location
		String[] coords = ChatColor.stripColor(warpShard.getItemMeta().getLore().get(0)).split(" ");
		World world = Bukkit.getWorld(coords[0]);
		if(world==null) return null;
		Location loc = new Location(world, Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));
		return loc;
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

	// Warp Heart
	public static final ShapedRecipe getWarpHeartRecipe(){
		// Prepare a shaped recipe with a key called warp_heart and a result of WARP_HEART
		ShapedRecipe warpHeartRecipe = new ShapedRecipe(new NamespacedKey(WarpstonesPlugin.plugin, "warp_heart"), WARP_HEART);

		// Set the ingredients to 2x2 warp dust
		warpHeartRecipe.shape("DSD","SES","DSD");
		warpHeartRecipe.setIngredient('D', new Dye(DyeColor.BLUE));
		warpHeartRecipe.setIngredient('S', new Dye(DyeColor.BLUE));
		warpHeartRecipe.setIngredient('E', Material.EYE_OF_ENDER);

		return warpHeartRecipe;
	}

	// Charged Warp Shard
	public static final ShapelessRecipe getChargedWarpShardRecipe(){
		// Prepare a shaped recipe with a key called warp_heart and a result of WARP_HEART
		ShapelessRecipe warpShardRecipe = new ShapelessRecipe(new NamespacedKey(WarpstonesPlugin.plugin, "charged_warp_shard"), CHARGED_WARP_SHARD);

		// Set the ingredients to warp heart + ender pearl
		warpShardRecipe.addIngredient(new Dye(DyeColor.BLUE));
		warpShardRecipe.addIngredient(Material.ENDER_PEARL);

		return warpShardRecipe;
	}
}