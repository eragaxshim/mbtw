package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.BrickBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

public class BrickBlockEntity extends BlockEntity implements Tickable {
    private int bake_stage_time;


    public BrickBlockEntity() {
        super(Mbtw.CLAY_BRICK_ENTITY);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("bake_stage_time", bake_stage_time);

        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        bake_stage_time = tag.getInt("bake_stage_time");
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isClient && this.world.getTime() % 20L == 0L)
        {
            int bake_progress = this.world.getBlockState(this.pos).get(BrickBlock.BAKE_PROGRESS);
            if (bake_progress != 8) {
                BlockState state = this.world.getBlockState(this.pos);

                if (((this.world.isRaining() && this.world.isSkyVisible(this.pos)) || state.get(BrickBlock.WATERLOGGED))) {
                    if (bake_stage_time != 0 || bake_progress != 0) {
                        bake_stage_time = 0;
                        this.world.setBlockState(this.pos, state.with(BrickBlock.BAKE_PROGRESS, 0), 3);
                        this.markDirty();
                    }
                }
                else {
                    long time = this.world.getTimeOfDay();
                    if ((time < 12500 || time > 23500) && this.world.isSkyVisible(this.pos)) {
                        if (state.isOf(Mbtw.CLAY_BRICK)) {
                            if (bake_stage_time > 75) {
                                bake_stage_time = 0;
                                this.world.setBlockState(this.pos, state.with(BrickBlock.BAKE_PROGRESS, bake_progress+1), 3);
                            }
                            else {
                                bake_stage_time++;
                            }
                            this.markDirty();
                        }
                    }
                    else if (bake_stage_time != 0) {
                        bake_stage_time = 0;
                        this.markDirty();
                    }
                }
            }
        }
    }
}
