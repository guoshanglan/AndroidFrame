package base2app.network.interceptor

import android.provider.Settings
import base2app.BaseApplication
import base2app.util.AppUtil
import base2app.util.DeviceUtil
import com.zrlib.lib_service.service
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.URLEncoder

/**
 * date   : 2019-05-20 14:13
 * desc   : 在网络请求拦截器中添加Header
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val osType = "android"
        val versionName = AppUtil.getVersionName(BaseApplication.baseApplication)
        val buildNumber = AppUtil.getBulidName(BaseApplication.baseApplication)
        val appVersion = "$versionName($buildNumber)"
        val osVersion = DeviceUtil.getSystemVersion()
        val deviceId = DeviceUtil.getDeviceUuid()

        /**
         * 设备型号，名称有可能是中文，Header不支持传输中文，需要转码
         * 通过蓝牙名称获取用户自定义设备名称，获取不到则取产商+设备型号
         */
        val deviceModel = URLEncoder.encode(DeviceUtil.getDeviceModel(), "UTF-8")
        val bluetoothName = Settings.Secure.getString(
            BaseApplication.baseApplication.contentResolver,
            "bluetooth_name"
        )
        val deviceName = URLEncoder.encode(
            bluetoothName ?: "${DeviceUtil.getManufacturer()} $deviceModel",
            "UTF-8"
        )

        val builder = chain.request().newBuilder().apply {

            addHeader("osType", osType)
            addHeader("osVersion", osVersion)
            addHeader("lang", "zh_CN")
            addHeader("appVersion", appVersion)
            addHeader("deviceId", deviceId)
            addHeader("deviceName", deviceName)
            addHeader("deviceModel", deviceModel)
        }

        /*加入自定义的header*/
        val request = builder.build()
        return chain.proceed(request)
    }
}