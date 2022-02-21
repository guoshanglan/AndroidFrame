package com.zhuorui.commonwidget.dialog

import android.graphics.Typeface
import android.view.View
import androidx.fragment.app.Fragment
import base2app.adapter.BaseListAdapter
import base2app.dialog.BaseBottomSheetsDialog
import base2app.viewbinding.viewBinding
import com.zhuorui.commonwidget.R
import com.zhuorui.commonwidget.adapter.ActionSheetDialogAdapter
import com.zhuorui.commonwidget.databinding.DialogActionSheetBinding
import com.zhuorui.commonwidget.model.SheetItem


/**
 * @date 2020/04/29$ $
 * @desc 多条目单选Dialog
 */
class ZRActionSheetDialog(fragment: Fragment) : BaseBottomSheetsDialog(fragment), View.OnClickListener,
    BaseListAdapter.OnClickItemCallback<SheetItem> {

    private var adapter: ActionSheetDialogAdapter? = null
    private var onItemClickListener: OnSheetItemClickListener? = null
    private val binding by viewBinding(DialogActionSheetBinding::bind)

    init {
        binding.tvCancel.setOnClickListener(this)
        adapter = ActionSheetDialogAdapter()
        binding.recyclerView.adapter = adapter
        adapter?.setClickItemCallback(this)
    }

//    override val dialogStyle: Int
//        get() = R.style.ActionSheetDialogStyle

    override val layout: Int
        get() = R.layout.dialog_action_sheet

    fun setTitle(title: String): ZRActionSheetDialog {
        binding.tvTitle.text = title
        return this
    }

    fun setTitleVisivle(visible: Int): ZRActionSheetDialog {
        binding.tvTitle.visibility = visible
        return this
    }

    fun setTitleColor(titleColor: Int): ZRActionSheetDialog {
        binding.tvTitle.setTextColor(titleColor)
        return this
    }

    fun setTitleStyle(typeFace: Typeface): ZRActionSheetDialog {
        binding.tvTitle.typeface = typeFace
        return this
    }

    fun setTitleSize(titleSize: Float): ZRActionSheetDialog {
        binding.tvTitle.textSize = titleSize
        return this
    }

    fun setDescriptionView(descriptionView: View): ZRActionSheetDialog {
        binding.layoutDescription.apply {
            visibility = View.VISIBLE
            addView(descriptionView)
        }
        return this
    }

    fun addSheetItems(items: List<SheetItem>): ZRActionSheetDialog {
        adapter?.items = items
        return this
    }

    interface OnSheetItemClickListener {
        fun onClick(itemIndex: Int, item: SheetItem?)
    }

    fun setOnItemClickListener(onItemClickListener: OnSheetItemClickListener): ZRActionSheetDialog {
        this.onItemClickListener = onItemClickListener
        return this
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_cancel -> {
                dismiss()
            }
        }
    }

    override fun onFragmentDestroy() {
        super.onFragmentDestroy()
        this.onItemClickListener = null
    }

    override fun onClickItem(itemIndex: Int, item: SheetItem?, v: View?) {
        dismiss()
        val items = adapter?.items
        items?.forEachIndexed { index, sheetItem ->
            items[index].select = index == itemIndex
        }
        onItemClickListener?.onClick(itemIndex, item)
    }

}