package androidx.navigation.fragment

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.SystemClock
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.RestrictTo
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavUtil

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/7/13 18:05
 *    desc   : 动画生成器
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class AnimGenerator {

    private var mAnimationListener: AnimationListener? = null
    private var fragment: Fragment? = null

    private var fragmentName: String? = null

    /**
     * 是否在进场动画期间（包括动画创建，等待执行，动画执行）
     */
    private var mEnterAniming = false

    /**
     * 进场动画时间结束时间，该时间只存在执行动画的fragment，其他fragment为-1
     * -1:没有进场动画；大于等于0：动画执行期间
     */
    private var mEnterAnimStopTime = -1L

    /**
     * 检查动画有没有正确结束，确保fragment的lazy功能正常
     */
    private val mCheckStopRun = lazy {
        Runnable {
            if (mEnterAniming) {
                resetAnimState()
                mAnimationListener?.onEnterAnimEnd()
            }
        }
    }

    private fun resetAnimState() {
        mEnterAnimStopTime = -1L
        mEnterAniming = false
    }

    fun createAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (nextAnim == 0) return null
        val ctx = fragment?.context ?: return null
        val dir = ctx.resources.getResourceTypeName(nextAnim)
        if (enter && "anim" == dir) {
            if (NavUtil.debug) Log.d(
                "life",
                "$fragmentName onCreateAnimation:transit:$transit enter:$enter nextAnim:$nextAnim dir:$dir"
            )
            mEnterAniming = true
            mAnimationListener?.onEnterAnimStart()
            return AnimationUtils.loadAnimation(ctx, nextAnim).apply {

                mEnterAnimStopTime = SystemClock.elapsedRealtime() + duration

                setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        mCheckStopRun.value.run()
                    }

                    override fun onAnimationStart(animation: Animation?) {
                        animation?.let {
                            //校正结束时间,并重新发起检查
                            mEnterAnimStopTime = SystemClock.elapsedRealtime() + it.duration + 5
                            checkStop()
                        }
                    }

                })
            }
        }
        return null
    }

    fun createAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        if (nextAnim == 0) return null
        val ctx = fragment?.context ?: return null
//        val dir = ctx.resources.getResourceTypeName(nextAnim)
        if (enter) {
            if (NavUtil.debug) Log.d(
                "life",
                "$fragmentName onCreateAnimator:transit:$transit enter:$enter nextAnim:$nextAnim"
            )
            mEnterAniming = true
            mAnimationListener?.onEnterAnimStart()
            return AnimatorInflater.loadAnimator(ctx, nextAnim).apply {

                mEnterAnimStopTime = SystemClock.elapsedRealtime() + duration

                doOnEnd {
                    mCheckStopRun.value.run()
                }
                doOnStart {
                    //校正结束时间,并重新发起检查
                    mEnterAnimStopTime = SystemClock.elapsedRealtime() + it.duration + 5
                    checkStop()

                }
            }
        }
        return null
    }

    private fun checkStop(): Boolean {
        val remainingTime = mEnterAnimStopTime - SystemClock.elapsedRealtime()
        if (remainingTime <= 0) return false
        return fragment?.view?.let { v ->
            val run = mCheckStopRun.value
            v.removeCallbacks(run)
            v.postDelayed(mCheckStopRun.value, remainingTime)
        }.let { it != null }
    }

    fun setRunging(t: Boolean) {
        mEnterAniming = t
    }

    fun isRunging(): Boolean {
        return mEnterAniming
    }

    fun bindFragment(fragment: Fragment) {
        fragmentName = fragment.javaClass.simpleName
        this.fragment = fragment
        mAnimationListener = fragment as AnimationListener
        fragment.lifecycle.addObserver(object : LifecycleEventObserver {

            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        onResume()
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        onPause()
                    }
                    Lifecycle.Event.ON_DESTROY -> {
                        onDestroy()
                    }

                }
            }
        })

    }

    private fun onResume() {
        when (mEnterAnimStopTime) {
            //不是执行动画的fragment，询问父类进场动画状态
            -1L -> {
                if (!mEnterAniming) {
                    val p = fragment?.parentFragment
                    if (p is AnimationListener) {
                        mEnterAniming = p.isEnterAnim()
                    }
                }
            }
            //是执行动画的fragment,检查动画结束
            else -> {
                if (!checkStop()) {
                    resetAnimState()
                }
            }
        }
        //可见时不在进场动画期间，通知动画结束，触发懒加载事件
        if (!mEnterAniming) {
            mAnimationListener?.onEnterAnimEnd()
        }
    }

    private fun onPause() {
        if (mCheckStopRun.isInitialized()) {
            fragment?.view?.removeCallbacks(mCheckStopRun.value)
        }
        resetAnimState()
    }

    private fun onDestroy() {
        mAnimationListener = null
        fragment = null
    }


    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    internal interface AnimationListener {

        /**
         * 进场动画期开始
         */
        fun onEnterAnimStart()

        /**
         * 进场动画期结束
         */
        fun onEnterAnimEnd()

        /**
         * 是否有进场动画
         */
        fun isEnterAnim(): Boolean
    }
}