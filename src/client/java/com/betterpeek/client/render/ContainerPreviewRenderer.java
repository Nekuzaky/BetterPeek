package com.betterpeek.client.render;

import com.betterpeek.client.PreviewState;
import com.betterpeek.client.detect.InventoryShulkerDetector;
import com.betterpeek.client.detect.WorldContainerDetector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;

/**
 * Top-level overlay coordinator with two entry points:
 *
 * <ul>
 *   <li>{@link #renderHud} — called from {@code HudRenderCallback} when the
 *       player is looking at the world (no screen open). Shows the world
 *       container preview anchored top-right.</li>
 *   <li>{@link #renderScreen} — called from {@code ScreenEvents.afterRender}
 *       after the active screen has finished rendering. Shows the shulker
 *       preview near the mouse cursor, on top of the screen.</li>
 * </ul>
 *
 * <p>Splitting the two callbacks is necessary because {@code HudRenderCallback}
 * fires before the screen is rendered — anything we draw from there sits
 * underneath the inventory's dim overlay and is invisible to the player.
 */
public final class ContainerPreviewRenderer {

    private static final int SCREEN_MARGIN = 8;

    private final WorldContainerDetector worldDetector = new WorldContainerDetector();
    private final InventoryShulkerDetector inventoryDetector = new InventoryShulkerDetector();
    private final PreviewGridRenderer gridRenderer = new PreviewGridRenderer();

    /** HUD path: visible only when no screen is open. */
    public void renderHud(DrawContext context, RenderTickCounter tickCounter) {
        if (!PreviewState.get().enabled()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) {
            return;
        }
        if (client.currentScreen != null) {
            // Screens are handled by the afterRender path; bail to avoid
            // double-drawing under the dim overlay.
            return;
        }

        PreviewSnapshot snapshot = worldDetector.detect(client);
        if (snapshot == null || snapshot.isEmpty()) {
            return;
        }

        int approxWidth = Math.max(1, snapshot.columns()) * 16 + 8;
        int anchorX = context.getScaledWindowWidth() - approxWidth - SCREEN_MARGIN;
        int anchorY = SCREEN_MARGIN;
        gridRenderer.render(context, snapshot, anchorX, anchorY);
    }

    /** Screen path: visible on top of the active screen. */
    public void renderScreen(Screen screen, DrawContext context, int mouseX, int mouseY, float tickDelta) {
        if (!PreviewState.get().enabled()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }

        PreviewSnapshot snapshot = inventoryDetector.detect(client);
        if (snapshot == null || snapshot.isEmpty()) {
            return;
        }

        int approxWidth = Math.max(1, snapshot.columns()) * 16 + 8;
        int approxHeight = Math.max(1, snapshot.rows()) * 16 + 20;
        // Anchor: above-right of the cursor, clamped inside the screen bounds.
        int anchorX = Math.min(mouseX + 12, context.getScaledWindowWidth() - approxWidth - SCREEN_MARGIN);
        int anchorY = Math.max(mouseY - approxHeight - 4, SCREEN_MARGIN);
        gridRenderer.render(context, snapshot, anchorX, anchorY);
    }
}
