package com.zhuorui.securties.debug.crashcanary;

import android.content.Context;
import android.util.Log;

import com.zhuorui.securties.debug.crashcanary.crash.CrashFactory;
import com.zhuorui.securties.debug.crashcanary.crash.CrashHelper;
import com.zhuorui.securties.debug.crashcanary.crash.db.dao.impl.ICrashDao;


/**
 * Created by 谷闹年 on 2019/8/28.
 */
public class CrashCanary {
    private CrashCanary() {
        throw new AssertionError();
    }


    private static ICrashDao crashDao;

    public static void install(Context context) {
        Log.d("CrashCanary", "install init success");
        crashDao = new ICrashDao(context);
        Thread.setDefaultUncaughtExceptionHandler(new CrashHelper(new ICrashDao(context), context));
        CrashFactory.setEnabled(context, CrashInfoActivity.class, true);
    }

    public static ICrashDao getCrashDao() {
        return crashDao;
    }
}


