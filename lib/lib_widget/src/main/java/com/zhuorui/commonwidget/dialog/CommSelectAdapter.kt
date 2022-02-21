package com.zhuorui.commonwidget.dialog

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import base2app.adapter.BaseListAdapter
import base2app.ex.color
import base2app.ex.setSafeViewClickListener
import base2app.viewbinding.viewBinding
import com.zhuorui.commonwidget.R
import com.zhuorui.commonwidget.databinding.DialogItemCommSelectBinding


/**
 * @date 2021/10/29
 * @desc 交易通用选择项Adapter
 */
class CommSelectAdapter(
    defaultIndex: Int,
    val onSelectedListener: (Int, String) -> Unit
) :
    BaseListAdapter<String>() {
    private var selectedPosition = 0

    init {
        this.selectedPosition = defaultIndex
    }

    override fun createViewHolderByParent(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return ViewHolder(inflateView(parent, R.layout.dialog_item_comm_select))
    }

    inner class ViewHolder(itemView: View) :
        ListItemViewHolder<String>(itemView, true, false) {
        private val binding by viewBinding(DialogItemCommSelectBinding::bind)
        @SuppressLint("NotifyDataSetChanged")
        override fun bind(item: String?, itemIndex: Int) {
            binding.tvOption.text = item
            when (itemIndex) {
                selectedPosition -> {
                    binding.tvOption.setTextColor(color(R.color.brand_main_color))
                }
                itemCount - 1 -> {
                    binding.line.visibility = View.GONE
                }
                else -> {
                    binding.tvOption.setTextColor(color(R.color.main_content_text_color))
                }
            }
            itemView.setSafeViewClickListener {
                selectedPosition = this.bindingAdapterPosition
                getItem(itemIndex)?.let {
                    onSelectedListener.invoke(itemIndex, it)
                }
                notifyDataSetChanged()
            }
        }

    }

}