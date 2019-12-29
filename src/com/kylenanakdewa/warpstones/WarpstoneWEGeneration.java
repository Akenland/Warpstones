package com.kylenanakdewa.warpstones;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.entity.Player;

import com.kylenanakdewa.core.common.CommonColors;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;

final class WarpstoneWEGeneration {

    static final int generateWarpstone(Player player, int size, WarpstoneDesigns design){
        // Get the worldedit plugin
        WorldEdit we = WorldEdit.getInstance();

        // Get the location of the warpstone's center
        BlockVector3 loc = BukkitAdapter.asBlockVector(player.getLocation().subtract(0, 1, 0));

        // Make sure size is valid
        if(size<1 || size>3){
            player.sendMessage(CommonColors.ERROR+"Invalid size for generating warpstone.");
            return 0;
        }

        // Determine which warpstone design to use
        Clipboard schem = design.getSchematic(size);

        // Paste the warpstone schematic
        try(EditSession editSession = we.getEditSessionFactory().getEditSession(BukkitAdapter.adapt(player.getWorld()), 100, BukkitAdapter.adapt(player))){
            ClipboardHolder clipboardHolder = new ClipboardHolder(schem);

            // Randomly rotate the schematic
            clipboardHolder.setTransform(new AffineTransform().rotateY(Arrays.asList(0, 90, 180, 270).get(ThreadLocalRandom.current().nextInt(3))));

            Operation operation = clipboardHolder.createPaste(editSession).to(loc).ignoreAirBlocks(true).build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            player.sendMessage(CommonColors.ERROR+"Unable to generate warpstone, unknown error when pasting: " + e.getLocalizedMessage());
            return 0;
        }

        // Get the pattern of blocks to generate
        Pattern groundPattern;
        try {
            // Ground pattern is the block the player is standing on, plus coarse dirt and gravel
            groundPattern = we.getPatternFactory().parseFromInput("coarse_dirt,gravel,"+player.getLocation().subtract(0, 1, 0).getBlock().getType().toString(), new ParserContext());
        } catch(InputParseException e){
            player.sendMessage(CommonColors.ERROR+"Invalid block pattern to generate Warpstone: " + e.getLocalizedMessage());
            return 0;
        }

        // Dirt/gravel on the ground around the stone
        int blocksChanged = 0;
        try(EditSession editSession = we.getEditSessionFactory().getEditSession(BukkitAdapter.adapt(player.getWorld()), 100, BukkitAdapter.adapt(player))){
            blocksChanged += editSession.makeCylinder(loc, groundPattern, 2+size, 1, true);

            Operation operation = editSession.commit();
            Operations.complete(operation);
        } catch(WorldEditException e){
            player.sendMessage(CommonColors.ERROR+"Unable to generate warpstone, unknown error when generating ground cover: " + e.getLocalizedMessage());
            return 0;
        }

        return blocksChanged;
    }
}