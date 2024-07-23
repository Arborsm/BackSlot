package dev.arbor.backslotforge.mixin;

import dev.arbor.backslotforge.BackSlotForge;
import dev.arbor.backslotforge.network.BackSlotClientPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public class EntityTrackerEntryMixin {
    @Mutable
    @Final
    @Shadow
    private final Entity entity;

    public EntityTrackerEntryMixin(Entity entity) {
        this.entity = entity;
    }

    @Inject(method = "addPairing", at = @At(value = "TAIL"))
    public void startTrackingMixin(ServerPlayer serverPlayer, CallbackInfo info) {
        if (entity instanceof Player player) {
            for (int i = 41; i < 43; i++) {
                if (!serverPlayer.getInventory().getItem(i).isEmpty()) {
                    BackSlotForge.getPacketHandler().sendToAllTracking(
                            new BackSlotClientPacket(i, serverPlayer.getId(), serverPlayer.getInventory().getItem(i)), serverPlayer);
                }
                if (!player.getInventory().getItem(i).isEmpty()) {
                    BackSlotForge.getPacketHandler().sendToAllTracking(
                            new BackSlotClientPacket(i, serverPlayer.getId(), serverPlayer.getInventory().getItem(i)), serverPlayer);
                }
            }
        }
    }

}
