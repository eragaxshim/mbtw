package mbtw.mbtw.block.entity;

import mbtw.mbtw.block.FiniteTorchBlock;
import mbtw.mbtw.item.FiniteTorchItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Tickable;

public class FiniteTorchBlockEntity extends BlockEntity implements Tickable {
    private int burnTime;

    public <T extends FiniteTorchBlockEntity> FiniteTorchBlockEntity(BlockEntityType<T> type) {
        super(type);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("BurnTime", this.burnTime);
        if (this.getCachedState().getBlock() instanceof FiniteTorchBlock)
        {
            tag.putInt("TorchFire", this.getCachedState().get(FiniteTorchBlock.TORCH_FIRE));
            tag.putInt("MaxProgress", ((FiniteTorchItem) this.getCachedState().getBlock().asItem()).getMaxProgress());
        }
        tag.putBoolean("TickProgress", this.burnTime != 0);

        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.burnTime = tag.getInt("BurnTime");
    }


    @Override
    public void tick() {
        BlockState state = this.getCachedState();
        if (this.world != null && this.world.getTime() % 11 == 0 && !this.world.isClient && state.getBlock() instanceof FiniteTorchBlock)
        {
            int torchFire = state.get(FiniteTorchBlock.TORCH_FIRE);
            if (torchFire > 1)
            {
                int maxBurnTime = ((FiniteTorchItem) state.getBlock().asItem()).getMaxProgress();
                if (this.burnTime == 0)
                {
                    this.burnTime = maxBurnTime;
                }
                boolean isRaining = false;
                if (this.world.isRaining() && this.world.isSkyVisible(this.pos))
                {
                    isRaining = true;
                    if (this.world.getRandom().nextFloat() < 0.01F)
                    {
                        this.world.playSound(null, this.pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.2F, (float) (0.9 + 0.1 * world.getRandom().nextFloat()));
                    }
                }

                this.burnTime -= isRaining ? 33 : 11;
                int newTorchFire = calculateTorchFire(this.burnTime, maxBurnTime);
                if (newTorchFire != torchFire)
                {
                    if (newTorchFire < torchFire && isRaining)
                    {
                        world.playSound(null, this.pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, (float) (0.9 + 0.1 * world.getRandom().nextFloat()));
                    }
                    this.world.setBlockState(this.pos, state.with(FiniteTorchBlock.TORCH_FIRE, newTorchFire));
                }
                if (this.burnTime != 0 && newTorchFire <= 1)
                {
                    this.burnTime = 0;
                }
                this.markDirty();
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
