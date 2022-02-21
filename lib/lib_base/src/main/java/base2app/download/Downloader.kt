package base2app.download

import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import base2app.Cache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Downloader
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  14:52
 */
fun download(url: String, build: IDowloadBuild) = flow {
    //UrlService.downloadFile()，这部分不用我教了吧
    val response = Cache[IDownLoadNet::class.java]?.downloadFile(url)
    val body = response?.body()
    if (body == null) {
        emit(DowloadStatus.DowloadErron(RuntimeException("下载出错")))
    } else {
        val length = body.contentLength()
        val contentType = body.contentType().toString()
        val ios = body.byteStream()
        val info = try {
            dowloadBuildToOutputStream(build, contentType)
        } catch (e: Exception) {
            emit(DowloadStatus.DowloadErron(e))
            DowloadInfo(null)
            return@flow
        }
        val ops = info.ops
        if (ops == null) {
            emit(DowloadStatus.DowloadErron(RuntimeException("下载出错")))
            return@flow
        }
        try {
            //下载的长度
            var currentLength: Int = 0
            //写入文件
            val bufferSize = 1024 * 8
            val buffer = ByteArray(bufferSize)
            val bufferedInputStream = BufferedInputStream(ios, bufferSize)
            var readLength: Int = 0
            while (bufferedInputStream.read(buffer, 0, bufferSize)
                            .also { readLength = it } != -1
            ) {
                ops.write(buffer, 0, readLength)
                currentLength += readLength
                emit(
                        DowloadStatus.DowloadProcess(
                                currentLength.toLong(),
                                length,
                                currentLength.toFloat() / length.toFloat()
                        )
                )
            }
            bufferedInputStream.close()
            if (info.uri != null)
                emit(DowloadStatus.DowloadSuccess(info.uri))
            else emit(DowloadStatus.DowloadSuccess(Uri.fromFile(info.file)))
        } catch (e: Exception) {
            emit(e)
        } finally {
            ops.close()
            ios.close()
        }
    }
}.flowOn(Dispatchers.IO)

private fun dowloadBuildToOutputStream(build: IDowloadBuild, contentType: String): DowloadInfo {
    val context = build.getContext()
    val uri = build.getUri(contentType)
    if (build.getDowloadFile() != null) {
        val file = build.getDowloadFile()!!
        return DowloadInfo(FileOutputStream(file), file)
    } else if (uri != null) {
        return DowloadInfo(context.contentResolver.openOutputStream(uri), uri = uri)
    } else {
        val name = build.getFileName()
        val fileName = if (!name.isNullOrBlank()) name else "${System.currentTimeMillis()}.${
            MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(contentType)
        }"
        val file = File("${context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}", fileName)
        return DowloadInfo(FileOutputStream(file), file)
    }
}

private class DowloadInfo(val ops: OutputStream?, val file: File? = null, val uri: Uri? = null)