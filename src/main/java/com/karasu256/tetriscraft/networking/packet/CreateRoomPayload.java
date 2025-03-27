package com.karasu256.tetriscraft.networking.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import com.karasu256.tetriscraft.TetrisCraft;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record CreateRoomPayload(String roomName) implements CustomPayload {
    public static final CustomPayload.Id<CreateRoomPayload> TYPE = new CustomPayload.Id<>(Identifier.of(TetrisCraft.MOD_ID, "create_room"));
    public static final PacketCodec<PacketByteBuf, CreateRoomPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, CreateRoomPayload::roomName,
            CreateRoomPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}