package mbtw.mbtw.mixin.world;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.tag.MbtwTagsMaps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.WorldAccess;
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
    @Inject(method = "placeLogsAndLeaves", at = @At(value = "TAIL"))
    private static void addTrunk(WorldAccess world, BlockBox box, Set<BlockPos> trunkPositions, Set<BlockPos> decorationPositions, CallbackInfoReturnable<Boolean> cir)
    {
        try {
            int minY = trunkPositions.stream()
                    .min(Comparator.comparing(BlockPos::getY))
                    .orElseThrow(NoSuchElementException::new)
                    .getY();
            List<BlockPos> minYPositions = trunkPositions.stream()
                    .filter(p -> p.getY() == minY).toList();

            for (BlockPos minYPos : minYPositions)
            {
                BlockState logState = world.getBlockState(minYPos);
                BlockState trunkState = MbtwTagsMaps.LOG_TRUNK_MAP.get(logState.getBlock());
                world.setBlockState(minYPos, trunkState != null ? trunkState : logState, 19);
            }
        }
        catch (NoSuchElementException ignored) { }
    }
}
