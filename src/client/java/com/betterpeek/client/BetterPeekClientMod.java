package com.betterpeek.client;

import com.betterpeek.BetterPeekMod;
import com.betterpeek.client.cache.ContainerCachingHook;
import com.betterpeek.client.render.ContainerPreviewRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

/**
 * Client entry point. Wires:
 *   1. The toggle keybind.
 *   2. The container-opening cache hook (so we can preview chests after the
 *      player has opened them at least once).
 *   3. The two render callbacks — HUD for the world view, ScreenEvents.afterRender
 *      for the on-top-of-screen view.
 */
public final class BetterPeekClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BetterPeekMod.LOGGER.info("[BetterPeek] client init");

        new BetterPeekKeybindings().register();
        new ContainerCachingHook().register();

        ContainerPreviewRenderer renderer = new ContainerPreviewRenderer();

        HudRenderCallback.EVENT.register(renderer::renderHud);

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.afterRender(screen).register(renderer::renderScreen);
        });
    }
}
