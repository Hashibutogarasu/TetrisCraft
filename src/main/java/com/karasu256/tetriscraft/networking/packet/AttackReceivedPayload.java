package com.karasu256.tetriscraft.networking.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import com.karasu256.tetriscraft.game.GameEventType;
import com.karasu256.tetriscraft.TetrisCraft;

import java.util.UUID;

public record AttackReceivedPayload(UUID fromPlayer, int lines, GameEventType eventType) implements CustomPayload {
    public static final CustomPayload.Id<AttackReceivedPayload> TYPE = new CustomPayload.Id<>(Identifier.of(TetrisCraft.MOD_ID, "attack_received"));

    public static final PacketCodec<PacketByteBuf, AttackReceivedPayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                buf.writeUuid(payload.fromPlayer);
                buf.writeInt(payload.lines);
                buf.writeEnumConstant(payload.eventType);
            },
            buf -> new AttackReceivedPayload(
                buf.readUuid(),
                buf.readInt(),
                buf.readEnumConstant(GameEventType.class)
            )
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}