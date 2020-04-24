package com.kylenanakdewa.warpstones.warpstone;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Biome;

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

/**
 * Handles Warpstone structure generation, using WorldEdit.
 *
 * @author Kyle Nanakdewa
 */
class WarpstoneWEGeneration {

    /**
     * Generates the specified Warpstone's structure.
     *
     * @param warpstone the Warpstone to generate
     * @param size      the size of the Warpstone, 1/2/3
     * @throws IllegalArgumentException if size is not 1/2/3
     */
    public static void generateWarpstone(Warpstone warpstone, int size) {
        generateStone(warpstone, size);
        generateGroundPattern(warpstone, size);
    }

    /**
     * Generates the specified Warpstone's structure.
     *
     * @param warpstone the Warpstone to generate
     * @param design    the biome design to use
     * @param size      the size of the Warpstone, 1/2/3
     * @throws IllegalArgumentException if size is not 1/2/3
     */
    public static void generateWarpstone(Warpstone warpstone, WarpstoneDesign design, int size) {
        generateStone(warpstone.getLocation(), design, size);
        generateGroundPattern(warpstone, size);
    }

    /**
     * Generates a Warpstone structure. The location and design will be determined
     * from the Warpstone's data.
     *
     * @param warpstone the Warpstone to generate
     * @param size      the size of the Warpstone, 1/2/3
     * @throws IllegalArgumentException if size is not 1/2/3
     */
    private static void generateStone(Warpstone warpstone, int size) {
        Location location = warpstone.getLocation();
        WarpstoneDesign design = getBiomeDesign(location);

        generateStone(location, design, size);
    }

    /**
     * Generates a Warpstone structure.
     *
     * @param location the centerpoint of the Warpstone, immediately below it
     * @param design   the biome design to use
     * @param size     the size of the Warpstone, 1/2/3
     * @throws IllegalArgumentException if size is not 1/2/3
     */
    private static void generateStone(Location location, WarpstoneDesign design, int size) {
        // Make sure size is valid
        if (size < 1 || size > 3) {
            throw new IllegalArgumentException("Unable to generate Warpstone structure, invalid size: " + size);
        }

        // Get the location of the warpstone's center
        BlockVector3 loc = BukkitAdapter.asBlockVector(location.subtract(0, 1, 0));

        // Determine which warpstone design to use
        Clipboard schem = design.getSchematic(size);

        // Paste the warpstone schematic
        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory()
                .getEditSession(BukkitAdapter.adapt(location.getWorld()), 100)) {
            ClipboardHolder clipboardHolder = new ClipboardHolder(schem);

            // Randomly rotate the schematic
            clipboardHolder.setTransform(
                    new AffineTransform().rotateY(Arrays.asList(0, 90, 180, 270).get(new Random().nextInt(3))));

            Operation operation = clipboardHolder.createPaste(editSession).to(loc).ignoreAirBlocks(true).build();
            Operations.complete(operation);
        }

        catch (WorldEditException e) {
            WorldEdit.logger.warn(
                    "Unable to generate Warpstone structure, unknown error when pasting: " + e.getLocalizedMessage());
        }
    }

    /**
     * Gets the appropriate Warpstone biome design for the specified location.
     *
     * @param location the location to use for determining the biome design
     */
    private static WarpstoneDesign getBiomeDesign(Location location) {
        WarpstoneDesign design = WarpstoneDesign.DEFAULT;

        double temperature = location.getBlock().getTemperature();
        Biome biome = location.getBlock().getBiome();

        // Temp 2.0+ - desert, mesa
        if (temperature >= 1.0) {

            // Desert biomes use Sand Warpstones
            if (biome.name().contains("DESERT")) {
                design = WarpstoneDesign.SAND;
            }

            // Nether biomes use Hell Warpstones
            else if (biome.equals(Biome.NETHER)) {
                design = WarpstoneDesign.HELL;
            }

            // All other hot biomes use Mesa Warpstones
            else {
                design = WarpstoneDesign.MESA;
            }

        }

        // Temp 0.1+ - most biomes
        else if (temperature > 0.1) {

            // End biomes use End Warpstones
            if (biome.name().contains("END")) {
                design = WarpstoneDesign.END;
            }

            // Most other biomes use Default Warpstones
            else {
                design = WarpstoneDesign.DEFAULT;
            }

        }

        // Temp less than 0.1 - use Snow Warpstones
        else {
            design = WarpstoneDesign.SNOW;
        }

        return design;
    }

    /**
     * Generates the ground pattern for the specified Warpstone.
     *
     * @param warpstone the Warpstone to generate
     * @param size      the radius of ground cover to generate
     */
    private static void generateGroundPattern(Warpstone warpstone, int size) {
        Location location = warpstone.getLocation();
        generateGroundPattern(location, size);
    }

    /**
     * Generates the ground pattern for the specified location.
     *
     * @param location the centerpoint of the area to decorate, immediately above
     *                 the ground
     * @param size     the radius of ground cover to generate
     */
    private static void generateGroundPattern(Location location, int size) {
        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory()
                .getEditSession(BukkitAdapter.adapt(location.getWorld()), 100)) {
            // Get the location of the warpstone's center
            BlockVector3 loc = BukkitAdapter.asBlockVector(location.subtract(0, 1, 0));

            // Get the ground pattern
            Pattern pattern = getGroundPattern(location);

            editSession.makeCylinder(loc, pattern, 2 + size, 1, true);

            Operation operation = editSession.commit();
            Operations.complete(operation);
        }

        catch (WorldEditException e) {
            WorldEdit.logger
                    .warn("Unable to generate Warpstone ground cover, unknown error when generating ground cover: "
                            + e.getLocalizedMessage());
        }
    }

    /**
     * Gets the ground pattern for the specified location.
     *
     * @param location the centerpoint of the area to decorate, immediately above
     *                 the ground
     * @return a WorldEdit pattern for the ground decoration
     */
    private static Pattern getGroundPattern(Location location) {
        try {
            // Ground pattern is the center block, plus coarse dirt and gravel
            String centerBlockName = location.subtract(0, 1, 0).getBlock().getType().getKey().getKey();
            String patternString = "coarse_dirt,gravel," + centerBlockName;

            return WorldEdit.getInstance().getPatternFactory().parseFromInput(patternString, new ParserContext());
        } catch (InputParseException e) {
            WorldEdit.logger
                    .warn("Unable to generate Warpstone ground cover, invalid pattern: " + e.getLocalizedMessage());
            return null;
        }
    }

}