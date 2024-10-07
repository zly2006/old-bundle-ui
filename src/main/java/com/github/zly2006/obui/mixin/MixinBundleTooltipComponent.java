package com.github.zly2006.obui.mixin;

import com.github.zly2006.obui.SlotSprite;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("OverwriteAuthorRequired")
@Mixin(BundleTooltipComponent.class)
public class MixinBundleTooltipComponent implements TooltipComponent {
    @Shadow @Final private BundleContentsComponent bundleContents;
    @Shadow @Final private static Identifier BUNDLE_SLOT_HIGHLIGHT_FRONT_TEXTURE;
    @Shadow @Final private static Identifier BUNDLE_SLOT_HIGHLIGHT_BACK_TEXTURE;
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of("bundle_ui", "container/bundle/background");

    @Overwrite
    @Override
    public int getHeight(TextRenderer textRenderer) {
        return this.getRowsHeight() + 4;
    }

    @Overwrite
    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.getColumnsWidth();
    }

    private int getColumnsWidth() {
        return this.getColumns() * 18 + 2;
    }

    private int getRowsHeight() {
        return this.getRows() * 20 + 2;
    }

    @Overwrite
    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        int i = this.getColumns();
        int j = this.getRows();
        context.drawGuiTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, x, y, this.getColumnsWidth(), this.getRowsHeight());
        boolean bl = this.bundleContents.getOccupancy().compareTo(Fraction.ONE) >= 0;
        int k = 0;

        for (int l = 0; l < j; l++) {
            for (int m = 0; m < i; m++) {
                int n = x + m * 18 + 1;
                int o = y + l * 20 + 1;
                this.drawSlot(n, o, k++, bl, context, textRenderer);
            }
        }

        if (!this.bundleContents.isEmpty()) {
            this.drawSelectedItemTooltip(textRenderer, context, x, y, width);
        }
    }

    private void drawSlot(int x, int y, int index, boolean shouldBlock, DrawContext context, TextRenderer textRenderer) {
        if (index >= this.bundleContents.size()) {
            this.draw(context, x, y, shouldBlock ? SlotSprite.BLOCKED_SLOT : SlotSprite.SLOT);
        } else {
            ItemStack itemStack = this.bundleContents.get(index);
            this.draw(context, x, y, SlotSprite.SLOT);

            if (index == this.bundleContents.getSelectedStackIndex()) {
                context.drawGuiTexture(RenderLayer::getGuiTextured, BUNDLE_SLOT_HIGHLIGHT_BACK_TEXTURE, x, y, 18, 18);
            }
            context.drawItem(itemStack, x + 1, y + 1, index);
            context.drawStackOverlay(textRenderer, itemStack, x + 1, y + 1);
            if (index == this.bundleContents.getSelectedStackIndex()) {
                context.drawGuiTexture(RenderLayer::getGuiTexturedOverlay, BUNDLE_SLOT_HIGHLIGHT_FRONT_TEXTURE, x, y, 18, 18);
            }
        }
    }

    private void drawSelectedItemTooltip(TextRenderer textRenderer, DrawContext drawContext, int x, int y, int width) {
        if (this.bundleContents.hasSelectedStack()) {
            ItemStack itemStack = this.bundleContents.get(this.bundleContents.getSelectedStackIndex());
            Text text = itemStack.getFormattedName();
            int i = textRenderer.getWidth(text.asOrderedText());
            int j = x + width / 2 - 12;
            drawContext.drawTooltip(textRenderer, text, j - i / 2, y - 15, itemStack.get(DataComponentTypes.TOOLTIP_STYLE));
        }
    }

    private void draw(DrawContext context, int x, int y, SlotSprite sprite) {
        context.drawGuiTexture(RenderLayer::getGuiTextured, sprite.texture, x, y, sprite.width, sprite.height);
    }

    private int getColumns() {
        return Math.max(2, (int) Math.ceil(Math.sqrt(this.bundleContents.size() + 1.0)));
    }

    private int getRows() {
        return (int) Math.ceil((this.bundleContents.size() + 1.0) / this.getColumns());
    }
}
