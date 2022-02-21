package base2app.ex

import android.content.res.Resources
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import com.zhuorui.securities.base2app.ex.TextFont
import com.zhuorui.securties.skin.ZRSkinManager
import com.zhuorui.securties.skin.view.ZRSkinAble

/**
 * date : 2020/9/14
 * dest : 控件相关的扩展函数集合
 */
fun TextView.sansSerifMedium() {
    typeface = TextFont.SAN_SERIf_MEDIUM
}

fun TextView.default() {
    typeface = Typeface.DEFAULT
}

fun TextView.blod() {
    typeface = Typeface.DEFAULT_BOLD
}

fun Any.skin(view: View, skin: ((Resources) -> Unit)? = null) {
    ZRSkinManager.instance.registSkin(this, view, skin)
}

fun Any.skin(skin: ((Resources) -> Unit)? = null) {
    ZRSkinManager.instance.registSkin(this, object : ZRSkinAble {
        override fun applyUIMode(resources: Resources?) {
            resources?.let { skin?.invoke(it) }
        }
    })
}

fun Any.unregistSkin() {
    ZRSkinManager.instance.unregistSkin(this)
}

/**
 * 控件安全点击
 */
inline fun View.setSafeClickListener(time: Long? = null, crossinline action: () -> Unit) {
    var lastClick = 0L
    setOnClickListener {
        val gap = System.currentTimeMillis() - lastClick
        lastClick = System.currentTimeMillis()
        val temp = time ?: 500
        if (gap < temp) return@setOnClickListener
        action.invoke()
    }
}

/**
 * 控件安全点击
 */
inline fun View.setSafeViewClickListener(time: Long? = null,crossinline action: (v:View) -> Unit) {
    var lastClick = 0L
    setOnClickListener {
        val gap = System.currentTimeMillis() - lastClick
        lastClick = System.currentTimeMillis()
        val temp = time?:300
        if (gap < temp) return@setOnClickListener
        action.invoke(it)
    }
}