package dev.arbor.backslotforge.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import dev.arbor.backslotforge.BackSlotForge;
import dev.arbor.backslotforge.network.BackSlotServerPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.glfw.GLFW;

public class SwitchKey {
    public static KeyMapping backSlotKeyBind =
            new KeyMapping("key.backslot.switch_backslot", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.backslot.key");
    public static KeyMapping beltSlotKeyBind =
            new KeyMapping("key.backslot.switch_beltslot", KeyConflictContext.GUI, KeyModifier.SHIFT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.backslot.key");

    public void registerKeyboard(RegisterKeyMappingsEvent event){
        event.register(backSlotKeyBind);
        event.register(beltSlotKeyBind);
    }

    @SubscribeEvent
    public void onKeyInputEvent(TickEvent.PlayerTickEvent event){
        if (event.side == LogicalSide.CLIENT && event.player != null && event.player == Minecraft.getInstance().getCameraEntity())
        {
            if (isKeyPressed(backSlotKeyBind)){
                syncSlotSwitchItem(41);
                return;
            }
            if (isKeyPressed(beltSlotKeyBind)){
                syncSlotSwitchItem(42);
            }
        }
    }

    public static void syncSlotSwitchItem(int slot) {
        BackSlotForge.getPacketHandler().sendToServer(new BackSlotServerPacket(slot));
    }

    public static boolean isKeyPressed(KeyMapping keyBinding) {
        if (keyBinding.isDown()) {
            return true;
        }
        if (keyBinding.getKeyConflictContext().isActive() && keyBinding.getKeyModifier().isActive(keyBinding.getKeyConflictContext())) {
            //Manually check in case keyBinding#pressed just never got a chance to be updated
            return isKeyDown(keyBinding);
        }
        //If we failed, due to us being a key modifier as our key, check the old way
        return KeyModifier.isKeyCodeModifier(keyBinding.getKey()) && isKeyDown(keyBinding);
    }

    private static boolean isKeyDown(KeyMapping keyBinding) {
        InputConstants.Key key = keyBinding.getKey();
        int keyCode = key.getValue();
        if (keyCode != InputConstants.UNKNOWN.getValue()) {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();
            try {
                if (key.getType() == InputConstants.Type.KEYSYM) {
                    return InputConstants.isKeyDown(windowHandle, keyCode);
                } else if (key.getType() == InputConstants.Type.MOUSE) {
                    return GLFW.glfwGetMouseButton(windowHandle, keyCode) == GLFW.GLFW_PRESS;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }
}
