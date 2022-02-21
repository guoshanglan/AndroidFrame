package com.zhuorui.commonwidget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * @date 2020/12/16 20:29
 * @desc
 */
class ZRImageCheckBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    init {
        setOnClickListener {
            if (isEnabled) {
                isSelected = !isSelected
                onSelectedListener?.invoke(isSelected)
            }
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (!enabled) {
            setImageResource(R.mipmap.ic_check_disable)
        }
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        if (isEnabled) {
            if (selected) {
                setImageResource(R.mipmap.ic_check_selected)
            } else {
                setImageResource(R.mipmap.ic_check_unselect)
            }
        }
    }

    private var onSelectedListener: ((hasSelected: Boolean) -> Unit)? = null

    fun setOnSelectedListener(onSelectedListener: ((hasSelected: Boolean) -> Unit)) {
        this.onSelectedListener = onSelectedListener
    }
}