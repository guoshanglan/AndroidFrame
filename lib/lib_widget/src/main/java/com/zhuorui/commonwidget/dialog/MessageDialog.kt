package com.zhuorui.commonwidget.dialog

import android.content.Context
import android.text.Spannable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import base2app.dialog.BaseDialog
import base2app.ex.color
import base2app.ex.setSafeClickListener
import base2app.ex.text
import base2app.util.AppUtil
import base2app.viewbinding.viewBinding
import com.zhuorui.commonwidget.R
import com.zhuorui.commonwidget.databinding.DialogMessageViewBinding


/**
 * @date 2020/4/21 18:55
 * @desc App弹出框
 */
open class MessageDialog : BaseDialog {

    constructor(
        fragment: Fragment,
        width: Int = (AppUtil.screenWidth * 0.8).toInt()
    ) : super(fragment, width, WindowManager.LayoutParams.WRAP_CONTENT)

    constructor(
        context: Context,
        width: Int = (AppUtil.screenWidth * 0.8).toInt()
    ) : super(
        context = context,
        w = width,
        h = WindowManager.LayoutParams.WRAP_CONTENT
    )


    private var onClickBottomCenterViewListener: (() -> Unit)? = null

    private var onClickBottomLeftViewListener: (() -> Unit)? = null

    private var onClickBottomRightViewListener: (() -> Unit)? = null

    override val layout: Int
        get() = R.layout.dialog_message_view

    private val binding by viewBinding(DialogMessageViewBinding::bind)

    init {
        with(binding){
            tvLeftButton.setSafeClickListener {
                dismiss()
                onClickBottomLeftViewListener?.invoke()
            }

            tvRightButton.setSafeClickListener {
                dismiss()
                onClickBottomRightViewListener?.invoke()
            }

            tvCenterButton.setSafeClickListener {
                dismiss()
                onClickBottomCenterViewListener?.invoke()
            }
        }

    }

    fun setMessageTitle(messageTitle: String): MessageDialog {
        binding.tvMessageTitle.text = messageTitle
        return this
    }

    fun setMessageTitleSize(titleTextSize: Float): MessageDialog {
        binding.tvMessageTitle.textSize = titleTextSize
        return this
    }

    fun setMessageContent(messageContent: CharSequence): MessageDialog {
        binding.tvMessageContent.text = messageContent
        return this
    }

    fun setMessageContent(messageContent: Spannable): MessageDialog {
        binding.tvMessageContent.text = messageContent
        return this
    }


    fun setMessageDialogStyle(messageDialogStyle: MessageDialogStyle): MessageDialog {
        with(binding){
            tvLeftButton.text = messageDialogStyle.leftText
            tvRightButton.text = messageDialogStyle.rightText
            tvLeftButton.setTextColor(messageDialogStyle.leftTextColor)
            tvRightButton.setTextColor(messageDialogStyle.rightTextColor)
            if (messageDialogStyle.isOnlyShowCenterView) {
                layoutBottom.visibility = View.GONE
                tvCenterButton.visibility = View.VISIBLE
                tvCenterButton.text = messageDialogStyle.centerText
                tvCenterButton.setTextColor(messageDialogStyle.centerTextColor)
            }

            tvMessageContent.gravity = messageDialogStyle.contentGravity
            tvMessageContent.setTextColor(messageDialogStyle.contentTextColor)
            dialog?.setCanceledOnTouchOutside(messageDialogStyle.isCanceledOnTouchOutside)
            if (messageDialogStyle.isIgnoreBackPressed) ignoreBackPressed()
            tvMessageTitle.visibility =
                if (messageDialogStyle.isShowMessageTitle) View.VISIBLE else View.GONE
        }
        return this
    }


    /**
     * 底部中间view点击，对应“我知道了”按钮
     */
    fun setOnClickBottomCenterViewListener(onClickBottomCenterViewListener: () -> Unit): MessageDialog {
        this.onClickBottomCenterViewListener = onClickBottomCenterViewListener
        return this
    }

    /**
     * 底部左侧点击，对应取消按钮
     */
    fun setOnClickBottomLeftViewListener(onClickBottomLeftViewListener: () -> Unit): MessageDialog {
        this.onClickBottomLeftViewListener = onClickBottomLeftViewListener
        return this
    }

