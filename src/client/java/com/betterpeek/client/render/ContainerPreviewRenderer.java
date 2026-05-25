package com.betterpeek.client.render;

import com.betterpeek.client.PreviewState;
import com.betterpeek.client.detect.InventoryShulkerDetector;
import com.betterpeek.client.detect.WorldContainerDetector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

/**
 * Top-level HUD overlay coordinator.
 *
 * <p>On every HUD frame:
 *   1. Bail if the preview state is disabled or the player is paused / on
 *      the title screen.
 *   2. If the player has an inventory-style screen open, ask the shulker
 *      detector; otherwise raycast for a world container.
 *   3. Hand the resulting snapshot (if any) to {@link PreviewGridRenderer}.
 *
 * <p>Detectors are instantiated once and reused (no per-frame allocation).
 */
public final class ContainerPreviewRenderer {

    private static final int SCREEN_MARGIN = 8;

    private final WorldContainerDetector worldDetector = new WorldContainerDetector();
    private final InventoryShulkerDetector inventoryDetector = new InventoryShulkerDetector();
    private final PreviewGridRenderer gridRenderer = new PreviewGridRenderer();

    public void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!PreviewState.get().enabled()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) {
            return;
        }

        PreviewSnapshot snapshot;
        boolean fromInventory = client.currentScreen != null;
        if (fromInventory) {
            snapshot = inventoryDetector.detect(client);
        } else {
            snapshot = worldDetector.detect(client);
        }
        if (snapshot == null || snapshot.isEmpty()) {
            return;
        }

        int screenWidth = context.getScaledWindowWidth();
        // Anchor: top-right for world previews (don't block crosshair / hand);
        // near the top-left for inventory previews (avoid the slot tooltip area).
        int anchorX;
        int anchorY = SCREEN_MARGIN;
        if (fromInventory) {
            anchorX = SCREEN_MARGIN;
        } else {
            int approxWidth = Math.max(1, snapshot.columns()) * 16 + 8;
            anchorX = screenWidth - approxWidth - SCREEN_MARGIN;
        }
        gridRenderer.render(context, snapshot, anchorX, anchorY);
    }
}
