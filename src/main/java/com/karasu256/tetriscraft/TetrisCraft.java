package com.karasu256.tetriscraft;

import net.fabricmc.api.ModInitializer;
import com.karasu256.tetriscraft.networking.NetworkManager;
import com.karasu256.tetriscraft.sounds.ModSounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TetrisCraft implements ModInitializer {
    public static final String MOD_ID = "tetriscraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // サウンドを初期化・登録
        ModSounds.initialize();
        
        // サーバー側のパケットハンドラーを登録
        NetworkManager.registerC2SPacketTypes();
        NetworkManager.registerServerHandlers();
        LOGGER.info("TetrisCraft networking initialized");
    }
}