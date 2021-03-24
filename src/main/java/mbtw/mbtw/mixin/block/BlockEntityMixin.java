package mbtw.mbtw.mixin.block;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.entity.VariableCampfireBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.passive.CowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public class BlockEntityMixin {
    @ModifyVariable(method = "<init>", at = @At(value = "LOAD"), argsOnly = true)
    protected BlockEntityType<?> changeType(BlockEntityType<?> type)
    {
        if (type == BlockEntityType.CAMPFIRE)
        {
            return Mbtw.VARIABLE_CAMPFIRE_ENTITY;
        }
        else {
            return type;
        }
    }
}
