package ru.skillbranch.skillarticles.ui.custom.spans

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.text.style.ReplacementSpan
import androidx.annotation.VisibleForTesting
import ru.skillbranch.skillarticles.R

class UnderlineSpan(
    private val underlineColor: Int,
    dotWidth: Float = 6f
) : ReplacementSpan() {
    private var textWidth = 0
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
        val bottom = y + paint.descent()//bottom.toFloat() - lineSpacing

        paint.forLine {
            path.reset()
            path.moveTo(x, bottom)
            path.lineTo(x + textWidth, bottom)
            canvas.drawPath(path,paint)
        }

        paint.forText {
            canvas.drawText(text,start,end,x,y.toFloat(),paint)
        }

    }


    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        textWidth = paint.measureText(text.toString(),start, end).toInt()
        return textWidth
    }


    private inline fun Paint.forLine(block: () -> Unit) {
        val oldStyle = style
        val oldWidth = strokeWidth

        style = Paint.Style.STROKE
        strokeWidth = 0f
        pathEffect = dashs
        color = underlineColor

        block()

        style = oldStyle
        pathEffect = null
        strokeWidth = oldWidth
    }

    private inline fun Paint.forText(block: () -> Unit) {
        val oldColor = color
        color = underlineColor
        block()
        color = oldColor
    }
}