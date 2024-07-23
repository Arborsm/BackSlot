package dev.arbor.backslotforge.network;

import dev.arbor.backslotforge.BackSlotForge;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class BackSlotClientPacket implements IBackSlotPacket {
    private final int slot;
    private final int entityId;
    private final ItemStack itemStack;

    public BackSlotClientPacket(int slot, int entityId, ItemStack itemStack) {
        this.slot = slot;
        this.entityId = entityId;
        this.itemStack = itemStack;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> {
            try (var level = Objects.requireNonNull(client.player).level()) {
                if (client.player != null && level.getEntity(entityId) != null) {
                    Player player = (Player) level.getEntity(entityId);
                    if (player != null) {
                        player.getInventory().setItem(slot, itemStack.copy());
                    }
                }
            } catch (IOException e) {
                BackSlotForge.info("Error while handling BackSlotClientPacket: {}", e);
            }
        });
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(slot);
        buffer.writeInt(entityId);
        buffer.writeItem(itemStack);
    }

    public static BackSlotClientPacket decode(FriendlyByteBuf buffer) {
        return new BackSlotClientPacket(buffer.readInt(), buffer.readInt(), buffer.readItem());
    }
}
