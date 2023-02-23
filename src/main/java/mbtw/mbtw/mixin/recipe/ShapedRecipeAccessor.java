package mbtw.mbtw.mixin.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(ShapedRecipe.class)
public interface ShapedRecipeAccessor {
    @Accessor("output")
    ItemStack accessOutput();

    @NotNull
    @Invoker("readSymbols")
    static Map<String, Ingredient> invokeReadSymbols(JsonObject json) {
        return null;
    }

    @NotNull
    @Invoker("removePadding")
    static String[] invokeRemovePadding(String... pattern) {
        return null;
    }

    @NotNull
    @Invoker("createPatternMatrix")
    static DefaultedList<Ingredient> invokeCreatePatternMatrix(String[] pattern, Map<String, Ingredient> symbols, int width, int height) {
        return null;
    }
}
