package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.*;
import mbtw.mbtw.screen.MillstoneScreenHandler;
import mbtw.mbtw.state.property.MbtwProperties;
import mbtw.mbtw.util.math.MechanicalVec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MillstoneBlockEntity extends AbstractMechanicalProcessor {
    public MillstoneBlockEntity(BlockPos pos, BlockState state) {
        super(Mbtw.MILLSTONE_ENTITY, pos, state, Mbtw.MILLING);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.mbtw.millstone");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new MillstoneScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public int getMaxCountOfSlot(int slotIndex) {
        return 64;
    }
}
