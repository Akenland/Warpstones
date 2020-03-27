package com.kylenanakdewa.warpstones;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.kylenanakdewa.core.common.CommonColors;
import com.kylenanakdewa.core.common.Utils;
import com.kylenanakdewa.core.common.prompts.Prompt;
import com.kylenanakdewa.core.common.savedata.SaveDataSection;
import com.kylenanakdewa.warpstones.events.WarpstoneActivateEvent;
import com.kylenanakdewa.warpstones.events.WarpstoneEditEvent;

/**
 * Represents a location that can be warped to, and is often marked by a lapis and stone structure.
 * @author Kyle Nanakdewa
 */
public class Warpstone {

	/**
	 * Gets a Warpstone on the server.
	 * @param identifier the unique name of the Warpstone to retrieve
	 * @return the Warpstone, or null if it does not exist
	 */
	public static Warpstone get(String identifier){
		if(identifier!=null) identifier = identifier.toLowerCase();
		return WarpstonesPlugin.getWarpstones().get(identifier);
	}
	/**
	 * Creates a new Warpstone.
	 * @param identifier a unique name for the new Warpstone
	 * @param location the location of the new Warpstone's exit
	 * @return the new Warpstone
	 * @throws IllegalArgumentException if the Warpstone already exists
	 */
	public static Warpstone create(String identifier, Location location){
		identifier = identifier.toLowerCase();
		if(WarpstonesPlugin.getWarpstones().containsKey(identifier)) throw new IllegalArgumentException("Warpstone "+identifier+" already exists");

		Warpstone warpstone = new Warpstone(identifier);
		warpstone.setLocation(location);

		WarpstonesPlugin.getWarpstones().put(identifier, warpstone);
		return warpstone;
	}
	/**
	 * Gets the server's spawn warpstone.
	 * @return the spawn warpstone, or null if it does not exist
	 */
	public static Warpstone getSpawn(){
		return Warpstone.get(WarpstonesConfig.warpstoneSpawn);
	}
	/**
	 * Gets the warpstone nearest the specified location.
	 * @param location the location to search near
	 * @param radius the maximum search radius
	 * @param includeDisabled whether to include disabled warpstones
	 * @return the nearest Warpstone, or null if one was not found
	 */
	public static Warpstone getNearest(Location location, double radius, boolean includeDisabled){
		double radiusSquared = Math.pow(radius, 2);
		Warpstone nearestStone = null;
		double distanceSquared = radiusSquared;
		for(Warpstone warpstone : WarpstonesPlugin.getWarpstones().values()){
			if(warpstone.getLocation()!=null && warpstone.getLocation().getWorld().equals(location.getWorld()) && (nearestStone==null || warpstone.getLocation().distanceSquared(location) <= distanceSquared)){
				nearestStone = warpstone;
				distanceSquared = warpstone.getLocation().distanceSquared(location);
			}
		}

		return nearestStone;
	}

	/**
	 * Loads a Warpstone from a ConfigurationSection.
	 */
	static Warpstone loadFromConfig(String identifier, ConfigurationSection warpstoneKey){
		Warpstone warpstone = new Warpstone(identifier);

		// Get warpstone location, if it exists
		if(warpstoneKey.contains("location")){
			World world = Bukkit.getWorld(warpstoneKey.getString("location.world"));
			double x = warpstoneKey.getDouble("location.x");
			double y = warpstoneKey.getDouble("location.y");
			double z = warpstoneKey.getDouble("location.z");
			float yaw = (float) warpstoneKey.getDouble("location.yaw");
			float pitch = (float) warpstoneKey.getDouble("location.pitch");
			warpstone.location = new Location(world, x, y, z, yaw, pitch);
		}

		warpstone.disabled = (warpstoneKey.getBoolean("disabled"));
		warpstone.disabledMsg = (warpstoneKey.getString("disabled-msg"));
		warpstone.requirePerm = (warpstoneKey.getBoolean("require-perm"));
		warpstone.condition = (warpstoneKey.getString("condition"));
		warpstone.displayName = (warpstoneKey.getString("display-name"));

		return warpstone;
	}
	/**
	 * Saves the Warpstone to a ConfigurationSection.
	 */
	ConfigurationSection saveToConfig(){
		ConfigurationSection warpstoneKey = new MemoryConfiguration();
		// Save the location
		if(location!=null && location.getWorld()!=null){
			warpstoneKey.set("location.world", location.getWorld().getName());
			warpstoneKey.set("location.x", location.getX());
			warpstoneKey.set("location.y", location.getY());
			warpstoneKey.set("location.z", location.getZ());
			warpstoneKey.set("location.yaw", location.getYaw());
			warpstoneKey.set("location.pitch", location.getPitch());
		}

		warpstoneKey.set("disabled", isDisabled() ? true : null);
		warpstoneKey.set("disabled-msg", getDisabledMsg());
		warpstoneKey.set("require-perm", requiresPerm() ? true : null);
		warpstoneKey.set("condition", getCondition());
		warpstoneKey.set("display-name", getDisplayName());

		data.getValues(true).forEach((key,value) -> warpstoneKey.set(key, value));

		return warpstoneKey;
	}

