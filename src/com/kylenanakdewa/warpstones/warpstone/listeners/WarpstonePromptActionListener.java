package com.kylenanakdewa.warpstones.warpstone.listeners;

import com.kylenanakdewa.core.common.prompts.PromptActionEvent;
import com.kylenanakdewa.warpstones.WarpstonesPlayerData;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;
import com.kylenanakdewa.warpstones.warpstone.WarpstoneManager;
import com.kylenanakdewa.warpstones.warpstone.events.PlayerWarpEvent.WarpCause;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listens for Warpstones prompt actions.
 *
 * @author Kyle Nanakdewa
 */
public class WarpstonePromptActionListener implements Listener {

    @EventHandler
    public void onPromptAction(PromptActionEvent event) {
        if (event.isType("warp")) {
            WarpstonesPlayerData player = new WarpstonesPlayerData(event.getPlayer());
            switch (event.getAction()) {
                case "home":
                    player.teleportHome(false);
                    break;
                case "recent":
                    Warpstone mostRecentWs = player.getMostRecentWarpstone();
                    player.warp(mostRecentWs, WarpCause.PROMPT, false);
                    break;
                case "spawn":
                    player.teleportSpawn(false);
                    break;
                default:
                    String wsIdentifier = event.getAction();
                    Warpstone warpstone = WarpstoneManager.get().getWarpstone(wsIdentifier);
                    player.warp(warpstone, WarpCause.PROMPT, false);
                    break;
            }
        }
    }

}