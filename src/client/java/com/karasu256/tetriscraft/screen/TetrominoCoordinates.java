package com.karasu256.tetriscraft.screen;

import com.karasu256.tetriscraft.game.MinoCondition;
import com.karasu256.tetriscraft.screen.widget.BoardWidget;

/**
 * テトリスの論理的なピース（ミノ）を表現するクラス
 */
public class TetrominoCoordinates {
    /**
     * テトリミノの操作イベントを通知するインターフェース
     */
    public interface TetrominoEventListener {
        void onRotate();
        void onTSpinRotate();
        void onSoftDrop();
        void onMove();
    }

    private TetrominoEventListener eventListener;
    private final MinoCondition minoType;
    private int[][] shape;
    private int boardX;
    private int boardY;
    private int rotation;
    private boolean isGhost = false;
    private boolean isTSpin = false;
    private boolean isTSpinMini = false;
    private boolean isAutoDrop = false;
    private boolean isHardDropping = false;

    // 各ミノタイプの回転軸を定義
    private static final int[][][] ROTATION_POINTS = {
        // I_MINO の回転オフセット (SuperRotationSystem準拠)
        {
            {0, 0}, {-1, 0}, {-1, 1}, {0, 1} // 0->1, 1->2, 2->3, 3->0
        },
        // その他のミノの回転オフセット
        {
            {0, 0}, {0, 0}, {0, 0}, {0, 0} // 共通のオフセット（回転軸は中央）
        }
    };

    // SRS壁キックテストのオフセットを更新（Tミノを含むJLSTZミノ用）
    private static final int[][][] JLSTZ_WALL_KICKS = {
        // 0->1
        {{0, 0}, {-1, 0}, {-1, 1}, {0, -2}, {-1, -2}},
        // 1->2
        {{0, 0}, {1, 0}, {1, -1}, {0, 2}, {1, 2}},
        // 2->3
        {{0, 0}, {1, 0}, {1, 1}, {0, -2}, {1, -2}},
        // 3->0
        {{0, 0}, {-1, 0}, {-1, -1}, {0, 2}, {-1, 2}}
    };

    private static final int[][][] I_WALL_KICKS = {
        // 0->1
        {{0, 0}, {-2, 0}, {1, 0}, {-2, 1}, {1, -2}},
        // 1->2
        {{0, 0}, {-1, 0}, {2, 0}, {-1, -2}, {2, 1}},
        // 2->3
        {{0, 0}, {2, 0}, {-1, 0}, {2, -1}, {-1, 2}},
        // 3->0
        {{0, 0}, {1, 0}, {-2, 0}, {1, 2}, {-2, -1}}
    };

    /**
     * テトリスピースを初期化します
     * @param minoType ミノのタイプ
     */
    public TetrominoCoordinates(MinoCondition minoType) {
        this.minoType = minoType;
        this.rotation = 0;
        
        // 初期位置は上部中央付近
        this.boardX = (BoardWidget.BOARD_WIDTH / 2) - 1;
        this.boardY = 0;
        
        // ミノの形状を初期化
        initializeShape();
    }
    
    /**
     * Hold時のテトリスピースを初期化します
     * @param minoType ミノのタイプ
     * @param spawnX 出現位置X
     * @param spawnY 出現位置Y
     */
    public TetrominoCoordinates(MinoCondition minoType, int spawnX, int spawnY) {
        this.minoType = minoType;
        this.rotation = 0;
        this.boardX = spawnX;
        this.boardY = spawnY;
        initializeShape();
    }

    /**
     * ミノのタイプに基づいて形状を初期化します
     * 各ミノは重心を考慮した座標で定義されます
     */
    private void initializeShape() {
        switch (minoType) {
            case I_MINO:
                // I型ミノの形状 (水平)
                // 重心は中央の2つのブロックの間（2のマス）
                shape = new int[][] {
                    {-1, 0}, {0, 0}, {1, 0}, {2, 0}
                };
                break;
            case O_MINO:
                // O型ミノの形状 (2x2の正方形)
                // 重心は中央
                shape = new int[][] {
                    {-1, -1}, {0, -1}, {-1, 0}, {0, 0}
                };
                break;
            case T_MINO:
                // T型ミノの形状
                // 中央のブロックを回転の中心とする
                shape = new int[][] {
                    {0, -1},   // 上部中央
                    {-1, 0},   // 中段左
                    {0, 0},    // 中段中央（回転中心）
                    {1, 0}     // 中段右
                };
                break;
            case S_MINO:
                // S型ミノの形状
                // 重心は2のマス
                shape = new int[][] {
                    {0, -1}, {1, -1}, {-1, 0}, {0, 0}
                };
                break;
            case Z_MINO:
                // Z型ミノの形状
                // 重心は2のマス
                shape = new int[][] {
                    {-1, -1}, {0, -1}, {0, 0}, {1, 0}
                };
                break;
            case J_MINO:
                // J型ミノの形状
                // 重心は2のマス
                shape = new int[][] {
                    {-1, -1}, {-1, 0}, {0, 0}, {1, 0}
                };
                break;
            case L_MINO:
                // L型ミノの形状
                // 重心は2のマス
                shape = new int[][] {
                    {1, -1}, {-1, 0}, {0, 0}, {1, 0}
                };
                break;
            default:
                // デフォルトケース（通常は発生しない）
                shape = new int[][] {
                    {0, 0}
                };
                break;
        }
    }
    
