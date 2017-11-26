package com.KyleNecrowolf.Warpstones;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KyleNecrowolf.RealmsCore.Common.ConfigAccessor;
import com.KyleNecrowolf.RealmsCore.Common.Utils;
import com.KyleNecrowolf.RealmsCore.Prompts.Prompt;
import com.KyleNecrowolf.Warpstones.ConfigValues;
import com.KyleNecrowolf.Warpstones.Main;
import com.KyleNecrowolf.Warpstones.Items.WarpItems;

// Represents a location that can be warped to, and is often marked by a lapis and stone structure
public class Warpstone {

	// Warpstone data
	private final String name; // Warpstone name, must be unique

	private boolean loaded; // Whether the warpstone has been loaded

	private Location loc; // Location of warpstone exit
	private boolean disabled; // True when warpstone is disabled/inactive
	private String disabledMsg; // Message to be displayed when disabled and entered, null to use default message
	private boolean requirePerm; // True if perm is required to use this warpstone
	
	
	// Constructor
	public Warpstone(String name){
		this.name = name.toLowerCase();
	}
	
	
	////// ACCESSING WARPSTONES FILE
	// Load warpstone data
	private boolean load(){
		// If already loaded, return
		if(loaded) return true;
		
		// Load the warpstones file
		ConfigAccessor warpstoneFile = new ConfigAccessor("warpstones.yml", Main.plugin);

		// See if the warpstone exists in file
		if(warpstoneFile.getConfig().contains("warpstones."+name)){

			// Get warpstone location, if it exists
			if(warpstoneFile.getConfig().contains("warpstones."+name+".location")){
				World world = Bukkit.getWorld(warpstoneFile.getConfig().getString("warpstones."+name+".location.world"));
				double x = warpstoneFile.getConfig().getDouble("warpstones."+name+".location.x");
				double y = warpstoneFile.getConfig().getDouble("warpstones."+name+".location.y");
				double z = warpstoneFile.getConfig().getDouble("warpstones."+name+".location.z");
				float yaw = (float) warpstoneFile.getConfig().getDouble("warpstones."+name+".location.yaw");
				float pitch = (float) warpstoneFile.getConfig().getDouble("warpstones."+name+".location.pitch");

				loc = new Location(world, x, y, z, yaw, pitch);
			}

			// Get disabled status
			disabled = warpstoneFile.getConfig().getBoolean("warpstones."+name+".disabled");
			disabledMsg = warpstoneFile.getConfig().getString("warpstones."+name+".disabled-msg");
			requirePerm = warpstoneFile.getConfig().getBoolean("warpstones."+name+".require-perm-to-use");

			loaded = true;
		}

		return loaded;
	}
	
	// Save warpstone data
	private void save(){
		// Load the warpstones file
		ConfigAccessor warpstoneFile = new ConfigAccessor("warpstones.yml", Main.plugin);
		
		// Save the location
		if(this.loc!=null){
			warpstoneFile.getConfig().set("warpstones."+name+".location.world", this.loc.getWorld().getName());
			warpstoneFile.getConfig().set("warpstones."+name+".location.x", this.loc.getX());
			warpstoneFile.getConfig().set("warpstones."+name+".location.y", this.loc.getY());
			warpstoneFile.getConfig().set("warpstones."+name+".location.z", this.loc.getZ());
			warpstoneFile.getConfig().set("warpstones."+name+".location.yaw", this.loc.getYaw());
			warpstoneFile.getConfig().set("warpstones."+name+".location.pitch", this.loc.getPitch());
		}
		
		// Save disabled status
		if(this.disabled) warpstoneFile.getConfig().set("warpstones."+name+".disabled", true); else warpstoneFile.getConfig().set("warpstones."+name+".disabled", null);
		
		warpstoneFile.saveConfig();
	}
	
	// Delete the warpstone
	public void delete(){
		// Load the warpstones file
		ConfigAccessor warpstoneFile = new ConfigAccessor("warpstones.yml", Main.plugin);
		
		// Delete the warpstone from file
		warpstoneFile.getConfig().set("warpstones."+name, null);
		warpstoneFile.saveConfig();
	}


	//// Retrieving warpstone information
	// Get warpstone name
	public String getName(){
		return name;
	}

	// Get warpstone location
	public Location getLocation(){
		if(load()) return loc;
		return null;
	}

