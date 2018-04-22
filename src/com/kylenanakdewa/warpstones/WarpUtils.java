package com.kylenanakdewa.warpstones;

import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;

import com.kylenanakdewa.core.common.Utils;

final class WarpUtils {
	
	// List of players setting home warps
	static final HashSet<String> playersSettingHome = new HashSet<String>();
	
	// HashMap for players setting warpstone command blocks
	static final HashMap<String, String> warpstoneCmdSet = new HashMap<String, String>();
    
    
	// Display warping particles
	static void displayWarpParticles(Location location){
		location.getWorld().spawnParticle(Particle.PORTAL, location.getX(), location.getY()+1.5, location.getZ(), ConfigValues.tpParticleCount, 0.5, 0.5, 0.5, 0.5);
	}
	// Play warping sound to player
	static void playWarpSound(Location location){
		location.getWorld().playSound(location, Sound.BLOCK_PORTAL_TRAVEL, SoundCategory.AMBIENT, ConfigValues.tpSoundVolume, 2);
	}
	// Play teleportation effects (sound and particles)
	static void playTPEffects(Location location){
		displayWarpParticles(location);
		playWarpSound(location);
	}
	
	
	// Construct vanilla TP command from args
	@Deprecated
	static boolean sendVanillaTP(CommandSender sender, String[] args){
		// Build the string to send
		StringBuilder vanillaCmd = new StringBuilder();
		vanillaCmd.append("minecraft:tp");
		for(String arg:args) {vanillaCmd.append(" ").append(arg);}
		// Send the command
		Utils.sendActionBar(sender, Utils.infoText+"Teleporting with "+vanillaCmd);
		return Bukkit.dispatchCommand(sender, vanillaCmd.toString());
	}

}
