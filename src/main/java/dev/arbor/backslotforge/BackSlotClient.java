package dev.arbor.backslotforge;

import dev.arbor.backslotforge.client.key.SwitchKey;
import dev.arbor.backslotforge.client.sprite.BackSlotSprites;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class BackSlotClient extends BackSlotMain {
    public BackSlotClient(){
        super();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        SwitchKey switchKey = new SwitchKey();
        modEventBus.addListener(switchKey::registerKeyboard);
        MinecraftForge.EVENT_BUS.register(switchKey);
        MinecraftForge.EVENT_BUS.register(new BackSlotSprites());
    }
}