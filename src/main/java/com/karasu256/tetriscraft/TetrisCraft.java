package com.karasu256.tetriscraft;

import net.fabricmc.api.ModInitializer;
import com.karasu256.tetriscraft.networking.NetworkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TetrisCraft implements ModInitializer {
    public static final String MOD_ID = "tetriscraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // サーバー側のパケットハンドラーを登録
        NetworkManager.registerC2SPacketTypes();
        NetworkManager.registerServerHandlers();
        LOGGER.info("TetrisCraft networking initialized");
    }
}