package mbtw.mbtw.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.ToIntFunction;

@Mixin(Blocks.class)
public interface LitStateInvoker {
    @Invoker("createLightLevelFromLitBlockState")
    static ToIntFunction<BlockState> invokeCreateLightLevelFromBlockState(int litLevel) {
        throw new AssertionError();
    }
}

