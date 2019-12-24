package com.kylenanakdewa.warpstones;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import com.kylenanakdewa.core.common.Utils;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;

enum WarpstoneDesigns {

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
        WarpstonesPlugin.plugin.saveResource("schematics\\"+schemName, false);

        // Attempt to load it
        File schemFile = new File(WarpstonesPlugin.plugin.getDataFolder(), "schematics\\"+schemName);
        ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
        try(ClipboardReader reader = format.getReader(new FileInputStream(schemFile))){
            return reader.read();
        } catch(IOException e){
            Utils.notifyAdminsError("[Warpstones] An error occured loading schematic "+schemName);
        }
        return null;
	}
}