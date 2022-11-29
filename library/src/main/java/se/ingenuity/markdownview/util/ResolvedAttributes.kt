package se.ingenuity.markdownview.util

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import se.ingenuity.markdownview.R

internal class ResolvedAttributes(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int
) {
    val headingStyleGroups: List<StyleGroup>
    val emphasisStyleGroup: StyleGroup
    val strongEmphasisStyleGroup: StyleGroup
    val blockQuoteStyleGroup: StyleGroup
    val codeStyleGroup: StyleGroup
    val fencedCodeBlockStyleGroup: StyleGroup
    val indentedCodeBlockStyleGroup: StyleGroup
    val orderedListItemStyleGroup: StyleGroup
    val unorderedListItemStyleGroup: StyleGroup
    val paragraphStyleGroup: StyleGroup
    val linkStyleGroup: StyleGroup
    val linkUnderlined: Boolean?
    val listItemColor: ColorStateList?
    val codeBlockBackgroundColor: ColorStateList?
    val codeBlockTextColor: ColorStateList?

    @Dimension
    val codeBlockMargin: Int
    val thematicBreakColor: ColorStateList?

    @Dimension
    val thematicBreakHeight: Int

    init {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.MarkdownTextView,
            defStyleAttr,
            defStyleRes
        )
        headingStyleGroups = listOf(
            StyleGroup.of(
                a,
                R.styleable.MarkdownTextView_mdH1PreStyle,
                R.styleable.MarkdownTextView_mdH1Style,
                R.styleable.MarkdownTextView_mdH1PostStyle
            ),
            StyleGroup.of(
                a,
                R.styleable.MarkdownTextView_mdH2PreStyle,
                R.styleable.MarkdownTextView_mdH2Style,
                R.styleable.MarkdownTextView_mdH2PostStyle
            ),
            StyleGroup.of(
                a,
                R.styleable.MarkdownTextView_mdH3PreStyle,
                R.styleable.MarkdownTextView_mdH3Style,
                R.styleable.MarkdownTextView_mdH3PostStyle
            ),
            StyleGroup.of(
                a,
                R.styleable.MarkdownTextView_mdH4PreStyle,
                R.styleable.MarkdownTextView_mdH4Style,
                R.styleable.MarkdownTextView_mdH4PostStyle
            ),
            StyleGroup.of(
                a,
                R.styleable.MarkdownTextView_mdH5PreStyle,
                R.styleable.MarkdownTextView_mdH5Style,
                R.styleable.MarkdownTextView_mdH5PostStyle
            ),
            StyleGroup.of(
                a,
                R.styleable.MarkdownTextView_mdH6PreStyle,
                R.styleable.MarkdownTextView_mdH6Style,
                R.styleable.MarkdownTextView_mdH6PostStyle
            )
        )
        emphasisStyleGroup = StyleGroup.of(
            a,
            R.styleable.MarkdownTextView_mdEmphasisPreStyle,
            R.styleable.MarkdownTextView_mdEmphasisStyle,
            R.styleable.MarkdownTextView_mdEmphasisPostStyle
        )
        strongEmphasisStyleGroup = StyleGroup.of(
            a,
            R.styleable.MarkdownTextView_mdStrongEmphasisPreStyle,
            R.styleable.MarkdownTextView_mdStrongEmphasisStyle,
            R.styleable.MarkdownTextView_mdStrongEmphasisPostStyle
        )
        blockQuoteStyleGroup = StyleGroup.of(
            a,
            R.styleable.MarkdownTextView_mdBlockQuotePreStyle,
            R.styleable.MarkdownTextView_mdBlockQuoteStyle,
            R.styleable.MarkdownTextView_mdBlockQuotePostStyle
        )
        codeStyleGroup = StyleGroup.of(
            a,
            R.styleable.MarkdownTextView_mdCodePreStyle,
            R.styleable.MarkdownTextView_mdCodeStyle,
            R.styleable.MarkdownTextView_mdCodePostStyle
        )
        fencedCodeBlockStyleGroup = StyleGroup.of(
            a,
            R.styleable.MarkdownTextView_mdFencedCodeBlockPreStyle,
            R.styleable.MarkdownTextView_mdFencedCodeBlockStyle,
            R.styleable.MarkdownTextView_mdFencedCodeBlockPostStyle
        )
        indentedCodeBlockStyleGroup = StyleGroup.of(
            a,
            R.styleable.MarkdownTextView_mdIndentedCodeBlockPreStyle,
            R.styleable.MarkdownTextView_mdIndentedCodeBlockStyle,
            R.styleable.MarkdownTextView_mdIndentedCodeBlockPostStyle
        )
        orderedListItemStyleGroup = StyleGroup.of(
            a,
            R.styleable.MarkdownTextView_mdOrderedListItemPreStyle,
            R.styleable.MarkdownTextView_mdOrderedListItemStyle,
            R.styleable.MarkdownTextView_mdOrderedListItemPostStyle
        )
        unorderedListItemStyleGroup = StyleGroup.of(
            a,
            R.styleable.MarkdownTextView_mdUnorderedListItemPreStyle,
            R.styleable.MarkdownTextView_mdUnorderedListItemStyle,
            R.styleable.MarkdownTextView_mdUnorderedListItemPostStyle
        )
        paragraphStyleGroup = StyleGroup.of(
            a,
            R.styleable.MarkdownTextView_mdParagraphPreStyle,
            R.styleable.MarkdownTextView_mdParagraphStyle,
            R.styleable.MarkdownTextView_mdParagraphPostStyle
        )
        codeBlockBackgroundColor =
            a.getColorStateList(R.styleable.MarkdownTextView_mdCodeBlockBackgroundColor)
        codeBlockMargin = a.getDimensionPixelSize(
            R.styleable.MarkdownTextView_mdCodeBlockMargin,
            Constants.UNDEFINED_DIMEN
        )
        codeBlockTextColor = a.getColorStateList(R.styleable.MarkdownTextView_mdCodeBlockTextColor)
        linkUnderlined = if (a.hasValue(R.styleable.MarkdownTextView_mdLinkUnderlined)) {
            a.getBoolean(R.styleable.MarkdownTextView_mdLinkUnderlined, true)
        } else {
            null
        }
        linkStyleGroup = StyleGroup.of(
            a,
            R.styleable.MarkdownTextView_mdLinkPreStyle,
            R.styleable.MarkdownTextView_mdLinkStyle,
            R.styleable.MarkdownTextView_mdLinkPostStyle
        )
        listItemColor = a.getColorStateList(R.styleable.MarkdownTextView_mdListItemColor)
        thematicBreakColor = a.getColorStateList(R.styleable.MarkdownTextView_mdThematicBreakColor)
        thematicBreakHeight = a.getDimensionPixelSize(
            R.styleable.MarkdownTextView_mdThematicBreakHeight,
            Constants.UNDEFINED_DIMEN
        )
        a.recycle()
    }

    internal data class StyleGroup(
        @field:StyleRes @param:StyleRes val preStyle: Int,
        @field:StyleRes @param:StyleRes val style: Int,
        @field:StyleRes @param:StyleRes val postStyle: Int
    ) {
        fun hasPreStyle() = isStyleValid(preStyle)

        fun hasStyle() = isStyleValid(style)

        fun hasPostStyle() = isStyleValid(postStyle)

        companion object {
            private val EMPTY = StyleGroup(Constants.ID_NULL, Constants.ID_NULL, Constants.ID_NULL)

            fun of(
                attributes: TypedArray,
                @StyleableRes preStyleable: Int,
                @StyleableRes styleable: Int,
                @StyleableRes postStyleable: Int
            ): StyleGroup {
                val preStyle =
                    if (preStyleable == Constants.ID_NULL) Constants.ID_NULL else attributes.getResourceId(
                        preStyleable,
                        Constants.ID_NULL
                    )
                val style =
                    if (styleable == Constants.ID_NULL) Constants.ID_NULL else attributes.getResourceId(
                        styleable,
                        Constants.ID_NULL
                    )
                val postStyle =
                    if (postStyleable == Constants.ID_NULL) Constants.ID_NULL else attributes.getResourceId(
                        postStyleable,
                        Constants.ID_NULL
                    )

                return if (
                    preStyle == Constants.ID_NULL &&
                    style == Constants.ID_NULL &&
                    postStyle == Constants.ID_NULL
                ) {
                    EMPTY
                } else {
                    StyleGroup(preStyle, style, postStyle)
                }
            }

            fun isStyleValid(@StyleRes styleResource: Int): Boolean {
                return styleResource != Constants.ID_NULL
            }
        }
    }
}
