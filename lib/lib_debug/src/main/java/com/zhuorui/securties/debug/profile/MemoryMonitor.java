package com.zhuorui.securties.debug.profile;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Process;

import base2app.BaseApplication;

/**
 * MemoryMonitor
 *
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time 17:39
 */
public class MemoryMonitor {

    private ActivityManager mActivityManager;

    public MemoryMonitor() {
        this.mActivityManager = (ActivityManager) BaseApplication.Companion.getBaseApplication().getSystemService(Context.ACTIVITY_SERVICE);
    }

    float getMemoryData() {
        float mem = 0.0F;
        try {
            Debug.MemoryInfo memInfo = null;
            //28 为Android P
            if (Build.VERSION.SDK_INT > 28) {
                // 统计进程的内存信息 totalPss
                memInfo = new Debug.MemoryInfo();
                Debug.getMemoryInfo(memInfo);
            } else {
                //As of Android Q, for regular apps this method will only return information about the memory info for the processes running as the caller's uid;
                // no other process memory info is available and will be zero. Also of Android Q the sample rate allowed by this API is significantly limited, if called faster the limit you will receive the same data as the previous call.

                Debug.MemoryInfo[] memInfos = mActivityManager.getProcessMemoryInfo(new int[]{Process.myPid()});
                if (memInfos != null && memInfos.length > 0) {
                    memInfo = memInfos[0];
                }
            }
            int totalPss = 0;
            if (memInfo != null) {
                totalPss = memInfo.getTotalPss();
            }
            if (totalPss >= 0) {
                // Mem in MB
                mem = totalPss / 1024.0F;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mem;
    }
}
