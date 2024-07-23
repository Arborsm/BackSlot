package dev.arbor.backslotforge.mixin.compat;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class OnDeathItemDropCompatibility extends Player {

    public OnDeathItemDropCompatibility(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "die", at = @At("HEAD"))
    public void onDeathMixin(DamageSource source, CallbackInfo info) {
        if (!this.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            if (!this.getInventory().getItem(41).isEmpty()) {
                if (this.getInventory().getFreeSlot() != -1)
                    this.getInventory().items.set(this.getInventory().getFreeSlot(), this.getInventory().getItem(41));
                else
                    this.spawnAtLocation(this.getInventory().getItem(41));
                this.getInventory().removeItemNoUpdate(41);
            }
            if (!this.getInventory().getItem(42).isEmpty()) {
                if (this.getInventory().getFreeSlot() != -1)
                    this.getInventory().items.set(this.getInventory().getFreeSlot(), this.getInventory().getItem(42));
                else
                    this.spawnAtLocation(this.getInventory().getItem(42));
                this.getInventory().removeItemNoUpdate(42);
            }
        }
    }
}
