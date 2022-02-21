package base2app.infra

import android.content.Context
import android.os.Build
import android.text.TextUtils
import com.getkeepsafe.relinker.ReLinker
import com.tencent.mmkv.MMKV

/**
 * @date 2021/2/25 14:29
 * @desc MMKV控制器
 */
class MMKVManager private constructor() {

    companion object {
        private var instance: MMKVManager? = null
        fun getInstance(): MMKVManager {
            if (instance == null) {
                synchronized(MMKVManager::class.java) {
                    if (instance == null) {
                        instance = MMKVManager()
                    }
                }
            }
            return instance!!
        }
    }

    fun init(context: Context) {
        if (Build.VERSION.SDK_INT == 19) {
            val root = context.filesDir.absolutePath + "/mmkv"
            MMKV.initialize(root) { libName: String? -> ReLinker.loadLibrary(context, libName) }
        } else {
            MMKV.initialize(context)
        }
    }

    fun getMMKV(): MMKV? {
        return getMMKV(null)
    }

    fun getMMKV(name: String?): MMKV? {
        return if (TextUtils.isEmpty(name)) {
            MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null)
        } else {
            MMKV.mmkvWithID(name, MMKV.MULTI_PROCESS_MODE)
        }
    }

    //String Set类型---------------------------------------------------------------------------------
    fun getStringSet(key: String): Set<String> {
        return getMMKV()?.getStringSet(key, HashSet()) ?: HashSet()
    }

    fun getStringSet(key: String, defValues: Set<String>): Set<String> {
        return getMMKV()?.getStringSet(key, defValues) ?: defValues
    }

    fun getStringSet(name: String?, key: String, defValues: Set<String>): Set<String> {
        return getMMKV(name)?.getStringSet(key, defValues) ?: defValues
    }

    fun putStringSet(key: String, values: Set<String>?) {
        getMMKV()?.putStringSet(key, values)
    }

    fun putStringSet(name: String?, key: String, values: Set<String>?) {
        getMMKV(name)?.putStringSet(key, values)
    }

    //Double类型---------------------------------------------------------------------------------
    fun putDouble(key: String, value: Double) {
        getMMKV()?.encode(key, value)
    }

    fun getDouble(key: String): Double {
        return getMMKV()?.decodeDouble(key) ?: 0.0
    }

    fun getDouble(key: String, defValue: Double): Double {
        return getMMKV()?.decodeDouble(key, defValue) ?: defValue
    }

    fun getDouble(name: String?, key: String, defValue: Double): Double {
        return getMMKV(name)?.decodeDouble(key, defValue) ?: defValue
    }

    //byte[]类型---------------------------------------------------------------------------------
    fun putByte(key: String, value: ByteArray?) {
        getMMKV()?.encode(key, value)
    }

    fun getBytes(key: String): ByteArray? {
        return getMMKV()?.decodeBytes(key)
    }

    fun getBytes(key: String, defValue: ByteArray): ByteArray {
        return getMMKV()?.decodeBytes(key, defValue) ?: defValue
    }

    fun getBytes(name: String?, key: String, defValue: ByteArray): ByteArray {
        return getMMKV(name)?.decodeBytes(key, defValue) ?: defValue
    }

    //String类型---------------------------------------------------------------------------------
    fun getString(key: String): String? {
        return getMMKV()?.getString(key, "")
    }

    fun getString(key: String, defValue: String): String {
        return getMMKV()?.getString(key, defValue) ?: defValue
    }

    fun getString(name: String?, key: String, defValue: String): String {
        return getMMKV(name)?.getString(key, defValue) ?: defValue
    }

    fun putString(key: String, value: String?) {
        getMMKV()?.putString(key, value)
    }

    fun putString(name: String?, key: String, value: String?) {
        getMMKV(name)?.putString(key, value)
    }

    //Boolean类型-----------------------------------------------------------------------------------------------
    fun getBoolean(key: String): Boolean {
        return getMMKV()?.getBoolean(key, false) ?: false
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return getMMKV()?.getBoolean(key, defValue) ?: defValue
    }

    fun getBoolean(name: String?, key: String, defValue: Boolean): Boolean {
        return getMMKV(name)?.getBoolean(key, defValue) ?: defValue
    }

    fun putBoolean(key: String, value: Boolean) {
        getMMKV()?.putBoolean(key, value)
    }

    fun putBoolean(name: String?, key: String, value: Boolean) {
        getMMKV(name)?.putBoolean(key, value)
    }

    //Int类型-----------------------------------------------------------------------------------------------
    fun putInt(key: String, value: Int) {
        getMMKV()?.putInt(key, value)
    }

    fun putInt(name: String?, key: String, value: Int) {
        getMMKV(name)?.putInt(key, value)
    }

    fun getInt(key: String): Int {
        return getMMKV()?.getInt(key, 0) ?: 0
    }

    fun getInt(key: String, defValue: Int): Int {
        return getMMKV()?.getInt(key, defValue) ?: defValue
    }

    fun getInt(name: String?, key: String, defValue: Int): Int {
        return getMMKV(name)?.getInt(key, defValue) ?: defValue
    }

    //Float类型-----------------------------------------------------------------------------------------------
    fun putFloat(key: String, value: Float) {
        getMMKV()?.putFloat(key, value)
    }

    fun putFloat(name: String?, key: String, value: Float) {
        getMMKV(name)?.putFloat(key, value)
    }

    fun getFloat(key: String): Float {
        return getMMKV()?.getFloat(key, 0f) ?: 0f
    }

    fun getFloat(key: String, defValue: Float): Float {
        return getMMKV()?.getFloat(key, defValue) ?: defValue
    }

    fun getFloat(name: String?, key: String, defValue: Float): Float {
        return getMMKV(name)?.getFloat(key, defValue) ?: defValue
    }

    //Long类型-----------------------------------------------------------------------------------------------
    fun putLong(key: String, value: Long) {
        getMMKV()?.putLong(key, value)
    }

    fun putLong(name: String?, key: String, value: Long) {
        getMMKV(name)?.putLong(key, value)
    }

    fun getLong(key: String): Long {
        return getMMKV()?.getLong(key, 0L) ?: 0L
    }

    fun getLong(key: String, defValue: Long): Long {
        return getMMKV()?.getLong(key, defValue) ?: defValue
    }

    fun getLong(name: String?, key: String, defValue: Long): Long {
        return getMMKV(name)?.getLong(key, defValue) ?: defValue
    }

    //其他方法-----------------------------------------------------------------------------------------------
    fun remove(key: String) {
        getMMKV()?.remove(key)
    }

    fun remove(name: String?, key: String) {
        getMMKV(name)?.remove(key)
    }

    fun clear() {
        getMMKV()?.clear()
    }

    fun clear(name: String?) {
        getMMKV(name)?.clear()
    }

    fun close() {
        getMMKV()?.close()
    }

    operator fun contains(key: String): Boolean {
        return getMMKV()?.contains(key) ?: false
    }

    fun contains(name: String?, key: String): Boolean {
        return getMMKV(name)?.contains(key) ?: false
    }
}