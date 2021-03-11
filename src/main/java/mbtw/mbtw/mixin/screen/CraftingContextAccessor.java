package mbtw.mbtw.mixin.screen;

import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(CraftingScreenHandler.class)
public interface CraftingContextAccessor {
    @Accessor("context")
    ScreenHandlerContext getContext();
}