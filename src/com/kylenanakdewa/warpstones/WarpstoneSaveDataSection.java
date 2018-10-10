package com.kylenanakdewa.warpstones;

import com.kylenanakdewa.core.common.savedata.SaveDataSection;

import org.bukkit.plugin.Plugin;

/**
 * Represents plugin save data that can be stored with a Warpstone.
 * <p>
 * Extend this class with your own plugin's methods, for easy saving and loading of data for Warpstones.
 */
public abstract class WarpstoneSaveDataSection extends SaveDataSection {

    /** The Warpstone that this save data is for. */
    protected final Warpstone warpstone;

    /**
     * Creates or retrieves a SaveDataSection for the specified Warpstone and plugin.
     * @param warpstone the Warpstone to save data for
     * @param plugin the plugin that is saving data
     */
    public WarpstoneSaveDataSection(Warpstone warpstone, Plugin plugin){
        super(warpstone.getData(plugin));
        this.warpstone = warpstone;
    }


    /**
     * Gets the Warpstone that this data is saved for.
     * @return the Warpstone where this data is stored
     */
    public Warpstone getWarpstone(){
        return warpstone;
    }

    @Override
    public void save(){
        WarpstonesPlugin.plugin.saveWarpstones();
    }

}