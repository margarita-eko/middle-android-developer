package ru.skillbranch.skillarticles.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

fun View.setMarginOptionally(
    left:Int = marginLeft,
    top : Int = marginTop,
    right : Int = marginRight,
    bottom : Int = marginBottom
){
    if (this.layoutParams is ViewGroup.LayoutParams) {
        val marginLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
        marginLayoutParams.setMargins(left,top,right,bottom)
        this.requestLayout()
    }
}

fun View.setPaddingOptionally(
    left:Int = marginLeft,
    top : Int = marginTop,
    right : Int = marginRight,
    bottom : Int = marginBottom
){
    this.setPadding(left,top,right,bottom)
}