package com.karasu256.tetriscraft.util;

import com.karasu256.tetriscraft.TetrisCraftClient;
import com.karasu256.tetriscraft.screen.IScreen;
import com.karasu256.tetriscraft.sounds.ModSounds;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;

/**
 * 音楽の再生と管理を担当するユーティリティクラス
 */
public class MusicUtility {
    private static boolean isMusicPlaying = false;
    private static PositionedSoundInstance currentMusicInstance;
    private static String currentMusicId = null;

    /**
     * 指定した音楽を再生する
     * 
     * @param soundEvent 再生する音楽のSoundEvent
     * @param volume 音量（0.0〜1.0）
     * @param pitch ピッチ（0.5〜2.0、1.0が通常）
     * @return 再生に成功した場合true
     */
    public static boolean playMusic(SoundEvent soundEvent, float volume, float pitch) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // 既存の音楽を停止
        stopCurrentMusic();
        
        if (soundEvent != null) {
            currentMusicInstance = PositionedSoundInstance.master(soundEvent, pitch, volume);
            if (currentMusicInstance != null) {
                client.getSoundManager().play(currentMusicInstance);
                isMusicPlaying = true;
                currentMusicId = soundEvent.id().toString();
                TetrisCraftClient.LOGGER.info("Started playing music: {}", currentMusicId);
                return true;
            }
        }
        
        TetrisCraftClient.LOGGER.error("Failed to play music: sound event is null or invalid");
        return false;
    }

    /**
     * ゲームBGMを再生する
     * 
     * @return 再生に成功した場合true
     */
    public static boolean playGameMusic() {
        return playMusic(ModSounds.GAME_MUSIC_EVENT, 1.0F, 1.0F);
    }

    /**
     * 現在再生中の音楽を停止する
     */
    public static void stopCurrentMusic() {
        if (isMusicPlaying && currentMusicInstance != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.getSoundManager().stop(currentMusicInstance);
            TetrisCraftClient.LOGGER.info("Stopped playing music: {}", currentMusicId);
            isMusicPlaying = false;
            currentMusicInstance = null;
            currentMusicId = null;
        }
    }

    /**
     * 音楽が再生中かどうかを確認する
     * 
     * @return 音楽が再生中の場合true
     */
    public static boolean isMusicCurrentlyPlaying() {
        // 再生中フラグがtrueかつ、インスタンスが有効な場合
        if (isMusicPlaying && currentMusicInstance != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            SoundManager soundManager = client.getSoundManager();
            
            // サウンドマネージャーに問い合わせて実際に再生中かどうかを確認
            return soundManager.isPlaying(currentMusicInstance);
        }
        
        // 再生中フラグがfalseまたはインスタンスが無効な場合
        return false;
    }

    /**
     * サウンドシステムの状態を更新し、音楽の再生状態を確認する
     * 毎ティック呼び出されることを想定
     */
    public static void tick() {
        if (isMusicPlaying && currentMusicInstance != null) {
            // 実際の再生状態を確認
            boolean actuallyPlaying = isMusicCurrentlyPlaying();
            
            // 再生が終了していた場合、状態を更新
            if (!actuallyPlaying) {
                TetrisCraftClient.LOGGER.info("Music playback has ended: {}", currentMusicId);
                isMusicPlaying = false;
                currentMusicInstance = null;
                currentMusicId = null;

                Screen screen = MinecraftClient.getInstance().currentScreen;

                if (screen instanceof IScreen) {
                    IScreen iScreen = (IScreen) screen;
                    // ループ再生が必要な場合は音楽を再生
                    if (iScreen.shouldMusicLoop()) {
                        iScreen.playBackgroundMusic();
                        TetrisCraftClient.LOGGER.info("Restarted music playback: {}", currentMusicId);
                    }
                } else {
                    // ループ再生が必要ない場合は音楽を停止
                    stopCurrentMusic();
                }
            }
        }
    }

    /**
     * 音楽が再生中かどうかのフラグを取得
     * 
     * @return 音楽が再生中の場合true
     */
    public static boolean getIsMusicPlaying() {
        return isMusicPlaying;
    }

    /**
     * 現在の画面が指定されたクラスの型かどうかを確認し、その場合はキャストして返す
     * 
     * @param <T> 確認する画面の型
     * @param screenClass 画面のクラスオブジェクト
     * @return キャストされた画面、または一致しない場合はnull
     */
    public static <T extends Screen> T getCurrentScreenAs(Class<T> screenClass) {
        MinecraftClient client = MinecraftClient.getInstance();
        Screen currentScreen = client.currentScreen;
        
        // 現在の画面がnullの場合や、指定されたクラスと一致しない場合はnullを返す
        if (currentScreen == null || !screenClass.isInstance(currentScreen)) {
            return null;
        }
        
        // 指定された型にキャストして返す
        return screenClass.cast(currentScreen);
    }

    /**
     * 現在の画面が指定されたクラスの型かどうかを確認する
     * 
     * @param <T> 確認する画面の型
     * @param screenClass 画面のクラスオブジェクト
     * @return 指定されたクラスの型である場合はtrue
     */
    public static <T extends Screen> boolean isCurrentScreen(Class<T> screenClass) {
        MinecraftClient client = MinecraftClient.getInstance();
        Screen currentScreen = client.currentScreen;
        
        return currentScreen != null && screenClass.isInstance(currentScreen);
    }

    /**
     * 指定された音を再生する
     * 
     * @param soundEvent 再生する音のSoundEvent
     */
    public static void playSound(SoundEvent soundEvent) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (soundEvent != null) {
            PositionedSoundInstance soundInstance = PositionedSoundInstance.master(soundEvent, 1.0F, 1.0F);
            client.getSoundManager().play(soundInstance);
        }
    }
}