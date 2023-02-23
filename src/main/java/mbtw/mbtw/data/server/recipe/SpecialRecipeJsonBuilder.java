package mbtw.mbtw.data.server.recipe;

import com.google.gson.JsonObject;
import mbtw.mbtw.Mbtw;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SpecialRecipeJsonBuilder {
    final RecipeSerializer<?> serializer;

    public SpecialRecipeJsonBuilder(RecipeSerializer<?> serializer) {
        this.serializer = serializer;
    }

    public static SpecialRecipeJsonBuilder create(RecipeSerializer<? extends Recipe<?>> serializer) {
        return new SpecialRecipeJsonBuilder(serializer);
    }

    public Identifier offerTo(Consumer<RecipeJsonProvider> exporter, final String recipeId) {
        Identifier recipeIdentifier = new Identifier(Mbtw.MOD_ID, recipeId);
        exporter.accept(new SpecialRecipeJsonBuilder.SpecialRecipeJsonProvider() {
            public RecipeSerializer<?> getSerializer() {
                return SpecialRecipeJsonBuilder.this.serializer;
            }

            public Identifier getRecipeId() {
                return recipeIdentifier;
            }

            @Nullable
            public JsonObject toAdvancementJson() {
                return null;
            }

            public Identifier getAdvancementId() {
                return new Identifier("");
            }
        });
        return recipeIdentifier;
    }

    protected abstract static class SpecialRecipeJsonProvider implements RecipeJsonProvider {

        protected SpecialRecipeJsonProvider() {
        }

        public void serialize(JsonObject json) {

        }
    }
}
