package mbtw.mbtw.data.server.recipe;

import mbtw.mbtw.Mbtw;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.VanillaRecipeProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class RecipeJsonBuilder {
    Identifier ROOT = new Identifier("recipes/root");
    private final RecipeCategory category;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.create();
    private final AbstractRecipeJsonProvider recipeBuildProvider;
    private String group = "";
    private String method;

    public RecipeJsonBuilder(RecipeCategory category, AbstractRecipeJsonProvider recipeBuildProvider) {
        this.category = category;
        this.recipeBuildProvider = recipeBuildProvider;
    }

    public RecipeJsonBuilder inputCriterion(ItemConvertible input) {
        return this.criterion(RecipeProvider.hasItem(input), VanillaRecipeProvider.conditionsFromItem(input));
    }

    public RecipeJsonBuilder criterion(String name, CriterionConditions conditions) {
        this.advancementBuilder.criterion(name, conditions);
        return this;
    }

    public RecipeJsonBuilder group(String group) {
        this.group = group;
        return this;
    }

    public RecipeJsonBuilder method(String method) {
        this.method = method;
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, ItemConvertible outputItem, @Nullable ItemConvertible inputItem) {
        if (this.method == null) {
            Identifier serializerId = Registries.RECIPE_SERIALIZER.getId(recipeBuildProvider.getSerializer());
            if (serializerId == null) {
                throw new IllegalArgumentException("Serializer" + recipeBuildProvider.getSerializer().toString() + " must be registered or provide method to builder!");
            }
            this.method = "_from_" + serializerId.getPath();
        }

        String from;
        if (inputItem != null) {
            from =  "_" + RecipeProvider.getItemPath(inputItem);
        } else {
            from = "";
        }

        Identifier recipeId = new Identifier(Mbtw.MOD_ID, RecipeProvider.getItemPath(outputItem) + method + from);
        this.offerTo(exporter, recipeId);
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        this.validate(recipeId);
        // If, for example, the criterion having a certain item, it would be revoked after completing the advancement
        // Below adds having the recipe as also sufficing (since it's merged as OR), so you don't lose the recipe once unlocked
        this.advancementBuilder.parent(ROOT).criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriterionMerger.OR);
        recipeBuildProvider.setGroup(group);
        recipeBuildProvider.setCategory(category);
        recipeBuildProvider.setRecipeId(recipeId);
        recipeBuildProvider.setAdvancementId(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/"));
        recipeBuildProvider.setAdvancementBuilder(this.advancementBuilder);
        exporter.accept(recipeBuildProvider);
    }

    private void validate(Identifier recipeId) {
        Objects.requireNonNull(recipeId);
        if (this.advancementBuilder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
}
