package mbtw.mbtw.mixin;
import mbtw.mbtw.block.MultiBreakBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerInteractionManager.class)
abstract class MbtwMixin {
    @Redirect(method = "tryBreakBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    private boolean notRemoveBlock(ServerWorld serverWorld, BlockPos pos, boolean move) {
        BlockState state = serverWorld.getBlockState(pos);
        try {
            MultiBreakBlock block = (MultiBreakBlock) state.getBlock();
            IntProperty BROKEN = (IntProperty) block.getStateManager().getProperty("broken");
            int i = state.get(BROKEN);
            if (i < block.breakingPoint) {
                return false;
            }
            else {
                return serverWorld.removeBlock(pos, move);
            }
        }
        catch(ClassCastException e) {
            return serverWorld.removeBlock(pos, move);
        }
    }
}


