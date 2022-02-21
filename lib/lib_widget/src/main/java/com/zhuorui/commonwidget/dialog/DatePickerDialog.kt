package com.zhuorui.commonwidget.dialog

import android.content.DialogInterface
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import base2app.dialog.BaseBottomSheetsDialog
import base2app.ex.color
import base2app.ex.dp2px
import base2app.viewbinding.viewBinding
import com.zhuorui.commonwidget.R
import com.zhuorui.commonwidget.databinding.DialogDatePickerBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 *    date   : 2019-08-21 18:33
 *    desc   : 日期选择对话框，弹出屏幕会变暗
 */
open class DatePickerDialog(fragment: Fragment) : BaseBottomSheetsDialog(fragment),
    View.OnClickListener {

    var format: String = "yyyy-MM-dd"
    var listener: OnDateSelectedListener? = null

    override val layout: Int
        get() = R.layout.dialog_date_picker

    private val binding by viewBinding(DialogDatePickerBinding::bind)

    init {
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(true)
        binding.confirm.setOnClickListener(this)
        binding.cancel.setOnClickListener(this)
        binding.stMianTopMarketStr.setOnClickListener(this)
        binding.picker.apply {
            setTextSize(16f.dp2px().toInt())//设置一般列表文字大小
            setSelectedItemTextSize(16f.dp2px().toInt())//设置被选中的文字大小
            setTextColor(color(R.color.dialog_daynight_text_color_03)) //设置一般列表文字颜色
            setSelectedItemTextColor(color(R.color.dialog_daynight_text_color_01)) //设置一般列表文字颜色
            setCurtainBorderColor(color(R.color.dialog_daynight_division_background))
            setCurtainColor(color(R.color.dialog_daynight_background))
        }

    }

    fun setCurrentData(timeInMillis: Long) {
        val tms = Calendar.getInstance()
        tms.timeInMillis = timeInMillis
        val y: Int = tms.get(Calendar.YEAR)
        val m: Int = tms.get(Calendar.MONTH) + 1
        val d: Int = tms.get(Calendar.DAY_OF_MONTH)
        binding.picker.setDate(y, m, d)
    }

    fun setSourceDate(dateMap: HashMap<Int, HashMap<Int, ArrayList<Int>>>) {
        binding.picker.dayPicker.setSourceDate(dateMap)
    }

    fun setCurrentData(timeStr: String, format: String?) {
        this.format = format.toString()
        if (TextUtils.isEmpty(timeStr)) return
        setCurrentData(SimpleDateFormat(format).parse(timeStr).time)
    }

    fun setMaxDate(date: Long) {
        binding.picker.setMaxDate(date)
    }

    fun setMinDate(date: Long) {
        binding.picker.setMinDate(date)
    }

    fun setOnDateSelectedListener(l: OnDateSelectedListener?) {
        listener = l
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDismiss(isClickOutDismiss)
    }

    override fun onFragmentDestroy() {
        super.onFragmentDestroy()
        listener = null
    }

    interface OnDateSelectedListener {
        fun onDateSelected(date: String)
        fun onDismiss(isClickOutDismiss: Boolean)
    }

    private var isClickOutDismiss = true

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.confirm -> {
                isClickOutDismiss = false
                dismiss()
                listener?.onDateSelected(binding.picker.getDate(SimpleDateFormat(format)))
            }
            R.id.cancel -> {
                isClickOutDismiss = false
                dismiss()
            }
        }
    }
}

