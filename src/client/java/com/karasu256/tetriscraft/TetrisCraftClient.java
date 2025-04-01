package com.karasu256.tetriscraft;

import com.karasu256.tetriscraft.networking.NetworkManager;
import com.karasu256.tetriscraft.networking.client.ClientNetworkManager;
import com.karasu256.tetriscraft.util.MusicUtility;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TetrisCraftClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(TetrisCraftClient.class);
	public static List<GameRoom> clientRoomsCache = new ArrayList<>();
	public static GameRoom currentRoom;

	@Override
	public void onInitializeClient() {
		ModKeyBinds.register();

		NetworkManager.registerS2CPacketTypes();
		ClientNetworkManager.registerClientHandlers();
		
		// 毎ティックごとに音楽の状態を更新するためのイベントリスナーを登録
		registerTickEvents();
	}
	
	/**
	 * ティック関連のイベントリスナーを登録
	 */
	private void registerTickEvents() {
		// クライアントティックイベントリスナー
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// 音楽再生状態の更新
			MusicUtility.tick();
		});
	}
}