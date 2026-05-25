package com.betterpeek.client.cache;

import com.betterpeek.client.render.PreviewSnapshot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Remembers the contents of containers the player has recently opened, keyed
 * by the world {@link BlockPos}.
 *
 * <p>Vanilla doesn't sync block-entity inventories to the client until the
 * player opens the container — so a client-only mod can't know what's inside
 * a chest by just looking at it. We side-step that by capturing the slot
 * stacks the moment a {@link net.minecraft.client.gui.screen.ingame.HandledScreen}
 * opens (when the items are necessarily present client-side, because the GUI
 * needs to render them), and re-using that snapshot when the player later
 * peeks the same block.
 *
 * <p>The cache is intentionally process-local and unbounded in API but the
 * caller is expected to {@link #clear()} when the world changes.
 */
public final class ContainerCache {

    private static final ContainerCache INSTANCE = new ContainerCache();

    /** Maximum number of remembered containers before we evict the oldest. */
    private static final int CAPACITY = 256;

    private final Map<BlockPos, Entry> entries = new HashMap<>();

    private ContainerCache() {
    }

    public static ContainerCache get() {
        return INSTANCE;
    }

    public void put(BlockPos pos, Text title, int columns, int rows, List<ItemStack> stacks) {
        if (entries.size() >= CAPACITY) {
            // Drop one arbitrary entry to keep memory bounded. HashMap
            // iteration order is fine for this — we just want it gone.
            BlockPos victim = entries.keySet().iterator().next();
            entries.remove(victim);
        }
        // Defensive copy: the slots we receive are live ItemStacks from the
        // open screen handler; we don't want them mutating under us.
        List<ItemStack> snapshot = stacks.stream().map(ItemStack::copy).toList();
        entries.put(pos.toImmutable(), new Entry(title, columns, rows, snapshot));
    }

    public PreviewSnapshot get(BlockPos pos) {
        Entry entry = entries.get(pos);
        if (entry == null) {
            return null;
        }
        return new PreviewSnapshot(entry.title, entry.columns, entry.rows, entry.stacks);
    }

    public void clear() {
        entries.clear();
    }

    private record Entry(Text title, int columns, int rows, List<ItemStack> stacks) {
    }
}
