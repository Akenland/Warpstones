package com.kylenanakdewa.warpstones.warpstone.listeners;

import com.kylenanakdewa.warpstones.warpstone.WarpstoneManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

/**
 * Saves all Warpstones to the data file when the world is saved.
 *
 * @author Kyle Nanakdewa
 */
public class WarpstonesWorldSaveListener implements Listener {

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        WarpstoneManager.get().saveAllWarpstones();
    }
}
