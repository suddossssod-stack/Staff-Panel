package com.staffpanel;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class StaffPanelClient implements ClientModInitializer {
    private KeyBinding openPanelKey;

    @Override
    public void onInitializeClient() {
        openPanelKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.staffpanel.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openPanelKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new StaffPanelScreen(null));
                }
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("staffpanel")
                        .executes(context -> {
                            MinecraftClient.getInstance().setScreen(new StaffPanelScreen(null));
                            return 1;
                        })
                        .then(ClientCommandManager.literal("help").executes(context -> {
                            context.getSource().sendFeedback(Text.literal("Staff Panel: press K or use /staffpanel to open the panel."));
                            return 1;
                        }))));
    }
}
