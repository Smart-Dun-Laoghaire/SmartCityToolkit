package org.example.smart.smartcitytoolkitforge1;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeMenuType;

public class CustomSmartCityContainer extends AbstractContainerMenu {

    private final BlockPos pos;
    private final ContainerLevelAccess access;

    // Constructor for MenuType
    public CustomSmartCityContainer(int id, Inventory playerInventory, FriendlyByteBuf data) {
        this(id, playerInventory, data.readBlockPos());
    }

    // Main constructor
    public CustomSmartCityContainer(int id, Inventory playerInventory, BlockPos pos) {
        super(Smartcitytoolkitforge1.CUSTOM_SMARTCITY_CONTAINER_TYPE.get(), id);
        this.pos = pos;
        this.access = ContainerLevelAccess.create(playerInventory.player.level, pos);

    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, Smartcitytoolkitforge1.CUSTOM_SMARTCITY_BLOCK.get());
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 36) {
                if (!this.moveItemStackTo(itemstack1, 36, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
}