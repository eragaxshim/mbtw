package mbtw.mbtw.mixin.recipe;

import mbtw.mbtw.recipe.RecipeMixinAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShapelessRecipe.class)
public abstract class ShapelessRecipeMixin implements RecipeMixinAccess {
    private DefaultedList<ItemStack> dropOutput;

    public void setDropOutput(DefaultedList<ItemStack> dropOutput)
    {
        this.dropOutput = dropOutput;
    }

    public DefaultedList<ItemStack> getDropOutput()
    {
        return this.dropOutput;
    }

}


