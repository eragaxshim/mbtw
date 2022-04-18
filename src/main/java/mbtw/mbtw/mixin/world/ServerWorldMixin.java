package mbtw.mbtw.mixin.world;

import mbtw.mbtw.world.BlockSchedulable;
import mbtw.mbtw.world.BlockScheduleManager;
import mbtw.mbtw.world.ChunkedPersistentState;
import mbtw.mbtw.world.ServerWorldMixinAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.raid.RaidManager;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.Spawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements ServerWorldMixinAccessor {

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> registryEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, registryEntry, profiler, isClient, debugWorld, seed);
    }

    @Shadow public abstract PersistentStateManager getPersistentStateManager();

    private BlockScheduleManager blockScheduleManager;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getWorldBorder()Lnet/minecraft/world/border/WorldBorder;"))
    protected void initBlockScheduleManager(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey, RegistryEntry<DimensionType> registryEntry, WorldGenerationProgressListener worldGenerationProgressListener, ChunkGenerator chunkGenerator, boolean debugWorld, long seed, List<Spawner> spawners, boolean shouldTickTime, CallbackInfo ci)
    {
        this.blockScheduleManager = this.getPersistentStateManager().getOrCreate(nbt -> BlockScheduleManager.fromNbt((ServerWorld) (Object) this, nbt), () -> new BlockScheduleManager((ServerWorld) (Object) this), BlockScheduleManager.key);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    protected void testTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci)
    {}

    @Inject(method = "tickChunk", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=tickBlocks"))
    protected void tickBlockScheduler2(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci)
    {
        Profiler profiler = this.getProfiler();
        profiler.swap("blockSchedule");
        blockScheduleManager.tick(chunk.getPos());
    }

    @Inject(method = "onBlockChanged", at = @At(value = "HEAD"))
    protected void cancelChanged(BlockPos pos, BlockState oldState, BlockState newState, CallbackInfo ci)
    {
        if (oldState.getBlock() != newState.getBlock() && oldState.getBlock() instanceof BlockSchedulable)
        {
            blockScheduleManager.onBlockChanged(pos, newState);
        }
    }

    public BlockScheduleManager getChunkedScheduleManager()
    {
        return this.blockScheduleManager;
    }
}
