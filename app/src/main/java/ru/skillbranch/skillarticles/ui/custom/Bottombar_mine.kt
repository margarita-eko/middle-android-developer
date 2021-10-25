package ru.skillbranch.skillarticles.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.*
import com.google.android.material.shape.MaterialShapeDrawable
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.*
import ru.skillbranch.skillarticles.ui.custom.behaviors.BottombarBehavior
import kotlin.math.hypot

class Bottombar_mine @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {

    //sizes
    @Px private val iconSize = context.dpToIntPx(56)
    @Px private val iconPadding = context.dpToIntPx(16)
    private val iconTint = context.getColorStateList(R.color.tint_color)

    //views
    val btnLike: CheckableImageView = bottomIconCheckableImageView(R.drawable.ic_favorite_black_24dp)
    val btnBookmark: CheckableImageView = bottomIconCheckableImageView(R.drawable.ic_bookmark_black_24dp)
    val btnShare: AppCompatImageView = bottomIconAppCompatImageView(R.drawable.ic_share_black_24dp).apply {
        imageTintList = iconTint
    }
    val btnSettings: CheckableImageView = bottomIconCheckableImageView(R.drawable.ic_format_size_black_24dp)

    private val searchBar: SearchBar = SearchBar()

    val tvSearchResult
        get() = searchBar.tvSearchResult
    val btnResultUp
        get() = searchBar.btnResultUp
    val btnResultDown
        get() = searchBar.btnResultDown
    val btnSearchClose
        get() = searchBar.btnSearchClose

    var isSearchMode = false

    override fun getBehavior(): CoordinatorLayout.Behavior<Bottombar> {
        return BottombarBehavior()
    }

    val leftButtonsList = mutableListOf<View>()
    val rightButtonsList = mutableListOf<View>()

    init {

        Log.d("BottomBar", "init")

        val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
        materialBg.elevation = elevation
        background = materialBg

        addView(btnLike)
        addView(btnBookmark)
        addView(btnShare)
        addView(btnSettings)

        leftButtonsList.add(btnLike)
        leftButtonsList.add(btnBookmark)
        leftButtonsList.add(btnShare)

        rightButtonsList.add(btnSettings)
        addView(searchBar)
    }

