package base2app.util

import android.graphics.Rect
import android.view.ViewGroup
import android.view.ViewTreeObserver

/**
 * 监听软键盘状态辅助类
 */
class SoftKeyboardStateHelper @JvmOverloads constructor(
    private val parentView: ViewGroup,
    private var isSoftKeyboardOpened: Boolean = false
) : ViewTreeObserver.OnGlobalLayoutListener {

    private var mViewTreeObserver: ViewTreeObserver? = null


    init {
        mViewTreeObserver = parentView.rootView.viewTreeObserver
        mViewTreeObserver?.addOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        val r = Rect()
        parentView.rootView.getWindowVisibleDisplayFrame(r)
        val heightDiff = parentView.rootView.height - r.bottom
        if (!isSoftKeyboardOpened && heightDiff > 200) {
            isSoftKeyboardOpened = true
            notifyOnSoftKeyboardOpened(heightDiff)
        } else if (isSoftKeyboardOpened && heightDiff < 200) {
            isSoftKeyboardOpened = false
            notifyOnSoftKeyboardClosed()
        }
    }

    fun removeSoftKeyboardStateListener() {
        mViewTreeObserver?.takeIf { it.isAlive }?.removeOnGlobalLayoutListener(this)
        onSoftKeyboardStateListener = null
    }


    private var onSoftKeyboardStateListener: OnSoftKeyboardStateListener? = null


    fun addSoftKeyboardStateListener(listener: OnSoftKeyboardStateListener) {
        this.onSoftKeyboardStateListener = listener
    }


    private fun notifyOnSoftKeyboardOpened(keyboardHeightInPx: Int) {
        onSoftKeyboardStateListener?.onSoftKeyboardOpened(keyboardHeightInPx)
    }

    private fun notifyOnSoftKeyboardClosed() {
        onSoftKeyboardStateListener?.onSoftKeyboardClosed()
    }

    interface OnSoftKeyboardStateListener {
        fun onSoftKeyboardOpened(keyboardHeightInPx: Int)

        fun onSoftKeyboardClosed()
    }
}
