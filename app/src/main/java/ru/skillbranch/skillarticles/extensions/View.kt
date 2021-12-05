package ru.skillbranch.skillarticles.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.widget.NestedScrollView

fun View.setMarginOptionally(
    left:Int = marginLeft,
    top : Int = marginTop,
    right : Int = marginRight,
    bottom : Int = marginBottom
){
    val params = layoutParams
    if (params is ViewGroup.MarginLayoutParams) {
        params.setMargins(left,top,right,bottom)
        layoutParams = params
    }
}

fun View.setPaddingOptionally(
    left: Int = 0,
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0
){
    setPadding(left, top, right, bottom)
}

fun<T : List<Pair<Int,Int>>> T.groupByBounds(bounds: T) :  List<List<Pair<Int,Int>>> =
    bounds.map { bound ->
        this.filter { item ->
            item.first > bound.first && item.second <= bound.second
        }
    }