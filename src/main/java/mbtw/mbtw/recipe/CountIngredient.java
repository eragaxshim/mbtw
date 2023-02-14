package mbtw.mbtw.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import mbtw.mbtw.Mbtw;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.NbtIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CountIngredient implements CustomIngredient {
    public static final CustomIngredientSerializer<CountIngredient> SERIALIZER = new Serializer();

    private final Ingredient base;
    private final int count;

    public CountIngredient(Ingredient base, int count) {
        this.base = base;
        this.count = count;
    }

    public CountIngredient(Ingredient base) {
        this(base, 1);
    }

    @Override
    public boolean test(ItemStack stack) {
        if (!base.test(stack)) return false;

        return stack.getCount() >= count;
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        return Arrays.stream(base.getMatchingStacks()).map(stack -> stack.copyWithCount(count)).toList();
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public int getCount() {
        return count;
    }

    private static class Serializer implements CustomIngredientSerializer<CountIngredient> {
        private final Identifier id = new Identifier(Mbtw.MOD_ID, "count");

        @Override
        public Identifier getIdentifier() {
            return id;
        }

        @Override
        public CountIngredient read(JsonObject json) {
            Ingredient base = Ingredient.fromJson(json.get("base"));
            int count = JsonHelper.getInt(json, "count", 1);
            return new CountIngredient(base, count);
        }

        @Override
        public void write(JsonObject json, CountIngredient ingredient) {
            json.add("base", ingredient.base.toJson());
            json.addProperty("count", ingredient.count);
        }

        @Override
        public CountIngredient read(PacketByteBuf buf) {
            Ingredient base = Ingredient.fromPacket(buf);
            int count = buf.readVarInt();
            return new CountIngredient(base, count);
        }

        @Override
        public void write(PacketByteBuf buf, CountIngredient ingredient) {
            ingredient.base.write(buf);
            buf.writeVarInt(ingredient.count);
        }
    }
}
