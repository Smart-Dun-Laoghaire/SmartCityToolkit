package org.example.smart.smartcitytoolkitforge1;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Sensor {
    protected BlockPos location;
    protected List<Block> blocks;
    private String deviceId;
    private String deviceName;
    private LocalDateTime createdTimestamp;
    private boolean deleted;
    private String link;
    public static List<Sensor> all = new ArrayList<>();

    // This block is registered elsewhere and not in the constructor
    public Sensor(BlockBehaviour.Properties properties, BlockPos location) {
        this.location = location;
        this.blocks = new ArrayList<>();
        this.createdTimestamp = LocalDateTime.now();
        all.add(this);  // Adding this instance to the list of all sensors
    }

    public abstract RegistryObject<Block> CreateNewBlock();

    public BlockPos getLocation() {
        return location;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public List<Block> getAllBlocks() {
        return blocks;
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public abstract void update(Level world, BlockPos pos) throws IOException, InterruptedException;
}