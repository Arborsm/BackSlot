package dev.arbor.backslotforge.mixin;

import com.google.common.collect.Iterables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "getAllSlots", at = @At("RETURN"), cancellable = true)
    public void getItemsEquippedWithBackSlotItems(CallbackInfoReturnable<Iterable<ItemStack>> info) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof Player playerEntity) {
            ItemStack backSlotStack = playerEntity.getInventory().getItem(41);
            ItemStack beltSlotStack = playerEntity.getInventory().getItem(42);
            Iterable<ItemStack> equippedItems = info.getReturnValue();
            Iterable<ItemStack> equippedBackSlotItems = Arrays.asList(backSlotStack, beltSlotStack);
            info.setReturnValue(Iterables.concat(equippedItems, equippedBackSlotItems));
        }
    }

}
