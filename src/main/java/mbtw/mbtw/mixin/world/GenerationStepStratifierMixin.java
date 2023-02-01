package mbtw.mbtw.mixin.world;

import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Biome.class)
public class GenerationStepStratifierMixin {
//    @Inject(method = "generateFeatureStep", at = @At(value = "INVOKE", target="Lnet/minecraft/world/gen/StructureAccessor;shouldGenerateStructures()Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
//    protected void buildStratification(StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, ChunkRegion region, long populationSeed, ChunkRandom random, BlockPos po, CallbackInfo ci, List<List<Supplier<ConfiguredFeature<?, ?>>>> list, int i, int j)
//    {
//        if (GenerationStep.Feature.values()[j] == GenerationStep.Feature.UNDERGROUND_ORES)
//        {
//            if (region.getDimension().hasSkyLight()) {
//                int chunkX = region.getCenterChunkX();
//                int chunkZ = region.getCenterChunkZ();
//                Chunk chunk = region.getChunk(chunkX, chunkZ);
//
//                ChunkRandom chunkRandom = new ChunkRandom();
//                ChunkPos chunkPos = chunk.getPos();
//                BlockPos.Mutable mutable = new BlockPos.Mutable();
//                int startX = chunkPos.getStartX();
//                int startY = chunkPos.getStartZ();
//                int k = 0;
//                int maxHeightDeep = Mbtw.DEEP_STONE_MAX;
//                int maxHeightHard = Mbtw.HARD_STONE_MAX;
//                int maxLowerDeviation = 5;
//
//                Iterator posIterator = BlockPos.iterate(startX, 0, startY, startX + 15, 0, startY + 15).iterator();
//
//                while (true) {
//                    BlockPos blockPos;
//                    int o;
//                    int p;
//                    if (!posIterator.hasNext()) {
//                        return;
//                    }
//
//                    blockPos = (BlockPos) posIterator.next();
//                    int x = blockPos.getX();
//                    int y;
//                    int z = blockPos.getZ();
//                    for (o = maxHeightDeep; o >= 0; --o) {
//                        if (o <= maxHeightDeep - maxLowerDeviation + chunkRandom.nextInt(maxLowerDeviation)) {
//                            y = k + o;
//
//                            if (chunk.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.STONE) {
//                                chunk.setBlockState(mutable.set(x, y, z), Mbtw.DEEP_STONE.getDefaultState(), false);
//                            }
//                        }
//                    }
//
//                    for (p = maxHeightHard; p >= maxHeightDeep - maxLowerDeviation - 1; --p) {
//                        if (p <= maxHeightHard - maxLowerDeviation + chunkRandom.nextInt(maxLowerDeviation)) {
//                            y = k + p;
//
//                            if (chunk.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.STONE) {
//                                chunk.setBlockState(mutable.set(x, y, z), Mbtw.HARD_STONE.getDefaultState(), false);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}
