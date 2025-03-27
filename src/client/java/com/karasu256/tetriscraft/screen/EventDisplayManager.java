package com.karasu256.tetriscraft.screen;

import com.karasu256.tetriscraft.game.GameEventType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ゲームイベントの表示を管理するクラス
 */
public class EventDisplayManager {
    private static final long DISPLAY_DURATION = 2000; // イベント表示時間（ミリ秒）
    private static final int FADE_DURATION = 500; // フェードアウト時間（ミリ秒）
    
    // イベントの表示情報を保持する内部クラス
    private static class EventDisplay {
        GameEventType event;
        long startTime;
        int x;
        int y;
        
        EventDisplay(GameEventType event, int x, int y) {
            this.event = event;
            this.startTime = System.currentTimeMillis();
            this.x = x;
            this.y = y;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - startTime > DISPLAY_DURATION;
        }
        
        float getAlpha() {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime > DISPLAY_DURATION - FADE_DURATION) {
                // フェードアウト期間中
                return 1.0f - (float)(elapsedTime - (DISPLAY_DURATION - FADE_DURATION)) / FADE_DURATION;
            }
            return 1.0f;
        }
        
        String getDisplayText() {
            switch (event) {
                case SINGLE:
                    return "SINGLE";
                case DOUBLE:
                    return "DOUBLE";
                case TRIPLE:
                    return "TRIPLE";
                case TETRIS:
                    return "TETRIS";
                case T_SPIN_MINI_SINGLE:
                    return "T-SPIN MINI";
                case T_SPIN_SINGLE:
                    return "T-SPIN SINGLE";
                case T_SPIN_DOUBLE:
                    return "T-SPIN DOUBLE";
                case T_SPIN_TRIPLE:
                    return "T-SPIN TRIPLE";
                case PERFECT_CLEAR:
                    return "PERFECT CLEAR!";
                case BACK_TO_BACK:
                    return "BACK TO BACK";
                default:
                    if (event.name().startsWith("COMBO_")) {
                        return event.name().replace("COMBO_", "") + " COMBO!";
                    }
                    return event.name();
            }
        }
    }
    
    private List<EventDisplay> activeEvents = new ArrayList<>();
    private static final int VERTICAL_SPACING = 20; // イベント表示間の垂直間隔
    
    /**
     * 新しいイベントを表示リストに追加します
     * @param event 表示するイベント
     * @param x 表示位置X
     * @param y 表示位置Y
     */
    public void addEvent(GameEventType event, int x, int y) {
        activeEvents.add(new EventDisplay(event, x, y));
    }
    
    /**
     * アクティブなイベントを描画します
     * @param context 描画コンテキスト
     * @param textRenderer テキストレンダラー
     */
    public void render(DrawContext context, TextRenderer textRenderer) {
        Iterator<EventDisplay> iterator = activeEvents.iterator();
        int currentY = 0;
        
        while (iterator.hasNext()) {
            EventDisplay display = iterator.next();
            if (display.isExpired()) {
                iterator.remove();
                continue;
            }
            
            String text = display.getDisplayText();
            float alpha = display.getAlpha();
            int color = (int)(alpha * 255) << 24 | 0xFFFFFF; // アルファ値を適用した白色
            
            // テキストを描画（影付き）
            context.drawText(
                textRenderer,
                text,
                display.x,
                display.y + currentY,
                color,
                true // 影を付ける
            );
            
            currentY += VERTICAL_SPACING;
        }
    }
}