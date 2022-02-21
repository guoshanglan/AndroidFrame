package android.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.WindowInsets

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2020/8/21 09:03
 *    desc   : 键盘弹出多fragment页面自适应，多次分发WindowInsets ，系统对每个activity只会分发一次
 */
class WindowInsetsFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        if (child.fitsSystemWindows) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                requestApplyInsets()
            } else {
                requestFitSystemWindows()
            }
        }
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        var result = super.onApplyWindowInsets(insets)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            for (index in 0 until childCount) {
                getChildAt(index).dispatchApplyWindowInsets(insets)
            }
        }
        return result
    }


}