package mbtw.mbtw.mixin.world;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.tag.MbtwTagsMaps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.security.KeyException;
import java.util.*;
import java.util.stream.Collectors;

@Mixin(TreeFeature.class)
public class TreeTrunkMixin {
    @Inject(method = "generate(Lnet/minecraft/world/ModifiableTestableWorld;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Ljava/util/Set;Ljava/util/Set;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/world/gen/feature/TreeFeatureConfig;)Z", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/gen/trunk/TrunkPlacer;generate(Lnet/minecraft/world/ModifiableTestableWorld;Ljava/util/Random;ILnet/minecraft/util/math/BlockPos;Ljava/util/Set;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/world/gen/feature/TreeFeatureConfig;)Ljava/util/List;"), locals = LocalCapture.NO_CAPTURE)
    protected void addTrunk(ModifiableTestableWorld world, Random random, BlockPos pos, Set<BlockPos> logPositions, Set<BlockPos> leavesPositions, BlockBox box, TreeFeatureConfig config, CallbackInfoReturnable<Boolean> cir)
    {
        try {
            int minY = logPositions.stream()
                    .min(Comparator.comparing(BlockPos::getY))
                    .orElseThrow(NoSuchElementException::new)
                    .getY();
            List<BlockPos> minYPositions = logPositions.stream()
                    .filter(p -> p.getY() == minY)
                    .collect(Collectors.toList());

            for (BlockPos minYPos : minYPositions)
            {
                BlockState logState = config.trunkProvider.getBlockState(random, minYPos);
                BlockState trunkState = MbtwTagsMaps.LOG_TRUNK_MAP.get(logState.getBlock());
                world.setBlockState(minYPos, trunkState != null ? trunkState : logState, 19);
            }
        }
        catch (NoSuchElementException ignored) { }
    }
}
