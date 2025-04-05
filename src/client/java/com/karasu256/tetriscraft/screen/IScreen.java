package com.karasu256.tetriscraft.screen;

import com.karasu256.tetriscraft.util.MusicUtility;

import net.minecraft.sound.SoundEvent;

/**
 * 音楽機能を持つゲーム画面のためのインターフェース
 * MusicUtilityと連携して音楽の再生、停止、ループ処理を行う
 */
public interface IScreen {
    
    /**
     * 画面固有のBGMを取得
     * 
     * @return 画面のBGM用SoundEvent
     */
    SoundEvent getBackgroundMusic();
    
    /**
     * 画面の音楽がループ再生されるべきかどうかを取得
     * 
     * @return ループ再生する場合はtrue、一度だけ再生する場合はfalse
     */
    boolean shouldMusicLoop();
    
    /**
     * 画面が表示された際に呼ばれる初期化処理
     * 通常はBGM再生を開始する
     */
    default void initializeMusic() {
        SoundEvent music = getBackgroundMusic();
        if (music != null) {
            playBackgroundMusic();
        }
    }
    
    /**
     * 画面のBGMを再生する
     */
    default void playBackgroundMusic() {
        SoundEvent music = getBackgroundMusic();
        if (music != null) {
            // デフォルトボリュームとピッチで再生
            playBackgroundMusic(0.3f, 1.0f);
        }
    }
    
    /**
     * 画面のBGMを指定したボリュームとピッチで再生する
     * 
     * @param volume 音量（0.0〜1.0）
     * @param pitch ピッチ（0.5〜2.0、1.0が通常）
     */
    default void playBackgroundMusic(float volume, float pitch) {
        SoundEvent music = getBackgroundMusic();
        if (music != null) {
            com.karasu256.tetriscraft.util.MusicUtility.playMusic(music, volume, pitch);
        }
    }
    
    /**
     * 現在再生中の音楽を停止する
     */
    default void stopBackgroundMusic() {
        com.karasu256.tetriscraft.util.MusicUtility.stopCurrentMusic();
    }
    
    /**
     * 毎ティック呼び出される処理
     * ループ設定が有効な場合、音楽が終了したときに自動的に再生を再開する
     */
    default void tickMusic() {
        if (shouldMusicLoop() && !com.karasu256.tetriscraft.util.MusicUtility.isMusicCurrentlyPlaying() &&
                !MusicUtility.getIsMusicPlaying()) {
            // 音楽が終了していて、ループが必要な場合は再生を再開
            playBackgroundMusic();
        }
    }

    default void playSound(SoundEvent soundEvent) {
        MusicUtility.playSound(soundEvent);
    }
}