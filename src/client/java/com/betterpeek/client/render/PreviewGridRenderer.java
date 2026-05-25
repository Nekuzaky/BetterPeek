package com.betterpeek.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Pure rendering of a {@link PreviewSnapshot} into a {@link DrawContext}.
 *
 * <p>Knows nothing about where the snapshot came from; the calling site
 * decides the anchor position (top-right corner for world containers,
 * near the cursor for inventory shulkers).
 *
 * <p>Layout:
 *   - 1 px padding around a translucent dark background
 *   - title text on top (item-style, light gray)
 *   - a 16x16 cell per slot, with vanilla item rendering (icons, count)
 */
public final class PreviewGridRenderer {

    private static final int CELL_SIZE = 16;
    private static final int PADDING = 4;
    private static final int TITLE_HEIGHT = 12;
    private static final int BACKGROUND_COLOR = 0xE0100010;
    private static final int BORDER_COLOR = 0xFF505050;
    private static final int SLOT_COLOR = 0xFF2A2A2A;
    private static final int TITLE_COLOR = 0xFFFFD080;

    /**
     * Renders the snapshot. Returns the bounding box width/height as a
     * packed long ({@code (width << 32) | height}) so the caller can
     * lay multiple snapshots out without re-measuring.
     */
    public long render(DrawContext context, PreviewSnapshot snapshot, int anchorX, int anchorY) {
        if (snapshot == null || snapshot.isEmpty()) {
            return 0L;
        }

        int columns = Math.max(1, snapshot.columns());
        int rows = Math.max(1, snapshot.rows());
        int gridWidth = columns * CELL_SIZE;
        int gridHeight = rows * CELL_SIZE;
        int totalWidth = gridWidth + PADDING * 2;
        int totalHeight = gridHeight + TITLE_HEIGHT + PADDING * 2;

        // Background panel.
        context.fill(anchorX, anchorY, anchorX + totalWidth, anchorY + totalHeight, BACKGROUND_COLOR);
        // Outline.
        context.drawStrokedRectangle(anchorX, anchorY, totalWidth, totalHeight, BORDER_COLOR);

        // Title.
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        Text title = snapshot.title() != null ? snapshot.title() : Text.empty();
        context.drawText(textRenderer, title, anchorX + PADDING, anchorY + PADDING, TITLE_COLOR, false);

        // Slot grid.
        int gridX = anchorX + PADDING;
        int gridY = anchorY + PADDING + TITLE_HEIGHT;
        int slotIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int cellX = gridX + col * CELL_SIZE;
                int cellY = gridY + row * CELL_SIZE;
                context.fill(cellX, cellY, cellX + CELL_SIZE, cellY + CELL_SIZE, SLOT_COLOR);
                if (slotIndex < snapshot.stacks().size()) {
                    ItemStack stack = snapshot.stacks().get(slotIndex);
                    if (!stack.isEmpty()) {
                        context.drawItem(stack, cellX, cellY);
                        // drawStackOverlay renders the count + damage bar — the
                        // 1.21 successor of the legacy drawItemInSlot().
                        context.drawStackOverlay(textRenderer, stack, cellX, cellY);
                    }
                }
                slotIndex++;
            }
        }

        return (((long) totalWidth) << 32) | (totalHeight & 0xFFFFFFFFL);
    }
}
