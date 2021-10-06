package ru.skillbranch.skillarticles.ui.custom.spans
import android.text.style.ForegroundColorSpan
/*open class SearchSpan(bgColor: Int, private val fgColor: Int): BackgroundColorSpan(bgColor) {

    private val alpha by lazy {
        ColorUtils.setAlphaComponent(backgroundColor, 160)
    }

    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.bgColor = alpha
        textPaint.color = fgColor
    }


}*/

open class SearchSpan(fgColor: Int): ForegroundColorSpan(fgColor)