	// Get if the warpstone is disabled
	public boolean isDisabled(){
		if(load()) return disabled;
		return true;
	}


	//// Editing warpstone information
	// Save the warpstone location
	public void setLocation(Location loc){
		this.loc = loc;
		save();
	}
	
	// Disable the warpstone
	public void disable(){
		this.disabled = true;
		save();
	}
	// Enable the warpstone
	public void enable(){
		this.disabled = false;
		save();
	}

	// Generate this warpstone
	public void generateWarpstone(Player player, int size, WarpstoneDesigns design){
		 // Make sure this player has permission to use WE and Warpstones
        if(!player.hasPermission("worldedit.generation.*") || !player.hasPermission("warpstones.manage")){
            player.sendMessage(Utils.errorText+"You can't generate Warpstones! Ask an admin for help.");
            return;
        }
		
		// Let WE generate the actual stone
		if(Bukkit.getPluginManager().getPlugin("WorldEdit")!=null) WarpstoneWEGeneration.generateWarpstone(player, size, design);
		else player.sendMessage(Utils.errorText+"WorldEdit not installed, could not generate Warpstone!");

		// Command blocks can be generated by Bukkit
		generateWarpstoneCmdBlock(player.getLocation().add(0, -2, -1 -size)); // North
		generateWarpstoneCmdBlock(player.getLocation().add(0, -2, +1 +size)); // South
		generateWarpstoneCmdBlock(player.getLocation().add(+1 +size, -2, 0)); // East
		generateWarpstoneCmdBlock(player.getLocation().add(-1 -size, -2, 0)); // West

		player.sendMessage(Utils.messageText+"Warpstone generated.");
		if(load()) player.teleport(getLocation());
	}
	public void generateWarpstone(Player player, int size){
		// Make sure WE is installed
		if(Bukkit.getPluginManager().getPlugin("WorldEdit")==null){
			player.sendMessage(Utils.errorText+"WorldEdit not installed, could not generate Warpstone!");
			return;
		}
		WarpstoneDesigns design = WarpstoneDesigns.DEFAULT;
		if(ConfigValues.generateBiomeWarpstones){
        	// Figure out which warpstone design, based on temperature
			double temperature = player.getLocation().getBlock().getTemperature();
			// Temp 2.0+ - desert, mesa
        	if(temperature>=1.0){
        	    // If it's a desert, sand warpstone
        	    Biome biome = player.getLocation().getBlock().getBiome();
        	    if(biome.name().contains("DESERT")){
        	        design = WarpstoneDesigns.SAND;
        	    }
        	    // If it's HELL, use Nether style
        	    else if(biome.equals(Biome.HELL)){
        	        design = WarpstoneDesigns.HELL;
        	    }
        	    // All other hot biomes use the mesa style
        	    else {
        	        design = WarpstoneDesigns.MESA;
        	    }
        	}
        	// Temp 0.1+ - most biomes
        	else if(temperature>0.1){
        	    // If it's SKY, use End style
        	    if(temperature==0.5 && player.getLocation().getBlock().getBiome().equals(Biome.SKY)){
        	        design = WarpstoneDesigns.END;
        	    }
        	    // Most other biomes use the default style
        	    else {
        	        design = WarpstoneDesigns.DEFAULT;
        	    }
        	}
        	// Temp less than 0.1 - snow
        	else {
        	    design = WarpstoneDesigns.SNOW;
			}
		}
		generateWarpstone(player, size, design);
	}
	private void generateWarpstoneCmdBlock(Location location){
		Block cmdBlock = location.getBlock();
		
		// Set it to a command block
		cmdBlock.setType(Material.COMMAND);

		// Set the command
		BlockState cmdBlockState = cmdBlock.getState();
		((CommandBlock) cmdBlockState).setCommand("/warpstone enter "+name+" @p[r=2]");
		cmdBlockState.update(true);

		// Place a pressure plate above
		Block plate = location.add(0, 2, 0).getBlock();
		plate.setType(Material.STONE_PLATE);
	}
	
	
	////// WARPING FUNCTIONALITY
	// Enter the warpstone
	public void enter(Player player){
		
		WarpstoneActivateEvent event = new WarpstoneActivateEvent(player, this);

		//// Initial checks
		// If warpstone is disabled, stop and send the player a message
		if(isDisabled()){
			String disabledMsg = (this.disabledMsg==null) ? Utils.errorText+"This warpstone is inactive" : this.disabledMsg;
			Utils.sendActionBar(player, disabledMsg); 
			event.setCancelled(true);
			return;
		}
		
		// If warpstone requires special perms, check for that
		if(this.requirePerm){
			if(!player.hasPermission("warpstones.use."+this.name)){
				Utils.sendActionBar(player, Utils.errorText+"You can't use this warpstone");
				event.setCancelled(true);
				return;
			}
		}

		// Call activation event
		Bukkit.getServer().getPluginManager().callEvent(event);
		if(event.isCancelled()) return;
		
		
		WarpPlayer warpPlayer = new WarpPlayer(player);

		// If player is setting home, set this as their home warpstone
		if(WarpUtils.playersSettingHome.contains(player.getName())){
			WarpUtils.playersSettingHome.remove(player.getName());

			// If this is spawn warpstone, don't set it as home
			if(this.name.equals(ConfigValues.warpstoneSpawn.getName())){
				player.sendMessage(Utils.messageText+"This is the spawn warpstone, you cannot set it as your home warpstone. Return here with "+ConfigValues.color+"/spawn");
				return;
			}

			player.sendMessage(Utils.messageText+"Home warpstone saved. Return here with "+ConfigValues.color+"/home");
			warpPlayer.setHome(this);
			return;
		}

		// If player is holding a warp shard, attempt to link it
		ItemStack itemInHand = player.getInventory().getItemInMainHand();
		if(ConfigValues.warpShardsUsable && WarpItems.isWarpShard(itemInHand)){
			// If shard is linked, take the shard and warp the player
			if(WarpItems.isWarpShardLinked(itemInHand)){
				Warpstone dest = WarpItems.getLinkedShardWarpstone(itemInHand);
				
				// If the shard is linked to this warpstone, tell the player
				if(dest.getName().equals(this.getName())){
					player.sendMessage(Utils.infoText+"Your warp shard is linked to this warpstone. Use it at any other warpstone to return here.");
					return;
				}

				Utils.sendActionBar(player, "Warping to shard's stored destination...");
				itemInHand.setAmount(itemInHand.getAmount()-1);
				player.getEquipment().setItemInMainHand(itemInHand);

				warpPlayer.warp(dest, false);
				return;
			}

			// Otherwise, attempt to link it
			player.getEquipment().setItemInMainHand(WarpItems.getLinkedWarpShard(this, itemInHand));
			player.sendMessage(Utils.messageText+"Your warp shard was linked to this warpstone. Use it at any other warpstone to return here.");
			return;
		}


		//// Prepare a prompt
		Prompt prompt = new Prompt();
		boolean questionSet = false;
		
		// If this is player's home warpstone, send them to their last
		if(warpPlayer.getHome()!=null && this.name.equals(warpPlayer.getHome().getName())){
			prompt.addQuestion("This is your home warpstone. Return here with "+ConfigValues.color+"/home");
			questionSet = true;
		} else {
			prompt.addAnswer("Warp home", "command_warp home");
		}

		// If this is spawn warpstone
		if(this.name.equals(ConfigValues.warpstoneSpawn.getName())){
			prompt.addQuestion("This is the spawn warpstone. Return here with "+ConfigValues.color+"/spawn"+Utils.messageText+". "+"Warpstones allow you to fast travel to previously visited locations.");
			questionSet = true;
		} else {
			prompt.addAnswer("Warp to spawn", "command_warp spawn");
		}

		// If this is neither (last warpstone)
		if(!questionSet){
			prompt.addQuestion("Location saved. Return here with "+ConfigValues.color+"/warp last");
			
			// If this isn't the last warpstone they visited, get a random number to see if they get a warp dust
			if(warpPlayer.getLast()!=null && !this.name.equals(warpPlayer.getLast().getName()) && ThreadLocalRandom.current().nextInt(100) > 100-ConfigValues.warpDustChance){
				player.getInventory().addItem(WarpItems.WARP_DUST);
			}
			
			warpPlayer.setLast(this);

		} else {
			prompt.addAnswer("Warp to last warpstone", "command_warp last");
		}
		
		prompt.display(player);
		return;
	}
}
