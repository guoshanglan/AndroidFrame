package base2app.util

import android.text.TextUtils
import base2app.ex.text
import base2app.timeformat.FastDateFormat
import com.example.lib_base.R
import java.text.ParseException
import java.util.*

/**
 * Create by xieyingwu on 2018/8/29
 * 时区util
 */
object TimeZoneUtil {
    /**
     * 获取当前时间
     *
     * @return
     */
    @JvmStatic
    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    @JvmStatic
    fun currentTime(format: String?): String {
        return timeFormat(currentTimeMillis(), format)
    }

    val timeOffset: Int
        get() {
            val instance = Calendar.getInstance()
            val zoneOffset = instance[Calendar.ZONE_OFFSET]
            val dstOffset = instance[Calendar.DST_OFFSET]
            val offsetMills = zoneOffset + dstOffset
            return offsetMills / 1000 / 60 / 60
        }

    /**
     * 字符串时间格式转化
     *
     * @param time      字符串时间
     * @param oldFormat 原时间格式
     * @param newFormat 新时间格式
     * @return 返回新的时间格式，发生错误，原字符串返回
     */
    fun timeFormat(time: String?, oldFormat: String?, newFormat: String?): String? {
        if (TextUtils.isEmpty(time) || TextUtils.isEmpty(oldFormat) || TextUtils.isEmpty(newFormat)) return ""
        var sDate: Date? = null
        try {
            sDate = FastDateFormat.getInstance(oldFormat).parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return if (sDate == null) time else FastDateFormat.getInstance(newFormat).format(sDate)
    }
    /**
     * 时间格式转化
     *
     * @param timeMillis 毫秒
     * @param format     时间格式
     * @param timeZone   时区
     * @return 返回新的时间格式，发生错误，原字符串返回
     */
    @JvmOverloads
    @JvmStatic
    fun timeFormat(
        timeMillis: Long,
        format: String?,
        timeZone: TimeZone? = TimeZone.getDefault()
    ): String {
        return FastDateFormat.getInstance(format, timeZone).format(timeMillis)
    }

    /**
     * 时间格式转化
     *
     * @param timeMillis 毫秒
     * @param format     时间格式
     * @param ts         ts
     * @return 返回新的时间格式，发生错误，原字符串返回
     */
    @JvmStatic
    fun timeFormat(timeMillis: Long, format: String?, ts: String?): String {
        return getTimeZoneFormat(format, ts).format(timeMillis)
    }

    /**
     * 获取股票市场的时区
     *
     * @param ts
     * @return
     */
    @JvmStatic
    fun getTimeZoneByTs(ts: String?): TimeZone {
        when (ts) {
            "HK" ->                 //香港时间 (香港)
                return TimeZone.getTimeZone("Asia/Hong_Kong")
            "US" ->                 //美国东部时间 (纽约
                return TimeZone.getTimeZone("America/New_York")
            "SH", "SZ" ->                 //中国标准时间 (北京)
                return TimeZone.getTimeZone("Asia/Shanghai")
        }
        return TimeZone.getDefault()
    }

    /**
     * 根据TS获取对应时区时间格式化工具
     *
     * @param format
     * @param ts
     * @return
     */
    @JvmStatic
    fun getTimeZoneFormat(format: String?, ts: String?): FastDateFormat {
        return FastDateFormat.getInstance(format, getTimeZoneByTs(ts))
    }

    /**
     * 和今天日期比较大小
     *
     * @param date       要比较日期
     * @param dateFormat 时间格式
     * @return 1 日期比今天大（明天） ；0 日期和今天相同；-1 日期比今天小（昨天）
     */
    fun dateCompareToday(date: String?, dateFormat: String?): Int {
        val today = FastDateFormat.getInstance(dateFormat).format(System.currentTimeMillis())
        return compareToTime(date, dateFormat, today, dateFormat)
    }

    /**
     * 比较两个时间大小
     *
     * @param date1       时间1
     * @param date1Format 时间1格式
     * @param date2       时间2
     * @param date2Format 时间2格式
     * @return 1 date1>date2 ；0 date1=date2；-1 date1<date2></date2>
     */
    fun compareToTime(
        date1: String?,
        date1Format: String?,
        date2: String?,
        date2Format: String?
    ): Int {
        try {
            val d1: Date = FastDateFormat.getInstance(date1Format).parse(date1)
            val d2: Date = FastDateFormat.getInstance(date2Format).parse(date2)
            if (d1.time > d2.time) {
                return 1
            } else if (d1.time < d2.time) {
                return -1
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 时间字符串转成毫秒时间戳
     *
     * @param time
     * @param format
     * @return
     */
    @JvmStatic
    fun parseTime(time: String?, format: String?): Long {
        try {
            val date: Date = FastDateFormat.getInstance(format).parse(time)
            return date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 时间字符串转成毫秒时间戳
     *
     * @param time
     * @param format
     * @return
     */
    fun parseTime(ts: String?, time: String?, format: String?): Long {
        try {
            return getTimeZoneFormat(format, ts).parse(time).time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 判断出生时间到今天是否成年（18岁）
     *
     * @param birthDay   出生日期
     * @param timeFormat 时间格式
     * @return
     */
    fun isAdulthood(birthDay: String?, timeFormat: String?): Boolean {
        try {
            val birthDate: Date = FastDateFormat.getInstance(timeFormat).parse(birthDay)
            val tms = Calendar.getInstance()
            tms.time = birthDate
            val y = tms[Calendar.YEAR] + 18
            //成年日期
            val adulthoodDate = String.format(
                "%d-%02d-%02d",
                y,
                tms[Calendar.MONTH] + 1,
                tms[Calendar.DAY_OF_MONTH]
            )
            return dateCompareToday(adulthoodDate, "yyyy-MM-dd") < 1
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 根据当前时间增减天数
     *
     * @param day
     * @return
     */
    fun addDayByCurrentTime(day: Int): Long {
        return addDay(currentTimeMillis(), day)
    }

    /**
     * 增减天
     *
     * @param startTimeMillis
     * @param day
     * @return
     */
    @JvmStatic
    fun addDay(startTimeMillis: Long, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startTimeMillis
        calendar.add(Calendar.DAY_OF_MONTH, day)
        return calendar.timeInMillis
    }

    /**
     * 根据当前时间增减月数
     *
     * @param month
     * @return
     */
    @JvmStatic
    fun addMonthByCurrentTime(month: Int): Long {
        return addMonth(currentTimeMillis(), month)
    }

    /**
     * 增减月
     *
     * @param month
     * @return
     */
    @JvmStatic
    fun addMonth(startTimeMillis: Long, month: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startTimeMillis
        calendar.add(Calendar.MONTH, month)
        return calendar.timeInMillis
    }

    /**
     * 两个时间戳相隔天数
     *
     * @param begin
     * @param start
     * @param timeFormat
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getInterval(begin: Long, start: Long, timeFormat: String?): Long {
        var day: Long = 0
        val sdf = FastDateFormat.getInstance(timeFormat)
        val date1 = Date(begin)
        val date2 = Date(start)
        val begin_date = sdf.format(date1)
        val begin_date_info = sdf.parse(begin_date)
        val end_date = sdf.format(date2)
        val end_date_info = sdf.parse(end_date)
        day = (end_date_info.time - begin_date_info.time) / (24 * 60 * 60 * 1000)
        return day
    }

    /**
     * 两个时间戳相隔小时数
     *
     * @param begin
     * @param start
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getIntervalHours(begin: Long, start: Long): Long {
        val between: Long = if (begin > start) {
            (begin - start) / 1000
        } else {
            (start - begin) / 1000
        }
        return between / 3600
    }

    /**
     * 两个时间戳相隔分钟
     *
     * @param begin
     * @param start
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getIntervalMintues(begin: Long, start: Long): Long {
        val between: Long = if (begin > start) {
            (begin - start) / 1000
        } else {
            (start - begin) / 1000
        }
        return between / 60
    }

    @Throws(Exception::class)
    fun getTimeInterval(begin: Long, start: Long): String {
        return if (getIntervalMintues(begin, start) > 1 && getIntervalMintues(
                begin,
                start
            ) < 60
        ) {
            String.format(
                text(R.string.mintues_ahead),
                getIntervalMintues(begin, start)
            )
        } else if (getIntervalMintues(begin, start) > 60 && getIntervalHours(
                start,
                begin
            ) < 24
        ) {
            String.format(
                text(R.string.hours_ahead),
                getIntervalHours(start, begin)
            )
        } else if (getIntervalMintues(begin, start) < 1) {
            text(R.string.times_ago)
        } else {
            timeFormat(start, "yyyy-MM-dd")
        }
    }

    fun timeFormat(time: Long, showHourMinute: Boolean, forceShowYesterday: Boolean): String {
        return if (time <= 0) {
            ""
        } else {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = time
            val curCalendar = Calendar.getInstance()
            curCalendar.timeInMillis = System.currentTimeMillis()
            if (calendar[Calendar.YEAR] == curCalendar[Calendar.YEAR]) {
                //同一年
                if (calendar[Calendar.DAY_OF_YEAR] == curCalendar[Calendar.DAY_OF_YEAR]) {
                    //同一天
                    timeFormat(calendar.timeInMillis, "HH:mm")
                } else {
                    curCalendar.add(Calendar.DAY_OF_MONTH, -1)
                    if (calendar[Calendar.DAY_OF_YEAR] == curCalendar[Calendar.DAY_OF_YEAR] && forceShowYesterday) {
                        //昨日
                        text(R.string.yesterday)
                    } else {
                        //其它日
                        if (showHourMinute) {
                            timeFormat(calendar.timeInMillis, "MM-dd HH:mm")
                        } else {
                            timeFormat(calendar.timeInMillis, "MM-dd")
                        }
                    }
                }
            } else {
                //不同年
                timeFormat(calendar.timeInMillis, "yyyy-MM-dd")
            }
        }
    }

    /**
     * 获取时间梯度格式
     * @param publishTime
     * @return
     */
    fun getPublishTime(publishTime: Long): String {
        if (publishTime <= 0) return ""
        val m: Long = 60000 //分钟毫秒数
        val h: Long = 3600000 //小时毫秒数
        val d: Long = 86400000 //一天毫秒数
        val time = currentTimeMillis() - publishTime
        if (time < 0) return timeFormat(publishTime, "yyyy-MM-dd")
        return when {
            time < m -> {
                text(R.string.times_ago)
            }
            time < h -> {
                String.format(text(R.string.mintues_ahead), (time / m).toString())
            }
            time < d -> {
                String.format(text(R.string.hours_ahead), (time / h).toString())
            }
            else -> {
                val day = time / d
                when {
                    day >= 365 -> {
                        timeFormat(publishTime, "yyyy-MM-dd")
                    }
                    day >= 30 -> {
                        String.format(text(R.string.months_ahead), (day / 30).toString())
                    }
                    else -> {
                        String.format(text(R.string.days_ahead), day.toString())
                    }
                }
            }
        }
    }

    /**
     * 判断是否为当前年份
     */
    fun isSameYear(timeMillis: Long): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMillis
        val curCalendar = Calendar.getInstance()
        curCalendar.timeInMillis = System.currentTimeMillis()
        return calendar[Calendar.YEAR] == curCalendar[Calendar.YEAR]
    }


    /**
     * 时间格式如果为今年，那么就返回MM-dd,否则就是yyyy-MM-dd
     * @return
     */
    fun getSameYearFormat(time: Long): String {
        return if (time <= 0) {
            return ""
        } else {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = time
            val curCalendar = Calendar.getInstance()
            curCalendar.timeInMillis = System.currentTimeMillis()
            if (calendar[Calendar.YEAR] == curCalendar[Calendar.YEAR]) {
                timeFormat(
                    calendar.timeInMillis,
                    "MM-dd"
                )
            } else {
                timeFormat(
                    calendar.timeInMillis,
                    "yyyy-MM-dd"
                )
            }
        }
    }
}