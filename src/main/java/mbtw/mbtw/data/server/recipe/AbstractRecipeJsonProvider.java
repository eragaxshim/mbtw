package mbtw.mbtw.data.server.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractRecipeJsonProvider
        implements RecipeJsonProvider {

    private Identifier recipeId;
    private String group;
    private String subCategoryString;
    private Advancement.Builder advancementBuilder;
    private Identifier advancementId;
    private final RecipeSerializer<? extends Recipe<?>> serializer;

    public AbstractRecipeJsonProvider(RecipeSerializer<? extends Recipe<?>> serializer) {
        this.serializer = serializer;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        Identifier serializerId = Registries.RECIPE_SERIALIZER.getId(this.getSerializer());
        if (serializerId == null) {
            throw new IllegalArgumentException("Serializer" + this.getSerializer().toString() + " must be registered or provide method to builder!");
        }
        jsonObject.addProperty("type", serializerId.toString());
        if (!this.group.isEmpty()) {
            jsonObject.addProperty("group", this.group);
        }
        if (!this.subCategoryString.isEmpty()) {
            jsonObject.addProperty("category", this.subCategoryString);
        }
        this.serialize(jsonObject);
        return jsonObject;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Override this for custom subcategories.
     */
    public void setCategory(RecipeCategory category) {
        this.subCategoryString = "";
    }

    public void setRecipeId(Identifier recipeId) {
        this.recipeId = recipeId;
    }

    public void setAdvancementId(Identifier advancementId) {
        this.advancementId = advancementId;
    }

    public void setAdvancementBuilder(Advancement.Builder advancementBuilder) {
        this.advancementBuilder = advancementBuilder;
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
