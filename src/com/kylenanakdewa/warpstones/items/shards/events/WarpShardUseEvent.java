package com.kylenanakdewa.warpstones.items.shards.events;

import java.util.Random;

import com.kylenanakdewa.warpstones.Warpstone;
import com.kylenanakdewa.warpstones.events.WarpstoneActivateEvent;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Fired when a Warp Shard is used by a Player.
 * <p>
 * This event will fire as cancelled if the Warp Shard was used at the Warpstone
 * where it was linked, or if the Warpstone could not be found.
 *
 * @author Kyle Nanakdewa
 */
public class WarpShardUseEvent extends WarpShardEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;

    /** The WarpstoneActivateEvent that triggered this event. */
    private final WarpstoneActivateEvent warpstoneActivateEvent;

    /** The destination Warpstone. */
    private Warpstone destinationWarpstone;

    /** Whether the Warp Shard will break (be consumed). */
    private boolean consumeShard;

    public WarpShardUseEvent(WarpstoneActivateEvent warpstoneActivateEvent, ItemStack item) {
        super(warpstoneActivateEvent.getPlayer(), item);
        this.warpstoneActivateEvent = warpstoneActivateEvent;

        destinationWarpstone = getLinkedWarpstone();

        if (destinationWarpstone == null || destinationWarpstone.equals(warpstoneActivateEvent.getWarpstone())) {
            cancelled = true;
        }

        setConsumeShardAutomatic();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    /**
     * Gets the WarpstoneActivateEvent that triggered this event.
     *
     * @return the event data for the Warpstone that was activated
     */
    public WarpstoneActivateEvent getWarpstoneActivateEvent() {
        return warpstoneActivateEvent;
    }

    /**
     * Gets the destination Warpstone, that the player will be warped to. Defaults
     * to {@link #getLinkedWarpstone()}.
     * <p>
     * The destination Warpstone may be null. If this is the case, the event will be
     * cancelled.
     *
     * @return the destination Warpstone
     */
    public Warpstone getDestinationWarpstone() {
        return destinationWarpstone;
    }

    /**
     * Sets the destination Warpstone, that the player will be warped to.
     * <p>
     * If the destination Warpstone is set to null, the event will be cancelled.
     *
     * @param warpstone the Warpstone that the player should be warped to
     */
    public void setDestinationWarpstone(Warpstone warpstone) {
        destinationWarpstone = warpstone;
    }

    /**
     * Checks if the Warp Shard will be consumed and break.
     * <p>
     * If the Warp Shard is not enchanted with Unbreaking, this defaults to true.
     * <p>
     * If the Warp Shard is enchanted with Unbreaking, this value will be set
     * randomly, based on the level of the enchantment.
     *
     * @return true if the shard will be consumed and break
     */
    public boolean willConsumeShard() {
        return consumeShard;
    }

    /**
     * Sets whether the Warp Shard will be consumed and break.
     *
     * @param consumeShard true to make the shard be consumed and break
     */
    public void setConsumeShard(boolean consumeShard) {
        this.consumeShard = consumeShard;
    }

    /**
     * Determines whether the Warp Shard should be consumed and break.
     *
     * If the Warp Shard is not enchanted with Unbreaking, this will set true.
     * <p>
     * If the Warp Shard is enchanted with Unbreaking, this value will be set
     * randomly, based on the level of the enchantment.
     */
    public void setConsumeShardAutomatic() {
        int unbreakingLevel = getItem().getEnchantmentLevel(Enchantment.DURABILITY);

        if (unbreakingLevel == 0) {
            consumeShard = true;
        } else {
            // Each level grants 20% chance of shard not being consumed
            // Chance will be 2, 4, or 6
            int chance = unbreakingLevel * 2;
            consumeShard = chance < new Random().nextInt(10) + 1;
        }
    }

}