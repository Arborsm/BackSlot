package dev.arbor.backslotforge;

import com.mojang.logging.LogUtils;
import dev.arbor.backslotforge.network.PacketHandler;
import dev.arbor.backslotforge.sound.BackSlotSounds;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
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
        BackSlotSounds.SOUND_REGISTRY.register(modEventBus);
    }

    public static void info(String text) {
        if (!FMLLoader.isProduction()) {
            LOGGER.info(text);
        }
    }

    public static void info(String text, Object... args) {
        if (!FMLLoader.isProduction()) {
            LOGGER.info(text, args);
        }
    }

    public static void debug(String text, Object... args) {
        if (!FMLLoader.isProduction()) {
            LOGGER.debug(text, args);
        }
    }

    public static PacketHandler getPacketHandler() {
        return backSlotForge.packetHandler;
    }
    private void commonSetup(FMLCommonSetupEvent event) {
        packetHandler.initialize();
    }
}
