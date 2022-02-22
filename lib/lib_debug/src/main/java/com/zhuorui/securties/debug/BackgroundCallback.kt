package com.zhuorui.securties.debug


/**
 * Background
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  13:34
 */
interface BackgroundCallback {
    /**
     * 从后台切到前台
     */
    fun onBack2foreground()

    /**
     * 从前台切到后台
     */
    fun onFore2background()
}