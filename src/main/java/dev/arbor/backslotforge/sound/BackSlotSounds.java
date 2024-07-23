package dev.arbor.backslotforge.sound;

import dev.arbor.backslotforge.BackSlotForge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BackSlotSounds {
    public static final DeferredRegister<SoundEvent> SOUND_REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BackSlotForge.MODID);
    public static final ResourceLocation PACK_UP_ITEM = new ResourceLocation("backslot:pack_up_item");
    public static final ResourceLocation SHEATH_SWORD = new ResourceLocation("backslot:sheath_sword");

    public static final RegistryObject<SoundEvent> PACK_UP_ITEM_EVENT = SOUND_REGISTRY.register("pack_up_item",() -> SoundEvent.createVariableRangeEvent(PACK_UP_ITEM));
    public static final RegistryObject<SoundEvent> SHEATH_SWORD_EVENT = SOUND_REGISTRY.register("sheath_sword",() -> SoundEvent.createVariableRangeEvent(SHEATH_SWORD));
}
