package com.zhuorui.securties.skin.util

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.NightMode
import com.zhuorui.securties.skin.ZRSkinManager
import com.zhuorui.securties.skin.app.ResourcesFlusher

/**
 * SkinUtil 类或接口
 *
 * @company 深圳市卓锐网络科技有限公司
 * @description
 * @date 2021/1/5-14:02
 */
object SkinUtil {


    /**
     * 根据 Configuration 判断当前的 AppCompatDelegate 类型
     */
    fun getAPPUIMode(configuration: Configuration): Int {
        return when (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
//                Log.d("ZRSkinManager", "configuration  AppCompatDelegate.MODE_NIGHT_YES ")
                AppCompatDelegate.MODE_NIGHT_YES
            }
            Configuration.UI_MODE_NIGHT_NO -> {
//                Log.d("ZRSkinManager", "configuration  AppCompatDelegate.MODE_NIGHT_NO ")
                AppCompatDelegate.MODE_NIGHT_NO
            }
            else -> {
//                Log.d("ZRSkinManager", "configuration  AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM ")
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        }
    }

}
