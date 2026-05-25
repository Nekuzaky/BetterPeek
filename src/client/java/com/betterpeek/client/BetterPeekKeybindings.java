package com.betterpeek.client;

import com.betterpeek.BetterPeekMod;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/**
 * Registers a single keybind that toggles the preview overlay on/off.
 *
 * <p>Default key: V. Configurable from vanilla Controls menu under the
 * "BetterPeek" category. We drain wasPressed() so multiple toggles within
 * a tick still register one flip.
 */
public final class BetterPeekKeybindings {

    /**
     * Custom controls category. Use the modern KeyBinding.Category type
     * (1.20.5+) — the legacy String overload was removed.
     */
    private static final KeyBinding.Category CATEGORY =
        KeyBinding.Category.create(Identifier.of(BetterPeekMod.MOD_ID, "main"));

    private KeyBinding togglePreviewKey;

    public void register() {
        togglePreviewKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key." + BetterPeekMod.MOD_ID + ".toggle_preview",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(net.minecraft.client.MinecraftClient client) {
        while (togglePreviewKey.wasPressed()) {
            PreviewState.get().toggle();
        }
    }
}
