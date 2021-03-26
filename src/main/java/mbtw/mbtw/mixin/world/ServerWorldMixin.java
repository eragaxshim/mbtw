package mbtw.mbtw.mixin.world;

import mbtw.mbtw.world.*;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements ServerWorldMixinAccessor {

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
    }

    @Shadow public abstract PersistentStateManager getPersistentStateManager();

    private BlockScheduleManager blockScheduleManager;
    private ItemTickManager1 itemTickManager;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getWorldBorder()Lnet/minecraft/world/border/WorldBorder;"))
    protected void initBlockScheduleManager(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> registryKey, DimensionType dimensionType, WorldGenerationProgressListener worldGenerationProgressListener, ChunkGenerator chunkGenerator, boolean debugWorld, long l, List<Spawner> list, boolean bl, CallbackInfo ci)
    {

        this.blockScheduleManager = this.getPersistentStateManager().getOrCreate(() -> new BlockScheduleManager(((ServerWorld) (Object) this)), ChunkedPersistentState.nameFor(this.getDimension(), BlockScheduleManager.key));
        this.itemTickManager = this.getPersistentStateManager().getOrCreate(() -> new ItemTickManager1(((ServerWorld) (Object) this)), ItemTickManager1.nameFor(this.getDimension()));
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=tickBlocks"))
    protected void tickBlockScheduler2(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci)
    {
        Profiler profiler = this.getProfiler();
        profiler.swap("blockSchedule");
        blockScheduleManager.tick(chunk.getPos());
    }

    @Inject(method = "onBlockChanged", at = @At(value = "HEAD"))
    protected void cancelChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock, CallbackInfo ci)
    {
        if (oldBlock.getBlock() != newBlock.getBlock() && oldBlock.getBlock() instanceof BlockSchedulable)
        {
            blockScheduleManager.onBlockChanged(pos);
        }
    }

    public BlockScheduleManager getChunkedScheduleManager()
    {
        return this.blockScheduleManager;
    }

    public ItemTickManager1 getItemTickManager() {
        return this.itemTickManager;
    }
}
