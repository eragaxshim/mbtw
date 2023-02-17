package mbtw.mbtw.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mbtw.mbtw.Mbtw;
import mbtw.mbtw.inventory.BlockStateInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class HopperRecipe implements Recipe<BlockStateInventory> {
    protected final Identifier id;
    protected final String group;
    protected final Ingredient input;
    protected final int inputCount;
    protected final ItemStack output;
    protected final Identifier conversionRecipeId;
    protected final boolean explodes;

    public HopperRecipe(Identifier id, String group, Ingredient input, int inputCount, ItemStack output, @Nullable Identifier conversionRecipeId, boolean explodes) {
        this.id = id;
        this.group = group;
        this.input = input;
        this.inputCount = inputCount;
        this.output = output;
        this.conversionRecipeId = conversionRecipeId;
        this.explodes = explodes;
    }

    @Override
    public boolean matches(BlockStateInventory inventory, World world) {
        if (!inventory.containsAny(input) || inventory.getStack(0).getCount() < inputCount) {
            return false;
        }

        return conversionRecipe(world).matches(inventory, world);
    }

    @Override
    public ItemStack craft(BlockStateInventory inventory, DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output;
    }

    public int timesCraftable(int inputStackCount) {
        return inputStackCount / inputCount;
    }

    public void decrementInput(ItemStack input, int timesCrafted) {
        input.decrement(inputCount*timesCrafted);
    }

    public boolean doesExplode() {
        return explodes;
    }

    public HopperBlockConversionRecipe conversionRecipe(World world) {
        Optional<? extends Recipe<?>> recipe = world.getRecipeManager().get(conversionRecipeId);
        if (recipe.isPresent() && recipe.get() instanceof HopperBlockConversionRecipe conversionRecipe) {
            return conversionRecipe;
        } else {
            throw new IllegalStateException("Invalid conversionRecipeId for recipe " + id);
        }
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Mbtw.HOPPER_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return Mbtw.HOPPER_FILTERING;
    }

    public static class Serializer implements RecipeSerializer<HopperRecipe> {
        public HopperRecipe read(Identifier identifier, JsonObject jsonObject) {
            String group = JsonHelper.getString(jsonObject, "group", "");
            JsonElement jsonElement = JsonHelper.hasArray(jsonObject, "ingredient") ? JsonHelper.getArray(jsonObject, "ingredient") : JsonHelper.getObject(jsonObject, "ingredient");
            int inputCount = JsonHelper.getInt(jsonObject, "inputcount", 1);
            Ingredient ingredient = Ingredient.fromJson(jsonElement);

            Identifier conversionId;
            if (jsonObject.has("conversion")) {
                String conversionString = JsonHelper.getString(jsonObject, "conversion");
                conversionId = new Identifier(conversionString);
            } else {
                conversionId = null;
            }

            String resultString = JsonHelper.getString(jsonObject, "result");
            Identifier resultId = new Identifier(resultString);
            ItemStack outputStack = new ItemStack(Registries.ITEM.getOrEmpty(resultId).orElseThrow(() -> new IllegalStateException("Item: " + resultString + " does not exist")));
            boolean explodes;
            if (jsonObject.has("explodes")) {
                explodes =  JsonHelper.getBoolean(jsonObject, "explodes");
            } else {
                explodes = false;
            }
            return new HopperRecipe(identifier, group, ingredient, inputCount, outputStack, conversionId, explodes);
        }

        public HopperRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            String group = packetByteBuf.readString();
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            int inputCount = packetByteBuf.readVarInt();
            ItemStack outputStack = packetByteBuf.readItemStack();
            boolean explodes = packetByteBuf.readBoolean();
            Identifier conversionId = packetByteBuf.readIdentifier();
            return new HopperRecipe(identifier, group, ingredient, inputCount, outputStack, conversionId, explodes);
        }

        public void write(PacketByteBuf packetByteBuf, HopperRecipe recipe) {
            packetByteBuf.writeString(recipe.group);
            recipe.input.write(packetByteBuf);
            packetByteBuf.writeVarInt(recipe.inputCount);
            packetByteBuf.writeItemStack(recipe.output);
            packetByteBuf.writeIdentifier(recipe.conversionRecipeId);
            packetByteBuf.writeBoolean(recipe.explodes);
        }
    }
}
