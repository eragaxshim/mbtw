package mbtw.mbtw.block;

import mbtw.mbtw.screen.CraftingStationScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoulforgedWorkstationBlock extends CraftingStationBlock {
    private static final Text SCREEN_TITLE = Text.translatable("container.mbtw.soulforged_workstation");

    public SoulforgedWorkstationBlock(Settings settings) {
        super(settings);
    }

    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> new CraftingStationScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)), SCREEN_TITLE);
    }
}
