package com.zhuorui.commonwidget

import android.content.Context
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import base2app.ex.color

/**
 * @date 2020/12/10 10:22
 * @desc 最大显示指定行，超过部分使用 ellipsizeText代替，并支持点击ellipsizeText查看全部内容
 */
class ZREllipsizeTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    /**
     * 省略的颜色
     */
    private var ellipsizeColor: Int? = null

    /**
     * 省略的文字描述
     */
    private var ellipsizeText: String? = null

    /**
     * 显示的最大行数，超过该值后显示ellipsizeText
     */
    private var ellipsizeMaxLines: Int = 4

    /**
     * 是否已经展开
     */
    private var isExpanded: Boolean = false

    /**
     * 截取之后的文字
     */
    private var interceptText: CharSequence? = null


    private var originalText: CharSequence? = null


    private var onPreDrawListener: ViewTreeObserver.OnPreDrawListener? = null


    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.ZREllipsizeTextView)
        ellipsizeMaxLines = typeArray.getInteger(R.styleable.ZREllipsizeTextView_etv_maxLines, 4)
        ellipsizeColor = typeArray.getColor(
            R.styleable.ZREllipsizeTextView_etv_ellipsize_color,
            color(R.color.brand_main_color)
        )
        ellipsizeText = typeArray.getString(R.styleable.ZREllipsizeTextView_etv_ellipsize_text)
        typeArray.recycle()
    }

    override fun getDefaultMovementMethod(): MovementMethod {
        return LinkMovementMethod.getInstance()
    }

    /**
     * 获取截取之后的内容
     */
    fun getInterceptText(): CharSequence? {
        return interceptText
    }

    /**
     * 是否已经展开
     */
    fun isExpanded(): Boolean {
        return isExpanded
    }


    /**
     * 获取原文本内容
     */
    fun getOriginalText(): CharSequence? {
        return originalText
    }

    /**
     * 设置需要监听最大行的内容
     */
    fun setEllipsizeContent(content: CharSequence?) {
        if (!content.isNullOrEmpty()) {
            this.originalText = content
            onPreDrawListener = object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    onPreDrawListener = null
                    ellipsize()
                    return true
                }
            }
            viewTreeObserver.addOnPreDrawListener(onPreDrawListener)
            text = content
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onPreDrawListener?.let {
            viewTreeObserver.removeOnPreDrawListener(onPreDrawListener)
        }
    }

    private fun ellipsize() {
        if (TextUtils.isEmpty(originalText) || maxLines == -1 || lineCount <= ellipsizeMaxLines) return
        SpannableStringBuilder(originalText).apply {
            val lastLine = ellipsizeMaxLines - 1
            val layout = layout
            val lineStart = layout.getLineStart(lastLine)
            val lineVisibleEnd = layout.getLineVisibleEnd(lastLine)
            val ellipsizeText = getEllipsizeSpannableString()
            val lastLineText = subSequence(lineStart, lineVisibleEnd)
            if (TextUtils.isEmpty(lastLineText.trim()) || lastLineText.length <= ellipsizeText.length) {
                replace(lineStart, length, ellipsizeText)
            } else {
                replace(lineVisibleEnd - ellipsizeText.length, length, ellipsizeText)
            }
        }.let {
            this.interceptText = it
            this.text = it
        }
    }

    private fun getEllipsizeSpannableString(): CharSequence {
        return SpannableString(ellipsizeText).apply {
            setSpan(
                LookMoreClickableSpan(this@ZREllipsizeTextView, ellipsizeColor!!, originalText!!) {
                    isExpanded = true
                },
                0,
                length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    class LookMoreClickableSpan(
        private val originalTextView: TextView,
        private val ellipsizeColor: Int,
        private val originalText: CharSequence,
        private val onTextExpandedListener: (() -> Unit)?
    ) : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = ellipsizeColor
        }

        override fun onClick(widget: View) {
            originalTextView.text = originalText
            onTextExpandedListener?.invoke()
        }
    }
}