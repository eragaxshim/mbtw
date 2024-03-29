package mbtw.mbtw.screen;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.mixin.screen.CraftingContextAccessor;
import mbtw.mbtw.tag.MbtwTagsMaps;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.world.World;

public class TrunkWorkbenchScreenHandler extends CraftingScreenHandler {
    public TrunkWorkbenchScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(syncId, playerInventory);
    }

    public TrunkWorkbenchScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(syncId, playerInventory, context);
    }

    public boolean canUse(PlayerEntity player) {
        ScreenHandlerContext context = ((CraftingContextAccessor)this).getContext();

        BlockState blockState = ((BlockState) context.get(World::getBlockState, false));

        if (blockState.isIn(MbtwTagsMaps.TRUNKS))
        {
            return canUse(context, player, blockState.getBlock());
        }
        else {
            return canUse(context, player, Mbtw.OAK_TRUNK);
        }
    }
}
