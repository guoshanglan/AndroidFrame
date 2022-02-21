package base2app.viewbinding

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.annotation.RestrictTo
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.get

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.fragment.DestinationFragment
import androidx.recyclerview.widget.RecyclerView

import base2app.dialog.BaseBottomSheetsDialog
import base2app.dialog.BaseDialog
import com.example.lib_base.R
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 *    date   : 2021/10/20 15:02
 *    desc   :
 */

// -------------------------------------------------------------------------------------
// ViewBindingProperty for Activity
// -------------------------------------------------------------------------------------

@JvmName("viewBindingActivity")
inline fun <V> FragmentActivity.viewBinding(
    crossinline viewBinder: (View) -> V,
    crossinline viewProvider: (ComponentActivity) -> View = ::findRootView
): ViewBindingProperty<ComponentActivity, V> =
    ActivityViewBindingProperty { activity: ComponentActivity ->
        viewBinder(viewProvider(activity))
    }

@JvmName("viewBindingActivity")
inline fun <V> FragmentActivity.viewBinding(
    crossinline viewBinder: (View) -> V,
    @IdRes viewBindingRootId: Int
): ViewBindingProperty<ComponentActivity, V> =
    ActivityViewBindingProperty { activity: ComponentActivity ->
        viewBinder(activity.requireViewByIdCompat(viewBindingRootId))
    }

// -------------------------------------------------------------------------------------
// ViewBindingProperty for Fragment / DialogFragment
// -------------------------------------------------------------------------------------

@Suppress("UNCHECKED_CAST")
@JvmName("ViewBindingFragment")
inline fun <F : Fragment, V> Fragment.viewBinding(
    crossinline viewBinder: (View) -> V,
    crossinline viewProvider: (F) -> View = { f ->
        val v = f.requireView()
        if (f is DestinationFragment) {
            v.findViewById<ViewGroup>(androidx.navigation.R.id.destination_root_view)?.let { it[0] }
                ?: v
        } else {
            v
        }
    }
): ViewBindingProperty<F, V> = when (this) {
    is DialogFragment -> DialogFragmentViewBindingProperty { fragment: F ->
        viewBinder(
            viewProvider(
                fragment
            )
        )
    } as ViewBindingProperty<F, V>
    else -> FragmentViewBindingProperty { fragment: F -> viewBinder(viewProvider(fragment)) }
}

@Suppress("UNCHECKED_CAST")
@JvmName("ViewBindingFragment")
inline fun <F : Fragment, V> Fragment.viewBinding(
    crossinline viewBinder: (View) -> V,
    @IdRes viewBindingRootId: Int
): ViewBindingProperty<F, V> = when (this) {
    is DialogFragment -> viewBinding(viewBinder) { fragment: DialogFragment ->
        fragment.getRootView(
            viewBindingRootId
        )
    } as ViewBindingProperty<F, V>
    else -> viewBinding(viewBinder) { fragment: F ->
        fragment.requireView().requireViewByIdCompat(viewBindingRootId)
    }
}

// -------------------------------------------------------------------------------------
// ViewBindingProperty for ViewGroup
// -------------------------------------------------------------------------------------

@JvmName("viewBindingViewGroup")
inline fun <V> ViewGroup.viewBinding(
    crossinline viewBinder: (View) -> V,
    crossinline viewProvider: (ViewGroup) -> View = { this }
): ViewBindingProperty<ViewGroup, V> = LazyViewBindingProperty { viewGroup: ViewGroup ->
    viewBinder(viewProvider(viewGroup))
}

@JvmName("viewBindingViewGroup")
inline fun <V> ViewGroup.viewBinding(
    crossinline viewBinder: (View) -> V,
    @IdRes viewBindingRootId: Int
): ViewBindingProperty<ViewGroup, V> = LazyViewBindingProperty { viewGroup: ViewGroup ->
    viewBinder(viewGroup.requireViewByIdCompat(viewBindingRootId))
}

@JvmName("viewBindingViewGroup")
inline fun <V> ViewGroup.viewBinding(
    crossinline viewInflater: (LayoutInflater) -> V,
): ViewBindingProperty<ViewGroup, V> = LazyViewBindingProperty { viewGroup: ViewGroup ->
    viewInflater(LayoutInflater.from(viewGroup.context))
}

// -------------------------------------------------------------------------------------
// ViewBindingProperty for RecyclerView#ViewHolder
// -------------------------------------------------------------------------------------

@JvmName("viewBindingViewHolder")
inline fun <V> RecyclerView.ViewHolder.viewBinding(
    crossinline viewBinder: (View) -> V,
    crossinline viewProvider: (RecyclerView.ViewHolder) -> View = RecyclerView.ViewHolder::itemView
): ViewBindingProperty<RecyclerView.ViewHolder, V> =
    LazyViewBindingProperty { holder: RecyclerView.ViewHolder ->
        viewBinder(viewProvider(holder))
    }

@JvmName("viewBindingViewHolder")
inline fun <V> RecyclerView.ViewHolder.viewBinding(
    crossinline viewBinder: (View) -> V,
    @IdRes viewBindingRootId: Int
): ViewBindingProperty<RecyclerView.ViewHolder, V> =
    LazyViewBindingProperty { holder: RecyclerView.ViewHolder ->
        viewBinder(holder.itemView.requireViewByIdCompat(viewBindingRootId))
    }

// -------------------------------------------------------------------------------------
// ViewBindingProperty for zhuorui Dialog
// -------------------------------------------------------------------------------------

