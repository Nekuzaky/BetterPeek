package com.betterpeek.client.detect;

import com.betterpeek.client.cache.ContainerCache;
import com.betterpeek.client.render.PreviewSnapshot;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

/**
 * Looks at whatever the player's crosshair is pointed at and tries to return
 * a {@link PreviewSnapshot} for the container at that position.
 *
 * <p>Detection has two layers, in order:
 *   1. <b>Cached snapshot</b> from a previous open of the same block (see
 *      {@link ContainerCache}). Vanilla doesn't sync block-entity inventories
 *      to the client, so this is the only path that actually returns data
 *      on most servers.
 *   2. <b>Live block-entity inventory</b> as a fallback. This works in a few
 *      edge cases — for example single-player when the integrated server has
 *      already synced the items, or modded servers with eager sync.
 */
public final class WorldContainerDetector {

    public PreviewSnapshot detect(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return null;
        }

        HitResult hit = client.crosshairTarget;
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) {
            return null;
        }

        BlockPos pos = blockHit.getBlockPos();

        // Layer 1: cached snapshot from a previous open.
        PreviewSnapshot cached = ContainerCache.get().get(pos);
        if (cached != null) {
            return cached;
        }

        // Layer 2: try to read live block-entity inventory in case the items
        // were actually synced (e.g. modded servers, certain edge cases).
        BlockEntity blockEntity = client.world.getBlockEntity(pos);
        if (!(blockEntity instanceof Inventory inventory) || inventory.isEmpty()) {
            return null;
        }
        int size = inventory.size();
        java.util.List<net.minecraft.item.ItemStack> stacks = new java.util.ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            stacks.add(inventory.getStack(i));
        }
        int columns = size <= 9 ? size : 9;
        int rows = (size + columns - 1) / columns;
        net.minecraft.text.Text title = blockEntity instanceof net.minecraft.util.Nameable nameable
            ? nameable.getDisplayName()
            : net.minecraft.text.Text.translatable(
                client.world.getBlockState(pos).getBlock().getTranslationKey());

        return new PreviewSnapshot(title, columns, rows, stacks);
    }
}
