package mbtw.mbtw.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class MechanicalRecipeSerializer<T extends AbstractMechanicalRecipe> implements RecipeSerializer<T> {
    private final int defaultProcessingTime;
    private final int defaultRequiredPower;
    private final RecipeFactory<T> recipeFactory;

    public MechanicalRecipeSerializer(RecipeFactory<T> recipeFactory, int defaultProcessingTime, int defaultRequiredPower) {
        this.defaultProcessingTime = defaultProcessingTime;
        this.defaultRequiredPower = defaultRequiredPower;
        this.recipeFactory = recipeFactory;
    }

    @Override
    public T read(Identifier id, JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "group", "");
        JsonElement jsonElement = JsonHelper.hasArray(jsonObject, "ingredient") ? JsonHelper.getArray(jsonObject, "ingredient") : JsonHelper.getObject(jsonObject, "ingredient");
        int inputCount = JsonHelper.getInt(jsonObject, "inputcount", 1);
        Ingredient ingredient = Ingredient.fromJson(jsonElement);
        int outputCount = JsonHelper.getInt(jsonObject, "outputcount", 1);
        String resultString = JsonHelper.getString(jsonObject, "result");
        Identifier resultId = new Identifier(resultString);
        ItemStack outputStack = new ItemStack(Registries.ITEM.getOrEmpty(resultId).orElseThrow(() -> new IllegalStateException("Item: " + resultString + " does not exist")), outputCount);
        int processingTime = JsonHelper.getInt(jsonObject, "processingtime", this.defaultProcessingTime);
        int requiredPower = JsonHelper.getInt(jsonObject, "requiredpower", this.defaultRequiredPower);
        return this.recipeFactory.create(id, string, ingredient, inputCount, outputStack, processingTime, requiredPower);
    }

    @Override
    public T read(Identifier id, PacketByteBuf packetByteBuf) {
        String group = packetByteBuf.readString();
        Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
        int inputCount = packetByteBuf.readVarInt();
        ItemStack itemStack = packetByteBuf.readItemStack();
        int processingTime = packetByteBuf.readVarInt();
        int requiredPower = packetByteBuf.readVarInt();
        return this.recipeFactory.create(id, group, ingredient, inputCount, itemStack, processingTime, requiredPower);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf, T recipe) {
        packetByteBuf.writeString(recipe.group);
        recipe.input.write(packetByteBuf);
        packetByteBuf.writeVarInt(recipe.inputCount);
        packetByteBuf.writeItemStack(recipe.output);
        packetByteBuf.writeVarInt(recipe.processingTime);
        packetByteBuf.writeVarInt(recipe.requiredPower);
    }

    public interface RecipeFactory<T extends AbstractMechanicalRecipe> {
        T create(Identifier id, String group, Ingredient input, int inputCount, ItemStack output, int processingTime, int requiredPower);
    }
}
