package com.kylenanakdewa.warpstones.items.shards;

import java.util.ArrayList;
import java.util.List;

import com.kylenanakdewa.warpstones.items.LapisItem;
import com.kylenanakdewa.warpstones.items.dust.WarpDust;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;
import com.kylenanakdewa.warpstones.warpstone.WarpstoneManager;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

/**
 * Warp Shard custom item. Allows players to link and warp to Warpstones.
 *
 * @author Kyle Nanakdewa
 */
public class WarpShard extends LapisItem {

    public WarpShard() {
        super("Warp Shard");
    }

    @Override
    public boolean matchesItem(ItemStack item) {
        // Check exact match
        if (super.matchesItem(item)) {
            return true;
        }

        // Check that item material and name matches
        if (item != null && item.getType().equals(material) && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            return meta.hasDisplayName() && meta.getDisplayName().equals(itemName);
        }

        return false;
    }

    @Override
    public Recipe getRecipe(Plugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "recipes/warp_shard");

        // Recipe gives 2 shards
        ItemStack result = getNewItem();
        result.setAmount(2);

        ShapedRecipe recipe = new ShapedRecipe(key, result);

        // Ingredient must be exact match for Warp Dust - uses new Bukkit draft API
        @SuppressWarnings("deprecation")
        ExactChoice ingredient = new ExactChoice(new WarpDust().getNewItem());

        recipe.shape("DD", "DD");
        recipe.setIngredient('D', ingredient);

        return recipe;
    }

    /**
     * Checks if a Warp Shard is linked. If the item is not a Warp Shard, returns
     * false.
     */
    public boolean isLinked(ItemStack item) {
        // If item is not a Shard, return false
        if (!matchesItem(item)) {
            return false;
        }

        // Presence of lore text indicates that Shard is linked
        return item.getItemMeta().hasLore();
    }

    /**
     * Gets the target that a Warp Shard is linked to. If the item is not a linked
     * Warp Shard, returns null.
     */
    protected String getLinked(ItemStack item) {
        // If item is not a linked Shard, return null
        if (!isLinked(item)) {
            return null;
        }

        // Get first line of lore
        String target = item.getItemMeta().getLore().get(0);

        // Strip colours and formatting
        target = ChatColor.stripColor(target);

        return target;
    }

    /**
     * Gets the Warpstone that a Warp Shard is linked to. If the item is not a
     * linked Warp Shard, or the Warpstone does not exist, returns null.
     */
    public Warpstone getLinkedWarpstone(ItemStack item) {
        String target = getLinked(item);
        return WarpstoneManager.get().getWarpstone(target);
    }

    /**
     * Links a Warp Shard to the specified target. If the item is not an unlinked
     * Warp Shard, it will be returned as-is.
     */
    protected ItemStack link(ItemStack item, String target) {
        if (matchesItem(item) && !isLinked(item)) {
            ItemMeta meta = item.getItemMeta();

            // Add blue + magic format codes, to obscure target string
            String targetPrefix = ChatColor.BLUE + ChatColor.MAGIC.toString();
            target = targetPrefix + target;

            // Create variable-size array to store lore text
            List<String> lore = new ArrayList<String>();
            lore.add(target);

            // Set lore text on item
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Links a Warp Shard to the specified Warpstone. If the item is not an unlinked
     * Warp Shard, it will be returned as-is.
     */
    public ItemStack link(ItemStack item, Warpstone warpstone) {
        // Use Warpstone's unique ID as target string
        String target = warpstone.getIdentifier();

        // Set data on item
        item = link(item, target);

        // Add X Z co-ords
        String coords = warpstone.getLocation().getBlockX() + " " + warpstone.getLocation().getBlockZ();
        item = appendLore(item, ChatColor.GRAY + coords);

        // Add Warpstone display name
        String displayName = warpstone.getDisplayName();
        if (displayName != null) {
            item = appendLore(item, ChatColor.GRAY + displayName);
        }

        // TODO Add farthing, with colour

        return item;
    }

    /**
     * Appends a line of lore text to the specified item.
     * <p>
     * The item must already have existing lore text, in a variable-size list (not
     * from Arrays.asList(), which returns a fixed-size list).
     */
    protected static ItemStack appendLore(ItemStack item, String text) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        lore.add(text);

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

}