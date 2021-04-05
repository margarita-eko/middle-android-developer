package ru.skillbranch.skillarticles.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.AppSettings
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.format
import java.text.FieldPosition

class ArticleViewModel(private val articleId: String): BaseViewModel<ArticleState>(ArticleState()) {

    private val repository = ArticleRepository
    private var menuIsShown = false

    init {

        subscribeOnDataSource(getArticleData()) { article, state ->
            article ?: return@subscribeOnDataSource null
            state.copy(
                shareLink = article.shareLink,
                title = article.title,
                author = article.author,
                category = article.category,
                categoryIcon = article.categoryIcon,
                date = article.date.format()
            )
        }

        subscribeOnDataSource(getArticleContent()) { content, state ->
            content ?: return@subscribeOnDataSource null
            state.copy(
                isLoadingContent = false,
                content = content
            )
        }

        subscribeOnDataSource(getArticlePersonalInfo()){ info, state ->
            info ?: return@subscribeOnDataSource null
            state.copy(
                isBookmark = info.isBookmark,
                isLike = info.isLike
            )

        }

        subscribeOnDataSource(repository.getAppSettings()){ settings, state ->
            state.copy(
                isDarkMode = settings.isDarkMode,
                isBigText = settings.isBigText
            )
        }

    }

    private fun getArticleData(): LiveData<ArticleData?>{
        return repository.getArticle(articleId)
    }

    private fun getArticleContent(): LiveData<List<Any>?>{
        return repository.loadArticleContent(articleId)
    }

    private fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?>{
        return repository.loadArticlePersonalInfo(articleId)
    }

    //session state
    fun handleToggleMenu(){
        updateState { state ->
            state.copy(isShowMenu = !state.isShowMenu).also { menuIsShown = !state.isShowMenu }
        }
    }

    // app settings

    fun handleUpText(){
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    fun handleDownText(){
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }

    //personal article info
    fun handleBookmark(){
        val info = currentState.toArtcilePersonalInfo()
        repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))

        val msg = if (currentState.isBookmark) "Add to bookmarks" else "Remove from bookmarks"
        notify(Notify.TextMessage(msg))
    }

    fun handleLike(){
        Log.e(TAG, "handle like: ")
        val isLiked = currentState.isLike
        val toggleLike = {
            val info = currentState.toArtcilePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
        }

        toggleLike()

        val msg = if (!isLiked) Notify.TextMessage("Mark as liked")
        else{
            Notify.ActionMessage(
                "Don't like it anymore",
                "No, still like it",
                toggleLike
            )
        }

        notify(msg)
    }

    fun handleShare(){
        val msg = "Share is not implemented"
        notify(Notify.ErrorMessage(msg, "ОК", null))
    }

    fun hideMenu(){
        updateState { it.copy(isShowMenu = false) }
    }

    fun showMenu(){
        updateState { it.copy(isShowMenu = menuIsShown) }
    }

    fun handleSearchQuery(query: String?){
        updateState { it.copy(searchQuery = query) }
    }

    fun handleIsSearch(isSearch: Boolean) {
        updateState { it.copy(isSearch = isSearch)}
    }

    companion object{
        private const val TAG = "ArticleViewModel"
    }

}

data class ArticleState(
    val isAuth: Boolean = false,
    val isLoadingContent: Boolean = false,
    val isLoadingReviews: Boolean = false,
    val isLike: Boolean = false,
    val isBookmark: Boolean = false,
    val isShowMenu: Boolean = false,
    val isBigText: Boolean = false,
    val isDarkMode: Boolean = false,
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val searchResults: List<Pair<Int,Int>> = emptyList(),
    val searchPosition: Int = 0,
    val shareLink: String? = null,
    val title: String? = null,
    val category: String? = null,
    val categoryIcon: Any? = null,
    val date: String? = null,
    val author: Any? = null,
    val poster: String? = null,
    val content: List<Any> = emptyList(),
    val reviews: List<Any> = emptyList()

){

    fun toAppSettings(): AppSettings {
        return AppSettings(isBigText,isDarkMode)
    }

    fun toArtcilePersonalInfo(): ArticlePersonalInfo {
        return ArticlePersonalInfo(isLike = isLike,isBookmark = isBookmark)
    }

}

