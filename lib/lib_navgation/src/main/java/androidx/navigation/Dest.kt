package androidx.navigation

import android.os.Bundle
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.RestrictTo
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import kotlin.reflect.KClass

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/7/6 11:12
 *    desc   : 启动目的地描述信息
 */
class Dest private constructor(
    destId: Int,
    className: String?,
    arguments: Bundle?,
    popDest: Dest?,
    popInclusive: Boolean,
    singleTop: Boolean,
    extras: Navigator.Extras?,
    enterAnim: Int,
    exitAnim: Int,
    popEnterAnim: Int,
    popExitAnim: Int,
) {

    companion object {

        @JvmOverloads
        fun createDest(kClass: KClass<*>, arguments: Bundle? = null): Dest {
            return Builder(kClass).setArguments(arguments).build()
        }

        @JvmOverloads
        fun createDest(java: Class<*>, arguments: Bundle? = null): Dest {
            return Builder(java).setArguments(arguments).build()
        }

        @JvmOverloads
        fun createDest(className: String, arguments: Bundle? = null): Dest {
            return Builder(className).setArguments(arguments).build()
        }

        @JvmOverloads
        fun createDest(destinationId: Int, arguments: Bundle? = null): Dest {
            return Builder(destinationId).setArguments(arguments).build()
        }

    }

    val destId: Int = destId
    val className: String? = className
    val arguments: Bundle? = arguments
    val popDest: Dest? = popDest
    val popInclusive = popInclusive
    val singleTop = singleTop
    val extras: Navigator.Extras? = extras

    @AnimRes
    @AnimatorRes
    val enterAnim = enterAnim

    @AnimRes
    @AnimatorRes
    val exitAnim = exitAnim

    @AnimRes
    @AnimatorRes
    val popEnterAnim = popEnterAnim

    @AnimRes
    @AnimatorRes
    val popExitAnim = popExitAnim

    private fun getDestinationId(): Int {
        return if (destId != 0) {
            destId
        } else {
            className?.let {
                NavUtil.createDestinationId(it)
            } ?: 0
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    internal fun getDestinationClassName(navController: NavController): String? {
        return className ?: kotlin.run {
            val destination = navController.findDestination(destId) ?: return null
            when (destination) {
                is FragmentNavigator.Destination -> {
                    destination.className
                }
                is DialogFragmentNavigator.Destination -> {
                    destination.className
                }
                else -> {
                    return null
                }
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    internal fun toNavOptions(): NavOptions {
        val builder = toAnimOptionsBuilder().setLaunchSingleTop(singleTop)
        popDest?.let { builder.setPopUpTo(it.getDestinationId(), popInclusive) }
        return builder.build()
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    internal fun toAnimOptionsBuilder(): NavOptions.Builder {
        return NavOptions.Builder()
            .setEnterAnim(enterAnim)
            .setExitAnim(exitAnim)
            .setPopEnterAnim(popEnterAnim)
            .setPopExitAnim(popExitAnim)
    }

    class Builder {


        constructor(destinationId: Int) {
            mDestId = destinationId
        }

        constructor (kClass: KClass<*>) {
            mClassName = kClass.java.name
        }

        constructor (java: Class<*>) {
            mClassName = java.name
        }

        constructor (className: String) {
            mClassName = className
        }

        constructor (dest: Dest) {
            mDestId = dest.destId
            mClassName = dest.className
            mArguments = dest.arguments
            mExtras = dest.extras
            mSingleTop = dest.singleTop
            mPopDest = dest.popDest
            mPopInclusive = dest.popInclusive
            mEnterAnim = dest.enterAnim
            mExitAnim = dest.exitAnim
            mPopEnterAnim = dest.popEnterAnim
            mPopExitAnim = dest.popExitAnim
        }


        private var mArguments: Bundle? = null
        private var mExtras: Navigator.Extras? = null
        private var mDestId: Int = 0
        private var mClassName: String? = null
        private var mSingleTop: Boolean = false
        private var mPopDest: Dest? = null
        private var mPopInclusive: Boolean = false

        @AnimRes
        @AnimatorRes
        private var mEnterAnim = R.anim.def_h_enter_anim

        @AnimRes
        @AnimatorRes
        private var mExitAnim = R.anim.def_h_exit_anim

        @AnimRes
        @AnimatorRes
        private var mPopEnterAnim = R.anim.def_h_pop_enter_anim

        @AnimRes
        @AnimatorRes
        private var mPopExitAnim = R.anim.def_h_pop_exit_anim

        fun setArguments(arguments: Bundle?): Builder {
            this.mArguments = arguments
            return this
        }

        fun setLaunchSingleTop(singleTop: Boolean): Builder {
            mSingleTop = singleTop
            return this
        }

        fun setPopUpTo(kClass: KClass<*>, inclusive: Boolean): Builder {
            this.mPopDest = createDest(kClass)
            this.mPopInclusive = inclusive
            return this
        }

        fun setPopUpTo(className: String, inclusive: Boolean): Builder {
            this.mPopDest = createDest(className)
            this.mPopInclusive = inclusive
            return this
        }

        fun setPopUpTo(destinationId: Int, inclusive: Boolean): Builder {
            this.mPopDest = createDest(destinationId)
            this.mPopInclusive = inclusive
            return this
        }

        fun setPopUpTo(dest: Dest, inclusive: Boolean): Builder {
            this.mPopDest = dest
            this.mPopInclusive = inclusive
            return this
        }

        fun setExtras(extras: Navigator.Extras): Builder {
            this.mExtras = extras
            return this
        }

        fun verticalAnim(): Builder {
            mEnterAnim = R.anim.def_v_enter_anim
            mExitAnim = R.anim.def_v_exit_anim
            mPopEnterAnim = R.anim.def_v_pop_enter_anim
            mPopExitAnim = R.anim.def_v_pop_exit_anim
            return this
        }

        fun clearAnim(): Builder {
            mEnterAnim = 0
            mExitAnim = 0
            mPopEnterAnim = 0
            mPopExitAnim = 0
            return this
        }

        fun setEnterAnim(@AnimRes @AnimatorRes enterAnim: Int): Builder {
            mEnterAnim = enterAnim
            return this
        }

        fun setExitAnim(@AnimRes @AnimatorRes exitAnim: Int): Builder {
            mExitAnim = exitAnim
            return this
        }

        fun setPopEnterAnim(@AnimRes @AnimatorRes popEnterAnim: Int): Builder {
            mPopEnterAnim = popEnterAnim
            return this
        }

        fun setPopExitAnim(@AnimRes @AnimatorRes popExitAnim: Int): Builder {
            mPopExitAnim = popExitAnim
            return this
        }


        fun build(): Dest {
            if (mExtras != null) clearAnim()   //元素共享动画与普通过度动画不能共存
            return Dest(
                destId = mDestId,
                className = mClassName,
                arguments = mArguments,
                popDest = mPopDest,
                popInclusive = mPopInclusive,
                singleTop = mSingleTop,
                extras = mExtras,
                enterAnim = mEnterAnim,
                exitAnim = mExitAnim,
                popEnterAnim = mPopEnterAnim,
                popExitAnim = mPopExitAnim
            )
        }
    }


}