    override fun onSaveInstanceState(): Parcelable {
        val saveState = SavedState(super.onSaveInstanceState())
        saveState.ssIsSearchMode = isSearchMode
        return saveState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if(state is SavedState){
            isSearchMode = state.ssIsSearchMode
            searchBar.isVisible = isSearchMode
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d("BottomBar", "onMeasure")

        val height = getDefaultSize(iconSize, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        children.forEach {
            measureChild(it, width, height)
        }
        setMeasuredDimension(width, height)

    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onLayout(p0: Boolean, l: Int, t: Int, r: Int, b: Int) {

        Log.d("BottomBar", "onLayout")
        var usedWidth = paddingLeft
        var top = paddingTop

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
            usedWidth -= it.measuredWidth
        }
        usedWidth = paddingLeft
        top = paddingTop
        searchBar.layout(
            usedWidth,
            top,
            usedWidth + searchBar.measuredWidth,
            top + searchBar.measuredHeight
        )

        searchBar.isVisible = isSearchMode
    }


    fun setSearchState(isSearch: Boolean) {
        if (isSearch == isSearchMode || !isAttachedToWindow) return
        isSearchMode = isSearch
        if (isSearchMode)  animatedShowSearch()
        else animateHideSearch()
    }

    fun setSearchInfo(searchCount: Int = 0, position: Int = 0) {
        btnResultUp.isEnabled = searchCount > 0
        btnResultDown.isEnabled = searchCount > 0

        tvSearchResult.text =
            if (searchCount == 0) "Not found" else "${position.inc()} of $searchCount"

        when (position) {
            0 -> btnResultUp.isEnabled = false
            searchCount.dec() -> btnResultDown.isEnabled = false
        }
    }

    private fun showBar(value: Boolean){
        leftButtonsList.forEach {
            it.isVisible = value
        }
        rightButtonsList.forEach {
            it.isVisible = value
        }
    }

    private fun animatedShowSearch() {
        searchBar.isVisible = true
        val endRadius = hypot(width.toDouble(), height/2.toDouble())
        val va = ViewAnimationUtils.createCircularReveal(
            searchBar,
            width,
            height/2,
            0f,
            endRadius.toFloat()
        )
        va.doOnEnd {
            showBar(false)
        }
        va.start()
    }

    private fun animateHideSearch() {
        showBar(true)
        val endRadius = hypot(width.toDouble(), height/2.toDouble())
        val va = ViewAnimationUtils.createCircularReveal(
            searchBar,
            width,
            height/2,
            0f,
            endRadius.toFloat()
        )
        va.doOnEnd {
            searchBar.isVisible = false
        }
        va.start()
    }

    private class SavedState : BaseSavedState, Parcelable {
        var ssIsSearchMode: Boolean = false

        constructor(superState: Parcelable?) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            ssIsSearchMode = parcel.readByte() != 0.toByte()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeByte(if (ssIsSearchMode) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }

    }

    @SuppressLint("ViewConstructor")
    inner class SearchBar : ViewGroup(context, null, 0) {

        @ColorInt private val iconColor = context.attrValue(R.attr.colorPrimary)
        internal val btnSearchClose: AppCompatImageView = bottomIconAppCompatImageView(R.drawable.ic_close_black_24dp).apply {
            setColorFilter(iconColor)
        }
        internal val tvSearchResult: TextView = TextView(context).apply {
            setTextColor(iconColor)
            text = "Not found"
            setPadding(0,0,context.dpToIntPx(16),0)
        }
        internal val btnResultDown: AppCompatImageView = bottomIconAppCompatImageView(R.drawable.ic_keyboard_arrow_down_black_24dp).apply {
            setColorFilter(iconColor)
        }
        internal val btnResultUp: AppCompatImageView = bottomIconAppCompatImageView(R.drawable.ic_keyboard_arrow_up_black_24dp).apply {
            setColorFilter(iconColor)
        }

        private val leftButtonsList = mutableListOf<View>()
        private val rightButtonsList = mutableListOf<View>()


        init {

            Log.d("SearchBar", "init")

            btnSearchClose.imageTintList = ContextCompat.getColorStateList(context,R.color.tint_search_color)

            addView(btnSearchClose)
            addView(tvSearchResult)
            addView(btnResultDown)
            addView(btnResultUp)

            val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
            materialBg.elevation = elevation
            materialBg.fillColor = ContextCompat.getColorStateList(context,R.color.color_on_article_bar)
            background = materialBg

            setBackgroundColor(ContextCompat.getColor(context,R.color.color_on_article_bar))

            this.leftButtonsList.add(btnSearchClose)
            this.leftButtonsList.add(tvSearchResult)

            this.rightButtonsList.add(btnResultUp)
            this.rightButtonsList.add(btnResultDown)

        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            Log.d("SearchBar", "OnMeasure")
            val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
            val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

            children.forEach {
                if (it is AppCompatImageView) {
                    measureChild(it, iconSize, iconSize)
                }else {
                    measureChild(it, width, height)
                }
            }
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        }

        override fun onLayout(p0: Boolean, l: Int, t: Int, r: Int, b: Int) {
            Log.d("SearchBar", "onLayout")

            var usedWidth = paddingLeft
            val top = paddingTop

            btnSearchClose.layout(
                usedWidth,
                top,
                usedWidth + btnSearchClose.measuredWidth,
                top + btnSearchClose.measuredHeight
            )

            usedWidth += btnSearchClose.measuredWidth

            tvSearchResult.layout(
                usedWidth,
                (this.measuredHeight - tvSearchResult.measuredHeight)/2,
                usedWidth + tvSearchResult.measuredWidth,
                this.measuredHeight - (this.measuredHeight - tvSearchResult.measuredHeight)/2
            )

            usedWidth = r - iconPadding
            rightButtonsList.forEach {
                it.layout(
                    usedWidth - it.measuredWidth,
                    top,
                    usedWidth,
                    top + it.measuredHeight
                )
                usedWidth-=(it.measuredWidth)
            }

        }

    }

    private fun bottomIconAppCompatImageView(icon: Int): AppCompatImageView {
        return AppCompatImageView(context,null).apply {
            setImageResource(icon)
            isClickable = true
            isFocusable = true
            setPadding(iconPadding)
            background = ContextCompat.getDrawable(context,R.drawable.ripple)
        }
    }

    private fun bottomIconCheckableImageView(icon: Int): CheckableImageView{
        return CheckableImageView(context,null,0,R.style.BottombarIcon).apply {
            setImageResource(icon)
        }
    }
}

