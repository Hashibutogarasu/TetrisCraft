package com.karasu256.tetriscraft.networking.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import com.karasu256.tetriscraft.TetrisCraft;

public record LeaveRoomPayload() implements CustomPayload {
    public static final CustomPayload.Id<LeaveRoomPayload> TYPE = new CustomPayload.Id<>(Identifier.of(TetrisCraft.MOD_ID, "leave_room"));
    
    public static final PacketCodec<PacketByteBuf, LeaveRoomPayload> CODEC = PacketCodec.of(
            (payload, buf) -> {},
            buf -> new LeaveRoomPayload()
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}