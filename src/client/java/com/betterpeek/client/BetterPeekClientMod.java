package com.betterpeek.client;

import com.betterpeek.BetterPeekMod;
import com.betterpeek.client.render.ContainerPreviewRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

/**
 * Client entry point. Wires the preview renderer into the HUD pipeline and
 * delegates all detection / rendering to the renderer itself so this class
 * stays a thin coordinator.
 */
public final class BetterPeekClientMod implements ClientModInitializer {

    private ContainerPreviewRenderer renderer;

    @Override
    public void onInitializeClient() {
        BetterPeekMod.LOGGER.info("[BetterPeek] client init");
        this.renderer = new ContainerPreviewRenderer();
        HudRenderCallback.EVENT.register(this.renderer::render);
    }
}
