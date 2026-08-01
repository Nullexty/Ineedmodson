package net.notsofull.nsfs;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

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
                    // +1 pixel of height stops Windows from silently treating this as real
                    // exclusive fullscreen (which caused the alt-tab flash and capture lag).
                    GLFW.glfwSetWindowMonitor(handle, 0L, 0, 0, mode.width(), mode.height() + 1, GLFW.GLFW_DONT_CARE);
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
