package com.kylenanakdewa.warpstones.quicksave;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * QuickSaveUtils
 * @author Kyle Nanakdewa
 */
public final class QuickSaveUtils {

    /**
     * Determines if player death was PvP.
     */
    static boolean wasPvPDeath(PlayerDeathEvent event) {
        // If no death message, death was probably PvP
        if(event.getDeathMessage()==null || event.getDeathMessage().isEmpty()) return true;

        // Strip player's own name out of death message
        String deathMsg = event.getDeathMessage().replace(event.getEntity().getName(), "");

        // Check if death message mentions a player
        for(Player player : Bukkit.getOnlinePlayers()){
            // If death message contains any player name, it's a PvP death
            if(deathMsg.contains(player.getName())) return true;
        }
        // Otherwise return false
        return false;
    }

}