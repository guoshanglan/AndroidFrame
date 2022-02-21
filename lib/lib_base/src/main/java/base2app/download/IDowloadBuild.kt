package base2app.download

import android.content.Context
import android.net.Uri
import java.io.File

/**
 * @descraption  下载
 * @time  14:53
 */
abstract class IDowloadBuild {
    open fun getFileName(): String? = null
    open fun getUri(contentType: String): Uri? = null
    open fun getDowloadFile(): File? = null
    abstract fun getContext(): Context //贪方便的话，返回Application就行
}
