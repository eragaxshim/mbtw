package mbtw.mbtw.mixin.screen;

import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingScreenHandler.class)
public interface CraftingContextAccessor {
    @Accessor("context")
    ScreenHandlerContext getContext();
}