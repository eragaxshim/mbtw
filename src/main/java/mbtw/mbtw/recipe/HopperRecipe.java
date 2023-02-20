package mbtw.mbtw.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mbtw.mbtw.Mbtw;
import mbtw.mbtw.inventory.BlockStateInventory;
import mbtw.mbtw.inventory.FilterInventory;
import net.minecraft.block.Block;
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

public class HopperRecipe implements Recipe<FilterInventory> {
    protected final Identifier id;
    protected final String group;
    protected final Ingredient input;
    protected final Block filter;
    protected final int inputCount;
    protected final ItemStack output;
    protected final Identifier conversionRecipeId;

    public HopperRecipe(Identifier id, String group, Ingredient input, Block filter, int inputCount, ItemStack output, @Nullable Identifier conversionRecipeId) {
        this.id = id;
        this.group = group;
        this.input = input;
        this.filter = filter;
        this.inputCount = inputCount;
        this.output = output;
        this.conversionRecipeId = conversionRecipeId;
    }

    @Override
    public boolean matches(FilterInventory inventory, World world) {
        return inventory.getFilter() == filter && input.test(inventory.inFilter()) && inventory.inFilter().getCount() >= inputCount;
    }

    @Override
    public ItemStack craft(FilterInventory inventory, DynamicRegistryManager registryManager) {
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
            String filterString = JsonHelper.getString(jsonObject, "filter");
            Identifier filterBlockId = new Identifier(filterString);
            Block filterBlock = Registries.BLOCK.getOrEmpty(filterBlockId).orElseThrow(() -> new IllegalStateException("Block: " + filterString + " does not exist"));

            String resultString = JsonHelper.getString(jsonObject, "result");
            Identifier resultId = new Identifier(resultString);
            ItemStack outputStack = new ItemStack(Registries.ITEM.getOrEmpty(resultId).orElseThrow(() -> new IllegalStateException("Item: " + resultString + " does not exist")));
            return new HopperRecipe(identifier, group, ingredient, filterBlock, inputCount, outputStack, conversionId);
        }

        public HopperRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            String group = packetByteBuf.readString();
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            Block filter = packetByteBuf.readRegistryValue(Registries.BLOCK);
            int inputCount = packetByteBuf.readVarInt();
            ItemStack outputStack = packetByteBuf.readItemStack();
            Identifier conversionId = packetByteBuf.readIdentifier();
            return new HopperRecipe(identifier, group, ingredient, filter, inputCount, outputStack, conversionId);
        }

        public void write(PacketByteBuf packetByteBuf, HopperRecipe recipe) {
            packetByteBuf.writeString(recipe.group);
            recipe.input.write(packetByteBuf);
            packetByteBuf.writeRegistryValue(Registries.BLOCK, recipe.filter);
            packetByteBuf.writeVarInt(recipe.inputCount);
            packetByteBuf.writeItemStack(recipe.output);
            packetByteBuf.writeIdentifier(recipe.conversionRecipeId);
        }
    }
}
