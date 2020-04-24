package com.kylenanakdewa.warpstones.items.shards.charged.events;

import com.kylenanakdewa.warpstones.items.shards.charged.ChargedWarpShard;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an event involving a Player and a Charged Warp Shard.
 *
 * @author Kyle Nanakdewa
 */
public abstract class ChargedWarpShardEvent extends PlayerEvent {

    /** The Charged Warp Shard item. */
    private ItemStack item;

    /**
     * Creates the event.
     *
     * @param player the player involved in the event
     * @param item   the Charged Warp Shard item involved in the event
     *
     * @throws IllegalArgumentException if the item is not a Charged Warp Shard
     */
    protected ChargedWarpShardEvent(Player player, ItemStack item) {
        super(player);
        setItem(item);
    }

    /**
     * Gets the Charged Warp Shard item involved in this event.
     *
     * @return the Charged Warp Shard item stack
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * Sets the Charged Warp Shard item involved in this event.
     * <p>
     * To link the shard, you can simply use {@link #link(Warpstone)}, instead of
     * this method.
     *
     * @param item the new Charged Warp Shard item
     *
     * @throws IllegalArgumentException if the item is not a Charged Warp Shard
     */
    public void setItem(ItemStack item) {
        ChargedWarpShard shard = new ChargedWarpShard();

        if (shard.matchesItem(item)) {
            this.item = item;
        } else {
            throw new IllegalArgumentException("Item is not a Charged Warp Shard");
        }
    }

    /**
     * Checks if the Charged Warp Shard is linked.
     *
     * @return true if the Charged Warp Shard is linked
     */
    public boolean isLinked() {
        return new ChargedWarpShard().isLinked(item);
    }

    /**
     * Gets the Location that the Charged Warp Shard is linked to. If the World does
     * not exist, returns null.
     *
     * @return the Location that this Charged Warp Shard is linked to, or null
     */
    public Location getLinkedLocation() {
        return new ChargedWarpShard().getLinkedLocation(item);
    }

    /**
     * Links the Charged Warp Shard to the specified Location. If the shard is
     * already linked, it will be returned as-is.
     *
     * @param warpstone the Location to link this shard to
     */
    public void link(Location location) {
        item = new ChargedWarpShard().link(item, location);
    }

}