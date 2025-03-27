package com.karasu256.tetriscraft.networking.packet;

import java.util.List;
import java.util.UUID;

import com.karasu256.tetriscraft.game.GameEventType;
import com.karasu256.tetriscraft.util.UUIDUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import com.karasu256.tetriscraft.TetrisCraft;

public record AttackEventPayload(int lines, List<UUID> targets, GameEventType eventType) implements CustomPayload {

    public static final CustomPayload.Id<AttackEventPayload> TYPE = new CustomPayload.Id<>(
            Identifier.of(TetrisCraft.MOD_ID, "attack_event"));
    public static final PacketCodec<PacketByteBuf, AttackEventPayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                buf.writeInt(payload.lines);
                buf.writeNbt(UUIDUtils.getUUIDNbts(payload.targets));
                buf.writeEnumConstant(payload.eventType);
            },
            buf -> new AttackEventPayload(
                    buf.readInt(),
                    UUIDUtils.getUUIDList(buf.readNbt()),
                    buf.readEnumConstant(GameEventType.class)));

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}