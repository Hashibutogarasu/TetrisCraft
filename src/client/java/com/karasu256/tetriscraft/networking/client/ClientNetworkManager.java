package com.karasu256.tetriscraft.networking.client;

import com.karasu256.tetriscraft.screen.GameScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;
import java.util.UUID;

import com.karasu256.tetriscraft.game.GameEventType;
import com.karasu256.tetriscraft.networking.packet.*;

public class ClientNetworkManager {
    public static void registerClientHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(RoomStatePayload.TYPE, (payload, context) -> {

        });

        ClientPlayNetworking.registerGlobalReceiver(GameStatePayload.TYPE, (payload, context) -> {

        });

        ClientPlayNetworking.registerGlobalReceiver(AttackReceivedPayload.TYPE, (payload, context) -> {
            if(MinecraftClient.getInstance().currentScreen instanceof GameScreen screen){
                screen.getGarbageManager().addGarbageLines(payload.lines());
            }
        });
    }

    public static void sendToServer(CustomPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    // ユーティリティメソッド
    public static void sendCreateRoom(String roomName) {
        sendToServer(new CreateRoomPayload(roomName));
    }

    public static void sendJoinRoom(String roomId) {
        sendToServer(new JoinRoomPayload(roomId));
    }

    public static void sendLeaveRoom() {
        sendToServer(new LeaveRoomPayload());
    }

    public static void sendAttackEvent(int lines, List<UUID> players, GameEventType eventType) {
        sendToServer(new AttackEventPayload(lines, players, eventType));
    }
}