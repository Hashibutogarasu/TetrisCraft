package com.karasu256.tetriscraft.screen.widget;

import com.karasu256.tetriscraft.mixin.client.DrawContextAccessor;

import com.karasu256.tetriscraft.game.MinoGenerator;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;

/**
 * 次に出現するミノを表示するウィジェット
 */
public class NextPreviewWidget implements Drawable {
    private static final int PREVIEW_COUNT = 5; // 表示するNEXTの数
    private static final int PREVIEW_SPACING = (BoardWidget.DOT_SIZE + BoardWidget.DOT_SPACING) * (MinoPreviewWidget.PREVIEW_SIZE + 1); // プレビュー間の縦方向の間隔（1個分の余白を含む）
    private final int x;
    private final int y;
    private final MinoPreviewWidget[] previewWidgets;
    
    public NextPreviewWidget(int x, int y) {
        this.x = x;
        this.y = y;
        this.previewWidgets = new MinoPreviewWidget[PREVIEW_COUNT];
        
        // プレビューウィジェットを初期化（1個分の余白を空けて配置）
        for (int i = 0; i < PREVIEW_COUNT; i++) {
            previewWidgets[i] = new MinoPreviewWidget(x, y + i * PREVIEW_SPACING);
        }
    }
    
    /**
     * NEXTミノのリストを更新します
     */
    public void updateNextMinos(MinoGenerator generator) {
        for (int i = 0; i < PREVIEW_COUNT; i++) {
            previewWidgets[i].setMinoType(generator.peekNextMino(i));
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // NEXTラベルの描画
        context.drawText(((DrawContextAccessor)(context)).getClient().textRenderer, "NEXT", x, y - 15, 0xFFFFFF, true);
        
        // 各プレビューの描画
        for (MinoPreviewWidget widget : previewWidgets) {
            widget.render(context, mouseX, mouseY, delta);
        }
    }
}