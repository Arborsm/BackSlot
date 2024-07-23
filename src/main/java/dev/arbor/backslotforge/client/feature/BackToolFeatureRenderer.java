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
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class BackToolFeatureRenderer extends ItemInHandLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private final ItemInHandRenderer heldItemRenderer;

    public BackToolFeatureRenderer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> context, ItemInHandRenderer heldItemRenderer) {
        super(context, heldItemRenderer);
        this.heldItemRenderer = heldItemRenderer;
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, @NotNull MultiBufferSource vertexConsumerProvider, int i, @NotNull AbstractClientPlayer livingEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack backSlotStack = livingEntity.getInventory().getItem(41);
        if (livingEntity instanceof AbstractClientPlayer && !backSlotStack.isEmpty()) {
            matrixStack.pushPose();
            ModelPart modelPart = this.getParentModel().body;
            modelPart.translateAndRotate(matrixStack);
            Item backSloItem = backSlotStack.getItem();

            if (backSloItem instanceof TridentItem) {
                matrixStack.mulPose(Axis.YP.rotationDegrees(52.0F));
                matrixStack.mulPose(Axis.XP.rotationDegrees(40.0F));
                matrixStack.mulPose(Axis.ZP.rotationDegrees(-25.0F));
                matrixStack.translate(-0.26D, 0.0D, 0.0D);
                if (!livingEntity.hasItemInSlot(EquipmentSlot.CHEST))
                    matrixStack.translate(0.05F, 0.0F, 0.0F);
                matrixStack.scale(1.0F, -1.0F, -1.0F);
                heldItemRenderer.renderItem(livingEntity, backSlotStack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, matrixStack, vertexConsumerProvider, i);
            } else {
                matrixStack.translate(0.0D, 0.0D, 0.16D);
                matrixStack.scale(BackSlotMain.CONFIG.backslotScaling, BackSlotMain.CONFIG.backslotScaling, BackSlotMain.CONFIG.backslotScaling);
                if (backSlotStack.getItem() instanceof FishingRodItem || backSlotStack.getItem() instanceof FoodOnAStickItem) {
                    matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                    matrixStack.translate(0.0D, -0.3D, 0.0D);
                }
                if (livingEntity.hasItemInSlot(EquipmentSlot.CHEST))
                    matrixStack.translate(0.0F, 0.0F, 0.06F);

                heldItemRenderer.renderItem(livingEntity, backSlotStack, ItemDisplayContext.HEAD, false, matrixStack, vertexConsumerProvider, i);
            }
            matrixStack.popPose();
        }
    }

}