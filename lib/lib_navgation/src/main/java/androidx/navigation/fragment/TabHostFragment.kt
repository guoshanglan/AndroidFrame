//package androidx.navigation.fragment
//
//import android.os.Bundle
//import androidx.annotation.CallSuper
//import androidx.annotation.IdRes
//import androidx.annotation.LayoutRes
//import androidx.annotation.NavigationRes
//import androidx.navigation.NavController
//import androidx.navigation.NavHostController
//
///**
// *    author : liuwei
// *    e-mail : vsanliu@foxmail.com
// *    date   : 2021/6/25 17:47
// *    desc   : 平级Tab切换控制NavHost Fragment
// */
//abstract class TabHostFragment(@LayoutRes contentLayoutId: Int? = null, @IdRes containerId: Int? = null, @NavigationRes navigationId: Int? = null)
//    : SubHostFragment(contentLayoutId, containerId, navigationId) {
//
//    @CallSuper
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        (navController as NavHostController).let {
//            //禁止当前navController处理返回事件
//            it.enableOnBackPressed(false)
//        }
//    }
//
//    override fun onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment: Boolean) {
//        //重写，不接收导航变化事件，固定false禁止当前navController处理返回事件
//        super.onPrimaryNavigationFragmentChanged(false)
//    }
//
//    override fun onCreateNavController(navController: NavController) {
//        super.onCreateNavController(navController)
//        //自定义Fragment导航逻辑
//        navController.navigatorProvider.addNavigator(
//                TabFragmentNavigator(
//                        requireContext(),
//                        childFragmentManager,
//                        getContainerId()
//                )
//        )
//    }
//
//
//}