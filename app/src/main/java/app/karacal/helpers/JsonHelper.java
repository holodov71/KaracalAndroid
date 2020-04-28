package app.karacal.helpers;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JsonHelper {

    public static JsonObject getJsonObject(String string){
        JsonParser jsonParser = new JsonParser();
        return (JsonObject) jsonParser.parse(string);
    }

    public static String extractStrings(String string){
        try {
            JsonObject jsonObject = getJsonObject(string);
            List<String> strings = new LinkedList<>();
            for (String key : jsonObject.keySet()) {
                JsonElement jsonElement = jsonObject.get(key);
                if (jsonElement.isJsonArray()) {
                    try {
                        strings.add(jsonElement.getAsString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return TextUtils.join("\n", strings);
        } catch (Exception e) {
            return null;
        }
    }

}
