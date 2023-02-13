package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.screen.CrucibleScreenHandler;
import mbtw.mbtw.screen.MillstoneScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrucibleBlockEntity extends AbstractBlockProcessorEntity {

    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(Mbtw.CRUCIBLE_ENTITY, pos, state, 10, Mbtw.CRUCIBLE_SMELTING);
        this.availablePower = 1;
    }

    public static void serverTick(World world, BlockPos sinkPos, BlockState state, AbstractBlockProcessorEntity processor) {
        recipeTick(world, sinkPos, state, processor);
    }

    @Override
    public int getMaxCountOfSlot(int slotIndex) {
        return 64;
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.mbtw.crucible");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new CrucibleScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    protected int getAvailablePower() {
        return 1;
    }
}
