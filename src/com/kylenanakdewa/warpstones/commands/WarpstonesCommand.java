package com.kylenanakdewa.warpstones.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Error;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.core.common.prompts.Prompt;
import com.kylenanakdewa.warpstones.WarpstonesPlugin;
import com.kylenanakdewa.warpstones.items.dust.WarpDust;
import com.kylenanakdewa.warpstones.items.heart.WarpHeart;
import com.kylenanakdewa.warpstones.items.shards.WarpShard;
import com.kylenanakdewa.warpstones.items.shards.charged.ChargedWarpShard;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;
import com.kylenanakdewa.warpstones.warpstone.WarpstoneDesign;
import com.kylenanakdewa.warpstones.warpstone.WarpstoneManager;

/**
 * Command handler for the Warpstones plugin. Basic plugin commands, as well as
 * managing Warpstones on the server.
 *
 * @author Kyle Nanakdewa
 */
public final class WarpstonesCommand implements TabExecutor {

    /** The Warpstones plugin instance. */
    private final WarpstonesPlugin plugin;

    public WarpstonesCommand(WarpstonesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Version command
        if (args.length == 0 || args[0].equalsIgnoreCase("version")) {
            sender.sendMessage(
                    ChatColor.BLUE + "Warpstones " + plugin.getDescription().getVersion() + " by Kyle Nanakdewa");
            sender.sendMessage(CommonColors.MESSAGE
                    + "- A uniquely immersive warping system, based around floating structures known as Warpstones.");
            sender.sendMessage(CommonColors.MESSAGE + "- Website: https://plugins.akenland.com/");
            return true;
        }

        // Reload command
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("core.admin")) {
                return Error.NO_PERMISSION.displayChat(sender);
            }
            plugin.reload();
            Utils.notifyAdmins(sender.getName() + CommonColors.MESSAGE + " reloaded Warpstones.");
            return true;
        }

        // Create a warpstone
        if (args.length >= 2 && args[0].equalsIgnoreCase("create")) {
            if (!sender.hasPermission("warpstones.manage") || !(sender instanceof Player)) {
                return Error.NO_PERMISSION.displayChat(sender);
            }

            Player player = (Player) sender;
            String wsIdentifier = args[1].toLowerCase();

            // Make sure Warpstone doesn't already exist
            if (WarpstoneManager.get().getWarpstone(wsIdentifier) != null) {
                sender.sendMessage(CommonColors.ERROR + "Warpstone " + wsIdentifier + " already exists.");
                return Error.INVALID_ARGS.displayActionBar(sender);
            }

            // Create the Warpstone
            WarpstoneManager.get().createWarpstone(wsIdentifier, player.getLocation());
            sender.sendMessage(CommonColors.MESSAGE + "Warpstone " + wsIdentifier + " created.");

            // Prompt the player to generate the warpstone
            if(args.length ==2){
                onCommand(sender, command, label, new String[] { "generate", wsIdentifier });
            } else if (args.length >= 3) {
                onCommand(sender, command, label, new String[] { "generate", wsIdentifier, args[2] });
            }

            // Allow display name to be set
            if(args.length >=4) {
                // Merge all remaining args into a single string
                List<String> lastArgs = new ArrayList<String>(Arrays.asList(args));
                // Change "create" to "name", remove size
                lastArgs.set(0, "name");
                lastArgs.remove(2);

                onCommand(sender, command, label, lastArgs.toArray(new String[lastArgs.size()]));
            }

            return true;
        }

        // Delete a warpstone
        if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            if (!sender.hasPermission("warpstones.manage")) {
                return Error.NO_PERMISSION.displayChat(sender);
            }

            String wsIdentifier = args[1].toLowerCase();
            Warpstone warpstone = WarpstoneManager.get().getWarpstone(wsIdentifier);

            // Make sure Warpstone exists
            if (warpstone == null) {
                sender.sendMessage(CommonColors.ERROR + "Warpstone " + wsIdentifier + " not found.");
                return Error.INVALID_ARGS.displayActionBar(sender);
            }

            // Delete the Warpstone
            WarpstoneManager.get().deleteWarpstone(warpstone);
            sender.sendMessage(CommonColors.MESSAGE + "Warpstone " + wsIdentifier + " deleted.");

            return true;
        }

        // Generate a warpstone
        if (args.length >= 2 && args[0].equalsIgnoreCase("generate")) {
            if (!sender.hasPermission("warpstones.manage")) {
                return Error.NO_PERMISSION.displayChat(sender);
            }

            String wsIdentifier = args[1];
            Warpstone warpstone = WarpstoneManager.get().getWarpstone(wsIdentifier);

            // Make sure Warpstone exists
            if (warpstone == null) {
                sender.sendMessage(CommonColors.ERROR + "Warpstone " + wsIdentifier + " not found.");
                return Error.INVALID_ARGS.displayActionBar(sender);
            }

            // If only 2 args, show prompt
            if (args.length == 2) {
                Prompt prompt = new Prompt();
                prompt.addQuestion("Choose a size for the Warpstone:");
                prompt.addAnswer("Small (up to 5x7x5)", "command_warpstones generate " + wsIdentifier + " 1");
                prompt.addAnswer("Medium (up to 7x9x7)", "command_warpstones generate " + wsIdentifier + " 2");
                prompt.addAnswer("Large (up to 9x12x9)", "command_warpstones generate " + wsIdentifier + " 3");

                prompt.display(sender);
                return true;
            }

            // If 3 args, generate Warpstone, using default biome design
            if (args.length == 3) {
                int size = Integer.parseInt(args[2]);
                warpstone.generateWarpstoneStructure(size);
                sender.sendMessage(CommonColors.MESSAGE + "Warpstone generated.");
                return true;
            }

            // If 4 args, generate Warpstone, using provided design
            if (args.length == 4) {
                int size = Integer.parseInt(args[2]);
                String designName = args[3].toUpperCase();
                WarpstoneDesign design = null;

                // Make sure they entered a valid warpstone design
                for (WarpstoneDesign testDesign : WarpstoneDesign.values()) {
                    if (testDesign.name().equals(designName)) {
                        design = testDesign;
                    }
                }
                if (design == null) {
                    sender.sendMessage(CommonColors.ERROR + "Warpstone design not found.");
                    return Error.INVALID_ARGS.displayActionBar(sender);
                }

                warpstone.generateWarpstoneStructure(design, size);
                sender.sendMessage(CommonColors.MESSAGE + "Warpstone generated.");
                return true;
            }
        }

        // Rename command
        if (args.length >= 2 && args[0].equalsIgnoreCase("name")) {
            if (!sender.hasPermission("warpstones.manage")) {
                return Error.NO_PERMISSION.displayChat(sender);
            }

            String wsIdentifier = args[1];
            Warpstone warpstone = WarpstoneManager.get().getWarpstone(wsIdentifier);

            // Make sure Warpstone exists
            if (warpstone == null) {
                sender.sendMessage(CommonColors.ERROR + "Warpstone " + wsIdentifier + " not found.");
                return Error.INVALID_ARGS.displayActionBar(sender);
            }

            // If only 2 args, clear the display name
            if (args.length == 2) {
                warpstone.setDisplayName(null);
                sender.sendMessage(
                        CommonColors.MESSAGE + "Cleared display name for Warpstone " + warpstone.getIdentifier());
                return true;
            }

            // If 3+ args, set new display name
            else if (args.length >= 3) {
                // Merge all remaining args into a single string
                List<String> lastArgs = new ArrayList<String>(Arrays.asList(args));
                lastArgs.subList(0, 2).clear();

                String name = String.join(" ", lastArgs);
                name = ChatColor.translateAlternateColorCodes('&', name);

                warpstone.setDisplayName(name);
                sender.sendMessage(CommonColors.MESSAGE + "Display name for Warpstone " + warpstone.getIdentifier()
                        + " set to " + name);
                return true;
            }
        }

        // Give command - used to give players warp items
        if (args.length >= 2 && args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("warpstones.give") || !(sender instanceof Player)) {
                return Error.NO_PERMISSION.displayChat(sender);
            }

            Player player = (Player) sender;
            String itemName = args[1];

            // Figure out the requested item
            ItemStack item = null;
            switch (itemName) {
                case "warp_dust":
                    item = new WarpDust().getNewItem();
                    break;

                case "warp_shard":
                    item = new WarpShard().getNewItem();
                    break;

                case "warp_shard_linked":
                    if (args.length == 3) {
                        String wsIdentifier = args[2];
                        Warpstone warpstone = WarpstoneManager.get().getWarpstone(wsIdentifier);

                        // Get a Shard and link it to specified Warpstone
                        WarpShard shard = new WarpShard();
                        item = shard.getNewItem();
                        item = shard.link(item, warpstone);
                    }
                    break;

                case "warp_heart":
                    item = new WarpHeart().getNewItem();
                    break;

                case "charged_warp_shard":
                    // Get a Charged Shard and link it to player's location
                    ChargedWarpShard shard = new ChargedWarpShard();
                    item = shard.getNewItem();
                    item = shard.link(item, player.getLocation());
                    break;

                default:
                    break;
            }

            if (item == null) {
                return Error.ITEM_NOT_FOUND.displayActionBar(sender);
            }

            player.getInventory().addItem(item);
            Utils.sendActionBar(player, "You received a " + item.getItemMeta().getDisplayName());
            return true;
        }

        return Error.INVALID_ARGS.displayActionBar(sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        // Main command
        if (args.length == 1) {
            // Return the available warpstone commands, including extra ones that the sender
            // has permission for
            List<String> completions = new ArrayList<String>();

            completions.add("version");

            if (sender.hasPermission("core.admin")) {
                completions.add("reload");
            }
            if (sender.hasPermission("warpstones.manage")) {
                completions.addAll(Arrays.asList("create", "delete", "generate", "name"));
            }
            if (sender.hasPermission("warpstones.give")) {
                completions.add("give");
            }

            return completions;
        }

        // Warpstone management commands - Warpstone names
        if (args.length == 2 && Arrays.asList("delete", "generate", "name").contains(args[0].toLowerCase())) {
            List<String> completions = new ArrayList<String>();
            completions.addAll(WarpstoneManager.get().getAllWarpstoneIdentifiers());
            return completions;
        }

        // Generate command - sizes
        if (args.length == 3 && args[0].equalsIgnoreCase("generate")) {
            return Arrays.asList("1", "2", "3");
        }

        // Generate command - biome designs
        if (args.length == 4 && args[0].equalsIgnoreCase("generate")) {
            List<String> completions = new ArrayList<String>();
            for (WarpstoneDesign design : WarpstoneDesign.values()) {
                completions.add(design.name());
            }
            return completions;
        }

        // Give command - item names
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return Arrays.asList("warp_dust", "warp_shard", "warp_shard_linked", "warp_heart", "charged_warp_shard");
        }

        // Give command - Linked Warp Shard - Warpstone names
        if (args.length == 3 && args[0].equalsIgnoreCase("give") && args[1].equalsIgnoreCase("warp_shard_linked")) {
            List<String> completions = new ArrayList<String>();
            completions.addAll(WarpstoneManager.get().getAllWarpstoneIdentifiers());
            return completions;
        }

        return Arrays.asList("");
    }

}