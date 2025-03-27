package com.karasu256.tetriscraft.screen.widget;

import com.karasu256.tetriscraft.game.MinoCondition;
import com.karasu256.tetriscraft.mixin.client.DrawContextAccessor;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;

/**
 * ホールドされているミノを表示するウィジェット
 */
public class HoldPreviewWidget implements Drawable {
    private final int x;
    private final int y;
    private final MinoPreviewWidget previewWidget;
    
    public HoldPreviewWidget(int x, int y) {
        this.x = x;
        this.y = y;
        this.previewWidget = new MinoPreviewWidget(x, y);
    }
    
    /**
     * ホールドされているミノを更新します
     */
    public void updateHoldMino(MinoCondition holdPiece) {
        previewWidget.setMinoType(holdPiece);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // HOLDラベルの描画
        context.drawText(((DrawContextAccessor)(context)).getClient().textRenderer, "HOLD", x, y - 15, 0xFFFFFF, true);
        
        // ホールドミノの描画
        previewWidget.render(context, mouseX, mouseY, delta);
    }
}