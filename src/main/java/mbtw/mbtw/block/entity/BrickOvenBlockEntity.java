package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.mixin.IsBurningInvoker;
import mbtw.mbtw.mixin.LitStateInvoker;
import mbtw.mbtw.screen.BrickOvenScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class BrickOvenBlockEntity extends AbstractFurnaceBlockEntity {
    public BrickOvenBlockEntity() {
        super(Mbtw.BRICK_OVEN_ENTITY, Mbtw.BRICK_SMELTING);
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
        } else if (!((IsBurningInvoker)this).invokeIsBurning()) {
            ItemStack itemStack = (ItemStack) this.inventory.get(1);
            return canUseAsFuel(stack) || stack.getItem() == Items.BUCKET && itemStack.getItem() != Items.BUCKET;
        }
        else {
            return false;
        }
    }
}
