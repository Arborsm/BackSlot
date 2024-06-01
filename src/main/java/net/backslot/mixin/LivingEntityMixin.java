package net.backslot.mixin;

import java.util.Arrays;

import com.google.common.collect.Iterables;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "getEquippedItems", at = @At("RETURN"), cancellable = true)
    private void getEquippedItemsMixin(CallbackInfoReturnable<Iterable<ItemStack>> info) {
        if ((Entity) (Object) this instanceof PlayerEntity playerEntity) {
            ItemStack backSlotStack = playerEntity.getInventory().getStack(41);
            ItemStack beltSlotStack = playerEntity.getInventory().getStack(42);
            Iterable<ItemStack> equippedItems = info.getReturnValue();
            Iterable<ItemStack> equippedBackSlotItems = Arrays.asList(backSlotStack, beltSlotStack);
            info.setReturnValue(Iterables.concat(equippedItems, equippedBackSlotItems));
        }
    }

}
