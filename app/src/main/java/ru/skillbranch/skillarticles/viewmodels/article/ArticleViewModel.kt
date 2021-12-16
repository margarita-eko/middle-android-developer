package ru.skillbranch.skillarticles.viewmodels

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.skillarticles.MainFlowDirections
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.AppSettings
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.asMap
import ru.skillbranch.skillarticles.extensions.format
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.data.repositories.clearContent
import ru.skillbranch.skillarticles.ui.article.ArticleFragmentArgs
import ru.skillbranch.skillarticles.viewmodels.article.IArticleViewModel

class ArticleViewModel(savedStateHandle: SavedStateHandle): BaseViewModel<ArticleState>(ArticleState(),savedStateHandle),
    IArticleViewModel {

    private val repository = ArticleRepository
    private val args: ArticleFragmentArgs = ArticleFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val articleId = args.articleId

    private var clearContent: String? = null

    init {

        //set custom state provider from non serializable or custom states
        savedStateHandle.setSavedStateProvider("state") {
            currentState.toBundle()
        }

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


    override fun getArticleData(): LiveData<ArticleData?>{
        return repository.getArticle(articleId)
    }

    override fun getArticleContent(): LiveData<List<MarkdownElement>?> {
        return repository.loadArticleContent(articleId)
    }

    override fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?>{
        return repository.loadArticlePersonalInfo(articleId)
    }

    //session state
    override fun handleToggleMenu(){
        updateState { state ->
            state.copy(isShowMenu = !state.isShowMenu)
        }
    }

    override fun handleNightMode(){
        val settings = currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }

    // app settings

    override fun handleUpText(){
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    override fun handleDownText(){
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }

    //personal article info
    override fun handleBookmark(){
        val info = currentState.toArtcilePersonalInfo()
        repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))

        val msg = if (currentState.isBookmark) "Add to bookmarks" else "Remove from bookmarks"
        notify(Notify.TextMessage(msg))
    }

    override fun handleLike(){
        Log.e(TAG, "handle like: ")
        val isLiked = currentState.isLike
        val toggleLike = {
            val info = currentState.toArtcilePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
        }

        toggleLike()

        val msg = if (!isLiked) Notify.TextMessage("Mark is liked")
        else{
            Notify.ActionMessage(
                "Don`t like it anymore",
                "No, still like it",
                toggleLike
            )
        }

        notify(msg)
    }

    override fun handleShare(){
        val msg = "Share is not implemented"
        notify(Notify.ErrorMessage(msg, "OK", null))
    }


    override fun handleSearch(query: String?){
        query ?: return

        if (clearContent==null && currentState.content.isNotEmpty())
            clearContent = currentState.content.clearContent()

        val result = clearContent.indexesOf(query)
            .map { it to it + query.length }
        /*val result = currentState.content
                .firstOrNull()
                .indexesOf(query)
                .map { it to it + query.length }*/
        updateState { it.copy(searchQuery = query, searchResults = result) }
    }


    override fun handleSearchMode(isSearch: Boolean) {
        updateState { it.copy(isSearch = isSearch, isShowMenu = false, searchPosition = 0)}
    }

    override fun handleUpResult() {
        updateState { it.copy(searchPosition = it.searchPosition.dec()) }
    }

    override fun handleDownResult() {
        updateState { it.copy(searchPosition = it.searchPosition.inc()) }
    }

    override fun handleCopyCode() {
        notify(Notify.TextMessage("Code copied to clipboard"))
    }

    override fun handleSendMessage(message: String) {
        if (!currentState.isAuth) {
            val action = MainFlowDirections.startLogin((R.id.nav_articles))
            navigate(NavCommand.Action(action))
        }
    }

    companion object{
        private const val TAG = "ArticleViewModel"
    }

}



data class ArticleState(
    val isAuth: Boolean = false,
    val isLoadingContent: Boolean = true,
    val isLoadingReviews: Boolean = true,
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
    val content: List<MarkdownElement> = emptyList(),
    val reviews: List<Any> = emptyList(),
    val message: String? = null

): VMState{

    fun toAppSettings(): AppSettings {
        return AppSettings(isDarkMode,isBigText)
    }

    fun toArtcilePersonalInfo(): ArticlePersonalInfo {
        return ArticlePersonalInfo(isLike = isLike,isBookmark = isBookmark)
    }

    override fun toBundle(): Bundle {
        val map = copy(content = emptyList(), isLoadingContent = true)
            .asMap()
            .toList()
            .toTypedArray()
        return bundleOf(*map)
    }

    override fun fromBundle(bundle: Bundle): ArticleState? {
        val map = bundle.keySet().associateWith { bundle[it] }
        return copy(
            isAuth = map["isAuth"] as Boolean,
            isLoadingContent = map["isLoadingContent"] as Boolean,
            isLoadingReviews = map["isLoadingReviews"] as Boolean,
            isLike = map["isLike"] as Boolean,
            isBookmark = map["isBookmark"] as Boolean,
            isShowMenu = map["isShowMenu"] as Boolean,
            isBigText = map["isBigText"] as Boolean,
            isDarkMode = map["isDarkMode"] as Boolean,
            isSearch = map["isSearch"] as Boolean,
            searchQuery = map["searchQuery"] as String?,
            searchResults = map["searchResults"] as List<Pair<Int, Int>>,
            searchPosition = map["searchPosition"] as Int,
            shareLink = map["shareLink"] as String?,
            title = map["title"] as String?,
            category = map["category"] as String?,
            categoryIcon = map["categoryIcon"] as Any?,
            date = map["date"] as String?,
            author = map["author"] as Any?,
            poster = map["poster"] as String?,
            content = map["content"] as List<MarkdownElement>,
            reviews = map["reviews"] as List<Any>,
            message = map["message"] as String?,
        )
    }

}

data class BottombarData(
    val isLike: Boolean = false,
    val isBookmark: Boolean = false,
    val isShowMenu: Boolean = false,
    val isSearch: Boolean = false,
    val resultsCount: Int = 0,
    val searchPosition: Int = 0
)

data class SubmenuData(
    val isShowMenu: Boolean = false,
    val isBigText: Boolean = false,
    val isDarkMode: Boolean = false
)

fun ArticleState.toBottombarData() = BottombarData(isLike, isBookmark, isShowMenu, isSearch, searchResults.size, searchPosition)
fun ArticleState.toSubmenuData() = SubmenuData(isShowMenu, isBigText, isDarkMode)
