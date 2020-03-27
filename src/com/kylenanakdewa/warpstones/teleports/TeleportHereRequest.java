package com.kylenanakdewa.warpstones.teleports;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.core.common.prompts.Prompt;
import com.kylenanakdewa.warpstones.WarpstonesConfig;
import com.kylenanakdewa.warpstones.WarpPlayer;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

class TeleportHereRequest extends TeleportRequest {

    // HashMap to store all TPHere requests - UUID is of the receiving player
    static final HashMap<UUID,TeleportRequest> requests = new HashMap<UUID,TeleportRequest>();


    private Location dest;

    TeleportHereRequest(Player sendingPlayer, Player receivingPlayer, boolean delay, boolean checkDistance){
        super(sendingPlayer, receivingPlayer, delay, checkDistance);

        dest = sendingPlayer.getLocation();
    }
    TeleportHereRequest(Player sendingPlayer, Player receivingPlayer){
        super(sendingPlayer, receivingPlayer);

        dest = sendingPlayer.getLocation();
    }


    @Override
    void notifyReceivingPlayer(){
        // Check if there's already a TP request
        TeleportRequest request = TeleportRequest.requests.get(sendingPlayer.getUniqueId());
        if(request!=null){
            request.doTeleport();
            return;
        }

        // Prompt the receiving player to confirm teleport
        Prompt prompt = new Prompt();
        prompt.addQuestion(sendingPlayer.getDisplayName()+CommonColors.INFO+" wants you to teleport to them.");
        prompt.addAnswer("Click here or type "+WarpstonesConfig.color+"/tp"+CommonColors.MESSAGE+" to confirm", "command_tp");
        prompt.display(receivingPlayer);

        // Save the request
        requests.put(receivingPlayer.getUniqueId(), this);
    }


    @Override
    boolean doTeleport(){
        if(dest==null) dest = sendingPlayer.getLocation();

        // Check distance
        if(checkDistance
        && !sendingPlayer.hasPermission("warpstones.tp.nolimits") && !receivingPlayer.hasPermission("warpstones.tp.nolimits")
        && (!sendingPlayer.getWorld().equals(receivingPlayer.getWorld()) || dest.distanceSquared(receivingPlayer.getLocation()) > WarpstonesConfig.tpDistance)){
            Utils.sendActionBar(receivingPlayer, CommonColors.ERROR+"You are too far away to teleport");
            Utils.sendActionBar(sendingPlayer, receivingPlayer.getDisplayName()+CommonColors.ERROR+" is too far away to teleport");
            return false;
        }

        // Clear the request
        requests.remove(receivingPlayer.getUniqueId());

        Utils.sendActionBar(receivingPlayer, "Teleporting to "+sendingPlayer.getDisplayName()+CommonColors.MESSAGE+". Your current location will be lost.");

        // Warn if receiving player is flying
        if(sendingPlayer.isFlying() && !receivingPlayer.isFlying()) receivingPlayer.sendMessage(sendingPlayer.getDisplayName()+CommonColors.INFO+" is flying. You may suffer fall damage after teleporting.");

        // Complete the teleport
        return new WarpPlayer(receivingPlayer).teleport(dest, delay);
    }
}