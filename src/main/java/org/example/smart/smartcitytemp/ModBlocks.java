package org.example.smart.smartcitytemp;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Smartcitytem.MODID);

    // Example block with custom properties
    public static final RegistryObject<Block> TEMPERATURE_SENSOR = BLOCKS.register("temperature_sensor",
            () -> new Block(Block.Properties.copy(Blocks.IRON_BLOCK).strength(1.5f)));

    // Example block with custom properties
    public static final RegistryObject<Block> AIR_QUALITY_SENSOR = BLOCKS.register("air_quality_sensor",
            () -> new Block(Block.Properties.copy(Blocks.IRON_BLOCK).strength(1.5f)));

}