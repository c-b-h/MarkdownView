package se.ingenuity.markdownview.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.style.BackgroundColorSpan
import android.text.style.LineBackgroundSpan
import android.util.TypedValue
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import io.noties.markwon.core.spans.TextViewSpan
import se.ingenuity.markdownview.R
import se.ingenuity.markdownview.util.span.TextAppearanceSpanCompat
import java.util.*
import kotlin.math.max
import kotlin.math.min

internal class SpanGenerator(private val context: Context) {
    fun createSpansForStyle(@StyleRes style: Int): Array<Any?> {
        var attributes = context.obtainStyledAttributes(style, TEXT_APPEARANCE_ATTR)
        @StyleRes val textAppearance = attributes.getResourceId(0, Constants.ID_NULL)
        attributes.recycle()

        attributes = context.obtainStyledAttributes(TEXT_APPEARANCE_ATTR)
        val styleIsTextAppearance = attributes.getResourceId(0, Constants.ID_NULL) == textAppearance
        attributes.recycle()

        val buffer = arrayOfNulls<Any>(5)
        var index = 0
        if (textAppearance != Constants.ID_NULL && !styleIsTextAppearance) {
            buffer[index++] = TextAppearanceSpanCompat(context, textAppearance)
        }

        buffer[index++] = TextAppearanceSpanCompat(context, style)

        // Custom attributes
        attributes = context.obtainStyledAttributes(style, R.styleable.MarkdownTextView_Style)
        if (attributes.hasValue(R.styleable.MarkdownTextView_Style_mdBackgroundColor)) {
            buffer[index++] = BackgroundColorSpan(attributes.getColor(0, Color.TRANSPARENT))
        }

        if (attributes.hasValue(R.styleable.MarkdownTextView_Style_mdLineBackground)) {
            val lineBackground = attributes.getColor(
                R.styleable.MarkdownTextView_Style_mdLineBackground, Color.TRANSPARENT
            )
            val padAttrIndex = R.styleable.MarkdownTextView_Style_mdLineBackgroundPadding
            var padding = INTRINSIC_LINE_BACKGROUND_PADDING
            if (attributes.getType(padAttrIndex) == TypedValue.TYPE_DIMENSION) {
                padding = attributes.getDimensionPixelSize(
                    padAttrIndex,
                    INTRINSIC_LINE_BACKGROUND_PADDING
                )
            }
            buffer[index++] = PaddedLineBackgroundSpan(lineBackground, padding)
        }
        attributes.recycle()
        buffer.fill(null, index, buffer.size)
        buffer.reverse()
        return buffer
    }

    /**
     * Constructor taking a color integer.
     *
     * @param color Color integer that defines the background color.
     */
    private class PaddedLineBackgroundSpan(
        @param:ColorInt private val mColor: Int,
        @param:Px private val mPadding: Int
    ) : LineBackgroundSpan {
        private val mTextRect = Rect()
        private val mContainerRect = Rect()
        override fun drawBackground(
            canvas: Canvas, paint: Paint,
            @Px left: Int, @Px right: Int,
            @Px top: Int, @Px baseline: Int, @Px bottom: Int,
            text: CharSequence, start: Int, end: Int,
            lineNumber: Int
        ) {
            val textView = TextViewSpan.textViewOf(text)
            val layout = textView!!.layout
            val firstLine = lineNumber == 0
            val useIntrinsicPadding = mPadding == INTRINSIC_LINE_BACKGROUND_PADDING

            // Draw the background
            mTextRect[left - (if (useIntrinsicPadding) textView.paddingLeft else mPadding), top - (if (firstLine) (if (useIntrinsicPadding) textView.paddingTop else mPadding) / 2 else 0), left + Math.round(
                layout.getLineWidth(lineNumber)
            ) + (if (useIntrinsicPadding) textView.paddingRight else mPadding)] =
                bottom + if (useIntrinsicPadding) textView.paddingBottom else mPadding
            mContainerRect[
                    min(left, mTextRect.left),
                    min(top, mTextRect.top),
                    max(right, mTextRect.right)
            ] = max(bottom, mTextRect.bottom)
            Gravity.apply(
                textView.gravity,
                mTextRect.width(),
                mTextRect.height(),
                mContainerRect,
                0,
                0,
                mTextRect
            )
            val paintColor = paint.color
            paint.color = mColor
            canvas.drawRect(mTextRect, paint)
            paint.color = paintColor
        }
    }

    companion object {
        private const val INTRINSIC_LINE_BACKGROUND_PADDING = -1

        @StyleableRes
        private val TEXT_APPEARANCE_ATTR = intArrayOf(android.R.attr.textAppearance)
    }
}
