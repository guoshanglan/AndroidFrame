package com.zhuorui.commonwidget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.zhuorui.commonwidget.databinding.PersonalViewOptionBinding

/**
 *
 * @Description:    个人信息选项
 */
class CommentOptionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: PersonalViewOptionBinding =
        PersonalViewOptionBinding.inflate(LayoutInflater.from(context), this, true)

    init {
//        View.inflate(context, R.layout.personal_view_option, this)
        val a = context.obtainStyledAttributes(attrs, R.styleable.CommentOptionView)
        with(binding) {
            optionTitle.text = a.getString(R.styleable.CommentOptionView_option_title)
            optionContent.text = a.getString(R.styleable.CommentOptionView_option_content)
            optionContent.gravity =
                a.getInt(R.styleable.CommentOptionView_option_content_gravity, Gravity.START)
            if (a.getBoolean(R.styleable.CommentOptionView_has_more_btn, true)) {
                optionMoreBtn.visibility = View.VISIBLE
            } else {
                optionMoreBtn.visibility = View.GONE
            }
        }
        a.recycle()
    }

    /**
     * 设置选项内容
     */
    fun setContent(content: String?) {
        binding.optionContent.text = content
    }

    /**
     * 获取选项内容
     */
    fun getContent(): String {
        return binding.optionContent.text.toString()
    }

    /**
     * 清空数据
     */
    fun clearContent() {
        binding.optionContent.text = ""
        binding.optionMoreBtn.visibility = View.GONE
    }
}