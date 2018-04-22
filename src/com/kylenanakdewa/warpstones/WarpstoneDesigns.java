package com.kylenanakdewa.warpstones;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import com.kylenanakdewa.core.common.Utils;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.data.DataException;

@SuppressWarnings("deprecation")
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
	CuboidClipboard getSchematic(int size){
        // Pick a random schem
        String schemName = this.name().toLowerCase() + size + ThreadLocalRandom.current().nextInt(3) + ".schematic";
        
        // Save the schem from the jar
        WarpstonesPlugin.plugin.saveResource("schematics\\"+schemName, false);
        
        // Attempt to load it
        File schemFile = new File(WarpstonesPlugin.plugin.getDataFolder(), "schematics\\"+schemName);
        //SchematicFormat schem = SchematicFormat.getFormat(schemFile);
        try {
            return SchematicFormat.MCEDIT.load(schemFile);
        } catch(IOException | DataException e){
            Utils.notifyAdminsError("[Warpstones] An error occured loading schematic "+schemName);
        }
        return null;
	}
}