package ru.skillbranch.skillarticles.ui.articles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.ItemArticleBinding
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.format
import ru.skillbranch.skillarticles.ui.custom.ArticleItemView
import ru.skillbranch.skillarticles.viewmodels.articles.ArticleItem

class ArticlesAdapter(
    private val onClick: (ArticleItem) -> Unit,
    private val onToggleBookmark: (ArticleItem, Boolean) -> Unit
) : ListAdapter<ArticleItem, ArticleVH>(ArticleDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleVH {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleVH(binding)
        //return ArticleVH(ArticleItemView(parent.context))
    }

    override fun onBindViewHolder(holder: ArticleVH, position: Int) {
        holder.bind(getItem(position), onClick, onToggleBookmark)
    }

}

class ArticleDiffCallback: DiffUtil.ItemCallback<ArticleItem>() {
    override fun areItemsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
        return oldItem == newItem
    }
}

/*class ArticleVH(private val container: View): RecyclerView.ViewHolder(container){
    fun bind(
        item: ArticleItem,
        onClick: (ArticleItem) -> Unit,
        onToggleBookmark: (ArticleItem, Boolean) -> Unit
    ){
        (container as ArticleItemView).bind(item, onClick, onToggleBookmark)
    }
}*/

class ArticleVH(private val binding: ItemArticleBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(
        item: ArticleItem,
        onClick: (ArticleItem) -> Unit,
        onToggleBookmark: (ArticleItem, Boolean) -> Unit
    ){
        //val logoSize: Int by lazy { binding.root.dpToIntPx(40) }

        with(binding){
            tvAuthor.text = item.author
            tvDate.text = item.date.format()
            tvTitle.text = item.title
            tvDescription.text = item.description
            tvCommentsCount.text = item.commentCount.toString()
            tvLikesCount.text = item.likeCount.toString()
            tvReadDuration.text = item.readDuration.toString()


            /*Glide.with(binding.root)
                .load(item.categoryIcon)
                .placeholder(R.drawable.logo_placeholder)
                .apply(RequestOptions.circleCropTransform())
                .override(logoSize)
                .into(ivCategory)

            Glide.with(binding.root)
                .load(item.poster)
                .placeholder(R.drawable.poster_placeholder)
                .transform(CenterCrop())
                .into(ivPoster)*/
        }
    }
}