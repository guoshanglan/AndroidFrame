package base2app.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import base2app.BaseApplication;

import com.example.lib_base.R;

import java.util.List;

import base2app.ex.LogExKt;

/**
 * app跳转工具类
 */
public class JumpUtil {
    private JumpUtil() {
    }

    private static final String TAG = "JumpUtil";

    public static void jump(Context context, Class<?> cls, Bundle bundle, int... flags) {
        LogExKt.logd(TAG, "context = " + context + ";cls:" + cls);
        Intent intent = new Intent(context, cls);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        if (flags != null && flags.length > 0) {
            for (int flag : flags) {
                intent.addFlags(flag);
            }
        }

        if (bundle != null)
            intent.putExtras(bundle);
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent);
        } else {
            LogExKt.logd("JumpUtil", "jump app is not exist!");
        }
    }

    public static void jump(Context context, Class<?> cla, int... flags) {
        jump(context, cla, null, flags);
    }

    public static void jump(Activity currentAc, Class<?> cla, boolean closeCurrentAc) {
        jump(currentAc, cla, null);
        if (closeCurrentAc) {
            currentAc.finish();
        }
    }

    public static void jumpWithBundle(Activity currentAc, Class<?> cla, boolean closeCurrentAc, Bundle bundle) {
        jump(currentAc, cla, bundle);
        if (closeCurrentAc) currentAc.finish();
    }

    public static void jumpWithBundle(Context context, Class<?> cla, Bundle bundle) {
        jump(context, cla, bundle);
    }


    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static void jumpActivityNewTask(Context context, Class<?> acClass, Bundle bundle) {
        LogExKt.logd(TAG, "context = " + context + ";acClass:" + acClass);
        Intent intent = new Intent(context, acClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent);
        } else {
            LogExKt.logd("JumpUtil", "jump app is not exist!");
        }
    }

//    public static void jumpSinglePortraitActivityWithTagetFragment(String fragmentPath, Bundle bundle) {
//        ARouter.getInstance().build(BaseRouterPath.BASE_SINGLE_PORTRAIT_ACTIVITY)
//                .with(bundle).withString(NAVIGATION_FLAG_FRAGMENT, fragmentPath).navigation();
//    }
//
//    public static void jumpLandscapeActivityWithTagetFragment(String fragmentPath, Bundle bundle) {
//        ARouter.getInstance().build(BaseRouterPath.BASE_LANDSCAPE_ACTIVITY)
//                .with(bundle).withString(NAVIGATION_FLAG_FRAGMENT, fragmentPath).navigation();
//    }
//
//    public static void jumpLandscapeFragmentWithTagetFragment(Activity activity, String fragmentPath, Bundle bundle) {
//        ARouter.getInstance().build(BaseRouterPath.BASE_LANDSCAPE_FRAGMENT)
//                .with(bundle).withString(NAVIGATION_FLAG_FRAGMENT, fragmentPath).navigation();
//    }

    /**
     * 在浏览器打开第三方url
     */
    public static void jumpBrowse(String url) {
        try {
            if (!TextUtils.isEmpty(url)) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                Activity activity = BaseApplication.Companion.getBaseApplication().getTopActivity();
                if (activity != null) activity.startActivity(intent);
            }
        } catch (Exception e) {
            ToastUtil.Companion.getInstance().toastCenter(R.string.base_invalid_link);
        }
    }

    /**
     * 拨打客服电话
     */
    public static void contactCustomerService(String phoneCode) {
        try {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneCode));
            Activity activity = BaseApplication.Companion.getBaseApplication().getTopActivity();
            if (activity != null) activity.startActivity(dialIntent);
        } catch (Exception e) {
            Log.d(TAG, "contactCustomerService: failure");
        }
    }
}