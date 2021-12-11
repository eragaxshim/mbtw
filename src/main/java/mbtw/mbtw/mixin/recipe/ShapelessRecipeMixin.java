package mbtw.mbtw.mixin.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import mbtw.mbtw.recipe.RecipeMixinAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Iterator;

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


