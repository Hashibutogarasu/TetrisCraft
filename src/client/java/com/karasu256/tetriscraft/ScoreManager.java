package com.karasu256.tetriscraft;

import com.karasu256.tetriscraft.game.GameEventType;

/**
 * テトリスのスコアを管理するクラス
 */
public class ScoreManager {
    private int currentScore = 0;
    private int level = 1;

    /**
     * イベントに応じたスコアを加算します
     * @param event 発生したイベント
     * @param isBackToBack BTB状態かどうか
     */
    public void addEventScore(GameEventType event, boolean isBackToBack) {
        currentScore += event.calculateScore(isBackToBack) * level;
    }

    /**
     * コンボによるスコアを加算します
     * @param comboCount コンボ数
     * @param isBackToBack BTB状態かどうか
     */
    public void addComboScore(int comboCount, boolean isBackToBack) {
        currentScore += GameEventType.calculateComboScore(comboCount, isBackToBack) * level;
    }

    /**
     * ソフトドロップによるスコアを加算します
     * @param dropDistance ドロップした距離（マス数）
     */
    public void addSoftDropScore(int dropDistance) {
        currentScore += GameEventType.calculateSoftDropScore(dropDistance);
    }

    /**
     * ハードドロップによるスコアを加算します
     * @param dropDistance ドロップした距離（マス数）
     */
    public void addHardDropScore(int dropDistance) {
        currentScore += GameEventType.calculateHardDropScore(dropDistance);
    }

    /**
     * 現在のスコアを取得します
     * @return 現在のスコア
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * 現在のレベルを設定します
     * @param level 新しいレベル
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * 現在のレベルを取得します
     * @return 現在のレベル
     */
    public int getLevel() {
        return level;
    }

    /**
     * スコアをリセットします
     */
    public void reset() {
        currentScore = 0;
        level = 1;
    }
}