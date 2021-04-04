package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.AppSettings
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.viewmodels.ArticleState

/**
 * Type description here....
 *
 * Created by Andrey on 03.04.2021
 */
fun ArticleState.toAppSettings() : AppSettings {
    return AppSettings(isDarkMode, isBigText)
}

fun ArticleState.toArticlePersonalInfo() : ArticlePersonalInfo{
    return ArticlePersonalInfo(isLike, isBookmark)
}