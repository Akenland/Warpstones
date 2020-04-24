package com.kylenanakdewa.warpstones.warpstone;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public enum WarpstoneDesign {

    // The different styles of warpstones
	DEFAULT (),
	END (),
	HELL (),
	MESA (),
    SAND (),
    SNOW (),
    THEFLOOD ();

    // Get a random schematic of a particular design and size
	Clipboard getSchematic(int size){
        // Pick a random schem
        String schemName = this.name().toLowerCase() + size + ThreadLocalRandom.current().nextInt(3) + ".schematic";

        // Save the schem from the jar
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Warpstones");
        plugin.saveResource("schematics\\"+schemName, false);

        // Attempt to load it
        File schemFile = new File(plugin.getDataFolder(), "schematics\\"+schemName);
        ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
        try(ClipboardReader reader = format.getReader(new FileInputStream(schemFile))){
            return reader.read();
        } catch(IOException e){
            plugin.getLogger().warning("An error occured loading schematic "+schemName);
        }
        return null;
	}
}