inline fun <V> BaseDialog.viewBinding(
    crossinline viewBinder: (View) -> V,
    crossinline viewProvider: (BaseDialog) -> View = BaseDialog::requireView
): ViewBindingProperty<BaseDialog, V> =
    LazyViewBindingProperty { dialog: BaseDialog ->
        viewBinder(viewProvider(dialog))
    }

inline fun <V> BaseBottomSheetsDialog.viewBinding(
    crossinline viewBinder: (View) -> V,
    crossinline viewProvider: (BaseBottomSheetsDialog) -> View = {
        it.requireView().findViewById<ViewGroup>(R.id.design_bottom_sheet)[0]
    }
): ViewBindingProperty<BaseBottomSheetsDialog, V> =
    LazyViewBindingProperty { dialog: BaseBottomSheetsDialog ->
        viewBinder(viewProvider(dialog))
    }

// -------------------------------------------------------------------------------------
// ViewBindingProperty
// -------------------------------------------------------------------------------------

private const val TAG = "ViewBindingProperty"

interface ViewBindingProperty<in R : Any, out V> : ReadOnlyProperty<R, V> {
    @MainThread
    fun clear()
}

class LazyViewBindingProperty<in R : Any, out V>(
    private val viewBinder: (R) -> V
) : ViewBindingProperty<R, V> {

    private var viewBinding: V? = null

    @Suppress("UNCHECKED_CAST")
    @MainThread
    override fun getValue(thisRef: R, property: KProperty<*>): V {
        // Already bound
        viewBinding?.let { return it }
        return viewBinder(thisRef).also {
            this.viewBinding = it
        }
    }

    @MainThread
    override fun clear() {
        viewBinding = null
    }
}

abstract class LifecycleViewBindingProperty<in R : Any, out V>(
    private val viewBinder: (R) -> V
) : ViewBindingProperty<R, V> {

    private var viewBinding: V? = null

    protected abstract fun getLifecycleOwner(thisRef: R): LifecycleOwner

    @MainThread
    override fun getValue(thisRef: R, property: KProperty<*>): V {
        // Already bound
        viewBinding?.let { return it }
        val lifecycle = getLifecycleOwner(thisRef).lifecycle
        val viewBinding = viewBinder(thisRef)
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            Log.w(
                TAG, "Access to viewBinding after Lifecycle is destroyed or hasn't created yet. " +
                        "The instance of viewBinding will be not cached."
            )
            // We can access to ViewBinding after Fragment.onDestroyView(), but don't save it to prevent memory leak
        } else {
            lifecycle.addObserver(ClearOnDestroyLifecycleObserver(this))
            this.viewBinding = viewBinding
        }
        return viewBinding
    }

    @MainThread
    override fun clear() {
        viewBinding = null
    }

    private class ClearOnDestroyLifecycleObserver(
        private val property: LifecycleViewBindingProperty<*, *>
    ) : LifecycleObserver {

        private companion object {
            private val mainHandler = Handler(Looper.getMainLooper())
        }

        @MainThread
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy(owner: LifecycleOwner) {
            mainHandler.post { property.clear() }
        }
    }
}

class FragmentViewBindingProperty<in F : Fragment, out V>(
    viewBinder: (F) -> V
) : LifecycleViewBindingProperty<F, V>(viewBinder) {

    override fun getLifecycleOwner(thisRef: F): LifecycleOwner {
        try {
            return thisRef
        } catch (ignored: IllegalStateException) {
            error("Fragment doesn't have view associated with it or the view has been destroyed")
        }
    }
}

class DialogFragmentViewBindingProperty<in F : DialogFragment, out V>(
    viewBinder: (F) -> V
) : LifecycleViewBindingProperty<F, V>(viewBinder) {

    override fun getLifecycleOwner(thisRef: F): LifecycleOwner {
        return if (thisRef.showsDialog) {
            thisRef
        } else {
            try {
                thisRef
            } catch (ignored: IllegalStateException) {
                error("Fragment doesn't have view associated with it or the view has been destroyed")
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// Utils
// -------------------------------------------------------------------------------------

@RestrictTo(RestrictTo.Scope.LIBRARY)
class ActivityViewBindingProperty<in A : ComponentActivity, out V>(
    viewBinder: (A) -> V
) : LifecycleViewBindingProperty<A, V>(viewBinder) {

    override fun getLifecycleOwner(thisRef: A): LifecycleOwner {
        return thisRef
    }
}

fun <V : View> View.requireViewByIdCompat(@IdRes id: Int): V {
    return ViewCompat.requireViewById(this, id)
}

fun <V : View> Activity.requireViewByIdCompat(@IdRes id: Int): V {
    return ActivityCompat.requireViewById(this, id)
}

/**
 * Utility to find root view for ViewBinding in Activity
 */
fun findRootView(activity: Activity): View {
    val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
    checkNotNull(contentView) { "Activity has no content view" }
    return when (val size = contentView.childCount) {
        0 -> error("Content view has no children. Provide root view explicitly")
        else -> {
            for (i in 0 until size) {
                val v = contentView.getChildAt(i)
                if (v is ViewGroup) {
                    return v
                }
            }
            error("More than one child view found in Activity content view")
        }
    }
}

fun DialogFragment.getRootView(viewBindingRootId: Int): View {
    val dialog = checkNotNull(dialog) {
        "DialogFragment doesn't have dialog. Use viewBinding delegate after onCreateDialog"
    }
    val window = checkNotNull(dialog.window) { "Fragment's Dialog has no window" }
    return with(window.decorView) {
        if (viewBindingRootId != 0) requireViewByIdCompat(
            viewBindingRootId
        ) else this
    }
}
