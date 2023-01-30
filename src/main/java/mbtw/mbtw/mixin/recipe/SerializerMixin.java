package mbtw.mbtw.mixin.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import mbtw.mbtw.recipe.RecipeMixinAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
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


@Mixin(ShapelessRecipe.Serializer.class)
public class SerializerMixin {
    @Inject(method = "read(Lnet/minecraft/util/Identifier;Lcom/google/gson/JsonObject;)Lnet/minecraft/recipe/ShapelessRecipe;", at = @At("RETURN"), cancellable = true)
    protected void read(Identifier identifier, JsonObject jsonObject, CallbackInfoReturnable<ShapelessRecipe> cir)
    {
        ShapelessRecipe shapelessRecipe = cir.getReturnValue();
        DefaultedList<ItemStack> defaultedList;
        try {
            defaultedList = getDropResults(JsonHelper.getArray(jsonObject, "dropResults"));
        }
        catch (JsonSyntaxException exception) {
            defaultedList = DefaultedList.of();
        }

        ((RecipeMixinAccess) shapelessRecipe).setDropOutput(defaultedList);

        cir.setReturnValue(shapelessRecipe);
    }

    @Inject(method = "read(Lnet/minecraft/util/Identifier;Lnet/minecraft/network/PacketByteBuf;)Lnet/minecraft/recipe/ShapelessRecipe;", at = @At("RETURN"), cancellable = true)
    protected void readBuf(Identifier identifier, PacketByteBuf packetByteBuf, CallbackInfoReturnable<ShapelessRecipe> cir)
    {
        ShapelessRecipe shapelessRecipe = cir.getReturnValue();
        int k = packetByteBuf.readVarInt();
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(k, ItemStack.EMPTY);

        defaultedList.replaceAll(ignored -> packetByteBuf.readItemStack());
        ((RecipeMixinAccess)shapelessRecipe).setDropOutput(defaultedList);
        cir.setReturnValue(shapelessRecipe);
    }

    @Inject(method = "write(Lnet/minecraft/network/PacketByteBuf;Lnet/minecraft/recipe/ShapelessRecipe;)V", at = @At("TAIL"))
    protected void writeBuf(PacketByteBuf packetByteBuf, ShapelessRecipe shapelessRecipe, CallbackInfo ci)
    {
        DefaultedList<ItemStack> dropOutput = ((RecipeMixinAccess)shapelessRecipe).getDropOutput();
        packetByteBuf.writeVarInt(dropOutput.size());
        for (ItemStack itemStack : dropOutput) {
            packetByteBuf.writeItemStack(itemStack);
        }
    }

    private static DefaultedList<ItemStack> getDropResults(JsonArray json) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();

        for(int i = 0; i < json.size(); ++i) {
            ItemStack itemStack = ShapedRecipe.outputFromJson((JsonObject) json.get(i));
            if (!itemStack.isEmpty()) {
                defaultedList.add(itemStack);
            }
        }

        return defaultedList;
    }
}