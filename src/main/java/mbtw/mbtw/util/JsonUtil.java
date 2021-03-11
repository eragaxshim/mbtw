package mbtw.mbtw.util;

import com.google.gson.*;

import java.util.Iterator;
import java.util.Map;

public class JsonUtil {
    public static JsonObject deepCopy(JsonObject originalJson) {
        JsonObject result = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : originalJson.entrySet()) {
            result.add(entry.getKey(), deepCopy(entry.getValue()));
        }
        return result;
    }

    public static JsonElement deepCopy(JsonElement element)
    {
        if (element instanceof JsonNull)
        {
            return JsonNull.INSTANCE;
        }
        else if (element instanceof JsonObject) {
            return deepCopy((JsonObject) element);
        }
        else if (element instanceof JsonArray)
        {
            return deepCopy((JsonArray) element);
        }
        else if (element instanceof JsonPrimitive) {
            return element;
        }
        else {
            throw new ClassCastException();
        }
    }

    public static JsonArray deepCopy(JsonArray array) {
        JsonArray result = new JsonArray();
        for (JsonElement element : array) {
            result.add(deepCopy(element));
        }
        return result;
    }
}
