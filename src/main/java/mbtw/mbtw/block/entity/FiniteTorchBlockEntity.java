package mbtw.mbtw.block.entity;

import mbtw.mbtw.block.FiniteTorchBlock;
import mbtw.mbtw.item.FiniteTorchItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FiniteTorchBlockEntity extends BlockEntity {
    private int burnTime;

    public <T extends FiniteTorchBlockEntity> FiniteTorchBlockEntity(BlockEntityType<T> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("BurnTime", this.burnTime);
        if (this.getCachedState().getBlock() instanceof FiniteTorchBlock)
        {
            tag.putInt("TorchFire", this.getCachedState().get(FiniteTorchBlock.TORCH_FIRE));
            tag.putInt("MaxProgress", ((FiniteTorchItem) this.getCachedState().getBlock().asItem()).getMaxProgress());
        }
        tag.putBoolean("TickProgress", this.burnTime != 0);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.burnTime = tag.getInt("BurnTime");
    }


    public static void tick(World world, BlockPos pos, BlockState state, FiniteTorchBlockEntity be) {
        if (world != null && world.getTime() % 11 == 0 && !world.isClient && state.getBlock() instanceof FiniteTorchBlock)
        {
            int torchFire = state.get(FiniteTorchBlock.TORCH_FIRE);
            if (torchFire > 1)
            {
                int maxBurnTime = ((FiniteTorchItem) state.getBlock().asItem()).getMaxProgress();
                if (be.burnTime == 0)
                {
                    be.burnTime = maxBurnTime;
                }
                boolean isRaining = false;
                if (world.isRaining() && world.isSkyVisible(pos))
                {
                    isRaining = true;
                    if (world.getRandom().nextFloat() < 0.01F)
                    {
                        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.2F, (float) (0.9 + 0.1 * world.getRandom().nextFloat()));
                    }
                }

                be.burnTime -= isRaining ? 33 : 11;
                int newTorchFire = calculateTorchFire(be.burnTime, maxBurnTime);
                if (newTorchFire != torchFire)
                {
                    if (newTorchFire < torchFire && isRaining)
                    {
                        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, (float) (0.9 + 0.1 * world.getRandom().nextFloat()));
                    }
                    world.setBlockState(pos, state.with(FiniteTorchBlock.TORCH_FIRE, newTorchFire));
                }
                if (be.burnTime != 0 && newTorchFire <= 1)
                {
                    be.burnTime = 0;
                }
                be.markDirty();
            }
        }
    }

    public static int calculateTorchFire(int burnTime, int maxBurnTime)
    {

        if (burnTime <= 0)
        {
            return 1;
        }
        else if (burnTime < 0.2 * maxBurnTime)
        {
            return 2;
        }
        else {
            return 3;
        }
    }
}
