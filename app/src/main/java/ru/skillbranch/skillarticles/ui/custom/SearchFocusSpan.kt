package ru.skillbranch.skillarticles.ui.custom

import android.text.TextPaint

/**
 * Type description here....
 *
 * Created by Andrey on 10.05.2021
 */
class SearchFocusSpan(private val bgColor: Int, private val fgColor: Int) : SearchSpan(bgColor, fgColor) {

    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.bgColor = bgColor
        textPaint.color = fgColor
    }

}