	/**
	 * Deletes the Warpstone. This action is permanent.
	 */
	public void delete(){
		setDisabled(true);
		WarpstonesPlugin.deleteWarpstone(identifier);
	}


	/** The unique name that identifies this Warpstone. */
	private final String identifier;

	/** The display/friendly name of this Warpstone. */
	private String displayName;
	/** Location of this Warpstone (exit location). */
	private Location location;

	/** Whether this Warpstone is disabled. When true, players cannot activate the Warpstone. */
	private boolean disabled;
	/** Message to display to players who activate this Warpstone while it is disabled. Optional. */
	private String disabledMsg;
	/** Whether permission is required to activate this Warpstone. If true, players must have warpstones.use.warpstone_name */
	private boolean requirePerm;
	/** A condition that must be met to activate this Warpstone. Optional. */
	private String condition;

	/** A data section for other plugins to store data for this Warpstone. */
	private ConfigurationSection data;


	private Warpstone(String identifier){
		this.identifier = identifier.toLowerCase();
		data = WarpstonesPlugin.getWarpstonesFile().getConfigurationSection(identifier);
		if(data==null) data = WarpstonesPlugin.getWarpstonesFile().createSection(identifier);
	}


	// Get warpstone name
	@Deprecated
	public String getName(){
		return identifier;
	}
	/**
	 * Gets the unique name of this Warpstone.
	 * @return the unique identifier of this Warpstone
	 */
	public String getIdentifier(){
		return identifier;
	}

	/**
	 * Gets the display (friendly) name of this Warpstone.
	 * @return the display name of this Warpstone, or null if one was not set
	 */
	public String getDisplayName(){
		return displayName;
	}
	/**
	 * Sets the display (friendly) name of this Warpstone.
	 * @param name the new display name for this Warpstone, or null to clear
	 */
	public void setDisplayName(String name){
		displayName = name;
		fireEditEvent();
	}

	/**
	 * Gets the location of this Warpstone.
	 * <p>
	 * Note that this refers to the exit location of the Warp - not the actual location of the stone.
	 * @return the location of this Warpstone's exit
	 */
	public Location getLocation(){
		return location;
	}
	/**
	 * Sets the location of this Warpstone.
	 * <p>
	 * Note that this refers to the exit location of the Warp - not the actual location of the stone.
	 * @param location the new location of this Warpstone's exit
	 */
	public void setLocation(Location location){
		this.location = location;
		fireEditEvent();
	}

	/**
	 * Gets whether this Warpstone is disabled.
	 * Disabled Warpstones cannot be activated by players, but otherwise function normally.
	 * <p>
	 * When a player attempts to activate a disabled Warpstone, they see {@link #getDisabledMsg()}, if set.
	 * @return true if this Warpstone is disabled
	 */
	public boolean isDisabled(){
		return disabled;
	}
	/**
	 * Sets whether this Warpstone is disabled.
	 * Disabled Warpstones cannot be activated by players, but otherwise function normally.
	 * <p>
	 * When a player attempts to activate a disabled Warpstone, they see {@link #getDisabledMsg()}, if set.
	 * @param disable true to disable the Warpstone, false to enable the Warpstone
	 */
	public void setDisabled(boolean disable){
		disabled = disable;
		fireEditEvent();
	}

