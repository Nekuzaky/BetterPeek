package com.betterpeek.client.cache;

import com.betterpeek.BetterPeekMod;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Watches for {@link HandledScreen} openings and, when the player has the
 * crosshair pointed at a block at that moment, captures the visible
 * container-side slot stacks into {@link ContainerCache}.
 *
 * <p>"Container-side" means slots whose backing inventory is not the
 * player's own — we drop everything from {@link PlayerInventory} so we only
 * cache the chest/barrel/shulker grid, never the player's hotbar.
 *
 * <p>The cache is wiped on disconnect to avoid leaking snapshots from a
 * previous world into the next one.
 */
public final class ContainerCachingHook {

    public void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof HandledScreen<?> handled)) {
                return;
            }
            BlockPos pos = lookedAtBlockPos(client);
            if (pos == null) {
                return;
            }
            // Defer the actual capture to a single render-cycle later: the
            // ScreenHandler exists at AFTER_INIT but slot stacks may still be
            // empty for one tick while the server pushes the initial sync.
            // Hooking on the first remove() gives us the populated state.
            ScreenEvents.afterTick(handled).register(s -> {
                if (!entriesAreEmpty(handled.getScreenHandler())) {
                    capture(handled, pos);
                }
            });
        });

        // Wipe the cache between worlds so we don't show stale snapshots from
        // the previous server / save.
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ContainerCache.get().clear());
    }

    private static BlockPos lookedAtBlockPos(MinecraftClient client) {
        HitResult hit = client.crosshairTarget;
        if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
            return blockHit.getBlockPos();
        }
        return null;
    }

    private static boolean entriesAreEmpty(ScreenHandler handler) {
        for (Slot slot : handler.slots) {
            if (slot.inventory instanceof PlayerInventory) {
                break;
            }
            if (!slot.getStack().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static void capture(HandledScreen<?> screen, BlockPos pos) {
        ScreenHandler handler = screen.getScreenHandler();
        List<ItemStack> containerStacks = new ArrayList<>();
        for (Slot slot : handler.slots) {
            if (slot.inventory instanceof PlayerInventory) {
                break;
            }
            containerStacks.add(slot.getStack());
        }
        if (containerStacks.isEmpty()) {
            return;
        }

        int slotCount = containerStacks.size();
        int columns = slotCount <= 9 ? slotCount : 9;
        int rows = (slotCount + columns - 1) / columns;
        Text title = screen.getTitle();

        BetterPeekMod.LOGGER.debug("[BetterPeek] cached {} slots at {}", slotCount, pos);
        ContainerCache.get().put(pos, title, columns, rows, containerStacks);
    }
}
