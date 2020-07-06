package app.karacal.helpers;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import app.karacal.App;

public class JsonHelper {

    public static JsonObject getJsonObject(String string){
        JsonParser jsonParser = new JsonParser();
        return (JsonObject) jsonParser.parse(string);
    }

    public static String extractStrings(String string){
        Log.v(App.TAG, "extractStrings(String string) = "+string);
        try {
            JsonObject jsonObject = getJsonObject(string);
            return extractStrings(jsonObject);
        } catch (Exception e) {
            return null;
        }
    }

    public static String extractStrings(JsonObject jsonObject){
        Log.v(App.TAG, "extractStrings(JsonObject jsonObject)");

        try {
            List<String> strings = new LinkedList<>();
            for (String key : jsonObject.keySet()) {
                Log.v(App.TAG, "String key = "+key);

                JsonElement jsonElement = jsonObject.get(key);
                if (jsonElement.isJsonArray()) {
                    Log.v(App.TAG, "jsonElement.isJsonArray()");

                    try {
                        strings.add(jsonElement.getAsString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(jsonElement.isJsonObject() && key.equalsIgnoreCase("error_message")) {
                    return extractStrings((JsonObject)jsonElement);
                }
            }
            return TextUtils.join("\n", strings);
        } catch (Exception e) {
            return null;
        }
    }

}
