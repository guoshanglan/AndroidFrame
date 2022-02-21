package base2app.download

import android.net.Uri

/**
 * DowloadStatus
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  14:55
 */
sealed class DowloadStatus {
    class DowloadProcess(val currentLength: Long, val length: Long, val process: Float) :
        DowloadStatus()
    class DowloadErron(val t: Throwable) : DowloadStatus()
    class DowloadSuccess(val uri: Uri) : DowloadStatus()
}