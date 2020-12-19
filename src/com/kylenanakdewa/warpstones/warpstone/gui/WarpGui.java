package com.kylenanakdewa.warpstones.warpstone.gui;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.warpstones.WarpstonesPlayerData;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;
import com.kylenanakdewa.warpstones.warpstone.WarpstoneManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The GUI that appears when a player activates a Warpstone.
 *
 * @author Kyle Nanakdewa
 */
public class WarpGui {

    /** The player that will see this GUI. */
    private final Player player;

    /** The Warpstone that this GUI is for. May be null. */
    private final Warpstone warpstone;

    /** The Pocket Warpstone item that this GUI is for. May be null. */
    private final ItemStack pocketWarpstone;

    /** Whether to show information about this Warpstone. */
    private boolean showWarpstoneInfo;

    /** Whether the player may warp home. */
    private boolean allowWarpHome;
    /** Whether the player may warp to their farthing hub. */
    private boolean allowWarpFarthingHub;
    /** Whether the player may warp to the server spawn. */
    private boolean allowWarpSpawn;
    /** Whether the player may warp to the server events hub. */
    private boolean allowWarpEvents;

    /** Whether the player may warp to recent Warpstones. */
    private boolean allowWarpRecent;

    public WarpGui(Player player, Warpstone warpstone) {
        this.player = player;
        this.warpstone = warpstone;
        pocketWarpstone = null;

        showWarpstoneInfo = true;

        allowWarpHome = true;
        allowWarpFarthingHub = false; // Disabled for Banmas
        allowWarpSpawn = false; // Disabled for Banmas
        allowWarpEvents = true;

        allowWarpRecent = true;
    }

    public WarpGui(Player player, ItemStack pocketWarpstone) {
        this.player = player;
        warpstone = null;
        this.pocketWarpstone = pocketWarpstone;

        showWarpstoneInfo = false;

        allowWarpHome = true;
        allowWarpFarthingHub = true;
        allowWarpSpawn = true;
        allowWarpEvents = true;

        allowWarpRecent = true;
    }

    /**
     * Displays this GUI to the player.
     *
     * @return the newly opened inventory GUI
     */
    public InventoryView display() {
        return player.openInventory(createInventory());
    }

    /**
     * Creates the inventory GUI.
     */
    private Inventory createInventory() {
        String title;
        if (pocketWarpstone != null) {
            title = "Pocket Warpstone";
        } else if (warpstone.getDisplayName() != null) {
            title = warpstone.getDisplayName();
        } else {
            title = "Warpstone";
        }

        // Prefix used to identify Warp GUI
        String titlePrefix = ChatColor.BLUE + ChatColor.GRAY.toString() + ChatColor.BLUE;

        Inventory inv = Bukkit.createInventory(player, 54, titlePrefix + title);

        // Info item
        if (showWarpstoneInfo && warpstone != null) {
            inv.setItem(13, getWarpstoneInfoItem());
        }

        // Home Bed item
        if (allowWarpHome) {
            inv.setItem(19, getHomeBedItem());
        }
        // Farthing Hub item
        if (allowWarpFarthingHub) {
            inv.setItem(20, getFarthingHubItem());
        }

        // Spawn item
        if (allowWarpSpawn) {
            inv.setItem(24, getSpawnItem());
        }
        // Events item
        if (allowWarpEvents) {
            inv.setItem(25, getEventsItem());
        }

        // Recent items
        if (allowWarpRecent) {
            ItemStack[] items = getRecentWarpstoneItems();
            if (items.length >= 1) {
                inv.setItem(39, items[0]);
            }
            if (items.length >= 2) {
                inv.setItem(40, items[1]);
            }
            if (items.length >= 3) {
                inv.setItem(41, items[2]);
            }
        }

        return inv;
    }

