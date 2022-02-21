package base2app.infra;

public class LogInfra {
    public static String TAG = LogInfra.class.getSimpleName();
    public static int LEVEL = android.util.Log.ERROR;
    public static boolean OPEN_LOG = false;

    /***
     * 传入App级别TAG,级别高于level才允许输出日志；初始化调用在Application
     * @param tag 日志TAG
     * @param openLog 是否开启日志打印
     * @param level 日志Level；用于判断日志是否打印
     */
    public static void init(String tag, boolean openLog, int level) {
        TAG = tag;
        OPEN_LOG = openLog;
        LEVEL = level;
    }

    public static boolean isOpenLog(){
        return OPEN_LOG;
    }

}
