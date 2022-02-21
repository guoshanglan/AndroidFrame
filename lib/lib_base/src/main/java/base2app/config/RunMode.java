package base2app.config;

import android.text.TextUtils;

/**
 * App运行模式
 */
public enum RunMode {
    ci, /*内网测试环境*/
    dev,/*内网开发环境*/
    qa, /*正式环境*/
    out,/*外网测试环境*/
    preview;/*外网预发布环境*/

    private static RunMode current = qa;

    public boolean isDevMode() {
        return current == dev;
    }

    public boolean isCiMode() {
        return current == ci;
    }

    public boolean isOutCiMode() {
        return current == out;
    }

    public boolean isQaMode() {
        return current == qa;
    }

    public boolean isPreview() {
        return current == preview;
    }

    public RunMode getRunModel() {
        return current;
    }

    public static RunMode readRunModeByString(String run_mode) {
        if (TextUtils.isEmpty(run_mode)) {
            current = qa;/*默认正式环境*/
            return current;
        }
        try {
            current = RunMode.valueOf(run_mode);
            return current;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        current = qa;/*默认正式环境*/
        return current;
    }
}
