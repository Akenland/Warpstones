package com.kylenanakdewa.warpstones.items.dust;

import com.kylenanakdewa.warpstones.warpstone.events.WarpstoneActivateEvent;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Gives Warp Dust to a player when they activate a Warpstone or break Lapis
 * Lazuli Ore.
 *
 * @author Kyle Nanakdewa
 */
public class WarpDustListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWarpstoneActivate(WarpstoneActivateEvent event) {
        if (!event.isCancelled() && !event.isMostRecentWarpstone() && !event.isSpawnWarpstone()) {
            ItemStack dust = new WarpDust().getRandomWarpDust(40, 8);

            // Make sure air isn't dropped
            if (!dust.getType().equals(Material.AIR)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), dust);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLapisOreBreak(BlockBreakEvent event) {
        // If player is breaking lapis ore, event drops items, item used does not have
        // silk touch, and player not in creative
        if (!event.isCancelled() && event.getBlock().getType().equals(Material.LAPIS_ORE) && event.isDropItems()
                && !event.getPlayer().getEquipment().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)
                && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {

            // Fortune boost
            ItemStack pickaxe = event.getPlayer().getEquipment().getItemInMainHand();
            int fortuneLevel = pickaxe.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

            ItemStack dust = new WarpDust().getRandomWarpDust(40, 8 + (fortuneLevel * 2));

            // Make sure air isn't dropped
            if (!dust.getType().equals(Material.AIR)) {
                event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), dust);
            }
        }
    }

}