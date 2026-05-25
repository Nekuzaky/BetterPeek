package com.betterpeek.client.render;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Immutable view of what should be drawn for a single preview frame.
 *
 * <p>Detectors produce a {@link PreviewSnapshot}; the renderer consumes it
 * and never reaches back into block entities / item NBT. Decoupling the
 * detection side from the render side keeps the latter trivially
 * reusable across the "world container" and "inventory shulker" paths.
 */
public record PreviewSnapshot(
    Text title,
    int columns,
    int rows,
    List<ItemStack> stacks
) {

    public boolean isEmpty() {
        return stacks == null || stacks.isEmpty();
    }
}
