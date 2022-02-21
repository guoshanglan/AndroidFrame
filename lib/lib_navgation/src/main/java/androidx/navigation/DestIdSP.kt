package androidx.navigation

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.util.ArrayMap
import androidx.annotation.MainThread
import java.util.concurrent.atomic.AtomicInteger

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/8/2 17:20
 *    desc   : 目的地Id储存SP
 */
class DestIdSP private constructor() {

    private object SingletonHolder {
        val holder = DestIdSP()
    }

    companion object {
        val instance = SingletonHolder.holder
        private const val KEY_SELF_INCREMENT_ID = "selfIncrementID"
        private const val KEY_VERSION_CODE = "versionCode"
    }

    private lateinit var sharedPreferences: SharedPreferences

    /**
     * 自增Id
     */
    private val selfIncrementID: AtomicInteger = AtomicInteger(0)

    /**
     * 内存缓存
     */
    private val destIdMap = ArrayMap<String, Int>()

    fun init(application: Application) {
        sharedPreferences =
            application.getSharedPreferences(this::class.java.name, Context.MODE_PRIVATE)
        val curVersionCode = sharedPreferences.getLong(KEY_VERSION_CODE, 0)
        val appVersionCode = try {
            val pi = application.packageManager.getPackageInfo(
                application.packageName,
                PackageManager.GET_CONFIGURATIONS
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pi.longVersionCode else pi.versionCode.toLong()
        } catch (e: PackageManager.NameNotFoundException) {
            curVersionCode
        }
        if (curVersionCode != appVersionCode) {
            //版本更新后清一次
            sharedPreferences.edit().clear().putLong(KEY_VERSION_CODE, appVersionCode).commit()
        } else {
            //恢复内存缓存
            selfIncrementID.set(sharedPreferences.getInt(KEY_SELF_INCREMENT_ID, 0))
            sharedPreferences.all.iterator().forEach {
                it.key.let { key ->
                    if (key != KEY_VERSION_CODE && key != KEY_SELF_INCREMENT_ID) {
                        destIdMap[it.key] = it.value as Int
                    }
                }
            }
        }
    }

    @MainThread
    fun requireDestinationId(name: String): Int {
        return destIdMap[name] ?: selfIncrementID.incrementAndGet().also { id ->
            destIdMap[name] = id
            sharedPreferences.edit().putInt(KEY_SELF_INCREMENT_ID, id).putInt(name, id).apply()
        }
    }


}