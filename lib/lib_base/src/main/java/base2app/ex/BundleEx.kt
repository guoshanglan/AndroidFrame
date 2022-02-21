package base2app.ex

import android.os.Bundle
import base2app.util.JsonUtil
import com.google.gson.reflect.TypeToken

/**
 * date : 2020/11/2
 * dest : BundleEx 旨在协助 Scheme 路由协议的扩展中 ,能更方便的获取基础数据类型
 * Scheme 由于读取都只能是String类型的数据 , 而 bundle 的存储除了有基础数据类型还有有复杂数据的构造
 * 那么在使用比较常见的基础数据类型 : float int double long Short Boolean 时候 , 能够快速的转换处理
 *
 * 非compareable 类型则直接使用 json 进行序列化
 *
 */
fun Bundle.safeInt(key: String): Int? {
    return safe(key)
}

fun Bundle.safeInt(key: String, def: Int): Int {
    return safe(key) ?: def
}

fun Bundle.safeDouble(key: String): Double? {
    return safe(key)
}

fun Bundle.safeDouble(key: String, def: Double): Double {
    return safe(key) ?: def
}

fun Bundle.safeFloat(key: String): Float? {
    return safe(key)
}

fun Bundle.safeFloat(key: String, def: Float): Float {
    return safe(key) ?: def
}

fun Bundle.safeLong(key: String, def: Long? = null): Long? {
    return safe(key) ?: def
}

fun Bundle.safeShort(key: String): Short? {
    return safe(key)
}

fun Bundle.safeShort(key: String, def: Short): Short {
    return safe(key) ?: def
}

fun Bundle.safeBoolean(key: String): Boolean? {
    return safe(key)
}

fun Bundle.safeBoolean(key: String, def: Boolean): Boolean {
    return safe(key) ?: def
}

fun Bundle.safeString(key: String): String? {
    return safe(key)
}

fun Bundle.safeString(key: String, def: String): String {
    return safe(key) ?: def
}

inline fun <reified T : Any> Bundle.safe(key: String): T? {
    val any = get(key) ?: return null
    if (any is T) {
        return any
    }
    return any.toString().takeIf { it.isNotEmpty() }?.let {
        val type = object : TypeToken<T>() {}.type
        JsonUtil.fromJson(it, type)
    }
}

fun Bundle.put(key: String, any: Any) {
    when (any) {
        is String -> putString(key, any)
        is Float -> putFloat(key, any)
        is Int -> putInt(key, any)
        is Boolean -> putBoolean(key, any)
        is Long -> putLong(key, any)
        is Double -> putDouble(key, any)
        is Short -> putShort(key, any)
        else -> putString(key, any.gson())
    }
}

