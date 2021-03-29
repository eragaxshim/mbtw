package mbtw.mbtw.block;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.entity.FiniteTorchBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.ToIntFunction;

public class FiniteTorchBlock extends TorchBlock implements BlockEntityProvider, Ignitable {
    public static final IntProperty TORCH_FIRE = IntProperty.of("torch_fire", 0, 3);

    public FiniteTorchBlock(Settings settings, ParticleEffect particle) {
        super(settings, particle);
        this.setDefaultState(this.getDefaultState().with(TORCH_FIRE, 0));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new FiniteTorchBlockEntity(Mbtw.FINITE_TORCH_BLOCK_ENTITY);
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int torch_fire = state.get(FiniteTorchBlock.TORCH_FIRE);
        if (torch_fire > 1)
        {
            double d = (double)pos.getX() + 0.5D;
            double e = (double)pos.getY() + 0.7D;
            double f = (double)pos.getZ() + 0.5D;
            world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0D, 0.0D, 0.0D);
            if (torch_fire == 2 && random.nextFloat() < 0.5F)
            {
                world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0D, 0.0D, 0.0D);
            }
            if (torch_fire == 3 || random.nextFloat() < 0.4F)
            {
                world.addParticle(this.particle, d, e, f, 0.0D, 0.0D, 0.0D);
            }

        }

    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(TORCH_FIRE);
    }

    public static ToIntFunction<BlockState> createLightLevelFromTorchFire() {
        return (blockState) -> {
            switch (blockState.get(TORCH_FIRE))
            {
                case 2:
                    return 8;
                case 3:
                    return 14;
                default:
                    return 0;
            }
        };
    }



    @Override
    public boolean attemptFireStart(World world, LivingEntity user, ItemStack stack, int meanStartTick, int remainingUseTick, BlockState state, BlockPos pos) {
        return state.getBlock() instanceof FiniteTorchBlock && state.get(TORCH_FIRE) == 0 && Ignitable.super.attemptFireStart(world, user, stack, meanStartTick, remainingUseTick, state, pos);
    }

    @Override
    public float getStartTickFactor(BlockState block) {
        return 0.25F;
    }

    @Override
    public boolean ignite(World world, LivingEntity entity, ItemStack stack, BlockState state, BlockPos pos) {
        if (state.isOf(this) && state.get(TORCH_FIRE) == 0)
        {
            world.playSound(null, pos, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F + 1.0F);
            return world.setBlockState(pos, state.with(TORCH_FIRE, 3), 11);
        }
        return false;
    }
}
