package dev.arbor.backslotforge.network;

import dev.arbor.backslotforge.BackSlotForge;
import dev.arbor.backslotforge.BackSlotMain;
import dev.arbor.backslotforge.sound.BackSlotSounds;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;

public class BackSlotServerPacket implements IBackSlotPacket  {
    private final int slot;

    public BackSlotServerPacket(int slot) {
        this.slot = slot;
    }

    public static boolean isItemAllowed(ItemStack stack, int slot) {
        // BackSlot
        return stack.isEmpty() || stack.getItem() instanceof TieredItem
                || (slot == 41 && (stack.is(BackSlotMain.BACKSLOT_ITEMS) || stack.getItem() instanceof ProjectileWeaponItem || stack.getItem() instanceof FishingRodItem
                || stack.getItem() instanceof TridentItem || stack.getItem() instanceof FoodOnAStickItem)) || (slot == 42 && (stack.is(BackSlotMain.BELTSLOT_ITEMS)
                || stack.getItem() instanceof FlintAndSteelItem || stack.getItem() instanceof ShearsItem));
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        assert player != null;
        MinecraftServer server = player.getServer();
        assert server != null;

        server.execute(() -> {
            // player inventory and selected slot
            Inventory playerInventory = player.getInventory();

            // think in a way of pulling out a case (although it can be just a putting back)
            int slotToPullOutTo = playerInventory.selected;

            // pull out to offhand if pulling out from belt slot and offhand switch is on,
            // aka belt slot always goes with offhand
            if (BackSlotMain.CONFIG.offhandSwitch) {
                if (slotToPullOutTo != 40) {
                    if (slot == 42) {
                        slotToPullOutTo = 40;
                    }
                }
            }

            // shield in the back would always be pulled out to offhand
            if (BackSlotMain.CONFIG.offhandShield) {
                if (slotToPullOutTo != 40) {
                    if (slot == 41) {
                        ItemStack backSlotStack = playerInventory.getItem(slot);
                        boolean shouldPullOutToOffHand = backSlotStack.getItem() instanceof ShieldItem;
                        if (shouldPullOutToOffHand) {
                            slotToPullOutTo = 40;
                        }
                    }
                }
            }

            // fallback early to offhand if failure in the main hand is expected
            if (BackSlotMain.CONFIG.offhandFallback) {
                if (slotToPullOutTo != 40) {
                    ItemStack slotStack = playerInventory.getItem(slot);
                    if (slotStack.isEmpty()) {
                        ItemStack mainHandStack = playerInventory.getItem(slotToPullOutTo);
                        boolean nothingsGonnaHappenWithTheMainHand = mainHandStack.isEmpty() || !isItemAllowed(mainHandStack, slot);
                        if (nothingsGonnaHappenWithTheMainHand) {
                            ItemStack offHandStack = playerInventory.offhand.get(0);
                            if (isItemAllowed(offHandStack, slot)) {
                                slotToPullOutTo = 40;
                            }
                        }
                    }
                }
            }

            // check items in both slots
            ItemStack stackInSlotToPullOutFrom = playerInventory.getItem(slot);
            ItemStack stackInSlotToPullOutTo = playerInventory.getItem(slotToPullOutTo);

            // switching is meaningless if both slots are empty
            boolean bothSlotsAreEmpty = stackInSlotToPullOutFrom.isEmpty() && stackInSlotToPullOutTo.isEmpty();
            if (bothSlotsAreEmpty) {
                return;
            }

            // slot and stack information for putting back
            ItemStack stackToPutBack = stackInSlotToPullOutTo;

            // check conditions
            boolean doneSwitching = false;
            boolean canPutBack = isItemAllowed(stackToPutBack, slot);
            if (canPutBack) {
                // do switch
                playerInventory.setItem(slotToPullOutTo, stackInSlotToPullOutFrom); // pull out
                playerInventory.setItem(slot, stackToPutBack); // put back
                playerInventory.setChanged();
                doneSwitching = true;
            } else if (BackSlotMain.CONFIG.putAside) {
                // try to put aside to an empty inventory slot if there is any
                int slotToPutAside = playerInventory.getFreeSlot();
                boolean canPutAside = slotToPutAside >= 0;
                if (canPutAside) {
                    // do switch while putting item being held aside
                    ItemStack stackToPutAside = stackToPutBack;
                    stackToPutBack = ItemStack.EMPTY;
                    playerInventory.setItem(slotToPutAside, stackToPutAside); // put aside
                    playerInventory.setItem(slotToPullOutTo, stackInSlotToPullOutFrom); // pull out
                    playerInventory.setItem(slot, stackToPutBack); // put back
                    playerInventory.setChanged();
                    doneSwitching = true;
                } else if (BackSlotMain.CONFIG.dropHolding) {
                    // even drop an item stack being held to make an empty space for
                    // pulling out
                    ItemStack stackToDrop = playerInventory.removeItemNoUpdate(slotToPullOutTo);
                    ItemStack stackRemaining = playerInventory.getItem(slotToPullOutTo);
                    // the line below is from implementation of ServerPlayerEntity.dropSelectedItem()
                    // without fully understanding what it does
                    player.containerMenu.findSlot(playerInventory, slotToPullOutTo).ifPresent((i) -> player.containerMenu.setRemoteSlot(i, stackRemaining));
                    player.drop(stackToDrop, false, true);
                    stackToPutBack = ItemStack.EMPTY;
                    // do switch with an empty hand
                    playerInventory.setItem(slotToPullOutTo, stackInSlotToPullOutFrom); // pull out
                    playerInventory.setItem(slot, stackToPutBack); // put back
                    playerInventory.setChanged();
                    doneSwitching = true;
                }
            }

            // play sound if done switching
            if (doneSwitching) {
                if (BackSlotMain.CONFIG.backslotSounds) {
                    try(var level = player.level()) {
                        if (stackInSlotToPullOutTo.isEmpty() && !stackInSlotToPullOutFrom.isEmpty()) {
                            if (stackInSlotToPullOutFrom.getItem() instanceof SwordItem) {
                                // pulling out sword to an empty hand
                                level.playSound(null, player.blockPosition(), BackSlotSounds.SHEATH_SWORD_EVENT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                            } else {
                                // pulling out others to an empty hand
                                level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 1.0F, 1.0F);
                            }
                        } else if (stackInSlotToPullOutFrom.getItem() instanceof SwordItem) {
                            // pulling out sword to a non-empty hand
                            level.playSound(null, player.blockPosition(), BackSlotSounds.SHEATH_SWORD_EVENT.get(), SoundSource.PLAYERS, 1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);
                        } else if (stackInSlotToPullOutTo.getItem() instanceof SwordItem) {
                            // putting back sword item (including while pulling out what there was other
                            // than sword)
                            level.playSound(null, player.blockPosition(), BackSlotSounds.PACK_UP_ITEM_EVENT.get(), SoundSource.PLAYERS, 1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);
                        } else if (!stackInSlotToPullOutTo.isEmpty()) {
                            // putting back other than sword
                            level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 1.0F, 1.0F);
                        }
                    } catch (IOException e) {
                        BackSlotForge.info("Failed to play sound {}", e);
                    }
                }
            }
        });
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(slot);
    }

    public static BackSlotServerPacket decode(FriendlyByteBuf buffer) {
        return new BackSlotServerPacket(buffer.readInt());
    }
}