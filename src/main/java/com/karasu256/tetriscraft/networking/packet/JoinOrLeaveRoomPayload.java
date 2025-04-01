package com.karasu256.tetriscraft.networking.packet;

import com.karasu256.tetriscraft.JoinEventType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import com.karasu256.tetriscraft.GameRoom;
import com.karasu256.tetriscraft.TetrisCraft;

import java.util.UUID;

public record JoinOrLeaveRoomPayload(GameRoom room, UUID player, JoinEventType type) implements CustomPayload {
    public static final CustomPayload.Id<JoinOrLeaveRoomPayload> TYPE = new CustomPayload.Id<>(Identifier.of(TetrisCraft.MOD_ID, "join_room"));
    
    public static final PacketCodec<PacketByteBuf, JoinOrLeaveRoomPayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                GameRoom.CODEC.encode(buf, payload.room);
                buf.writeUuid(payload.player);
                buf.writeEnumConstant(payload.type);
            },
            buf -> new JoinOrLeaveRoomPayload(GameRoom.CODEC.decode(buf), buf.readUuid(), buf.readEnumConstant(JoinEventType.class))
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}