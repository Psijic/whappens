package com.psvoid.whappens.adapters

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.psvoid.whappens.R

// Expressions: https://developer.android.com/topic/libraries/data-binding/expressions

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    //android:visibility="@{isGone ? View.GONE : View.VISIBLE}"
    view.visibility = if (isGone) View.GONE else View.VISIBLE
}

@BindingAdapter("bottomSheetState")
fun bindBottomSheetState(container: View, state: Int) {
    val behavior = BottomSheetBehavior.from(container)
    behavior.state = if (state == 0) BottomSheetBehavior.STATE_HIDDEN else state
}

@BindingAdapter(value = ["imageFromUrl", "category"], requireAll = false)
fun bindImageFromUrl(view: ImageView, imageUrl: String?, category: String?) {
    Glide.with(view)
        .load(imageUrl)
        .placeholder(R.drawable.discoveries)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(view)
}