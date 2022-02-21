//package androidx.navigation.fragment
//
//import android.content.Context
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentManager
//import androidx.fragment.app.FragmentTransaction
//import androidx.navigation.NavDestination
//import androidx.navigation.NavOptions
//import androidx.navigation.Navigator
//import java.util.*
//
///**
// *    author : liuwei
// *    e-mail : vsanliu@foxmail.com
// *    date   : 2021/6/25 17:39
// *    desc   : 同级TAB切换导航器
// */
//@Navigator.Name("fragment")
//class TabFragmentNavigator(context: Context, manager: FragmentManager, containerId: Int) :
//    Navigator<FragmentNavigator.Destination>() {
//    private val TAG = "TabFragmentNavigator"
//
//    private val mContext: Context = context
//    private val mFragmentManager: FragmentManager = manager
//    private val mContainerId = containerId
//    private val mBackStack = ArrayDeque<Int>()
//
//    override fun navigate(
//        destination: FragmentNavigator.Destination,
//        args: Bundle?,
//        navOptions: NavOptions?,
//        navigatorExtras: Extras?
//    ): NavDestination? {
//        if (mFragmentManager.isStateSaved) {
//            Log.i(
//                TAG, "Ignoring navigate() call: FragmentManager has already"
//                        + " saved its state"
//            )
//            return null
//        }
//        var className = destination.className
//        if (className[0] == '.') {
//            className = mContext.packageName + className
//        }
//
//        val ft: FragmentTransaction = mFragmentManager.beginTransaction()
//        var enterAnim = navOptions?.enterAnim ?: -1
//        var exitAnim = navOptions?.exitAnim ?: -1
//        var popEnterAnim = navOptions?.popEnterAnim ?: -1
//        var popExitAnim = navOptions?.popExitAnim ?: -1
//        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
//            enterAnim = if (enterAnim != -1) enterAnim else 0
//            exitAnim = if (exitAnim != -1) exitAnim else 0
//            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
//            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
//            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
//        }
//
//        val destId = destination.id
//        val tag = destId.toString()
//        var isAdd = true
//        val frag = mFragmentManager.findFragmentByTag(tag) ?: kotlin.run {
//            isAdd = false
//            instantiateFragment(
//                mContext, mFragmentManager,
//                className, args
//            ).apply {
//                arguments = args
//            }
//        }
//        if (!isAdd) {
//            ft.add(mContainerId, frag, tag)
//        } else {
//            ft.attach(frag)
//        }
//        mFragmentManager.fragments.forEach {
//            Log.i(TAG, "navigate: $it")
//            if (it != frag) {
//                ft.detach(it)
//            }
//        }
//        ft.setPrimaryNavigationFragment(frag)
//        val initialNavigation: Boolean = mBackStack.isEmpty()
//        val isSingleTopReplacement = (navOptions != null && !initialNavigation
//                && navOptions.shouldLaunchSingleTop()
//                && mBackStack.peekLast() == destId)
//        val isAdded: Boolean
//        isAdded = if (initialNavigation) true else !isSingleTopReplacement
//        ft.setReorderingAllowed(true)
//        ft.commit()
//        return if (isAdded) {
//            mBackStack.add(destId)
//            destination
//        } else {
//            null
//        }
//    }
//
//
//    override fun popBackStack(): Boolean {
//        if (mBackStack.isEmpty()) {
//            return false
//        }
//        if (mFragmentManager.isStateSaved) {
//            Log.i(
//                TAG, "Ignoring popBackStack() call: FragmentManager has already"
//                        + " saved its state"
//            )
//            return false
//        }
////        mFragmentManager.popBackStack(
////                generateBackStackName(mBackStack.size, mBackStack.peekLast()),
////                FragmentManager.POP_BACK_STACK_INCLUSIVE)
//        mBackStack.removeLast()
//        return true
//    }
//
//    /**
//     * Construct a new NavDestination associated with this Navigator.
//     *
//     *
//     * Any initialization of the destination should be done in the destination's constructor as
//     * it is not guaranteed that every destination will be created through this method.
//     * @return a new NavDestination
//     */
//    override fun createDestination(): FragmentNavigator.Destination {
//        return FragmentNavigator.Destination(this)
//    }
//
//    private fun instantiateFragment(
//        context: Context,
//        fragmentManager: FragmentManager,
//        className: String, args: Bundle?
//    ): Fragment {
//        return fragmentManager.fragmentFactory.instantiate(
//            context.classLoader, className
//        )
//    }
//
//
//}