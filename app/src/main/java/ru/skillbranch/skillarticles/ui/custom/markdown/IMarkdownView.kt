package ru.skillbranch.skillarticles.ui.custom.markdown

import android.text.Spannable
import android.text.SpannableString
import androidx.core.text.getSpans
import ru.skillbranch.skillarticles.ui.custom.spans.SearchFocusSpan
import ru.skillbranch.skillarticles.ui.custom.spans.SearchSpan
import java.lang.Exception
import kotlin.properties.Delegates

/**
 * Type description here....
 *
 * Created by Andrey on 10.08.2021
 */
interface IMarkdownView {
    var fontSize: Float
    val spannableContent: Spannable


    fun renderSearchResult(
        results: List<Pair<Int, Int>>,
        offset: Int
    ) {
        clearSearchResult()
        val offsetResult = results
            .map { (start, end) -> start.minus(offset) to end.minus(offset) }
        try {
            offsetResult.forEach { (start, end) ->
                spannableContent.setSpan(
                    SearchSpan(),
                    start,
                    end,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

        } catch (e: Exception) {
            println(e)
        }

    }

    fun renderSearchPosition(
        searchPosition: Pair<Int, Int>,
        offset: Int
    ) {
        spannableContent.getSpans<SearchFocusSpan>().forEach { spannableContent.removeSpan(it) }
        spannableContent.setSpan(
            SearchFocusSpan(),
            searchPosition.first.minus(offset),
            searchPosition.second.minus(offset),
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    fun clearSearchResult() {
        spannableContent.getSpans<SearchSpan>().forEach { spannableContent.removeSpan(it) }
    }

}