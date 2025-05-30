package com.karasu256.tetriscraft.screen;

import com.karasu256.tetriscraft.TetrisCraft;
import com.karasu256.tetriscraft.TetrisCraftClient;
import com.karasu256.tetriscraft.game.GameCondition;
import com.karasu256.tetriscraft.GameEventManager;
import com.karasu256.tetriscraft.game.GameEventType;
import com.karasu256.tetriscraft.game.MinoCondition;
import com.karasu256.tetriscraft.ScoreManager;
import com.karasu256.tetriscraft.game.GarbageManager;
import com.karasu256.tetriscraft.game.MinoGenerator;
import com.karasu256.tetriscraft.networking.client.ClientNetworkManager;

import com.karasu256.tetriscraft.screen.widget.BoardWidget;
import com.karasu256.tetriscraft.screen.widget.HoldPreviewWidget;
import com.karasu256.tetriscraft.screen.widget.MinoPreviewWidget;
import com.karasu256.tetriscraft.screen.widget.NextPreviewWidget;
import com.karasu256.tetriscraft.sounds.ModSounds;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class GameScreen extends Screen implements IScreen, TetrominoCoordinates.TetrominoEventListener {
    private BoardWidget boardWidget;
    private TetrominoCoordinates currentPiece;
    private long lastDropTime;
    private static final long DROP_DELAY = 500;
    private long landedTime = 0;
    private static final long LOCK_DELAY = 500;
    private int moveCount = 0;
    private static final int MAX_MOVE_COUNT = 15;
    private MinoCondition holdPiece = null;
    private boolean hasUsedHold = false;
    private HoldPreviewWidget holdPreviewWidget;
    private NextPreviewWidget nextPreviewWidget;
    private MinoGenerator minoGenerator;
    private GameCondition gameCondition;
    private final TextRenderer textRenderer;
    private GarbageManager garbageManager;
    private final GameEventManager eventManager;
    private final EventDisplayManager eventDisplayManager;
    private final ScoreManager scoreManager;

    private boolean isFadingOut = false;
    private float fadeOutProgress = 0.0f;
    private static final float FADE_OUT_SPEED = 0.1f;

    private boolean isMusicPlaying = false;
    private PositionedSoundInstance musicInstance;

    private int spawnDelayTicks = 0;
    private static final int SPAWN_DELAY_TICKS = 30;

    public GameScreen() {
        super(Text.literal("Tetris"));
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
        this.garbageManager = new GarbageManager(5);
        this.gameCondition = GameCondition.WAITING_START;
        this.eventManager = new GameEventManager();
        this.eventDisplayManager = new EventDisplayManager(); // イベント表示マネージャーの初期化
        this.scoreManager = new ScoreManager(); // スコアマネージャーの初期化を追加
    }

    private void initializeGame() {
        // ゲームを初期状態に設定
        this.gameCondition = GameCondition.PLAYING;
        this.boardWidget.clearBoard();
        this.currentPiece = null;
        this.holdPiece = null;
        this.hasUsedHold = false;
        this.minoGenerator = new MinoGenerator();
        this.nextPreviewWidget.updateNextMinos(minoGenerator);
        this.holdPreviewWidget.updateHoldMino(null);
        this.spawnNewPiece();
        this.lastDropTime = System.currentTimeMillis();
        this.landedTime = 0;
        this.moveCount = 0;
        this.scoreManager.reset(); // スコアをリセット
        this.spawnDelayTicks = 0;

        this.playBackgroundMusic();
    }

    @Override
    protected void init() {
        // テトリスの標準的なサイズ 10x20のグリッド
        int boardWidth = (BoardWidget.DOT_SIZE + BoardWidget.DOT_SPACING) * BoardWidget.BOARD_WIDTH;
        int boardHeight = (BoardWidget.DOT_SIZE + BoardWidget.DOT_SPACING) * BoardWidget.BOARD_HEIGHT;

        // プレビューのサイズ
        int previewWidth = (BoardWidget.DOT_SIZE + BoardWidget.DOT_SPACING) * MinoPreviewWidget.PREVIEW_SIZE;

        // メインのボードを中央に配置
        int centerX = (this.width - boardWidth) / 2;
        int centerY = (this.height - boardHeight) / 2;

        // 各ウィジェットを初期化
        this.boardWidget = new BoardWidget(
                this.gameCondition,
                centerX,
                centerY);

        // ホールドウィジェットの位置を設定（ボードの左側、適切な間隔を空ける）
        this.holdPreviewWidget = new HoldPreviewWidget(
                centerX - previewWidth - (BoardWidget.DOT_SIZE + BoardWidget.DOT_SPACING) * 2,
                centerY);

        // ネクストウィジェットの位置を設定（ボードの右側、適切な間隔を空ける）
        this.nextPreviewWidget = new NextPreviewWidget(
                centerX + boardWidth + (BoardWidget.DOT_SIZE + BoardWidget.DOT_SPACING) * 2,
                centerY);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // 一時的な描画用ボードを作成
        BoardWidget tempBoard = new BoardWidget(
                gameCondition,
                boardWidget.getXOffset(),
                boardWidget.getYOffset());

        // 固定されたミノを通常表示でコピー
        for (int x = 0; x < BoardWidget.BOARD_WIDTH; x++) {
            for (int y = 0; y < BoardWidget.BOARD_HEIGHT; y++) {
                MinoCondition mino = boardWidget.getMino(x, y);
                if (mino != MinoCondition.EMPTY) {
                    MinoCondition displayMino = mino.copy();
                    displayMino.setGhost(false);
                    tempBoard.setMino(x, y, displayMino);
                }
            }
        }

        // ゴーストピースの描画（設置予定位置のみゴースト表示）
        if (currentPiece != null) {
            TetrominoCoordinates ghostPiece = new TetrominoCoordinates(currentPiece);
            // 可能な限り下に移動
            while (ghostPiece.moveDown(boardWidget)) {
                // 移動可能な限り下に移動
            }
            // ゴーストフラグを設定して描画
            ghostPiece.setGhost(true);
            ghostPiece.render(tempBoard);
        }

        // 現在のピースを通常表示で描画
        if (currentPiece != null) {
            currentPiece.setGhost(false);
            currentPiece.render(tempBoard);
        }

        // 各ウィジェットを描画
        tempBoard.render(context, mouseX, mouseY, delta);
        holdPreviewWidget.render(context, mouseX, mouseY, delta);
        nextPreviewWidget.render(context, mouseX, mouseY, delta);

        // イベント表示の描画
        if (gameCondition == GameCondition.PLAYING) {
            // ボードの左側にイベントを表示
            eventDisplayManager.render(context, textRenderer);
        }

        // ゲーム状態に応じたメッセージ表示
        if (gameCondition == GameCondition.WAITING_START) {
            if (MinecraftClient.getInstance().player != null && TetrisCraftClient.currentRoom != null
                    && TetrisCraftClient.currentRoom.getHostPlayerId()
                            .equals(MinecraftClient.getInstance().player.getUuid())) {
                String startText = "Press SPACE to Start";
                int textWidth = this.textRenderer.getWidth(startText);
                int centerX = (this.width - textWidth) / 2;
                int centerY = this.height / 2;
                context.drawText(this.textRenderer, startText, centerX, centerY, 0xFFFFFF, true);
            } else {
                String waitingText = "Waiting for Host to Start";
                int textWidth = this.textRenderer.getWidth(waitingText);
                int centerX = (this.width - textWidth) / 2;
                int centerY = this.height / 2;
                context.drawText(this.textRenderer, waitingText, centerX, centerY, 0xFFFFFF, true);
            }
        } else if (gameCondition == GameCondition.GAME_OVER) {
            String gameOverText = "GAME OVER - Press R to Restart";
            int textWidth = this.textRenderer.getWidth(gameOverText);
            int centerX = (this.width - textWidth) / 2;
            int centerY = this.height / 2;
            context.drawText(this.textRenderer, gameOverText, centerX, centerY, 0xFF0000, true);
        }

        // スコア表示の追加
        String scoreText = "Score: " + scoreManager.getCurrentScore();
        context.drawText(this.textRenderer, scoreText,
                boardWidget.getXOffset() - 100,
                boardWidget.getYOffset() + 200,
                0xFFFFFF, true);

        // ゲーム中の場合のみ自動落下と固定処理を実行
        if (gameCondition == GameCondition.PLAYING) {
            if (spawnDelayTicks > 0) {
                spawnDelayTicks--;
                if (spawnDelayTicks == 0) {
                    // 遅延後にテトリミノを出現させる
                    if (!spawnNewPiece()) {
                        gameCondition = GameCondition.GAME_OVER;
                        stopBackgroundMusic();
                    }
                }
            } else if (currentPiece != null) {
                // 通常の更新処理
                long currentTime = System.currentTimeMillis();
                boolean isLanded = currentPiece.isLanded(boardWidget);

                if (isLanded) {
                    // 接地状態の場合
                    if (landedTime == 0) {
                        // 最初の接地
                        landedTime = currentTime;
                    } else if (currentTime - landedTime >= LOCK_DELAY || moveCount >= MAX_MOVE_COUNT) {
                        // 猶予時間が経過したか、移動回数が制限を超えた場合は固定
                        lockCurrentPiece();
                        landedTime = 0;
                        moveCount = 0;
                    }
                } else {
                    // 非接地状態の場合は猶予時間とカウントをリセット
                    landedTime = 0;
                    moveCount = 0;

                    // 通常の自動落下処理
                    if (currentTime - lastDropTime > DROP_DELAY) {
                        currentPiece.setAutoDrop(true);
                        currentPiece.moveDown(boardWidget);
                        currentPiece.setAutoDrop(false);
                        lastDropTime = currentTime;
                    }
                }
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ゲーム開始待ち状態でスペースキーが押された場合
        if (gameCondition == GameCondition.WAITING_START && keyCode == GLFW.GLFW_KEY_SPACE) {
            initializeGame();
            return true;
        }

        // ゲームオーバー時のリスタート処理
        if (gameCondition == GameCondition.GAME_OVER && (keyCode == GLFW.GLFW_KEY_R)) {
            initializeGame();
            return true;
        }

        // プレイ中のみ通常の入力を受け付ける
        if (gameCondition == GameCondition.PLAYING) {
            if (currentPiece != null) {
                boolean isLanded = currentPiece.isLanded(boardWidget);

                if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
                    if (currentPiece.moveLeft(boardWidget) && isLanded) {
                        resetLockDelay();
                    }
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
                    if (currentPiece.moveRight(boardWidget) && isLanded) {
                        resetLockDelay();
                    }
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_S) {
                    if (currentPiece.moveDown(boardWidget)) {
                        lastDropTime = System.currentTimeMillis();
                        scoreManager.addSoftDropScore(1);
                    }
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_W) {
                    if (currentPiece.rotate(boardWidget) && isLanded) {
                        resetLockDelay();
                    }
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_Z || keyCode == GLFW.GLFW_KEY_LEFT_CONTROL) {
                    if (currentPiece.rotateCounterClockwise(boardWidget) && isLanded) {
                        resetLockDelay();
                    }
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_SPACE) {
                    // ハードドロップの処理
                    int dropDistance = 0;
                    if (currentPiece != null) {
                        currentPiece.setHardDropping(true);
                        while (currentPiece.moveDown(boardWidget)) {
                            dropDistance++;
                        }
                        currentPiece.setHardDropping(false);
                        scoreManager.addHardDropScore(dropDistance);
                        playSound(SoundEvents.BLOCK_STONE_PLACE);
                        lockCurrentPiece();
                        landedTime = 0;
                    }
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
                    if (!hasUsedHold) {
                        MinoCondition currentType = currentPiece.getMinoType();
                        if (holdPiece == null) {
                            // 初回のホールド
                            holdPiece = currentType;
                            spawnNewPiece();
                        } else {
                            // ホールドミノと交換
                            MinoCondition temp = holdPiece;
                            holdPiece = currentType;
                            currentPiece = new TetrominoCoordinates(temp);
                            currentPiece.setEventListener(this); // イベントリスナーを設定
                        }
                        hasUsedHold = true;
                        holdPreviewWidget.updateHoldMino(holdPiece);
                        playSound(SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON);
                        return true;
                    }
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * 接地固定までの猶予時間をリセットします
     * 移動回数が制限を超えていない場合のみリセットが有効です
     */
    private void resetLockDelay() {
        if (moveCount < MAX_MOVE_COUNT) {
            landedTime = System.currentTimeMillis();
            moveCount++;
        }
    }

    /**
     * マウスがクリックされたときの処理
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * 新しいテトリミノを生成します
     * 
     * @return スポーンが成功した場合はtrue、ゲームオーバーの場合はfalse
     */
    private boolean spawnNewPiece() {
        // 7種1巡方式でミノタイプを取得
        MinoCondition newType = minoGenerator.getNextMino();
        TetrominoCoordinates newPiece = new TetrominoCoordinates(newType);

        // スポーン位置で衝突があればゲームオーバー
        if (newPiece.checkSpawnCollision(boardWidget)) {
            return false;
        }

        // スポーンが成功した場合、現在のピースとして設定
        currentPiece = newPiece;

        // NEXTの表示を更新
        nextPreviewWidget.updateNextMinos(minoGenerator);

        // 新しいピースが出現したら各カウンターをリセット
        landedTime = 0;
        moveCount = 0;
        hasUsedHold = false;

        if (currentPiece != null) {
            currentPiece.setEventListener(this);
        }

        return true;
    }

    @Override
    public void onRotate() {
        playSound(SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON);
    }

    @Override
    public void onSoftDrop() {
        playSound(SoundEvents.BLOCK_STONE_STEP);
    }

    @Override
    public void onTSpinRotate() {
        playSound(SoundEvents.BLOCK_LEVER_CLICK);
    }

    @Override
    public void onMove() {
        playSound(SoundEvents.BLOCK_STONE_STEP);
    }

    /**
     * 現在のピースをボードに固定し、次のピースを生成します
     */
    private void lockCurrentPiece() {
        if (currentPiece != null) {
            // ブロックを固定する前に音声を再生
            playSound(SoundEvents.BLOCK_STONE_PLACE);
            currentPiece.lockToBoard(boardWidget);
            
            // イベントをリセット
            eventManager.resetEvents();

            // Tスピン判定を先に取得
            int tSpinType = currentPiece.getTSpinType();
            boolean isTSpin = tSpinType > 0 && currentPiece.getMinoType() == MinoCondition.T_MINO;

            // ボードの左端にイベントを表示するための座標を計算
            int eventX = boardWidget.getXOffset() - 150; // ボードの左150ピクセルの位置
            int eventY = boardWidget.getYOffset(); // ボードと同じ高さ
            // ライン消去の判定と実行
            int clearedLines = boardWidget.clearFilledLines();
            
            // ライン消去があった場合は遅延を設定
            if (clearedLines > 0) {
                spawnDelayTicks = SPAWN_DELAY_TICKS;
                currentPiece = null;  // 現在のピースをクリア
            } else {
                // ライン消去がない場合は即座に次のピースを出現
                if (!spawnNewPiece()) {
                    gameCondition = GameCondition.GAME_OVER;
                    stopBackgroundMusic();
                }
            }

            // サウンドエフェクト再生の判定
            if (clearedLines > 0) {
                // 特殊ケースの判定
                if (isTSpin) {
                    // Tスピンダブル
                    if (clearedLines == 2) {
                        playSound(SoundEvents.ENTITY_VILLAGER_YES);
                    } 
                    // Tスピントリプル
                    else if (clearedLines == 3) {
                        playSound(SoundEvents.ENTITY_TNT_PRIMED);
                    }
                    // その他のTスピン
                    else {
                        playSound(ModSounds.LINE_DELETED_1_EVENT);
                    }
                }
                // テトリス（4ライン消し）
                else if (clearedLines == 4) {
                    playSound(SoundEvents.ENTITY_VILLAGER_YES);
                }
                // 通常のライン消去
                else {
                    playSound(ModSounds.LINE_DELETED_1_EVENT);
                }
            }

            // Tスピンの場合は、ライン消去の有無にかかわらずTスピン系イベントを表示
            if (isTSpin) {
                GameEventType eventType;
                if (clearedLines == 0) {
                    // ライン消去なしのTスピン
                    eventType = (tSpinType == 1) ? GameEventType.T_SPIN_MINI : GameEventType.T_SPIN;
                } else {
                    // ライン消去ありのTスピン
                    switch (clearedLines) {
                        case 1:
                            eventType = (tSpinType == 1) ? GameEventType.T_SPIN_MINI_SINGLE
                                    : GameEventType.T_SPIN_SINGLE;
                            break;
                        case 2:
                            eventType = GameEventType.T_SPIN_DOUBLE;
                            break;
                        case 3:
                            eventType = GameEventType.T_SPIN_TRIPLE;
                            break;
                        default:
                            eventType = (tSpinType == 1) ? GameEventType.T_SPIN_MINI : GameEventType.T_SPIN;
                            break;
                    }
                }

                // イベントを追加
                eventManager.addEvent(eventType);
                eventDisplayManager.addEvent(eventType, eventX, eventY);
                scoreManager.addEventScore(eventType, eventManager.isBackToBack()); // スコア加算
            }
            // 通常のライン消去の場合（Tスピンでない場合のみ）
            else if (clearedLines > 0) {
                GameEventType eventType = null;

                switch (clearedLines) {
                    case 1:
                        eventType = GameEventType.SINGLE;
                        break;
                    case 2:
                        eventType = GameEventType.DOUBLE;
                        break;
                    case 3:
                        eventType = GameEventType.TRIPLE;
                        break;
                    case 4:
                        eventType = GameEventType.TETRIS;
                        break;
                }

                // イベントを追加
                if (eventType != null) {
                    eventManager.addEvent(eventType);
                    eventDisplayManager.addEvent(eventType, eventX, eventY);
                    scoreManager.addEventScore(eventType, eventManager.isBackToBack()); // スコア加算
                }
            }

            // パーフェクトクリアの判定（ライン消去がある場合のみ）
            if (clearedLines > 0 && boardWidget.isPerfectClear()) {
                eventManager.addEvent(GameEventType.PERFECT_CLEAR);
                eventDisplayManager.addEvent(GameEventType.PERFECT_CLEAR, eventX, eventY);
                scoreManager.addEventScore(GameEventType.PERFECT_CLEAR, eventManager.isBackToBack()); // スコア加算
            }

            // BTBとコンボの更新
            boolean btbAdded = false;
            for (GameEventType event : eventManager.getCurrentEvents()) {
                eventManager.updateBackToBack(event);
                if (event == GameEventType.BACK_TO_BACK && !btbAdded) {
                    eventDisplayManager.addEvent(event, eventX, eventY);
                    btbAdded = true;
                }
            }

            // コンボ数の更新と表示（ライン消去がある場合のみ）
            eventManager.updateCombo(clearedLines > 0);
            if (eventManager.getComboCount() > 1) {
                GameEventType comboEvent = GameEventType.valueOf("COMBO_" + Math.min(eventManager.getComboCount(), 17));
                eventDisplayManager.addEvent(comboEvent, eventX, eventY);
                scoreManager.addComboScore(eventManager.getComboCount(), eventManager.isBackToBack()); // コンボスコア加算
            }

            // お邪魔ミノの処理
            int attackLines = eventManager.calculateTotalAttackLines();
            if (attackLines > 0) {
                garbageManager.cancelGarbageLines(attackLines);
                // 攻撃を送信
                // GameEventType lastEvent = eventManager.getLastEvent();
                // if (lastEvent != null) {
                // if (MinecraftClient.getInstance().player != null &&
                // TetrisCraftClient.currentRoom != null) {
                // List<UUID> targetPlayers = TetrisCraftClient.currentRoom.getPlayerIds();
                // ClientNetworkManager.sendAttackEvent(attackLines, targetPlayers, lastEvent);
                // }
                // }
            }

            // お邪魔ミノの挿入
            int garbageLines = garbageManager.getAndDecrementGarbageLines();
            if (garbageLines > 0) {
                boardWidget.insertGarbageLines(garbageLines);
            }
        }
    }

    public void receiveGarbage(int lines) {
        garbageManager.addGarbageLines(lines);
    }

    public void setMaxGarbagePerDrop(int maxLines) {
        this.garbageManager = new GarbageManager(maxLines);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public GarbageManager getGarbageManager() {
        return garbageManager;
    }

    @Override
    public void close() {
        ClientNetworkManager.sendLeaveRoom();
        TetrisCraftClient.currentRoom = null;
        this.stopBackgroundMusic();
        super.close();
    }

    @Override
    public SoundEvent getBackgroundMusic() {
        return ModSounds.GAME_MUSIC_EVENT;
    }

    @Override
    public boolean shouldMusicLoop() {
        return true;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        context.drawTexture(RenderLayer::getGuiTextured, Identifier.of("minecraft", "textures/block/dirt.png"), 0, 0, 0, 0, width, height, 16, 16);
    }
}
