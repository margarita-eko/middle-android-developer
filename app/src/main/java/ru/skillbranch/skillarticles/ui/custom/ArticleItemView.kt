package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.coordinatorlayout.widget.CoordinatorLayout
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.viewmodels.articles.ArticleItem

class ArticleItemView(baseContext: Context) :
    ViewGroup(ContextThemeWrapper(baseContext, R.style.ArticleBarsTheme), null, 0),
    CoordinatorLayout.AttachedBehavior {
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("Not yet implemented")
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> {
        TODO("Not yet implemented")
    }

    fun bind(
        item: ArticleItem,
        onClick: (ArticleItem) -> Unit,
        onToggleBookmark: (ArticleItem, Boolean) -> Unit
    ){

    }
}