package com.karasu256.tetriscraft;

import com.karasu256.tetriscraft.networking.NetworkManager;
import com.karasu256.tetriscraft.networking.client.ClientNetworkManager;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TetrisCraftClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(TetrisCraftClient.class);

	@Override
	public void onInitializeClient() {
		ModKeyBinds.register();

		NetworkManager.registerS2CPacketTypes();
		ClientNetworkManager.registerClientHandlers();
	}
}