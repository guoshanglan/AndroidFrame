package base2app.infra;

import android.text.TextUtils;

import base2app.util.JsonUtil;


/**
 * 存储控制器
 */
public class StorageInfra {

    private StorageInfra() {
    }

    /***
     * schema为库名，一个App可以有多个库，建议命名：{appname}.{name}，比如navigation.local，free.share
     * @param schema schema
     * @param key key
     * @param value value
     * @param <T> value
     * @return true
     */
    public static <T> boolean put(final String schema, final String key, final T value) {
        if (value == null) {
            return false;
        }
        String jsonValue = JsonUtil.toJson(value);
        MMKVManager.Companion.getInstance().putString(schema, key, jsonValue);
        return true;
    }

    public static <T> boolean put(T value) {
        return value != null && put(null, value.getClass().getName(), value);
    }

    public static <T> boolean put(String schema, T value) {
        return value != null && put(schema, value.getClass().getName(), value);
    }

    public static <T> T get(Class<T> clz) {
        return get(null, clz);
    }

    public static <T> T get(String schema, Class<T> clz) {
        String key = clz.getName();
        String jsonValue = MMKVManager.Companion.getInstance().getString(schema, key, "");
        if (TextUtils.isEmpty(jsonValue)) {
            return null;
        }
        return JsonUtil.fromJson(jsonValue, clz);
    }

    public static <T> T get(String schema, String key, Class<T> clz) {
        String jsonValue = MMKVManager.Companion.getInstance().getString(schema, key, "");
        if (TextUtils.isEmpty(jsonValue)) {
            return null;
        }
        return JsonUtil.fromJson(jsonValue, clz);
    }

    public static boolean remove(final String schema, final String key) {
        MMKVManager.Companion.getInstance().remove(schema, key);
        return true;
    }

    public static boolean remove(Class clz) {
        return remove(null, clz.getName());
    }
}
