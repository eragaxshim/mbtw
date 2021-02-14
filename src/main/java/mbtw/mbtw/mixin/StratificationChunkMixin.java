package mbtw.mbtw.mixin;

import mbtw.mbtw.Mbtw;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Iterator;

@Mixin(ChunkGenerator.class)
public class StratificationChunkMixin {
    @Inject(method = "generateFeatures", at = @At(value = "TAIL"))
    protected void buildStratification(ChunkRegion region, StructureAccessor accessor, CallbackInfo ci)
    {
        if (region.getDimension().hasSkyLight())
        {
            int i = region.getCenterChunkX();
            int j = region.getCenterChunkZ();
            Chunk chunk = region.getChunk(i, j);

            ChunkRandom chunkRandom = new ChunkRandom();
            ChunkPos chunkPos = chunk.getPos();
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            int startX = chunkPos.getStartX();
            int startY = chunkPos.getStartZ();
            int k = 0;
            int maxHeightDeep = 25;
            int maxHeightHard = 45;
            int maxLowerDeviation = 5;

            Iterator posIterator = BlockPos.iterate(startX, 0, startY, startX + 15, 0, startY + 15).iterator();

            while(true) {
                BlockPos blockPos;
                int o;
                int p;
                if (!posIterator.hasNext()) {
                    return;
                }

                blockPos = (BlockPos)posIterator.next();
                int x = blockPos.getX();
                int y;
                int z = blockPos.getZ();
                for(o = maxHeightDeep; o >= 0; --o) {
                    if (o <= maxHeightDeep - maxLowerDeviation + chunkRandom.nextInt(maxLowerDeviation)) {
                        y = k + o;

                        if (chunk.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.STONE)
                        {
                            chunk.setBlockState(mutable.set(x, y, z), Mbtw.MBTW_DEEP_STONE.getDefaultState(), false);
                        }
                    }
                }

                for(p = maxHeightHard; p >= maxHeightDeep - maxLowerDeviation - 1; --p) {
                    if (p <= maxHeightHard - maxLowerDeviation + chunkRandom.nextInt(maxLowerDeviation)) {
                        y = k + p;

                        if (chunk.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.STONE)
                        {
                            chunk.setBlockState(mutable.set(x, y, z), Mbtw.MBTW_HARD_STONE.getDefaultState(), false);
                        }
                    }
                }
            }
        }
    }
}
