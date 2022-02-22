package com.zhuorui.securties.debug.info

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.LinearLayoutCompat
import base2app.ui.activity.AbsActivity
import base2app.util.AppUtil
import base2app.util.DeviceUtil
import base2app.util.StatusBarUtil
import base2app.viewbinding.viewBinding
import com.zhuorui.securties.debug.R
import com.zhuorui.securties.debug.databinding.DebugInfoActivityBinding
import java.io.File
import java.math.BigDecimal
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*


class ZRInfoConfigActivity : AbsActivity() {
    private val binding by viewBinding(DebugInfoActivityBinding::bind)

    override val layout: Int
        get() = R.layout.debug_info_activity

    override val acContentRootViewId: Int
        get() = R.id.root_layout

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val barHeight = StatusBarUtil.getStatusBarHeight(this)
        binding.back.setOnClickListener { finish() }
        (binding.titleBar.layoutParams as LinearLayoutCompat.LayoutParams).topMargin = barHeight

        binding.recyclerview.adapter = InfoConfigAdapter().apply {

            this.datas.let { datas ->
                try {
                    val pm: PackageManager = this@ZRInfoConfigActivity.packageManager
                    val pi = pm.getPackageInfo(
                        this@ZRInfoConfigActivity.packageName,
                        PackageManager.GET_CONFIGURATIONS
                    )
                    datas.add(SysInfoItem("包名", pi.packageName))
                    datas.add(SysInfoItem("应用版本名", pi.versionName))
                    datas.add(SysInfoItem("应用版本号", pi.versionCode.toString()))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        datas.add(
                            SysInfoItem(
                                "最低系统版本号",
                                this@ZRInfoConfigActivity.applicationInfo.minSdkVersion.toString()
                            )
                        )
                    }
                    datas.add(
                        SysInfoItem(
                            "目标系统版本号",
                            this@ZRInfoConfigActivity.applicationInfo.targetSdkVersion.toString()
                        )
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                datas.add(
                    SysInfoItem(
                        "手机型号",
                        Build.MANUFACTURER + " " + Build.MODEL
                    )
                )
                datas.add(
                    SysInfoItem(
                        "系统版本",
                        Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ")"
                    )
                )

                try {
                    datas.add(
                        SysInfoItem(
                            "分辨率",
                            AppUtil.phoneRealScreenWidth.toString() + "x" + AppUtil.phoneRealScreenHeight.toString()
                        )
                    )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                try {
                    datas.add(
                        SysInfoItem(
                            "屏幕尺寸",
                            "" + getScreenInch(this@ZRInfoConfigActivity)
                        )
                    )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                try {
                    datas.add(
                        SysInfoItem(
                            "ROOT",
                            isDeviceRooted().toString()
                        )
                    )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                try {
                    datas.add(
                        SysInfoItem(
                            "DENSITY",
                            Resources.getSystem().displayMetrics.density.toString()
                        )
                    )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                try {
                    val dress = getIPAddress(true)
                    datas.add(
                        SysInfoItem(
                            "IP",
                            dress ?: "null"
                        )
                    )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                try {
                    val uid = DeviceUtil.getDeviceUuid()
                    datas.add(
                        SysInfoItem(
                            "设备唯一标识符",
                            uid ?: "null"
                        )
                    )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    /**
     * 获取屏幕尺寸
     *
     * @param context
     * @return
     */
    fun getScreenInch(context: Activity): Double {
        var inch = 0.0
        try {
            var realWidth = 0
            var realHeight = 0
            val display = context.windowManager.defaultDisplay
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)
            if (Build.VERSION.SDK_INT >= 17) {
                val size = Point()
                display.getRealSize(size)
                realWidth = size.x
                realHeight = size.y
            } else if (Build.VERSION.SDK_INT < 17
                && Build.VERSION.SDK_INT >= 14
            ) {
                val mGetRawH = Display::class.java.getMethod("getRawHeight")
                val mGetRawW = Display::class.java.getMethod("getRawWidth")
                realWidth = mGetRawW.invoke(display) as Int
                realHeight = mGetRawH.invoke(display) as Int
            } else {
                realWidth = metrics.widthPixels
                realHeight = metrics.heightPixels
            }
            inch = formatDouble(
                Math.sqrt((realWidth / metrics.xdpi * (realWidth / metrics.xdpi) + realHeight / metrics.ydpi * (realHeight / metrics.ydpi)).toDouble()),
                1
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return inch
    }

    /**
     * Double类型保留指定位数的小数，返回double类型（四舍五入）
     * newScale 为指定的位数
     */
    private fun formatDouble(d: Double, newScale: Int): Double {
        val bd = BigDecimal(d)
        return bd.setScale(newScale, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    /**
     * Return whether device is rooted.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDeviceRooted(): Boolean {
        val su = "su"
        val locations = arrayOf(
            "/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
            "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/",
            "/system/sbin/", "/usr/bin/", "/vendor/bin/"
        )
        for (location in locations) {
            if (File(location + su).exists()) {
                return true
            }
        }
        return false
    }


    /**
     * Return the ip address.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param useIPv4 True to use ipv4, false otherwise.
     * @return the ip address
     */
    @RequiresPermission(permission.INTERNET)
    fun getIPAddress(useIPv4: Boolean): kotlin.String? {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            val adds = LinkedList<InetAddress>()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp || ni.isLoopback) continue
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement())
                }
            }
            for (add in adds) {
                if (!add.isLoopbackAddress) {
                    val hostAddress = add.hostAddress
                    val isIPv4 = hostAddress.indexOf(':') < 0
                    if (useIPv4) {
                        if (isIPv4) return hostAddress
                    } else {
                        if (!isIPv4) {
                            val index = hostAddress.indexOf('%')
                            return if (index < 0) hostAddress.uppercase(Locale.getDefault()) else hostAddress.substring(
                                0,
                                index
                            ).uppercase(Locale.getDefault())
                        }
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return ""
    }
}