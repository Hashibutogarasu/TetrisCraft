package com.karasu256.tetriscraft.networking.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import com.karasu256.tetriscraft.TetrisCraft;
import java.util.Map;
import java.util.HashMap;

public record GameStatePayload(
    Map<String, PlayerGameState> playerStates
) implements CustomPayload {
    public static final CustomPayload.Id<GameStatePayload> TYPE = new CustomPayload.Id<>(Identifier.of(TetrisCraft.MOD_ID, "game_state"));
    
    public static final PacketCodec<PacketByteBuf, GameStatePayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                buf.writeMap(payload.playerStates, 
                    PacketByteBuf::writeString, 
                    (buf2, state) -> {
                        buf2.writeInt(state.score());
                        buf2.writeInt(state.level());
                        buf2.writeInt(state.lines());
                    }
                );
            },
            buf -> new GameStatePayload(
                buf.readMap(
                    HashMap::new,
                    PacketByteBuf::readString,
                    buf2 -> new PlayerGameState(
                        buf2.readInt(),
                        buf2.readInt(),
                        buf2.readInt()
                    )
                )
            )
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }

    public record PlayerGameState(int score, int level, int lines) {}
}