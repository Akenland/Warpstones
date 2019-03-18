package com.kylenanakdewa.warpstones.quicksave;

import java.util.ArrayList;
import java.util.List;

import com.kylenanakdewa.core.characters.players.PlayerCharacter;
import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.savedata.PlayerSaveDataSection;
import com.kylenanakdewa.warpstones.WarpstonesPlugin;

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

        ConfigurationSection itemData = data.getConfigurationSection("quicksave.items");
        PlayerInventory inv = character.getPlayer().getPlayer().getInventory();

        // Create data section, if it doesn't exist
        if(itemData==null){
            itemData = data.createSection("quicksave.items");
        }

        // Clear existing save
        itemData.set("", null);

        // Add all items in the inventory
        for(int i=0; i<inv.getSize(); i++){
            itemData.set(""+i, inv.getItem(i).serialize());
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
        List<ItemStack> items = new ArrayList<ItemStack>();
        ConfigurationSection itemData = data.getConfigurationSection("quicksave.items");
        for(String itemKey : itemData.getKeys(false)){
            items.add(Integer.parseInt(itemKey), itemData.getItemStack(itemKey));
        }
        return items;
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