    /**
     * Gets an item with the specified material, name, and lore.
     */
    private ItemStack getItem(Material material, String name, List<String> lore) {
        // Item meta
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
        meta.setDisplayName(name);
        meta.setLore(lore);

        // Item stack
        ItemStack item = new ItemStack(material);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Gets the Warpstone Info item.
     */
    private ItemStack getWarpstoneInfoItem() {
        List<String> lore = new ArrayList<String>();

        // Item name
        String warpstoneName;
        if (warpstone.getDisplayName() != null) {
            warpstoneName = ChatColor.BLUE + warpstone.getDisplayName() + " Warpstone";
        } else {
            warpstoneName = ChatColor.BLUE + "Warpstone";
        }

        // Co-ords
        Location wsLoc = warpstone.getLocation();
        if (wsLoc != null) {
            String locationString = wsLoc.getBlockX() + " " + wsLoc.getBlockY() + " " + wsLoc.getBlockZ();
            lore.add(CommonColors.MESSAGE + locationString);
            // TODO story location name, continent, and protection zone
        }

        // Spawn Warpstone
        if (WarpstoneManager.get().getSpawnWarpstone().equals(warpstone)) {
            lore.add("");
            lore.add(CommonColors.MESSAGE + ChatColor.ITALIC.toString() + "This is the Spawn Warpstone.");
        }

        // Saving blocked
        if (warpstone.isSaveBlocked()) {
            lore.add("");
            lore.add(CommonColors.ERROR + ChatColor.ITALIC.toString() + "You cannot save at this Warpstone.");
        }

        // Admin info
        if (player.hasPermission("warpstones.manage")) {
            // Disabled
            if (warpstone.isDisabled()) {
                lore.add("");
                lore.add(CommonColors.INFO + ChatColor.ITALIC.toString() + "Warpstone is disabled.");
            }
            // Requires permission
            if (warpstone.requiresPermission()) {
                lore.add("");
                lore.add(CommonColors.INFO + ChatColor.ITALIC.toString() + "Warpstone requires permission to use.");
            }
            // Requires condition
            if (warpstone.getCondition() != null) {
                lore.add("");
                lore.add(CommonColors.INFO + ChatColor.ITALIC.toString() + "Warpstone has condition to use:");
                lore.add(CommonColors.INFO + ChatColor.ITALIC.toString() + warpstone.getCondition());
            }
            // Forced destination
            Location destLoc = warpstone.getForcedDestination();
            if (destLoc != null) {
                lore.add("");
                String locationString = destLoc.getBlockX() + " " + destLoc.getBlockY() + " " + destLoc.getBlockZ();
                lore.add(CommonColors.INFO + ChatColor.ITALIC.toString() + "Destination: " + locationString);
            }

            // Identifier
            lore.add("");
            lore.add(CommonColors.INFO + "ID: " + warpstone.getIdentifier());
        }

        return getItem(Material.PAPER, warpstoneName, lore);
    }

    /**
     * Gets the home bed item.
     */
    private ItemStack getHomeBedItem() {
        Material material = Material.PURPLE_BED; // TODO colour bed based on continent

        String name = ChatColor.BLUE + "Warp Home";

        List<String> lore = new ArrayList<String>();
        lore.add(CommonColors.MESSAGE + ChatColor.ITALIC.toString() + "Fast travel to your bed.");
        lore.add("");

        // Get bed location
        Location homeBed = player.getBedSpawnLocation();
        if (homeBed != null) {
            // Co-ords
            String locationString = homeBed.getBlockX() + " " + homeBed.getBlockY() + " " + homeBed.getBlockZ();
            lore.add(CommonColors.MESSAGE + locationString);
            // TODO Faeir Location and continent

            // TODO check continent restrictions
        }

        else {
            lore.add(CommonColors.ERROR + ChatColor.ITALIC.toString() + "You have no home bed or respawn anchor,");
            lore.add(CommonColors.ERROR + ChatColor.ITALIC.toString() + "or it was obstructed.");
        }

        return getItem(material, name, lore);
    }

    /**
     * Gets the Farthing Hub item.
     */
    private ItemStack getFarthingHubItem() {
        Material material = Material.PURPLE_BANNER; // TODO colour banner based on continent

        String name = ChatColor.BLUE + "Warp to Farthing Hub";

        List<String> lore = new ArrayList<String>();
        lore.add(CommonColors.MESSAGE + ChatColor.ITALIC.toString() + "Fast travel to your Farthing's spawn.");
        lore.add(CommonColors.MESSAGE + ChatColor.ITALIC.toString() + "Farthing market, and realm diplomacy.");
        lore.add("");

        // Get Farthing Hub Warpstone
        Warpstone warpstone = WarpstoneManager.get().getWarpstone("TODO");
        if (warpstone != null) {
            // Co-ords
            Location wsLoc = warpstone.getLocation();
            String locationString = wsLoc.getBlockX() + " " + wsLoc.getBlockY() + " " + wsLoc.getBlockZ();
            lore.add(CommonColors.MESSAGE + locationString);
            // TODO Faeir Location and continent

            // TODO check continent restrictions
        }

        else {
            lore.add(CommonColors.ERROR + ChatColor.ITALIC.toString() + "You haven't yet chosen a Farthing.");
            lore.add(CommonColors.INFO + ChatColor.ITALIC.toString() + "Enter the Tree of Harmony at spawn");
            lore.add(CommonColors.INFO + ChatColor.ITALIC.toString() + "to choose a Farthing.");
        }

        return getItem(material, name, lore);
    }

    /**
     * Gets the server spawn item.
     */
    private ItemStack getSpawnItem() {
        Material material = Material.OAK_SAPLING;

        String name = ChatColor.BLUE + "Warp to Spawn";

        List<String> lore = new ArrayList<String>();
        lore.add(CommonColors.MESSAGE + ChatColor.ITALIC.toString() + "Fast travel to the Aunix Sanctuary.");
        lore.add("");

        // Get Spawn Warpstone
        Warpstone warpstone = WarpstoneManager.get().getSpawnWarpstone();
        if (warpstone != null) {
            // Co-ords
            Location wsLoc = warpstone.getLocation();
            String locationString = wsLoc.getBlockX() + " " + wsLoc.getBlockY() + " " + wsLoc.getBlockZ();
            lore.add(CommonColors.MESSAGE + locationString);
            // TODO Faeir Location and continent
        }

        else {
            lore.add(CommonColors.ERROR + ChatColor.ITALIC.toString() + "Spawn is currently unavailable.");
        }

        return getItem(material, name, lore);
    }

    /**
     * Gets the events hub item.
     */
    private ItemStack getEventsItem() {
        // TODO load all this from an external file

        Material material = Material.SPRUCE_SAPLING; // Material.DIAMOND;

        String name = ChatColor.BLUE + "Warp to Banmas Festival";// "Warp to Events Hub";

        List<String> lore = new ArrayList<String>();
        // lore.add(CommonColors.MESSAGE + ChatColor.ITALIC.toString() + "Fast travel to
        // the Events Hub.");
        // BANMAS TEMPORARY
        lore.add(CommonColors.MESSAGE + ChatColor.ITALIC.toString() + "Fast travel to the Snow Globe.");
        lore.add(CommonColors.MESSAGE + ChatColor.ITALIC.toString() + "Spawn & Festival Events.");
        lore.add("");

        // Get event location
        Location eventLoc = WarpstoneManager.get().getSpawnWarpstone().getLocation();
        if (eventLoc != null) {
            // Co-ords
            String locationString = eventLoc.getBlockX() + " " + eventLoc.getBlockY() + " " + eventLoc.getBlockZ();
            lore.add(CommonColors.MESSAGE + locationString);
            // TODO Faeir Location and continent
        }

        else {
            lore.add(CommonColors.ERROR + ChatColor.ITALIC.toString() + "No event is currently active.");
        }

        return getItem(material, name, lore);
    }

    /**
     * Gets an item for a recent Warpstone.
     */
    private ItemStack getRecentWarpstoneItem(Warpstone warpstone, long time) {
        Material material = Material.LAPIS_LAZULI;

        // Item name
        String warpstoneName;
        if (warpstone != null && warpstone.getDisplayName() != null) {
            warpstoneName = ChatColor.BLUE + warpstone.getDisplayName();
        } else {
            warpstoneName = ChatColor.BLUE + "Recent Warpstone";
        }

        List<String> lore = new ArrayList<String>();

        if (warpstone != null) {
            lore.add(ChatColor.BLUE + ChatColor.MAGIC.toString() + warpstone.getIdentifier());
            lore.add(CommonColors.MESSAGE + ChatColor.ITALIC.toString() + "Return to your previous location.");

            // Time
            String timeString;
            Duration duration = Duration.between(Instant.ofEpochMilli(time), Instant.now());
            if (duration.toMinutes() < 60) {
                timeString = duration.toMinutes() + " minutes ago.";
            } else if (duration.toHours() < 48) {
                timeString = duration.toHours() + " hours ago.";
            } else {
                timeString = duration.toDays() + " days ago.";
            }
            lore.add(CommonColors.MESSAGE + ChatColor.ITALIC.toString() + timeString);

            lore.add("");

            // Co-ords
            Location wsLoc = warpstone.getLocation();
            String locationString = wsLoc.getBlockX() + " " + wsLoc.getBlockY() + " " + wsLoc.getBlockZ();
            lore.add(CommonColors.MESSAGE + locationString);
            // TODO Faeir Location and continent

            // TODO check continent restrictions
        }

        else {
            lore.add(CommonColors.ERROR + ChatColor.ITALIC.toString() + "Warpstone is currently unavailable.");
        }

        return getItem(material, warpstoneName, lore);
    }

    /**
     * Gets up to 3 recent Warpstone items.
     */
    private ItemStack[] getRecentWarpstoneItems() {
        ItemStack[] items = new ItemStack[3];

        WarpstonesPlayerData playerData = new WarpstonesPlayerData(player);
        /*
         * LinkedHashMap<Warpstone, Long> recentWarpstones =
         * playerData.getRecentThreeWarpstones(); Warpstone[] wsArray =
         * recentWarpstones.keySet().toArray(new Warpstone[0]);
         *
         * for (int i = 0; i < wsArray.length; i++) { Warpstone warpstone = wsArray[i];
         * long time = recentWarpstones.get(warpstone); items[i] =
         * getRecentWarpstoneItem(warpstone, time); }
         *
         * return items;
         */
        int warpstonesFound = 0;
        for (Entry<Warpstone, Long> entry : playerData.getRecentWarpstones(3, warpstone).entrySet()) {
            if (!entry.getKey().equals(warpstone)) {
                items[warpstonesFound] = getRecentWarpstoneItem(entry.getKey(), entry.getValue());
                warpstonesFound++;
            }
            if (warpstonesFound == 3) {
                break;
            }
        }
        return items;
    }

}