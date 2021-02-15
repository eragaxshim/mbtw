package mbtw.mbtw.mixin;

import mbtw.mbtw.block.InterceptBreakBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(SurvivesExplosionLootCondition.class)
class SurvivesExplosionMixin {
    @Inject(method = "test", at = @At("HEAD"), cancellable = true)
    protected void changeTest(LootContext lootContext, CallbackInfoReturnable<Boolean> cir)
    {
        BlockState blockState = lootContext.get(LootContextParameters.BLOCK_STATE);
        if (blockState != null)
        {
            Block block = blockState.getBlock();

            if (block == Blocks.STONE || block instanceof InterceptBreakBlock)
            {
                Entity entity = lootContext.get(LootContextParameters.THIS_ENTITY);

                if (!(entity instanceof TntEntity))
                {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}

@Mixin(ExplosionDecayLootFunction.class)
class ExplosionDecayMixin {
    @Inject(method = "process", at = @At("HEAD"), cancellable = true)
    protected void changeProcess(ItemStack stack, LootContext lootContext, CallbackInfoReturnable<Boolean> cir)
    {
        BlockState blockState = lootContext.get(LootContextParameters.BLOCK_STATE);
        if (blockState != null)
        {
            Block block = blockState.getBlock();

            if (block == Blocks.STONE || block instanceof InterceptBreakBlock)
            {
                Entity entity = lootContext.get(LootContextParameters.THIS_ENTITY);

                if (!(entity instanceof TntEntity))
                {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
