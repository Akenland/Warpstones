package com.kylenanakdewa.warpstones.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Error;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.warpstones.WarpstonesPlayerData;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;
import com.kylenanakdewa.warpstones.warpstone.WarpstoneManager;
import com.kylenanakdewa.warpstones.warpstone.events.PlayerWarpEvent.WarpCause;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 * Command handler for warping to Warpstones.
 *
 * @author Kyle Nanakdewa
 */
public final class WarpCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check for at least one arg
        if (args.length == 0) {
            return Error.INVALID_ARGS.displayChat(sender);
        }

        // Destination Warpstone
        String wsIdentifier = args[0].toLowerCase();
        Warpstone warpstone = WarpstoneManager.get().getWarpstone(wsIdentifier);

        // Make sure Warpstone exists
        if (warpstone == null) {
            sender.sendMessage(CommonColors.ERROR + "Warpstone " + wsIdentifier + " not found.");
            return Error.INVALID_ARGS.displayActionBar(sender);
        }

        // Check permissions
        if (!sender.hasPermission("warpstones.warp." + wsIdentifier) && !sender.hasPermission("warpstones.warp.*")) {
            return Error.NO_PERMISSION.displayChat(sender);
        }

        // Target player
        Player player = null;
        // Warping another player
        if (args.length == 2) {
            if (!sender.hasPermission("warpstones.warpothers")) {
                return Error.NO_PERMISSION.displayChat(sender);
            }
            String playerName = args[1];
            player = Utils.getPlayer(playerName);
        }
        // Warping self
        else if (sender instanceof Player) {
            player = (Player) sender;
        }
        // Make sure player exists
        if (player == null) {
            return Error.PLAYER_NOT_FOUND.displayActionBar(sender);
        }

        return new WarpstonesPlayerData(player).warp(warpstone, WarpCause.COMMAND, true);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        // If sender has access to all Warpstones, send list of every identifier
        if (args.length == 1 && sender.hasPermission("warpstones.warp.*")) {
            List<String> completions = new ArrayList<String>();
            completions.addAll(WarpstoneManager.get().getAllWarpstoneIdentifiers());
            return completions;
        }

        // If sender can warp others, send player names
        if (args.length == 2 && sender.hasPermission("warpstones.warpothers")) {
            return null;
        }

        return Arrays.asList("");

    }

}