package mbtw.mbtw.recipe;

import mbtw.mbtw.Mbtw;
import mbtw.mbtw.block.HopperResidueStore;
import mbtw.mbtw.inventory.BlockStateInventory;
import mbtw.mbtw.state.property.MbtwProperties;
import mbtw.mbtw.util.WorldUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
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
    // Can be no more than 1 above maximum souls in souls property
    protected final int countForConversion = 8;


    public HopperBlockConversionRecipe(Identifier id) {
        this.identifier = id;
    }

    @Override
    public boolean matches(BlockStateInventory inventory, World world) {
        return inventory.getConnectedState(0).isOf(Mbtw.URN);
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

    public boolean convert(World world, BlockPos hopperPos, HopperResidueStore store, BlockStateInventory blockStateInventory, int timesConverted) {
        BlockState connectedState = blockStateInventory.getConnectedState(0);
        boolean wantSoulConversion = this.matches(blockStateInventory, world)
                && connectedState != null
                && blockStateInventory.connectedDirection(0) == Direction.DOWN
                && connectedState.contains(MbtwProperties.SOULS);
        if (wantSoulConversion && store.passingResidue()) {
            int souls = connectedState.get(MbtwProperties.SOULS);
            store.resetResidue(conversionType);
            if (souls + timesConverted >= countForConversion) {
                world.breakBlock(hopperPos.offset(Direction.DOWN), false);
                WorldUtil.spawnItem(world, new ItemStack(Mbtw.SOUL_URN), hopperPos.offset(Direction.DOWN), 0.5);
            } else {
                // souls+timesConverted strictly less than countForConversion so not above maximum souls value
                world.setBlockState(hopperPos.offset(Direction.DOWN), connectedState.with(MbtwProperties.SOULS, souls+timesConverted));
            }
            // Since passing residue, any additional souls can just escape
        } else if (!store.passingResidue()) {
            int conversionProgress = store.increaseResidue(conversionType, timesConverted);
            if (conversionProgress >= countForConversion) {
                store.resetResidue(conversionType);
                Direction toConnected = blockStateInventory.connectedDirection(0);
                world.setBlockState(hopperPos.offset(toConnected), Blocks.CRIMSON_STEM.getDefaultState());
                // This would be an explosion or some other nasty effects, so don't return true
                return false;
            }
        } else {
            store.resetResidue(conversionType);
        }

        return true;
    }
}
