package ru.skillbranch.skillarticles.ui.custom.spans

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting

class IconLinkSpan(
    private val linkDrawable: Drawable,
    @Px
    private val gap: Float,
    @ColorInt
    private val textColor: Int,
    dotWidth: Float = 6f
) : ReplacementSpan() {

    private var iconSize = 0
    private var textWidth = 0f
    private val dashs = DashPathEffect(floatArrayOf(dotWidth, dotWidth), 0f)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var path = Path()

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val textStart = x + iconSize + gap
        val linkBottom = y + paint.descent()//bottom.toFloat() - lineSpacing

        paint.forLine {
            path.reset()
            path.moveTo(textStart, linkBottom)
            path.lineTo(textStart + textWidth,linkBottom)
            canvas.drawPath(path,paint)
        }

        canvas.save() // save canvas position
        val transY  = (linkBottom - linkDrawable.bounds.bottom.toFloat()) //translate icon to line position
        canvas.translate(x + gap/2f, transY)
        linkDrawable.draw(canvas)
        canvas.restore()

        paint.forText {
            canvas.drawText(text,start,end,textStart,y.toFloat(),paint)
        }
    }


    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        if (fm != null) {
            iconSize = fm.descent - fm.ascent
        }
        if (iconSize!=0) {
            linkDrawable.setBounds(0,0,iconSize,iconSize) //set drawable bounds as line height
            textWidth = paint.measureText(text.toString(),start, end)
        }
        return (iconSize + textWidth + gap).toInt()
    }


    private inline fun Paint.forLine(block: () -> Unit) {
        val oldStyle = style
        val oldWidth = strokeWidth

        style = Paint.Style.STROKE
        strokeWidth = 0f
        pathEffect = dashs
        color = textColor

        block()

        pathEffect = null
        style = oldStyle
        strokeWidth = oldWidth
    }

    private inline fun Paint.forText(block: () -> Unit) {
        val oldColor = color

        color = textColor

        block()

        color = oldColor
    }
}