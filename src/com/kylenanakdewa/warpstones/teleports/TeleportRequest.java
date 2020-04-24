package com.kylenanakdewa.warpstones.teleports;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.core.common.prompts.Prompt;
import com.kylenanakdewa.warpstones.WarpstonesPlayerData;

/**
 * Represents a teleport initiated by /tp <destPlayer>.
 *
 * @author Kyle Nanakdewa
 */
class TeleportRequest {

    // HashMap to store all TP requests - UUID is of the receiving player
    static final HashMap<UUID, TeleportRequest> requests = new HashMap<UUID, TeleportRequest>();

    protected final Player receivingPlayer;
    protected final Player sendingPlayer;

    protected final boolean delay;
    protected final boolean checkDistance;

    //// Constructor
    // For normal TP
    TeleportRequest(Player sendingPlayer, Player destPlayer, boolean delay, boolean checkDistance) {
        this.sendingPlayer = sendingPlayer;
        this.receivingPlayer = destPlayer;
        this.delay = delay;
        this.checkDistance = checkDistance;

        // If sending player has noask, skip the request and complete the teleport
        if (sendingPlayer.hasPermission("warpstones.tp.noask")) {
            // delay = false;
            doTeleport();
            return;
        }

        // Notify receiving player
        notifyReceivingPlayer();
    }

    // Normal TP, with delay and distance check
    TeleportRequest(Player sendingPlayer, Player destPlayer) {
        this(sendingPlayer, destPlayer, true, true);
    }

    // Show the request to the receiving player
    void notifyReceivingPlayer() {
        // Check if there's already a TPHere request
        TeleportRequest request = TeleportHereRequest.requests.get(sendingPlayer.getUniqueId());
        if (request != null) {
            request.doTeleport();
            return;
        }

        Utils.sendActionBar(sendingPlayer, CommonColors.MESSAGE + "Waiting for " + receivingPlayer.getDisplayName()
                + CommonColors.MESSAGE + " to confirm teleport");

        // Prompt the receiving player to confirm teleport
        Prompt prompt = new Prompt();
        prompt.addQuestion(sendingPlayer.getDisplayName() + CommonColors.INFO + " wants to teleport to you.");
        prompt.addAnswer("Click here or type " + ChatColor.BLUE + "/tphere" + CommonColors.MESSAGE + " to confirm",
                "command_tphere");
        prompt.display(receivingPlayer);

        // Save the request
        requests.put(receivingPlayer.getUniqueId(), this);
    }

    // Complete the teleport
    boolean doTeleport() {
        // Check distance
        if (checkDistance && isTooFar()) {
            return false;
        }

        // Clear the request
        requests.remove(receivingPlayer.getUniqueId());

        Utils.sendActionBar(sendingPlayer, "Teleporting to " + receivingPlayer.getDisplayName() + CommonColors.MESSAGE
                + ". Your current location will be lost.");

        // Warn if receiving player is flying
        if (receivingPlayer.isFlying() && !sendingPlayer.isFlying())
            sendingPlayer.sendMessage(receivingPlayer.getDisplayName() + CommonColors.INFO
                    + " is flying. You may suffer fall damage after teleporting.");

        // Complete the teleport
        return new WarpstonesPlayerData(sendingPlayer).teleport(receivingPlayer.getLocation(), delay);
    }


    private boolean isTooFar() {
        // If sending player has nolimits permission, skip checks
        if(sendingPlayer.hasPermission("warpstones.tp.nolimits")){
            return false;
        }

        // Check that players are in the same world
        if(!sendingPlayer.getWorld().equals(receivingPlayer.getWorld())){
            Utils.sendActionBar(sendingPlayer, CommonColors.ERROR + "Can't teleport from a different world");
            Utils.sendActionBar(receivingPlayer, sendingPlayer.getDisplayName() + CommonColors.ERROR + " is in a different world");
            return true;
        }

        // Check distance - TODO: make value configurable
        if(sendingPlayer.getLocation().distanceSquared(receivingPlayer.getLocation()) > Math.pow(100, 2)) {
            Utils.sendActionBar(sendingPlayer, CommonColors.ERROR + "You are too far away to teleport");
            Utils.sendActionBar(receivingPlayer, sendingPlayer.getDisplayName() + CommonColors.ERROR + " is too far away to teleport");
            return true;
        }

        return false;
    }
}