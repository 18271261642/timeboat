@file:Suppress("RedundantVisibilityModifier", "unused")
@file:JvmName("ReflectionActivityViewBindings")

package com.device.ui.viewBinding

import android.app.Activity
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding


/**
 * Create new [ViewBinding] associated with the [Activity][ComponentActivity]
 *
 * @param T Class of expected [ViewBinding] result class
 * @param viewBindingRootId Root view's id that will be used as root for the view binding
 */
@JvmName("viewBindingActivity")
public inline fun <reified T : ViewBinding> ComponentActivity.viewBinding(
    @IdRes viewBindingRootId: Int,
    noinline onViewDestroyed: (T) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ComponentActivity, T> {
    return viewBinding(T::class.java, viewBindingRootId, onViewDestroyed)
}

/**
 * Create new [ViewBinding] associated with the [Activity][ComponentActivity]
 *
 * @param viewBindingClass Class of expected [ViewBinding] result class
 * @param viewBindingRootId Root view's id that will be used as root for the view binding
 */
@JvmName("viewBindingActivity")
public fun <T : ViewBinding> ComponentActivity.viewBinding(
    viewBindingClass: Class<T>,
    @IdRes viewBindingRootId: Int,
    onViewDestroyed: (T) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ComponentActivity, T> {
    return viewBinding(onViewDestroyed) { activity ->
        val rootView = ActivityCompat.requireViewById<View>(activity, viewBindingRootId)
        ViewBindingCache.getBind(viewBindingClass).bind(rootView)
    }
}

/**
 * Create new [ViewBinding] associated with the [Activity]
 *
 * @param viewBindingClass Class of expected [ViewBinding] result class
 * @param rootViewProvider Provider of root view for the [ViewBinding] from the [Activity][this]
 */
@JvmName("viewBindingActivity")
public fun <T : ViewBinding> ComponentActivity.viewBinding(
    viewBindingClass: Class<T>,
    rootViewProvider: (ComponentActivity) -> View,
    onViewDestroyed: (T) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ComponentActivity, T> {
    return viewBinding(onViewDestroyed) { activity ->
        ViewBindingCache.getBind(viewBindingClass).bind(rootViewProvider(activity))
    }
}

/**
 * Create new [ViewBinding] associated with the [Activity][ComponentActivity].
 * You need to set [ViewBinding.getRoot] as content view using [Activity.setContentView].
 *
 * @param T Class of expected [ViewBinding] result class
 */
@JvmName("inflateViewBindingActivity")
public inline fun <reified T : ViewBinding> ComponentActivity.viewBinding(
    createMethod: CreateMethod = CreateMethod.BIND,
    noinline onViewDestroyed: (T) -> Unit = emptyVbCallback(),
) = viewBinding(T::class.java, createMethod, onViewDestroyed)

@JvmName("inflateViewBindingActivity")
public fun <T : ViewBinding> ComponentActivity.viewBinding(
    viewBindingClass: Class<T>,
    createMethod: CreateMethod = CreateMethod.BIND,
    onViewDestroyed: (T) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ComponentActivity, T> = when (createMethod) {
    CreateMethod.BIND -> viewBinding(viewBindingClass, ::findRootView, onViewDestroyed)
    CreateMethod.INFLATE -> {
        activityViewBinding(onViewDestroyed, viewNeedInitialization = false) {
            ViewBindingCache.getInflateWithLayoutInflater(viewBindingClass)
                .inflate(layoutInflater, null, false)
        }
    }
 }
//no delegate
@RestrictTo(LIBRARY)
private class ActivityViewBindingProperty<in A : ComponentActivity, out T : ViewBinding>(
        onViewDestroyed: (T) -> Unit,
        private val viewNeedInitialization: Boolean = true,
        viewBinder: (A) -> T
) : LifecycleViewBindingProperty<A, T>(viewBinder, onViewDestroyed) {

    override fun getLifecycleOwner(thisRef: A): LifecycleOwner = thisRef

    override fun isViewInitialized(thisRef: A): Boolean {
        return !viewNeedInitialization || thisRef.window != null
    }
}

/**
 * Create new [ViewBinding] associated with the [Activity][ComponentActivity] and allow customize how
 * a [View] will be bounded to the view binding.
 */
@JvmName("viewBindingActivity")
public fun <A : ComponentActivity, T : ViewBinding> ComponentActivity.viewBinding(
        viewBinder: (A) -> T
): ViewBindingProperty<A, T> {
    return viewBinding(emptyVbCallback(), viewBinder)
}

/**
 * Create new [ViewBinding] associated with the [Activity][ComponentActivity] and allow customize how
 * a [View] will be bounded to the view binding.
 */
@JvmName("viewBindingActivityWithCallbacks")
public fun <A : ComponentActivity, T : ViewBinding> ComponentActivity.viewBinding(
        onViewDestroyed: (T) -> Unit = {},
        viewBinder: (A) -> T
): ViewBindingProperty<A, T> {
    return ActivityViewBindingProperty(onViewDestroyed, viewBinder = viewBinder)
}

/**
 * Create new [ViewBinding] associated with the [Activity][ComponentActivity] and allow customize how
 * a [View] will be bounded to the view binding.
 */
@JvmName("viewBindingActivity")
public inline fun <A : ComponentActivity, T : ViewBinding> ComponentActivity.viewBinding(
        crossinline vbFactory: (View) -> T,
        crossinline viewProvider: (A) -> View = ::findRootView
): ViewBindingProperty<A, T> {
    return viewBinding(emptyVbCallback(), vbFactory, viewProvider)
}

/**
 * Create new [ViewBinding] associated with the [Activity][ComponentActivity] and allow customize how
 * a [View] will be bounded to the view binding.
 */
@JvmName("viewBindingActivityWithCallbacks")
public inline fun <A : ComponentActivity, T : ViewBinding> ComponentActivity.viewBinding(
        noinline onViewDestroyed: (T) -> Unit = {},
        crossinline vbFactory: (View) -> T,
        crossinline viewProvider: (A) -> View = ::findRootView
): ViewBindingProperty<A, T> {
    return viewBinding(onViewDestroyed) { activity -> vbFactory(viewProvider(activity)) }
}

/**
 * Create new [ViewBinding] associated with the [Activity][this] and allow customize how
 * a [View] will be bounded to the view binding.
 *
 * @param vbFactory Function that create new instance of [ViewBinding]. `MyViewBinding::bind` can be used
 * @param viewBindingRootId Root view's id that will be used as root for the view binding
 */
@Suppress("unused")
@JvmName("viewBindingActivity")
public inline fun <T : ViewBinding> ComponentActivity.viewBinding(
        crossinline vbFactory: (View) -> T,
        @IdRes viewBindingRootId: Int
): ViewBindingProperty<ComponentActivity, T> {
    return viewBinding(emptyVbCallback(), vbFactory, viewBindingRootId)
}

/**
 * Create new [ViewBinding] associated with the [Activity][this] and allow customize how
 * a [View] will be bounded to the view binding.
 *
 * @param vbFactory Function that create new instance of [ViewBinding]. `MyViewBinding::bind` can be used
 * @param viewBindingRootId Root view's id that will be used as root for the view binding
 */
@Suppress("unused")
@JvmName("viewBindingActivity")
public inline fun <T : ViewBinding> ComponentActivity.viewBinding(
        noinline onViewDestroyed: (T) -> Unit = {},
        crossinline vbFactory: (View) -> T,
        @IdRes viewBindingRootId: Int
): ViewBindingProperty<ComponentActivity, T> {
    return viewBinding(onViewDestroyed) { activity ->
        vbFactory(activity.requireViewByIdCompat(viewBindingRootId))
    }
}

@RestrictTo(LIBRARY_GROUP)
fun <A : ComponentActivity, T : ViewBinding> activityViewBinding(
        onViewDestroyed: (T) -> Unit,
        viewNeedInitialization: Boolean = true,
        viewBinder: (A) -> T
): ViewBindingProperty<A, T> {
    return ActivityViewBindingProperty(onViewDestroyed, viewNeedInitialization, viewBinder)
}