    /**
     * イベントリスナーを設定します
     * @param listener 設定するリスナー
     */
    public void setEventListener(TetrominoEventListener listener) {
        this.eventListener = listener;
    }

    /**
     * ピースを左に移動します
     * @param boardWidget 現在のボード状態
     * @return 移動が成功した場合はtrue
     */
    public boolean moveLeft(BoardWidget boardWidget) {
        // 移動前に衝突チェック
        if (checkCollision(-1, 0, boardWidget)) {
            return false;
        }
        boardX--;
        if (eventListener != null) {
            eventListener.onMove();
        }
        return true;
    }
    
    /**
     * ピースを右に移動します
     * @param boardWidget 現在のボード状態
     * @return 移動が成功した場合はtrue
     */
    public boolean moveRight(BoardWidget boardWidget) {
        // 移動前に衝突チェック
        if (checkCollision(1, 0, boardWidget)) {
            return false;
        }
        boardX++;
        if (eventListener != null) {
            eventListener.onMove();
        }
        return true;
    }
    
    /**
     * ピースを下に移動します
     * @param boardWidget 現在のボード状態
     * @return 移動が成功した場合はtrue、底に達したかブロックに衝突した場合はfalse
     */
    public boolean moveDown(BoardWidget boardWidget) {
        if (checkCollision(0, 1, boardWidget)) {
            return false;
        }
        boardY++;
        if (eventListener != null && !isAutoDrop && !isHardDropping) {
            eventListener.onSoftDrop();
        }
        return true;
    }

    /**
     * 自然落下モードを設定します
     * @param autoDrop 自然落下の場合はtrue
     */
    public void setAutoDrop(boolean autoDrop) {
        this.isAutoDrop = autoDrop;
    }

    /**
     * ハードドロップモードを設定します
     * @param hardDropping ハードドロップ中の場合はtrue
     */
    public void setHardDropping(boolean hardDropping) {
        this.isHardDropping = hardDropping;
    }

    /**
     * 回転を適用します（時計回りまたは反時計回り）
     * @param coordinates 回転させる座標の配列
     * @param isClockwise 時計回りならtrue、反時計回りならfalse
     */
    private void applyRotation(int[][] coordinates, boolean isClockwise) {
        for (int i = 0; i < coordinates.length; i++) {
            int x = coordinates[i][0];
            int y = coordinates[i][1];
            
            if (isClockwise) {
                // 時計回り90度回転
                coordinates[i][0] = -y;
                coordinates[i][1] = x;
            } else {
                // 反時計回り90度回転
                coordinates[i][0] = y;
                coordinates[i][1] = -x;
            }
        }
    }

    /**
     * ピースを時計回りに回転させます
     * @param boardWidget 現在のボード状態
     * @return 回転が成功した場合はtrue
     */
    public boolean rotate(BoardWidget boardWidget) {
        // Oミノは回転しても形が変わらないため、即座にtrueを返す
        if (minoType == MinoCondition.O_MINO) {
            return true;
        }
        
        int nextRotation = (rotation + 1) % 4;
        
        // 現在の形状を保存
        int[][] originalShape = new int[shape.length][2];
        for (int i = 0; i < shape.length; i++) {
            originalShape[i][0] = shape[i][0];
            originalShape[i][1] = shape[i][1];
        }
        
        // 回転を適用
        applyRotation(shape, true);

        // 壁キックテストを使用して回転を試みる
        if (tryWallKicks(boardWidget, originalShape, rotation, nextRotation, true)) {
            rotation = nextRotation;
            
            // Tミノの場合はTスピン判定を行う
            if (minoType == MinoCondition.T_MINO) {
                // Tスピン判定の実行
                boolean isTSpinRotation = checkTSpin(boardWidget, 0, 0);
                if (isTSpinRotation && eventListener != null) {
                    eventListener.onTSpinRotate();
                    return true;
                }
            }
            
            // 通常の回転イベントを発火（Tスピンでない場合）
            if (eventListener != null) {
                eventListener.onRotate();
            }
            return true;
        }

        // 回転が失敗した場合は元の形状に戻す
        for (int i = 0; i < shape.length; i++) {
            shape[i][0] = originalShape[i][0];
            shape[i][1] = originalShape[i][1];
        }
        return false;
    }
    
