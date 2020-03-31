package se.ingenuity.markdownview.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.AttrRes;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.annotation.StyleableRes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static se.ingenuity.markdownview.R.styleable.MarkdownView;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdBlockQuoteStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdCodeBlockBackgroundColor;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdCodeBlockMargin;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdCodeBlockTextColor;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdCodeStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdEmphasisStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdFencedCodeBlockStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH1PostStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH1PreStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH1Style;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH2PostStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH2PreStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH2Style;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH3PostStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH3PreStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH3Style;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH4PostStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH4PreStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH4Style;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH5PostStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH5PreStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH5Style;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH6PostStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH6PreStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdH6Style;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdIndentedCodeBlockStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdLinkStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdListItemColor;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdOrderedListItemStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdParagraphPostStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdParagraphPreStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdParagraphStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdStrongEmphasisStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdThematicBreakColor;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdThematicBreakHeight;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdUnorderedListItemPostStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdUnorderedListItemPreStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownView_mdUnorderedListItemStyle;
import static se.ingenuity.markdownview.util.Constants.ID_NULL;
import static se.ingenuity.markdownview.util.Constants.UNDEFINED_DIMEN;

class ResolvedAttributes {
    @NonNull
    final List<StyleGroup> headingStyleGroups;
    @NonNull
    final StyleGroup emphasisStyleGroup;
    @NonNull
    final StyleGroup strongEmphasisStyleGroup;
    @NonNull
    final StyleGroup blockQuoteStyleGroup;
    @NonNull
    final StyleGroup codeStyleGroup;
    @NonNull
    final StyleGroup fencedCodeBlockStyleGroup;
    @NonNull
    final StyleGroup indentedCodeBlockStyleGroup;
    @NonNull
    final StyleGroup orderedListItemStyleGroup;
    @NonNull
    final StyleGroup unorderedListItemStyleGroup;
    @NonNull
    final StyleGroup paragraphStyleGroup;
    @NonNull
    final StyleGroup linkStyleGroup;

    @Nullable
    final ColorStateList listItemColor;
    @Nullable
    final ColorStateList codeBlockBackgroundColor;
    @Nullable
    final ColorStateList codeBlockTextColor;
    @Dimension
    final int codeBlockMargin;
    @Nullable
    final ColorStateList thematicBreakColor;
    @Dimension
    final int thematicBreakHeight;

