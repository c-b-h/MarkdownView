package se.ingenuity.markdownview

import android.content.Context
import android.util.AttributeSet
import android.util.SparseIntArray

object StyleResolver {
    fun resolveStyles(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyleAttr: Int,
        defStyleRes: Int
    ): SparseIntArray {
        val markdownStyles = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.MarkdownView,
            defStyleAttr,
            defStyleRes
        )

        val resolvedStyles = SparseIntArray()
        for (idx in 0 until markdownStyles.indexCount) {
            val attr: Int = markdownStyles.getIndex(idx)

            if (attr != R.styleable.MarkdownView_markdown) {
                resolvedStyles.put(attr, markdownStyles.getResourceId(attr, -1))
            }
        }

        markdownStyles.recycle()

        return resolvedStyles
    }
}