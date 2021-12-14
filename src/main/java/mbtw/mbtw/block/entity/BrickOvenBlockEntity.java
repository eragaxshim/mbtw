package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.screen.BrickOvenScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class BrickOvenBlockEntity extends AbstractFurnaceBlockEntity {
    public BrickOvenBlockEntity(BlockPos pos, BlockState state) {
        super(Mbtw.BRICK_OVEN_ENTITY, pos, state, Mbtw.BRICK_SMELTING);
    }

    @Override
    public Text getContainerName() {
        return new TranslatableText("container.mbtw.brick_oven");
    }

    //public int getFuelTime(ItemStack fuel) {
        //return super.getFuelTime(fuel) / 2;
    //}

    @Override
    public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new BrickOvenScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    protected int getFuelTime(ItemStack fuel) {
        return Math.min(super.getFuelTime(fuel) * 40, 12000);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (this.inventory.get(slot).getCount() > 0)
        {
            return false;
        }
        if (slot == 2) {
            return false;
        } else if (slot != 1) {
            return true;
        } else {
            int burnTime = propertyDelegate.get(0);
            float fuelProgress = ((float) propertyDelegate.get(0)) / ((float) propertyDelegate.get(1));
            if (!(burnTime > 0) || fuelProgress > 0.9)
            {
                ItemStack itemStack = (ItemStack) this.inventory.get(1);
                return canUseAsFuel(stack) || stack.getItem() == Items.BUCKET && itemStack.getItem() != Items.BUCKET;
            }
            else {
                return false;
            }
        }
    }
}