    ResolvedAttributes(@NonNull Context context,
                       @Nullable AttributeSet attrs,
                       @AttrRes int defStyleAttr,
                       @StyleRes int defStyleRes) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs,
                MarkdownView,
                defStyleAttr,
                defStyleRes);

        headingStyleGroups = Collections.unmodifiableList(Arrays.asList(
                StyleGroup.of(a,
                        MarkdownView_mdH1PreStyle,
                        MarkdownView_mdH1Style,
                        MarkdownView_mdH1PostStyle),
                StyleGroup.of(a,
                        MarkdownView_mdH2PreStyle,
                        MarkdownView_mdH2Style,
                        MarkdownView_mdH2PostStyle),
                StyleGroup.of(a,
                        MarkdownView_mdH3PreStyle,
                        MarkdownView_mdH3Style,
                        MarkdownView_mdH3PostStyle),
                StyleGroup.of(a,
                        MarkdownView_mdH4PreStyle,
                        MarkdownView_mdH4Style,
                        MarkdownView_mdH4PostStyle),
                StyleGroup.of(a,
                        MarkdownView_mdH5PreStyle,
                        MarkdownView_mdH5Style,
                        MarkdownView_mdH5PostStyle),
                StyleGroup.of(a,
                        MarkdownView_mdH6PreStyle,
                        MarkdownView_mdH6Style,
                        MarkdownView_mdH6PostStyle)
        ));
        emphasisStyleGroup = StyleGroup.of(a,
                MarkdownView_mdEmphasisStyle,
                MarkdownView_mdEmphasisStyle,
                MarkdownView_mdEmphasisStyle);
        strongEmphasisStyleGroup = StyleGroup.of(a,
                MarkdownView_mdStrongEmphasisStyle,
                MarkdownView_mdStrongEmphasisStyle,
                MarkdownView_mdStrongEmphasisStyle);
        blockQuoteStyleGroup = StyleGroup.of(a,
                MarkdownView_mdBlockQuoteStyle,
                MarkdownView_mdBlockQuoteStyle,
                MarkdownView_mdBlockQuoteStyle);
        codeStyleGroup = StyleGroup.of(a,
                MarkdownView_mdCodeStyle,
                MarkdownView_mdCodeStyle,
                MarkdownView_mdCodeStyle);
        fencedCodeBlockStyleGroup = StyleGroup.of(a,
                MarkdownView_mdFencedCodeBlockStyle,
                MarkdownView_mdFencedCodeBlockStyle,
                MarkdownView_mdFencedCodeBlockStyle);
        indentedCodeBlockStyleGroup = StyleGroup.of(a,
                MarkdownView_mdIndentedCodeBlockStyle,
                MarkdownView_mdIndentedCodeBlockStyle,
                MarkdownView_mdIndentedCodeBlockStyle);
        orderedListItemStyleGroup = StyleGroup.of(a,
                MarkdownView_mdOrderedListItemStyle,
                MarkdownView_mdOrderedListItemStyle,
                MarkdownView_mdOrderedListItemStyle);
        unorderedListItemStyleGroup = StyleGroup.of(a,
                MarkdownView_mdUnorderedListItemPreStyle,
                MarkdownView_mdUnorderedListItemStyle,
                MarkdownView_mdUnorderedListItemPostStyle);
        paragraphStyleGroup = StyleGroup.of(a,
                MarkdownView_mdParagraphPreStyle,
                MarkdownView_mdParagraphStyle,
                MarkdownView_mdParagraphPostStyle);
        linkStyleGroup = StyleGroup.of(a,
                MarkdownView_mdLinkStyle,
                MarkdownView_mdLinkStyle,
                MarkdownView_mdLinkStyle);

        listItemColor = a.getColorStateList(MarkdownView_mdListItemColor);
        codeBlockBackgroundColor = a.getColorStateList(MarkdownView_mdCodeBlockBackgroundColor);
        codeBlockTextColor = a.getColorStateList(MarkdownView_mdCodeBlockTextColor);
        codeBlockMargin = a.getDimensionPixelSize(MarkdownView_mdCodeBlockMargin, UNDEFINED_DIMEN);
        thematicBreakColor = a.getColorStateList(MarkdownView_mdThematicBreakColor);
        thematicBreakHeight = a.getDimensionPixelSize(MarkdownView_mdThematicBreakHeight, UNDEFINED_DIMEN);

        a.recycle();
    }

    static class StyleGroup {
        private static StyleGroup EMPTY = new StyleGroup(ID_NULL, ID_NULL, ID_NULL);

        @NonNull
        static StyleGroup of(@NonNull TypedArray attributes,
                             @StyleableRes int preStyleable,
                             @StyleableRes int styleable,
                             @StyleableRes int postStyleable) {
            final int preStyle = attributes.getResourceId(preStyleable, ID_NULL);
            final int style = attributes.getResourceId(styleable, ID_NULL);
            final int postStyle = attributes.getResourceId(postStyleable, ID_NULL);
            if (preStyle == ID_NULL && style == ID_NULL && postStyle == ID_NULL) {
                return EMPTY;
            } else {
                return new StyleGroup(preStyle, style, postStyle);
            }
        }

        @StyleRes
        final int preStyle;

        @StyleRes
        final int style;

        @StyleRes
        final int postStyle;

        private StyleGroup(@StyleRes int preStyle,
                           @StyleRes int style,
                           @StyleRes int postStyle) {
            this.preStyle = preStyle;
            this.style = style;
            this.postStyle = postStyle;
        }

        final boolean hasPreStyle() {
            return isStyleValid(preStyle);
        }

        final boolean hasStyle() {
            return isStyleValid(style);
        }

        final boolean hasPostStyle() {
            return isStyleValid(postStyle);
        }

        static boolean isStyleValid(@StyleRes int styleResource) {
            return styleResource != ID_NULL;
        }
    }
}
