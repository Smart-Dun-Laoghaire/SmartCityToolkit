package org.example.smart.smartcitytoolkitforge1;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import net.minecraft.world.item.CreativeModeTabs;

import java.io.IOException;

@Mod(Smartcitytoolkitforge1.MODID)
public class Smartcitytoolkitforge1 {

    public static final String MODID = "smartcitytoolkitforge1_21";
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    private static final Logger LOGGER = LogUtils.getLogger();


    // Register custom SmartCity block, item, container, and block entity type
    public static final RegistryObject<MenuType<CustomSmartCityContainer>> CUSTOM_SMARTCITY_CONTAINER_TYPE = CONTAINERS.register("custom_smartcity_container",
            () -> IForgeMenuType.create(CustomSmartCityContainer::new));


    public static final RegistryObject<Block> CUSTOM_SMARTCITY_BLOCK = BLOCKS.register("custom_smartcity_block",
            () -> {
                try {
                    return new CustomSmartCityBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)
                                                    .mapColor(MapColor.STONE)
                                                    .strength(1.5f, 6.0f)
                                                    .requiresCorrectToolForDrops(),
                                                    new TemperatureSensor(new BlockPos(0, 0, 0)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });


    public static final RegistryObject<BlockEntityType<CustomSmartCityBlockEntity>> CUSTOM_SMARTCITY_BLOCK_ENTITY_TYPE = BLOCK_ENTITIES.register("custom_smartcity_block_entity",
            () -> BlockEntityType.Builder.of(CustomSmartCityBlockEntity::new, CUSTOM_SMARTCITY_BLOCK.get()).build(null));

    public static final RegistryObject<Block> AIR_QUALITY_SENSOR_BLOCK = BLOCKS.register("air_quality_sensor_block",
            () -> {
                try {
                    return new CustomSmartCityBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .strength(1.5f, 6.0f)
                            .requiresCorrectToolForDrops(),
                            new AirQualitySensor(new BlockPos(0, 0, 0)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });


    // Declare and create the creative tab
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab>  SMART_CITY_TAB = CREATIVE_MODE_TABS.register("smartcity_tab",
                    () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Smartcitytoolkitforge1.CUSTOM_SMARTCITY_BLOCK_ITEM.get()))
                    .title(Component.translatable("itemGroup.smartcitytoolkit_tab"))
                    .build());

    public static final RegistryObject<Item> CUSTOM_SMARTCITY_BLOCK_ITEM = ITEMS.register("custom_smartcity_block",
            () -> new BlockItem(Smartcitytoolkitforge1.CUSTOM_SMARTCITY_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> AIR_QUALITY_SENSOR_BLOCK_ITEM = ITEMS.register("air_quality_sensor_block",
            () -> new BlockItem(Smartcitytoolkitforge1.AIR_QUALITY_SENSOR_BLOCK.get(), new Item.Properties()));

    //public static final RegistryObject<Item> CUSTOM_SMARTCITY_BLOCK_ITEM = ITEMS.register("custom_smartcity_block",
     //       () -> new Item(new Item.Properties().tab(SMART_CITY_TAB)));

   // public static final RegistryObject<Item> EXAMPLE_SMART_CITY_ITEM = ITEMS.register("example_smart_city_item",
     //       () -> new Item(new Item.Properties().tab(SMART_CITY_TAB)));

    private WeatherType currentWeather;
    private TemperatureSensor temperatureSensor;
    private WeatherSensor weatherSensor;
    private AirQualitySensor airQualitySensor;

    public Smartcitytoolkitforge1() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CONTAINERS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        CREATIVE_MODE_TABS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);

        // Initialize sensors
        this.temperatureSensor = new TemperatureSensor(new BlockPos(0, 0, 0));
        this.weatherSensor = new WeatherSensor(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5f), new BlockPos(0, 0, 0));
        this.airQualitySensor = new AirQualitySensor(new BlockPos(0, 0, 0));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) throws IOException, InterruptedException {
        LOGGER.info("HELLO from server starting");
        if (event.getServer().getLevel(Level.OVERWORLD) != null) {
            temperatureSensor.update(event.getServer().getLevel(Level.OVERWORLD), new BlockPos(0, 0, 0));
        } else {
            LOGGER.warn("World is null, unable to update temperature sensor.");
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            if (CUSTOM_SMARTCITY_CONTAINER_TYPE.isPresent()) {
                MenuScreens.register(CUSTOM_SMARTCITY_CONTAINER_TYPE.get(), CustomSmartCityScreen::new);
            } else {
                LOGGER.error("Failed to register CustomSmartCityContainer as it is not present.");
            }

        }
    }

    public void setWeather(WeatherType weatherType) {
        this.currentWeather = weatherType;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            if (weatherType == WeatherType.SUNNY) {
                minecraft.level.setRainLevel(0);
                minecraft.level.setThunderLevel(0);
            } else if (weatherType == WeatherType.RAINY) {
                minecraft.level.setRainLevel(1);
                minecraft.level.setThunderLevel(0.2F);
            }
        } else {
            LOGGER.warn("Minecraft level is null, unable to set weather.");
        }
    }
}