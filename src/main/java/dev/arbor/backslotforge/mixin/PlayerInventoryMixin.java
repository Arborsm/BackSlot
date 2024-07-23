package dev.arbor.backslotforge.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Inventory.class)
public abstract class PlayerInventoryMixin implements Container {
    @Shadow
    @Final
    @Mutable
    private List<NonNullList<ItemStack>> compartments;

    @Unique
    private NonNullList<ItemStack> backSlotForge$backSlot;
    @Unique
    private NonNullList<ItemStack> backSlotForge$beltSlot;


    @Inject(method = "<init>*", at = @At("RETURN"))
    private void onConstructed(Player playerEntity, CallbackInfo info) {
        this.backSlotForge$backSlot = NonNullList.withSize(1, ItemStack.EMPTY);
        this.backSlotForge$beltSlot = NonNullList.withSize(1, ItemStack.EMPTY);
        this.compartments = new ArrayList<>(compartments);
        this.compartments.add(backSlotForge$backSlot);
        this.compartments.add(backSlotForge$beltSlot);
        this.compartments = ImmutableList.copyOf(this.compartments);
    }

    @Inject(method = "save", at = @At("TAIL"))
    public void serializeMixin(ListTag tag, CallbackInfoReturnable<ListTag> info) {
        if (!this.backSlotForge$backSlot.get(0).isEmpty()) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte) (110));
            this.backSlotForge$backSlot.get(0).save(compoundTag);
            tag.add(compoundTag);
        }
        if (!this.backSlotForge$beltSlot.get(0).isEmpty()) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte) (111));
            this.backSlotForge$beltSlot.get(0).save(compoundTag);
            tag.add(compoundTag);
        }

    }

    @Inject(method = "load", at = @At("TAIL"))
    public void deserializeMixin(ListTag tag, CallbackInfo info) {
        this.backSlotForge$backSlot.clear();
        this.backSlotForge$beltSlot.clear();
        for (int i = 0; i < tag.size(); ++i) {
            CompoundTag compoundTag = tag.getCompound(i);
            int slot = compoundTag.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.of(compoundTag);
            if (!itemStack.isEmpty()) {
                if (slot >= 110 && slot < this.backSlotForge$backSlot.size() + 110) {
                    this.backSlotForge$backSlot.set(slot - 110, itemStack);
                } else if (slot >= 111 && slot < this.backSlotForge$beltSlot.size() + 111) {
                    this.backSlotForge$beltSlot.set(slot - 111, itemStack);
                }
            }
        }
    }

    @Inject(method = "getContainerSize", at = @At("HEAD"), cancellable = true)
    public void sizeMixin(CallbackInfoReturnable<Integer> info) {
        int size = 0;
        for (NonNullList<ItemStack> list : compartments) {
            size += list.size();
        }
        info.setReturnValue(size);
    }

    @Inject(method = "isEmpty", at = @At("HEAD"), cancellable = true)
    public void isEmptyMixin(CallbackInfoReturnable<Boolean> info) {
        if (!this.backSlotForge$backSlot.isEmpty() || !this.backSlotForge$beltSlot.isEmpty()) {
            info.setReturnValue(false);
        }
    }

}