package mbtw.mbtw.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mbtw.mbtw.Mbtw;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CrucibleRecipe implements PoweredRecipe {
    protected final Identifier id;
    protected final String group;
    protected final DefaultedList<Ingredient> input;
    protected final ItemStack output;
    protected final int smeltingTime;
    protected final int requiredTemperature;

    public CrucibleRecipe(Identifier id, String group, DefaultedList<Ingredient> input, ItemStack output, int smeltingTime, int requiredTemperature) {
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output;
        this.smeltingTime = smeltingTime;
        this.requiredTemperature = requiredTemperature;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        List<ItemStack> nonEmptyStacks = new ArrayList<>(inventory.size());

        // i = 0 is output
        for (int i = 1; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);

            if (!stack.isEmpty()) {
                nonEmptyStacks.add(stack);
            }
        }
        return ShapelessCountMatch.isMatch(nonEmptyStacks, input);
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= this.input.size();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.input;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.output;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    public int getProcessingTime() {
        return this.smeltingTime;
    }

    public int getRequiredPower() {
        return this.requiredTemperature;
    }

    @Override
    public void decrementInput(DefaultedList<ItemStack> inputStacks) {
        List<ItemStack> combinedStacks = ShapelessCountMatch.combineStacks(inputStacks, true);
        int[] match = ShapelessCountMatch.computeMatch(combinedStacks, input);
        if (match == null) {
            throw new IllegalStateException("Input or recipe changed during processing!");
        }

        // We go over all the ingredients
        for (int i = 0; i < match.length; i++) {
            int decrement = 1;
            CustomIngredient customIngredient;
            if ((customIngredient = input.get(i).getCustomIngredient()) != null && customIngredient instanceof CountIngredient countIngredient) {
                decrement = countIngredient.getCount();
            }
            // Match at index i contains the index of the combined stack that satisfies the ingredient i
            ItemStack stackTypeToDecrement = combinedStacks.get(match[i]);
            // Sort in ascending order all satisfying stacks that we could decrement
            List<ItemStack> stacksToDecrement = inputStacks.stream()
                    .filter(stack -> ItemStack.canCombine(stackTypeToDecrement, stack))
                    .sorted(Comparator.comparing(ItemStack::getCount)).toList();
            // We decrement, starting from low count stacks all stacks until we have exhausted the decrement
            for (ItemStack stackToDecrement : stacksToDecrement) {
                int count = stackToDecrement.getCount();
                if (count < decrement) {
                    stackToDecrement.setCount(0);
                    decrement = decrement - count;
                } else {
                    stackToDecrement.decrement(decrement);
                    break;
                }
            }
        }
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Mbtw.CRUCIBLE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return Mbtw.CRUCIBLE_SMELTING;
    }

    public static class Serializer implements RecipeSerializer<CrucibleRecipe> {
        public CrucibleRecipe read(Identifier identifier, JsonObject jsonObject) {
            String string = JsonHelper.getString(jsonObject, "group", "");
            DefaultedList<Ingredient> defaultedList = getIngredients(JsonHelper.getArray(jsonObject, "ingredients"));

            if (defaultedList.isEmpty()) {
                throw new JsonParseException("No ingredients for crucible recipe");
            } else if (defaultedList.size() > 16) {
                throw new JsonParseException("Too many ingredients for crucible recipe");
            } else {
                ItemStack outputStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
                int smeltingTime = JsonHelper.getInt(jsonObject, "processingtime", 200);
                int requiredTemperature = JsonHelper.getInt(jsonObject, "requiredtemperature", 1);
                return new CrucibleRecipe(identifier, string, defaultedList, outputStack, smeltingTime, requiredTemperature);
            }
        }

        private static DefaultedList<Ingredient> getIngredients(JsonArray json) {
            DefaultedList<Ingredient> defaultedList = DefaultedList.of();

            for(int i = 0; i < json.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(json.get(i));
                if (!ingredient.isEmpty()) {
                    defaultedList.add(ingredient);
                }
            }

            return defaultedList;
        }

        public CrucibleRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            String string = packetByteBuf.readString();
            int i = packetByteBuf.readVarInt();
            DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);
            for(int j = 0; j < defaultedList.size(); ++j) {
                defaultedList.set(j, Ingredient.fromPacket(packetByteBuf));
            }

            ItemStack outputStack = packetByteBuf.readItemStack();
            int smeltingTime = packetByteBuf.readVarInt();
            int requiredTemperature = packetByteBuf.readVarInt();
            return new CrucibleRecipe(identifier, string, defaultedList, outputStack, smeltingTime, requiredTemperature);
        }

        public void write(PacketByteBuf packetByteBuf, CrucibleRecipe recipe) {
            packetByteBuf.writeString(recipe.group);
            packetByteBuf.writeVarInt(recipe.input.size());
            for (Ingredient ingredient : recipe.input) {
                ingredient.write(packetByteBuf);
            }
            packetByteBuf.writeItemStack(recipe.output);
            packetByteBuf.writeVarInt(recipe.smeltingTime);
            packetByteBuf.writeVarInt(recipe.requiredTemperature);
        }
    }
}
