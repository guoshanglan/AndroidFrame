package base2app.ui

import androidx.appcompat.app.AppCompatDelegate
import base2app.infra.AbsConfig
import base2app.infra.StorageInfra

/**
 * SkinConfig
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time 09 15:49
 */
class SkinConfig : AbsConfig() {

    @AppCompatDelegate.NightMode
    var mUIMode: Int = AppCompatDelegate.MODE_NIGHT_NO
        set(value) {
            field = value
            write()
        }

    override fun write() {
        StorageInfra.put(SkinConfig::class.java.simpleName, this)
    }

    companion object {

        private var instance: SkinConfig? = null

        fun getInstance(): SkinConfig {//使用同步锁
            if (instance == null) {
                synchronized(SkinConfig::class.java) {
                    if (instance == null) {
                        instance = read()
                    }
                }
            }
            return instance!!
        }

        private fun read(): SkinConfig {
            var config: SkinConfig? =
                StorageInfra.get(SkinConfig::class.java.simpleName, SkinConfig::class.java)
            if (config == null) {
                config = SkinConfig()
                config.write()
            }
            return config
        }

        fun clear() {
            StorageInfra.remove(SkinConfig::class.java.simpleName, SkinConfig::class.java.name)
            instance = null
        }
    }
}