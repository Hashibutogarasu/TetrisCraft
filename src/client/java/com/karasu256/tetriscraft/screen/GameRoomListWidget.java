package com.karasu256.tetriscraft.screen;

import com.karasu256.tetriscraft.GameRoom;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class GameRoomListWidget extends AlwaysSelectedEntryListWidget<GameRoomListWidget.Entry> {
    private static final Identifier JOIN_HIGHLIGHTED_TEXTURE = Identifier.of("tetriscraft", "textures/gui/join_highlighted.png");
    private static final Identifier JOIN_TEXTURE = Identifier.of("tetriscraft", "textures/gui/join.png");
    
    private final SelectGameRoomScreen parent;
    private CompletableFuture<List<GameRoom>> roomsFuture;
    @Nullable
    private List<GameRoom> rooms;
    private String search = "";
    private final LoadingEntry loadingEntry;

    public GameRoomListWidget(SelectGameRoomScreen parent, MinecraftClient client, int width, int height, int y, int itemHeight, String search) {
        super(client, width, height, y, itemHeight);
        this.parent = parent;
        this.loadingEntry = new LoadingEntry(client);
        this.search = search;
        this.roomsFuture = this.loadRooms();
        this.show(tryGet());
    }

    @Nullable
    private List<GameRoom> tryGet() {
        try {
            return this.roomsFuture.getNow(null);
        } catch (Exception e) {
            return null;
        }
    }

    public void load() {
        this.roomsFuture = this.loadRooms();
    }

    private CompletableFuture<List<GameRoom>> loadRooms() {
        // この部分は実際のネットワーク処理を行うべきですが、デモとしてダミーデータを生成します
        // 実装時にはサーバーからルーム情報を取得する処理に置き換えてください
        return CompletableFuture.supplyAsync(() -> {
            List<GameRoom> dummyRooms = new ArrayList<>();
            // デモ用ダミーデータ
            dummyRooms.add(new GameRoom(UUID.randomUUID(), "テトリス部屋1", UUID.randomUUID()));
            dummyRooms.add(new GameRoom(UUID.randomUUID(), "初心者歓迎！", UUID.randomUUID()));
            dummyRooms.add(new GameRoom(UUID.randomUUID(), "腕に自信あり集合", UUID.randomUUID()));
            // ダミーのプレイヤーを追加
            dummyRooms.get(0).addPlayer(UUID.randomUUID());
            dummyRooms.get(0).addPlayer(UUID.randomUUID());
            dummyRooms.get(1).addPlayer(UUID.randomUUID());
            
            // 実際の実装では一時停止を削除してください
            try {
                Thread.sleep(500); // ロード中の表示をデモするための一時停止
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return dummyRooms;
        });
    }

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        List<GameRoom> roomList = this.tryGet();
        if (roomList != this.rooms) {
            this.show(roomList);
        }
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    private void show(@Nullable List<GameRoom> rooms) {
        if (rooms == null) {
            this.showLoadingScreen();
        } else {
            this.showRooms(this.search, rooms);
        }
        this.rooms = rooms;
    }

    public void setSearch(String search) {
        if (this.rooms != null && !search.equals(this.search)) {
            this.showRooms(search, this.rooms);
        }
        this.search = search;
    }

    private void showRooms(String search, List<GameRoom> rooms) {
        this.clearEntries();
        String searchLower = search.toLowerCase();

        for (GameRoom room : rooms) {
            if (this.shouldShow(searchLower, room)) {
                this.addEntry(new GameRoomEntry(this, room));
            }
        }
    }

    private boolean shouldShow(String search, GameRoom room) {
        return search.isEmpty() || room.getName().toLowerCase().contains(search);
    }

    private void showLoadingScreen() {
        this.clearEntries();
        this.addEntry(this.loadingEntry);
    }

    public int getRowWidth() {
        return 270;
    }

    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        GameRoom selectedRoom = null;
        if (entry instanceof GameRoomEntry roomEntry) {
            selectedRoom = roomEntry.room;
        }
        this.parent.roomSelected(selectedRoom);
    }

    public Optional<GameRoomEntry> getSelectedAsOptional() {
        Entry entry = this.getSelectedOrNull();
        if (entry instanceof GameRoomEntry roomEntry) {
            return Optional.of(roomEntry);
        }
        return Optional.empty();
    }

    public SelectGameRoomScreen getParent() {
        return parent;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyCodes.isToggle(keyCode)) {
            Optional<GameRoomEntry> optional = this.getSelectedAsOptional();
            if (optional.isPresent()) {
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                optional.get().joinRoom();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        // アクセシビリティ対応（必要に応じて実装）
    }

    public abstract static class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        public void close() {
        }
    }

    public class GameRoomEntry extends Entry {
        private final MinecraftClient client;
        private final SelectGameRoomScreen screen;
        final GameRoom room;
        private long lastClickTime;

        public GameRoomEntry(GameRoomListWidget roomList, GameRoom room) {
            this.client = roomList.client;
            this.screen = roomList.getParent();
            this.room = room;
            this.lastClickTime = 0;
        }

        @Override
        public Text getNarration() {
            return Text.translatable("narrator.select.gameroom", room.getName(), room.getPlayerIds().size());
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            String roomName = room.getName();
            String playerCount = room.getPlayerIds().size() + "人のプレイヤー";
            
            // ルーム名を描画
            context.drawTextWithShadow(this.client.textRenderer, roomName, x + 32 + 3, y + 1, 0xFFFFFF);
            
            // プレイヤー数を描画
            TextRenderer textRenderer = this.client.textRenderer;
            int textY = y + 9 + 3;
            context.drawTextWithShadow(textRenderer, playerCount, x + 32 + 3, textY, 0x808080);
            
            // ホスト情報
            String hostText = "ホスト: " + (room.getHostPlayerId().toString().substring(0, 6) + "...");
            context.drawTextWithShadow(textRenderer, hostText, x + 32 + 3, textY + 12, 0x808080);
            
            // プレイヤーアイコンのレンダリング（デフォルトのスティーブアイコン）
            Identifier playerIconTexture = Identifier.of("minecraft", "textures/entity/steve.png");
            context.drawTexture(RenderLayer::getGuiTextured, playerIconTexture, x, y, 32, 32, 8, 8, 8, 8, 64, 64);
            
            if ((Boolean)this.client.options.getTouchscreen().getValue() || hovered) {
                context.fill(x, y, x + 32, y + 32, 0x1F000000);
                
                int mouseXOffset = mouseX - x;
                boolean isOverIcon = mouseXOffset < 32;
                
                // 参加アイコンの描画
                Identifier joinTexture = isOverIcon ? JOIN_HIGHLIGHTED_TEXTURE : JOIN_TEXTURE;
                context.drawGuiTexture(RenderLayer::getGuiTextured, joinTexture, x, y, 32, 32);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            GameRoomListWidget.this.setSelected(this);
            
            if (mouseX - GameRoomListWidget.this.getRowLeft() <= 32.0 && Util.getMeasuringTimeMs() - this.lastClickTime >= 250L) {
                this.lastClickTime = Util.getMeasuringTimeMs();
                joinRoom();
                return true;
            }

            if (Util.getMeasuringTimeMs() - this.lastClickTime < 250L) {
                // ダブルクリックで参加
                joinRoom();
                return true;
            }
            
            this.lastClickTime = Util.getMeasuringTimeMs();
            return true;
        }

        public void joinRoom() {
            // 実際の実装ではこのルームに接続するコードを記述します
            this.client.setScreen(new GameScreen());
        }
    }

    public static class LoadingEntry extends Entry {
        private static final Text LOADING_TEXT = Text.translatable("multiplayer.loadingRooms");
        private final MinecraftClient client;

        public LoadingEntry(MinecraftClient client) {
            this.client = client;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int centerX = (this.client.currentScreen.width - this.client.textRenderer.getWidth(LOADING_TEXT)) / 2;
            int centerY = y + (entryHeight - 9) / 2;
            context.drawTextWithShadow(this.client.textRenderer, LOADING_TEXT, centerX, centerY, 0xFFFFFF);
            
            String dots = Util.getMeasuringTimeMs() / 300L % 4L == 0L ? "   " : 
                          Util.getMeasuringTimeMs() / 300L % 4L == 1L ? ".  " : 
                          Util.getMeasuringTimeMs() / 300L % 4L == 2L ? ".." : "...";
            context.drawTextWithShadow(this.client.textRenderer, dots, 
                    centerX + this.client.textRenderer.getWidth(LOADING_TEXT), centerY, 0xFFFFFF);
        }

        @Override
        public Text getNarration() {
            return LOADING_TEXT;
        }
    }
}