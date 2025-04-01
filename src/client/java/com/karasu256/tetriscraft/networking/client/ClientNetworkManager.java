package com.karasu256.tetriscraft.networking.client;

import com.karasu256.tetriscraft.JoinEventType;
import com.karasu256.tetriscraft.TetrisCraftClient;
import com.karasu256.tetriscraft.screen.GameScreen;
import com.karasu256.tetriscraft.toast.PlayerJoinedToast;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;
import java.util.UUID;

import com.karasu256.tetriscraft.GameRoom;
import com.karasu256.tetriscraft.game.GameEventType;
import com.karasu256.tetriscraft.networking.packet.*;

public class ClientNetworkManager {
    public static void registerClientHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(RequestNowRoomsPayload.TYPE, (payload, context) -> {
            TetrisCraftClient.clientRoomsCache = payload.nowRooms();
        });

        ClientPlayNetworking.registerGlobalReceiver(CreateRoomPayload.TYPE, (payload, context) -> {
            TetrisCraftClient.clientRoomsCache.add(payload.room());
        });

        ClientPlayNetworking.registerGlobalReceiver(JoinOrLeaveRoomPayload.TYPE, (payload, context) -> {
            for (GameRoom gameRoom : TetrisCraftClient.clientRoomsCache) {
                if (gameRoom.getId().equals(payload.room().getId())){
                    if (payload.type() == JoinEventType.JOIN) {
                        gameRoom.addPlayer(payload.player());

                        if(TetrisCraftClient.currentRoom.getId().equals(payload.room().getId())){
                            MinecraftClient.getInstance().getToastManager().add(
                                    new PlayerJoinedToast(payload.player())
                            );
                        }

                    } else {
                        gameRoom.removePlayer(payload.player());
                    }
                    break;
                }
            }
        });

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
    public static void sendCreateRoom(GameRoom room) {
        sendToServer(new CreateRoomPayload(room));
    }

    public static void sendJoinRoom(GameRoom room) {
        if (MinecraftClient.getInstance().player != null) {
            sendToServer(new JoinOrLeaveRoomPayload(room, MinecraftClient.getInstance().player.getUuid(), JoinEventType.JOIN));
        }
    }

    public static void sendLeaveRoom() {
        if (MinecraftClient.getInstance().player != null) {
            sendToServer(new JoinOrLeaveRoomPayload(TetrisCraftClient.currentRoom, MinecraftClient.getInstance().player.getUuid(), JoinEventType.LEAVE));
        }
    }

    public static void sendAttackEvent(int lines, List<UUID> players, GameEventType eventType) {
        sendToServer(new AttackEventPayload(lines, players, eventType));
    }
}