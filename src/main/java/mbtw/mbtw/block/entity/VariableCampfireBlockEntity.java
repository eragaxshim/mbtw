package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.VariableCampfireBlock;
import mbtw.mbtw.world.BlockSchedule;
import mbtw.mbtw.world.BlockScheduleManagerAccess;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VariableCampfireBlockEntity extends CampfireBlockEntity {
    private int burnTime;
    private int burnableTime;
    private int tempWhileFueling;
    private int burnPower;
    private int embersTime;

    public VariableCampfireBlockEntity() {
        super();
        this.burnableTime = 1600;
        this.calculateBurnPower();
    }

    public void tick() {
        BlockState state = this.getCachedState();

        boolean isServer = this.world != null && !this.world.isClient;
        if (isServer && state.getBlock() instanceof VariableCampfireBlock)
        {
            boolean isRaining = false;
            if (this.world.isRaining() && this.world.isSkyVisible(this.pos))
            {
                isRaining = true;
                if (this.world.getRandom().nextFloat() < 0.01F)
                {
                    this.world.playSound(null, this.pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.2F, (float) (0.9 + 0.1 * world.getRandom().nextFloat()));
                }
            }

            boolean isEmbers;
            if (state.get(Properties.LIT))
            {
                if (this.embersTime != 0)
                {
                    this.embersTime = 0;
                }

                if (this.burnableTime > 0)
                {
                    int burnTransfer = Math.min(this.burnPower * 50, this.burnableTime);
                    this.tempWhileFueling += burnTransfer * 8 + 6000;
                    this.burnTime += burnTransfer;
                    this.burnableTime -= burnTransfer;
                }
                else if (this.tempWhileFueling > 0) {
                    this.tempWhileFueling -= Math.min((int)(this.tempWhileFueling*0.05) + 4, this.tempWhileFueling);
                }

                if (this.burnTime == 0)
                {
                    this.world.setBlockState(pos, state.with(Properties.LIT, false).with(VariableCampfireBlock.EMBERS, true).with(VariableCampfireBlock.FIRE_SIZE, 1), 3);
                    this.burnableTime = 0;
                    this.tempWhileFueling = 0;
                    this.updateListeners();
                }
                else {
                    if (this.world.getTime() % 5 == 0)
                    {
                        this.calculateBurnPower();
                        this.markDirty();
                    }
                    if ((this.burnTime < 300 && this.tempWhileFueling == 0 && this.world.getTime() % 20 == 0) || this.world.getTime() % 30 == 0)
                    {
                        this.updateFireSize(this.world, isRaining);
                    }
                    this.burnTime -= Math.min(Math.min(this.burnPower, Math.max((int)(0.05*burnTime), 200))*(isRaining ? 3 : 1), this.burnTime);
                }
            }
            else if (this.world.getTime() % 20 == 0 && ((isEmbers = state.get(VariableCampfireBlock.EMBERS)) || state.get(VariableCampfireBlock.FIRE_SIZE) == 0))
            {
                this.embersTime += 20*(isRaining ? 3 : 1);

                if (this.embersTime >= 1600 && isEmbers)
                {
                    this.embersTime = 0;
                    this.world.setBlockState(this.pos, Mbtw.ASH.getDefaultState());
                    this.world.playSound(null, this.pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 0.1F, (float) (0.9 + 0.1 * this.world.getRandom().nextFloat()));
                    BlockSchedule blockSchedule = new BlockSchedule(pos, Mbtw.ASH);
                    ((BlockScheduleManagerAccess)world).getBlockScheduleManager().schedule(4500, blockSchedule);
                    this.updateListeners();
                    return;
                }
                //fire_size = 0, so special case where there is fuel and is smoldering
                else if (this.embersTime >= 800 && !isEmbers)
                {
                    this.burnableTime = 0;
                    this.world.setBlockState(this.pos, state.with(VariableCampfireBlock.EMBERS, true).with(VariableCampfireBlock.FIRE_SIZE, 1));
                    this.updateListeners();
                }
                else {
                    this.markDirty();
                }
            }
            else if (this.world.getTime() % 40 == 0) {
                if (BlockPos.streamOutwards(this.pos, 1, 0, 1)
                        .anyMatch(blockPos -> {
                            BlockState block = world.getBlockState(blockPos);
                            if (block.getBlock() instanceof VariableCampfireBlock)
                            {
                                return block.get(Properties.LIT) && block.get(VariableCampfireBlock.FIRE_SIZE) > 1;
                            }
                            else return block.isOf(Blocks.FIRE);
                        })
                && this.world.getRandom().nextFloat() > 0.75F) {
                    ((VariableCampfireBlock) state.getBlock()).ignite(this.world, null, null, state, this.pos);
                    this.updateListeners();
                }
            }

        }
        super.tick();
    }

    public void addFuel(ItemStack itemStack, int fuelTime)
    {
        itemStack.split(1);
        this.burnableTime += Math.round(fuelTime * 4 / (float) this.burnPower) + 1;
        this.markDirty();
    }

    public void resetEmbers()
    {
        this.embersTime = 0;
    }

    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.burnTime = tag.getInt("BurnTime");
        this.burnableTime = tag.getInt("BurnableTime");
        this.calculateBurnPower();
        this.tempWhileFueling = tag.getInt("TempWhileFueling");
        this.embersTime = tag.getInt("EmbersTime");
    }

    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("BurnTime", this.burnTime);
        tag.putInt("BurnableTime", this.burnableTime);
        tag.putInt("TempWhileFueling", this.tempWhileFueling);
        tag.putInt("EmbersTime", this.embersTime);
        return tag;
    }

    private void updateFireSize(World world, boolean isRaining)
    {
        BlockState state = this.getCachedState();
        int fireSize = state.get(VariableCampfireBlock.FIRE_SIZE);
        int newFireSize = fireSize;
        int burnComparisonValue = this.burnTime + this.tempWhileFueling;
        if (burnComparisonValue < 750)
        {
            if (fireSize > 1)
            {
                newFireSize--;
            }
            else if (this.tempWhileFueling == 0 && this.burnTime < 450 && world.getRandom().nextFloat() < 0.2F)
            {
                newFireSize = 0;
                world.playSound(null, this.pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.2F, (float) (0.9 + 0.1 * world.getRandom().nextFloat()));
            }
            else if (fireSize == 0)
            {
                newFireSize = 1;
            }
        }
        else if (burnComparisonValue < 6000)
        {
            if (fireSize > 2)
            {
                newFireSize--;
            }
            else if (fireSize < 2)
            {
                newFireSize++;
            }
        }
        else if (burnComparisonValue < 18000)
        {
            if (fireSize > 3)
            {
                newFireSize--;
            }
            else if (fireSize < 3)
            {
                newFireSize++;
            }
        }
        else
        {
            if (fireSize < 4)
            {
                newFireSize++;
            }
        }

        if (newFireSize != fireSize)
        {
            if (newFireSize < fireSize && isRaining)
            {
                world.playSound(null, this.pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.3F, (float) (0.9 + 0.1 * world.getRandom().nextFloat()));
            }
            world.setBlockState(this.pos, state.with(VariableCampfireBlock.FIRE_SIZE, newFireSize));
            this.updateListeners();
        }
        else {
            this.markDirty();
        }
    }

    private void updateListeners() {
        this.markDirty();
        World world = this.getWorld();
        if (world != null)
        {
            this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
        }
    }

    private void calculateBurnPower()
    {
        int burnCalc = Math.min(this.burnTime + this.burnableTime, 23000);
        // This formula results in a burnTime of 18000 being closer to 12000 seconds
        // It makes it more efficient to keep your fire at a lower level, below 6000 is quite efficient
        // At the above limit it approaches 500 times less than int max
        this.burnPower = (int) Math.round(Math.pow(1.00004, burnCalc) + Math.pow(1.003, burnCalc - 18000));
    }
}
