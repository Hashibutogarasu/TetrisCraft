package com.karasu256.tetriscraft.networking;

import com.karasu256.tetriscraft.GameRoom;
import com.karasu256.tetriscraft.JoinEventType;
import com.karasu256.tetriscraft.networking.packet.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class NetworkManager {
    private static final List<GameRoom> rooms = new ArrayList<>();

    public static void registerC2SPacketTypes(){
        PayloadTypeRegistry.playC2S().register(CreateRoomPayload.TYPE, CreateRoomPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RoomStatePayload.TYPE, RoomStatePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(JoinOrLeaveRoomPayload.TYPE, JoinOrLeaveRoomPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(LeaveRoomPayload.TYPE, LeaveRoomPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AttackEventPayload.TYPE, AttackEventPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AttackReceivedPayload.TYPE, AttackReceivedPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(GameStatePayload.TYPE, GameStatePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestNowRoomsPayload.TYPE, RequestNowRoomsPayload.CODEC);
    }

    public static void registerS2CPacketTypes() {
        PayloadTypeRegistry.playS2C().register(CreateRoomPayload.TYPE, CreateRoomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RoomStatePayload.TYPE, RoomStatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(JoinOrLeaveRoomPayload.TYPE, JoinOrLeaveRoomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LeaveRoomPayload.TYPE, LeaveRoomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AttackEventPayload.TYPE, AttackEventPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AttackReceivedPayload.TYPE, AttackReceivedPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GameStatePayload.TYPE, GameStatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RequestNowRoomsPayload.TYPE, RequestNowRoomsPayload.CODEC);
    }

    public static void registerServerHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(RequestNowRoomsPayload.TYPE, (payload, player) -> {
            for (ServerPlayerEntity serverPlayer : Objects.requireNonNull(player.player().getServer()).getPlayerManager().getPlayerList()) {
                sendToPlayer(serverPlayer, new RequestNowRoomsPayload(rooms));
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(CreateRoomPayload.TYPE, (payload, player) -> {
            rooms.add(payload.room());

            for (ServerPlayerEntity serverPlayer : Objects.requireNonNull(player.player().getServer()).getPlayerManager().getPlayerList()) {
                sendToPlayer(serverPlayer, new CreateRoomPayload(payload.room()));
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(JoinOrLeaveRoomPayload.TYPE, (payload, player) -> {
            if (payload.type() == JoinEventType.JOIN) {
                payload.room().addPlayer(payload.player());
            } else {
                payload.room().removePlayer(payload.player());
            }

            for (ServerPlayerEntity serverPlayer : Objects.requireNonNull(player.player().getServer()).getPlayerManager().getPlayerList()) {
                sendToPlayer(serverPlayer, new JoinOrLeaveRoomPayload(payload.room(), payload.player(), payload.type()));
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(LeaveRoomPayload.TYPE, (payload, player) -> {
            
        });

        ServerPlayNetworking.registerGlobalReceiver(AttackEventPayload.TYPE, (payload, player) -> {
            var server = player.player().getServer();
            for (UUID target : payload.targets()) {
                if (server != null) {
                    ServerPlayerEntity targetPlayer = server.getPlayerManager().getPlayer(target);

                    if (targetPlayer != null) {
                        sendToPlayer(targetPlayer, new AttackReceivedPayload(targetPlayer.getUuid(), payload.lines(), payload.eventType()));
                    }
                }
            }
        });
    }

    public static void sendToPlayer(ServerPlayerEntity player, CustomPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }
}