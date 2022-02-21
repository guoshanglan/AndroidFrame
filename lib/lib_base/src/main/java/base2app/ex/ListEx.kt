package base2app.ex

/**
 * ListEx
 * @descraption List 集合工具类
 * @time  10:05
 */

inline fun <T> Iterable<T>.forEachSafe(action: (t: T) -> Unit){
    synchronized (this) {
        val item: Iterator<T> = this.iterator()
        while (item.hasNext()) {
            val t = item.next()
            action(t)
        }
    }
}