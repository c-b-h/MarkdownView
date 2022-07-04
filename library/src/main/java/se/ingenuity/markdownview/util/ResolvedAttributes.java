package se.ingenuity.markdownview.util;

import static se.ingenuity.markdownview.R.styleable.MarkdownTextView;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdBlockQuoteStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdCodeBlockBackgroundColor;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdCodeBlockMargin;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdCodeBlockTextColor;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdCodeStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdEmphasisStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdFencedCodeBlockStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH1PostStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH1PreStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH1Style;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH2PostStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH2PreStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH2Style;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH3PostStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH3PreStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH3Style;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH4PostStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH4PreStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH4Style;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH5PostStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH5PreStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH5Style;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH6PostStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH6PreStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdH6Style;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdIndentedCodeBlockStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdLinkStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdLinkUnderlined;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdListItemColor;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdOrderedListItemStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdParagraphStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdStrongEmphasisStyle;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdThematicBreakColor;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdThematicBreakHeight;
import static se.ingenuity.markdownview.R.styleable.MarkdownTextView_mdUnorderedListItemStyle;
import static se.ingenuity.markdownview.util.Constants.ID_NULL;
import static se.ingenuity.markdownview.util.Constants.UNDEFINED_DIMEN;

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
    final Boolean linkUnderlined;

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
                MarkdownTextView,
                defStyleAttr,
                defStyleRes);

        headingStyleGroups = Collections.unmodifiableList(Arrays.asList(
                StyleGroup.of(a,
                        MarkdownTextView_mdH1PreStyle,
                        MarkdownTextView_mdH1Style,
                        MarkdownTextView_mdH1PostStyle),
                StyleGroup.of(a,
                        MarkdownTextView_mdH2PreStyle,
                        MarkdownTextView_mdH2Style,
                        MarkdownTextView_mdH2PostStyle),
                StyleGroup.of(a,
                        MarkdownTextView_mdH3PreStyle,
                        MarkdownTextView_mdH3Style,
                        MarkdownTextView_mdH3PostStyle),
                StyleGroup.of(a,
                        MarkdownTextView_mdH4PreStyle,
                        MarkdownTextView_mdH4Style,
                        MarkdownTextView_mdH4PostStyle),
                StyleGroup.of(a,
                        MarkdownTextView_mdH5PreStyle,
                        MarkdownTextView_mdH5Style,
                        MarkdownTextView_mdH5PostStyle),
                StyleGroup.of(a,
                        MarkdownTextView_mdH6PreStyle,
                        MarkdownTextView_mdH6Style,
                        MarkdownTextView_mdH6PostStyle)
        ));
        emphasisStyleGroup = StyleGroup.of(a,
                ID_NULL,
                MarkdownTextView_mdEmphasisStyle,
                ID_NULL);
        strongEmphasisStyleGroup = StyleGroup.of(a,
                ID_NULL,
                MarkdownTextView_mdStrongEmphasisStyle,
                ID_NULL);
        blockQuoteStyleGroup = StyleGroup.of(a,
                ID_NULL,
                MarkdownTextView_mdBlockQuoteStyle,
                ID_NULL);
        codeStyleGroup = StyleGroup.of(a,
                ID_NULL,
                MarkdownTextView_mdCodeStyle,
                ID_NULL);
        fencedCodeBlockStyleGroup = StyleGroup.of(a,
                ID_NULL,
                MarkdownTextView_mdFencedCodeBlockStyle,
                ID_NULL);
        indentedCodeBlockStyleGroup = StyleGroup.of(a,
                ID_NULL,
                MarkdownTextView_mdIndentedCodeBlockStyle,
                ID_NULL);
        orderedListItemStyleGroup = StyleGroup.of(a,
                ID_NULL,
                MarkdownTextView_mdOrderedListItemStyle,
                ID_NULL);
        unorderedListItemStyleGroup = StyleGroup.of(a,
                ID_NULL,
                MarkdownTextView_mdUnorderedListItemStyle,
                ID_NULL);
        paragraphStyleGroup = StyleGroup.of(a,
                ID_NULL,
                MarkdownTextView_mdParagraphStyle,
                ID_NULL);

        codeBlockBackgroundColor = a.getColorStateList(MarkdownTextView_mdCodeBlockBackgroundColor);
        codeBlockMargin = a.getDimensionPixelSize(MarkdownTextView_mdCodeBlockMargin, UNDEFINED_DIMEN);
        codeBlockTextColor = a.getColorStateList(MarkdownTextView_mdCodeBlockTextColor);

        if (a.hasValue(MarkdownTextView_mdLinkUnderlined)) {
            linkUnderlined = a.getBoolean(MarkdownTextView_mdLinkUnderlined, true);
            linkStyleGroup = StyleGroup.of(a,
                    ID_NULL,
                    MarkdownTextView_mdLinkStyle,
                    ID_NULL);
        } else {
            linkUnderlined = null;
            linkStyleGroup = StyleGroup.of(a,
                    ID_NULL,
                    ID_NULL,
                    MarkdownTextView_mdLinkStyle);
        }

        listItemColor = a.getColorStateList(MarkdownTextView_mdListItemColor);
        thematicBreakColor = a.getColorStateList(MarkdownTextView_mdThematicBreakColor);
        thematicBreakHeight = a.getDimensionPixelSize(MarkdownTextView_mdThematicBreakHeight, UNDEFINED_DIMEN);

        a.recycle();
    }

    static class StyleGroup {
        private static StyleGroup EMPTY = new StyleGroup(ID_NULL, ID_NULL, ID_NULL);

        @NonNull
        static StyleGroup of(@NonNull TypedArray attributes,
                             @StyleableRes int preStyleable,
                             @StyleableRes int styleable,
                             @StyleableRes int postStyleable) {
            final int preStyle = preStyleable == ID_NULL ? ID_NULL : attributes.getResourceId(preStyleable, ID_NULL);
            final int style = styleable == ID_NULL ? ID_NULL : attributes.getResourceId(styleable, ID_NULL);
            final int postStyle = postStyleable == ID_NULL ? ID_NULL : attributes.getResourceId(postStyleable, ID_NULL);
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
