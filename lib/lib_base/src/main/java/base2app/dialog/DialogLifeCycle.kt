package base2app.dialog

/**
 * @desc Dialog的生命周期
 */
interface DialogLifeCycle {

    /**
     * dialog弹出
     */
    fun onDialogShow(dialogName: String)

    /**
     * dialog关闭
     */
    fun onDialogDismiss(dialogName: String)

    fun onFragmentResume()

}