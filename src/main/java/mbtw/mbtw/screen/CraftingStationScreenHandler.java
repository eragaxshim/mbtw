package mbtw.mbtw.screen;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;

public class CraftingStationScreenHandler extends CraftingScreenHandler {
    public CraftingStationScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(syncId, playerInventory);
    }

    public CraftingStationScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(syncId, playerInventory, context);
    }

//    public boolean canUse(PlayerEntity player) {
//        return canUse(this.context, player, Blocks.CRAFTING_TABLE);
//    }
}
