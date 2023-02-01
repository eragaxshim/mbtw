package mbtw.mbtw.mixin.world;

import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DefaultBiomeFeatures.class)
public class ReplaceUndergroundOreFeatureMixin {
//    @Inject(method = "addMineables", at = @At("HEAD"), cancellable = true)
//    private static void removeMineables(GenerationSettings.Builder builder, CallbackInfo ci)
//    {
//        ci.cancel();
//    }
//    @Inject(method = "addDefaultOres", at = @At("HEAD"), cancellable = true)
//    private static void removeDefaultOres(GenerationSettings.Builder builder, CallbackInfo ci)
//    {
//        builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, Mbtw.ORE_COAL);
//        builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, Mbtw.ORE_COAL_HARD);
//        builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, Mbtw.ORE_COAL_DEEP);
//        builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, Mbtw.ORE_IRON);
//        builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, Mbtw.ORE_IRON_HARD);
//        builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, Mbtw.ORE_IRON_DEEP);
//        ci.cancel();
//    }
//    @Inject(method = "addExtraGoldOre", at = @At("HEAD"), cancellable = true)
//    private static void removeExtraGoldOre(GenerationSettings.Builder builder, CallbackInfo ci)
//    {
//        ci.cancel();
//    }
//    @Inject(method = "addEmeraldOre", at = @At("HEAD"), cancellable = true)
//    private static void removeEmeraldOre(GenerationSettings.Builder builder, CallbackInfo ci)
//    {
//        ci.cancel();
//    }
}
