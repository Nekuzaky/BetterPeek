package com.betterpeek.client.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

/**
 * Renders the container preview overlay near the crosshair.
 *
 * <p>This is the v0.1 stub. Subsequent commits will:
 *   1. Detect the container the player is currently looking at (raycast for
 *      world containers, slot under cursor for inventory shulkers).
 *   2. Read the container's items (BlockEntity NBT for world, ItemStack NBT
 *      for shulkers in inventory).
 *   3. Render a small grid of item icons + counts here.
 */
public final class ContainerPreviewRenderer {

    public void render(DrawContext context, RenderTickCounter tickCounter) {
        // Intentionally empty in v0.1 scaffolding — implementation lands in
        // the next commit (container detection + grid render).
    }
}
