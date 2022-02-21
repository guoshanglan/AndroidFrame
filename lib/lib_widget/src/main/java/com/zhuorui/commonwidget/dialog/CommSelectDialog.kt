package com.zhuorui.commonwidget.dialog

import android.view.View
import androidx.fragment.app.Fragment
import base2app.dialog.BaseBottomSheetsDialog
import base2app.viewbinding.viewBinding
import com.zhuorui.commonwidget.R
import com.zhuorui.commonwidget.databinding.DialogCommSelectBinding


/**
 * @date 2021/10/29
 * @desc 交易通用选择项Dialog
 */
class CommSelectDialog(fragment: Fragment, private val curSelectIndex: Int = 0) :
    BaseBottomSheetsDialog(fragment) {
    private val binding by viewBinding(DialogCommSelectBinding::bind)
    private var mOptionsAdapter: CommSelectAdapter? = null
    private var mOnSelectResultListener: ((selectedIndex: Int) -> Unit)? = null

    override val layout: Int
        get() = R.layout.dialog_comm_select

    fun setOnResultListener(onSingleSelectResultListener: ((selectedIndex: Int) -> Unit)): CommSelectDialog {
        this.mOnSelectResultListener = onSingleSelectResultListener
        return this
    }

    fun setTitle(title: String): CommSelectDialog {
        binding.layoutTitle.visibility = View.VISIBLE
        binding.tvTitle.text = title
        return this
    }

    fun setMaxHeight(height:Int):CommSelectDialog{
        binding.root.layoutParams.height = height
        return this
    }

    fun setOptions(options: List<String>): CommSelectDialog {
        if (mOptionsAdapter == null) {
            binding.recyclerView.adapter =
                CommSelectAdapter(curSelectIndex) { index, _ ->
                    dismiss()
                    mOnSelectResultListener?.invoke(index)
                }.apply {
                    mOptionsAdapter = this
                }
            binding.recyclerView.adapter = mOptionsAdapter
        }
        binding.recyclerView.scrollToPosition(curSelectIndex)
        mOptionsAdapter?.items = options
        return this
    }


    fun setOnDismissListener(onDismissListener: () -> Unit): CommSelectDialog {
        this.dialog?.setOnDismissListener {
            onDismissListener.invoke()
        }
        return this
    }

    override fun onFragmentDestroy() {
        super.onFragmentDestroy()
        mOnSelectResultListener = null
    }

}