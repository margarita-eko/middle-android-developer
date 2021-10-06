package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.content.res.AppCompatResources
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import com.google.android.material.shape.MaterialShapeDrawable
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.custom.behaviors.BottombarBehavior


class BottombarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr),CoordinatorLayout.AttachedBehavior {

    val btnLike: CheckableImageView = bottomIconCheckableImageView(R.drawable.ic_favorite_black_24dp)
    val btnBookmark: CheckableImageView = bottomIconCheckableImageView(R.drawable.ic_bookmark_black_24dp)
    val btnShare: ImageView = bottomIconImageView(R.drawable.ic_share_black_24dp)
    val btnSettings: CheckableImageView = bottomIconCheckableImageView(R.drawable.ic_format_size_black_24dp)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val btnResultUp: ImageView = bottomIconImageView(R.drawable.ic_keyboard_arrow_up_black_24dp)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val btnResultDown: ImageView = bottomIconImageView(R.drawable.ic_keyboard_arrow_up_black_24dp)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val btnSearchClose: ImageView = bottomIconImageView(R.drawable.ic_close_black_24dp)

    var isSearchMode = false

    val leftButtonsList = mutableListOf<View>()
    val rightButtonsList = mutableListOf<View>()

    override fun getBehavior(): CoordinatorLayout.Behavior<*> {
        return BottombarBehavior()
    }
    init {

        val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
        materialBg.elevation = elevation
        background = materialBg

        addView(btnLike)
        addView(btnBookmark)
        addView(btnShare)
        addView(btnSettings)
        //addView(btnSearchClose)
        //addView(btnResultUp)
        //addView(btnResultDown)

        leftButtonsList.add(btnLike)
        leftButtonsList.add(btnBookmark)
        leftButtonsList.add(btnShare)

        rightButtonsList.add(btnSettings)

    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        children.forEach {
            measureChild(it, width, height)
        }
        setMeasuredDimension(width, height)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var usedWidth = paddingLeft
        val top = paddingTop

        leftButtonsList.forEach {
            it.layout(
                usedWidth,
                top,
                usedWidth + it.measuredWidth,
                top + it.measuredHeight
            )
            usedWidth += it.measuredWidth
        }

        usedWidth = r - paddingRight
        rightButtonsList.forEach {
            it.layout(
                usedWidth - it.measuredWidth,
                top,
                usedWidth,
                top + it.measuredHeight
            )
            usedWidth-=it.measuredWidth
        }
    }

    fun setSearchState(isSearch: Boolean){
        if (isSearch == isSearchMode || !isAttachedToWindow) return
        isSearchMode = isSearch
        if (isSearchMode)  animatedShowSearch()
        else animatedHideSearch()
    }

    private fun animatedShowSearch() {
        /*binding.reveal.isVisible = true
        val endRadius = hypot(width.toDouble(), height/2.toDouble())
        val va = ViewAnimationUtils.createCircularReveal(
            binding.reveal,
            width,
            height/2,
            0f,
            endRadius.toFloat()
        )
        va.doOnEnd {
            binding.bottomGroup.isVisible = false
        }
        va.start()*/
    }

    private fun animatedHideSearch() {
        /*binding.bottomGroup.isVisible = true
        val endRadius = hypot(width.toDouble(), height/2.toDouble())
        val va = ViewAnimationUtils.createCircularReveal(
            binding.reveal,
            width,
            height/2,
            0f,
            endRadius.toFloat()
        )
        va.doOnEnd {
            binding.reveal.isVisible = false
        }
        va.start()*/
    }

    fun setSearchInfo(searchCount: Int = 0, position: Int = 0){
       /* with (binding) {
            btnResultUp.isEnabled = searchCount > 0
            btnResultDown.isEnabled = searchCount > 0

            tvSearchResult.text =
                if (searchCount == 0) "Not found" else "${position.inc()} of $searchCount"

            when (position) {
                0 -> btnResultUp.isEnabled = false
                searchCount.dec() -> btnResultDown.isEnabled = false
            }
        }*/
    }

    inner class SearchBar : ViewGroup(context){

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            TODO("Not yet implemented")
        }

    }

    private fun bottomIconImageView(icon: Int): ImageView{
        return ImageView(context,null,0,R.style.BottombarIcon).apply {
            setImageResource(icon)
            isClickable = true
            isFocusable = true
        }
    }

    private fun bottomIconCheckableImageView(icon: Int): CheckableImageView{
        return CheckableImageView(context,null,0,R.style.BottombarIcon).apply {
            setImageResource(icon)
        }
    }
}


