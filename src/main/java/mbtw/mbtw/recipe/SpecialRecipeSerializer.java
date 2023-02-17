package mbtw.mbtw.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

/**
 * Exactly like SpecialRecipeSerializer, but extends some other recipe
 */
public class SpecialRecipeSerializer<C extends Inventory, T extends Recipe<C>> implements RecipeSerializer<T> {
    private final Factory<C, T> factory;

    public SpecialRecipeSerializer(Factory<C, T> factory) {
        this.factory = factory;
    }
    @Override
    public T read(Identifier id, JsonObject json) {
        return this.factory.create(id);
    }

    @Override
    public T read(Identifier id, PacketByteBuf buf) {
        return this.factory.create(id);
    }

    @Override
    public void write(PacketByteBuf buf, T recipe) {

    }

    @FunctionalInterface
    public interface Factory<C extends Inventory, T extends Recipe<C>> {
        T create(Identifier id);
    }
}
