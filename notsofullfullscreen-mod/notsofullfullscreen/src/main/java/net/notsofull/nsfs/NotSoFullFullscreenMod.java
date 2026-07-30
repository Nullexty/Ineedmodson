package net.notsofull.nsfs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class NotSoFullFullscreenMod implements ClientModInitializer {
    public static final String MOD_ID = "notsofullfullscreen";
    public static NsfsConfig CONFIG;

    private static KeyBinding toggleKey;
    private static boolean appliedStartupState = false;

    @Override
    public void onInitializeClient() {
        CONFIG = NsfsConfig.load();

        // Unbound by default - set it in Options > Controls > NotSoFullFullscreen.
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.notsofullfullscreen.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.notsofullfullscreen"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Re-apply the saved setting once the window actually exists.
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
