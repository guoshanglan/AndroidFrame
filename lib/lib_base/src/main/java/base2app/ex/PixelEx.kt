package base2app.ex

import android.util.TypedValue

/**
 * date : 2020/10/28
 * dest : PixelEx 当前类方法都是为了帮助将特定单位数据转换成像素
 * px 转 dp 暂时用处并不大
 */
fun Int.dp2px(): Float {
    return this.toFloat().dp2px()
}

fun Float.dp2px(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, resource().displayMetrics)
}

fun Int.sp2px(): Float {
    return this.toFloat().sp2px()
}

fun Float.sp2px(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this,  resource().displayMetrics)
}

