package mbtw.mbtw.mixin.client.world;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World {

    protected ClientWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    /**
     * Dark Nights
     */
    @Inject(method = "getStarBrightness", at = @At("RETURN"), cancellable = true)
    protected void changeLightMethod(float tickDelta, CallbackInfoReturnable<Float> cir) {
        // baseValue is between 0.2 (at night) and 1 (height of day)
        double baseValue = cir.getReturnValue();
        baseValue = (baseValue - 0.2) / 0.8f;
        // MoonSize is between 0 and 1
        float plateau = 0.2f*this.getMoonSize();
        cir.setReturnValue((float) (baseValue * (1 - plateau) + plateau));
    }
}
