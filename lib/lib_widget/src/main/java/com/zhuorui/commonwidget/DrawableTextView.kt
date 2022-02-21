package com.zhuorui.commonwidget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatTextView

/**
 *    date   : 2020/9/2 09:21
 *    desc   :
 */
class DrawableTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    @Px
    private var drawableWidth = 0
    @Px
    private var drawableHeight = 0
    @Px
    var leftDrawableWidth = 0
    @Px
    var leftDrawableHeight = 0
    @Px
    var rightDrawableWidth = 0
    @Px
    var rightDrawableHeight = 0

    init {
        attrs?.let {
            val a = getContext().obtainStyledAttributes(attrs, R.styleable.DrawableTextView)
            val defSize = textSize.toInt()
            val dw = a.getDimensionPixelOffset(
                    R.styleable.DrawableTextView_drawableWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val dh = a.getDimensionPixelOffset(R.styleable.DrawableTextView_drawableHeight, defSize)
            a.recycle()
            setDrawableSize(dw, dh)
        }
    }

    /**
     * 设置所有方向图片大小
     */
    fun setDrawableSize(@Px width: Int, @Px height: Int) {
        drawableWidth = width
        drawableHeight = height
        leftDrawableWidth = width
        leftDrawableHeight = height
        rightDrawableWidth = width
        rightDrawableHeight = height
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3])
    }

    override fun setCompoundDrawables(
            left: Drawable?,
            top: Drawable?,
            right: Drawable?,
            bottom: Drawable?
    ) {
        left?.let { resetBounds(it, leftDrawableWidth, leftDrawableHeight) }
        top?.let { resetBounds(it, drawableWidth, drawableHeight) }
        right?.let { resetBounds(it, rightDrawableWidth, rightDrawableHeight) }
        bottom?.let { resetBounds(it, drawableWidth, drawableHeight) }
        super.setCompoundDrawables(left, top, right, bottom)
    }

    private fun resetBounds(d: Drawable, width: Int, height: Int) {
        if (width > 0 && height > 0) {
            d.setBounds(0, 0, width, height)
        } else if (width > 0) {
            val h = width * 1f / d.minimumWidth * d.minimumHeight
            d.setBounds(0, 0, width, h.toInt())
        } else if (height > 0) {
            val w = height * 1f / d.minimumHeight * d.minimumWidth
            d.setBounds(0, 0, w.toInt(), height)
        }
    }
}