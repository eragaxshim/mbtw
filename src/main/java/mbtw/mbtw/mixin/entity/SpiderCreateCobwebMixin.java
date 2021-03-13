package mbtw.mbtw.mixin.entity;

import mbtw.mbtw.Mbtw;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SpiderEntity.class)
public abstract class SpiderCreateCobwebMixin extends SpiderDespawnCobwebMixin{
    protected SpiderCreateCobwebMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void createCobwebAtDespawn(CallbackInfo ci)
    {
        if (!world.isClient && this.world.getDifficulty() != Difficulty.PEACEFUL)
        {
            float f = new Random().nextFloat();
            if (f > 0.6f && (world.getTimeOfDay() > 22800 || world.getTimeOfDay() < 12000))
            {
                BlockPos.Mutable mutable = this.getBlockPos().mutableCopy();
                int x = mutable.getX();
                int z = mutable.getZ();
                int y;
                for (y = mutable.getY(); world.getBlockState(mutable).isAir() && mutable.getY() > 32; y--)
                {
                    mutable.set(x, y, z);
                }

                if (!world.getBlockState(mutable).isSolidBlock(world, mutable))
                {
                    mutable = checkAround(mutable.set(x, y, z));
                }

                if (mutable != null)
                {
                    BlockPos pos = mutable.up().toImmutable();
                    world.setBlockState(pos, Mbtw.DAMAGED_COBWEB.getDefaultState(), 3);
                }
            }
        }
    }

    private BlockPos.Mutable checkAround(BlockPos.Mutable mutable)
    {
        int x = mutable.getX();
        int y = mutable.getY();
        int z = mutable.getZ();

        for (int i = -1; i <= 1; i++)
        {
            for (int j = -1; j <= 1; j++)
            {
                mutable.set(x+i, y, z+j);
                if (world.getBlockState(mutable).isSolidBlock(world, mutable) && world.getBlockState(mutable.up()).isAir())
                {
                    return mutable;
                }
            }
        }

        return null;
    }
}
