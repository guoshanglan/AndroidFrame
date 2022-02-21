package base2app.ex

import base2app.infra.LogInfra
import base2app.util.JsonUtil


/**
 * date : 2020/4/10
 * dest : LogEx 日志扩展函数集合
 * 正常不加 tag 的话 会直接输入对应 class 的名称 ;
 */

fun Any.gson(): String {
    return JsonUtil.toJson(this)
}

fun Any.gsonFormat(): String {
    return JsonUtil.jsonFormat(gson())
}

fun logd(tag: String, message: String) {
    Log.d(tag, message)
}

fun Any.logd(message: String) {
    Log.d(this::class.java.simpleName, message)
}

fun Any.logd(ob: Any) {
    Log.d(this::class.java.simpleName, ob.gsonFormat())
}

fun loge(tag: String, msg: String?, tr: Throwable?) {
    Log.e(tag, msg, tr)
}

fun loge(tag: String, message: String) {
    Log.e(tag, message)
}

fun Any.loge(message: String) {
    Log.e(this::class.java.simpleName, message)
}

fun Any.loge(ob: Any) {
    Log.e(this::class.java.simpleName, ob.gsonFormat())
}

fun logw(tag: String, message: String) {
    Log.w(tag, message)
}

fun Any.logw(message: String) {
    Log.w(this::class.java.simpleName, message)
}

fun Any.logw(ob: Any) {
    Log.w(this::class.java.simpleName, ob.gsonFormat())
}

fun logi(tag: String, message: String) {
    Log.i(tag, message)
}

fun Any.logi(message: String) {
    Log.i(this::class.java.simpleName, message)
}

fun Any.logi(ob: Any) {
    Log.i(this::class.java.simpleName, ob.gsonFormat())
}

fun logv(tag: String, message: String) {
    Log.v(tag, message)
}

fun Any.logv(message: String) {
    Log.v(this::class.java.simpleName, message)
}

fun Any.logv(ob: Any) {
    Log.v(this::class.java.simpleName, ob.gsonFormat())
}

fun Any.logTask(tag: String) {
    val stacks = Thread.currentThread().stackTrace
    for (stack in stacks) {
        if (stack.className.contains("com.zhuorui.securities"))
            Log.d(
                this::class.java.simpleName,
                "TAG : $tag -----${stack.className} --------------- -${stack.methodName} ---"
            )
    }
}

/**
 * 目前使用已过期 , 请使用 [com.zhuorui.securities.base2app.ex.LogExKt]
 * *  logd("") loge("") 等等扩展方法
 * V和D只进行android输出
 * 其他会依据设置的LEVEL进行输出和存储文件
 */
internal object Log {
    private fun log(level: Int): Boolean {
        return LogInfra.LEVEL > level
    }

    fun v(tag: String, msg: String?) {
        if (!LogInfra.OPEN_LOG || log(android.util.Log.VERBOSE)) return
        android.util.Log.v(LogInfra.TAG + " " + tag, msg!!)
    }

    fun v(tag: String, msg: String?, tr: Throwable?) {
        if (!LogInfra.OPEN_LOG || log(android.util.Log.VERBOSE)) return
        android.util.Log.v(LogInfra.TAG + " " + tag, msg, tr)
    }

    fun d(tag: String, msg: String?) {
        if (!LogInfra.OPEN_LOG || log(android.util.Log.DEBUG)) return
        android.util.Log.d(LogInfra.TAG + " " + tag, msg!!)
    }

    fun d(tag: String, msg: String?, tr: Throwable?) {
        if (!LogInfra.OPEN_LOG || log(android.util.Log.DEBUG)) return
        android.util.Log.d(LogInfra.TAG + " " + tag, msg, tr)
    }

    fun i(tag: String, msg: String?) {
        if (!LogInfra.OPEN_LOG || log(android.util.Log.INFO)) return
        android.util.Log.i(LogInfra.TAG + " " + tag, msg!!)
    }

    fun i(tag: String, msg: String?, tr: Throwable?) {
        if (!LogInfra.OPEN_LOG || log(android.util.Log.INFO)) return
        android.util.Log.i(LogInfra.TAG + " " + tag, msg, tr)
    }

    fun w(tag: String, msg: String?) {
        if (!LogInfra.OPEN_LOG || log(android.util.Log.WARN)) return
        android.util.Log.w(LogInfra.TAG + " " + tag, msg!!)
    }

    fun w(tag: String, tr: Throwable?) {
        if (!LogInfra.OPEN_LOG || log(android.util.Log.WARN)) return
        android.util.Log.w(LogInfra.TAG + " " + tag, tr)
    }

    fun w(tag: String, msg: String?, tr: Throwable?) {
        if (!LogInfra.OPEN_LOG || log(android.util.Log.WARN)) return
        android.util.Log.w(LogInfra.TAG + " " + tag, msg, tr)
    }

    fun e(tag: String, msg: String?) {
        if (!LogInfra.OPEN_LOG || log(android.util.Log.ERROR)) return
        android.util.Log.e(LogInfra.TAG + " " + tag, msg!!)
    }

    fun e(tag: String, tr: Throwable?) {
        if (!LogInfra.OPEN_LOG || log(android.util.Log.ERROR)) return
        android.util.Log.e(LogInfra.TAG + " " + tag, "", tr)
    }

    fun e(tag: String, msg: String?, tr: Throwable?) {
        if (!LogInfra.OPEN_LOG || log(android.util.Log.ERROR)) return
        android.util.Log.e(LogInfra.TAG + " " + tag, msg, tr)
    }
}
