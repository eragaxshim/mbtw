package mbtw.mbtw.block.entity;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.HopperConversionStore;
import mbtw.mbtw.block.MechanicalSink;
import mbtw.mbtw.inventory.BlockStateInventory;
import mbtw.mbtw.inventory.FilterInventory;
import mbtw.mbtw.inventory.HopperInventory;
import mbtw.mbtw.inventory.SingleFilterInventory;
import mbtw.mbtw.recipe.HopperRecipe;
import mbtw.mbtw.util.NbtUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Map;

public class MechanicalHopperBlockEntity extends LockableContainerBlockEntity
        implements MechanicalSinkBlockEntity, HopperConversionStore, HopperInventory {
    private int sink;
    private int availablePower;
    private Block filter;
    private final Map<Identifier, Integer> conversionProgress = new Object2IntOpenHashMap<>();

    private final RecipeManager.MatchGetter<FilterInventory, ? extends HopperRecipe> matchGetter;

    public MechanicalHopperBlockEntity(BlockPos pos, BlockState state) {
        super(Mbtw.MECHANICAL_HOPPER_ENTITY, pos, state);
        this.matchGetter = RecipeManager.createCachedMatchGetter(Mbtw.HOPPER_FILTERING);
    }

    @Override
    public MechanicalSink sinkBlock() {
        return (MechanicalSink) Mbtw.MECHANICAL_HOPPER;
    }

    public static int[] inventoryCounts(DefaultedList<ItemStack> inventory) {
        return inventory.stream().mapToInt(ItemStack::getCount).toArray();
    }

    private void spawnItem(World world, ItemStack stack, BlockPos pos) {
        double x = pos.getX() + world.random.nextTriangular(0.5, 0.03);
        double y = pos.getY() + world.random.nextTriangular(0.8, 0.03);
        double z = pos.getZ() + world.random.nextTriangular(0.5, 0.03);

        ItemEntity itemEntity = new ItemEntity(world, x, y, z, stack);
        itemEntity.setVelocity(
                world.random.nextTriangular(0, 0.0172275),
                world.random.nextTriangular(0.35, 0.0172275*3),
                world.random.nextTriangular(0, 0.0172275)
        );
        world.spawnEntity(itemEntity);
    }



    public static void recipeTick(World world, BlockPos pos, BlockState state, ItemStack addedStack, MechanicalHopperBlockEntity hopperEntity) {
        // Update states for use in recipe
        BlockStateInventory.updateStates(world, pos, hopperEntity);
        HopperRecipe recipe = hopperEntity.matchGetter.getFirstMatch((FilterInventory) hopperEntity, world).orElse(null);
        if (recipe == null) {
            return;
        }
        // Get output
        int inputStackCount = addedStack.getCount();

        int timesCraftable = recipe.timesCraftable(inputStackCount);
        ItemStack output = recipe.craft(recipeInventory, world.getRegistryManager());

        // For each time we craft we want to get the recipe output
        output.setCount(output.getCount()*timesCraftable);
        // We decrement the input stack
        recipe.decrementInput(addedStack, timesCraftable);
        // We spawn the output
        hopperEntity.spawnItem(world, output, pos);

        recipe.conversionRecipe(world).convert(world, pos, hopperEntity, hopperEntity);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, MechanicalHopperBlockEntityOld blockEntity) {
        int[] previousCounts = inventoryCounts(blockEntity.getInvStackList());
        HopperBlockEntity.serverTick(world, pos, state, blockEntity);
        recipeTick(world, pos, state, previousCounts, blockEntity);
        MechanicalSinkBlockEntity.mechanicalTick(world, pos, state, blockEntity);
    }

    @Override
    public int getAvailablePower() {
        return availablePower;
    }

    @Override
    public int getSink() {
        return sink;
    }

    @Override
    public void setAvailablePower(int availablePower) {
        this.availablePower = availablePower;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.filter = NbtUtil.blockFromNbt(nbt, "Filter");
        this.availablePower = nbt.getShort("AvailablePower");
        this.sink = nbt.getShort("Sink");
        NbtList nbtList = nbt.getList("ConversionProgress", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound conversionNbt = nbtList.getCompound(i);
            conversionProgress.put(new Identifier(conversionNbt.getString("identifier")), conversionNbt.getInt("value"));
        }
    }

    public void setFilter() {
        if (this.filter == null || this.filter == Blocks.AIR) {
            this.filter = Blocks.SOUL_SAND;
        } else {
            this.filter = Blocks.AIR;
        }

        this.updateListeners();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtUtil.writeBlockToNbt(nbt, "Filter", this.filter);
        nbt.putShort("Sink", (short)this.sink);
        nbt.putShort("AvailablePower", (short)this.availablePower);
        //nbt.putShort("ConversionProgress", (short)this.conversionProgress);
        NbtList nbtList = new NbtList();
        conversionProgress.forEach((key, value) -> {
            NbtCompound conversionNbt = new NbtCompound();
            conversionNbt.putString("identifier", key.toString());
            conversionNbt.putInt("value", value);
            nbtList.add(conversionNbt);
        });
        nbt.put("ConversionProgress", nbtList);

    }

    @Override
    protected Text getContainerName() {
        return null;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    public void setSink(int sink) {
        this.sink = sink;
    }

    @Override
    public int incrementConversionProgress(Identifier conversionType) {
        int current = conversionProgress.getOrDefault(conversionType, 0);
        int newValue = current+1;
        conversionProgress.put(conversionType, newValue);
        this.markDirty();
        return newValue;
    }

    @Override
    public void resetConversionProgress(Identifier conversionType) {
        conversionProgress.put(conversionType, 0);
        this.markDirty();
    }

    public BlockState getFilterModel() {
        if (filter == null) {
            return null;
        }
        return filter.getDefaultState();
    }

    // This is necessary to render the filter on game load
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    // We need to send the filter NBT to the client
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        NbtUtil.writeBlockToNbt(nbt, "Filter", filter);
        return nbt;
    }

    // Used to ensure proper update when changing filter
    private void updateListeners() {
        this.markDirty();
        World world = this.getWorld();
        if (world != null) {
            world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return null;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public BlockState getConnectedState(int index) {
        return null;
    }

    @Override
    public void setConnectedState(int index, BlockState state) {

    }

    @Override
    public int connectedSize() {
        return 0;
    }

    @Override
    public Direction connectedDirection(int index) {
        return null;
    }

    @Override
    public Block getFilter() {
        return null;
    }

    @Override
    public void setFilter(Block filter) {

    }
}
