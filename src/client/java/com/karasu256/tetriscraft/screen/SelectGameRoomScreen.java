package com.karasu256.tetriscraft.screen;

import com.karasu256.tetriscraft.GameRoom;
import com.karasu256.tetriscraft.networking.client.ClientNetworkManager;
import com.karasu256.tetriscraft.networking.packet.RequestNowRoomsPayload;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * ゲームルーム選択画面
 * プレイヤーが参加できる利用可能なテトリス対戦ルームを表示します
 */
public class SelectGameRoomScreen extends Screen {
    private static final Text TITLE = Text.translatable("tetriscraft.select_room.title");
    private static final Text CREATE_ROOM = Text.translatable("tetriscraft.select_room.create");
    private static final Text JOIN_ROOM = Text.translatable("tetriscraft.select_room.join");
    private static final Text REFRESH = Text.translatable("tetriscraft.select_room.refresh");
    
    private final Screen parent;
    private ButtonWidget joinButton;
    private ButtonWidget refreshButton;
    private ButtonWidget createRoomButton;
    protected TextFieldWidget searchBox;
    private GameRoomListWidget roomListWidget;
    
    public SelectGameRoomScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        // 検索ボックス
        this.searchBox = new TextFieldWidget(
            this.textRenderer,
            this.width / 2 - 100,
            22,
            200,
            20,
            this.searchBox,
            Text.translatable("tetriscraft.select_room.search")
        );
        this.searchBox.setChangedListener(search -> this.roomListWidget.setSearch(search));
        this.addSelectableChild(this.searchBox);
        
        // ルームリストウィジェット
        this.roomListWidget = this.addDrawableChild(new GameRoomListWidget(
            this,
            this.client,
            this.width,
            this.height - 112,
            48,
            36,
            this.searchBox.getText()
        ));
        
        // ボタン配置
        this.joinButton = this.addDrawableChild(ButtonWidget.builder(
            JOIN_ROOM,
            button -> this.roomListWidget.getSelectedAsOptional().ifPresent(GameRoomListWidget.GameRoomEntry::joinRoom)
        ).dimensions(this.width / 2 - 154, this.height - 52, 150, 20).build());
        
        this.createRoomButton = this.addDrawableChild(ButtonWidget.builder(
            CREATE_ROOM,
            button -> this.createRoom()
        ).dimensions(this.width / 2 + 4, this.height - 52, 150, 20).build());
        
        this.refreshButton = this.addDrawableChild(ButtonWidget.builder(
            REFRESH,
            button -> this.refreshRoomList()
        ).dimensions(this.width / 2 - 154, this.height - 28, 150, 20).build());
        
        this.addDrawableChild(ButtonWidget.builder(
            ScreenTexts.BACK,
            button -> this.client.setScreen(this.parent)
        ).dimensions(this.width / 2 + 4, this.height - 28, 150, 20).build());
        
        // 初期状態ではルームが選択されていないためボタンを無効化
        this.roomSelected(null);
    }
    
    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.searchBox);
    }
    
    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.searchBox.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
    }
    
    /**
     * ルーム選択時の処理
     * @param room 選択されたルーム、選択解除の場合はnull
     */
    public void roomSelected(@Nullable GameRoom room) {
        if (room == null) {
            this.joinButton.active = false;
        } else {
            this.joinButton.active = true;
        }
    }
    
    /**
     * ルームリストを更新
     */
    private void refreshRoomList() {
        if (this.roomListWidget != null) {
            this.roomListWidget.load();
        }
    }
    
    /**
     * 新規ルーム作成画面へ遷移
     */
    private void createRoom() {
        this.client.setScreen(new CreateGameRoomScreen(this));
    }

    @Override
    public void removed() {
        // リソース解放処理があれば実装
    }
}