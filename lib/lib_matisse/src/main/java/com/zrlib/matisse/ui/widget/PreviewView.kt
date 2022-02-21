package com.zrlib.matisse.ui.widget

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2020/9/23 14:13
 *    desc   : 原图预览View
 */
class PreviewView(context: Context, attr: AttributeSet? = null) : SubsamplingScaleImageView(context, attr) {

    constructor(context: Context) : this(context, null)

    private var actionUpAnimateScaleAndCenter = false
    private var mFitScale = 0f//高或宽满屏的缩放比
    private var mMinimumScaleType: Int = SCALE_TYPE_CENTER_INSIDE
    private var fitStart: Boolean = false

    init {
        orientation = ORIENTATION_USE_EXIF
        setDoubleTapZoomDuration(300)

    }

    override fun onImageLoaded() {
        val picW: Int = oWidth()
        val picH: Int = oHeight()
        val showH = height
        val showW = width
        if (picH * 1f / picW > showH * 1f / showW) {
            //满宽高度超出
            mFitScale = showH * 1f / picH
            minScale = showH / 3.0f / picH
            maxScale = if (picW < showW) {
                val max = maxScale.coerceAtLeast(showH * 3.0f / picH)
                if (max * picW < showW) {
                    showW * 3.0f / picW
                } else {
                    max
                }
            } else {
                maxScale.coerceAtLeast(showH * 3.0f / picH)
            }
            if (fitStart) {
                setScaleAndCenter(showW * 1f / picW, PointF((picW / 2).toFloat(), 0f))
            } else {
                setScaleAndCenter(mFitScale, PointF((picW / 2).toFloat(), (picH / 2).toFloat()))
            }
        } else {
            mFitScale = showW * 1f / picW
            minScale = showW / 3.0f / picW
            maxScale = maxScale.coerceAtLeast(showW * 3.0f / picW)
            setScaleAndCenter(mFitScale, PointF((picW / 2).toFloat(), (picH / 2).toFloat()))
        }
        setDoubleTapZoomScale(maxScale)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchCount = event.pointerCount
        when (event.action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (mFitScale !== 0f && scale < mFitScale) {
                    actionUpAnimateScaleAndCenter = true
                    animateScaleAndCenter(mFitScale, PointF((oWidth() / 2).toFloat(), (oHeight() / 2).toFloat()))
                        ?.withDuration(200)
                        ?.withEasing(EASE_OUT_QUAD)
                        ?.withInterruptible(false)
                        ?.start()
                    postDelayed({ changeMinimumScaleType(scale, ORIGIN_ANIM) }, 200)
                } else {
                    changeMinimumScaleType(scale, ORIGIN_FLING)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchCount >= 2) {
                    changeMinimumScaleType(scale, ORIGIN_TOUCH)
                }
            }
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                if (touchCount >= 2) {
                    changeMinimumScaleType(scale, ORIGIN_TOUCH)
                }
            }

        }
        return super.onTouchEvent(event)
    }


    /**
     * 获取显示宽
     */
    private fun oWidth(): Int {
        return if (appliedOrientation == 0 || appliedOrientation == 180) {
            sWidth
        } else {
            sHeight
        }
    }

    /**
     * 获取显示高
     */
    private fun oHeight(): Int {
        return if (appliedOrientation == 0 || appliedOrientation == 180) {
            sHeight
        } else {
            sWidth
        }
    }

    private fun changeMinimumScaleType(newScale: Float, origin: Int) {
        val minimumScaleType = when (origin) {
            ORIGIN_ANIM -> SCALE_TYPE_CENTER_INSIDE
            ORIGIN_TOUCH -> SCALE_TYPE_CUSTOM
            ORIGIN_DOUBLE_TAP_ZOOM -> SCALE_TYPE_CENTER_INSIDE
            else -> SCALE_TYPE_CENTER_INSIDE
        }
        if (mMinimumScaleType != minimumScaleType) {
            mMinimumScaleType = minimumScaleType
            setMinimumScaleType(mMinimumScaleType)
        }
    }

    /**
     * 设置ImageView的FIT_START模式
     */
    fun setFitStart(t: Boolean) {
        fitStart = t
    }

    /**
     * 重置View状态
     */
    fun reset() {
        if (mFitScale !== 0f) {
            onImageLoaded()
        }
    }

}