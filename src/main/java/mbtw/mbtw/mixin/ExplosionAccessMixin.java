package mbtw.mbtw.mixin;

import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Explosion.class)
public interface ExplosionAccessMixin {
    @Accessor("power")
    float getPower();
}

