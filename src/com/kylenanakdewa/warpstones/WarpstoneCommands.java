package com.kylenanakdewa.warpstones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.rmi.CORBA.Util;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Error;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.core.common.prompts.Prompt;
import com.kylenanakdewa.warpstones.events.PlayerWarpEvent.WarpCause;
import com.kylenanakdewa.warpstones.items.WarpItems;

/**
 * Command handler for the Warpstones plugin.
 * @author Kyle Nanakdewa
 */
final class WarpstoneCommands implements TabExecutor {

    /** The plugin instance. */
    private final WarpstonesPlugin plugin;

    WarpstoneCommands(WarpstonesPlugin plugin){
        this.plugin = plugin;
    }


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // If a warping alias is used, re-run this command
        switch(label){
			case "spawn": case "home": case "last": case "sethome":
                // Turn the label into the first arg
                String[] newArgs = new String[args.length+1];
                newArgs[0] = label;
                System.arraycopy(args, 0, newArgs, 1, args.length);

                // Re-run the command with the label chnaged to an arg
                return onCommand(sender, command, "warp", newArgs);

            // Otherwise, proceed as normal
			default:
                break;
		}


        if(args.length==0){
            // Make sure sender has access to warp options
            if(!sender.hasPermission("warpstones.last") && !sender.hasPermission("warpstones.home") && !sender.hasPermission("warpstones.spawn"))
                return Error.NO_PERMISSION.displayChat(sender);
            
            // Show prompt with available warpstones commands
            Prompt prompt = new Prompt();
            prompt.addQuestion(CommonColors.INFO+"--- "+ConfigValues.color+"Warpstones"+CommonColors.INFO+" ---");
            
            if(sender.hasPermission("warpstones.last")) prompt.addAnswer("Warp to last warpstone", "command_warp last");
            if(sender.hasPermission("warpstones.home")) prompt.addAnswer("Warp home", "command_warp home");
            if(sender.hasPermission("warpstones.spawn")) prompt.addAnswer("Warp to spawn", "command_warp spawn");
            
            prompt.display(sender);
            return true;
        }


        // Spawn, home, and last command
        if(args[0].equalsIgnoreCase("spawn")){
            if(!sender.hasPermission("warpstones.spawn")) return Error.NO_PERMISSION.displayChat(sender);
            Player targetPlayer = getTargetPlayer(sender, args);
            if(targetPlayer==null) return Error.PLAYER_NOT_FOUND.displayActionBar(sender);

            return new WarpPlayer(targetPlayer).warpSpawn(true, WarpCause.COMMAND);
        }
        if(args[0].equalsIgnoreCase("home")){
            if(!sender.hasPermission("warpstones.home")) return Error.NO_PERMISSION.displayChat(sender);
            Player targetPlayer = getTargetPlayer(sender, args);
            if(targetPlayer==null) return Error.PLAYER_NOT_FOUND.displayActionBar(sender);

            return new WarpPlayer(targetPlayer).warpHome(true, WarpCause.COMMAND);
        }
        if(args[0].equalsIgnoreCase("last")){
            if(!sender.hasPermission("warpstones.last")) return Error.NO_PERMISSION.displayChat(sender);
            Player targetPlayer = getTargetPlayer(sender, args);
            if(targetPlayer==null) return Error.PLAYER_NOT_FOUND.displayActionBar(sender);

            return new WarpPlayer(targetPlayer).warpLast(true, WarpCause.COMMAND);
        }


        // Set home command
        if(args[0].equalsIgnoreCase("sethome") && sender instanceof Player){
            new WarpPlayer((Player) sender).setNextHome();
			return true;
        }


        // Version command
        if(args[0].equalsIgnoreCase("version")){
            sender.sendMessage(ConfigValues.color+"Warpstones "+plugin.getDescription().getVersion()+" by Kyle Nanakdewa");
            sender.sendMessage(CommonColors.MESSAGE+"- A uniquely immersive warping system, based around floating structures known as Warpstones.");
            sender.sendMessage(CommonColors.MESSAGE+"- Website: http://plugins.akenland.com/");
			return true;
        }

