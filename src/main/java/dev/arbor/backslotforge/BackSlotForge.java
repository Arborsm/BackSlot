package dev.arbor.backslotforge;

import com.mojang.logging.LogUtils;
import dev.arbor.backslotforge.network.BackSlotClientPacket;
import dev.arbor.backslotforge.network.PacketHandler;
import dev.arbor.backslotforge.sound.BackSlotSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(BackSlotForge.MODID)
public class BackSlotForge {
    public static final String MODID = "backslotforge";
    public static final Logger LOGGER = LogUtils.getLogger();
    private final PacketHandler packetHandler;
    private static BackSlotForge backSlotForge;

    public BackSlotForge() {
        backSlotForge = this;
        packetHandler = new PacketHandler();
        DistExecutor.unsafeRunForDist(() -> BackSlotClient::new, () -> BackSlotMain::new);
        LOGGER.info("BackSlotForge initialized");
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        //MinecraftForge.EVENT_BUS.addListener(this::onPlayerJoin);
        MinecraftForge.EVENT_BUS.addListener(this::onStartTracking);
        BackSlotSounds.SOUND_REGISTRY.register(modEventBus);
    }

    public static PacketHandler getPacketHandler() {
        return backSlotForge.packetHandler;
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        packetHandler.initialize();
    }

    public void onStartTracking(PlayerEvent.StartTracking event) {
        var target = event.getTarget();
        if (target instanceof ServerPlayer player) {
            for (int i = 41; i < 43; i++) {
                if (!player.getInventory().getItem(i).isEmpty()) {
                    BackSlotForge.getPacketHandler().sendToAllTracking(
                            new BackSlotClientPacket(i, player.getId(), player.getInventory().getItem(i)), player);
                }
            }
        }
    }
}
