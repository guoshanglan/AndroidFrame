package base2app.ex

import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import base2app.BaseApplication
import com.zhuorui.securties.skin.util.Utils


/**
 * date : 2020/10/28
 * dest : Resource 资源类型的扩展函数集合 , 与 ResUtil 保持一致
 */
fun resource(): Resources {
    BaseApplication.context?.let {
        Utils.correctConfigUiMode(it)
    }
    return BaseApplication.context?.resources ?: Resources.getSystem()
}

fun drawable(@DrawableRes id: Int): Drawable? {
    return resource().getDrawable(id)
}

fun text(@StringRes id: Int): String {
    return if (id == 0) "" else resource().getString(id)
}

fun color(@ColorRes id: Int): Int {
    return resource().getColor(id)
}

fun colorState(@ColorRes id: Int): ColorStateList {
    return resource().getColorStateList(id)
}

fun alphaColor(alphaPercent: Float, color: Int): Int {
    return (alphaPercent * 255).toInt() shl 24 or (color and 0xffffff)
}

fun stringArray(@ArrayRes id: Int): Array<String> {
    return resource().getStringArray(id)
}

fun intArray(@ArrayRes id: Int): IntArray {
    return resource().getIntArray(id)
}

/** 获取屏幕是否是竖屏
 * @return
 */
fun isScreenPortrait(): Boolean {
    return when (BaseApplication.baseApplication.topActivity?.requestedOrientation) {
        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE -> false
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT -> true
        else -> true
    }
}