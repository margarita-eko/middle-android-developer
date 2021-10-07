package ru.skillbranch.skillarticles.extensions

import ru.skillbranch.skillarticles.data.AppSettings
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.local.User
import ru.skillbranch.skillarticles.viewmodels.ArticleState

fun ArticleState.toAppSettings(): AppSettings {
    return AppSettings(isDarkMode, isBigText)
}

fun ArticleState.toArticlePersonalInfo(): ArticlePersonalInfo {
    return ArticlePersonalInfo(isLike, isBookmark)
}

fun ArticleState.asMap(): Map<String, Any?> = mapOf(
    "isAuth" to isAuth,
    "isLoadingContent" to isLoadingContent,
    "isLoadingReviews" to isLoadingReviews,
    "isLike" to isLike,
    "isBookmark" to isBookmark,
    "isShowMenu" to isShowMenu,
    "isBigText" to isBigText,
    "isDarkMode" to isDarkMode,
    "isSearch" to isSearch,
    "searchQuery" to searchQuery,
    "searchResults" to searchResults,
    "searchPosition" to searchPosition,
    "shareLink" to shareLink,
    "title" to title,
    "category" to category,
    "categoryIcon" to categoryIcon,
    "date" to date,
    "author" to author,
    "poster" to poster,
    "content" to content,
    "reviews" to reviews,
)

fun User.asMap(): Map<String, Any?> = mapOf(
    "id"  to id,
    "name"  to name,
    "avatar"  to avatar,
    "rating"  to rating,
    "respect"  to respect,
    "about"  to about
)

fun List<Pair<Int,Int>>.groupByBounds(bounds: List<Pair<Int,Int>>): List<List<Pair<Int,Int>>> {
    val listByBounds: MutableList<MutableList<Pair<Int,Int>>> = mutableListOf()
    /*this.forEach { span ->
        bounds.forEach { bound ->
            if (span.first in bound.first..bound.second){
                val index = bounds.indexOf(bound)
                var el = listByBounds.getOrNull(index)
                if (el == null) {
                    val elList = mutableListOf<Pair<Int,Int>>()
                    elList.add(span)
                    listByBounds.add(index,elList)
                }else{
                    el.add(span)
                }
            }
        }
    }*/
    var searchIndexStart = 0
    bounds.forEach { curBound ->
        val listResults: MutableList<Pair<Int,Int>> = mutableListOf()
        for (i in searchIndexStart until this.size) {
            val span = this[i]
            if (span.first > curBound.second){
                searchIndexStart = i
                break
            }
            if (span.first in curBound.first..curBound.second
                && span.second in curBound.first..curBound.second) {
                listResults.add(span)
            }
        }
        /*this.forEach { span->
            if (span.first in curBound.first..curBound.second
                && span.second in curBound.first..curBound.second) {
                listResults.add(span)
            }
        }*/
        listByBounds.add(listResults)
    }
    return listByBounds
}
