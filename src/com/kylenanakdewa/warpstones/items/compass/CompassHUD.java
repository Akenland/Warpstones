package com.kylenanakdewa.warpstones.items.compass;

import com.kylenanakdewa.warpstones.WarpstonesPlugin;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;
import com.kylenanakdewa.warpstones.warpstone.WarpstoneManager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * A HUD that shows while holding a compass, showing distances to points of
 * interest.
 *
 * @author Kyle Nanakdewa
 */
public class CompassHUD {

    /** The Warpstones plugin instance. */
    private final WarpstonesPlugin plugin;

    public CompassHUD(WarpstonesPlugin plugin) {
        this.plugin = plugin;
        setupCompassTask();
    }

    /**
     * Starts the compass task.
     */
    private void setupCompassTask() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (isHoldingCompass(player)) {
                    showCompassHUD(player);
                } else {
                    hideCompassHUD(player);
                }
            }

        }, 60, 5);
    }

    /**
     * Returns true if the specified player is holding a compass in either hand.
     */
    private boolean isHoldingCompass(Player player) {
        return (player.getInventory().getItemInMainHand() != null
                && player.getInventory().getItemInMainHand().getType().equals(Material.COMPASS))
                || (player.getInventory().getItemInOffHand() != null
                        && player.getInventory().getItemInOffHand().getType().equals(Material.COMPASS));
    }

    /**
     * Shows the Compass HUD to a player.
     */
    private void showCompassHUD(Player player) {
        Scoreboard scoreboard = getScoreboard(player);
        player.setScoreboard(scoreboard);
    }

    /**
     * Hides the Compass HUD from a player, and restores their original scoreboard.
     */
    private void hideCompassHUD(Player player) {
        Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();
        player.setScoreboard(scoreboard);
    }

    /**
     * Gets the scoreboard that should be displayed to the specified player.
     */
    private Scoreboard getScoreboard(Player player) {
        // Set up scoreboard
        Scoreboard board = plugin.getServer().getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("ws_distances", "dummy", "Distance to");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Location playerLocation = player.getLocation();

        // Nearest Warpstone
        Warpstone nearestStone = WarpstoneManager.get().getNearestWarpstone(player.getLocation(), 100, false);
        if (nearestStone != null) {
            String nearestName = "Nearest Warpstone";

            // Admins see ID of nearest stone
            if (player.hasPermission("warpstones.manage")) {
                nearestName = "Nearest: " + nearestStone.getIdentifier();
            }

            Score nearest = obj.getScore(nearestName);
            nearest.setScore((int) nearestStone.getLocation().distance(playerLocation));
        }

        // Spawn Warpstone
        Warpstone spawnStone = WarpstoneManager.get().getSpawnWarpstone();
        if (spawnStone != null) {
            Location spawnLoc = spawnStone.getLocation();
            if (spawnLoc.getWorld().equals(player.getWorld())) {
                Score spawn = obj.getScore("Spawn");
                spawn.setScore((int) spawnLoc.distance(playerLocation));
            }
        }

        // Home bed
        Location homeBed = player.getBedSpawnLocation();
        if (homeBed != null && homeBed.getWorld().equals(player.getWorld())) {
            Score home = obj.getScore("Home Bed");
            home.setScore((int) homeBed.distance(playerLocation));
        }

        return board;
    }

}