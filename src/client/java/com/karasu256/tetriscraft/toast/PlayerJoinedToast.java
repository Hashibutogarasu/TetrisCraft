package com.karasu256.tetriscraft.toast;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.network.PlayerListEntry;

import java.util.Objects;
import java.util.UUID;

public class PlayerJoinedToast implements Toast {
    private static final Identifier TEXTURE = Identifier.ofVanilla("toast/advancement");
    public static final int DEFAULT_DURATION_MS = 5000;

    private final TextRenderer textRenderer;
    private String playerName;
    private UUID playerUuid;
    private Toast.Visibility visibility;
    private boolean soundPlayed;

    public PlayerJoinedToast(UUID uuid) {
        MinecraftClient client = MinecraftClient.getInstance();
        this.visibility = Visibility.SHOW;
        this.soundPlayed = false;
        this.playerUuid = uuid;

        this.textRenderer = client.textRenderer;
        if (client.world != null) {
            this.playerName = client.world.getPlayers().stream()
                    .filter(player -> player.getUuid().equals(uuid))
                    .findFirst()
                    .map(player -> Objects.requireNonNull(player.getDisplayName()).getString())
                    .orElse("Unknown Player");
        }
    }

    @Override
    public Visibility getVisibility() {
        return this.visibility;
    }

    @Override
    public void update(ToastManager manager, long time) {
        this.visibility = (double) time >= (double) DEFAULT_DURATION_MS * manager.getNotificationDisplayTimeMultiplier()
                ? Visibility.HIDE
                : Visibility.SHOW;
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        // トーストの背景を描画
        context.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURE, 0, 0, this.getWidth(), this.getHeight());

        // プレイヤーアイコンを表示
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerListEntry playerEntry = null;

        if (client.getNetworkHandler() != null) {
            playerEntry = client.getNetworkHandler().getPlayerListEntry(this.playerUuid);
        }

        // プレイヤーアイコンの描画（左端）
        int iconSize = 16; // アイコンサイズ
        int iconX = 8; // 左マージン
        int iconY = 7; // 上マージン

        // プレイヤーのスキンテクスチャを描画
        if (playerEntry != null) {
            // 指定された新しいメソッドを使用
            PlayerSkinDrawer.draw(context, playerEntry.getSkinTextures(), iconX, iconY, iconSize, Colors.WHITE);
        } else {
            // フォールバック: デフォルトのスティーブスキン
            Identifier defaultSkin = Identifier.of("minecraft", "textures/entity/steve.png");
            context.drawTexture(RenderLayer::getGuiTextured, defaultSkin, iconX, iconY, 8, 8, 8, 8, 64, 64);
        }

        // アイコン表示スペースを考慮したテキスト開始位置
        int textX = iconX + iconSize + 6;

        // プレイヤー名とメッセージを準備
        String baseMessage = "Player " + this.playerName + " joined the room";

        // 表示幅を計算して必要に応じて分割
        int maxWidth = getWidth() - textX - 10; // 左右のマージンを考慮

        // 分割が必要かチェック
        int color = 0xFFFFFF;

        if (textRenderer.getWidth(baseMessage) > maxWidth) {
            String firstLine = "Player " + this.playerName;
            String secondLine = "joined the room";

            renderMessage(startTime);

            // 複数行に分けて表示
            context.drawText(textRenderer, firstLine, textX, 7, color, false);
            context.drawText(textRenderer, secondLine, textX, 17, color, false);
        } else {
            // 通常の単一行表示
            renderMessage(startTime);

            // 通常表示
            context.drawText(textRenderer, baseMessage, textX, 12, color, false);
        }
    }

    private void renderMessage(long startTime) {
        int color = 0xFFFFFF;

        if (startTime < 500L) {
            // フェードイン
            int alpha = MathHelper.floor(MathHelper.clamp((float) startTime / 500.0F, 0.0F, 1.0F) * 255.0F);
            color = alpha << 24 | 0xFFFFFF;
        } else if (startTime > DEFAULT_DURATION_MS - 500L) {
            // フェードアウト
            int alpha = MathHelper.floor(
                    MathHelper.clamp((float) (DEFAULT_DURATION_MS - startTime) / 500.0F, 0.0F, 1.0F) * 255.0F);
            color = alpha << 24 | 0xFFFFFF;
        }
    }
}
