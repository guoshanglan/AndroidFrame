package com.zhuorui.commonwidget.drawable

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Gravity
import androidx.annotation.DrawableRes
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.zhuorui.securities.base2app.glide.ZRGlide

/**
 * date : 2020/9/22
 * dest : ImageDrawable
 */
open class ImageDrawable private constructor(mDrawableParams: DrawableParams) :
    BaseDrawable(mDrawableParams) {

    private var mDrawable: Drawable? = null

    override fun measureContent() {
        mDrawable?.bounds?.let {
            bounds.set(it)
        }
    }

    @SuppressLint("CanvasSize", "RtlHardcoded")
    override fun draw(canvas: Canvas) {
        mDrawable?.let {
            it.bounds = bounds //将 drawable 的范围进行同步
            val count = canvas.save()
            if (canvas.height > bounds.height() && mDrawableParams.gravity != -1) {
                when (mDrawableParams.gravity) {
                    Gravity.BOTTOM -> {
                        canvas.translate(0f, (canvas.height - bounds.height()).toFloat())
                    }
                    Gravity.CENTER -> {
                        canvas.translate(0f, (canvas.height - bounds.height()) / 2f)
                    }
                    /*  Gravity.TOP -> {top 就是从0点开始画
                          canvas.translate(0f, (canvas.height - bounds.height()).toFloat())
                      }*/
                }
            }

            val r = bounds
            val width = r.width()
            val height = r.height()

            if (canvas.height == bounds.height() && mDrawableParams.gravity != -1) {
                var x = 0f
                var y = 0f
                when (mDrawableParams.gravity and Gravity.VERTICAL_GRAVITY_MASK) {
                    Gravity.BOTTOM -> {
                        y = (height - it.bounds.height()).toFloat()
                    }
                    Gravity.CENTER -> {
                        y = (height - it.bounds.height()) / 2f
                    }
                }
                when (mDrawableParams.gravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                    Gravity.END, Gravity.RIGHT -> {
                        x = (width - it.bounds.width()).toFloat()
                    }
                }
                canvas.translate(x, y)
                it.draw(canvas)
            } else {
                it.draw(canvas)
            }
            canvas.restoreToCount(count)
        }
    }

    override fun setAlpha(alpha: Int) {
        mDrawable?.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mDrawable?.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    fun setImage(drawable: Drawable): ImageDrawable {
        drawable.bounds.set(0, 0, drawable.intrinsicWidth, drawable.intrinsicWidth)
        mDrawable = drawable
        mView?.invalidate(this)
        return this
    }


    fun setImage(@DrawableRes id: Int): ImageDrawable {
        if (mView != null) {
            val drawable = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                mView!!.context.resources.getDrawable(id,null)
            } else {
                mView!!.context.resources.getDrawable(id)
            }
            drawable.bounds.set(0, 0, drawable.intrinsicWidth, drawable.intrinsicWidth)
            mDrawable = drawable
            mView?.invalidate(this)
        }
        return this
    }

    fun setImage(path: String): ImageDrawable {
        if (mView != null) {
            ZRGlide.with(mView!!).load(path).into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    Log.d("setImage", "onResourceReady: ")
                    resource.bounds.set(0, 0, resource.intrinsicWidth, resource.intrinsicWidth)
                    mDrawable = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
        }
        return this
    }

    fun adjust(what: ImageDrawable.() -> Unit = {}) {
        this.what()
    }

    companion object {

        @JvmStatic
        fun build(
            view: DrawableLayout,
            mParams: DrawableParams.() -> Unit = {}
        ): ImageDrawable {
            return ImageDrawable(DrawableParams.builder(mParams)).apply { mView = view }
        }
    }

}