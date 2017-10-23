package com.KyleNecrowolf.Warpstones;

import org.bukkit.plugin.java.JavaPlugin;

import com.KyleNecrowolf.Warpstones.Items.ItemListener;
import com.KyleNecrowolf.Warpstones.Items.WarpItems;
import com.KyleNecrowolf.Warpstones.Teleports.TeleportCommands;

public final class Main extends JavaPlugin {	
	
	public static JavaPlugin plugin;

	//// Plugin Enabled
	@Override
	public void onEnable(){
		plugin = this;

		// Load default config
		ConfigValues.saveDefaultConfig();

		// Register commands
		this.getCommand("warpstones").setExecutor(new WarpstoneCommands());
		this.getCommand("tp").setExecutor(new TeleportCommands());
		this.getCommand("tphere").setExecutor(new TeleportCommands());

		// Register event listeners
		getServer().getPluginManager().registerEvents(new EventListener(), this);

		// Register recipes (and event listener)
		if(ConfigValues.warpShardsCraftable){
			getServer().addRecipe(WarpItems.getWarpShardRecipe());
			getServer().getPluginManager().registerEvents(new ItemListener(), this);
		}
	}
	

	//// Commands
	/*@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		String command = cmd.getName().toLowerCase();

		// Check permissions if command is from a player
		if(sender instanceof Player && !sender.hasPermission("warpstones."+command)){
			sender.sendMessage(Utils.errorText+"You can't use this Warpstones command! Ask an "+ChatColor.DARK_PURPLE+"Admin"+Utils.errorText+" for help.");
			return true;
		}

		
		switch(command){
		// Main command
		
		case "warpstones":
			
			if(args.length >= 1){
				
				// Check permissions if command is from a player
				if(sender instanceof Player && !sender.hasPermission("warpstones."+args[0])){
					sender.sendMessage(Utils.errorText+"You can't use this Warpstones command! Ask an "+ChatColor.DARK_PURPLE+"Admin"+Utils.errorText+" for help.");
					return true;
				}
				
				// Check first argument
				switch(args[0]){
				
				// Version
				case "version":
					sender.sendMessage(Utils.messageText+"Warpstones "+getDescription().getVersion()+" by Kyle Necrowolf");
					return true;
				
				
				// Go to warpstone
				case "to":
					// If second arg, go to that warpstone
					if(args.length==2 && sender instanceof Player){
						new Warpstone(args[1]).warpPlayer((Player) sender, true, false); return true;
					}
					// If third arg, send a player to that warpstone
					if(args.length==3){
						Player player = Utils.getPlayer(args[1]);
						if(player==null){Utils.sendActionBar(sender, Utils.errorText+"Player not found."); return false;}
						new Warpstone(args[2]).warpPlayer(player, false, false); return true;
					}
					Utils.sendActionBar(sender, Utils.errorText+"Incorrect arguments.");
					return false;
					
				// Warp home
				case "home":
					if(sender instanceof Player) new WarpPlayer((Player) sender).warpHome(true);
					return true;
				
				// Set home
				case "sethome":
					if(sender instanceof Player) new WarpPlayer((Player) sender).setNextHome();
					return true;
				
				// Warp to last warpstone
				case "last":
					if(sender instanceof Player) new WarpPlayer((Player) sender).warpLast(true);
					return true;
				
				
				// Create a new warpstone
				case "create":
					if(args.length==2 && sender instanceof Player){
						new Warpstone(args[1]).setLocation(((Player) sender).getLocation());
						sender.sendMessage(Utils.messageText+"Created "+args[1]+" warpstone.");
						return true;
					}
					return false;
				// Delete a warpstone
				case "remove":
					if(args.length==2){
						new Warpstone(args[1]).delete();
						sender.sendMessage(Utils.messageText+"Removed "+args[1]+" warpstone.");
						return true;
					}
					return false;
					
				// Set the command blocks for a warpstone
				case "setcmd":
					if(args.length==2 && sender instanceof Player){
						WarpUtils.warpstoneCmdSet.put(sender.getName(), args[1].toLowerCase());
						Utils.sendActionBar(sender, Utils.messageText+"Activate plates to set command blocks for "+args[1]+" warpstone");
						return true;
					}
					if(args.length==1 && sender instanceof Player){
						EventListener.timesSetCmd = 0;
						WarpUtils.warpstoneCmdSet.remove(sender.getName());
						Utils.sendActionBar(sender, Utils.messageText+"Command blocks will no longer be set");
						return true;
					}
					sender.sendMessage(Utils.errorText+"Invalid command."); return false;
					
					
				// Enter a warpstone
				case "enter":
					Player player = Bukkit.getPlayer(args[2]);
					if(player==null){sender.sendMessage(Utils.errorText+"Player not found."); return false;}
					new Warpstone(args[1]).enter(player);
					return true;
				
				
				// Args not recognized
				default:
					sender.sendMessage(Utils.errorText+"Unknown Warpstones command. Check your spelling or ask an admin for help.");
					return false;
				}
			}
			// If no args, warp to last
			if(sender instanceof Player) new WarpPlayer((Player) sender).warpLast(true);
			return true;
		
		
		// Warp home
		case "home":
			if(sender instanceof Player) new WarpPlayer((Player) sender).warpHome(true);
			return true;

		// Set home
		case "sethome":
			if(sender instanceof Player) new WarpPlayer((Player) sender).setNextHome();
			return true;
			
			
		// Warp to spawn
		case "spawn":
			if(args.length==0 && sender instanceof Player){new Warpstone("spawn").warpPlayer((Player) sender, true, false); return true;}
			if(args.length==1 && sender.hasPermission("warpstones.spawn.others")){
				Player player = Utils.getPlayer(args[0]);
				if(player==null){Utils.sendActionBar(sender, Utils.errorText+"Player not found."); return false;}
				new Warpstone("spawn").warpPlayer(player, false, false); return true;
			}
			return false;
		// Warp to mall
		case "mall":
			if(sender instanceof Player){new Warpstone("intwilmarket").warpPlayer((Player) sender, true, false); return true;}
		
		
		// Teleport to another player (or vanilla TP)
		case "tp":
			return Teleports.tpCommand(sender, args);
		// Teleport another player to sender
		case "tphere":
			return Teleports.tpHereCommand(sender, args);
		

		// Command not set up
		default:
			sender.sendMessage(Utils.errorText+"This command is not set up in Warpstones. Yell at Kyle if you want it fixed.");
			return false;
		}
	}*/
}
