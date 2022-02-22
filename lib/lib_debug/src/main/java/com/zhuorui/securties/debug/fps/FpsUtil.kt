package com.zhuorui.securties.debug.fps

import java.text.SimpleDateFormat
import java.util.*

/**
 * FpsUtil
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  9:43
 */


fun ms2Date(ms: Long): String {

    val date = Date(ms)
    val format = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault())
    return format.format(date)
}