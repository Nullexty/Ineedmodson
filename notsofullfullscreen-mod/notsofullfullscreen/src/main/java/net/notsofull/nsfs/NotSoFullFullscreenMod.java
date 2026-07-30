package net.notsofull.nsfs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class NotSoFullFullscreenMod implements ClientModInitializer {
    public static final String MOD_ID = "notsofullfullscreen";
    public static NsfsConfig CONFIG;

    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));

    private static KeyBinding toggleKey;
    private static boolean appliedStartupState = false;

    @Override
    public void onInitializeClient() {
        CONFIG = NsfsConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.notsofullfullscreen.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!appliedStartupState) {
                appliedStartupState = true;
                if (CONFIG.enabled) {
                    WindowModeHelper.setEnabled(true);
                }
            }

            while (toggleKey.wasPressed()) {
                boolean newState = !WindowModeHelper.isEnabled();
                WindowModeHelper.setEnabled(newState);
                CONFIG.enabled = newState;
                CONFIG.save();
            }
        });
    }
}
