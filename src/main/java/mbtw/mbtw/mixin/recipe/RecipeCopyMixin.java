package mbtw.mbtw.mixin.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mbtw.mbtw.tag.MbtwTagsMaps;
import mbtw.mbtw.util.JsonUtil;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeCopyMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At(value = "HEAD"))
    protected void copyRecipes(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci)
    {
        Map<Identifier, JsonElement> newMap = new HashMap<>();

        for (Identifier id : map.keySet()) {
            if (id.getNamespace().equals("minecraft"))
            {
                String path = id.getPath();
                Pair<Identifier, Float> pair = MbtwTagsMaps.RECIPE_COPY_MAP.get(path);
                if (pair != null)
                {
                    JsonObject recipeJson = JsonUtil.deepCopy(map.get(id).getAsJsonObject());
                    recipeJson.addProperty("type", pair.getLeft().toString());
                    if (recipeJson.has("cookingtime"))
                    {
                        recipeJson.addProperty("cookingtime", (int) (recipeJson.get("cookingtime").getAsInt() * pair.getRight()));
                    }
                    newMap.put(new Identifier("mbtw", (path + "_" + pair.getLeft().getPath())), recipeJson);
                }
            }
        }

        map.putAll(newMap);
    }
}
