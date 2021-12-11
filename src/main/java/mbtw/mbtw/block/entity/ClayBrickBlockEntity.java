package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.ClayBrickBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClayBrickBlockEntity extends BlockEntity {
    private int bakeStageTime;
    
    public ClayBrickBlockEntity(BlockPos pos, BlockState state) {
        super(Mbtw.CLAY_BRICK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("bakeStageTime", bakeStageTime);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        bakeStageTime = tag.getInt("bakeStageTime");
    }

    public static void tick(World world, BlockPos pos, BlockState state, ClayBrickBlockEntity be) {
        if (world != null && !world.isClient && world.getTime() % 20L == 0L)
        {
            int bake_progress = world.getBlockState(pos).get(ClayBrickBlock.BAKE_PROGRESS);
            if (bake_progress != 8) {

                if (((world.isRaining() && world.isSkyVisible(pos)) || state.get(ClayBrickBlock.WATERLOGGED))) {
                    if (be.bakeStageTime != 0 || bake_progress != 0) {
                        be.bakeStageTime = 0;
                        world.setBlockState(pos, state.with(ClayBrickBlock.BAKE_PROGRESS, 0), 3);
                        be.markDirty();
                    }
                }
                else {
                    long time = world.getTimeOfDay();
                    if ((time < 12500 || time > 23500) && world.isSkyVisible(pos)) {
                        if (state.isOf(Mbtw.CLAY_BRICK)) {
                            if (be.bakeStageTime > 75) {
                                be.bakeStageTime = 0;
                                world.setBlockState(pos, state.with(ClayBrickBlock.BAKE_PROGRESS, bake_progress+1), 3);
                            }
                            else {
                                be.bakeStageTime++;
                            }
                            be.markDirty();
                        }
                    }
                    else if (be.bakeStageTime != 0) {
                        be.bakeStageTime = 0;
                        be.markDirty();
                    }
                }
            }
        }
    }
}
