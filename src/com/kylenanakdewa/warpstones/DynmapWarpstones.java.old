package com.kylenanakdewa.warpstones;

import com.kylenanakdewa.warpstones.events.WarpstoneEditEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

/**
 * Shows Warpstones on Dynmap.
 */
final class DynmapWarpstones implements Listener {

    private final WarpstonesPlugin warpstonesPlugin;
    private final DynmapAPI dynmapAPI = (DynmapAPI)Bukkit.getServer().getPluginManager().getPlugin("dynmap");

    /**
     * Sets up Dynmap Warpstones.
     */
    DynmapWarpstones(WarpstonesPlugin plugin){
        this.warpstonesPlugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, warpstonesPlugin);
        updateMarkers();
    }

    /**
     * Gets the marker set.
     */
    private MarkerSet getMarkerSet(){
        MarkerSet set = dynmapAPI.getMarkerAPI().getMarkerSet("Warpstones");
        if(set==null) set = dynmapAPI.getMarkerAPI().createMarkerSet("Warpstones", "Warpstones", null, false);
        set.setMinZoom(3);
        return set;
    }

    /**
     * Updates marker set with warpstones.
     */
    private void updateMarkers(){
        // Remove existing
        //getMarkerSet().getMarkers().clear();
        getMarkerSet().deleteMarkerSet();

        // Add warpstones
        for(Warpstone warpstone : WarpstonesPlugin.getWarpstones().values()){
            String id = warpstone.getIdentifier();
            String label = warpstone.getDisplayName()==null ? "Warpstone" : warpstone.getDisplayName();
            Location loc = warpstone.getLocation();
            if(loc!=null && loc.getWorld()!=null)
                getMarkerSet().createMarker(id, label, loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), getMarkerIcon(), false);
        }
    }


    /**
     * Gets the marker icon.
     */
    private MarkerIcon getMarkerIcon(){
        MarkerIcon icon = dynmapAPI.getMarkerAPI().getMarkerIcon("warpstone");
        if(icon==null)
            dynmapAPI.getMarkerAPI().createMarkerIcon("warpstone", "Warpstone", warpstonesPlugin.getResource("warpstone.png"));
        return icon;
    }


    /**
     * Event listener that updates markers when a warpstone is edited.
     */
    @EventHandler
    public void onWarpstoneEdit(WarpstoneEditEvent event){
        updateMarkers();
    }
}