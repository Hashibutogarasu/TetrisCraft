package com.karasu256.tetriscraft.networking;

import net.minecraft.util.Identifier;
import com.karasu256.tetriscraft.TetrisCraft;

public class TetrisPackets {
    public static final Identifier CREATE_ROOM = Identifier.of(TetrisCraft.MOD_ID, "create_room");
    public static final Identifier JOIN_ROOM = Identifier.of(TetrisCraft.MOD_ID, "join_room");
    public static final Identifier LEAVE_ROOM = Identifier.of(TetrisCraft.MOD_ID, "leave_room");
    public static final Identifier ATTACK_EVENT = Identifier.of(TetrisCraft.MOD_ID, "attack_event");
    public static final Identifier GAME_STATE_UPDATE = Identifier.of(TetrisCraft.MOD_ID, "game_state_update");
}