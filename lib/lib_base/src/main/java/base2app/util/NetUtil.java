package base2app.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by xieyingwu on 2017/12/25.
 * 网络工具类
 */

public final class NetUtil {
    private static final int NETWORK_NONE = 0; // 没有网络连接
    private static final int NETWORK_WIFI = 1; // wifi连接
    private static final int NETWORK_2G = 2; // 2G
    private static final int NETWORK_3G = 3; // 3G
    private static final int NETWORK_4G = 4; // 4G
    private static final int NETWORK_MOBILE = 5; // 手机流量

    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            return info != null && info.isConnected() && info.isAvailable() && NetworkInfo.State.CONNECTED.equals(info.getState());
        }
        return false;
    }

    /**
     * WiFi是否连接
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnect(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            return info != null && info.getType() == ConnectivityManager.TYPE_WIFI && info.isConnected() && info.isAvailable() && NetworkInfo.State.CONNECTED.equals(info.getState());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnect(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            return info != null && info.getType() == ConnectivityManager.TYPE_MOBILE && info.isConnected() && info.isAvailable() && NetworkInfo.State.CONNECTED.equals(info.getState());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前的运营商
     *
     * @return 运营商名字
     */
    public static String getOperatorName(Context context, String networkType) {
        if ("wifi".equals(networkType)) {
            return "unknown";
        }
        String ProvidersName = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return "unknown";
        }
        String IMSI = telephonyManager.getSimOperator();
        Log.i("qweqwes", "运营商代码" + IMSI);
        if (IMSI != null) {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                ProvidersName = "MOBILE";//中国移动
            } else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) {
                ProvidersName = "UNICOM";//中国联通
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "TELECOM";//中国电信
            }
            return ProvidersName;
        } else {
            return "unknown";
        }
    }
}
