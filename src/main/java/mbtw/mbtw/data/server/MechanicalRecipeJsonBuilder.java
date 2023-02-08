package mbtw.mbtw.data.server;

import com.google.gson.JsonObject;
import mbtw.mbtw.recipe.AbstractMechanicalRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MechanicalRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
    private final RecipeCategory category;
    private final Ingredient input;
    private final int inputCount;
    private final Item output;
    private final int outputCount;
    private final int processingTime;
    private final int requiredPower;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.create();
    @Nullable
    private String group;
    private final RecipeSerializer<? extends AbstractMechanicalRecipe> serializer;

    public MechanicalRecipeJsonBuilder(RecipeCategory category, Ingredient input, int inputCount, ItemConvertible output, int outputCount, int processingTime, int requiredPower, RecipeSerializer<? extends AbstractMechanicalRecipe> serializer) {
        this.category = category;
        this.output = output.asItem();
        this.outputCount = outputCount;
        this.input = input;
        this.inputCount = inputCount;
        this.processingTime = processingTime;
        this.requiredPower = requiredPower;
        this.serializer = serializer;
    }

    @Override
    public CraftingRecipeJsonBuilder criterion(String name, CriterionConditions conditions) {
        this.advancementBuilder.criterion(name, conditions);
        return this;
    }

    @Override
    public CraftingRecipeJsonBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.output;
    }

    @Override
    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        this.validate(recipeId);
        this.advancementBuilder.parent(ROOT).criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriterionMerger.OR);
        exporter.accept(new MechanicalRecipeJsonBuilder.MechanicalRecipeJsonProvider(recipeId, this.group == null ? "" : this.group, this.input, this.inputCount, this.output, this.outputCount, this.processingTime, this.requiredPower, this.advancementBuilder, recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/"), this.serializer));
    }

    private void validate(Identifier recipeId) {
        if (this.advancementBuilder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }

    static class MechanicalRecipeJsonProvider
            implements RecipeJsonProvider {
        private final Identifier recipeId;
        private final String group;
        private final Ingredient input;
        private final int inputCount;
        private final Item result;
        private final int outputCount;
        private final int processingTime;
        private final int requiredPower;
        private final Advancement.Builder advancementBuilder;
        private final Identifier advancementId;
        private final RecipeSerializer<? extends AbstractMechanicalRecipe> serializer;

        public MechanicalRecipeJsonProvider(Identifier recipeId, String group, Ingredient input, int inputCount, Item output, int outputCount, int processingTime, int requiredPower, Advancement.Builder advancementBuilder, Identifier advancementId, RecipeSerializer<? extends AbstractMechanicalRecipe> serializer) {
            this.recipeId = recipeId;
            this.group = group;
            this.input = input;
            this.inputCount = inputCount;
            this.result = output;
            this.outputCount = outputCount;
            this.processingTime = processingTime;
            this.requiredPower = requiredPower;
            this.advancementBuilder = advancementBuilder;
            this.advancementId = advancementId;
            this.serializer = serializer;
        }

        @Override
        public void serialize(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            json.add("ingredient", this.input.toJson());
            json.addProperty("inputcount", this.inputCount);
            json.addProperty("result", Registries.ITEM.getId(this.result).toString());
            json.addProperty("outputcount", this.outputCount);
            json.addProperty("processingtime", this.processingTime);
            json.addProperty("requiredpower", this.requiredPower);
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return this.serializer;
        }

        @Override
        public Identifier getRecipeId() {
            return this.recipeId;
        }

        @Override
        @Nullable
        public JsonObject toAdvancementJson() {
            return this.advancementBuilder.toJson();
        }

        @Override
        @Nullable
        public Identifier getAdvancementId() {
            return this.advancementId;
        }
    }
}
