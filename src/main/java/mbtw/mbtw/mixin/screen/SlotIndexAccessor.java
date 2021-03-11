package mbtw.mbtw.mixin.screen;

import net.minecraft.screen.slot.Slot;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Slot.class)
public interface SlotIndexAccessor {
    @Accessor("index")
    int getIndex();
}