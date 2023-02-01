package mbtw.mbtw.mixin.screen;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface SlotIndexAccessor {
    @Accessor("index")
    int getIndex();
}