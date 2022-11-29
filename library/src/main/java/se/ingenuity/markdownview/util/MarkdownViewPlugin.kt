package se.ingenuity.markdownview.util

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import io.noties.markwon.*
import io.noties.markwon.core.CoreProps
import io.noties.markwon.core.MarkwonTheme
import org.commonmark.node.*
import se.ingenuity.markdownview.util.ResolvedAttributes.StyleGroup

class MarkdownViewPlugin(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int
) : AbstractMarkwonPlugin() {
    private val spanGenerator: SpanGenerator
    private val resolvedAttributes: ResolvedAttributes

    init {
        spanGenerator = SpanGenerator(context)
        resolvedAttributes = ResolvedAttributes(context, attrs, defStyleAttr, defStyleRes)
    }

    override fun configureTheme(builder: MarkwonTheme.Builder) {
        resolvedAttributes.listItemColor?.also {
            builder.listItemColor(it.defaultColor)
        }
        resolvedAttributes.codeBlockBackgroundColor?.also {
            builder.codeBlockBackgroundColor(it.defaultColor)
        }
        resolvedAttributes.codeBlockTextColor?.also {
            builder.codeBlockTextColor(it.defaultColor)
        }

        resolvedAttributes.codeBlockMargin.takeIf { it != Constants.UNDEFINED_DIMEN }?.also {
            builder.codeBlockMargin(it)
        }

        resolvedAttributes.thematicBreakColor?.also {
            builder.thematicBreakColor(it.defaultColor)
        }
        resolvedAttributes.thematicBreakHeight.takeIf { it != Constants.UNDEFINED_DIMEN }?.also {
            builder.thematicBreakHeight(it)
        }

        resolvedAttributes.linkUnderlined?.also {
            builder.isLinkUnderlined(it)
        }
    }

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        maybeApplyHeadingFactories(builder)
        maybeApplyFactories(Emphasis::class.java, builder, resolvedAttributes.emphasisStyleGroup)
        maybeApplyFactories(
            StrongEmphasis::class.java,
            builder,
            resolvedAttributes.strongEmphasisStyleGroup
        )
        maybeApplyFactories(
            BlockQuote::class.java,
            builder,
            resolvedAttributes.blockQuoteStyleGroup
        )
        maybeApplyFactories(Code::class.java, builder, resolvedAttributes.codeStyleGroup)
        maybeApplyFactories(
            FencedCodeBlock::class.java,
            builder,
            resolvedAttributes.fencedCodeBlockStyleGroup
        )
        maybeApplyFactories(
            IndentedCodeBlock::class.java,
            builder,
            resolvedAttributes.indentedCodeBlockStyleGroup
        )
        maybeApplyListFactories(builder)
        maybeApplyFactories(Paragraph::class.java, builder, resolvedAttributes.paragraphStyleGroup)
        maybeApplyFactories(Link::class.java, builder, resolvedAttributes.linkStyleGroup)
    }

    private fun <N : Node> maybeApplyFactories(
        node: Class<N>,
        builder: MarkwonSpansFactory.Builder,
        styleGroup: StyleGroup
    ) {
        if (styleGroup.hasPreStyle()) {
            builder.prependFactory(node) { _, _ ->
                spanGenerator.createSpansForStyle(styleGroup.preStyle)
            }
        }
        if (styleGroup.hasStyle()) {
            builder.setFactory(node) { _, _ ->
                spanGenerator.createSpansForStyle(styleGroup.style)
            }
        }
        if (styleGroup.hasPostStyle()) {
            builder.appendFactory(node) { _, _ ->
                spanGenerator.createSpansForStyle(styleGroup.postStyle)
            }
        }
    }

    private fun maybeApplyHeadingFactories(builder: MarkwonSpansFactory.Builder) {
        val headingStyleGroups = resolvedAttributes.headingStyleGroups
        if (headingStyleGroups.any(StyleGroup::hasPreStyle)) {
            builder.prependFactory(
                Heading::class.java, HeadingSpanFactory(
                    spanGenerator,
                    headingStyleGroups.map(StyleGroup::preStyle).toIntArray()
                )
            )
        }
        if (headingStyleGroups.any(StyleGroup::hasStyle)) {
            builder.setFactory(
                Heading::class.java, HeadingSpanFactory(
                    spanGenerator,
                    headingStyleGroups.map(StyleGroup::style).toIntArray()
                )
            )
        }
        if (headingStyleGroups.any(StyleGroup::hasPostStyle)) {
            builder.appendFactory(
                Heading::class.java, HeadingSpanFactory(
                    spanGenerator,
                    headingStyleGroups.map(StyleGroup::postStyle).toIntArray()
                )
            )
        }
    }

    private fun maybeApplyListFactories(builder: MarkwonSpansFactory.Builder) {
        val ordered = resolvedAttributes.orderedListItemStyleGroup
        val unordered = resolvedAttributes.unorderedListItemStyleGroup
        if (ordered.hasPreStyle() || unordered.hasPreStyle()) {
            builder.prependFactory(
                ListItem::class.java, ListItemSpanFactory(
                    spanGenerator, ordered.preStyle, unordered.preStyle
                )
            )
        }
        if (ordered.hasStyle() || unordered.hasStyle()) {
            builder.setFactory(
                ListItem::class.java, ListItemSpanFactory(
                    spanGenerator, ordered.style, unordered.style
                )
            )
        }
        if (ordered.hasPostStyle() || unordered.hasPostStyle()) {
            builder.appendFactory(
                ListItem::class.java, ListItemSpanFactory(
                    spanGenerator, ordered.postStyle, unordered.postStyle
                )
            )
        }
    }

    private class ListItemSpanFactory(
        private val spanGenerator: SpanGenerator,
        @field:StyleRes @param:StyleRes private val orderedStyle: Int,
        @field:StyleRes @param:StyleRes private val unorderedStyle: Int
    ) : SpanFactory {
        override fun getSpans(
            configuration: MarkwonConfiguration,
            props: RenderProps
        ): Any? {
            if (CoreProps.ListItemType.ORDERED == CoreProps.LIST_ITEM_TYPE.require(props) &&
                StyleGroup.isStyleValid(orderedStyle)
            ) {
                return spanGenerator.createSpansForStyle(orderedStyle)
            } else if (CoreProps.ListItemType.BULLET == CoreProps.LIST_ITEM_TYPE.require(props) &&
                StyleGroup.isStyleValid(unorderedStyle)
            ) {
                return spanGenerator.createSpansForStyle(unorderedStyle)
            }
            return null
        }
    }

    private class HeadingSpanFactory(
        private val spanGenerator: SpanGenerator,
        private val headingStyles: IntArray
    ) : SpanFactory {
        override fun getSpans(
            configuration: MarkwonConfiguration,
            props: RenderProps
        ): Any? {
            val indexOfLevel = CoreProps.HEADING_LEVEL.require(props) - 1
            @StyleRes val style = headingStyles[indexOfLevel]
            if (StyleGroup.isStyleValid(style)) {
                return spanGenerator.createSpansForStyle(style)
            }
            return null
        }
    }
}
