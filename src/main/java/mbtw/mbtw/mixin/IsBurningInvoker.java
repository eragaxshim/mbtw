package mbtw.mbtw.mixin;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.ToIntFunction;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface IsBurningInvoker {
    @Invoker("isBurning")
    boolean invokeIsBurning();
}
