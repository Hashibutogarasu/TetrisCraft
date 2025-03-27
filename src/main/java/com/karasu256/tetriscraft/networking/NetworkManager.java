package com.karasu256.tetriscraft.networking;

import com.karasu256.tetriscraft.TetrisCraft;
import com.karasu256.tetriscraft.networking.packet.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class NetworkManager {
    public static void registerC2SPacketTypes(){
        PayloadTypeRegistry.playC2S().register(CreateRoomPayload.TYPE, CreateRoomPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RoomStatePayload.TYPE, RoomStatePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(JoinRoomPayload.TYPE, JoinRoomPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(LeaveRoomPayload.TYPE, LeaveRoomPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AttackEventPayload.TYPE, AttackEventPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AttackReceivedPayload.TYPE, AttackReceivedPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(GameStatePayload.TYPE, GameStatePayload.CODEC);
    }

    public static void registerS2CPacketTypes() {
        PayloadTypeRegistry.playS2C().register(CreateRoomPayload.TYPE, CreateRoomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RoomStatePayload.TYPE, RoomStatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(JoinRoomPayload.TYPE, JoinRoomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LeaveRoomPayload.TYPE, LeaveRoomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AttackEventPayload.TYPE, AttackEventPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AttackReceivedPayload.TYPE, AttackReceivedPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GameStatePayload.TYPE, GameStatePayload.CODEC);
    }

    public static void registerServerHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(CreateRoomPayload.TYPE, (payload, player) -> {
            
        });

        ServerPlayNetworking.registerGlobalReceiver(JoinRoomPayload.TYPE, (payload, player) -> {
            
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