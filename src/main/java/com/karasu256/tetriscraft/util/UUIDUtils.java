package com.karasu256.tetriscraft.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDUtils {
    public static NbtCompound getUUIDNbts(List<UUID> uuids) {
        NbtCompound nbt = new NbtCompound();
        for (int i = 0; i < uuids.size(); i++) {
            nbt.putByteArray("uuid" + i, toByteArray(uuids.get(i)));
        }
        return nbt;
    }

    public static List<UUID> getUUIDList(@Nullable NbtList nbt) {
        if (nbt == null) {
            return new ArrayList<>();
        }

        List<UUID> uuids = new ArrayList<>();
        for (int i = 0; i < nbt.size(); i++) {
            uuids.add(fromByteArray((nbt.getCompound(i)).getByteArray("uuid")));
        }
        return uuids;
    }

    public static List<UUID> getUUIDList(@Nullable NbtCompound nbt) {
        if (nbt == null) {
            return new ArrayList<>();
        }

        List<UUID> uuids = new ArrayList<>();
        for (int i = 0; nbt.contains("uuid" + i); i++) {
            uuids.add(fromByteArray(nbt.getByteArray("uuid" + i)));
        }
        return uuids;
    }

    public static UUID fromByteArray(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        return new UUID(high, low);
    }

    public static byte[] toByteArray(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
