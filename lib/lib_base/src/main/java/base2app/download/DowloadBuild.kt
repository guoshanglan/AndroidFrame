package base2app.download

import android.content.Context

/**
 * DowloadBuild
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  14:55
 */
class DowloadBuild(val cxt: Context): IDowloadBuild(){
    override fun getContext(): Context = cxt
}