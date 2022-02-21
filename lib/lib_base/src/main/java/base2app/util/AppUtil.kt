package base2app.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import android.view.WindowManager
import base2app.BaseApplication
import base2app.ex.logd
import base2app.ex.text
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


/**
 * App相关信息获取
 */

object AppUtil {
    var screenHeight: Int = 0
        private set
    var screenWidth: Int = 0
        private set
    var dpi: Float = 0.toFloat()
        private set
    var phoneScreenHeight: Int = 0
        private set
    var phoneScreenWidth: Int = 0
        private set

    var phoneRealScreenHeight: Int = 0
        private set
    var phoneRealScreenWidth: Int = 0
        private set

    /**
     * 需要在Application中初始化
     *
     * @param context
     */
    fun init(context: Context) {
        val displayMetrics = context.resources.displayMetrics
        screenHeight = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels
        dpi = displayMetrics.density
        val dm = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val defaultDisplay = windowManager.defaultDisplay
        defaultDisplay.getMetrics(dm)
        phoneScreenHeight = dm.heightPixels
        phoneScreenWidth = dm.widthPixels
        val dmReal = DisplayMetrics()
        defaultDisplay.getRealMetrics(dmReal)
        phoneRealScreenHeight = dmReal.heightPixels
        phoneRealScreenWidth = dmReal.widthPixels
    }


    fun getPackageInfo(context: Context): PackageInfo? {
        try {
            val pm = context.packageManager
            return pm.getPackageInfo(context.packageName, PackageManager.GET_CONFIGURATIONS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getVersionCode(context: Context): Long? {
        val packageInfo = getPackageInfo(context) ?: return null
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            return packageInfo.versionCode.toLong()
        }
    }

    fun getVersionName(context: Context): String? {
        val packageInfo = getPackageInfo(context) ?: return null
        return packageInfo.versionName
    }

    fun getAppName(context: Context): String? {
        try {
            val packageInfo = getPackageInfo(context) ?: return null
            val labelRes = packageInfo.applicationInfo.labelRes
            return text(labelRes)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }


    /**
     * 判断当前进程是否为主进程
     */
    fun isMainProcess(context: Context): Boolean {
        var reader: BufferedReader? = null
        try {
            val mainProcessName = context.packageName
            val pid = android.os.Process.myPid()
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName: String = reader.readLine()
            if (processName.isNotEmpty()) {
                processName = processName.trim { it <= ' ' }
            }
            return mainProcessName == processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return false
    }

    /**
     * Return whether the navigation bar visible.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isSupportNavBar(): Boolean {
        val wm = BaseApplication.baseApplication.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                ?: return false
        val display = wm.defaultDisplay
        val size = Point()
        val realSize = Point()
        display.getSize(size)
        display.getRealSize(realSize)
        return realSize.y !== size.y || realSize.x !== size.x
        val menu = ViewConfiguration.get(BaseApplication.baseApplication).hasPermanentMenuKey()
        val back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        return !menu && !back
    }

    /**
     * 打印方法调用者
     *
     * @param object
     */
    fun getCaller(obj: Any,isFilter:Boolean? = true) {
        logd("getCaller>>", "--------------------------------------------------------------")
        val code = obj.hashCode()
        if (isFilter == true) {
            logd("getCaller>>", "$code $obj")
            val stack = Throwable().stackTrace
            for (i in 2 until stack.size) {
                val s = stack[i]
                val className = s.className
                if (isFilter != true || className.startsWith("com.zhuorui")) {
                    val txt = String.format(
                        "%d[%d]:%s.%s(%s %d)",
                        code,
                        i,
                        className,
                        s.methodName,
                        s.fileName,
                        s.lineNumber
                    )
                    logd("getCaller>>", txt)
                }
            }
        } else {
            Log.d("getCaller>>", "$code $obj", Throwable())
        }
        logd("getCaller>>", "--------------------------------------------------------------")
    }

    /**
     * 获取APP Bulid 版本号
     */
    fun getBulidName(context: Context):String{
        val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        val channel = String.format("%02d", appInfo.metaData.getInt("channel", 0))
        val packetNumber = String.format("%03d", appInfo.metaData.getInt("packet_number", 1))
        return "$channel$packetNumber"
    }

    /**
     * 输入密码安全键盘是否和Toast冲突
     * 目前只有华为9.0后会
     */
    fun isKeyboardToastConflict():Boolean{
        return (Build.MANUFACTURER.equals("HUAWEI",true) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
    }
}