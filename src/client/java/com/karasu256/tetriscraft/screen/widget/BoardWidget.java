package com.karasu256.tetriscraft.screen.widget;

import com.karasu256.tetriscraft.game.GameCondition;
import com.karasu256.tetriscraft.game.MinoCondition;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;

public class BoardWidget implements Drawable {
    // ドットの大きさと間隔を定数として定義
    public static final int DOT_SIZE = 12; // DotWidgetの実際のサイズに合わせて8に修正
    public static final int DOT_SPACING = 0; // ドット間の間隔（ピクセル）
    
    // テトリスの論理的なボードサイズを定義
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    
    private final GameCondition gameCondition;
    private final int x_offset;
    private final int y_offset;
    
    // テトリスの論理的なボード状態を保持する2次元配列
    private MinoCondition[][] boardState;

    public BoardWidget(GameCondition gameCondition, int x_offset, int y_offset) {
        this(gameCondition, x_offset, y_offset, BOARD_WIDTH, BOARD_HEIGHT);
    }

    public BoardWidget(GameCondition gameCondition, int x_offset, int y_offset, int width, int height) {
        this.gameCondition = gameCondition;
        this.x_offset = x_offset;
        this.y_offset = y_offset;
        
        // 論理的なボード状態の初期化
        this.boardState = new MinoCondition[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boardState[x][y] = MinoCondition.EMPTY;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 現在のボード状態を描画
        for (int x = 0; x < boardState.length; x++) {
            for (int y = 0; y < boardState[0].length; y++) {
                DotWidget dotWidget = new DotWidget(
                    x * (DOT_SIZE + DOT_SPACING),
                    x_offset,
                    y * (DOT_SIZE + DOT_SPACING),
                    y_offset,
                    boardState[x][y]  // 論理ボードの状態を使用
                );
                dotWidget.render(context, mouseX, mouseY, delta);
            }
        }
    }
    
    /**
     * 論理座標（テトリスのボード上の位置）をレンダリング座標に変換します
     * @param boardX ボード上のX座標（0-9）
     * @param boardY ボード上のY座標（0-19）
     * @return レンダリング座標の配列 [x, y]
     */
    public int[] boardToScreenCoordinates(int boardX, int boardY) {
        int screenX = x_offset + boardX * (DOT_SIZE + DOT_SPACING);
        int screenY = y_offset + boardY * (DOT_SIZE + DOT_SPACING);
        return new int[]{screenX, screenY};
    }
    
    /**
     * レンダリング座標を論理座標（テトリスのボード上の位置）に変換します
     * @param screenX 画面上のX座標
     * @param screenY 画面上のY座標
     * @return ボード上の座標の配列 [x, y]、ボード外の場合はnull
     */
    public int[] screenToBoardCoordinates(int screenX, int screenY) {
        // オフセットを引いた相対座標
        int relativeX = screenX - x_offset;
        int relativeY = screenY - y_offset;
        
        // ドットのサイズと間隔を考慮して除算
        int boardX = relativeX / (DOT_SIZE + DOT_SPACING);
        int boardY = relativeY / (DOT_SIZE + DOT_SPACING);
        
        // ボード内の座標であるか確認
        if (boardX >= 0 && boardX < BOARD_WIDTH && boardY >= 0 && boardY < BOARD_HEIGHT) {
            return new int[]{boardX, boardY};
        }
        return null; // ボード外の座標
    }
    
    /**
     * 指定した論理座標にミノを設定します
     * @param boardX ボード上のX座標
     * @param boardY ボード上のY座標
     * @param condition 設定するミノの状態
     */
    public void setMino(int boardX, int boardY, MinoCondition condition) {
        if (boardX >= 0 && boardX < boardState.length && boardY >= 0 && boardY < boardState[0].length) {
            boardState[boardX][boardY] = condition;
        }
    }
    
    /**
     * 指定した論理座標のミノの状態を取得します
     * @param boardX ボード上のX座標
     * @param boardY ボード上のY座標
     * @return ミノの状態、座標が範囲外の場合はnull
     */
    public MinoCondition getMino(int boardX, int boardY) {
        if (boardX >= 0 && boardX < boardState.length && boardY >= 0 && boardY < boardState[0].length) {
            return boardState[boardX][boardY];
        }
        return null;
    }
    
    /**
     * ボード状態全体をクリアします
     */
    public void clearBoard() {
        for (int x = 0; x < boardState.length; x++) {
            for (int y = 0; y < boardState[0].length; y++) {
                boardState[x][y] = MinoCondition.EMPTY;
            }
        }
    }
    
    /**
     * X方向のオフセット値を取得します
     * @return X方向のオフセット
     */
    public int getXOffset() {
        return x_offset;
    }
    
    /**
     * Y方向のオフセット値を取得します
     * @return Y方向のオフセット
     */
    public int getYOffset() {
        return y_offset;
    }

    /**
     * 指定された行が完全に埋まっているかチェックします
     * @param y 行のインデックス
     * @return 行が完全に埋まっている場合はtrue
     */
    public boolean isLineFilled(int y) {
        for (int x = 0; x < boardState.length; x++) {
            if (boardState[x][y] == MinoCondition.EMPTY) {
                return false;
            }
        }
        return true;
    }

    /**
     * 指定された行を消去し、上の行を下にシフトします
     * @param y 消去する行のインデックス
     */
    private void clearLine(int y) {
        // 指定された行から上の行を1つずつ下にシフト
        for (int row = y; row > 0; row--) {
            for (int x = 0; x < boardState.length; x++) {
                boardState[x][row] = boardState[x][row - 1];
            }
        }
        // 最上行を空にする
        for (int x = 0; x < boardState.length; x++) {
            boardState[x][0] = MinoCondition.EMPTY;
        }
    }

    /**
     * 全ての行をチェックし、埋まっている行を消去します
     * @return 消去した行数
     */
    public int clearFilledLines() {
        int clearedLines = 0;
        // 下から上に向かってチェック
        for (int y = boardState[0].length - 1; y >= 0; y--) {
            if (isLineFilled(y)) {
                clearLine(y);
                clearedLines++;
                // 同じ行を再チェック（上の行が下がってくるため）
                y++;
            }
        }
        return clearedLines;
    }

    /**
     * お邪魔ミノを指定された段数分挿入します
     * @param lines 挿入する段数
     */
    public void insertGarbageLines(int lines) {
        // 挿入する段数が0以下の場合は何もしない
        if (lines <= 0) {
            return;
        }

        // 既存の行を上にシフト
        for (int y = lines; y < boardState[0].length; y++) {
            for (int x = 0; x < boardState.length; x++) {
                boardState[x][y - lines] = boardState[x][y];
            }
        }

        // 下からlines段分をお邪魔ミノで埋める
        for (int y = boardState[0].length - lines; y < boardState[0].length; y++) {
            // 各行に1つだけ空白を作る（ランダムな位置に）
            int emptyPos = (int) (Math.random() * boardState.length);
            for (int x = 0; x < boardState.length; x++) {
                boardState[x][y] = (x == emptyPos) ? MinoCondition.EMPTY : MinoCondition.GARBAGE;
            }
        }
    }

    /**
     * 現在のボードがパーフェクトクリア状態（全消し）かどうかを判定します
     * @return ボード上のすべてのマスが空の場合はtrue
     */
    public boolean isPerfectClear() {
        // ボード全体をスキャンして、ブロックが存在するかチェック
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (boardState[x][y] != MinoCondition.EMPTY) {
                    // 1つでもブロックが存在すれば、パーフェクトクリアではない
                    return false;
                }
            }
        }
        // すべてのマスが空ならパーフェクトクリア
        return true;
    }
}
