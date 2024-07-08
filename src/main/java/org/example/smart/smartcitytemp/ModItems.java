package org.example.smart.smartcitytemp;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Smartcitytem.MODID);

    public static final RegistryObject<Item> TEMPERATURE_SENSOR = ITEMS.register("temperature_sensor",
            () -> new BlockItem(ModBlocks.TEMPERATURE_SENSOR.get(), new Item.Properties().tab(CreativeModeTabs.TAB_DECORATIONS)));

    public static final RegistryObject<Item> AIR_QUALITY_SENSOR = ITEMS.register("air_quality_sensor",
            () -> new BlockItem(ModBlocks.AIR_QUALITY_SENSOR.get(), new Item.Properties().tab(CreativeModeTabs.TAB_DECORATIONS)));
}