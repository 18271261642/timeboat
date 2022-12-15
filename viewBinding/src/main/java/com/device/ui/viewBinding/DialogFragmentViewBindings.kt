@file:Suppress("RedundantVisibilityModifier", "unused")
@file:JvmName("ReflectionDialogFragmentViewBindings")

package com.device.ui.viewBinding


import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment

import androidx.viewbinding.ViewBinding

/**
 * Create new [ViewBinding] associated with the [DialogFragment]'s view
 *
 * @param T Class of expected [ViewBinding] result class
 * @param viewBindingRootId Id of the root view from your custom view
 */
@JvmName("viewBindingDialogFragment")
@Deprecated(
    "Use viewBinding delegate",
    ReplaceWith("viewBinding(viewBindingRootId)", "com.device.ui.viewBinding.viewBinding")
)
public inline fun <reified T : ViewBinding> DialogFragment.dialogViewBinding(
    @IdRes viewBindingRootId: Int
): ViewBindingProperty<DialogFragment, T> {
    return viewBinding(viewBindingRootId)
}

@JvmName("viewBindingDialogFragment")
@Deprecated(
    "Use viewBinding delegate",
    ReplaceWith("viewBinding(viewBindingClass, viewBindingRootId)", "com.device.ui.viewBinding.viewBinding")
)
public fun <T : ViewBinding> DialogFragment.dialogViewBinding(
    viewBindingClass: Class<T>,
    @IdRes viewBindingRootId: Int
): ViewBindingProperty<DialogFragment, T> {
    return viewBinding(viewBindingClass, viewBindingRootId)
}

//no delegate
/**
 * Create new [ViewBinding] associated with the [DialogFragment]
 */
@JvmName("viewBindingDialogFragment")
@Deprecated(
        "Use viewBinding delegate",
        ReplaceWith("viewBinding(viewBinder)", "com.device.ui.viewBinding.viewBinding")
)
public fun <F : DialogFragment, T : ViewBinding> DialogFragment.dialogViewBinding(
        viewBinder: (F) -> T
): ViewBindingProperty<F, T> {
    return viewBinding(viewBinder)
}

/**
 * Create new [ViewBinding] associated with the [DialogFragment]
 *
 * @param vbFactory Function that create new instance of [ViewBinding]. `MyViewBinding::bind` can be used
 */
@JvmName("viewBindingDialogFragment")
@Deprecated(
        "Use viewBinding delegate",
        ReplaceWith("viewBinding(vbFactory, viewProvider)", "com.device.ui.viewBinding.viewBinding")
)
public inline fun <F : DialogFragment, T : ViewBinding> DialogFragment.dialogViewBinding(
        crossinline vbFactory: (View) -> T,
        crossinline viewProvider: (F) -> View
): ViewBindingProperty<F, T> {
    return viewBinding(vbFactory, viewProvider)
}

/**
 * Create new [ViewBinding] associated with the [DialogFragment][this]
 *
 * @param vbFactory Function that create new instance of [ViewBinding]. `MyViewBinding::bind` can be used
 * @param viewBindingRootId Id of the root view from your custom view
 */
@Suppress("unused")
@JvmName("viewBindingDialogFragment")
@Deprecated(
        "Use viewBinding delegate",
        ReplaceWith("viewBinding(vbFactory, viewBindingRootId)", "com.device.ui.viewBinding.viewBinding"),
)
public inline fun <T : ViewBinding> DialogFragment.dialogViewBinding(
        crossinline vbFactory: (View) -> T,
        @IdRes viewBindingRootId: Int
): ViewBindingProperty<DialogFragment, T> {
    return viewBinding(vbFactory, viewBindingRootId)
}



