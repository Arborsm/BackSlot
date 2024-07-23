package dev.arbor.backslotforge;

import dev.arbor.backslotforge.config.BackSlotConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class BackSlotMain {
    public static BackSlotConfig CONFIG;

    public static final TagKey<Item> BACKSLOT_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation("backslot", "backslot_items"));
    public static final TagKey<Item> BELTSLOT_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation("backslot", "beltslot_items"));

    public BackSlotMain(){
        init();
    }

    public static void init() {
        AutoConfig.register(BackSlotConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(BackSlotConfig.class).getConfig();
    }
}