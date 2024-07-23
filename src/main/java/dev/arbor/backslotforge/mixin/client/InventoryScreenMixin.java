package dev.arbor.backslotforge.mixin.client;

import dev.arbor.backslotforge.BackSlotMain;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {

    @Unique
    private static final ResourceLocation BACK_TEXTURE = new ResourceLocation("backslot", "textures/gui/blank.png");
    @Unique
    private static final boolean backSlotForge$changeArrangement = BackSlotMain.CONFIG.changeSlotArrangement;

    public InventoryScreenMixin(InventoryMenu screenHandler, Inventory playerInventory, Component text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "renderBg", at = @At(value = "RETURN"))
    public void drawBackgroundMixin(GuiGraphics context, float delta, int mouseX, int mouseY, CallbackInfo info) {
        int backSlotX = BackSlotMain.CONFIG.backSlotX;
        int backSlotY = BackSlotMain.CONFIG.backSlotY;

        int beltSlotX = BackSlotMain.CONFIG.beltSlotX;
        int beltSlotY = BackSlotMain.CONFIG.beltSlotY;

        if (backSlotForge$changeArrangement) {
            backSlotX += 57;
            backSlotY += 40;
            beltSlotX += 75;
            beltSlotY += 22;
        }

        context.blit(BACK_TEXTURE, this.leftPos + 76 + backSlotX, this.topPos + 43 + backSlotY, 0.0F, 0.0F, 18, 18, 18, 18);
        context.blit(BACK_TEXTURE, this.leftPos + 76 + beltSlotX, this.topPos + 25 + beltSlotY, 0.0F, 0.0F, 18, 18, 18, 18);

    }

}