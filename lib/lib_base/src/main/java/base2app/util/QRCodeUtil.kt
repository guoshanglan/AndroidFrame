package base2app.util


import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.TextUtils
import androidx.annotation.Px
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*


/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/12/13 15:52
 *    desc   :
 */
object QRCodeUtil {

    fun createQRImage(content: String?, @Px size:Int, logo: Bitmap? = null): Bitmap? {
        try {
            if (TextUtils.isEmpty(content)) {
                return null
            }
            //配置参数
            val hints: MutableMap<EncodeHintType, Any?> = EnumMap(com.google.zxing.EncodeHintType::class.java)
            hints[EncodeHintType.CHARACTER_SET] = "utf-8"
            //容错级别
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            //设置空白边距的宽度
            hints[EncodeHintType.MARGIN] = 2 //default is 4

            // 图像数据转换，使用了矩阵转换
            val bitMatrix =
                QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            val pixels = IntArray(size * size)
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (y in 0 until size) {
                for (x in 0 until size) {
                    if (bitMatrix[x, y]) {
                        pixels[y * size + x] = -0x1000000
                    } else {
                        pixels[y * size + x] = -0x1
                    }
                }
            }

            // 生成二维码图片的格式，使用ARGB_8888
            var bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
            if (logo != null) {
                bitmap = addLogo(bitmap, logo)
            }

            //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }

    private fun addLogo(src: Bitmap, logo: Bitmap): Bitmap? {

        //获取图片的宽高
        val srcWidth = src.width
        val srcHeight = src.height
        val logoWidth = logo.width
        val logoHeight = logo.height
        if (srcWidth == 0 || srcHeight == 0) {
            return null
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src
        }

        //logo大小为二维码整体大小的1/5
        val scaleFactor = srcWidth * 1.0f / 5 / logoWidth
        var bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888)
        try {
            val canvas = Canvas(bitmap)
            canvas.drawBitmap(src, 0f, 0f, null)
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2f, srcHeight / 2f)
            canvas.drawBitmap(
                logo,
                (srcWidth - logoWidth) / 2f,
                (srcHeight - logoHeight) / 2f,
                null
            )
            canvas.save()
            canvas.restore()
        } catch (e: Exception) {
            bitmap = null
            e.stackTrace
        }
        return bitmap
    }


}