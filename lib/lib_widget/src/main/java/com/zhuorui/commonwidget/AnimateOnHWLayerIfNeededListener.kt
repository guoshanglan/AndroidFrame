package com.zhuorui.commonwidget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View

/**
 * date : 2020/8/28
 * dest : AnimateOnHWLayerIfNeededListener
 */
open class AnimateOnHWLayerIfNeededListener(private var mView: View?) : Animator.AnimatorListener {
    private var mShouldRunOnHWLayer = false
    override fun onAnimationStart(animation: Animator) {
        mShouldRunOnHWLayer = shouldRunOnHWLayer(mView, animation)
        if (mShouldRunOnHWLayer) {
            mView!!.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
    }
    override fun onAnimationEnd(animation: Animator) {
        if (mShouldRunOnHWLayer) {
            mView!!.setLayerType(View.LAYER_TYPE_NONE, null)
        }
        mView = null
        animation.removeListener(this)
    }
    override fun onAnimationCancel(animation: Animator) {
    }
    override fun onAnimationRepeat(animation: Animator) {
    }
    fun shouldRunOnHWLayer(v: View?, anim: Animator?): Boolean {
        return if (v == null || anim == null) {
            false
        } else v.layerType == View.LAYER_TYPE_NONE
                && v.hasOverlappingRendering()
                && modifiesAlpha(anim)
    }
    private fun modifiesAlpha(anim: Animator?): Boolean {
        if (anim == null) {
            return false
        }
        if (anim is ValueAnimator) {
            val valueAnim = anim as ValueAnimator?
            val values = valueAnim!!.values
            for (i in values.indices) {
                if ("alpha" == values[i].propertyName) {
                    return true
                }
            }
        } else if (anim is AnimatorSet) {
            val animList = anim.childAnimations
            for (i in animList.indices) {
                if (modifiesAlpha(animList[i])) {
                    return true
                }
            }
        }
        return false
    }
}