package com.karasu256.tetriscraft.game;

/**
 * お邪魔ミノの管理を行うクラス
 */
public class GarbageManager {
    private int pendingGarbageLines; // 待機中のお邪魔ミノの段数
    private final int maxGarbagePerDrop; // 1回の接地で最大何段挿入されるか

    public GarbageManager(int maxGarbagePerDrop) {
        this.pendingGarbageLines = 0;
        this.maxGarbagePerDrop = maxGarbagePerDrop;
    }

    /**
     * お邪魔ミノの段数を追加します
     * @param lines 追加する段数
     */
    public void addGarbageLines(int lines) {
        this.pendingGarbageLines += lines;
    }

    /**
     * 現在待機中のお邪魔ミノの段数を取得します
     * @return 待機中の段数
     */
    public int getPendingGarbageLines() {
        return pendingGarbageLines;
    }

    /**
     * お邪魔ミノを挿入する段数を取得し、待機中の段数から差し引きます
     * @return 挿入する段数
     */
    public int getAndDecrementGarbageLines() {
        int linesToAdd = Math.min(pendingGarbageLines, maxGarbagePerDrop);
        pendingGarbageLines -= linesToAdd;
        return linesToAdd;
    }

    /**
     * お邪魔ミノの段数をキャンセルします
     * @param lines キャンセルする段数
     */
    public void cancelGarbageLines(int lines) {
        pendingGarbageLines = Math.max(0, pendingGarbageLines - lines);
    }
}