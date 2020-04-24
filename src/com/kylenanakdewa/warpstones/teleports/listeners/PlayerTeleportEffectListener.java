package com.kylenanakdewa.warpstones.teleports.listeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Provides extra visual and sound effects when a player teleports.
 *
 * @author Kyle Nanakdewa
 */
public class PlayerTeleportEffectListener implements Listener {

    /**
     * Displays the visual particle effect at the specified location.
     */
    public static void displayWarpParticles(Location location) {
        location.getWorld().spawnParticle(Particle.PORTAL, location.getX(), location.getY() + 1, location.getZ(), 200,
                0.1, 0.1, 0.1, 0.75);
    }

    /**
     * Plays the sound effect at the specified location.
     */
    public static void playWarpSound(Location location) {
        location.getWorld().playSound(location, Sound.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS, 0.25f, 1.75f);
    }

    /**
     * Plays visual and sound effects at the specified location.
     */
    public static void playTpEffects(Location location) {
        displayWarpParticles(location);
        playWarpSound(location);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled() || event.getPlayer().hasPermission("warpstones.tp.silent")) {
            return;
        }

        switch (event.getCause()) {
            // Plugin-triggered teleports, as well as Ender Pearls, get full effects
            // Chorus fruits do have a sound at enter location, but it's not loud enough
            case ENDER_PEARL:
            case CHORUS_FRUIT:
            case PLUGIN:
                playTpEffects(event.getFrom());
                playTpEffects(event.getTo());
                break;

            // Portals already have sound effects at their exit locations
            case NETHER_PORTAL:
            case END_PORTAL:
            case END_GATEWAY:
                // Enter location - both effects
                playTpEffects(event.getFrom());
                // Exit location - particles only, sound already part of game
                displayWarpParticles(event.getTo());
                break;

            // Spectator and commands don't need effects
            default:
                break;
        }
    }

}