    /**
     * ピースを反時計回りに回転させます
     * @param boardWidget 現在のボード状態
     * @return 回転が成功した場合はtrue
     */
    public boolean rotateCounterClockwise(BoardWidget boardWidget) {
        // Oミノは回転しても形が変わらないため、即座にtrueを返す
        if (minoType == MinoCondition.O_MINO) {
            return true;
        }
        
        int nextRotation = (rotation + 3) % 4;
        
        // 現在の形状を保存
        int[][] originalShape = new int[shape.length][2];
        for (int i = 0; i < shape.length; i++) {
            originalShape[i][0] = shape[i][0];
            originalShape[i][1] = shape[i][1];
        }
        
        // 回転を適用
        applyRotation(shape, false);

        // 壁キックテストを使用して回転を試みる
        if (tryWallKicks(boardWidget, originalShape, rotation, nextRotation, false)) {
            rotation = nextRotation;
            
            // Tミノの場合はTスピン判定を行う
            if (minoType == MinoCondition.T_MINO) {
                // Tスピン判定の実行
                boolean isTSpinRotation = checkTSpin(boardWidget, 0, 0);
                if (isTSpinRotation && eventListener != null) {
                    eventListener.onTSpinRotate();
                    return true;
                }
            }
            
            // 通常の回転イベントを発火（Tスピンでない場合）
            if (eventListener != null) {
                eventListener.onRotate();
            }
            return true;
        }

        // 回転が失敗した場合は元の形状に戻す
        for (int i = 0; i < shape.length; i++) {
            shape[i][0] = originalShape[i][0];
            shape[i][1] = originalShape[i][1];
        }
        return false;
    }

    /**
     * 壁キックテストを実行して回転を試みます
     */
    private boolean tryWallKicks(BoardWidget boardWidget, int[][] originalShape, int currentRotation, int nextRotation, boolean isClockwise) {
        int[][][] wallKicks = (minoType == MinoCondition.I_MINO) ? I_WALL_KICKS : JLSTZ_WALL_KICKS;
        int kickIndex = getKickIndex(currentRotation, nextRotation);
        
        // 各壁キックテストを試行
        for (int i = 0; i < wallKicks[kickIndex].length; i++) {
            int[] offset = wallKicks[kickIndex][i];
            // テスト中のオフセットを反時計回りの場合は反転
            int testX = isClockwise ? offset[0] : -offset[0];
            int testY = isClockwise ? -offset[1] : offset[1]; // Y軸は反転

            if (!checkCollision(testX, testY, boardWidget)) {
                // このオフセットで回転が可能
                boardX += testX;
                boardY += testY;
                
                // Tスピン判定を更新
                if (minoType == MinoCondition.T_MINO) {
                    checkTSpin(boardWidget, testX, testY);
                }
                
                return true;
            }
        }
        
        return false;
    }

    /**
     * Tスピンの状態を取得します
     * @return 0: 非Tスピン, 1: Tスピンミニ, 2: 通常のTスピン
     */
    public int getTSpinType() {
        if (!isTSpin) return 0;
        return isTSpinMini ? 1 : 2;
    }

