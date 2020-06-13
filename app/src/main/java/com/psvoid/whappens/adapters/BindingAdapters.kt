package com.psvoid.whappens.adapters

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) View.GONE else View.VISIBLE
}

@BindingAdapter("bottomSheetState")
fun bindingBottomSheet(container: View, state: Int) {
    val behavior = BottomSheetBehavior.from(container)
    behavior.state = state
}
