package com.KyleNecrowolf.Warpstones.Teleports;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.KyleNecrowolf.RealmsCore.Common.Error;
import com.KyleNecrowolf.RealmsCore.Common.Utils;

public final class TeleportCommands implements CommandExecutor {
    
    //// Commands
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        
        // Make sure they have permission
        if(!sender.hasPermission("warpstones.tp")) return Error.NO_PERMISSION.displayChat(sender);


        //// If no args and sender is a player, accept TP request
        if(args.length==0 && sender instanceof Player){
            Player player = (Player) sender;

            // Get the request
            TeleportRequest request = null;
            if(command.getName().equalsIgnoreCase("tp")) request = TeleportHereRequest.requests.get(player.getUniqueId());
            if(command.getName().equalsIgnoreCase("tphere")) request = TeleportRequest.requests.get(player.getUniqueId());

            // If request exists, complete the teleport
            if(request!=null){
                return request.doTeleport();
            } else {
                if(command.getName().equalsIgnoreCase("tp")) Utils.sendActionBar(sender, "Specify a player to teleport to");
                if(command.getName().equalsIgnoreCase("tphere")) Utils.sendActionBar(sender, "Specify a player to teleport to you");
                return false;
            }
        }


        //// If command is /tp, and more than one arg, let vanilla handle command
        if(args.length>1 && command.getName().equalsIgnoreCase("tp") && sender.hasPermission("warpstones.tp.vanilla")){
            // Build the string to send
		    StringBuilder vanillaCmd = new StringBuilder();
		    vanillaCmd.append("minecraft:tp");
		    for(String arg:args) {vanillaCmd.append(" ").append(arg);}
		    // Send the command
		    Utils.sendActionBar(sender, Utils.infoText+"Teleporting with "+vanillaCmd);
		    return Bukkit.dispatchCommand(sender, vanillaCmd.toString());
        }


        //// If at least one arg, start a TeleportRequest
        if(args.length>=1 && sender instanceof Player){
            Player player = (Player) sender;

            // If command is /tp, get a single player and start request
            if(command.getName().equalsIgnoreCase("tp")){
                Player targetPlayer = Utils.getPlayer(args[0]);
                if(targetPlayer!=null){
                    new TeleportRequest(player, targetPlayer);
                    return true;
                } else return Error.PLAYER_NOT_FOUND.displayActionBar(sender);
            }


            // If command is /tphere, start a request for every player
            if(command.getName().equalsIgnoreCase("tphere")){
                StringBuilder destPlayerNames = new StringBuilder();
                for(String arg : args){
                    // For each arg specified, if it's a valid player, add it to the list
				    Player targetPlayer = Utils.getPlayer(arg);
				    if(targetPlayer != null){
					    new TeleportHereRequest(player, targetPlayer);
					    destPlayerNames.append(targetPlayer.getDisplayName()).append(Utils.messageText+", ");
				    } else Error.PLAYER_NOT_FOUND.displayActionBar(sender);
                }

                if(destPlayerNames.length()>3){
                    // Remove the extra comma at the end of the playernames
				    destPlayerNames.delete(destPlayerNames.length()-2, destPlayerNames.length()-1);
				
                    Utils.sendActionBar(sender, Utils.messageText+"Teleporting "+destPlayerNames+"to you");
                    
                    return true;
                } else return Error.PLAYER_NOT_FOUND.displayActionBar(sender);
            }
        }

        return Error.INVALID_ARGS.displayChat(sender);
    }
}