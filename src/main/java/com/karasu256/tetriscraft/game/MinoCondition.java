package com.karasu256.tetriscraft.game;

import com.karasu256.tetriscraft.TetrisCraft;
import net.minecraft.util.Identifier;

public enum MinoCondition {
    I_MINO("i", true),
    T_MINO("t", false),
    O_MINO("o", false),
    S_MINO("s", false),
    Z_MINO("z", false),
    J_MINO("j", false),
    L_MINO("l", false),
    GARBAGE("garbage", false), // お邪魔ミノ
    EMPTY("empty", false);

    private boolean ghost;
    private final Identifier texture;
    private final Identifier ghostTexture;

    MinoCondition(String textureName, boolean ghost) {
        this.ghost = ghost;
        this.texture = Identifier.of(TetrisCraft.MOD_ID, "textures/minos/" + textureName + ".png");
        this.ghostTexture = Identifier.of(TetrisCraft.MOD_ID, "textures/minos/" + textureName + "_ghost.png");
    }

    public boolean isGhost() {
        return ghost;
    }

    public void setGhost(boolean ghost) {
        this.ghost = ghost;
    }

    public Identifier getTexture() {
        return ghost ? ghostTexture : texture;
    }

    /**
     * このミノの新しいインスタンスをコピーします
     * 
     * @param ghost ゴースト表示にする場合はtrue
     * @return 新しいMinoConditionインスタンス
     */
    public MinoCondition copy(boolean ghost) {
        MinoCondition copy = valueOf(name());
        copy.ghost = ghost;
        return copy;
    }

    /**
     * このミノの新しいインスタンスをコピーします
     * 
     * @return 新しいMinoConditionインスタンス
     */
    public MinoCondition copy() {
        return copy(this.ghost);
    }
}
