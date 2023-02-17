package mbtw.mbtw.recipe;

import mbtw.mbtw.inventory.BlockStateInventory;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class BlockStateRecipe<T extends BlockStateInventory> implements Recipe<T> {
    protected final Identifier identifier;


    public BlockStateRecipe(Identifier identifier) {
        this.identifier = identifier;
    }

}
