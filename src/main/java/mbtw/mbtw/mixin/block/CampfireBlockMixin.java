package mbtw.mbtw.mixin.block;

import mbtw.mbtw.block.VariableCampfireBlock;
import mbtw.mbtw.block.entity.CampfireBlockMixinAccessor;
import mbtw.mbtw.block.entity.VariableCampfireBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin implements CampfireBlockMixinAccessor {
    @Inject(method = "createBlockEntity", at = @At("HEAD"), cancellable = true)
    protected void createBlockEntity(BlockPos pos, BlockState state, CallbackInfoReturnable<BlockEntity> cir) {
        cir.setReturnValue(new VariableCampfireBlockEntity(pos, state));
    }

    @Inject(method = "spawnSmokeParticle", at = @At("HEAD"), cancellable = true)
    private static void changeSpawnSmokeParticle(World world, BlockPos pos, boolean isSignal, boolean lotsOfSmoke, CallbackInfo ci) {
        if (world.getBlockState(pos).getBlock() instanceof VariableCampfireBlock)
        {
            Random random = world.getRandom();
            DefaultParticleType defaultParticleType = isSignal ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
            if (isSignal || random.nextFloat() > 0.4F)
            {
                world.addImportantParticle(defaultParticleType, true, (double)pos.getX() + 0.5D + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + random.nextDouble() + random.nextDouble(), (double)pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);
            }

            if (lotsOfSmoke || (world.isRaining() && random.nextFloat() > 0.5F)) {
                world.addParticle(ParticleTypes.SMOKE, (double)pos.getX() + 0.25D + random.nextDouble() / 2.0D * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + 0.4D, (double)pos.getZ() + 0.25D + random.nextDouble() / 2.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.005D, 0.0D);
            }
            ci.cancel();
        }
    }

    @Invoker("isSignalFireBaseBlock")
    public abstract boolean invokeIsSignalFireBaseBlock(BlockState state);
}
