package com.zhuorui.commonwidget.adapter

import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import base2app.adapter.BaseListAdapter
import base2app.ex.color
import base2app.ex.dp2px
import base2app.viewbinding.viewBinding
import com.zhuorui.commonwidget.R
import com.zhuorui.commonwidget.databinding.ItemActionSheetDialogBinding
import com.zhuorui.commonwidget.model.SheetItem


/**
 * @date 2020/7/15 17:54
 * @desc
 */
class ActionSheetDialogAdapter : BaseListAdapter<SheetItem>() {

    override fun createViewHolderByParent(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return ViewHolder(
            inflateView(parent, R.layout.item_action_sheet_dialog),
            needClick = true,
            needLongClick = false
        )
    }

    inner class ViewHolder(v: View?, needClick: Boolean, needLongClick: Boolean) :
        ListItemViewHolder<SheetItem>(v, needClick, needLongClick) {

        private val binding by viewBinding(ItemActionSheetDialogBinding::bind)

        override fun bind(item: SheetItem?, itemIndex: Int) {
            with(binding) {
                tvItemName.text = item?.item
                tvItemName.isSelected = item?.select == true
                if (false == item?.enabled) {
                    itemView.isEnabled = false
                    itemView.isClickable = false
                    val itemName = item.item
                    if (itemName!!.contains("\n")) {
                        val spannableString = SpannableString(itemName)
                        val frontSpan = AbsoluteSizeSpan(16f.dp2px().toInt())
                        val behindSpan = AbsoluteSizeSpan(12f.dp2px().toInt())
                        spannableString.setSpan(
                            frontSpan,
                            0,
                            itemName.indexOf("\n"),
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        spannableString.setSpan(
                            behindSpan,
                            itemName.indexOf("\n"),
                            itemName.length,
                            Spanned.SPAN_INCLUSIVE_INCLUSIVE
                        )
                        tvItemName.text = spannableString
                    }

                    tvItemName.setTextColor(color(R.color.useless_text_color))
                }
            }
        }
    }
}