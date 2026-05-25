package com.betterpeek;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common-side entry point. BetterPeek is a client-only mod, so this initializer
 * intentionally stays empty — Fabric requires the entry point to exist but all
 * runtime logic lives in {@code com.betterpeek.client.BetterPeekClientMod}.
 */
public final class BetterPeekMod implements ModInitializer {

    public static final String MOD_ID = "betterpeek";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[BetterPeek] common init (client-only mod, see client entrypoint)");
    }
}
