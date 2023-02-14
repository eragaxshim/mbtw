package mbtw.mbtw.block.entity;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.VariableCampfireBlock;
import mbtw.mbtw.state.property.MbtwProperties;
import mbtw.mbtw.world.BlockSchedule;
import mbtw.mbtw.world.ServerWorldMixinAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VariableCampfireBlockEntity extends CampfireBlockEntity {
    private int burnTime;
    private int burnableTime;
    private int tempWhileFueling;
    private int burnPower;
    private int embersTime;

    public VariableCampfireBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.burnableTime = 1600;
        this.calculateBurnPower();
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, VariableCampfireBlockEntity campfire) {
        boolean isRaining = false;
        if (world.isRaining() && world.isSkyVisible(pos))
        {
            isRaining = true;
            if (world.getRandom().nextFloat() < 0.01F)
            {
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.2F, (float) (0.9 + 0.1 * world.getRandom().nextFloat()));
            }
        }

        boolean isEmbers;
        if (state.get(Properties.LIT))
        {
            if (campfire.embersTime != 0)
            {
                campfire.embersTime = 0;
            }

            if (campfire.burnableTime > 0)
            {
                int burnTransfer = Math.min(campfire.burnPower * 50, campfire.burnableTime);
                campfire.tempWhileFueling += burnTransfer * 8 + 6000;
                campfire.burnTime += burnTransfer;
                campfire.burnableTime -= burnTransfer;
            }
            else if (campfire.tempWhileFueling > 0) {
                campfire.tempWhileFueling -= Math.min((int)(campfire.tempWhileFueling*0.05) + 4, campfire.tempWhileFueling);
            }

            if (campfire.burnTime == 0)
            {
                world.setBlockState(pos, state.with(Properties.LIT, false).with(MbtwProperties.EMBERS, true).with(MbtwProperties.FIRE_SIZE, 1), 3);
                campfire.burnableTime = 0;
                campfire.tempWhileFueling = 0;
                campfire.updateListeners();
            }
            else {
                if (world.getTime() % 5 == 0)
                {
                    campfire.calculateBurnPower();
                    campfire.markDirty();
                }
                if ((campfire.burnTime < 300 && campfire.tempWhileFueling == 0 && world.getTime() % 20 == 0) || world.getTime() % 30 == 0)
                {
                    VariableCampfireBlockEntity.updateFireSize(world, pos, state, campfire, isRaining);
                }
                campfire.burnTime -= Math.min(Math.min(campfire.burnPower, Math.max((int)(0.05*campfire.burnTime), 200))*(isRaining ? 3 : 1), campfire.burnTime);
            }
        }
        else if (world.getTime() % 20 == 0 && ((isEmbers = state.get(MbtwProperties.EMBERS)) || state.get(MbtwProperties.FIRE_SIZE) == 0))
        {
            campfire.embersTime += 20*(isRaining ? 3 : 1);

            if (campfire.embersTime >= 100 && isEmbers)
            {
                campfire.embersTime = 0;
                world.setBlockState(pos, Mbtw.ASH.getDefaultState());
                world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 0.1F, (float) (0.9 + 0.1 * world.getRandom().nextFloat()));
                BlockSchedule blockSchedule = new BlockSchedule(pos, Mbtw.ASH);
                ((ServerWorldMixinAccessor)world).getChunkedScheduleManager().schedule(360, blockSchedule);
                campfire.updateListeners();
                return;
            }
            //fire_size = 0, so special case where there is fuel and is smoldering
            else if (campfire.embersTime >= 800 && !isEmbers)
            {
                campfire.burnableTime = 0;
                world.setBlockState(pos, state.with(MbtwProperties.EMBERS, true).with(MbtwProperties.FIRE_SIZE, 1));
                campfire.updateListeners();
            }
            else {
                campfire.markDirty();
            }
            CampfireBlockEntity.unlitServerTick(world, pos, state, campfire);
        }
        else if (world.getTime() % 40 == 0) {
            if (BlockPos.streamOutwards(pos, 1, 0, 1)
                    .anyMatch(blockPos -> {
                        BlockState block = world.getBlockState(blockPos);
                        if (block.getBlock() instanceof VariableCampfireBlock)
                        {
                            return block.get(Properties.LIT) && block.get(MbtwProperties.FIRE_SIZE) > 1;
                        }
                        else return block.isOf(Blocks.FIRE);
                    })
            && world.getRandom().nextFloat() > 0.75F) {
                ((VariableCampfireBlock) state.getBlock()).ignite(world, null, null, state, pos);
                campfire.updateListeners();
            }
        }
        VariableCampfireBlockEntity.updateItemsBeingCooked(world, pos, state, campfire);
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

    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.burnTime = tag.getInt("BurnTime");
        this.burnableTime = tag.getInt("BurnableTime");
        this.calculateBurnPower();
        this.tempWhileFueling = tag.getInt("TempWhileFueling");
        this.embersTime = tag.getInt("EmbersTime");
    }

    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("BurnTime", this.burnTime);
        tag.putInt("BurnableTime", this.burnableTime);
        tag.putInt("TempWhileFueling", this.tempWhileFueling);
        tag.putInt("EmbersTime", this.embersTime);
    }

    private static void updateFireSize(World world, BlockPos pos, BlockState state,
                                       VariableCampfireBlockEntity campfire, boolean isRaining)
    {
        int fireSize = state.get(MbtwProperties.FIRE_SIZE);
        int newFireSize = fireSize;
        int burnComparisonValue = campfire.burnTime + campfire.tempWhileFueling;
        if (burnComparisonValue < 750)
        {
            if (fireSize > 1)
            {
                newFireSize--;
            }
            else if (campfire.tempWhileFueling == 0 && campfire.burnTime < 450 && world.getRandom().nextFloat() < 0.2F)
            {
                newFireSize = 0;
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.2F, (float) (0.9 + 0.1 * world.getRandom().nextFloat()));
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
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.3F, (float) (0.9 + 0.1 * world.getRandom().nextFloat()));
            }
            world.setBlockState(pos, state.with(MbtwProperties.FIRE_SIZE, newFireSize));
            campfire.updateListeners();
        }
        else {
            campfire.markDirty();
        }
    }

    public static void updateItemsBeingCooked(World world, BlockPos pos, BlockState state, VariableCampfireBlockEntity campfire) {
        CampfireBlockEntityMixinAccessor campfireAccess = (CampfireBlockEntityMixinAccessor)campfire;
        DefaultedList<ItemStack> itemsBeingCooked = campfireAccess.getItemsBeingCooked();
        for(int i = 0; i < itemsBeingCooked.size(); ++i) {
            ItemStack itemStack = itemsBeingCooked.get(i);
            if (!itemStack.isEmpty()) {
                int fireSize = state.get(MbtwProperties.FIRE_SIZE);
                campfireAccess.getCookingTimes()[i] += 1 - (fireSize == 1 ? world.getRandom().nextInt(1) : 0) + (fireSize == 4 ? 1 : 0);

                if (campfireAccess.getFinishedItems()[i] && campfireAccess.getCookingTimes()[i] > 2 * campfireAccess.getCookingTotalTimes()[i])
                {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), Mbtw.ASH_PILE.getDefaultStack());
                    campfireAccess.getFinishedItems()[i] = false;
                    campfireAccess.getItemsBeingCooked().set(i, ItemStack.EMPTY);
                }
                else if (!campfireAccess.getFinishedItems()[i] && campfireAccess.getCookingTimes()[i] >= campfireAccess.getCookingTotalTimes()[i]) {
                    Inventory inventory = new SimpleInventory(itemStack);
                    DynamicRegistryManager registryManager = world.getRegistryManager();
                    ItemStack craftedStack = world.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, inventory, world).map((campfireCookingRecipe) -> campfireCookingRecipe.craft(inventory, registryManager)).orElse(itemStack);
                    if (craftedStack != itemStack)
                    {
                        campfireAccess.getFinishedItems()[i] = true;
                        campfireAccess.getItemsBeingCooked().set(i, craftedStack);
                    }
                    else {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), craftedStack);
                        campfireAccess.getItemsBeingCooked().set(i, ItemStack.EMPTY);
                    }
                }
                campfire.updateListeners();
            }
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
