package net.backslot.mixin;

import java.util.AbstractMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin {

    @ModifyVariable(method = "repairPlayerGears", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;chooseEquipmentWith(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/Map$Entry;"), ordinal = 0)
    private Map.Entry<EquipmentSlot, ItemStack> repairPlayerGearsMixin(Map.Entry<EquipmentSlot, ItemStack> original, PlayerEntity player, int amount) {
        ItemStack backStack = player.getInventory().getStack(41);
        ItemStack beltStack = player.getInventory().getStack(42);
        boolean backSlotRepairable = !backStack.isEmpty() && backStack.isDamaged()
                && beltStack.getEnchantments().getEnchantments().stream().anyMatch(entry -> entry.matchesId(Enchantments.MENDING.getRegistry()));
        boolean beltSlotRepairable = !beltStack.isEmpty() && beltStack.isDamaged()
                && beltStack.getEnchantments().getEnchantments().stream().anyMatch(entry -> entry.matchesId(Enchantments.MENDING.getRegistry()));

        beltStack.getEnchantments().getEnchantments().stream().anyMatch(entry -> entry.matchesId(Enchantments.MENDING.getRegistry()));
        if (backSlotRepairable || beltSlotRepairable) {
            if (original != null) {
                if (backSlotRepairable && beltSlotRepairable) {
                    if (player.getRandom().nextInt(6) == 0) {
                        return new AbstractMap.SimpleEntry<EquipmentSlot, ItemStack>(EquipmentSlot.OFFHAND, player.getInventory().getStack(41 + player.getRandom().nextInt(2)));
                    } else
                        return original;
                } else if (backSlotRepairable) {
                    if (player.getRandom().nextInt(4) == 0) {
                        return new AbstractMap.SimpleEntry<EquipmentSlot, ItemStack>(EquipmentSlot.OFFHAND, player.getInventory().getStack(41));
                    } else
                        return original;
                } else {
                    if (player.getRandom().nextInt(4) == 0) {
                        return new AbstractMap.SimpleEntry<EquipmentSlot, ItemStack>(EquipmentSlot.OFFHAND, player.getInventory().getStack(42));
                    } else
                        return original;
                }
            } else if (backSlotRepairable && beltSlotRepairable) {
                return new AbstractMap.SimpleEntry<EquipmentSlot, ItemStack>(EquipmentSlot.OFFHAND, player.getInventory().getStack(41 + player.getRandom().nextInt(2)));
            } else if (backSlotRepairable) {
                return new AbstractMap.SimpleEntry<EquipmentSlot, ItemStack>(EquipmentSlot.OFFHAND, player.getInventory().getStack(41));
            } else {
                return new AbstractMap.SimpleEntry<EquipmentSlot, ItemStack>(EquipmentSlot.OFFHAND, player.getInventory().getStack(42));
            }
        } else
            return original;
    }
}
