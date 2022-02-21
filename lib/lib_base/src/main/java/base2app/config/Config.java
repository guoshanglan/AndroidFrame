package base2app.config;


import android.content.Context;

import com.example.lib_base.BuildConfig;
import com.github.fernandodev.androidproperties.lib.AssetsProperties;
import com.github.fernandodev.androidproperties.lib.Property;
import base2app.infra.MMKVManager;

/**
 * 默认配置信息
 * 其余配置额外参数信息可继承该类进行配置读取
 * 1.assets目录下需要配置properties文件
 * 2.run_mode配置文件名采用config_{runMode}.properties
 * 3.统一配置文件名config.properties
 */
public class Config extends AssetsProperties {

    public static final String CACHE_CONFIG_NAME = "CACHE_CONFIG_NAME";

    @Property
    private String run_mode;/*运行环境*/

    /*自定义属性*/
    private final RunConfig runConfig;
    private final RunMode runMode;

    public Config(Context context) {
        super(context);
        /*在debug模式下允许读取自定义选择的运行环境*/
        if (BuildConfig.DEBUG) {
            run_mode = MMKVManager.Companion.getInstance().getString(CACHE_CONFIG_NAME, run_mode);
        }
        /*依据配置读取运行环境*/
        runMode = RunMode.readRunModeByString(run_mode);
        runConfig = new RunConfig(context, "config_" + runMode.name());
    }

    public RunConfig getRunConfig() {
        return runConfig;
    }

    /**
     * 此处由app assets目录config_xxx文件中配置读取而来，用于自定义是否使用debug模式
     *
     * @return true允许debug模式  false不允许debug模式
     */
    public boolean isDebug() {
        return runConfig.open_debug;
    }

    public int logLevel() {
        return runConfig.log_level;
    }

    public String domain() {
        // http使用ip
        return runConfig.domain_api;
    }

    public String oss() {
        // oss 历史K线地址
        return runConfig.oss_api;
    }

    public String tcp(String ts) {
        // tcp使用ip
        if ("HK".equals(ts)) {
            return runConfig.tcp_api_hk;
        }
        if ("US".equals(ts)) {
            return runConfig.tcp_api_us;
        }
        if ("A".equals(ts)) {
            return runConfig.tcp_api_a;
        }
        return "";
    }

    public String push() {
        return runConfig.tcp_push;
    }

    public String h5() {
        // h5使用ip
        return runConfig.h5_api;
    }

    public String privateKey() {
        // 加密私钥
        return runConfig.private_key;
    }

    public int writeTimeout() {
        return runConfig.write_timeout;
    }

    public int readTimeout() {
        return runConfig.read_timeout;
    }

    public int connectTimeout() {
        return runConfig.connect_timeout;
    }

    public RunMode getRunMode() {
        return runMode;
    }
}