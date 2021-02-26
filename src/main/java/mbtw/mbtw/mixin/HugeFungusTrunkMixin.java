package mbtw.mbtw.mixin;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.tag.MbtwTagsMaps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.HugeFungusFeature;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(HugeFungusFeature.class)
public class HugeFungusTrunkMixin {
    @Inject(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/HugeFungusFeature;generateHat(Lnet/minecraft/world/WorldAccess;Ljava/util/Random;Lnet/minecraft/world/gen/feature/HugeFungusFeatureConfig;Lnet/minecraft/util/math/BlockPos;IZ)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    protected void addTrunk(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, HugeFungusFeatureConfig hugeFungusFeatureConfig, CallbackInfoReturnable<Boolean> cir, Block block, BlockPos blockPos2, int i, boolean thickStem)
    {
        BlockState trunk = MbtwTagsMaps.LOG_TRUNK_MAP.get(hugeFungusFeatureConfig.stemState.getBlock());
        if (trunk != null)
        {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            int w = thickStem ? 1 : 0;
            for(int j = -w; j <= w; ++j) {
                for (int k = -w; k <= w; ++k) {
                    mutable.set(blockPos, j, 0, k);
                    if (structureWorldAccess.getBlockState(mutable).getBlock() == hugeFungusFeatureConfig.stemState.getBlock())
                    {
                        structureWorldAccess.setBlockState(mutable, trunk, 3);
                    }
                }
            }
        }
    }
}
