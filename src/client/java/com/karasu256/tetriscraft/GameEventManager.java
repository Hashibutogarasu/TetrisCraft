package com.karasu256.tetriscraft;

import com.karasu256.tetriscraft.game.GameEventType;

import java.util.ArrayList;
import java.util.List;

/**
 * テトリスのゲームイベントを管理するクラス
 */
public class GameEventManager {
    private boolean isBackToBack = false;
    private int comboCount = 0;
    private final List<GameEventType> currentEvents = new ArrayList<>();

    /**
     * 現在のイベントをリセットします
     */
    public void resetEvents() {
        currentEvents.clear();
    }

    /**
     * イベントを追加します
     * @param event 追加するイベント
     */
    public void addEvent(GameEventType event) {
        currentEvents.add(event);
    }

    /**
     * コンボ数を更新します
     * @param didClearLines ラインを消去したかどうか
     */
    public void updateCombo(boolean didClearLines) {
        if (didClearLines) {
            comboCount++;
            // コンボイベントを追加（最大10コンボまで）
            if (comboCount > 0 && comboCount <= 10) {
                addEvent(GameEventType.valueOf("COMBO_" + comboCount));
            }
        } else {
            comboCount = 0;
        }
    }

    /**
     * Back-to-Back状態を更新します
     * @param event 発生したイベント
     */
    public void updateBackToBack(GameEventType event) {
        if (event.isBtbEligible()) {
            if (isBackToBack) {
                // BTB継続中の場合はBTBイベントを追加
                addEvent(GameEventType.BACK_TO_BACK);
            }
            isBackToBack = true;
        } else if (event != GameEventType.BACK_TO_BACK) {
            // BTB対象外のイベントが発生した場合（BTBイベント自体は除く）
            isBackToBack = false;
        }
    }

    /**
     * 全てのイベントによる合計攻撃段数を計算します
     * @return 攻撃段数
     */
    public int calculateTotalAttackLines() {
        int total = 0;
        for (GameEventType event : currentEvents) {
            total += event.calculateAttackLines(isBackToBack);
        }
        return total;
    }

    /**
     * 現在のコンボ数を取得します
     * @return コンボ数
     */
    public int getComboCount() {
        return comboCount;
    }

    /**
     * 現在のBack-to-Back状態を取得します
     * @return BTB状態の場合はtrue
     */
    public boolean isBackToBack() {
        return isBackToBack;
    }

    /**
     * 現在発生しているイベントのリストを取得します
     * @return イベントのリスト
     */
    public List<GameEventType> getCurrentEvents() {
        return new ArrayList<>(currentEvents);
    }

    /**
     * 最後に発生したイベントを取得します
     * @return 最後のイベント。イベントがない場合はnull
     */
    public GameEventType getLastEvent() {
        if (currentEvents.isEmpty()) {
            return null;
        }
        return currentEvents.get(currentEvents.size() - 1);
    }
}