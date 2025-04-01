package com.karasu256.tetriscraft.sounds;

import com.karasu256.tetriscraft.TetrisCraft;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final Identifier GAME_MUSIC_ID = Identifier.of(TetrisCraft.MOD_ID, "music-a-2");
    public static SoundEvent GAME_MUSIC_EVENT;

    public static final Identifier LINE_DELETED_1_ID = Identifier.of(TetrisCraft.MOD_ID, "line_deleted_1");
    public static SoundEvent LINE_DELETED_1_EVENT;

    public static void initialize() {
        TetrisCraft.LOGGER.info("Initializing TetrisCraft sounds");
        GAME_MUSIC_EVENT = Registry.register(
                Registries.SOUND_EVENT,
                GAME_MUSIC_ID,
                SoundEvent.of(GAME_MUSIC_ID));

        LINE_DELETED_1_EVENT = Registry.register(
                Registries.SOUND_EVENT,
                LINE_DELETED_1_ID,
                SoundEvent.of(LINE_DELETED_1_ID));
    }
}
