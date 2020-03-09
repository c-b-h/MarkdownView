package se.ingenuity.markdownview

import android.content.Context
import android.util.AttributeSet
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonPlugin
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.core.CoreProps
import org.commonmark.node.*

internal class MarkwonHelper(
    private val context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) {
    var markwon = Markwon.builder(context).usePlugin(object : AbstractMarkwonPlugin() {
        private val resolvedStyles =
            StyleResolver.resolveStyles(context, attrs, defStyleAttr, defStyleRes)
        private val spanFactory = SpanFactory(context)

        override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
            builder.maybeSetHeadingFactory()
            builder.maybeSetFactory(
                R.styleable.MarkdownView_mdEmphasisStyle,
                Emphasis::class.java
            )
            builder.maybeSetFactory(
                R.styleable.MarkdownView_mdStrongEmphasisStyle,
                StrongEmphasis::class.java
            )
            builder.maybeSetFactory(
                R.styleable.MarkdownView_mdBlockQuoteStyle,
                BlockQuote::class.java
            )
            builder.maybeSetFactory(
                R.styleable.MarkdownView_mdCodeStyle,
                Code::class.java
            )
            builder.maybeSetFactory(
                R.styleable.MarkdownView_mdFencedCodeBlockStyle,
                FencedCodeBlock::class.java
            )
            builder.maybeSetFactory(
                R.styleable.MarkdownView_mdIndentedCodeBlockStyle,
                IndentedCodeBlock::class.java
            )
            builder.maybeSetListFactory()
            builder.maybeSetFactory(
                R.styleable.MarkdownView_mdParagraphStyle,
                Paragraph::class.java
            )
            builder.maybeSetFactory(
                R.styleable.MarkdownView_mdLinkStyle,
                Link::class.java
            )
        }

        private fun <N : Node> MarkwonSpansFactory.Builder.maybeSetFactory(
            styleIndex: Int,
            node: Class<N>
        ) {
            val style = resolvedStyles.get(styleIndex, -1)
            if (style != -1) {
                setFactory(node) { _, _ ->
                    spanFactory.createSpansForStyle(style)
                }
            }
        }

        private fun MarkwonSpansFactory.Builder.maybeSetHeadingFactory() {
            val headingStyles = arrayOf(
                resolvedStyles.get(R.styleable.MarkdownView_mdH1Style, -1),
                resolvedStyles.get(R.styleable.MarkdownView_mdH2Style, -1),
                resolvedStyles.get(R.styleable.MarkdownView_mdH3Style, -1),
                resolvedStyles.get(R.styleable.MarkdownView_mdH4Style, -1),
                resolvedStyles.get(R.styleable.MarkdownView_mdH5Style, -1),
                resolvedStyles.get(R.styleable.MarkdownView_mdH6Style, -1)
            )

            if (headingStyles.any { style -> style != -1 }) {
                val original = getFactory(Heading::class.java)

                setFactory(Heading::class.java) { configuration, props ->
                    val style = headingStyles[CoreProps.HEADING_LEVEL.require(props) - 1]
                    if (style != -1) {
                        spanFactory.createSpansForStyle(style)
                    } else {
                        original?.getSpans(configuration, props)
                    }
                }
            }
        }

        private fun MarkwonSpansFactory.Builder.maybeSetListFactory() {
            val orderedListStyle =
                resolvedStyles.get(R.styleable.MarkdownView_mdOrderedListItemStyle, -1)

            val unorderedListStyle =
                resolvedStyles.get(R.styleable.MarkdownView_mdUnorderedListItemStyle, -1)

            if (orderedListStyle != -1 || unorderedListStyle != -1) {
                val original = getFactory(ListItem::class.java)
                setFactory(ListItem::class.java) { configuration, props ->
                    if (CoreProps.ListItemType.ORDERED == CoreProps.LIST_ITEM_TYPE.require(props)) {
                        if (orderedListStyle != -1) {
                            spanFactory.createSpansForStyle(orderedListStyle)
                        } else {
                            original?.getSpans(configuration, props)
                        }
                    } else {
                        if (unorderedListStyle != -1) {
                            spanFactory.createSpansForStyle(unorderedListStyle)
                        } else {
                            original?.getSpans(configuration, props)
                        }
                    }
                }
            }
        }
    }).build()

    fun usePlugin(plugin: MarkwonPlugin) {
        val plugins = markwon.plugins.toMutableList()
        plugins.removeAll { currentPlugin ->
            currentPlugin == plugin
        }

        plugins += plugin

        markwon = Markwon.builderNoCore(context).usePlugins(plugins).build()
    }
}
