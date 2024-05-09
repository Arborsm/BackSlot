package net.backslot.network;

import chronosacaria.mcdw.bases.McdwGlaive;
import chronosacaria.mcdw.bases.McdwHammer;
import chronosacaria.mcdw.bases.McdwSickle;
import chronosacaria.mcdw.bases.McdwSpear;
import chronosacaria.mcdw.bases.McdwStaff;
import net.backslot.BackSlotMain;
import net.backslot.sound.BackSlotSounds;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.medievalweapons.item.Big_Axe_Item;
import net.medievalweapons.item.Francisca_Item;
import net.medievalweapons.item.Healing_Staff_Item;
import net.medievalweapons.item.Javelin_Item;
import net.medievalweapons.item.Lance_Item;
import net.medievalweapons.item.Long_Sword_Item;
import net.medievalweapons.item.Sickle_Item;
import net.medievalweapons.item.Small_Axe_Item;
import net.medievalweapons.item.Thalleous_Sword_Item;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.OnAStickItem;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.TridentItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class SwitchPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<SwitchPacket> {

    @Override
    public void receive(SwitchPacket payload, ServerPlayNetworking.Context context) {

        int slot = payload.slotId();

        context.player().server.execute(() -> {
            // player inventory and selected slot
            PlayerInventory playerInventory = context.player().getInventory();
            int selectedSlot = playerInventory.selectedSlot;

            // think in a way of pulling out case (although it can be just a putting back)
            int slotToPullOutFrom = slot;
            int slotToPullOutTo = selectedSlot;

            // pull out to offhand if pulling out from belt slot and offhand switch is on,
            // aka belt slot always goes with offhand
            if (BackSlotMain.CONFIG.offhandSwitch) {
                if (slotToPullOutTo != 40) {
                    if (slotToPullOutFrom == 42) {
                        slotToPullOutTo = 40;
                    }
                }
            }

            // shield in the back would always be pulled out to offhand
            if (BackSlotMain.CONFIG.offhandShield) {
                if (slotToPullOutTo != 40) {
                    if (slotToPullOutFrom == 41) {
                        ItemStack backSlotStack = playerInventory.getStack(slotToPullOutFrom);
                        boolean shouldPullOutToOffHand = backSlotStack.getItem() instanceof ShieldItem;
                        if (shouldPullOutToOffHand) {
                            slotToPullOutTo = 40;
                        }
                    }
                }
            }

            // fallback early to offhand if failure in main hand is expected
            if (BackSlotMain.CONFIG.offhandFallback) {
                if (slotToPullOutTo != 40) {
                    ItemStack slotStack = playerInventory.getStack(slotToPullOutFrom);
                    if (slotStack.isEmpty()) {
                        ItemStack mainHandStack = playerInventory.getStack(slotToPullOutTo);
                        boolean nothingsGonnaHappenWithTheMainHand = mainHandStack.isEmpty() || !isItemAllowed(mainHandStack, slotToPullOutFrom);
                        if (nothingsGonnaHappenWithTheMainHand) {
                            ItemStack offHandStack = playerInventory.offHand.get(0);
                            if (isItemAllowed(offHandStack, slotToPullOutFrom)) {
                                slotToPullOutTo = 40;
                            }
                        }
                    }
                }
            }

            // check items in both slots
            ItemStack stackInSlotToPullOutFrom = playerInventory.getStack(slotToPullOutFrom);
            ItemStack stackInSlotToPullOutTo = playerInventory.getStack(slotToPullOutTo);

            // switching is meaningless if both slots are empty
            boolean bothSlotsAreEmpty = stackInSlotToPullOutFrom.isEmpty() && stackInSlotToPullOutTo.isEmpty();
            if (bothSlotsAreEmpty) {
                return;
            }

            // slot and stack information for putting back
            int slotToPutBackTo = slotToPullOutFrom;
            ItemStack stackToPutBack = stackInSlotToPullOutTo;

            // check conditions
            boolean doneSwitching = false;
            boolean canPutBack = isItemAllowed(stackToPutBack, slotToPutBackTo);
            if (canPutBack) {
                // just do switch
                playerInventory.setStack(slotToPullOutTo, stackInSlotToPullOutFrom); // pull out
                playerInventory.setStack(slotToPutBackTo, stackToPutBack); // put back
                playerInventory.markDirty();
                doneSwitching = true;
            } else if (BackSlotMain.CONFIG.putAside) {
                // try to put aside to an empty inventory slot if there is any
                int slotToPutAside = playerInventory.getEmptySlot();
                boolean canPutAside = slotToPutAside >= 0;
                if (canPutAside) {
                    // do switch while putting item being held aside
                    ItemStack stackToPutAside = stackToPutBack;
                    stackToPutBack = ItemStack.EMPTY;
                    playerInventory.setStack(slotToPutAside, stackToPutAside); // put aside
                    playerInventory.setStack(slotToPullOutTo, stackInSlotToPullOutFrom); // pull out
                    playerInventory.setStack(slotToPutBackTo, stackToPutBack); // put back
                    playerInventory.markDirty();
                    doneSwitching = true;
                } else if (BackSlotMain.CONFIG.dropHolding) {
                    // even drop an item stack being held in order to make an empty space for
                    // pulling out
                    ItemStack stackToDrop = playerInventory.removeStack(slotToPullOutTo);
                    ItemStack stackRemaining = playerInventory.getStack(slotToPullOutTo);
                    // line below is from implementation of ServerPlayerEntity.dropSelectedItem()
                    // without fully understanding what it does
                    context.player().currentScreenHandler.getSlotIndex(playerInventory, slotToPullOutTo).ifPresent((i) -> {
                        context.player().currentScreenHandler.setPreviousTrackedSlot(i, stackRemaining);
                    });
                    context.player().dropItem(stackToDrop, false, true);
                    stackToPutBack = ItemStack.EMPTY;
                    // do switch with an empty hand
                    playerInventory.setStack(slotToPullOutTo, stackInSlotToPullOutFrom); // pull out
                    playerInventory.setStack(slotToPutBackTo, stackToPutBack); // put back
                    playerInventory.markDirty();
                    doneSwitching = true;
                }
            }

            // play sound if done switching
            if (doneSwitching) {
                if (BackSlotMain.CONFIG.backslotSounds) {
                    if (stackInSlotToPullOutTo.isEmpty() && !stackInSlotToPullOutFrom.isEmpty()) {
                        if (stackInSlotToPullOutFrom.getItem() instanceof SwordItem) {
                            // pulling out sword to an empty hand
                            context.player().getWorld().playSound(null, context.player().getBlockPos(), BackSlotSounds.SHEATH_SWORD_EVENT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        } else {
                            // pulling out others to an empty hand
                            context.player().getWorld().playSound(null, context.player().getBlockPos(), (SoundEvent) SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        }
                    } else if (stackInSlotToPullOutFrom.getItem() instanceof SwordItem) {
                        // pulling out sword to a non empty hand
                        context.player().getWorld().playSound(null, context.player().getBlockPos(), BackSlotSounds.SHEATH_SWORD_EVENT, SoundCategory.PLAYERS, 1.0F,
                                0.9F + context.player().getRandom().nextFloat() * 0.2F);
                    } else if (stackInSlotToPullOutTo.getItem() instanceof SwordItem) {
                        // putting back sword item (including while pulling out what there was other
                        // than sword)
                        context.player().getWorld().playSound(null, context.player().getBlockPos(), BackSlotSounds.PACK_UP_ITEM_EVENT, SoundCategory.PLAYERS, 1.0F,
                                0.9F + context.player().getRandom().nextFloat() * 0.2F);
                    } else if (!stackInSlotToPullOutTo.isEmpty()) {
                        // putting back other than sword
                        context.player().getWorld().playSound(null, context.player().getBlockPos(), (SoundEvent) SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        // public void playSound(@Nullable Entity source, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
                    }
                }
            }
        });
    }

    public static boolean isItemAllowed(ItemStack stack, int slot) {
        // BeltSlot
        if (slot == 42) {
            if (BackSlotMain.isMcdwLoaded && (stack.getItem() instanceof McdwHammer || stack.getItem() instanceof McdwGlaive || stack.getItem() instanceof McdwSpear
                    || stack.getItem() instanceof McdwSickle || stack.getItem() instanceof McdwStaff)) {
                return false;
            }
            if (BackSlotMain.isMedievalWeaponsLoaded && (stack.getItem() instanceof Small_Axe_Item || stack.getItem() instanceof Long_Sword_Item || stack.getItem() instanceof Big_Axe_Item
                    || stack.getItem() instanceof Javelin_Item || stack.getItem() instanceof Lance_Item || stack.getItem() instanceof Healing_Staff_Item
                    || stack.getItem() instanceof Thalleous_Sword_Item || stack.getItem() instanceof Sickle_Item)) {
                return false;
            }
        }
        // BackSlot
        if (stack.isEmpty() || stack.getItem() instanceof ToolItem
                || (slot == 41 && (stack.isIn(BackSlotMain.BACKSLOT_ITEMS) || stack.getItem() instanceof RangedWeaponItem || stack.getItem() instanceof FishingRodItem
                        || stack.getItem() instanceof TridentItem || stack.getItem() instanceof OnAStickItem || (BackSlotMain.isMedievalWeaponsLoaded && stack.getItem() instanceof Francisca_Item)))
                || (slot == 42 && (stack.isIn(BackSlotMain.BELTSLOT_ITEMS) || stack.getItem() instanceof FlintAndSteelItem || stack.getItem() instanceof ShearsItem
                        || (BackSlotMain.isMedievalWeaponsLoaded && stack.getItem() instanceof Francisca_Item)))) {
            return true;
        } else {
            return false;
        }
    }

}