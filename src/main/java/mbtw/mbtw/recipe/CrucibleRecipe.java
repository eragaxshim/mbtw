package mbtw.mbtw.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CrucibleRecipe implements Recipe<Inventory> {
    protected final Identifier id;
    protected final String group;
    protected final DefaultedList<Ingredient> input;
    protected final int inputCount;
    protected final ItemStack output;
    protected final int processingTime;
    protected final int requiredPower;

    public CrucibleRecipe(Identifier id, String group, DefaultedList<Ingredient> input, int inputCount, ItemStack output, int processingTime, int requiredPower) {
        this.id = id;
        this.group = group;
        this.input = input;
        this.inputCount = inputCount;
        this.output = output;
        this.processingTime = processingTime;
        this.requiredPower = requiredPower;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        ItemStack stack = inventory.getStack(0);
        return this.input.test(stack) && stack.getCount() >= this.inputCount;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(this.input);
        return defaultedList;
    }

    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    public int getRequiredPower() {
        return this.requiredPower;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return this.type;
    }
}
