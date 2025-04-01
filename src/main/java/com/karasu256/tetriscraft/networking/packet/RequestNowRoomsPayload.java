package com.karasu256.tetriscraft.networking.packet;

import com.karasu256.tetriscraft.GameRoom;
import com.karasu256.tetriscraft.TetrisCraft;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record RequestNowRoomsPayload(List<GameRoom> nowRooms) implements CustomPayload {
    public static final CustomPayload.Id<RequestNowRoomsPayload> TYPE = new CustomPayload.Id<>(
            Identifier.of(TetrisCraft.MOD_ID, "request_now_rooms"));
    public static final PacketCodec<PacketByteBuf, RequestNowRoomsPayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                if(payload.nowRooms == null) {
                    buf.writeNbt(new NbtCompound());
                    return;
                }

                NbtCompound nbt = new NbtCompound();

                for (GameRoom nowRoom : payload.nowRooms) {
                    nbt.put(nowRoom.getId().toString(), nowRoom.toNbt());
                }

                buf.writeNbt(nbt);
            },
            buf -> {
                NbtCompound nbt = buf.readNbt();

                if (nbt == null) {
                    return new RequestNowRoomsPayload(new ArrayList<>());
                }

                List<GameRoom> nowRooms = new ArrayList<>();

                for (String key : nbt.getKeys()) {
                    nowRooms.add(GameRoom.fromNbt(nbt.getCompound(key)));
                }


                return new RequestNowRoomsPayload(nowRooms);
            });

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}