    /**
     * Tスピンの判定を行います
     */
    private boolean checkTSpin(BoardWidget boardWidget, int kickX, int kickY) {
        if (this.minoType != MinoCondition.T_MINO) {
            isTSpin = false;
            isTSpinMini = false;
            return false;
        }

        // Tミノの角の位置を確認
        int[] corners = new int[4];
        corners[0] = isCornerFilled(boardX - 1, boardY - 1, boardWidget) ? 1 : 0; // 左上
        corners[1] = isCornerFilled(boardX + 1, boardY - 1, boardWidget) ? 1 : 0; // 右上
        corners[2] = isCornerFilled(boardX - 1, boardY + 1, boardWidget) ? 1 : 0; // 左下
        corners[3] = isCornerFilled(boardX + 1, boardY + 1, boardWidget) ? 1 : 0; // 右下

        int filledCorners = corners[0] + corners[1] + corners[2] + corners[3];

        // キック移動なしでの回転は通常のTスピンとみなさない
        boolean hasKick = kickX != 0 || kickY != 0;

        // 回転後のTミノの向きに応じて、前方の角を判定
        int[] frontCorners = new int[2];
        switch (rotation) {
            case 0: // 上向き
                frontCorners[0] = corners[2]; // 左下
                frontCorners[1] = corners[3]; // 右下
                break;
            case 1: // 右向き
                frontCorners[0] = corners[0]; // 左上
                frontCorners[1] = corners[2]; // 左下
                break;
            case 2: // 下向き
                frontCorners[0] = corners[0]; // 左上
                frontCorners[1] = corners[1]; // 右上
                break;
            case 3: // 左向き
                frontCorners[0] = corners[1]; // 右上
                frontCorners[1] = corners[3]; // 右下
                break;
        }

        int filledFrontCorners = frontCorners[0] + frontCorners[1];

        if (filledCorners >= 3) {
            // 3つ以上の角が埋まっている場合は通常のTスピン
            isTSpin = true;
            isTSpinMini = false;
            return true;
        } else if (filledCorners == 2 && hasKick && filledFrontCorners == 2) {
            // 2つの角が埋まっていて、キック移動があり、前方の2つの角が埋まっている場合も通常のTスピン
            isTSpin = true;
            isTSpinMini = false;
            return true;
        } else if (filledCorners == 2 && hasKick) {
            // 2つの角が埋まっていて、キック移動があるがTスピンミニの条件を満たす場合
            isTSpin = true;
            isTSpinMini = true;
            return false;  // Tスピンミニは通常の回転として扱う
        }

        // それ以外は非Tスピン
        isTSpin = false;
        isTSpinMini = false;
        return false;
    }

    /**
     * 指定された角が埋まっているかどうかを判定します
     */
    private boolean isCornerFilled(int x, int y, BoardWidget boardWidget) {
        // ボード範囲外は埋まっているとみなす
        if (x < 0 || x >= BoardWidget.BOARD_WIDTH || 
            y < 0 || y >= BoardWidget.BOARD_HEIGHT) {
            return true;
        }
        // 固定ブロックがある場合も埋まっているとみなす
        return boardWidget.getMino(x, y) != MinoCondition.EMPTY;
    }

    /**
     * 現在の回転状態と次の回転状態からキックインデックスを取得します
     */
    private int getKickIndex(int currentRotation, int nextRotation) {
        if (currentRotation == 0 && nextRotation == 1) return 0;
        if (currentRotation == 1 && nextRotation == 2) return 1;
        if (currentRotation == 2 && nextRotation == 3) return 2;
        if (currentRotation == 3 && nextRotation == 0) return 3;
        if (currentRotation == 1 && nextRotation == 0) return 0;
        if (currentRotation == 2 && nextRotation == 1) return 1;
        if (currentRotation == 3 && nextRotation == 2) return 2;
        if (currentRotation == 0 && nextRotation == 3) return 3;
        return 0;
    }

    /**
     * 現在のピースがTスピン状態かどうかを取得します
     * @return Tスピン状態ならtrue
     */
    public boolean isTSpin() {
        return isTSpin;
    }

    /**
     * 現在のピースがTスピンミニ状態かどうかを取得します
     * @return Tスピンミニ状態ならtrue
     */
    public boolean isTSpinMini() {
        return isTSpinMini;
    }

