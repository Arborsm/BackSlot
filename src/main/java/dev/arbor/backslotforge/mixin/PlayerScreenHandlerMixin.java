package dev.arbor.backslotforge.mixin;

import com.mojang.datafixers.util.Pair;
import dev.arbor.backslotforge.BackSlotMain;
import dev.arbor.backslotforge.client.sprite.BackSlotSprites;
import dev.arbor.backslotforge.network.BackSlotServerPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryMenu.class)
public abstract class PlayerScreenHandlerMixin extends RecipeBookMenu<TransientCraftingContainer> {
    @Unique
    private static final boolean backSlotForge$changeArrangement = BackSlotMain.CONFIG.changeSlotArrangement;

    public PlayerScreenHandlerMixin(MenuType<InventoryMenu> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    // Tried different injection points to fix a mod compatibility bug, but it didn't work
    @Inject(method = "<init>*", at = @At("TAIL"))
    private void onConstructed(Inventory inventory, boolean onServer, Player owner, CallbackInfo info) {
        int backSlotX = BackSlotMain.CONFIG.backSlotX;
        int backSlotY = BackSlotMain.CONFIG.backSlotY;

        int beltSlotX = BackSlotMain.CONFIG.beltSlotX;
        int beltSlotY = BackSlotMain.CONFIG.beltSlotY;
        if (backSlotForge$changeArrangement) {
            backSlotX += 75;
            backSlotY += 22;
            beltSlotX += 57;
            beltSlotY += 40;
        }

        this.addSlot(new Slot(inventory, 41, 77 + backSlotX, 44 + backSlotY) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return BackSlotServerPacket.isItemAllowed(stack, 41);
            }

            @Override
            public boolean mayPickup(@NotNull Player playerEntity) {
                ItemStack itemStack = this.getItem();
                return (itemStack.isEmpty() || playerEntity.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack)) && super.mayPickup(playerEntity);
            }

            @OnlyIn(Dist.CLIENT)
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, BackSlotSprites.EMPTY_BACK_SLOT_TEXTURE);
            }

        });

        this.addSlot(new Slot(inventory, 42, 77 + beltSlotX, 26 + beltSlotY) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return BackSlotServerPacket.isItemAllowed(stack, 42);
            }

            @Override
            public boolean mayPickup(@NotNull Player playerEntity) {
                ItemStack itemStack = this.getItem();
                return (itemStack.isEmpty() || playerEntity.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack)) && super.mayPickup(playerEntity);
            }

            @OnlyIn(Dist.CLIENT)
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, BackSlotSprites.EMPTY_BELT_SLOT_TEXTURE);
            }

        });

    }

}