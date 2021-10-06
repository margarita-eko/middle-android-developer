package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.setPaddingOptionally
import ru.skillbranch.skillarticles.repositories.MarkdownElement
import kotlin.properties.Delegates

class MarkdownContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private lateinit var copyListener: (String) -> Unit
    private var elements: List<MarkdownElement> = emptyList()
    var textSize by Delegates.observable(14f) {_, old, value ->
        if (value == old) return@observable
        this.children.forEach {
            it as IMarkdownView
            it.fontSize = value
        }
    }
    var isLoading: Boolean = true
    private val padding = context.dpToIntPx(8)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedHeight = paddingTop
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        children.forEach {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
            usedHeight += it.measuredHeight
        }

        usedHeight += paddingBottom
        setMeasuredDimension(width,usedHeight)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var usedHeight = paddingTop
        val bodyWidth = right - left -paddingLeft - paddingRight
        val left = paddingLeft
        val right = paddingLeft + bodyWidth

        children.forEach {
            if (it is MarkdownTextView) {
                it.layout(
                    left - paddingLeft / 2,
                    usedHeight,
                    r - paddingRight / 2,
                    usedHeight + it.measuredHeight
                )
            } else {
                it.layout(
                    left,
                    usedHeight,
                    right,
                    usedHeight + it.measuredHeight
                )
            }

            usedHeight += it.measuredHeight

        }
    }

    fun setContent(content: List<MarkdownElement>) {
        if (elements.isNotEmpty()) return
        elements = content
        content.forEach {
            when (it) {
                is MarkdownElement.Text -> {
                    val tv = MarkdownTextView(context, textSize).apply {
                        setPaddingOptionally(
                            left = padding,
                            right = padding
                        )
                    }


                    MarkdownBuilder(context).markdownToSpan(it)
                        .run {
                            tv.setText(this, TextView.BufferType.SPANNABLE)
                        }

                    addView(tv)
                }

                is MarkdownElement.Image -> {
                    val iv = MarkdownImageView(
                        context,
                        textSize,
                        it.image.url,
                        it.image.text,
                        it.image.alt
                    )

                    addView(iv)
                }

                is MarkdownElement.Scroll -> {
                    val sv = MarkdownCodeView(
                        context,
                        textSize,
                        it.blockCode.text.toString()
                    )
                    sv.copyListener = copyListener
                    addView(sv)
                }

            }
        }
    }

    fun renderSearchResult(searchResult: List<Pair<Int, Int>>) {
        children.forEach { view ->
            view as IMarkdownView
            view.clearSearchResult()

        }

        if (searchResult.isEmpty()) return

        val bounds = elements.map { it.bounds }
        val result = searchResult.groupByBounds(bounds)

        children.forEachIndexed{ index, view ->
            view as IMarkdownView
            //search for child with markdown element offset
            view.renderSearchResult(result[index], elements[index].offset)
        }
    }

    fun renderSearchPosition(
        searchPosition: Pair<Int, Int>?
    ) {
        searchPosition ?: return
        val bounds = elements.map { it.bounds }
        val index = bounds.indexOfFirst { (start, end) ->
            val boundRange = start..end
            val (startPos, endPos) = searchPosition
            startPos in boundRange && endPos in boundRange
        }

        if (index == -1) return
        val view = getChildAt(index)
        view as IMarkdownView
        view.renderSearchPosition(searchPosition, elements[index].offset)
    }

    fun clearSearchResult() {
        children.forEach { view ->
            view as IMarkdownView
            view.clearSearchResult()
        }
    }

    fun setCopyListener(listener: (String) -> Unit) {
        copyListener = listener
    }
}

private fun List<Pair<Int,Int>>.groupByBounds(bounds: List<Pair<Int,Int>>): List<List<Pair<Int,Int>>> {
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
        for (i in searchIndexStart until this.size - 1) {
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
		