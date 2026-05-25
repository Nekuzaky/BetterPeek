package com.betterpeek.client;

import com.betterpeek.BetterPeekMod;
import com.betterpeek.client.render.ContainerPreviewRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

/**
 * Client entry point. Wires the preview renderer into the HUD pipeline
 * and registers the toggle keybind.
 *
 * <p>Both the world-container preview and the inventory-shulker preview
 * are driven by the same {@link ContainerPreviewRenderer} from the HUD
 * callback — Fabric's HUD callback fires while any screen is open too,
 * so we don't need a separate ScreenEvents hook.
 */
public final class BetterPeekClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BetterPeekMod.LOGGER.info("[BetterPeek] client init");

        BetterPeekKeybindings keybindings = new BetterPeekKeybindings();
        keybindings.register();

        ContainerPreviewRenderer renderer = new ContainerPreviewRenderer();
        HudRenderCallback.EVENT.register(renderer::render);
    }
}
