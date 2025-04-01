package com.karasu256.tetriscraft.networking.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import com.karasu256.tetriscraft.GameRoom;
import com.karasu256.tetriscraft.TetrisCraft;
import com.karasu256.tetriscraft.util.UUIDUtils;

import java.util.List;
import java.util.UUID;

public record RoomStatePayload(GameRoom room, List<UUID> players) implements CustomPayload {
    public static final CustomPayload.Id<RoomStatePayload> TYPE = new CustomPayload.Id<>(
            Identifier.of(TetrisCraft.MOD_ID, "room_state"));

    public static final PacketCodec<PacketByteBuf, RoomStatePayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                GameRoom.CODEC.encode(buf, payload.room);
                buf.writeNbt(UUIDUtils.getUUIDNbts(payload.players));
            },
            buf -> new RoomStatePayload(
                    GameRoom.CODEC.decode(buf),
                    UUIDUtils.getUUIDList(buf.readNbt())));

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}