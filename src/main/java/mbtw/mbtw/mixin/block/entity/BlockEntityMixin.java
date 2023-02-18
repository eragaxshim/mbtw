package mbtw.mbtw.mixin.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.entity.MechanicalHopperBlockEntityOld;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BlockEntity.class)
public class BlockEntityMixin {
    // Do not make static
    @ModifyVariable(method = "<init>", at = @At(value = "LOAD"), argsOnly = true)
    protected BlockEntityType<?> changeType(BlockEntityType<?> type)
    {
        // ignore warning
        if (((Object)this) instanceof MechanicalHopperBlockEntityOld) {
            return Mbtw.MECHANICAL_HOPPER_ENTITY;
        }

        if (type == BlockEntityType.CAMPFIRE)
        {
            return Mbtw.VARIABLE_CAMPFIRE_ENTITY;
        }
        else {
            return type;
        }
    }
}
