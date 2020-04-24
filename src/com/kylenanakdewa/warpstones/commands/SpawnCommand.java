package com.kylenanakdewa.warpstones.commands;

import java.util.Arrays;
import java.util.List;

import com.kylenanakdewa.core.common.Error;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.warpstones.WarpstonesPlayerData;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 * Command handler for Spawn command.
 *
 * @author Kyle Nanakdewa
 */
public final class SpawnCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permissions
        if (!sender.hasPermission("warpstones.spawn")) {
            return Error.NO_PERMISSION.displayChat(sender);
        }

        // Target player
        Player player = null;
        // Warping another player
        if (args.length == 1) {
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

        return new WarpstonesPlayerData(player).teleportSpawn(true);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // If sender can warp others, send player names
        if (args.length == 1 && sender.hasPermission("warpstones.warpothers")) {
            return null;
        }

        return Arrays.asList("");
    }

}