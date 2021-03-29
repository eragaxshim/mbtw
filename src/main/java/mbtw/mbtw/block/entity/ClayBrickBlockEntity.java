package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.ClayBrickBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

public class ClayBrickBlockEntity extends BlockEntity implements Tickable {
    private int bakeStageTime;
    
    public ClayBrickBlockEntity() {
        super(Mbtw.CLAY_BRICK_ENTITY);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("bakeStageTime", bakeStageTime);

        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        bakeStageTime = tag.getInt("bakeStageTime");
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isClient && this.world.getTime() % 20L == 0L)
        {
            int bake_progress = this.world.getBlockState(this.pos).get(ClayBrickBlock.BAKE_PROGRESS);
            if (bake_progress != 8) {
                BlockState state = this.world.getBlockState(this.pos);

                if (((this.world.isRaining() && this.world.isSkyVisible(this.pos)) || state.get(ClayBrickBlock.WATERLOGGED))) {
                    if (bakeStageTime != 0 || bake_progress != 0) {
                        bakeStageTime = 0;
                        this.world.setBlockState(this.pos, state.with(ClayBrickBlock.BAKE_PROGRESS, 0), 3);
                        this.markDirty();
                    }
                }
                else {
                    long time = this.world.getTimeOfDay();
                    if ((time < 12500 || time > 23500) && this.world.isSkyVisible(this.pos)) {
                        if (state.isOf(Mbtw.CLAY_BRICK)) {
                            if (bakeStageTime > 75) {
                                bakeStageTime = 0;
                                this.world.setBlockState(this.pos, state.with(ClayBrickBlock.BAKE_PROGRESS, bake_progress+1), 3);
                            }
                            else {
                                bakeStageTime++;
                            }
                            this.markDirty();
                        }
                    }
                    else if (bakeStageTime != 0) {
                        bakeStageTime = 0;
                        this.markDirty();
                    }
                }
            }
        }
    }
}
