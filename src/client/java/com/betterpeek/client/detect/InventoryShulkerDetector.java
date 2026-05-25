package com.betterpeek.client.detect;

import com.betterpeek.client.mixin.HandledScreenAccessor;
import com.betterpeek.client.render.PreviewSnapshot;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects when the player is hovering a shulker-box item inside any
 * {@link HandledScreen} (player inventory, a chest GUI, creative, anvil…)
 * and produces a preview of the shulker's stored contents.
 *
 * <p>Since Minecraft 1.20.5 shulker contents live on the {@link
 * DataComponentTypes#CONTAINER} component of the {@link ItemStack}, so
 * the items are always available client-side — even on locked-down
 * vanilla servers where world containers stay opaque to the client.
 */
public final class InventoryShulkerDetector {

    public PreviewSnapshot detect(MinecraftClient client) {
        Screen screen = client.currentScreen;
        if (!(screen instanceof HandledScreen<?> handled)) {
            return null;
        }

        Slot hovered = ((HandledScreenAccessor) handled).betterpeek$getFocusedSlot();
        if (hovered == null) {
            return null;
        }

        ItemStack stack = hovered.getStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem blockItem)
            || !(blockItem.getBlock() instanceof ShulkerBoxBlock)) {
            return null;
        }

        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        if (container == null || container.equals(ContainerComponent.DEFAULT)) {
            return null;
        }

        // Materialize the 27 slots, padding with EMPTY so the renderer always
        // gets a full 9x3 grid even for sparsely-filled shulkers.
        List<ItemStack> stored = new ArrayList<>(ShulkerBoxBlockEntity.INVENTORY_SIZE);
        container.stream().limit(ShulkerBoxBlockEntity.INVENTORY_SIZE).forEach(stored::add);
        while (stored.size() < ShulkerBoxBlockEntity.INVENTORY_SIZE) {
            stored.add(ItemStack.EMPTY);
        }

        int columns = 9;
        int rows = ShulkerBoxBlockEntity.INVENTORY_SIZE / columns; // = 3
        Text title = stack.getName();

        return new PreviewSnapshot(title, columns, rows, stored);
    }
}
