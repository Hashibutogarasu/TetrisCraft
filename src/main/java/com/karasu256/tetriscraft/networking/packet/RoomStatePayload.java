package com.karasu256.tetriscraft.networking.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import com.karasu256.tetriscraft.TetrisCraft;

import java.util.ArrayList;
import java.util.List;

public record RoomStatePayload(String roomId, List<String> players) implements CustomPayload {
    public static final CustomPayload.Id<RoomStatePayload> TYPE = new CustomPayload.Id<>(Identifier.of(TetrisCraft.MOD_ID, "room_state"));
    
    public static final PacketCodec<PacketByteBuf, RoomStatePayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                buf.writeString(payload.roomId);
                buf.writeCollection(payload.players, PacketByteBuf::writeString);
            },
            buf -> new RoomStatePayload(
                buf.readString(),
                buf.readCollection(ArrayList::new, PacketByteBuf::readString)
            )
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}