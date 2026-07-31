package net.notsofull.nsfs;

import net.minecraft.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

/**
 * Implements "borderless fullscreen" / "fullscreen windowed" mode.
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
        MinecraftClient client = MinecraftClient.method_1551();
        if (client == null || client.method_22683() == null) {
            return;
        }

        long handle = client.method_22683().method_4490();
        if (handle == 0L) {
            return;
        }

        if (enabled && !active) {
            // Leave exclusive fullscreen if enabled.
            if (client.method_22683().method_4498()) {
                client.method_22683().method_4500();
            }

            // Save current window position.
            int[] xArr = new int[1];
            int[] yArr = new int[1];
            GLFW.glfwGetWindowPos(handle, xArr, yArr);
            prevX = xArr[0];
            prevY = yArr[0];

            // Save current window size.
            int[] wArr = new int[1];
            int[] hArr = new int[1];
            GLFW.glfwGetWindowSize(handle, wArr, hArr);
            prevWidth = wArr[0];
            prevHeight = hArr[0];

            long monitor = GLFW.glfwGetPrimaryMonitor();
            if (monitor != 0L) {
                GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);
                if (mode != null) {

                    // Get the monitor's actual position in the virtual desktop.
                    int[] monitorX = new int[1];
                    int[] monitorY = new int[1];
                    GLFW.glfwGetMonitorPos(monitor, monitorX, monitorY);

                    GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);

                    GLFW.glfwSetWindowMonitor(
                            handle,
                            0L,
                            monitorX[0],
                            monitorY[0],
                            mode.width(),
                            mode.height(),
                            GLFW.GLFW_DONT_CARE
                    );

                    active = true;
                }
            }
        } else if (!enabled && active) {
            GLFW.glfwSetWindowMonitor(
                    handle,
                    0L,
                    prevX,
                    prevY,
                    prevWidth,
                    prevHeight,
                    GLFW.GLFW_DONT_CARE
            );
            GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
            active = false;
        }
    }
}
