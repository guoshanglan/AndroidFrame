package base2app.config;

import android.content.Context;

import com.github.fernandodev.androidproperties.lib.AssetsProperties;
import com.github.fernandodev.androidproperties.lib.Property;

/**
 * 类描述：配置默认的运行config
 */
public class RunConfig extends AssetsProperties {

    public RunConfig(Context context, String propertiesFileName) {
        super(context, propertiesFileName);
    }

    @Property
    int log_level;/*日志等级*/
    @Property
    boolean open_debug;/*是否开启debug*/

    /*网络配置信息*/
    @Property
    String domain_api;
    @Property
    String oss_api;
    @Property
    String tcp_api_hk;
    @Property
    String tcp_api_a;
    @Property
    String tcp_api_us;
    @Property
    String tcp_push;
    @Property
    String h5_api;
    @Property
    int write_timeout;/*写超时时间*/
    @Property
    int read_timeout;/*读超时时间*/
    @Property
    int connect_timeout;/*链接超时时间*/
    @Property
    String private_key;/*APP与服务端校验参数的加密私钥*/

    public String getDomain_api() {
        return domain_api;
    }
}