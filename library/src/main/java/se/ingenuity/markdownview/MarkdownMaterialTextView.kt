package se.ingenuity.markdownview

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textview.MaterialTextView
import io.noties.markwon.MarkwonPlugin

class MarkdownMaterialTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle,
    defStyleRes: Int = 0
) : MaterialTextView(context, attrs, defStyleAttr, defStyleRes) {
    private var markwonHelper = MarkwonHelper(context, attrs, R.attr.markdownStyles, defStyleRes)

    init {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.MarkdownView,
            defStyleAttr,
            defStyleRes
        )

        if (a.hasValue(R.styleable.MarkdownView_markdown)) {
            setMarkdown(a.getString(R.styleable.MarkdownView_markdown)!!)
        }

        a.recycle()
    }

    fun setMarkdown(markdown: String) {
        markwonHelper.markwon.setMarkdown(this, markdown)
    }

    fun getMarkdown(): CharSequence {
        return text
    }

    fun usePlugin(plugin: MarkwonPlugin) {
        markwonHelper.usePlugin(plugin)
    }
}