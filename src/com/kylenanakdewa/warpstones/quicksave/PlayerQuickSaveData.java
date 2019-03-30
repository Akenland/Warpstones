package com.kylenanakdewa.warpstones.quicksave;

import java.util.Arrays;
import java.util.List;

import com.kylenanakdewa.core.characters.players.PlayerCharacter;
import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.savedata.PlayerSaveDataSection;
import com.kylenanakdewa.warpstones.WarpstonesPlugin;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerQuickSaveData extends PlayerSaveDataSection {

    public PlayerQuickSaveData(PlayerCharacter player){
		super(player, WarpstonesPlugin.plugin);
	}
	public PlayerQuickSaveData(OfflinePlayer player){
		super(player, WarpstonesPlugin.plugin);
	}

    /**
     * Quicksaves this player's current inventory.
     */
    public void quickSave() {
        // If player is offline, do nothing
        if(!character.isOnline()) return;

        // Clear existing save
        data.set("quicksave.items", null);

        // Create data section
        ConfigurationSection itemData = data.createSection("quicksave.items");

        // Add all items in the inventory
        PlayerInventory inv = character.getPlayer().getPlayer().getInventory();
        for(int i=0; i<inv.getSize(); i++){
            ItemStack item = inv.getItem(i);
            if(item!=null && !item.getType().equals(Material.AIR)) itemData.set(""+i, item.serialize());
        }

        // Save the data to file
        save();

        character.getPlayer().getPlayer().sendMessage(CommonColors.INFO+"Your inventory has been quicksaved.");
    }

    /**
     * Gets the items in this player's quicksave.
     * The indexes should match the PlayerInventory slots.
     */
    public List<ItemStack> getSavedItems() {
        ConfigurationSection itemData = data.getConfigurationSection("quicksave.items");
        ItemStack[] items = new ItemStack[41];
        for(String itemKey : itemData.getKeys(false)){
            items[Integer.parseInt(itemKey)] = itemData.getItemStack(itemKey);
        }
        return Arrays.asList(items);
    }

    /**
     * Loads this player's saved inventory.
     * @param itemsToInclude If non-null, only includes items in collection. Use to exclude items lost after the quicksave, but before death (to avoid duplication).
     */
    /*public void quickLoad(Collection<ItemStack> itemsToInclude) {
        List<ItemStack> items = getSavedItems();

        items.removeIf(item -> item.)
    }*/
}