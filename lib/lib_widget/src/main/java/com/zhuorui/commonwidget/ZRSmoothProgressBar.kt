package com.zhuorui.commonwidget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.ProgressBar
import base2app.ex.color

/**
 * @date 2022/1/25 14:00
 * @desc
 */
class ZRSmoothProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ProgressBar(context, attrs, defStyleAttr) {


    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.ZRSmoothProgressBar)
        setProgressColor(typeArray.getColor(R.styleable.ZRSmoothProgressBar_progressBarColor, color(R.color.brand_main_color)))
        typeArray.recycle()
    }


    private fun setProgressColor(progressBarColor: Int) {
        val p = GradientDrawable()
        p.cornerRadius = 0f
        p.setColor(progressBarColor)
        val progress = ClipDrawable(p, Gravity.START, ClipDrawable.HORIZONTAL)
        val background = GradientDrawable()
        background.setColor(Color.TRANSPARENT)
        background.cornerRadius = 0f
        val pd = LayerDrawable(arrayOf(background, progress))
        this.progressDrawable = pd
    }

    fun onProgressStart() {
        this.progress = 0
        this.visibility = View.VISIBLE
        this.alpha = 1.0f
    }

    fun onProgressChange(newProgress: Int) {
        progressAnimation(newProgress, this.progress)
    }


    private fun progressAnimation(newProgress: Int, currentProgress: Int) {
        if (newProgress > currentProgress) {
            onProgressStart()
            val animator = ObjectAnimator.ofInt(this, "progress", currentProgress, newProgress)
            animator.duration = (((newProgress - currentProgress) / 100f) * 1000).toLong()
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (newProgress >= 100) {
                        dismissAnimation()
                    }
                }
            })
            animator.start()
        }
    }

    private fun dismissAnimation() {
        val anim = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f)
        anim.duration = 300
        anim.interpolator = AccelerateInterpolator()
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@ZRSmoothProgressBar.progress = 0
                this@ZRSmoothProgressBar.visibility = View.GONE
            }
        })
        anim.start()
    }
}