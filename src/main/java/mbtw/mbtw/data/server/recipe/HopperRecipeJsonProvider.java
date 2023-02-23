package mbtw.mbtw.data.server.recipe;

import com.google.gson.JsonObject;
import mbtw.mbtw.recipe.HopperRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class HopperRecipeJsonProvider extends AbstractRecipeJsonProvider {
    protected final Ingredient input;
    protected final Block filter;
    protected final int inputCount;
    protected final Item result;
    protected final Identifier conversionRecipeId;

    public HopperRecipeJsonProvider(Ingredient input, Block filter, int inputCount, Item result, Identifier conversionRecipeId, RecipeSerializer<? extends HopperRecipe> serializer) {
        super(serializer);
        this.input = input;
        this.filter = filter;
        this.inputCount = inputCount;
        this.result = result;
        this.conversionRecipeId = conversionRecipeId;
    }

    @Override
    public void serialize(JsonObject json) {
        json.add("ingredient", this.input.toJson());
        json.addProperty("inputcount", this.inputCount);
        json.addProperty("result", Registries.ITEM.getId(this.result).toString());
        json.addProperty("filter", Registries.BLOCK.getId(this.filter).toString());
        json.addProperty("conversion", this.conversionRecipeId.toString());
    }
}
