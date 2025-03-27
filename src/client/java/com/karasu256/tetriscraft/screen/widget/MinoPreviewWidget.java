package com.karasu256.tetriscraft.screen.widget;

import com.karasu256.tetriscraft.game.GameCondition;
import com.karasu256.tetriscraft.game.MinoCondition;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;

/**
 * HOLDやNEXT表示用の小さなミノプレビューウィジェット
 */
public class MinoPreviewWidget implements Drawable {
    public static final int PREVIEW_SIZE = 5; // プレビューボードのサイズを5x5に
    private final int x;
    private final int y;
    private MinoCondition minoType;
    private final BoardWidget previewBoard;
    
    public MinoPreviewWidget(int x, int y) {
        this.x = x;
        this.y = y;
        this.minoType = null;
        this.previewBoard = new BoardWidget(GameCondition.PLAYING, x, y, PREVIEW_SIZE, PREVIEW_SIZE);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (minoType == null) {
            return;
        }

        previewBoard.clearBoard();
        
        // ミノタイプをコピーして通常表示に設定
        MinoCondition displayMino = minoType.copy();
        displayMino.setGhost(false);
        
        // ミノタイプに応じてプレビューボードの中央に配置
        switch (minoType) {
            case I_MINO:
                for (int i = 0; i < 4; i++) {
                    previewBoard.setMino(i + 1, 2, displayMino);
                }
                break;
            case O_MINO:
                previewBoard.setMino(2, 1, displayMino);
                previewBoard.setMino(3, 1, displayMino);
                previewBoard.setMino(2, 2, displayMino);
                previewBoard.setMino(3, 2, displayMino);
                break;
            case T_MINO:
                previewBoard.setMino(1, 2, displayMino);
                previewBoard.setMino(2, 2, displayMino);
                previewBoard.setMino(3, 2, displayMino);
                previewBoard.setMino(2, 3, displayMino);
                break;
            case S_MINO:
                previewBoard.setMino(2, 2, displayMino);
                previewBoard.setMino(3, 2, displayMino);
                previewBoard.setMino(1, 3, displayMino);
                previewBoard.setMino(2, 3, displayMino);
                break;
            case Z_MINO:
                previewBoard.setMino(1, 2, displayMino);
                previewBoard.setMino(2, 2, displayMino);
                previewBoard.setMino(2, 3, displayMino);
                previewBoard.setMino(3, 3, displayMino);
                break;
            case J_MINO:
                previewBoard.setMino(1, 2, displayMino);
                previewBoard.setMino(1, 3, displayMino);
                previewBoard.setMino(2, 3, displayMino);
                previewBoard.setMino(3, 3, displayMino);
                break;
            case L_MINO:
                previewBoard.setMino(3, 2, displayMino);
                previewBoard.setMino(1, 3, displayMino);
                previewBoard.setMino(2, 3, displayMino);
                previewBoard.setMino(3, 3, displayMino);
                break;
        }
        
        previewBoard.render(context, mouseX, mouseY, delta);
    }
    
    public void setMinoType(MinoCondition minoType) {
        this.minoType = minoType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}