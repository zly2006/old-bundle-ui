package com.github.zly2006.obui;

import net.minecraft.util.Identifier;

public enum SlotSprite {
    BLOCKED_SLOT(Identifier.of("bundle_ui", "container/bundle/blocked_slot"), 18, 20),
    SLOT(Identifier.of("bundle_ui", "container/bundle/slot"), 18, 20);

    public final Identifier texture;
    public final int width;
    public final int height;

    private SlotSprite(final Identifier texture, final int width, final int height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }
}
