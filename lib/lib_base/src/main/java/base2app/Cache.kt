@file:Suppress("UNCHECKED_CAST")

package base2app

import base2app.network.Network

import java.util.HashMap

/**
 * 集合Map实现单例模式
 */
object Cache {
    private val caches = HashMap<String, Any>()

    operator fun <T> get(cls: Class<T>): T? {
        var `object` = caches[cls.name]
        if (`object` == null) {
            instance(cls)
            `object` = caches[cls.name]
        }
        return `object` as T?
    }

    fun <T> remove(cls: Class<T>): T? {
        return caches.remove(cls.name) as T?
    }

    fun clear() {
        caches.clear()
    }

    private fun instance(clz: Class<*>) {
//        val instance: Any? = Network.retrofit!!.create(clz)
//        if (instance == null) {
//            try {
//                // 根据一个类名创建一个实例
//                val clazz = Class.forName(clz.name)
//                val cons = clazz.getDeclaredConstructor()
//                cons.isAccessible = true
//                instance = cons.newInstance()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//        if (instance != null)
            caches[clz.name] = Network.retrofit!!.create(clz)
    }
}