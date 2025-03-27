package com.karasu256.tetriscraft.screen.widget;

import com.karasu256.tetriscraft.game.MinoCondition;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.render.RenderLayer;

public class DotWidget implements Drawable {
    private final int x;
    private final int x_offset;
    private final int y;
    private final int y_offset;

    private MinoCondition condition;

    public DotWidget(int x, int x_offset, int y, int y_offset) {
        this(x, x_offset, y, y_offset, MinoCondition.EMPTY);
    }

    public DotWidget(int x, int x_offset, int y, int y_offset, MinoCondition condition) {
        this.x = x;
        this.x_offset = x_offset;
        this.y = y;
        this.y_offset = y_offset;
        this.condition = condition;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // ゴースト表示の場合は半透明で描画
        float alpha = condition.isGhost() ? 0.5f : 1.0f;

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        
        context.drawTexture(
                RenderLayer::getGuiTextured,
                condition.getTexture(),
                x + x_offset,
                y + y_offset,
                0,
                0,
                12,
                12,
                32,
                32,
                32,
                32);
                
        // 色設定をリセット
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setCondition(MinoCondition condition) {
        this.condition = condition;
    }

    public MinoCondition getCondition() {
        return condition;
    }
}