	/**
	 * Gets the message displayed to players who attempt to activate this Warpstone while it is disabled.
	 * @return the disabled message for this Warpstone, or null if one was not set
	 */
	public String getDisabledMsg(){
		return disabledMsg;
	}
	/**
	 * Sets the message displayed to players who attempt to activate this Warpstone while it is disabled.
	 * @param message the new disabled message, or null to clear
	 */
	public void setDisabledMsg(String message){
		disabledMsg = message;
		fireEditEvent();
	}

	/**
	 * Gets whether a permission is required to activate this Warpstone.
	 * If enabled, players must have warpstones.use.warpstone_name to activate the Warpstone.
	 * <p>
	 * If a player attempts to activate a Warpstone without permission, they see {@link #getDisabledMsg()}, if set.
	 * @return true if permission is required to activate this Warpstone
	 */
	public boolean requiresPerm(){
		return requirePerm;
	}
	/**
	 * Sets whether a permission is required to activate this Warpstone.
	 * If enabled, players must have warpstones.use.warpstone_name to activate the Warpstone.
	 * <p>
	 * If a player attempts to activate a Warpstone without permission, they see {@link #getDisabledMsg()}, if set.
	 * @param require true to require permission
	 */
	public void setRequirePerm(boolean require){
		requirePerm = require;
		fireEditEvent();
	}

	/**
	 * Gets the condition required to activate this Warpstone.
	 * If set, players must meet the Condition to activate the Warpstone.
	 * <p>
	 * If a player attempts to activate a Warpstone without meeting the condition, they see {@link #getDisabledMsg()}, if set.
	 * @return the condition, or null if one was not set
	 */
	public String getCondition(){
		return condition;
	}
	/**
	 * Sets the condition required to activate this Warpstone.
	 * If set, players must meet the Condition to activate the Warpstone.
	 * <p>
	 * If a player attempts to activate a Warpstone without meeting the condition, they see {@link #getDisabledMsg()}, if set.
	 * @param condition the new condition, or null to clear
	 */
	public void setCondition(String condition){
		this.condition = condition;
		fireEditEvent();
	}


