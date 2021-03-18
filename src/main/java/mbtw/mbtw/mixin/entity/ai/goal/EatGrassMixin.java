package mbtw.mbtw.mixin.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(EatGrassGoal.class)
public class EatGrassMixin {
    @Shadow @Final private MobEntity mob;

    @Shadow @Final private static Predicate<BlockState> GRASS_PREDICATE;

    @Shadow @Final private World world;

    @Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
    protected void changeCanStart(CallbackInfoReturnable<Boolean> cir)
    {
        if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 25 : 50) != 0) {
            cir.setReturnValue(false);
        } else {
            BlockPos blockPos = this.mob.getBlockPos();
            if (GRASS_PREDICATE.test(this.world.getBlockState(blockPos))) {
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(this.world.getBlockState(blockPos.down()).isOf(Blocks.GRASS_BLOCK));
            }
        }
    }

    @Inject(method = "start", at = @At("TAIL"))
    protected void changeStart(CallbackInfo ci)
    {

    }
}
