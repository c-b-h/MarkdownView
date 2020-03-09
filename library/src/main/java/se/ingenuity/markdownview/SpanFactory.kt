package se.ingenuity.markdownview

import android.content.Context
import android.text.style.TextAppearanceSpan

class SpanFactory(private val context: Context) {
    private val spans = arrayOfNulls<Any>(2)
    fun createSpansForStyle(style: Int): Array<Any?> {
        val a = context.obtainStyledAttributes(style, TEXT_APPEARANCE_ATTR)
        val textAppearance = a.getResourceId(0, ID_NULL)
        a.recycle()

        val baseTextAppearanceAttribute = context.obtainStyledAttributes(TEXT_APPEARANCE_ATTR)
        val styleIsTextAppearance =
            baseTextAppearanceAttribute.getResourceId(0, ID_NULL) == textAppearance
        baseTextAppearanceAttribute.recycle()

        spans.fill(null)

        var index = 0
        if (textAppearance != ID_NULL && styleIsTextAppearance.not()) {
            spans[index++] = TextAppearanceSpan(context, style)
        }

        spans[index] = TextAppearanceSpan(context, style)

        spans.reverse()
        return spans
    }

    private companion object {
        private val TEXT_APPEARANCE_ATTR = intArrayOf(android.R.attr.textAppearance)
        private const val ID_NULL = 0
    }
}