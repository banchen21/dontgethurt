package com.lastimp.dgh.common.client.hotkey;

import com.lastimp.dgh.common.client.ClientPlatformService;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class KeyBinding {
    public static final String KEY_CATEGORY_DGH = "key.category.dgh";

    public static final String KEY_HEALTH_MENU = "key.dgh.health_menu";
    public static final String KEY_GIVE_UP = "key.dgh.give_up";
    public static final String KEY_CALL_FOR_HELP = "key.dgh.call_for_help";

    public static final Set<KeyMapping> keys = new HashSet<>();

    public static final KeyMapping OPEN_MENU_KEY = addKey(KEY_HEALTH_MENU, GLFW.GLFW_KEY_O);
    public static final KeyMapping GIVE_UP = addKey(KEY_GIVE_UP, GLFW.GLFW_KEY_F);

    private static KeyMapping addKey(String name, int key) {
        var newKey = ClientPlatformService.KEY_HELPER.getKey(name, key);
        KeyBinding.keys.add(newKey);
        return newKey;
    }
}
