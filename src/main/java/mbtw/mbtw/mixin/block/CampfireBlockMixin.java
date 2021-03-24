package mbtw.mbtw.mixin.block;

import mbtw.mbtw.block.entity.VariableCampfireBlockEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin {
    @Inject(method = "createBlockEntity", at = @At("HEAD"), cancellable = true)
    protected void createBlockEntity(BlockView world, CallbackInfoReturnable<BlockEntity> cir) {
        cir.setReturnValue(new VariableCampfireBlockEntity());
    }
}
