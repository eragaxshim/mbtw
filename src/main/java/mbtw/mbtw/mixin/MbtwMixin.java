package mbtw.mbtw.mixin;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import mbtw.mbtw.block.InterceptBreakBlock;
import mbtw.mbtw.block.MultiBreakBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.State;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
abstract class MbtwMixin {
    @Redirect(method = "tryBreakBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    private boolean notRemoveBlock(ServerWorld serverWorld, BlockPos pos, boolean move) {
        BlockState state = serverWorld.getBlockState(pos);
        if (state.getBlock() instanceof InterceptBreakBlock)
        {
            BooleanProperty BROKEN = (BooleanProperty) state.getBlock().getStateManager().getProperty("broken");
            if (state.get(BROKEN)) {
                return serverWorld.removeBlock(pos, move);
            }
            else {
                return false;
            }
        }
       else {
           return serverWorld.removeBlock(pos, move);
        }
    }
}

