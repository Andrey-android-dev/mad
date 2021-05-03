package ru.skillbranch.skillarticles.ui

import ru.skillbranch.skillarticles.ui.custom.Bottombar
import ru.skillbranch.skillarticles.viewmodels.ArticleState

/**
 * Type description here....
 *
 * Created by Andrey on 25.04.2021
 */
interface IArticleView {

    fun setupSubmenu()
    fun setupBottombar()
//    fun renderBottombar(data: BottombarData)
//    fun renderSubmenu(data: SubmenuData)
    fun renderUi(data: ArticleState)
    fun setupToolbar()
    fun renderSearchResult(searchResult: List<Pair<Int,Int>>)
    fun renderSearchPosition(searchPosition: Int)
    fun clearSearchResult()
    fun showSearchBar()
    fun hideSearchBar()

}