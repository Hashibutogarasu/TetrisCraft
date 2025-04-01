package com.karasu256.tetriscraft.screen;

import com.karasu256.tetriscraft.GameRoom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

/**
 * 新規ゲームルーム作成画面
 * プレイヤーが新しいテトリス対戦ルームを作成するための画面
 */
public class CreateGameRoomScreen extends Screen {
    private static final Text TITLE = Text.translatable("tetriscraft.create_room.title");
    private static final Text ROOM_NAME_LABEL = Text.translatable("tetriscraft.create_room.room_name");
    private static final Text CREATE_BUTTON = Text.translatable("tetriscraft.create_room.create");
    private static final int MAX_NAME_LENGTH = 32;
    
    private final Screen parent;
    private TextFieldWidget roomNameField;
    private ButtonWidget createButton;
    private String errorMessage = "";
    
    public CreateGameRoomScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 3;
        
        // ルーム名入力フィールド
        this.roomNameField = new TextFieldWidget(
            this.textRenderer, 
            centerX - 100, 
            centerY, 
            200, 
            20, 
            Text.translatable("tetriscraft.create_room.room_name_hint")
        );
        this.roomNameField.setMaxLength(MAX_NAME_LENGTH);
        this.roomNameField.setChangedListener(this::validateInput);
        this.addSelectableChild(this.roomNameField);
        
        // ボタン配置
        this.createButton = this.addDrawableChild(ButtonWidget.builder(
            CREATE_BUTTON,
            button -> this.createRoom()
        ).dimensions(centerX - 100, centerY + 40, 95, 20).build());
        
        this.addDrawableChild(ButtonWidget.builder(
            ScreenTexts.CANCEL,
            button -> this.close()
        ).dimensions(centerX + 5, centerY + 40, 95, 20).build());
        
        this.setInitialFocus(this.roomNameField);
        this.validateInput("");
    }
    
    private void validateInput(String input) {
        // 入力検証
        if (input.isEmpty()) {
            this.errorMessage = "ルーム名を入力してください";
            this.createButton.active = false;
        } else if (input.length() < 3) {
            this.errorMessage = "ルーム名は3文字以上にしてください";
            this.createButton.active = false;
        } else {
            this.errorMessage = "";
            this.createButton.active = true;
        }
    }
    
    private void createRoom() {
        String roomName = this.roomNameField.getText().trim();
        
        // 有効な入力がある場合のみ実行
        if (!roomName.isEmpty() && roomName.length() >= 3) {
            // ここで実際のルーム作成処理を実装
            // 例: サーバーにルーム作成リクエストを送信
            
            // デモとしてローカルのGameRoomオブジェクトを作成
            UUID playerId = MinecraftClient.getInstance().player.getUuid();
            GameRoom newRoom = new GameRoom(UUID.randomUUID(), roomName, playerId);
            
            // 作成したルームに自分自身を追加
            newRoom.addPlayer(playerId);
            
            // ゲーム画面に遷移
            MinecraftClient.getInstance().setScreen(new GameScreen());
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        this.roomNameField.render(context, mouseX, mouseY, delta);


        // タイトル
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // フィールドラベル
        int centerY = this.height / 3;
        context.drawTextWithShadow(this.textRenderer, ROOM_NAME_LABEL, this.width / 2 - 100, centerY - 15, 0xFFFFFF);
        
        // エラーメッセージがあれば表示
        if (!this.errorMessage.isEmpty()) {
            context.drawCenteredTextWithShadow(
                this.textRenderer, 
                Text.literal(this.errorMessage).formatted(Formatting.RED), 
                this.width / 2, 
                centerY + 25, 
                0xFFFFFF
            );
        }
    }
    
    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}