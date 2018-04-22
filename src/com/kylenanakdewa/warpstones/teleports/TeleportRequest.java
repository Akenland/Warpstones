package com.kylenanakdewa.warpstones.teleports;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.core.common.prompts.Prompt;
import com.kylenanakdewa.warpstones.ConfigValues;
import com.kylenanakdewa.warpstones.WarpPlayer;

// Represents a teleport initiated by /tp <destPlayer>
class TeleportRequest {

    // HashMap to store all TP requests - UUID is of the receiving player
    static final HashMap<UUID,TeleportRequest> requests = new HashMap<UUID,TeleportRequest>();


    final Player receivingPlayer;
    final Player sendingPlayer;

    final boolean delay;
    final boolean checkDistance;

    //// Constructor
    // For normal TP
    TeleportRequest(Player sendingPlayer, Player destPlayer, boolean delay, boolean checkDistance){
        this.sendingPlayer = sendingPlayer;
        this.receivingPlayer = destPlayer;
        this.delay = delay;
        this.checkDistance = checkDistance;

        // If sending player has noask, skip the request and complete the teleport
        if(sendingPlayer.hasPermission("warpstones.tp.noask")){
            //delay = false;
            doTeleport();
            return;
        }

        // Notify receiving player
        notifyReceivingPlayer();
    }

    // Normal TP, with delay and distance check
    TeleportRequest(Player sendingPlayer, Player destPlayer){
        this(sendingPlayer, destPlayer, true, true);
    }


    // Show the request to the receiving player
    void notifyReceivingPlayer(){
        // Check if there's already a TPHere request
        TeleportRequest request = TeleportHereRequest.requests.get(sendingPlayer.getUniqueId());
        if(request!=null){
            request.doTeleport();
            return;
        }

        Utils.sendActionBar(sendingPlayer, CommonColors.MESSAGE+"Waiting for "+receivingPlayer.getDisplayName()+CommonColors.MESSAGE+" to confirm teleport");
        
        // Prompt the receiving player to confirm teleport
        Prompt prompt = new Prompt();
        prompt.addQuestion(sendingPlayer.getDisplayName()+CommonColors.INFO+" wants to teleport to you.");
        prompt.addAnswer("Click here or type "+ConfigValues.color+"/tphere"+CommonColors.MESSAGE+" to confirm", "command_tphere");
        prompt.display(receivingPlayer);

        // Save the request
        requests.put(receivingPlayer.getUniqueId(), this);
    }


    // Complete the teleport
    boolean doTeleport(){
        // Check distance
        if(checkDistance
        && !sendingPlayer.hasPermission("warpstones.tp.nolimits")
        && (!sendingPlayer.getWorld().equals(receivingPlayer.getWorld()) || (sendingPlayer.getLocation().distanceSquared(receivingPlayer.getLocation()) > ConfigValues.tpDistance))){
            Utils.sendActionBar(sendingPlayer, CommonColors.ERROR+"You are too far away to teleport");
            Utils.sendActionBar(receivingPlayer, sendingPlayer.getDisplayName()+CommonColors.ERROR+" is too far away to teleport");
            return false;
        }

        // Clear the request
        requests.remove(receivingPlayer.getUniqueId());

        Utils.sendActionBar(sendingPlayer, "Teleporting to "+receivingPlayer.getDisplayName()+CommonColors.MESSAGE+". Your current location will be lost.");

        // Warn if receiving player is flying
        if(receivingPlayer.isFlying() && !sendingPlayer.isFlying()) sendingPlayer.sendMessage(receivingPlayer.getDisplayName()+CommonColors.INFO+" is flying. You may suffer fall damage after teleporting.");

        // Complete the teleport
        return new WarpPlayer(sendingPlayer).teleport(receivingPlayer.getLocation(), delay);
    }
}