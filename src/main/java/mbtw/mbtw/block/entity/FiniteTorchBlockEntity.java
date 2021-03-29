package mbtw.mbtw.block.entity;

import mbtw.mbtw.block.FiniteTorchBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Tickable;

public class FiniteTorchBlockEntity extends BlockEntity implements Tickable {
    private int burningTime;

    public <T extends FiniteTorchBlockEntity> FiniteTorchBlockEntity(BlockEntityType<T> type) {
        super(type);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("BurningTime", burningTime);
        if (this.getCachedState().getBlock() instanceof FiniteTorchBlock)
        {
            tag.putInt("TorchFire", this.getCachedState().get(FiniteTorchBlock.TORCH_FIRE));
        }
        tag.putBoolean("TickDamage", burningTime != 0);

        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.burningTime = tag.getInt("BurningTime");
    }


    @Override
    public void tick() {
        BlockState state = this.getCachedState();


        if (this.world != null && this.world.getTime() % 11 == 0 && !this.world.isClient && state.getBlock() instanceof FiniteTorchBlock)
        {
            int torchFire = state.get(FiniteTorchBlock.TORCH_FIRE);
            if (torchFire > 1)
            {
                boolean isRaining = false;
                if (this.world.isRaining() && this.world.isSkyVisible(this.pos))
                {
                    isRaining = true;
                    if (this.world.getRandom().nextFloat() < 0.05F)
                    {
                        this.world.playSound(null, this.pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.5F, (float) (0.9 + 0.1 * world.getRandom().nextFloat()));
                    }
                }

                this.burningTime += isRaining ? 33 : 11;

                int maxBurnTime = state.getBlock().asItem().getMaxDamage();
                int newTorchFire = calculateTorchFire(this.burningTime, maxBurnTime);
                if (newTorchFire != torchFire)
                {
                    if (newTorchFire < torchFire && isRaining)
                    {
                        world.playSound(null, this.pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, (float) (0.9 + 0.1 * world.getRandom().nextFloat()));
                    }
                    this.world.setBlockState(this.pos, state.with(FiniteTorchBlock.TORCH_FIRE, newTorchFire));
                }
                if (this.burningTime != 0 && newTorchFire <= 1)
                {
                    this.burningTime = 0;
                }
                this.markDirty();
            }
        }
    }

    public static int calculateTorchFire(int burningTime, int maxBurnTime)
    {

        if (burningTime > maxBurnTime)
        {
            return 1;
        }
        else if (burningTime > 0.8 * maxBurnTime)
        {
            return 2;
        }
        else {
            return 3;
        }
    }
}
