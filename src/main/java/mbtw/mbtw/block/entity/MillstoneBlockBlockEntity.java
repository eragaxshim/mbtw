package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.MechanicalSink;
import mbtw.mbtw.screen.MillstoneScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class MillstoneBlockBlockEntity extends AbstractBlockMechanicalProcessor {
    public MillstoneBlockBlockEntity(BlockPos pos, BlockState state) {
        super(Mbtw.MILLSTONE_ENTITY, pos, state, (MechanicalSink) Mbtw.MILLSTONE, Mbtw.MILLING);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.mbtw.millstone");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new MillstoneScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public int getMaxCountOfSlot(int slotIndex) {
        return 64;
    }

    @Override
    public MechanicalSink sink() {
        return (MechanicalSink) Mbtw.MILLSTONE;
    }
}
