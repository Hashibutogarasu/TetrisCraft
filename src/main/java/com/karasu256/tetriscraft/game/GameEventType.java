package com.karasu256.tetriscraft.game;

/**
 * テトリスのゲームイベントを表すenum
 */
public enum GameEventType {
    // ライン消去イベント
    SINGLE(1, false, 100, 0), // シングル（1ライン消去）
    DOUBLE(2, false, 300, 0), // ダブル（2ライン消去）
    TRIPLE(4, false, 500, 0), // トリプル（3ライン消去）
    TETRIS(5, false, 800, 1200), // テトリス（4ライン消去）

    // Tスピン系イベント
    T_SPIN_MINI(1, false, 100, 0), // Tスピンミニ
    T_SPIN(0, false, 400, 0), // Tスピン
    T_SPIN_MINI_SINGLE(0, true, 200, 300), // Tスピンミニシングル
    T_SPIN_SINGLE(2, true, 800, 1200), // Tスピンシングル
    T_SPIN_DOUBLE(4, true, 1200, 1800), // Tスピンダブル
    T_SPIN_TRIPLE(6, true, 1600, 2400), // Tスピントリプル

    // コンボ系イベント（1-17コンボ）
    REN_0(0, false, 0, 0), // 0コンボ（何もなし）
    COMBO_1(0, false, 50, 75), // 1コンボ
    COMBO_2(1, false, 100, 150), // 2コンボ
    COMBO_3(1, false, 150, 225), // 3コンボ
    COMBO_4(2, false, 200, 300), // 4コンボ
    COMBO_5(2, false, 250, 375), // 5コンボ
    COMBO_6(3, false, 300, 450), // 6コンボ
    COMBO_7(3, false, 350, 525), // 7コンボ
    COMBO_8(4, false, 400, 600), // 8コンボ
    COMBO_9(4, false, 450, 675), // 9コンボ
    COMBO_10(4, false, 500, 750), // 10コンボ
    COMBO_11(5, false, 550, 825), // 11コンボ
    COMBO_12(5, false, 600, 900), // 12コンボ
    COMBO_13(5, false, 650, 975), // 13コンボ
    COMBO_14(5, false, 700, 1050), // 14コンボ
    COMBO_15(5, false, 750, 1125), // 15コンボ
    COMBO_16(5, false, 800, 1200), // 16コンボ
    COMBO_17(5, false, 850, 1275), // 17コンボ

    // その他のイベント
    PERFECT_CLEAR(20, true, 2800, 4400), // パーフェクトクリア（全消し）
    BACK_TO_BACK(1, true, 0, 0); // Back-to-Back（テトリスまたはTスピン）

    private final int attackLines; // 相手に送る段数
    private final boolean btbEligible; // BTB対象となるかどうか
    private final int baseScore; // 基本スコア
    private final int btbScore; // BTB時のスコア

    /**
     * GameEventTypeのコンストラクタ
     * 
     * @param attackLines 相手に送る段数
     * @param btbEligible BTB対象となるかどうか
     * @param baseScore 基本スコア
     * @param btbScore BTB時のスコア（0の場合はBTB対象外）
     */
    GameEventType(int attackLines, boolean btbEligible, int baseScore, int btbScore) {
        this.attackLines = attackLines;
        this.btbEligible = btbEligible;
        this.baseScore = baseScore;
        this.btbScore = btbScore;
    }

    /**
     * イベントによる攻撃段数を取得します
     * 
     * @return 相手に送る段数
     */
    public int getAttackLines() {
        return attackLines;
    }

    /**
     * イベントがBTB対象かどうかを取得します
     * 
     * @return BTB対象の場合はtrue
     */
    public boolean isBtbEligible() {
        return btbEligible;
    }

    /**
     * BTB状態での攻撃段数を計算します
     * 
     * @param isBackToBack BTB状態であるかどうか
     * @return BTBボーナスを含めた攻撃段数
     */
    public int calculateAttackLines(boolean isBackToBack) {
        if (isBackToBack && btbEligible) {
            // BTB状態では攻撃力が1.5倍（切り上げ）
            return (int) Math.ceil(attackLines * 1.5);
        }
        return attackLines;
    }

    /**
     * スコアを計算します
     * 
     * @param isBackToBack BTB状態であるかどうか
     * @return 計算されたスコア
     */
    public int calculateScore(boolean isBackToBack) {
        if (isBackToBack && btbScore > 0) {
            return btbScore;
        }
        return baseScore;
    }

    /**
     * コンボによるスコアを計算します
     * @param comboCount コンボ数
     * @param isBackToBack BTB状態であるかどうか
     * @return コンボによるスコア
     */
    public static int calculateComboScore(int comboCount, boolean isBackToBack) {
        if (comboCount <= 0) return 0;
        int score = Math.min(comboCount * 50, 1000); // 20コンボまで
        return isBackToBack ? (int)(score * 1.5) : score;
    }

    /**
     * ソフトドロップによるスコアを計算します
     * @param dropDistance ドロップした距離（マス数）
     * @return スコア
     */
    public static int calculateSoftDropScore(int dropDistance) {
        return dropDistance; // 1マスにつき1ポイント
    }

    /**
     * ハードドロップによるスコアを計算します
     * @param dropDistance ドロップした距離（マス数）
     * @return スコア
     */
    public static int calculateHardDropScore(int dropDistance) {
        return dropDistance * 2; // 1マスにつき2ポイント
    }

    /**
     * 基本スコアを取得します
     * @return 基本スコア
     */
    public int getBaseScore() {
        return baseScore;
    }

    /**
     * BTB時のスコアを取得します
     * @return BTB時のスコア
     */
    public int getBtbScore() {
        return btbScore;
    }
}