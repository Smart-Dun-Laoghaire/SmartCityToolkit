package org.example.smart.smartcitytoolkitforge1;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SmartCityBlock {
    public static List<SmartCityBlock> blocks = new ArrayList<>();
    private final Sensor linkedSensor;

    public SmartCityBlock(Sensor sensor) throws Exception {
        if (sensor == null) {
            throw new Exception("Sensor cannot be null");
        }

        if (!(sensor instanceof TemperatureSensor) && !(sensor instanceof AirQualitySensor) && !(sensor instanceof WeatherSensor)) {
            throw new Exception("Unsupported sensor type for block");
        }

        this.linkedSensor = sensor;
        blocks.add(this);
    }

    public Sensor getLinkedSensor() {
        return linkedSensor;
    }

    public void update(Level world, BlockPos pos) throws IOException, InterruptedException {
        if (linkedSensor != null) {
            linkedSensor.update(world, pos);
        } else {
            throw new IllegalStateException("No linked sensor to update");
        }
    }

    public static List<SmartCityBlock> getAllBlocks() {
        return blocks;
    }

    public static SmartCityBlock createSmartCityBlock(Level world, BlockPos pos, String sensorType) throws Exception {
        Sensor sensor;
        if (sensorType.equalsIgnoreCase("Temperature")) {
            sensor = new TemperatureSensor(pos);
        } else if (sensorType.equalsIgnoreCase("Air Quality")) {
            sensor = new AirQualitySensor(pos);
        } else if (sensorType.equalsIgnoreCase("Weather")) {
            sensor = new WeatherSensor(BlockBehaviour.Properties.of(Material.METAL).strength(1.5f), pos);
        } else {
            throw new IllegalArgumentException("Unknown sensor type: " + sensorType);
        }

        CustomSmartCityBlock smartCityBlock = new CustomSmartCityBlock(
                BlockBehaviour.Properties.of(Material.METAL).strength(1.5f), sensor
        );

        BlockState blockState = smartCityBlock.defaultBlockState();

        try {
            world.setBlock(pos, blockState, 3);
        } catch (Exception e) {
            throw new IOException("Failed to place block at " + pos.toString(), e);
        }

        return new SmartCityBlock(sensor);
    }
}