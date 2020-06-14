package com.psvoid.whappens.adapters

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior

// Expressions: https://developer.android.com/topic/libraries/data-binding/expressions

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    //android:visibility="@{isGone ? View.GONE : View.VISIBLE}"
    view.visibility = if (isGone) View.GONE else View.VISIBLE
}

@BindingAdapter("bottomSheetState")
fun bindingBottomSheet(container: View, state: Int) {
    val behavior = BottomSheetBehavior.from(container)
    behavior.state = if (state == 0) BottomSheetBehavior.STATE_HIDDEN else state
}
