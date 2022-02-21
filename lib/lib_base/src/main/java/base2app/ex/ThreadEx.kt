package base2app.ex

import android.os.Looper
import base2app.ex.ThreadEx.executor
import com.zhuorui.securities.base2app.tread.RxExecutorImpl


/**
 * date : 2020/12/11
 * dest : ThreadEx
 */

object ThreadEx {
    val executor = RxExecutorImpl()
}

inline fun singleIO(crossinline action: () -> Unit) {
    executor.singleIO { action() }
}

inline fun networkIO(crossinline action: () -> Unit) {
    executor.networkIO { action() }
}

inline fun computeIO(crossinline action: () -> Unit) {
    executor.computationIO { action() }
}

inline fun mainThread(crossinline action: () -> Unit) {
    if (isRunInUIThread()) {
        action()
    } else {
        executor.mainThread { action() }
    }
}

fun isRunInUIThread(): Boolean {
    return Thread.currentThread() === Looper.getMainLooper().thread
}
