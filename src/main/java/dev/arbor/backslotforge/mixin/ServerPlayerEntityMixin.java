package dev.arbor.backslotforge.mixin;

import com.mojang.authlib.GameProfile;
import dev.arbor.backslotforge.BackSlotForge;
import dev.arbor.backslotforge.network.BackSlotClientPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {
    @Unique
    ItemStack backSlotForge$backSlotStack = ItemStack.EMPTY;
    @Unique
    ItemStack backSlotForge$beltSlotStack = ItemStack.EMPTY;

    public ServerPlayerEntityMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    // LivingEntity getEquipmentChanges method only checks EquipmentSlot each tick
    @Inject(method = "tick", at = @At("TAIL"))
    private void tickMixin(CallbackInfo info) {
        try (var level = this.level()) {
            if (!level.isClientSide()) {
                if (!ItemStack.isSameItem(backSlotForge$backSlotStack, this.getInventory().getItem(41))) {
                    backSlotForge$sendPacket(41);
                }
                backSlotForge$backSlotStack = this.getInventory().getItem(41);
                if (!ItemStack.isSameItem(backSlotForge$beltSlotStack, this.getInventory().getItem(42))) {
                    backSlotForge$sendPacket(42);
                }
                backSlotForge$beltSlotStack = this.getInventory().getItem(42);
            }
        } catch (IOException e) {
            BackSlotForge.info("Failed to send packet for slot {}", e);
        }
    }

    @Unique
    private void backSlotForge$sendPacket(int slot) {
        BackSlotForge.getPacketHandler().sendToAllTracking(
                new BackSlotClientPacket(slot, this.getId(), this.getInventory().getItem(slot)), this.level(), this.blockPosition());
        BackSlotForge.debug("Sending packet for slot {}", slot);
    }
}
