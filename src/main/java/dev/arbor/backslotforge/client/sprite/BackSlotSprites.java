package dev.arbor.backslotforge.client.sprite;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.arbor.backslotforge.BackSlotMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class BackSlotSprites {
    // assets/minecraft/atlases/blocks.json
    public static final ResourceLocation EMPTY_BACK_SLOT_TEXTURE = new ResourceLocation("backslot", "gui/empty_back_slot");
    public static final ResourceLocation EMPTY_BELT_SLOT_TEXTURE = new ResourceLocation("backslot", "gui/empty_belt_slot");
    private static final ResourceLocation WIDGETS_TEXTURE = new ResourceLocation("textures/gui/widgets.png");

    @SubscribeEvent(priority = EventPriority.LOW)
    public void init(RenderGuiEvent.Pre event) {
        GuiGraphics guiGraphics = event.getGuiGraphics();
        Minecraft client = Minecraft.getInstance();
        var scale = (float) event.getWindow().getGuiScale();
        Player playerEntity = client.player;
        assert playerEntity != null;
        ItemStack backSlotStack = playerEntity.getInventory().getItem(41);
        ItemStack beltSlotStack = playerEntity.getInventory().getItem(42);

        // check everything
        if (BackSlotMain.CONFIG.disableBackslotHud) return;
        if (client.options.hideGui && event.isCanceled()) return;
        if (backSlotStack.isEmpty() && beltSlotStack.isEmpty()) return;

        int i = guiGraphics.guiWidth() / 2;
        int p = guiGraphics.guiHeight() - 16 - 3;
        HumanoidArm arm = playerEntity.getMainArm().getOpposite();
        // RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        // RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        guiGraphics.blit(WIDGETS_TEXTURE, i - 91 + (arm == HumanoidArm.LEFT ? -29 : 0) + BackSlotMain.CONFIG.hudSlotX,
                guiGraphics.guiHeight() - 23 + BackSlotMain.CONFIG.hudSlotY, 24, 22, 29, 24);
        renderHotbarItem(guiGraphics, client, i - 91 + (arm == HumanoidArm.LEFT ? -26 : 0) + BackSlotMain.CONFIG.hudSlotX, p + BackSlotMain.CONFIG.hudSlotY, scale, playerEntity,
                backSlotStack);
        RenderSystem.enableBlend();
        // RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        guiGraphics.blit(WIDGETS_TEXTURE, i - 112 + (arm == HumanoidArm.LEFT ? -29 : 0) + BackSlotMain.CONFIG.hudSlotX,
                guiGraphics.guiHeight() - 23 + BackSlotMain.CONFIG.hudSlotY, 24, 22, 29, 24);
        renderHotbarItem(guiGraphics, client, i - 112 + (arm == HumanoidArm.LEFT ? -26 : 0) + BackSlotMain.CONFIG.hudSlotX, p + BackSlotMain.CONFIG.hudSlotY, scale, playerEntity,
                beltSlotStack);

    }

    private static void renderHotbarItem(GuiGraphics context, Minecraft client, int x, int y, float f, Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        float g = (float) stack.getPopTime() - f;
        if (g > 0.0f) {
            float h = 1.0f + g / 5.0f;
            context.pose().pushPose();
            context.pose().translate(x + 8, y + 12, 0.0f);
            context.pose().scale(1.0f / h, (h + 1.0f) / 2.0f, 1.0f);
            context.pose().translate(-(x + 8), -(y + 12), 0.0f);
        }
        context.renderItem(player, stack, x, y, 0);
        if (g > 0.0f) {
            context.pose().popPose();
        }
        context.renderItemDecorations(client.font, stack, x, y);
    }

}
