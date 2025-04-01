package com.karasu256.tetriscraft;

import com.karasu256.tetriscraft.screen.GameScreen;
import com.karasu256.tetriscraft.screen.SelectGameRoomScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeyBinds {
    private static final KeyBinding KEY_OPEN_MENU = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.tetriscraft.open_menu",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_B,
        "category.tetriscraft"
    ));

    private static final KeyBinding KEY_OPEN_ROOM_LIST = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.tetriscraft.open_room_list",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "category.tetriscraft"
    ));

    private static final KeyBinding KEY_ROTATE_RIGHT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.tetriscraft.rotate_right",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UP,
        "category.tetriscraft"
    ));

    private static final KeyBinding KEY_ROTATE_LEFT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.tetriscraft.rotate_left",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_Z,
        "category.tetriscraft"
    ));

    private static final KeyBinding KEY_HOLD = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.tetriscraft.hold",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_LEFT_SHIFT,
        "category.tetriscraft"
    ));

    private static final KeyBinding KEY_SOFT_DROP = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.tetriscraft.soft_drop",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_S,
        "category.tetriscraft"
    ));

    private static final KeyBinding KEY_HARD_DROP = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.tetriscraft.hard_drop",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_SPACE,
        "category.tetriscraft"
    ));

    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            while (KEY_OPEN_MENU.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new GameScreen());
            }

            while (KEY_OPEN_ROOM_LIST.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new SelectGameRoomScreen(MinecraftClient.getInstance().currentScreen));
            }

            if(MinecraftClient.getInstance().currentScreen instanceof GameScreen gameScreen) {
                while (KEY_ROTATE_RIGHT.wasPressed()) {
                    System.out.println("Rotate Right");
                }

                while (KEY_ROTATE_LEFT.wasPressed()) {
                    System.out.println("Rotate Left");
                }

                while (KEY_HOLD.wasPressed()) {
                    System.out.println("Hold");
                }

                while (KEY_SOFT_DROP.wasPressed()) {
                    System.out.println("Soft Drop");
                }

                while (KEY_HARD_DROP.wasPressed()) {
                    System.out.println("Hard Drop");
                }
            }
        });
    }
}
