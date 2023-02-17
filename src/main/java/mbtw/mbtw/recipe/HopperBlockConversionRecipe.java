package mbtw.mbtw.recipe;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.HopperConversionStore;
import mbtw.mbtw.inventory.BlockStateInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HopperBlockConversionRecipe implements Recipe<BlockStateInventory> {
    protected Identifier identifier;
    protected Identifier conversionType = new Identifier(Mbtw.MOD_ID, "souls");
    protected final int countForConversion = 8;


    public HopperBlockConversionRecipe(Identifier id) {
        this.identifier = id;
    }

    @Override
    public boolean matches(BlockStateInventory inventory, World world) {


        return inventory.getConnectedState(0).isOf(Blocks.OAK_PLANKS);
    }

    @Override
    public ItemStack craft(BlockStateInventory inventory, DynamicRegistryManager registryManager) {
        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return null;
    }

    @Override
    public Identifier getId() {
        return identifier;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Mbtw.HOPPER_BLOCK_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return Mbtw.HOPPER_BLOCK_FILTERING;
    }

    public void convert(World world, BlockPos hopperPos, HopperConversionStore store, BlockStateInventory blockStateInventory) {
        int conversionProgress = store.incrementConversionProgress(conversionType);
        if (conversionProgress >= countForConversion) {
            store.resetConversionProgress(conversionType);
            Direction toConnected = blockStateInventory.connectedDirection(0);
            world.setBlockState(hopperPos.offset(toConnected), Blocks.WARPED_STEM.getDefaultState());
        }
    }
}
