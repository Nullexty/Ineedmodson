package net.notsofull.nsfs;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

/**
 * Implements "borderless fullscreen" / "fullscreen windowed" mode.
 *
 * The trick: Minecraft's own fullscreen toggle calls glfwSetWindowMonitor with an
 * actual monitor handle, which puts GLFW into exclusive fullscreen (this is what
 * options.fullscreen / Window#isFullscreen() tracks). Instead, we leave the window
 * in GLFW "windowed" mode (monitor = NULL) but:
 *   1. strip the OS window decorations (title bar / border), and
 *   2. resize + reposition the window to exactly cover the monitor's resolution.
 *
 * From Minecraft's perspective nothing changed - it's still windowed, isFullscreen()
 * still returns false, and options.fullscreen is never touched. Visually, though,
 * it fills the screen with no border, indistinguishable from real fullscreen.
 */
public final class WindowModeHelper {
    private static boolean enabled = false;
    private static boolean active = false;

    private static int prevX, prevY, prevWidth, prevHeight;

    private WindowModeHelper() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
        apply();
    }

    public static void toggle() {
        setEnabled(!enabled);
    }

    private static void apply() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) {
            return;
        }
        long handle = client.getWindow().getHandle();
        if (handle == 0L) {
            return;
        }

        if (enabled && !active) {
            // If the player is in real exclusive fullscreen, back out of it first -
            // NotSoFullFullscreen only makes sense starting from windowed mode.
            if (client.getWindow().isFullscreen()) {
                client.getWindow().toggleFullscreen();
            }

            int[] xArr = new int[1];
            int[] yArr = new int[1];
            GLFW.glfwGetWindowPos(handle, xArr, yArr);
            prevX = xArr[0];
            prevY = yArr[0];

            int[] wArr = new int[1];
            int[] hArr = new int[1];
            GLFW.glfwGetWindowSize(handle, wArr, hArr);
            prevWidth = wArr[0];
            prevHeight = hArr[0];

            long monitor = GLFW.glfwGetPrimaryMonitor();
            if (monitor != 0L) {
                GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);
                if (mode != null) {
                    GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
                    GLFW.glfwSetWindowMonitor(handle, 0L, 0, 0, mode.width(), mode.height(), GLFW.GLFW_DONT_CARE);
                    active = true;
                }
            }
        } else if (!enabled && active) {
            GLFW.glfwSetWindowMonitor(handle, 0L, prevX, prevY, prevWidth, prevHeight, GLFW.GLFW_DONT_CARE);
            GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
            active = false;
        }
    }
}
