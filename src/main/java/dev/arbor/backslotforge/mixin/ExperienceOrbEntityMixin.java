package dev.arbor.backslotforge.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.AbstractMap;
import java.util.Map;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbEntityMixin {

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "repairPlayerItems", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getRandomItemWith(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/Map$Entry;"), ordinal = 0)
    private Map.Entry<EquipmentSlot, ItemStack> repairPlayerGearsMixin(Map.Entry<EquipmentSlot, ItemStack> original, Player player, int amount) {
        ItemStack backStack = player.getInventory().getItem(41);
        ItemStack beltStack = player.getInventory().getItem(42);
        boolean backSlotRepairable = !backStack.isEmpty() && backStack.isDamaged() && EnchantmentHelper.getTagEnchantmentLevel(Enchantments.MENDING, backStack) > 0;
        boolean beltSlotRepairable = !beltStack.isEmpty() && beltStack.isDamaged() && EnchantmentHelper.getTagEnchantmentLevel(Enchantments.MENDING, beltStack) > 0;
        if (backSlotRepairable || beltSlotRepairable) {
            if (original != null) {
                if (backSlotRepairable && beltSlotRepairable) {
                    if (player.getRandom().nextInt(6) == 0) {
                        return new AbstractMap.SimpleEntry<>(EquipmentSlot.OFFHAND, player.getInventory().getItem(41 + player.getRandom().nextInt(2)));
                    } else
                        return original;
                } else if (backSlotRepairable) {
                    if (player.getRandom().nextInt(4) == 0) {
                        return new AbstractMap.SimpleEntry<>(EquipmentSlot.OFFHAND, player.getInventory().getItem(41));
                    } else
                        return original;
                } else {
                    if (player.getRandom().nextInt(4) == 0) {
                        return new AbstractMap.SimpleEntry<>(EquipmentSlot.OFFHAND, player.getInventory().getItem(42));
                    } else
                        return original;
                }
            } else if (backSlotRepairable && beltSlotRepairable) {
                return new AbstractMap.SimpleEntry<>(EquipmentSlot.OFFHAND, player.getInventory().getItem(41 + player.getRandom().nextInt(2)));
            } else if (backSlotRepairable) {
                return new AbstractMap.SimpleEntry<>(EquipmentSlot.OFFHAND, player.getInventory().getItem(41));
            } else {
                return new AbstractMap.SimpleEntry<>(EquipmentSlot.OFFHAND, player.getInventory().getItem(42));
            }
        } else
            return original;
    }
}
