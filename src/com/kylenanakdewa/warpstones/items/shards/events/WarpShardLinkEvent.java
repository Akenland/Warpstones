package com.kylenanakdewa.warpstones.items.shards.events;

import com.kylenanakdewa.warpstones.Warpstone;
import com.kylenanakdewa.warpstones.events.WarpstoneActivateEvent;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Fired when a Warp Shard is linked by a Player.
 *
 * @author Kyle Nanakdewa
 */
public class WarpShardLinkEvent extends WarpShardEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;

    /** The WarpstoneActivateEvent that triggered this event. */
    private final WarpstoneActivateEvent warpstoneActivateEvent;

    /** The Warpstone that this shard has been linked to. */
    private Warpstone linkedWarpstone;

    public WarpShardLinkEvent(WarpstoneActivateEvent warpstoneActivateEvent, ItemStack item) {
        super(warpstoneActivateEvent.getPlayer(), item);
        this.warpstoneActivateEvent = warpstoneActivateEvent;

        linkedWarpstone = warpstoneActivateEvent.getWarpstone();
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
     * Gets the Warpstone that the shard has been linked to.
     * <p>
     * The Warpstone may be null. If this is the case, the event will be cancelled.
     *
     * @return the Warpstone involved in this event
     */
    public Warpstone getWarpstone() {
        return linkedWarpstone;
    }

    /**
     * Sets the Warpstone that the shard will be linked to.
     * <p>
     * If the Warpstone is set to null, the event will be cancelled.
     *
     * @param warpstone the Warpstone to link this shard to
     */
    public void setWarpstone(Warpstone warpstone) {
        linkedWarpstone = warpstone;
    }

}