	/**
	 * Generates the Warpstone structure. Requires WorldEdit.
	 */
	public void generateWarpstone(Player player, int size, WarpstoneDesigns design){
		 // Make sure this player has permission to use WE and Warpstones
		if(!player.hasPermission("worldedit.generation.*") || !player.hasPermission("warpstones.manage")){
			player.sendMessage(CommonColors.ERROR+"You can't generate Warpstones! Ask an admin for help.");
			return;
		}

		// Let WE generate the actual stone
		if(Bukkit.getPluginManager().getPlugin("WorldEdit")!=null) WarpstoneWEGeneration.generateWarpstone(player, size, design);
		else player.sendMessage(CommonColors.ERROR+"WorldEdit not installed, could not generate Warpstone!");

		// Command blocks can be generated by Bukkit
		generateWarpstoneCmdBlock(player.getLocation().add(0, -2, -1 -size)); // North
		generateWarpstoneCmdBlock(player.getLocation().add(0, -2, +1 +size)); // South
		generateWarpstoneCmdBlock(player.getLocation().add(+1 +size, -2, 0)); // East
		generateWarpstoneCmdBlock(player.getLocation().add(-1 -size, -2, 0)); // West

		player.sendMessage(CommonColors.MESSAGE+"Warpstone generated.");
		player.teleport(getLocation());
	}
	public void generateWarpstone(Player player, int size){
		// Make sure WE is installed
		if(Bukkit.getPluginManager().getPlugin("WorldEdit")==null){
			player.sendMessage(CommonColors.ERROR+"WorldEdit not installed, could not generate Warpstone!");
			return;
		}
		WarpstoneDesigns design = WarpstoneDesigns.DEFAULT;
		if(WarpstonesConfig.generateBiomeWarpstones){
			// Figure out which warpstone design, based on temperature
			double temperature = getLocation().getBlock().getTemperature();
			// Temp 2.0+ - desert, mesa
			if(temperature>=1.0){
				// If it's a desert, sand warpstone
				Biome biome = getLocation().getBlock().getBiome();
				if(biome.name().contains("DESERT")){
					design = WarpstoneDesigns.SAND;
				}
				// If it's HELL, use Nether style
				else if(biome.equals(Biome.NETHER)){
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
				if(temperature==0.5 && player.getLocation().getBlock().getBiome().equals(Biome.THE_END)){
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
		cmdBlock.setType(Material.COMMAND_BLOCK);

		// Set the command
		BlockState cmdBlockState = cmdBlock.getState();
		((CommandBlock) cmdBlockState).setCommand("/warpstones enter "+identifier+" @p[r=2]");
		cmdBlockState.update(true);

		// Place a pressure plate above
		Block plate = location.add(0, 2, 0).getBlock();
		plate.setType(Material.STONE_PRESSURE_PLATE);
	}


	////// WARPING FUNCTIONALITY
	/**
	 * Activates the Warpstone.
	 * @param player the player activating the Warpstone
	 */
	public void activate(Player player){

		WarpstoneActivateEvent event = new WarpstoneActivateEvent(player, this);

		//// Initial checks
		// If warpstone is disabled, stop and send the player a message
		if(isDisabled()){
			String disabledMsg = (getDisabledMsg()==null) ? CommonColors.ERROR+"This warpstone is inactive" : getDisabledMsg();
			Utils.sendActionBar(player, disabledMsg);
			event.setCancelled(true);
		}

		// If warpstone requires special perms, check for that
		if(requiresPerm() && !player.hasPermission("warpstones.use."+getIdentifier())){
			String disabledMsg = (getDisabledMsg()==null) ? CommonColors.ERROR+"You can't use this warpstone" : getDisabledMsg();
			Utils.sendActionBar(player, disabledMsg);
			event.setCancelled(true);
		}

		// Call activation event
		Bukkit.getServer().getPluginManager().callEvent(event);
		if(event.isCancelled()) return;


		WarpPlayer warpPlayer = new WarpPlayer(player);

		// If player is setting home, set this as their home warpstone
		if(WarpUtils.playersSettingHome.contains(player.getName())){
			WarpUtils.playersSettingHome.remove(player.getName());

			// If this is spawn warpstone, don't set it as home
			if(this.equals(Warpstone.getSpawn())){
				player.sendMessage(CommonColors.MESSAGE+"This is the spawn warpstone, you cannot set it as your home warpstone. Return here with "+ChatColor.BLUE+"/spawn");
				return;
			}

			player.sendMessage(CommonColors.MESSAGE+"Home warpstone saved. Return here with "+ChatColor.BLUE+"/home");
			warpPlayer.setHome(this);
			return;
		}


		//// Prepare a prompt
		Prompt prompt = new Prompt();
		boolean questionSet = false;

		// If this is player's home warpstone, send them to their last
		if(warpPlayer.getHome()!=null && this.identifier.equals(warpPlayer.getHome().getName())){
			prompt.addQuestion("This is your home warpstone. Return here with "+ChatColor.BLUE+"/home");
			questionSet = true;
		} else {
			prompt.addAnswer("Warp home", "warp_home");
		}

		// If this is spawn warpstone
		if(this.equals(Warpstone.getSpawn())){
			prompt.addQuestion("This is the spawn warpstone. Return here with "+ChatColor.BLUE+"/spawn");
			prompt.addQuestion(CommonColors.MESSAGE+"Warpstones allow you to fast travel to previously visited locations.");
			questionSet = true;
		} else {
			prompt.addAnswer("Warp to spawn", "warp_spawn");
		}

		// If this is neither (last warpstone)
		if(!questionSet){
			prompt.addQuestion("Location saved. Return here with "+ChatColor.BLUE+"/warp last");
			warpPlayer.setLast(this);
		} else {
			prompt.addAnswer("Warp to last warpstone", "warp_last");
		}

		prompt.display(player);
		if(getDisplayName()!=null) player.sendTitle("", ChatColor.BLUE+getDisplayName(), -1, -1, -1);
	}


	/**
	 * Fires event to indicate Warpstone has been edited.
	 */
	private void fireEditEvent(){
		Bukkit.getServer().getPluginManager().callEvent(new WarpstoneEditEvent(this));
		WarpstonesPlugin.plugin.saveWarpstones();
	}


	/**
	 * Gets a plugin's data section for this Warpstone.
	 * Plugins can use this to save extra data for this Warpstone.
	 * @param plugin the plugin to get data for
	 * @return the data section
	 */
	public SaveDataSection getData(Plugin plugin){
		ConfigurationSection pluginData = data.getConfigurationSection(plugin.getName());
		if(pluginData==null) pluginData = data.createSection(plugin.getName());
		return new SaveDataSection(pluginData, plugin);
	}
}
