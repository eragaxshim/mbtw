package mbtw.mbtw.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import mbtw.mbtw.mixin.recipe.ShapedRecipeAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

import java.util.Map;

public class SoulforgedRecipe extends ShapedRecipe {
    public SoulforgedRecipe(Identifier id, String group, CraftingRecipeCategory category, int width, int height, DefaultedList<Ingredient> input, ItemStack output, boolean showNotification) {
        super(id, group, category, width, height, input, output, showNotification);
    }

    public SoulforgedRecipe(Identifier id, String group, CraftingRecipeCategory category, int width, int height, DefaultedList<Ingredient> input, ItemStack output) {
        super(id, group, category, width, height, input, output);
    }

    /**
     * Modifies getPattern in ShapedRecipe to allow for 4 rows and columns
     */
    static String[] getPattern(JsonArray json) {
        String[] strings = new String[json.size()];
        if (strings.length > 4) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 4 is maximum");
        } else if (strings.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for(int i = 0; i < strings.length; ++i) {
                String string = JsonHelper.asString(json.get(i), "pattern[" + i + "]");
                if (string.length() > 4) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 4 is maximum");
                }

                if (i > 0 && strings[0].length() != string.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                strings[i] = string;
            }

            return strings;
        }
    }

    // Copied directly from Minecraft ShapedRecipe
    public static class Serializer implements RecipeSerializer<SoulforgedRecipe> {
        public SoulforgedRecipe read(Identifier identifier, JsonObject jsonObject) {
            String string = JsonHelper.getString(jsonObject, "group", "");
            CraftingRecipeCategory craftingRecipeCategory = CraftingRecipeCategory.CODEC.byId(JsonHelper.getString(jsonObject, "category", null), CraftingRecipeCategory.MISC);
            Map<String, Ingredient> map = ShapedRecipeAccessor.invokeReadSymbols(JsonHelper.getObject(jsonObject, "key"));
            String[] strings = ShapedRecipeAccessor.invokeRemovePadding(SoulforgedRecipe.getPattern(JsonHelper.getArray(jsonObject, "pattern")));
            int i = strings[0].length();
            int j = strings.length;
            DefaultedList<Ingredient> defaultedList = ShapedRecipeAccessor.invokeCreatePatternMatrix(strings, map, i, j);
            ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
            boolean bl = JsonHelper.getBoolean(jsonObject, "show_notification", true);
            return new SoulforgedRecipe(identifier, string, craftingRecipeCategory, i, j, defaultedList, itemStack, bl);
        }

        @Override
        public SoulforgedRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            int i = packetByteBuf.readVarInt();
            int j = packetByteBuf.readVarInt();
            String string = packetByteBuf.readString();
            CraftingRecipeCategory craftingRecipeCategory = packetByteBuf.readEnumConstant(CraftingRecipeCategory.class);
            DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i * j, Ingredient.EMPTY);

            defaultedList.replaceAll(ignored -> Ingredient.fromPacket(packetByteBuf));

            ItemStack itemStack = packetByteBuf.readItemStack();
            boolean bl = packetByteBuf.readBoolean();
            return new SoulforgedRecipe(identifier, string, craftingRecipeCategory, i, j, defaultedList, itemStack, bl);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, SoulforgedRecipe shapedRecipe) {
            packetByteBuf.writeVarInt(shapedRecipe.getWidth());
            packetByteBuf.writeVarInt(shapedRecipe.getHeight());
            packetByteBuf.writeString(shapedRecipe.getGroup());
            packetByteBuf.writeEnumConstant(shapedRecipe.getCategory());

            for (Ingredient ingredient : shapedRecipe.getIngredients()) {
                ingredient.write(packetByteBuf);
            }

            packetByteBuf.writeItemStack(((ShapedRecipeAccessor)shapedRecipe).accessOutput());
            packetByteBuf.writeBoolean(shapedRecipe.showNotification());
        }
    }
}
