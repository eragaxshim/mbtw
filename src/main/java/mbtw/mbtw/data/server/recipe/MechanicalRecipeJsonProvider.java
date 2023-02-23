package mbtw.mbtw.data.server.recipe;

import com.google.gson.JsonObject;
import mbtw.mbtw.recipe.AbstractMechanicalRecipe;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;

public class MechanicalRecipeJsonProvider
        extends AbstractRecipeJsonProvider {
    private final Ingredient input;
    private final int inputCount;
    private final Item result;
    private final int outputCount;
    private final int processingTime;
    private final int requiredPower;

    public MechanicalRecipeJsonProvider(Ingredient input, int inputCount, Item output, int outputCount, int processingTime, int requiredPower, RecipeSerializer<? extends AbstractMechanicalRecipe> serializer) {
        super(serializer);
        this.input = input;
        this.inputCount = inputCount;
        this.result = output;
        this.outputCount = outputCount;
        this.processingTime = processingTime;
        this.requiredPower = requiredPower;
    }

    @Override
    public void serialize(JsonObject json) {
        json.add("ingredient", this.input.toJson());
        json.addProperty("inputcount", this.inputCount);
        json.addProperty("result", Registries.ITEM.getId(this.result).toString());
        json.addProperty("outputcount", this.outputCount);
        json.addProperty("processingtime", this.processingTime);
        json.addProperty("requiredpower", this.requiredPower);
    }
}
