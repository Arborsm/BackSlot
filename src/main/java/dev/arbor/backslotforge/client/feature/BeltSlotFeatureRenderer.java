package dev.arbor.backslotforge.client.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.arbor.backslotforge.BackSlotMain;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class BeltSlotFeatureRenderer extends ItemInHandLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private final ItemInHandRenderer heldItemRenderer;

    public BeltSlotFeatureRenderer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> context, ItemInHandRenderer heldItemRenderer) {
        super(context, heldItemRenderer);
        this.heldItemRenderer = heldItemRenderer;
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, @NotNull MultiBufferSource vertexConsumerProvider, int i, AbstractClientPlayer livingEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack beltSlotStack = livingEntity.getInventory().getItem(42);
        if (livingEntity instanceof AbstractClientPlayer && !beltSlotStack.isEmpty()) {
            matrixStack.pushPose();
            ModelPart modelPart = this.getParentModel().body;
            modelPart.translateAndRotate(matrixStack);
            double switchBeltSide = 0.29D;
            if (BackSlotMain.CONFIG.switchBeltslotSide) {
                switchBeltSide = -0.29D;
            }
            matrixStack.translate(switchBeltSide, 0.5D, 0.05D);
            if (beltSlotStack.getItem() instanceof FlintAndSteelItem) {
                matrixStack.translate(0.01F, 0.0F, -0.1F);
            }
            matrixStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            matrixStack.scale(BackSlotMain.CONFIG.beltslotScaling, BackSlotMain.CONFIG.beltslotScaling, BackSlotMain.CONFIG.beltslotScaling);
            if (beltSlotStack.getItem() instanceof ShearsItem || beltSlotStack.getItem() instanceof FlintAndSteelItem) {
                matrixStack.scale(0.65F, 0.65F, 0.65F);
                if (!livingEntity.hasItemInSlot(EquipmentSlot.CHEST)) {
                    matrixStack.translate(0.0F, 0.0F, 0.015F);
                }
            }
            heldItemRenderer.renderItem(livingEntity, beltSlotStack, ItemDisplayContext.HEAD, false, matrixStack, vertexConsumerProvider, i);
            matrixStack.popPose();
        }
    }

}