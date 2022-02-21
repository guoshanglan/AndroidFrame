package com.zhuorui.commonwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;


import base2app.BaseApplication;
import base2app.ex.PixelExKt;
import base2app.ex.ThreadExKt;

/**
 * date   : 2019/8/21 10:10
 * desc   : 屏幕中央带有图标提示的Toast
 */
public class ScreenCentralStateToast {

    @SuppressLint("InflateParams")
    private static void realShowToast(@DrawableRes int resId, String message) {
        //加载Toast布局
        View toastRoot = LayoutInflater.from(BaseApplication.Companion.getContext()).inflate(R.layout.view_toast, null);
        //初始化布局控件
        if (resId != 0) {
            ImageView iv_icon = toastRoot.findViewById(R.id.iv_icon);
            iv_icon.setImageResource(resId);
        }
        TextView tv_message = toastRoot.findViewById(R.id.tv_message);
        //为控件设置属性
        tv_message.setText(message);
        Context context = BaseApplication.Companion.getContext();
        //Toast的初始化
        Toast toastStart = new Toast(context);
        boolean isPortrait = false;
        if (context != null) {
            Resources resources = context.getResources();
            isPortrait = resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        }
        if (isPortrait) {
            toastStart.setGravity(Gravity.CENTER, 0, (int) -PixelExKt.dp2px(70));
        } else {
            toastStart.setGravity(Gravity.CENTER, 0, 0);
        }
        toastStart.setDuration(Toast.LENGTH_SHORT);
        toastStart.setView(toastRoot);
        toastStart.show();
    }

    /**
     * 弹出自定义提示
     */
    public static void show(@DrawableRes final int resId, final String message) {
        ThreadExKt.mainThread(() -> {
            realShowToast(resId, message);
            return null;
        });
    }

    /**
     * 弹出成功提示
     */
    public static void showSuccess(String message) {
        show(0, message);
    }

    /**
     * 弹出失败提示
     */
    public static void showFail(String message) {
        show(R.mipmap.ic_fail, message);
    }
}