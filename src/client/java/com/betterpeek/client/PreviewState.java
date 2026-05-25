package com.betterpeek.client;

/**
 * Single source of truth for "is the preview overlay enabled right now?".
 *
 * <p>Mutable singleton because the toggle is bound to a global keybind and
 * the renderer needs to query it on every frame. Keeping it here (rather
 * than scattered booleans on the renderer/detector) lets us add persistence
 * later in one place.
 */
public final class PreviewState {

    private static final PreviewState INSTANCE = new PreviewState();

    private boolean enabled = true;

    private PreviewState() {
    }

    public static PreviewState get() {
        return INSTANCE;
    }

    public boolean enabled() {
        return enabled;
    }

    public void toggle() {
        enabled = !enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
