package mbtw.mbtw.mixin.block;

import net.minecraft.block.entity.CampfireBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CampfireBlockEntity.class)
public interface CampfireBlockEntityAccessor {
    //@Invoker(value = "updateListeners")
    //void updateListeners();
}
