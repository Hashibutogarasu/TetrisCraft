package com.karasu256.tetriscraft;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.karasu256.tetriscraft.util.UUIDUtils;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class GameRoom {
    private final UUID id;
    private final String name;
    private final UUID hostPlayerId;
    private final List<UUID> playerIds;

    public static final PacketCodec<PacketByteBuf, GameRoom> CODEC = PacketCodec.of((value, buf) -> {
        buf.writeUuid(value.id);
        buf.writeString(value.name);
        buf.writeUuid(value.hostPlayerId);
        buf.writeNbt(UUIDUtils.getUUIDNbts(value.playerIds));
    }, buf -> {
        UUID id = buf.readUuid();
        String name = buf.readString(32767);
        UUID hostPlayerId = buf.readUuid();
        List<UUID> playerIds = UUIDUtils.getUUIDList(buf.readNbt());
        return new GameRoom(id, name, hostPlayerId, playerIds);
    });

    public GameRoom(UUID id, String name, UUID hostPlayerId) {
        this(id, name, hostPlayerId, new ArrayList<>());
    }

    public GameRoom(UUID id, String name, UUID hostPlayerId, List<UUID> playerIds) {
        this.id = id;
        this.name = name;
        this.hostPlayerId = hostPlayerId;
        this.playerIds = new ArrayList<>(playerIds);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UUID getHostPlayerId() {
        return hostPlayerId;
    }

    public List<UUID> getPlayerIds() {
        return playerIds;
    }

    public void addPlayer(UUID playerId) {
        playerIds.add(playerId);
    }

    public void removePlayer(UUID playerId) {
        playerIds.remove(playerId);
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("id", id);
        nbt.putString("name", name);
        nbt.putUuid("hostPlayerId", hostPlayerId);
        nbt.put("playerIds", UUIDUtils.getUUIDNbts(playerIds));
        return nbt;
    }

    public static GameRoom fromNbt(NbtCompound nbt) {
        UUID id = nbt.getUuid("id");
        String name = nbt.getString("name");
        UUID hostPlayerId = nbt.getUuid("hostPlayerId");
        List<UUID> playerIds = UUIDUtils.getUUIDList(nbt.getList("playerIds", NbtElement.COMPOUND_TYPE));
        return new GameRoom(id, name, hostPlayerId, playerIds);
    }
}
