package com.zhuorui.commonwidget;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * 自动调整字体大小适配屏幕的TextView
 * * Create by pengxinaglin on 2018/8/29.
 */
public class ZRAutoSizeTextView extends AppCompatTextView {
    private static final String KEY_DESIGN_HEIGHT = "design_height";
    private static final String KEY_DESIGN_WIDTH = "design_width";
    private int baseScreenWidth = 480;
    private int baseScreenHeight = 720;

    public ZRAutoSizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getMetaData(context);
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.ZRAutoSizeTextView);//获得属性值
        float px = type.getFloat(R.styleable.ZRAutoSizeTextView_textSizePx, 25f);
        Log.d("LOGCAT", "px:" + px);
        Log.d("LOGCAT", "baseScreenHeight:" + baseScreenHeight);
        Log.d("LOGCAT", "baseScreenWidth:" + baseScreenWidth);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getFontSize(px));
        type.recycle();
    }

    private void getMetaData(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(context
                    .getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                baseScreenWidth = (int) applicationInfo.metaData.get(KEY_DESIGN_WIDTH);
                baseScreenHeight = (int) applicationInfo.metaData.get(KEY_DESIGN_HEIGHT);
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(
                    "you must set " + KEY_DESIGN_HEIGHT + " and " + KEY_DESIGN_WIDTH + "  in your manifest file.", e);
        }
    }

    /**
     * @param textSize 设计稿中的dp大小
     */
    @Override
    public void setTextSize(float textSize) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getFontSize(textSize));
    }

    /**
     * 获取当前手机屏幕分辨率，然后根据和设计图的比例对照换算实际字体大小
     *
     * @param textSize
     * @return
     */
    private int getFontSize(float textSize) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        float screenHeight = dm.heightPixels;
        float screenWidth = dm.widthPixels;
        float scaleH = screenHeight / baseScreenHeight;
        float scaleW = screenWidth / baseScreenWidth;
        float scale = scaleH < scaleW ? scaleW : scaleH;
        return (int) (textSize * scale);
    }
}