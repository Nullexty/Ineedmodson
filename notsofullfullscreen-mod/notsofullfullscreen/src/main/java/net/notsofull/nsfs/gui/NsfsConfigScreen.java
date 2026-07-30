package net.notsofull.nsfs.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.notsofull.nsfs.NotSoFullFullscreenMod;
import net.notsofull.nsfs.WindowModeHelper;

public class NsfsConfigScreen extends Screen {
    private final Screen parent;

    public NsfsConfigScreen(Screen parent) {
        super(Text.translatable("screen.notsofullfullscreen.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = this.height / 2 - 20;

        this.addDrawableChild(ButtonWidget.builder(
                statusText(),
                button -> {
                    boolean newState = !WindowModeHelper.isEnabled();
                    WindowModeHelper.setEnabled(newState);
                    NotSoFullFullscreenMod.CONFIG.enabled = newState;
                    NotSoFullFullscreenMod.CONFIG.save();
                    button.setMessage(statusText());
                }
        ).dimensions(centerX - 100, y, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.done"),
                button -> this.close()
        ).dimensions(centerX - 100, y + 30, 200, 20).build());
    }

    private Text statusText() {
        return Text.translatable(
                "options.notsofullfullscreen.toggle",
                WindowModeHelper.isEnabled()
                        ? Text.translatable("options.on")
                        : Text.translatable("options.off")
        );
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
