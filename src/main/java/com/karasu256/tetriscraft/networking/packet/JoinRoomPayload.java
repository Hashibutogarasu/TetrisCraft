package com.karasu256.tetriscraft.networking.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import com.karasu256.tetriscraft.TetrisCraft;

public record JoinRoomPayload(String roomId) implements CustomPayload {
    public static final CustomPayload.Id<JoinRoomPayload> TYPE = new CustomPayload.Id<>(Identifier.of(TetrisCraft.MOD_ID, "join_room"));
    
    public static final PacketCodec<PacketByteBuf, JoinRoomPayload> CODEC = PacketCodec.of(
            (payload, buf) -> buf.writeString(payload.roomId),
            buf -> new JoinRoomPayload(buf.readString())
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}