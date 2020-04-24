package com.kylenanakdewa.warpstones.items.shards.events;

import com.kylenanakdewa.warpstones.items.shards.WarpShard;
import com.kylenanakdewa.warpstones.warpstone.Warpstone;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an event involving a Player and a Warp Shard.
 *
 * @author Kyle Nanakdewa
 */
public abstract class WarpShardEvent extends PlayerEvent {

    /** The Warp Shard item. */
    private ItemStack item;

    /**
     * Creates the event.
     *
     * @param player the player involved in the event
     * @param item   the Warp Shard item involved in the event
     *
     * @throws IllegalArgumentException if the item is not a Warp Shard
     */
    protected WarpShardEvent(Player player, ItemStack item) {
        super(player);
        setItem(item);
    }

    /**
     * Gets the Warp Shard item involved in this event.
     *
     * @return the Warp Shard item stack
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * Sets the Warp Shard item involved in this event.
     * <p>
     * To link the shard, you can simply use {@link #link(String)} or
     * {@link #link(Warpstone)}, instead of this method.
     *
     * @param item the new Warp Shard item
     *
     * @throws IllegalArgumentException if the item is not a Warp Shard
     */
    public void setItem(ItemStack item) {
        WarpShard shard = new WarpShard();

        if (shard.matchesItem(item)) {
            this.item = item;
        } else {
            throw new IllegalArgumentException("Item is not a Warp Shard");
        }
    }

    /**
     * Checks if the Warp Shard is linked.
     *
     * @return true if the Warp Shard is linked
     */
    public boolean isLinked() {
        return new WarpShard().isLinked(item);
    }

    /**
     * Gets the Warpstone that the Warp Shard is linked to. If the Warpstone does
     * not exist, returns null.
     *
     * @return the Warpstone that this Warp Shard is linked to, or null
     */
    public Warpstone getLinkedWarpstone() {
        return new WarpShard().getLinkedWarpstone(item);
    }

    /**
     * Links the Warp Shard to the specified Warpstone. If the shard is already
     * linked, it will be returned as-is.
     *
     * @param warpstone the Warpstone to link this shard to
     */
    public void link(Warpstone warpstone) {
        item = new WarpShard().link(item, warpstone);
    }

}