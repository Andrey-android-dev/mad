package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginRight
import ru.skillbranch.skillarticles.ui.custom.ArticleSubmenu
import ru.skillbranch.skillarticles.ui.custom.Bottombar

/**
 * Type description here....
 *
 * Created by Andrey on 08.04.2021
 */
class SubmenuBehavior : CoordinatorLayout.Behavior<ArticleSubmenu>() {


    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: ArticleSubmenu,
        dependency: View
    ): Boolean {
        return dependency is Bottombar
    }


    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: ArticleSubmenu,
        dependency: View
    ): Boolean {

        if (child.isOpen && dependency is Bottombar && dependency.translationY >= 0) {
            val fraction = dependency.translationY / dependency.minHeight
            child.translationX = (child.width + child.marginRight) * fraction
            return true
        } else {
            return false
        }

    }

}