    /**
     * Iミノの回転を処理します
     * @param boardWidget 現在のボード状態
     * @param isClockwise 時計回りか反時計回りか
     * @return 回転が成功した場合はtrue
     */
    private boolean rotateIMino(BoardWidget boardWidget, boolean isClockwise) {
        int currentRotation = rotation;
        int nextRotation = isClockwise ? (rotation + 1) % 4 : (rotation + 3) % 4;
        
        // 現在の形状を保存
        int[][] originalShape = new int[shape.length][2];
        for (int i = 0; i < shape.length; i++) {
            originalShape[i][0] = shape[i][0];
            originalShape[i][1] = shape[i][1];
        }
        
        // 回転行列を適用
        if (isClockwise) {
            // 時計回りの90度回転
            for (int i = 0; i < shape.length; i++) {
                int tempX = shape[i][0];
                int tempY = shape[i][1];
                
                shape[i][0] = -tempY;
                shape[i][1] = tempX;
            }
        } else {
            // 反時計回りの90度回転
            for (int i = 0; i < shape.length; i++) {
                int tempX = shape[i][0];
                int tempY = shape[i][1];
                
                shape[i][0] = tempY;
                shape[i][1] = -tempX;
            }
        }
        
        // 回転結果をそのまま適用できるかチェック
        if (!checkCollision(0, 0, boardWidget)) {
            rotation = nextRotation;
            return true;
        }
        
        // 軸調整を試みる
        int offsetX = 0;
        int offsetY = 0;
        
        // 提供された回転ルールに基づく軸調整
        if (currentRotation == 0) { // A状態（水平、0度）から回転
            if (nextRotation == 1) { // Bへ（90度、垂直）
                // 右へ移動（枠にくっつく）
                offsetX = 1;
            } else if (nextRotation == 3) { // Dへ（270度、垂直）
                // 左へ移動
                offsetX = -1;
                // 必要に応じて2マス移動
                if (checkCollision(offsetX, offsetY, boardWidget)) {
                    offsetX = -2;
                }
            }
        } else if (currentRotation == 1) { // B状態（垂直、90度）から回転
            if (nextRotation == 0) { // Aへ（0度、水平）
                // 逆方向へ移動（左へ）
                offsetX = -1;
                // 必要に応じて2マス移動
                if (checkCollision(offsetX, offsetY, boardWidget)) {
                    offsetX = -2;
                }
            } else if (nextRotation == 2) { // Cへ（180度、水平）
                // 下へ移動
                offsetY = 1;
            }
        } else if (currentRotation == 2) { // C状態（水平、180度）から回転
            if (nextRotation == 1) { // Bへ（90度、垂直）
                // 左へ移動
                offsetX = -1;
            } else if (nextRotation == 3) { // Dへ（270度、垂直）
                // 右へ移動
                offsetX = 1;
                // 必要に応じて2マス移動
                if (checkCollision(offsetX, offsetY, boardWidget)) {
                    offsetX = 2;
                }
            }
        } else if (currentRotation == 3) { // D状態（垂直、270度）から回転
            if (nextRotation == 0) { // Aへ（0度、水平）
                // 回転方向へ移動（右へ）
                offsetX = 1;
                // 必要に応じて2マス移動
                if (checkCollision(offsetX, offsetY, boardWidget)) {
                    offsetX = 2;
                }
            } else if (nextRotation == 2) { // Cへ（180度、水平）
                // 上へ移動
                offsetY = -1;
            }
        }
        
        // 上下の調整（必要な場合）
        if (offsetX == 0 && offsetY == 0) {
            if ((currentRotation == 0 && nextRotation == 1) || 
                (currentRotation == 2 && nextRotation == 3)) {
                // Aから回転前のミノが右半分にある場合
                offsetY = -1;
            } else if ((currentRotation == 0 && nextRotation == 3) ||
                       (currentRotation == 2 && nextRotation == 1)) {
                // Aから回転前のミノが左半分にある場合
                offsetY = isClockwise ? -1 : -2;
            }
        }
        
        // 調整した位置で衝突チェック
        if (!checkCollision(offsetX, offsetY, boardWidget)) {
            boardX += offsetX;
            boardY += offsetY;
            rotation = nextRotation;
            return true;
        }
        
        // 別の上下調整を試す
        if (offsetY == 0) {
            if (currentRotation == 0 || currentRotation == 2) {
                offsetY = currentRotation == 0 ? -2 : 2;
            } else {
                offsetY = currentRotation == 1 ? 2 : -2;
            }
            
            if (!checkCollision(offsetX, offsetY, boardWidget)) {
                boardX += offsetX;
                boardY += offsetY;
                rotation = nextRotation;
                return true;
            }
        }
        
        // 全ての調整が失敗した場合は元の形状に戻す
        for (int i = 0; i < shape.length; i++) {
            shape[i][0] = originalShape[i][0];
            shape[i][1] = originalShape[i][1];
        }
        return false;
    }
    
