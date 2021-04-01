package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import ru.skillbranch.skillarticles.R

/**
 * Type description here....
 *
 * Created by Andrey on 01.04.2021
 */
class Bottombar @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attr, defStyleAttr) {

    init {
        val view = View.inflate(context, R.layout.layout_bottombar, null)
        addView(view)
    }

}