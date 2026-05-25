package com.betterpeek.client.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes {@code HandledScreen#focusedSlot} — the slot currently under the
 * mouse cursor — so the inventory-shulker preview can read it without
 * reflection.
 */
@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {

    @Accessor("focusedSlot")
    Slot betterpeek$getFocusedSlot();
}
