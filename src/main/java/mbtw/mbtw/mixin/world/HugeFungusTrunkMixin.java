package mbtw.mbtw.mixin.world;

import mbtw.mbtw.tag.MbtwTagsMaps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.HugeFungusFeature;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HugeFungusFeature.class)
public class HugeFungusTrunkMixin {
    @Inject(method = "generate", at = @At(value = "RETURN"))
    protected void addTrunk(FeatureContext<HugeFungusFeatureConfig> context, CallbackInfoReturnable<Boolean> cir)
    {
        if (cir.getReturnValue()) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            StructureWorldAccess world = context.getWorld();
            BlockPos pos = context.getOrigin().mutableCopy();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    BlockPos newPos = mutable.set(pos, i, 0, j);
                    BlockState state = world.getBlockState(newPos);
                    BlockState trunkBlock = MbtwTagsMaps.LOG_TRUNK_MAP.get(state.getBlock());
                    if (trunkBlock != null) {
                        world.setBlockState(newPos, trunkBlock, Block.NOTIFY_ALL | Block.FORCE_STATE);
                    }
                }
            }
        }
    }
}
