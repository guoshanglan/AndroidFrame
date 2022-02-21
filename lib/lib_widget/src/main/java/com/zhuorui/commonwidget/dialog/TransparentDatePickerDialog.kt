package com.zhuorui.commonwidget.dialog

import android.view.View
import androidx.fragment.app.Fragment
import com.zhuorui.commonwidget.R

/**
 *    date   : 2019-08-21 18:33
 *    desc   : 日期选择对话框，弹出屏幕不会变暗
 */
class TransparentDatePickerDialog(fragment: Fragment) : DatePickerDialog(fragment),
    View.OnClickListener {

    override val dialogStyle: Int
        get() = R.style.DialogTransparent
}