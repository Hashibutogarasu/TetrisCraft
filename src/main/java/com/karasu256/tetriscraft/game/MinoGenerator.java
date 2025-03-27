package com.karasu256.tetriscraft.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * テトリミノの生成を管理するクラス
 * 7種1巡方式を実装し、NEXTの管理を行います
 */
public class MinoGenerator {
    private static final int NEXT_COUNT = 5; // 表示するNEXTの数
    private List<MinoCondition> currentBag; // 現在の巡回で使用するミノのリスト
    private List<MinoCondition> nextBag; // 次の巡回で使用するミノのリスト
    private List<MinoCondition> nextMinos; // NEXT表示用のミノリスト

    public MinoGenerator() {
        currentBag = new ArrayList<>();
        nextBag = new ArrayList<>();
        nextMinos = new ArrayList<>();
        initializeBags();
        fillNextMinos();
    }

    /**
     * 7種のミノをシャッフルして新しい巡回を作成します
     */
    private List<MinoCondition> createNewBag() {
        List<MinoCondition> bag = new ArrayList<>();
        bag.add(MinoCondition.I_MINO);
        bag.add(MinoCondition.O_MINO);
        bag.add(MinoCondition.T_MINO);
        bag.add(MinoCondition.S_MINO);
        bag.add(MinoCondition.Z_MINO);
        bag.add(MinoCondition.J_MINO);
        bag.add(MinoCondition.L_MINO);
        Collections.shuffle(bag);
        return bag;
    }

    /**
     * 現在の巡回と次の巡回のバッグを初期化します
     */
    private void initializeBags() {
        currentBag = createNewBag();
        nextBag = createNewBag();
    }

    /**
     * NEXT表示用のミノリストを補充します
     */
    private void fillNextMinos() {
        while (nextMinos.size() < NEXT_COUNT) {
            if (currentBag.isEmpty()) {
                currentBag = nextBag;
                nextBag = createNewBag();
            }
            nextMinos.add(currentBag.remove(0));
        }
    }

    /**
     * 次のミノを取得します
     * @return 次のミノ
     */
    public MinoCondition getNextMino() {
        if (nextMinos.isEmpty()) {
            fillNextMinos();
        }
        MinoCondition nextMino = nextMinos.remove(0);
        fillNextMinos();
        return nextMino;
    }

    /**
     * 指定されたインデックスのNEXTミノを取得します
     * @param index NEXTの位置（0-4）
     * @return 指定位置のミノ
     */
    public MinoCondition peekNextMino(int index) {
        if (index < 0 || index >= nextMinos.size()) {
            return null;
        }
        return nextMinos.get(index);
    }

    /**
     * 現在のNEXTミノのリストを取得します
     * @return NEXTミノのリスト
     */
    public List<MinoCondition> getNextMinos() {
        return new ArrayList<>(nextMinos);
    }
}