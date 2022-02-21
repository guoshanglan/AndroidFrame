package com.zhuorui.commonwidget.flow;

import android.text.TextUtils;

/**
 * Created by liuwei on 2017/7/18.
 */

public class KVUtil {

    public static int getOnclick(String click) {
        if (TextUtils.equals(click, "mobile")) {
            return KVInterdace.CLICK_MOBILE;
        } else if (TextUtils.equals(click, "shipping")) {
            return KVInterdace.CLICK_SHIPPING;
        }
        return 0;
    }

    public static String onclickKey(int click) {
        switch (click) {
            case KVInterdace.CLICK_MOBILE:
                return "mobile";
            case KVInterdace.CLICK_SHIPPING:
                return "shipping";
            case KVInterdace.CLICK_COPY:
                return "copy";
            case KVInterdace.CLICK_NONE:
            default:
                return "none";
        }
    }

    public static int getOrientation(String orientation) {
        if (TextUtils.equals(orientation, "rl")) {
            return KVInterdace.ORIENTATION_RL;
        } else if (TextUtils.equals(orientation, "tb")) {
            return KVInterdace.ORIENTATION_TB;
        } else if (TextUtils.equals(orientation, "no_k")) {
            return KVInterdace.ORIENTATION_NO_K;
        }
        return 0;
    }

    public static String getOrientationKey(int orientation) {
        switch (orientation) {
            case KVInterdace.ORIENTATION_NO_K:
                return "no_k";
            case KVInterdace.ORIENTATION_TB:
                return "tb";
            case KVInterdace.ORIENTATION_RL:
            default:
                return "rl";
        }
    }

    public static int getVFormat(String vFormat) {
        if (TextUtils.equals(vFormat, "string")) {
            return KVInterdace.V_FORMAT_STRING;
        } else if (TextUtils.equals(vFormat, "array")) {
            return KVInterdace.V_FORMAT_ARRAY;
        } else if(TextUtils.equals(vFormat,"array_group")) {
            return KVInterdace.V_FORMAT_ARRAY_GROUP;
        }
        return 0;
    }

    public static String getVFormatKey(int vFormat) {
        switch (vFormat) {
            case KVInterdace.V_FORMAT_ARRAY:
                return "array";
            case KVInterdace.V_FORMAT_ARRAY_GROUP:
                return "array_group";
            case KVInterdace.V_FORMAT_STRING:
            default:
                return "string";
        }
    }

    public static int getVType(String vType) {
        if (TextUtils.equals(vType, "txt")) {
            return KVInterdace.V_TYPE_TEXT;
        } else if (TextUtils.equals(vType, "img")) {
            return KVInterdace.V_TYPE_IMG;
        }
        return 0;
    }

    public static String getVTypeKey(int vType) {
        switch (vType) {
            case KVInterdace.V_TYPE_IMG:
                return "img";
            case KVInterdace.V_TYPE_TEXT:
            default:
                return "txt";
        }
    }

    public static int getKFormat(String vFormat) {
        if (TextUtils.equals(vFormat, "resid")) {
            return KVInterdace.K_FORMAT_RESID;
        } else if (TextUtils.equals(vFormat, "txt")) {
            return KVInterdace.K_FORMAT_TEXT;
        }
        return 0;
    }

    public static String getKFormatKey(int kFormat) {
        switch (kFormat) {
            case KVInterdace.K_FORMAT_RESID:
                return "resid";
            case KVInterdace.K_FORMAT_TEXT:
            default:
                return "txt";
        }
    }

    public static int getKType(String vType) {
        if (TextUtils.equals(vType, "txt")) {
            return KVInterdace.K_TYPE_TEXT;
        } else if (TextUtils.equals(vType, "img")) {
            return KVInterdace.K_TYPE_IMG;
        }
        return 0;
    }

    public static String getKTypeKey(int kType) {
        switch (kType) {
            case KVInterdace.K_TYPE_IMG:
                return "img";
            case KVInterdace.K_TYPE_TEXT:
            default:
                return "txt";
        }
    }
}