        // Reload command
        if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("core.admin")){
            plugin.reload();
            Utils.notifyAdmins(sender.getName()+" reloaded Warpstones");
            return true;
        }


        // To command (warping directly to any warpstone)
        if(args.length>=2 && args[0].equalsIgnoreCase("to")){
            if(sender.hasPermission("warpstones.to."+args[1].toLowerCase()) || sender.hasPermission("warpstones.to.*")){
                
                // Figure out who should be warped
                Player targetPlayer = null;
                if(args.length==3 && sender.hasPermission("warpstones.warpothers")) targetPlayer = Utils.getPlayer(args[2]);
                if(args.length==2 && sender instanceof Player) targetPlayer = (Player) sender;

                if(targetPlayer==null) return Error.PLAYER_NOT_FOUND.displayActionBar(sender);

                return new WarpPlayer(targetPlayer).warp(Warpstone.get(args[1]), true, WarpCause.COMMAND);
            }
            return Error.NO_PERMISSION.displayChat(sender);
        }


        //// Warpstone modification commands
        // Create a warpstone
        if(args.length==2 && args[0].equalsIgnoreCase("create")){
            if(!sender.hasPermission("warpstones.manage") || !(sender instanceof Player)) return Error.NO_PERMISSION.displayChat(sender);

            Player player = (Player) sender;

            // Make sure Warpstone doesn't already exist
            if(Warpstone.get(args[1])!=null){
                sender.sendMessage(CommonColors.ERROR+"Warpstone "+args[1]+" already exists.");
            }

            Warpstone.create(args[1], player.getLocation());
            sender.sendMessage(CommonColors.MESSAGE+"Warpstone "+args[1]+" created.");

            // Prompt the player to generate the warpstone
            onCommand(sender, command, label, new String[] {"generate", args[1]});

            return true;
        }

        // Remove a warpstone
        if(args.length==2 && args[0].equalsIgnoreCase("remove")){
            if(!sender.hasPermission("warpstones.manage")) return Error.NO_PERMISSION.displayChat(sender);

            Warpstone warpstone = Warpstone.get(args[1]);
            if(warpstone==null) return Error.INVALID_ARGS.displayActionBar(sender);
            warpstone.delete();
            sender.sendMessage(CommonColors.MESSAGE+"Warpstone "+args[1]+" removed from file.");

            return true;
        }

        // Generate a warpstone
        if(args.length>=2 && args[0].equalsIgnoreCase("generate")){
            if(!sender.hasPermission("warpstones.manage") || !(sender instanceof Player)) return Error.NO_PERMISSION.displayChat(sender);

            // If only 2 args, show prompt
            if(args.length==2){
                Prompt prompt = new Prompt();
                prompt.addQuestion("Stand at the center of where the warpstone should be, and choose an option:");
                prompt.addAnswer("Small (up to 5x7x5)", "command_warpstone generate "+args[1]+" 1");
                prompt.addAnswer("Medium (up to 7x9x7)", "command_warpstone generate "+args[1]+" 2");
                prompt.addAnswer("Large (up to 9x12x9)", "command_warpstone generate "+args[1]+" 3");
                prompt.addAnswer("Generate command blocks only", "command_warpstone setcmd "+args[1]);

                prompt.display(sender);
                return true;
            }

            // If 3 args, generate warpstone
            if(args.length>=3){
                Player player = (Player) sender;
                Warpstone warpstone = Warpstone.get(args[1]);
                if(warpstone==null) return Error.INVALID_ARGS.displayActionBar(sender);
                if(args.length==3){
                    warpstone.generateWarpstone(player, Integer.parseInt(args[2]));
                    return true;
                }
                if(args.length==4){
                    // Make sure they entered a valid warpstone design
                    for(WarpstoneDesigns design : WarpstoneDesigns.values()){
                        if(args[3].equalsIgnoreCase(design.name())){
                            warpstone.generateWarpstone(player, Integer.parseInt(args[2]), design);
                            return true;
                        }
                    }
                }
            }
        }
        // SetCmd - setting command blocks, mostly for legacy use
        if(args[0].equalsIgnoreCase("setcmd")){
            if(!sender.hasPermission("warpstones.manage") || !(sender instanceof Player)) return Error.NO_PERMISSION.displayChat(sender);

            if(args.length==2){
				WarpUtils.warpstoneCmdSet.put(sender.getName(), args[1].toLowerCase());
				Utils.sendActionBar(sender, CommonColors.MESSAGE+"Activate plates to set command blocks for "+args[1]+" warpstone");
				return true;
			}
			if(args.length==1){
				EventListener.timesSetCmd = 0;
				WarpUtils.warpstoneCmdSet.remove(sender.getName());
				Utils.sendActionBar(sender, CommonColors.MESSAGE+"Command blocks will no longer be set");
				return true;
			}
        }


        // Give command - used to give players warp items
        if(args[0].equalsIgnoreCase("give") && args.length>=2){
            if(!sender.hasPermission("warpstones.give") ||!(sender instanceof Player)) return Error.NO_PERMISSION.displayChat(sender);

            Player player = (Player) sender;

            // Figure out the requested item
            ItemStack item = null;
            switch(args[1]){
                case "warp_dust":
                    item = WarpItems.WARP_DUST;
                    break;
                case "warp_shard":
                    item = WarpItems.WARP_SHARD;
                    break;
                case "warp_shard_linked":
                    if(args.length==3) item = WarpItems.getLinkedWarpShard(Warpstone.get(args[2]));
                    break;
                default:
                    break;
            }
            
            if(item==null) return Error.INVALID_ARGS.displayActionBar(sender);

            player.getInventory().addItem(item);
            Utils.sendActionBar(player, "You received a "+item.getItemMeta().getDisplayName());
            return true;
        }


        // Enter command - used for command blocks triggered when a player enters a warpstone
        if(args[0].equalsIgnoreCase("enter") && sender.isOp()){
            Player player = Bukkit.getPlayer(args[2]);
            if(player==null) return Error.PLAYER_NOT_FOUND.displayChat(sender);

            Warpstone warpstone = Warpstone.get(args[1]);
            if(warpstone==null) return Error.INVALID_ARGS.displayActionBar(sender);

            warpstone.activate(player);
			return true;
        }


		return Error.INVALID_ARGS.displayActionBar(sender);
    }
    
    // Convienience method to get a target player
    private static Player getTargetPlayer(CommandSender sender, String[] args){
        if(args.length==2 && sender.hasPermission("warpstones.warpothers")){
            Player p = Utils.getPlayer(args[1]);
            if(p==null){
                Error.PLAYER_NOT_FOUND.displayActionBar(sender);
            }
            return p;
        }

        if(args.length==1 && sender instanceof Player){
            return (Player) sender;
        }

        return null;
    }


	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
	
        // Give command
        if(args.length>=2 && args.length<4 && args[0].equalsIgnoreCase("give")){
            List<String> completions = new ArrayList<String>();
            completions.addAll(Arrays.asList("warp_dust","warp_shard","warp_shard_linked"));

            return completions;
        }


		//// Main command
		if(args.length<=1){
            // Return the available warpstone commands, including extra ones that the sender has permission for
            List<String> completions = new ArrayList<String>();

            if(sender.hasPermission("warpstones.last")) completions.add("last");
            if(sender.hasPermission("warpstones.home")) completions.add("home");
            if(sender.hasPermission("warpstones.spawn")) completions.add("spawn");

            completions.addAll(Arrays.asList("to","version"));

            if(sender.hasPermission("warpstones.manage")) completions.addAll(Arrays.asList("create","remove","generate"));
            if(sender.hasPermission("warpstones.give")) completions.add("give");

            return completions;
        }

        
        return null;
    }

}