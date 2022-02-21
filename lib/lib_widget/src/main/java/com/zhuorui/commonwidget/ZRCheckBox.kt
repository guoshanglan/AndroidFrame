package com.zhuorui.commonwidget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import base2app.ex.dp2px

/**
 *
 * Desc:    自定义ChexBox 动态限制drawable大小
 * Author:  luosi
 *
 * 解决drawable文件中android:width和android:height属性在API23版本以下失效问题
 */
class ZRCheckBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatCheckBox(context, attrs, defStyleAttr) {

    private var btnDrawable: StateListDrawable? = null

    /**
     * buttonDrawable宽高
     */
    private val btnWidth: Int
    private val btnHeight: Int

    /**
     * 是否需要重置Drawable
     * 默认为false
     */
    private var resetDrawable = false


    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ZRCheckBox)
        btnWidth = a.getDimensionPixelOffset(
            R.styleable.ZRCheckBox_button_drawable_width,
            16.dp2px().toInt()
        )
        btnHeight = a.getDimensionPixelOffset(
            R.styleable.ZRCheckBox_button_drawable_height,
            16.dp2px().toInt()
        )
        if (resetDrawable) {
            resetDrawable = false
            a.getDrawable(R.styleable.ZRCheckBox_android_button)?.let { d ->
                buttonDrawable = d
            }
        }
        a.recycle()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        btnDrawable?.let { drawable ->
            if (drawable.isStateful && drawable.setState(drawableState)) {
                super.setButtonDrawable(BitmapDrawable(resources, drawableToBitmap(drawable.current)))
            }
        }
    }

    override fun setButtonDrawable(buttonDrawable: Drawable?) {
        //SDK版本<23，buttonDrawable为StateListDrawable需要重设宽高
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M && buttonDrawable is StateListDrawable) {
            if (btnWidth > 0 && btnHeight > 0) {
                btnDrawable = buttonDrawable
                btnDrawable?.let { drawable ->
                    drawable.state = drawableState
                    super.setButtonDrawable(
                        BitmapDrawable(
                            resources,
                            drawableToBitmap(drawable.current)
                        )
                    )
                }
            } else {
                resetDrawable = true
            }
        } else {
            //SDK版本>=23则不需要处理
            super.setButtonDrawable(buttonDrawable)
        }
    }

    /**
     * Drawable转Bitmap
     */
    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        val bitmap = Bitmap.createBitmap(btnWidth, btnHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, bitmap.width, bitmap.height)
        drawable.draw(canvas)
        return bitmap
    }
}