package androidx.navigation

import android.os.Bundle
import androidx.annotation.NavigationRes
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/7/1 13:33
 *    desc   : 动态目的地恢复器
 */
class DynamicDestiantionRestorer constructor(savedInstanceState: Bundle?) {

    companion object {
        /**
         * 导航图Xml文件ID保存Key(Tips:value需要与NavHostFragment KEY_GRAPH_ID保持一致)
         */
        private const val KEY_GRAPH_ID = "android-support-nav:fragment:graphId"

        /**
         * 导航图目的的保存Key
         */
        private const val KEY_DESTINATIONS = "HostFragment:destinations"

        /**
         * 保存导航图动态添加目的地
         * @param navigationId 代码动态关联导航图xml ID
         */
        fun saveInstanceState(
            navController: NavController,
            outState: Bundle,
            @NavigationRes navigationId: Int? = null
        ) {
            //获取Xml关联导航图，系统保存的导航图xml ID
            val outStateGraphId = outState.getInt(KEY_GRAPH_ID, 0)
            val graphId = if (outStateGraphId != 0) {
                outStateGraphId
            } else if (navigationId != null && navigationId != 0) {
                navigationId
            } else {
                0
            }
            //获取已在导航图Xml的目的在ID
            val xmlIds = mutableListOf<Int>()
            if (graphId != 0) {
                navController.navInflater.inflate(graphId).iterator().forEach {
                    xmlIds.add(it.id)
                }
            }
            //
            val destinations = arrayListOf<String>()
            navController.graph.iterator().forEach {
                val id = it.id
                if (!xmlIds.contains(id)) {
                    if (it is FragmentNavigator.Destination) {
                        destinations.add("${it.navigatorName}:${it.className}:$id")
                    } else if (it is DialogFragmentNavigator.Destination) {
                        destinations.add("${it.navigatorName}:${it.className}:$id")
                    }
                }
            }
            if (destinations.isNotEmpty()) {
                outState.putStringArrayList(KEY_DESTINATIONS, destinations)
            }
            if (!outState.containsKey(KEY_GRAPH_ID)) {
                outState.putInt(KEY_GRAPH_ID, graphId)
            }
        }
    }

    /**
     * 恢复导航图ID
     */
    var mGraphId: Int = 0

    /**
     * 恢复导航图目的地集合
     */
    private var destinations: ArrayList<String>? = null

    init {
        savedInstanceState?.let {
            mGraphId = it.getInt(KEY_GRAPH_ID)
            destinations = it.getStringArrayList(KEY_DESTINATIONS)
            it.remove(KEY_DESTINATIONS)
            //清除导航图xmlId,自定义导航图恢复逻辑
            it.remove(KEY_GRAPH_ID)
        }
    }

    fun setGraph(navController: NavController,graph:NavGraph,startDestinationArgs:Bundle?) {
        restoreDestinations(navController, graph)
        navController.setGraph(graph, startDestinationArgs)
    }

    /**
     * 恢复导航图动态添加目地的
     */
    fun restoreGraph(navController: NavController,startDestinationArgs:Bundle?): Boolean {
        return if (mGraphId != 0) {
            val navGraph = navController.navInflater.inflate(mGraphId)
            restoreDestinations(navController,navGraph)
            navController.setGraph(navGraph,startDestinationArgs)
            true
        } else {
            false
        }
    }

    private fun restoreDestinations(navController: NavController,graph:NavGraph){
        destinations?.forEach {
            val destination = it.split(":")
            when (val navigatorName = destination[0]) {
                "fragment" -> {
                    val className = destination[1]
                    val destinationId = destination[2].toInt()
                    val navigator =
                        navController.navigatorProvider.getNavigator<FragmentNavigator>(
                            navigatorName
                        )
                    navigator.createDestination().apply {
                        this.id = destinationId
                        this.className = className
                    }
                }
                "dialog" -> {
                    val className = destination[1]
                    val destinationId = destination[2].toInt()
                    val navigator =
                        navController.navigatorProvider.getNavigator<DialogFragmentNavigator>(
                            navigatorName
                        )
                    navigator.createDestination().apply {
                        this.id = destinationId
                        this.className = className
                    }
                }
                else -> null
            }?.let { dest ->
                graph.addDestination(dest)
            }
        }
        destinations = null
    }


}