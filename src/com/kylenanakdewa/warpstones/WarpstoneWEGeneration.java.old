package com.kylenanakdewa.warpstones;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.kylenanakdewa.core.common.Utils;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.patterns.Pattern;

@SuppressWarnings("deprecation")
final class WarpstoneWEGeneration {

    static final int generateWarpstone(Player player, int size, WarpstoneDesigns design){
        // Get the worldedit plugin
        WorldEditPlugin wePlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        WorldEdit we = wePlugin.getWorldEdit();

        // Get the player's WE edit session
        EditSession editSession = wePlugin.createEditSession(player);

        // Get the location of the warpstone's center
        //Vector loc = BukkitUtil.toVector(player.getLocation().add(0, 3+size, 0));
        Vector loc = BukkitUtil.toVector(player.getLocation().subtract(0, 1, 0));

        // Make sure size is valid
        if(size<1 || size>3){
            player.sendMessage(Utils.errorText+"Invalid size for generating warpstone.");
            return 0;
        }

        // Determine which warpstone design to use
        CuboidClipboard schem = design.getSchematic(size);
        
        // Randomly rotate the schematic
        schem.rotate2D(Arrays.asList(0, 90, 180, 270).get(ThreadLocalRandom.current().nextInt(3)));

        // Get the pattern of blocks to generate        
        //Pattern stonePattern;
        Pattern groundPattern;
        try {
            //stonePattern = we.getBlockPattern(wePlugin.wrapPlayer(player), "40%stone,24%stainedglasspane:3,12%lapisore,12%air,12%stainedglass:3");
            // Ground pattern is the block the player is standing on, plus coarse dirt and gravel
            groundPattern = we.getBlockPattern(wePlugin.wrapPlayer(player), "dirt:1,gravel,"+player.getLocation().subtract(0, 1, 0).getBlock().getType().toString());
        } catch(WorldEditException e){
            player.sendMessage(Utils.errorText+"Invalid block pattern to generate Warpstone. Contact the admin.");
            return 0;
        }

        // Make the warpstone
        int blocksChanged = 0;
        try {

            // Make the stone itself
            //blocksChanged += editSession.makeSphere(loc, stonePattern, size, 2+size, size, true);
            schem.paste(editSession, loc, true);

            // Dirt/gravel on the ground around the stone
            blocksChanged += editSession.makeCylinder(loc, groundPattern, 2+size, 1, true);

        } catch(MaxChangedBlocksException e){
            player.sendMessage(Utils.errorText+"Unable to generate warpstone, too many blocks changed.");
            return 0;
        }

        editSession.redo(editSession);
        
        return blocksChanged;
    }
}