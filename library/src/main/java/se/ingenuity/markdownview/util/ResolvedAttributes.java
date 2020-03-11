package se.ingenuity.markdownview.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.annotation.StyleableRes;

import java.util.Arrays;

import se.ingenuity.markdownview.R;

class ResolvedAttributes {
    @NonNull
    private int[] headingStyles = new int[6];
    @StyleRes
    private int emphasisStyle = Constants.ID_NULL;
    @StyleRes
    private int strongEmphasisStyle = Constants.ID_NULL;
    @StyleRes
    private int blockQuoteStyle = Constants.ID_NULL;
    @StyleRes
    private int codeStyle = Constants.ID_NULL;
    @StyleRes
    private int fencedCodeBlockStyle = Constants.ID_NULL;
    @StyleRes
    private int indentedCodeBlockStyle = Constants.ID_NULL;
    @StyleRes
    private int orderedListItemStyle = Constants.ID_NULL;
    @StyleRes
    private int unorderedListItemStyle = Constants.ID_NULL;
    @StyleRes
    private int paragraphStyle = Constants.ID_NULL;
    @StyleRes
    private int linkStyle = Constants.ID_NULL;

    @Nullable
    private ColorStateList listItemColor;
    @Nullable
    private ColorStateList codeBlockBackgroundColor;

    ResolvedAttributes(@NonNull Context context,
                       @Nullable AttributeSet attrs,
                       @AttrRes int defStyleAttr,
                       @StyleRes int defStyleRes) {
        Arrays.fill(headingStyles, Constants.ID_NULL);

        final TypedArray attributes = context.obtainStyledAttributes(
                attrs,
                R.styleable.MarkdownView,
                defStyleAttr,
                defStyleRes);

        for (int index = 0, attr; index < attributes.getIndexCount(); index++) {
            attr = attributes.getIndex(index);

            @StyleRes int style = attributes.getResourceId(attr, Constants.ID_NULL);
            if (attr == R.styleable.MarkdownView_mdH1Style) {
                headingStyles[0] = style;
            } else if (attr == R.styleable.MarkdownView_mdH2Style) {
                headingStyles[1] = style;
            } else if (attr == R.styleable.MarkdownView_mdH3Style) {
                headingStyles[2] = style;
            } else if (attr == R.styleable.MarkdownView_mdH4Style) {
                headingStyles[3] = style;
            } else if (attr == R.styleable.MarkdownView_mdH5Style) {
                headingStyles[4] = style;
            } else if (attr == R.styleable.MarkdownView_mdH6Style) {
                headingStyles[5] = style;
            } else if (attr == R.styleable.MarkdownView_mdEmphasisStyle) {
                emphasisStyle = style;
            } else if (attr == R.styleable.MarkdownView_mdStrongEmphasisStyle) {
                strongEmphasisStyle = style;
            } else if (attr == R.styleable.MarkdownView_mdBlockQuoteStyle) {
                blockQuoteStyle = style;
            } else if (attr == R.styleable.MarkdownView_mdCodeStyle) {
                codeStyle = style;
            } else if (attr == R.styleable.MarkdownView_mdFencedCodeBlockStyle) {
                fencedCodeBlockStyle = style;
            } else if (attr == R.styleable.MarkdownView_mdIndentedCodeBlockStyle) {
                indentedCodeBlockStyle = style;
            } else if (attr == R.styleable.MarkdownView_mdOrderedListItemStyle) {
                orderedListItemStyle = style;
            } else if (attr == R.styleable.MarkdownView_mdUnorderedListItemStyle) {
                unorderedListItemStyle = style;
            } else if (attr == R.styleable.MarkdownView_mdParagraphStyle) {
                paragraphStyle = style;
            } else if (attr == R.styleable.MarkdownView_mdLinkStyle) {
                linkStyle = style;
            } else {
                resolveAttributes(attributes, attr);
            }
        }

        attributes.recycle();
    }

    private void resolveAttributes(@NonNull TypedArray attributes,
                                   @StyleableRes int attr) {
        if (attr == R.styleable.MarkdownView_mdListItemColor) {
            listItemColor = attributes.getColorStateList(attr);
        } else if (attr == R.styleable.MarkdownView_mdCodeBlockBackgroundColor) {
            codeBlockBackgroundColor = attributes.getColorStateList(attr);
        }
    }

    @NonNull
    int[] getHeadingStyles() {
        return headingStyles;
    }

    @StyleRes
    int getEmphasisStyle() {
        return emphasisStyle;
    }

    @StyleRes
    int getStrongEmphasisStyle() {
        return strongEmphasisStyle;
    }

    @StyleRes
    int getBlockQuoteStyle() {
        return blockQuoteStyle;
    }

    @StyleRes
    int getCodeStyle() {
        return codeStyle;
    }

    @StyleRes
    int getFencedCodeBlockStyle() {
        return fencedCodeBlockStyle;
    }

    @StyleRes
    int getIndentedCodeBlockStyle() {
        return indentedCodeBlockStyle;
    }

    @StyleRes
    int getOrderedListItemStyle() {
        return orderedListItemStyle;
    }

    @StyleRes
    int getUnorderedListItemStyle() {
        return unorderedListItemStyle;
    }

    @StyleRes
    int getParagraphStyle() {
        return paragraphStyle;
    }

    @StyleRes
    int getLinkStyle() {
        return linkStyle;
    }

    @Nullable
    ColorStateList getListItemColor() {
        return listItemColor;
    }

    @Nullable
    ColorStateList getCodeBlockBackgroundColor() {
        return codeBlockBackgroundColor;
    }
}