    /**
     * 底部右侧点击，对应确定按钮
     */
    fun setOnClickBottomRightViewListener(onClickBottomRightViewListener: () -> Unit): MessageDialog {
        this.onClickBottomRightViewListener = onClickBottomRightViewListener
        return this
    }

    /**
     * 替换中间的View
     */
    fun replaceContentView(v: View): MessageDialog {
        with(binding){
            containerContent.removeAllViews()
            containerContent.addView(
                v,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
        }
        return this
    }


    fun getTvRightView(): TextView {
        return binding.tvRightButton
    }

    fun getTvLeftView(): TextView {
        return binding.tvLeftButton
    }

    fun getTvCenterView(): TextView {
        return binding.tvCenterButton
    }


    /**
     * 封装统一样式
     *
     */
    class MessageDialogStyle {
        /**
         * 左边按钮默认文本
         */
        var leftText: String = text(R.string.cancel)

        /**
         * 右边按钮默认文本
         */
        var rightText: String = text(R.string.ensure)

        /**
         * 中间按钮默认文本
         */
        var centerText: String = text(R.string.read_know)

        /**
         * 内容是否居中显示
         */
        var contentGravity: Int = Gravity.CENTER

        /**
         * 中间文字颜色
         */
        var centerTextColor: Int = color(R.color.dialog_content_text)

        /**
         * 左边文字颜色
         */
        var leftTextColor: Int = color(R.color.dialog_content_text)

        /**
         * 右边文字颜色
         */
        var rightTextColor: Int = color(R.color.brand_main_color)

        /**
         * 内容颜色
         */
        var contentTextColor: Int = color(R.color.dialog_content_text)

        /**
         * 是否只显示中间按钮
         */
        var isOnlyShowCenterView: Boolean = false

        /**
         * 是否显示标题
         */
        var isShowMessageTitle: Boolean = true

        /**
         * 点击取消点击外部关闭对话框
         */
        var isCanceledOnTouchOutside: Boolean = true

        /**
         * 是否忽略返回键，即点击返回键后无响应
         */
        var isIgnoreBackPressed: Boolean = false

        fun isShowMessageTitle(isShowMessageTitle: Boolean): MessageDialogStyle {
            this.isShowMessageTitle = isShowMessageTitle
            return this
        }

        fun onlyShowCenterView(): MessageDialogStyle {
            this.isOnlyShowCenterView = true
            return this
        }

        fun setLeftText(leftText: String): MessageDialogStyle {
            this.leftText = leftText
            return this
        }

        fun setRightText(rightText: String): MessageDialogStyle {
            this.rightText = rightText
            return this
        }

        fun setCenterText(centerText: String): MessageDialogStyle {
            this.centerText = centerText
            return this
        }

        fun setCenterTextColor(@ColorInt centerTextColor: Int): MessageDialogStyle {
            this.centerTextColor = centerTextColor
            return this
        }

        fun setLeftTextColor(@ColorInt leftTextColor: Int): MessageDialogStyle {
            this.leftTextColor = leftTextColor
            return this
        }

        fun setRightTextColor(@ColorInt rightTextColor: Int): MessageDialogStyle {
            this.rightTextColor = rightTextColor
            return this
        }

        fun setContentTextColor(@ColorInt contentTextColor: Int): MessageDialogStyle {
            this.contentTextColor = contentTextColor
            return this
        }

        fun setContentGravity(gravity: Int): MessageDialogStyle {
            this.contentGravity = gravity
            return this
        }

        fun ignoreBackPressed(): MessageDialogStyle {
            this.isIgnoreBackPressed = true
            return this
        }

        fun isCanceledOnTouchOutside(isCanceledOnTouchOutside: Boolean): MessageDialogStyle {
            this.isCanceledOnTouchOutside = isCanceledOnTouchOutside
            return this
        }
    }


    override fun onFragmentDestroy() {
        super.onFragmentDestroy()
        this.onClickBottomLeftViewListener = null
        this.onClickBottomCenterViewListener = null
        this.onClickBottomRightViewListener = null
    }

}