    /**
     * 指定した方向に移動または回転した場合に衝突するかチェックします
     * @param deltaX X方向の移動量
     * @param deltaY Y方向の移動量
     * @param boardWidget 現在のボード状態
     * @return 衝突する場合はtrue、そうでない場合はfalse
     */
    private boolean checkCollision(int deltaX, int deltaY, BoardWidget boardWidget) {
        int[][] coordinates = getCurrentBoardCoordinates();
        
        for (int[] coordinate : coordinates) {
            int newX = coordinate[0] + deltaX;
            int newY = coordinate[1] + deltaY;
            
            // ボードの境界外かチェック
            if (newX < 0 || newX >= BoardWidget.BOARD_WIDTH || newY >= BoardWidget.BOARD_HEIGHT) {
                return true;
            }
            
            // 既に固定されたブロックがあるかチェック
            if (newY >= 0 && boardWidget.getMino(newX, newY) != MinoCondition.EMPTY) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 現在のピースをボードに固定します
     * @param boardWidget 現在のボード状態
     */
    public void lockToBoard(BoardWidget boardWidget) {
        int[][] coordinates = getCurrentBoardCoordinates();
        
        for (int[] coordinate : coordinates) {
            int x = coordinate[0];
            int y = coordinate[1];
            
            if (x >= 0 && x < BoardWidget.BOARD_WIDTH && y >= 0 && y < BoardWidget.BOARD_HEIGHT) {
                boardWidget.setMino(x, y, minoType);
            }
        }
    }
    
    /**
     * ピースの現在のボード座標（各ブロックの座標）を取得します
     * @return 各ブロックのボード座標の配列 [[x1, y1], [x2, y2], ...]
     */
    public int[][] getCurrentBoardCoordinates() {
        int[][] coordinates = new int[shape.length][2];
        
        for (int i = 0; i < shape.length; i++) {
            coordinates[i][0] = boardX + shape[i][0];
            coordinates[i][1] = boardY + shape[i][1];
        }
        
        return coordinates;
    }

    /**
     * コピーコンストラクタ（ゴーストピース用）
     */
    public TetrominoCoordinates(TetrominoCoordinates other) {
        this.minoType = other.minoType;
        this.rotation = other.rotation;
        this.boardX = other.boardX;
        this.boardY = other.boardY;
        this.shape = new int[other.shape.length][2];
        for (int i = 0; i < other.shape.length; i++) {
            this.shape[i][0] = other.shape[i][0];
            this.shape[i][1] = other.shape[i][1];
        }
    }

    /**
     * ゴーストの状態を設定します
     * @param ghost ゴースト表示にする場合はtrue
     */
    public void setGhost(boolean ghost) {
        this.isGhost = ghost;
    }

    /**
     * ピースをボードに描画します
     */
    public void render(BoardWidget boardWidget) {
        int[][] coordinates = getCurrentBoardCoordinates();
        
        // 座標ごとにミノを描画
        for (int[] coordinate : coordinates) {
            int x = coordinate[0];
            int y = coordinate[1];
            
            if (x >= 0 && x < BoardWidget.BOARD_WIDTH && y >= 0 && y < BoardWidget.BOARD_HEIGHT) {
                // ゴースト状態を指定してミノをコピー
                MinoCondition displayMino = minoType.copy(isGhost);
                boardWidget.setMino(x, y, displayMino);
            }
        }
    }
    
    /**
     * ミノが接地しているかどうかを判定します
     * @param boardWidget 現在のボード状態
     * @return 接地している場合はtrue
     */
    public boolean isLanded(BoardWidget boardWidget) {
        return checkCollision(0, 1, boardWidget);
    }
    
    public MinoCondition getMinoType() {
        return minoType;
    }
    
    public int getBoardX() {
        return boardX;
    }
    
    public int getBoardY() {
        return boardY;
    }
    
    public void setBoardX(int boardX) {
        this.boardX = boardX;
    }
    
    public void setBoardY(int boardY) {
        this.boardY = boardY;
    }
    
    public int getRotation() {
        return rotation;
    }

    /**
     * スポーン位置で他のブロックと衝突するかチェックします
     * @param boardWidget 現在のボード状態
     * @return 衝突がある場合はtrue
     */
    public boolean checkSpawnCollision(BoardWidget boardWidget) {
        int[][] coordinates = getCurrentBoardCoordinates();
        
        for (int[] coordinate : coordinates) {
            int x = coordinate[0];
            int y = coordinate[1];
            
            // スポーン位置で既に別のブロックがある場合は衝突
            if (y >= 0 && y < BoardWidget.BOARD_HEIGHT && 
                x >= 0 && x < BoardWidget.BOARD_WIDTH && 
                boardWidget.getMino(x, y) != MinoCondition.EMPTY) {
                return true;
            }
        }
        
        return false;
    }
}