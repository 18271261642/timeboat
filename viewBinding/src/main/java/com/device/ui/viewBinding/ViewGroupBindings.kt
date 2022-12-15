@file:Suppress("RedundantVisibilityModifier", "unused")
@file:JvmName("ReflectionViewGroupBindings")

package com.device.ui.viewBinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.RestrictTo
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.viewbinding.ViewBinding

/**
 * Create new [ViewBinding] associated with the [ViewGroup]
 *
 * @param T Class of expected [ViewBinding] result class
 * @param createMethod Way of how create [ViewBinding]
 * @param lifecycleAware Get [LifecycleOwner] from the [ViewGroup][this] using [ViewTreeLifecycleOwner]
 */
@JvmName("viewBindingFragment")
public inline fun <reified T : ViewBinding> ViewGroup.viewBinding(
    createMethod: CreateMethod = CreateMethod.BIND,
    lifecycleAware: Boolean = false,
    noinline onViewDestroyed: (T) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ViewGroup, T> {
    return viewBinding(T::class.java, createMethod, lifecycleAware, onViewDestroyed)
}

/**
 * Create new [ViewBinding] associated with the [ViewGroup]
 *
 * @param viewBindingClass Class of expected [ViewBinding] result class
 * @param createMethod Way of how create [ViewBinding]
 * @param lifecycleAware Get [LifecycleOwner] from the [ViewGroup][this] using [ViewTreeLifecycleOwner]
 */
@JvmName("viewBindingFragment")
@JvmOverloads
public fun <T : ViewBinding> ViewGroup.viewBinding(
    viewBindingClass: Class<T>,
    createMethod: CreateMethod = CreateMethod.BIND,
    lifecycleAware: Boolean = false,
    onViewDestroyed: (T) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ViewGroup, T> = when (createMethod) {
    CreateMethod.BIND -> viewBinding(lifecycleAware, { viewGroup ->
        ViewBindingCache.getBind(viewBindingClass).bind(viewGroup)
    }, onViewDestroyed)
    CreateMethod.INFLATE ->
        viewBinding(viewBindingClass, attachToRoot = true, onViewDestroyed = onViewDestroyed)
}

/**
 * Inflate new [ViewBinding] with the [ViewGroup][this] as parent
 *
 * @param lifecycleAware Get [LifecycleOwner] from the [ViewGroup][this] using [ViewTreeLifecycleOwner]
 */
@JvmName("viewBindingFragment")
@JvmOverloads
public inline fun <reified T : ViewBinding> ViewGroup.viewBinding(
    attachToRoot: Boolean,
    lifecycleAware: Boolean = false,
    noinline onViewDestroyed: (T) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ViewGroup, T> {
    return viewBinding(T::class.java, attachToRoot, lifecycleAware, onViewDestroyed)
}

/**
 * Inflate new [ViewBinding] with the [ViewGroup][this] as parent
 *
 * @param lifecycleAware Get [LifecycleOwner] from the [ViewGroup][this] using [ViewTreeLifecycleOwner]
 */
@JvmName("viewBindingFragment")
@JvmOverloads
public fun <T : ViewBinding> ViewGroup.viewBinding(
    viewBindingClass: Class<T>,
    attachToRoot: Boolean,
    lifecycleAware: Boolean = false,
    onViewDestroyed: (T) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ViewGroup, T> {
    return viewBinding(lifecycleAware, { viewGroup ->
        ViewBindingCache.getInflateWithLayoutInflater(viewBindingClass)
            .inflate(LayoutInflater.from(context), viewGroup, attachToRoot)
    }, onViewDestroyed)
}

// no reflection
@PublishedApi
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class ViewGroupViewBindingProperty<in V : ViewGroup, out T : ViewBinding>(
    onViewDestroyed: (T) -> Unit,
    viewBinder: (V) -> T
) : LifecycleViewBindingProperty<V, T>(viewBinder, onViewDestroyed) {

    override fun getLifecycleOwner(thisRef: V): LifecycleOwner {
        return checkNotNull(ViewTreeLifecycleOwner.get(thisRef)) {
            "Fragment doesn't have view associated with it or the view has been destroyed"
        }
    }
}

/**
 * Create new [ViewBinding] associated with the [ViewGroup]
 *
 * @param vbFactory Function that create new instance of [ViewBinding]. `MyViewBinding::bind` can be used
 */
inline fun <T : ViewBinding> ViewGroup.viewBinding(
    crossinline vbFactory: (ViewGroup) -> T,
): ViewBindingProperty<ViewGroup, T> {
    return viewBinding(lifecycleAware = false, vbFactory)
}

/**
 * Create new [ViewBinding] associated with the [ViewGroup]
 *
 * @param vbFactory Function that create new instance of [ViewBinding]. `MyViewBinding::bind` can be used
 * @param lifecycleAware Get [LifecycleOwner] from the [ViewGroup][this] using [ViewTreeLifecycleOwner]
 */
inline fun <T : ViewBinding> ViewGroup.viewBinding(
    lifecycleAware: Boolean,
    crossinline vbFactory: (ViewGroup) -> T,
): ViewBindingProperty<ViewGroup, T> {
    return viewBinding(lifecycleAware, vbFactory, emptyVbCallback())
}

/**
 * Create new [ViewBinding] associated with the [ViewGroup]
 *
 * @param vbFactory Function that create new instance of [ViewBinding]. `MyViewBinding::bind` can be used
 * @param lifecycleAware Get [LifecycleOwner] from the [ViewGroup][this] using [ViewTreeLifecycleOwner]
 */
inline fun <T : ViewBinding> ViewGroup.viewBinding(
    lifecycleAware: Boolean,
    crossinline vbFactory: (ViewGroup) -> T,
    noinline onViewDestroyed: (T) -> Unit,
): ViewBindingProperty<ViewGroup, T> {
    return when {
        isInEditMode -> EagerViewBindingProperty(vbFactory(this))
        lifecycleAware -> ViewGroupViewBindingProperty(onViewDestroyed) { viewGroup -> vbFactory(viewGroup) }
        else -> LazyViewBindingProperty(onViewDestroyed) { viewGroup -> vbFactory(viewGroup) }
    }
}

/**
 * Create new [ViewBinding] associated with the [ViewGroup]
 *
 * @param vbFactory Function that create new instance of [ViewBinding]. `MyViewBinding::bind` can be used
 * @param viewBindingRootId Root view's id that will be used as root for the view binding
 */
@Deprecated("Order of arguments was changed", ReplaceWith("viewBinding(viewBindingRootId, vbFactory)"))
inline fun <T : ViewBinding> ViewGroup.viewBinding(
    crossinline vbFactory: (View) -> T,
    @IdRes viewBindingRootId: Int,
): ViewBindingProperty<ViewGroup, T> {
    return viewBinding(viewBindingRootId, vbFactory, emptyVbCallback())
}

/**
 * Create new [ViewBinding] associated with the [ViewGroup]
 *
 * @param vbFactory Function that create new instance of [ViewBinding]. `MyViewBinding::bind` can be used
 * @param viewBindingRootId Root view's id that will be used as root for the view binding
 */
inline fun <T : ViewBinding> ViewGroup.viewBinding(
    @IdRes viewBindingRootId: Int,
    crossinline vbFactory: (View) -> T,
    noinline onViewDestroyed: (T) -> Unit,
): ViewBindingProperty<ViewGroup, T> {
    return viewBinding(viewBindingRootId, lifecycleAware = false, vbFactory, onViewDestroyed)
}

/**
 * Create new [ViewBinding] associated with the [ViewGroup]
 *
 * @param vbFactory Function that create new instance of [ViewBinding]. `MyViewBinding::bind` can be used
 * @param viewBindingRootId Root view's id that will be used as root for the view binding
 * @param lifecycleAware Get [LifecycleOwner] from the [ViewGroup][this] using [ViewTreeLifecycleOwner]
 */
inline fun <T : ViewBinding> ViewGroup.viewBinding(
    @IdRes viewBindingRootId: Int,
    lifecycleAware: Boolean,
    crossinline vbFactory: (View) -> T,
): ViewBindingProperty<ViewGroup, T> {
    return viewBinding(viewBindingRootId, lifecycleAware, vbFactory, emptyVbCallback())
}


/**
 * Create new [ViewBinding] associated with the [ViewGroup]
 *
 * @param vbFactory Function that create new instance of [ViewBinding]. `MyViewBinding::bind` can be used
 * @param viewBindingRootId Root view's id that will be used as root for the view binding
 * @param lifecycleAware Get [LifecycleOwner] from the [ViewGroup][this] using [ViewTreeLifecycleOwner]
 */
inline fun <T : ViewBinding> ViewGroup.viewBinding(
    @IdRes viewBindingRootId: Int,
    lifecycleAware: Boolean,
    crossinline vbFactory: (View) -> T,
    noinline onViewDestroyed: (T) -> Unit,
): ViewBindingProperty<ViewGroup, T> {
    return when {
        isInEditMode -> EagerViewBindingProperty(vbFactory(this))
        lifecycleAware -> ViewGroupViewBindingProperty(onViewDestroyed) { viewGroup -> vbFactory(viewGroup) }
        else -> LazyViewBindingProperty(onViewDestroyed) { viewGroup: ViewGroup ->
            vbFactory(viewGroup.requireViewByIdCompat(viewBindingRootId))
        }
    }
}
