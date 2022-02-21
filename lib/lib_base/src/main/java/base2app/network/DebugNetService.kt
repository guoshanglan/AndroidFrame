package base2app.network

import com.zrlib.lib_service.base.ServiceProvider
import okhttp3.Interceptor


/**
 * DebugNetService
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  17:32
 */
abstract class DebugNetService : ServiceProvider() {
    abstract fun provideNetIntercept(): Interceptor
}