package com.zhuorui.commonwidget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import base2app.ex.color
import base2app.ex.dp2px

/**

@author: guoshanglan
@description:标签气泡View
@date : 2021/12/22 17:02
 */
class ZRBubbleView @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        val t = context?.obtainStyledAttributes(attrs, R.styleable.ZRBubbleView)
        val border = t?.getBoolean(R.styleable.ZRBubbleView_border, false)
        val backGround =
            if (border == true) R.drawable.bubble_view_stroke_bg else R.drawable.bubble_view_bg
        setBackgroundResource(backGround)
        setPadding(5f.dp2px().toInt(), 3f.dp2px().toInt(), 5f.dp2px().toInt(), 3f.dp2px().toInt())
        setTextColor(color(R.color.main_button_text_color))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f)
        t?.recycle()
    }


    fun isShowBubbleBorder(isShowBorder:Boolean){
        val backGround =
            if (isShowBorder) R.drawable.bubble_view_stroke_bg else R.drawable.bubble_view_bg
        setBackgroundResource(backGround)
    }

}