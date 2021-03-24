package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.AshBlock;
import mbtw.mbtw.block.VariableCampfireBlock;
import mbtw.mbtw.mixin.block.CampfireBlockEntityAccessor;
import mbtw.mbtw.mixin.block.CampfireBlockEntityMixin;
import mbtw.mbtw.mixin.world.ServerWorldMixin;
import mbtw.mbtw.world.BlockSchedule;
import mbtw.mbtw.world.BlockScheduleManagerAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class VariableCampfireBlockEntity extends CampfireBlockEntity {
    private int burnTime;
    private int burnableTime;
    private int tempWhileFueling;
    private int burnPower;
    private int embersTime;

    public VariableCampfireBlockEntity() {
        super();
        this.burnableTime = 200;
        this.calculateBurnPower();
    }

    public void tick() {
        BlockState state = this.getCachedState();

        boolean isServer = this.world != null && !this.world.isClient;
        if (isServer && state.isOf(Mbtw.VARIABLE_CAMPFIRE))
        {
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
                    this.world.setBlockState(pos, state.with(Properties.LIT, false).with(VariableCampfireBlock.EMBERS, true), 3);
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
                    if (this.world.getTime() % 30 == 0)
                    {
                        this.updateFireSize(this.world);
                    }
                    this.burnTime -= Math.min(Math.min(this.burnPower, Math.max((int)(0.05*burnTime), 200)), this.burnTime);
                }
            }
            else if (state.get(VariableCampfireBlock.EMBERS) && this.world.getTime() % 20 == 0)
            {
                this.embersTime += 20;

                if (this.embersTime >= 20)
                {
                    this.world.setBlockState(pos, Mbtw.ASH.getDefaultState().with(AshBlock.SCHEDULED, true).with(AshBlock.III, 5));
                    BlockSchedule blockSchedule = new BlockSchedule.Builder(pos, Mbtw.ASH)
                            .addProperty(AshBlock.SCHEDULED, true)
                            .build();

                    ((BlockScheduleManagerAccess)world).getBlockScheduleManager().schedule(20, blockSchedule);
                }
                else {
                    this.markDirty();
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

    private void updateFireSize(World world)
    {
        BlockState state = this.getCachedState();
        int fireSize = state.get(VariableCampfireBlock.FIRE_SIZE);
        int newFireSize = fireSize;
        int burnComparisonValue = this.burnTime + this.tempWhileFueling;
        if (burnComparisonValue < 600)
        {
            if (fireSize > 1)
            {
                newFireSize--;
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
