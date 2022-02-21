//package androidx.navigation.fragment
//
//import android.content.Context
//import android.os.Bundle
//import android.util.AttributeSet
//import android.util.Log
//import androidx.annotation.CallSuper
//import androidx.annotation.NonNull
//import androidx.fragment.app.DialogFragment
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentManager
//import androidx.fragment.app.mainNav
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleEventObserver
//import androidx.navigation.*
//import java.util.*
//
///**
// *    author : liuwei
// *    e-mail : vsanliu@foxmail.com
// *    date   : 2021/7/6 17:33
// *    desc   : 自定义fragment导航
// */
//@Navigator.Name("nav-dialog")
//class NavDialogFragmentNavigator(context: Context, manager: FragmentManager):Navigator<NavDialogFragmentNavigator.Destination>(){
//    companion object{
//        private const val TAG = "DialogFragmentNavigator"
//        private const val KEY_DIALOG_COUNT = "androidx-nav-dialogfragment:navigator:count"
//        private const val DIALOG_TAG = "androidx-nav-fragment:navigator:dialog:"
//    }
//
//
//    var mContext: Context = context
//    var mFragmentManager: FragmentManager = manager
//    var mDialogCount = 0
//    private val mRestoredTagsAwaitingAttach = HashSet<String?>()
//
//    private val mObserver: LifecycleEventObserver =
//        LifecycleEventObserver { source, event ->
//            if (event == Lifecycle.Event.ON_STOP) {
//                val dialogFragment = source as DialogFragment
//                if (!dialogFragment.requireDialog().isShowing) {
//                    dialogFragment.mainNav.popBackStack()
////                    NavHostFragment.findNavController(dialogFragment).popBackStack()
//                }
//            }
//        }
//
//    /**
//     * Construct a new NavDestination associated with this Navigator.
//     *
//     *
//     * Any initialization of the destination should be done in the destination's constructor as
//     * it is not guaranteed that every destination will be created through this method.
//     * @return a new NavDestination
//     */
//    override fun createDestination(): Destination {
//        return Destination(this)
//    }
//
//    /**
//     * Navigate to a destination.
//     *
//     *
//     * Requests navigation to a given destination associated with this navigator in
//     * the navigation graph. This method generally should not be called directly;
//     * [NavController] will delegate to it when appropriate.
//     *
//     * @param destination destination node to navigate to
//     * @param args arguments to use for navigation
//     * @param navOptions additional options for navigation
//     * @param navigatorExtras extras unique to your Navigator.
//     * @return The NavDestination that should be added to the back stack or null if
//     * no change was made to the back stack (i.e., in cases of single top operations
//     * where the destination is already on top of the back stack).
//     */
//    override fun navigate(
//        destination: NavDialogFragmentNavigator.Destination,
//        args: Bundle?,
//        navOptions: NavOptions?,
//        navigatorExtras: Extras?
//    ): NavDestination? {
//        if (mFragmentManager!!.isStateSaved) {
//            Log.i(
//                TAG, "Ignoring navigate() call: FragmentManager has already"
//                        + " saved its state"
//            )
//            return null
//        }
//        var className = destination.className?:return null
//        if (className[0] == '.') {
//            className = mContext.packageName + className
//        }
//        val frag = mFragmentManager.fragmentFactory.instantiate(
//            mContext.classLoader, className
//        )
//        if (!DialogFragment::class.java.isAssignableFrom(frag.javaClass)) {
//            throw IllegalArgumentException(
//                "Dialog destination " + destination.className
//                        + " is not an instance of DialogFragment"
//            )
//        }
//        val dialogFragment = frag as DialogFragment
//        dialogFragment.arguments = args
//        dialogFragment.lifecycle.addObserver(mObserver)
//
//        dialogFragment.show((mFragmentManager), DIALOG_TAG + mDialogCount++)
//
//        return destination
//    }
//
//    /**
//     * Attempt to pop this navigator's back stack, performing the appropriate navigation.
//     *
//     *
//     * Implementations should return `true` if navigation
//     * was successful. Implementations should return `false` if navigation could not
//     * be performed, for example if the navigator's back stack was empty.
//     *
//     * @return `true` if pop was successful
//     */
//    override fun popBackStack(): Boolean {
//        if (mDialogCount == 0) {
//            return false
//        }
//        if (mFragmentManager!!.isStateSaved) {
//            Log.i(
//                TAG,
//                "Ignoring popBackStack() call: FragmentManager has already"
//                        + " saved its state"
//            )
//            return false
//        }
//        val existingFragment = mFragmentManager?.findFragmentByTag(DIALOG_TAG + --mDialogCount)
//        if (existingFragment != null) {
//            existingFragment.lifecycle.removeObserver(mObserver)
//            (existingFragment as DialogFragment).dismiss()
//        }
//        return true
//    }
//
//    override fun onSaveState(): Bundle? {
//        if(mDialogCount == 0) {
//            return null
//        }
//        val b = Bundle()
//        b.putInt(KEY_DIALOG_COUNT, mDialogCount)
//        return b
//    }
//
//    override fun onRestoreState(savedState: Bundle) {
//        if (savedState != null) {
//            mDialogCount = savedState.getInt(KEY_DIALOG_COUNT, 0)
//            for (index in 0 until mDialogCount) {
//                val fragment = mFragmentManager
//                    .findFragmentByTag(DIALOG_TAG + index) as DialogFragment?
//                fragment?.lifecycle?.addObserver(mObserver)
//                    ?: mRestoredTagsAwaitingAttach.add(DIALOG_TAG + index)
//            }
//        }
//    }
//
//    fun onAttachFragment(childFragment: Fragment) {
//        val needToAddObserver = mRestoredTagsAwaitingAttach.remove(childFragment.tag)
//        if (needToAddObserver) {
//            childFragment.lifecycle.addObserver(mObserver)
//        }
//    }
//
//    /**
//     * Construct a new fragment destination. This destination is not valid until you set the
//     * Fragment via [.setClassName].
//     *
//     * @param fragmentNavigator The [DialogFragmentNavigator] which this destination
//     * will be associated with. Generally retrieved via a
//     * [NavController]'s
//     * [NavigatorProvider.getNavigator] method.
//     */
//
//    /**
//     * NavDestination specific to [DialogFragmentNavigator].
//     */
//    @NavDestination.ClassType(DialogFragment::class)
//    class Destination(@NonNull fragmentNavigator:Navigator<Destination>) : NavDestination(fragmentNavigator) {
//         var className: String? = null
//
////        /**
////         * Construct a new fragment destination. This destination is not valid until you set the
////         * Fragment via [.setClassName].
////         *
////         * @param navigatorProvider The [NavController] which this destination
////         * will be associated with.
////         */
////        constructor(navigatorProvider: NavigatorProvider) : this(navigatorProvider.getNavigator(NavDialogFragmentNavigator::class) {
////
////        }
//
//        @CallSuper
//        override fun onInflate(context: Context, attrs: AttributeSet) {
//            super.onInflate(context, attrs)
//            val a = context.resources.obtainAttributes(
//                attrs,
//                R.styleable.DialogFragmentNavigator
//            )
//            val className = a.getString(R.styleable.DialogFragmentNavigator_android_name)
//            className?.let { this.className = it }
//            a.recycle()
//        }
//
////        /**
////         * Set the DialogFragment class name associated with this destination
////         * @param className The class name of the DialogFragment to show when you navigate to this
////         * destination
////         * @return this [Destination]
////         */
////        fun setClassName(className: String): Destination {
////            mClassName = className
////            return this
////        }
////
////
////
////        /**
////         * Gets the DialogFragment's class name associated with this destination
////         *
////         * @throws IllegalStateException when no DialogFragment class was set.
////         */
////        val className: String
////            get() {
////                checkNotNull(mClassName) { "DialogFragment class was not set" }
////                return mClassName!!
////            }
//    }
//
//}
