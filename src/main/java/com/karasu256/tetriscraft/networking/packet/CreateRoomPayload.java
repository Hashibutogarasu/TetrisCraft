package com.karasu256.tetriscraft.networking.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import com.karasu256.tetriscraft.GameRoom;
import com.karasu256.tetriscraft.TetrisCraft;
import net.minecraft.network.codec.PacketCodec;

public record CreateRoomPayload(GameRoom room) implements CustomPayload {
    public static final CustomPayload.Id<CreateRoomPayload> TYPE = new CustomPayload.Id<>(
            Identifier.of(TetrisCraft.MOD_ID, "create_room"));
    public static final PacketCodec<PacketByteBuf, CreateRoomPayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                GameRoom.CODEC.encode(buf, payload.room);
            },
            buf -> new CreateRoomPayload(GameRoom.CODEC.decode(buf)));

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}