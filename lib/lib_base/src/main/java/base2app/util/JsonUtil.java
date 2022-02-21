package base2app.util;

import android.content.res.AssetManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import base2app.BaseApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static base2app.ex.LogExKt.logd;

/**
 * Json解析器
 */
public class JsonUtil {
    private JsonUtil() {
    }

    /**
     * 构造Gson
     * 注意：禁止使用excludeFieldsWithoutExposeAnnotation()，否则将于addSerializationExclusionStrategy()、addDeserializationExclusionStrategy()有冲突
     */
    private static final Gson mGson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues()
            // 添加序列化过滤条件，既object对象转json字符串
            .addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    Expose expose = f.getAnnotation(Expose.class);
                    if (expose == null) {
                        return false;
                    }
                    boolean ignore = !expose.serialize();
                    if (ignore) {
                        logd("JsonUtil", "字段'" + f.getDeclaringClass().getSimpleName() + "." + f.getName() + "'序列化被忽略");
                    }
                    return ignore;
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    // 此处可以通过类名、类的包名等信息来进行过滤
                    return false;
                }
            })
            // 添加反序列化过滤条件，既json字符串转object对象
            .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    Expose expose = f.getAnnotation(Expose.class);
                    if (expose == null) {
                        return false;
                    }
                    boolean ignore = !expose.deserialize();
                    if (ignore) {
                        logd("JsonUtil", "字段'" + f.getDeclaringClass().getSimpleName() + "." + f.getName() + "'反序列化被忽略");
                    }
                    return ignore;
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    // 此处可以通过类名、类的包名等信息来进行过滤
                    return false;
                }
            })
            .create();

    public static String toJson(Object model) {
        if (model == null) {
            return null;
        }
        return mGson.toJson(model);
    }

    /**
     * 根据json key排序
     *
     * @param json
     * @return
     */
    public static String sortJson(String json) {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        JsonParser p = new JsonParser();
        JsonElement e = p.parse(json);
        sort(e);
        return g.toJson(e);
    }

    /**
     * 排序
     *
     * @param e
     */
    public static void sort(JsonElement e) {
        if (e.isJsonNull() || e.isJsonPrimitive()) {
            return;
        }

        if (e.isJsonArray()) {
            JsonArray a = e.getAsJsonArray();
            for (JsonElement jsonElement : a) {
                sort(jsonElement);
            }
            return;
        }

        if (e.isJsonObject()) {
            Map<String, JsonElement> tm = new TreeMap<>(getComparator());
            for (Map.Entry<String, JsonElement> en : e.getAsJsonObject().entrySet()) {
                tm.put(en.getKey(), en.getValue());
            }

            String key;
            JsonElement val;
            for (Map.Entry<String, JsonElement> en : tm.entrySet()) {
                key = en.getKey();
                val = en.getValue();
                e.getAsJsonObject().remove(key);
                e.getAsJsonObject().add(key, val);
                sort(val);
            }
        }
    }

    /**
     * 定义比较规则
     *
     * @return
     */
    private static Comparator<String> getComparator() {
        return String::compareTo;
    }

    public static JSONObject toJSONObject(@NonNull String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, Class<T> t) {
        if (TextUtils.isEmpty(json)) return null;
        try {
            return mGson.fromJson(json, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(@NonNull String json, Type typeOfT) {
        if (TextUtils.isEmpty(json)) return null;

        try {
            return mGson.fromJson(json, typeOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromAssetJson(String fileName, Class<T> t) {
        String json = getJson(fileName);
        if (TextUtils.isEmpty(json)) return null;
        try {
            return mGson.fromJson(json, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static <T> T fromAssetJson(@NonNull String fileName, Type typeOfT) {
        String json = getJson(fileName);
        if (TextUtils.isEmpty(json)) return null;

        try {
            return mGson.fromJson(json, typeOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取 Asset json文件
     */
    public static String getJson(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = Objects.requireNonNull(BaseApplication.Companion.getContext()).getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    /**
     * @date 2017/8/24
     * @description 将字符串格式化成JSON的格式
     */
    public static String jsonFormat(String strJson) {
        // 计数tab的个数
        int tabNum = 0;
        StringBuffer jsonFormat = new StringBuffer();
        int length = strJson.length();

        char last = 0;
        for (int i = 0; i < length; i++) {
            char c = strJson.charAt(i);
            if (c == '{') {
                tabNum++;
                jsonFormat.append(c + "\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
            } else if (c == '}') {
                tabNum--;
                jsonFormat.append("\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
                jsonFormat.append(c);
            } else if (c == ',') {
                jsonFormat.append(c + "\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
            } else if (c == ':') {
                jsonFormat.append(c + " ");
            } else if (c == '[') {
                tabNum++;
                char next = strJson.charAt(i + 1);
                if (next == ']') {
                    jsonFormat.append(c);
                } else {
                    jsonFormat.append(c + "\n");
                    jsonFormat.append(getSpaceOrTab(tabNum));
                }
            } else if (c == ']') {
                tabNum--;
                if (last == '[') {
                    jsonFormat.append(c);
                } else {
                    jsonFormat.append("\n" + getSpaceOrTab(tabNum) + c);
                }
            } else {
                jsonFormat.append(c);
            }
            last = c;
        }
        return jsonFormat.toString();
    }

    private static String getSpaceOrTab(int tabNum) {
        StringBuffer sbTab = new StringBuffer();
        for (int i = 0; i < tabNum; i++) {
            sbTab.append('\t');
        }
        return sbTab.toString();
    }

}