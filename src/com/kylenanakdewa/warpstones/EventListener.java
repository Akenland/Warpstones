package com.kylenanakdewa.warpstones;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.kylenanakdewa.core.characters.players.PlayerCharacter;
import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.prompts.Prompt;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;
import com.kylenanakdewa.warpstones.warpstone.WarpstoneManager;

@Deprecated
public final class EventListener implements Listener {

	// Death handler
	@EventHandler
	public void showLocOnDeath(PlayerDeathEvent event) {
		PlayerCharacter character = PlayerCharacter.getCharacter(event.getEntity());

		Location loc = event.getEntity().getLocation();
		String locString = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();

		Warpstone nearestWarp = WarpstoneManager.get().getNearestWarpstone(loc, 100, false);
		String nearestWarpString = "";
		if (nearestWarp != null) {
			nearestWarpString += " (Nearest Warpstone: ";
			if (nearestWarp.getDisplayName() != null)
				nearestWarpString += nearestWarp.getDisplayName() + ", ";
			nearestWarpString += Math.round(nearestWarp.getLocation().distance(loc)) + "m away)";
		}

		// Prompt shown to player
		Prompt deathPrompt = new Prompt();
		deathPrompt.addQuestion(CommonColors.INFO + "You died at " + locString + nearestWarpString);

		if (event.getEntity().hasPermission("warpstones.death.teleport")) {
			deathPrompt.addAnswer("Teleport to death location", "command_tp " + locString);
			if (nearestWarp != null)
				deathPrompt.addAnswer("Warp to nearest warpstone", "command_warp to " + nearestWarp.getIdentifier());
		}

		deathPrompt.display(event.getEntity());

		// Message shown to realm members
		if (character.getRealm() != null) {
			Collection<Player> realmMembers = character.getRealm().getOnlinePlayers();
			realmMembers.remove(event.getEntity());
			for (Player member : realmMembers) {
				member.sendMessage(
						character.getName() + CommonColors.INFO + " died at " + locString + nearestWarpString);
			}
		}
	}

}