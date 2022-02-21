package com.zhuorui.commonwidget

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import com.zhuorui.commonwidget.adapter.IZRStateView

/**
 *    date   : 2021/4/21 13:55
 *    desc   :
 */
class ZRMultiStateFrame {

    private constructor(@IZRStateView.Companion.ZRSVState state: Int) {
        this.state = state
        this.baseStyleRes = 0
        this.onButtonClickListener = null
    }

    private constructor(@IZRStateView.Companion.ZRSVState state: Int, @StyleRes baseStyleRes: Int) {
        this.state = state
        this.baseStyleRes = baseStyleRes
    }


    @IZRStateView.Companion.ZRSVState
    val state: Int

    @StyleRes
    val baseStyleRes: Int

    var mode: Int? = null

    var onButtonClickListener: View.OnClickListener? = null

    @DrawableRes
    var iconResId: Int? = null

    var tipsText: CharSequence? = null

    var titleText: CharSequence? = null

    var buttonText: CharSequence? = null

    var customView: View? = null

    @StyleRes
    var stateBtnStyle:Int? = null


    companion object {

        /**
         * 正常空占位
         */
        fun createEmptyFrame(): ZRMultiStateFrame {
            return createEmptyFrame(R.style.Placeholder_Empty)
        }

        /**
         * 极简空占位
         */
        fun createEmptyMinimalismFrame(): ZRMultiStateFrame {
            return createEmptyFrame(R.style.Placeholder_Empty_Minimalism)
        }

        /**
         * button按钮空占位
         */
        fun createEmptyButtonFrame(): ZRMultiStateFrame {
            return createEmptyFrame(R.style.Placeholder_Empty_Button)
        }

        /**
         * 自定义样式空占位
         */
        fun createEmptyFrame(@StyleRes baseStyleRes: Int): ZRMultiStateFrame {
            return ZRMultiStateFrame(IZRStateView.EMPTY, baseStyleRes)
        }

        /**
         * 正常失败占位
         */
        fun createFailFrame(): ZRMultiStateFrame {
            return createFailFrame(R.style.Placeholder_Fail)
        }

        /**
         * 极简失败占位
         */
        fun createFailMinimalismFrame(): ZRMultiStateFrame {
            return createFailFrame(R.style.Placeholder_Fail_Minimalism)
        }

        /**
         * button按钮失败占位
         */
        fun createFailButtonFrame(): ZRMultiStateFrame {
            return createFailFrame(R.style.Placeholder_Fail_Button)
        }

        /**
         * 自定义样式失败占位
         */
        fun createFailFrame(@StyleRes baseStyleRes: Int): ZRMultiStateFrame {
            return ZRMultiStateFrame(IZRStateView.FAIL, baseStyleRes)
        }

        /**
         * 加载占位
         */
        fun createLoadingFrame(): ZRMultiStateFrame {
            return ZRMultiStateFrame(IZRStateView.LAODING)
        }

        /**
         * 自定义占位
         */
        fun createCustomFrame(view: View): ZRMultiStateFrame {
            return ZRMultiStateFrame(IZRStateView.CUSTOM).apply { customView = view }
        }

        /**
         * 显示内容
         */
        fun createContentFrame(): ZRMultiStateFrame {
            return ZRMultiStateFrame(IZRStateView.CONTENT)
        }

    }

}