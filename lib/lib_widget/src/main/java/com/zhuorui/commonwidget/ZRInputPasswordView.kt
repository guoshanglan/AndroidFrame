package com.zhuorui.commonwidget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.util.AttributeSet
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatEditText
import base2app.ex.color
import base2app.ex.dp2px
import java.util.*


/**
 * @date 2020/9/8 18:26
 * @desc 密码输入框
 */
class ZRInputPasswordView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var itemPasswordViews: Array<PasswordItemView?>? = null

    /**
     * 辅助输入框
     */
    private var mAssistEditText: AppCompatEditText? = null

    private var inputDigits: Int = 6

    private var isOpenCursor: Boolean = false


    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.ZRInputPasswordView)

        /**
         * 输入位数，默认6位
         */
        inputDigits = typeArray.getInteger(
            R.styleable.ZRInputPasswordView_ipv_password_digits,
            6
        )


        /**
         * 框的高度，实际高度取决于外部设置的高度
         */
        val itemHeight = typeArray.getDimensionPixelSize(
            R.styleable.ZRInputPasswordView_ipv_item_height, 50.dp2px().toInt()
        )

        /**
         * 框的宽度
         */
        val itemWidth = typeArray.getDimensionPixelSize(
            R.styleable.ZRInputPasswordView_ipv_item_width, 50.dp2px().toInt()
        )

        /**
         * 是否隐藏输入，如果隐藏，则显示点代替输入的值
         */
        val isHideInput = typeArray.getBoolean(R.styleable.ZRInputPasswordView_ipv_hide_input, true)

        /**
         * 是否需要游标
         */
        isOpenCursor = typeArray.getBoolean(R.styleable.ZRInputPasswordView_ipv_open_cursor, false)

        val borderMode = typeArray.getInt(R.styleable.ZRInputPasswordView_ipv_border_mode, 1)

        //边框颜色
        val borderColor = typeArray.getColor(
            R.styleable.ZRInputPasswordView_ipv_border_color, Color.parseColor("#CDCDD4")
        )

        //边框宽度
        val borderWidth = typeArray.getDimensionPixelSize(
            R.styleable.ZRInputPasswordView_ipv_border_width, 1.dp2px().toInt()
        )

        //游标颜色
        val cursorColor = typeArray.getColor(
            R.styleable.ZRInputPasswordView_ipv_cursor_color, color(R.color.brand_main_color)
        )
        addInputFrameParentView(
            borderMode, borderWidth, borderColor,
            cursorColor, isHideInput, itemWidth, itemHeight
        )
        addAssistEditText(itemWidth)
        typeArray.recycle()
    }


    /**
     * 创建输入框集合
     */
    private fun addInputFrameParentView(
        borderMode: Int, borderWidth: Int, borderColor: Int,
        cursorColor: Int, isHideInput: Boolean,
        frameWidth: Int, frameHeight: Int
    ) {
        val inputFrameParent = LinearLayout(context)
        inputFrameParent.orientation = LinearLayout.HORIZONTAL
        inputFrameParent.gravity = Gravity.CENTER
        if (borderMode == PasswordItemView.BORDER_MODE_FRAME) {
            inputFrameParent.background = GradientDrawable().apply {
                setStroke(borderWidth, borderColor)
            }
        }
        itemPasswordViews = arrayOfNulls(inputDigits)
        for (index in 0 until inputDigits) {
            val itemView = PasswordItemView(context)
                .borderMode(borderMode, borderColor, borderWidth)
                .isHideInput(isHideInput)
                .isOpenCursor(isOpenCursor, cursorColor).build()
            itemPasswordViews!![index] = itemView
            inputFrameParent.addView(itemView, LinearLayout.LayoutParams(frameWidth, frameHeight))
        }
        val parentLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        parentLayoutParams.addRule(CENTER_VERTICAL)
        addView(inputFrameParent, parentLayoutParams)
    }


    /**
     * 密码输入监听
     */
    fun setOnInputPasswordListener(onInputPasswordListener: ((isComplete: Boolean, inputResult: String?) -> Unit)) {
        this.mOnInputPasswordListener = onInputPasswordListener
    }

    private var mOnInputPasswordListener: ((isComplete: Boolean, inputResult: String?) -> Unit)? = null

    fun getAssistEditText(): EditText? {
        return mAssistEditText
    }

    /**
     * 加入辅助输入框，用于监听用户输入
     */
    private fun addAssistEditText(itemWidth: Int) {
        mAssistEditText = AppCompatEditText(context)
        mAssistEditText?.isCursorVisible = false
        mAssistEditText?.setTextColor(Color.TRANSPARENT)
        mAssistEditText?.textSize = 1f
        mAssistEditText?.setBackgroundColor(Color.TRANSPARENT)
        mAssistEditText?.filters = arrayOf(MaxDigitsInputFilter(inputDigits))
        mAssistEditText?.inputType =
            InputType.TYPE_NUMBER_VARIATION_PASSWORD or InputType.TYPE_CLASS_NUMBER

        mAssistEditText?.setOnFocusChangeListener { v, hasFocus ->
            if (v == mAssistEditText) animationCursor(hasFocus)
        }
        mAssistEditText?.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                val inputPassword = s?.toString()
                //有输入，根据输入的长度控制相关view的展示
                itemPasswordViews?.forEachIndexed { index, view ->
                    view?.controlInputText(index, inputPassword)
                }
                animationCursor(true)
                if (inputPassword?.length ?: 0 >= inputDigits) {
                    this@ZRInputPasswordView.postDelayed({
                        mOnInputPasswordListener?.invoke(true, inputPassword)
                    }, 100)
                } else {
                    mOnInputPasswordListener?.invoke(false, inputPassword)
                }
            }
        })
        addView(mAssistEditText, LayoutParams(itemWidth * inputDigits, LayoutParams.MATCH_PARENT))
    }


    private fun animationCursor(hasFocus: Boolean) {
        if (isOpenCursor) {
            itemPasswordViews?.forEachIndexed { index, view ->
                if (hasFocus) {
                    when (val textLength = mAssistEditText?.text?.length ?: 0) {
                        itemPasswordViews?.size ?: 0 -> view?.controlCursorVisible(false)
                        else -> view?.controlCursorVisible(index == textLength)
                    }
                } else {
                    view?.controlCursorVisible(false)
                }
            }
        }
    }


    class MaxDigitsInputFilter(private val maxDigits: Int) : InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            // 删除等特殊字符，直接返回
            if ("" == source.toString()) {
                return null
            }
            //不存在小数点,输入的为整数，控制最大输入位数
            return if (dest?.length ?: 0 >= maxDigits) {
                ""
            } else {
                source
            }
        }
    }

}