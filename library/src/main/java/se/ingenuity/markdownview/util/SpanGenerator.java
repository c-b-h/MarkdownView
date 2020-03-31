package se.ingenuity.markdownview.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.text.style.TextAppearanceSpan;

import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.core.content.res.ResourcesCompat;

import java.util.Arrays;

import io.noties.markwon.core.spans.CustomTypefaceSpan;
import se.ingenuity.markdownview.R;

class SpanGenerator {
    private static boolean CUSTOM_FONT_SUPPORT_IN_SPAN =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    @NonNull
    private static final int[] TEXT_APPEARANCE_ATTR = {android.R.attr.textAppearance};

    private static final int TEXT_FONT_WEIGHT_UNSPECIFIED = -1;

    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int MONOSPACE = 3;

    @NonNull
    private final Context context;

    @NonNull
    private final Object[] spanBuffer =
            new Object[CUSTOM_FONT_SUPPORT_IN_SPAN ? 2 : 3];

    private int style;

    private int fontWeight;

    @Nullable
    private Typeface fontTypeface;

    SpanGenerator(@NonNull Context context) {
        this.context = context;
        reset();
    }

    @NonNull
    Object[] createSpansForStyle(@StyleRes int style) {
        TypedArray attributes = context.obtainStyledAttributes(style, TEXT_APPEARANCE_ATTR);
        @StyleRes final int textAppearance = attributes.getResourceId(0, Constants.ID_NULL);
        attributes.recycle();

        attributes = context.obtainStyledAttributes(TEXT_APPEARANCE_ATTR);
        final boolean styleIsTextAppearance =
                attributes.getResourceId(0, Constants.ID_NULL) == textAppearance;
        attributes.recycle();

        int index = 0;
        if (textAppearance != Constants.ID_NULL && !styleIsTextAppearance) {
            spanBuffer[index++] = new TextAppearanceSpan(context, textAppearance);

            if (!CUSTOM_FONT_SUPPORT_IN_SPAN) {
                attributes = context.obtainStyledAttributes(textAppearance,
                        R.styleable.MarkdownView_TextAppearance);

                updateTypefaceAndStyle(attributes);
            }

            attributes.recycle();
        }

        spanBuffer[index++] = new TextAppearanceSpan(context, style);
        if (!CUSTOM_FONT_SUPPORT_IN_SPAN) {
            attributes = context.obtainStyledAttributes(style,
                    R.styleable.MarkdownView_TextAppearance);
            updateTypefaceAndStyle(attributes);
            attributes.recycle();

            if (fontTypeface != null) {
                spanBuffer[index++] = CustomTypefaceSpan.create(fontTypeface);
            }
        }

        if (index < spanBuffer.length) {
            Arrays.fill(spanBuffer, index, spanBuffer.length, null);
        }

        ArrayUtils.reverse(spanBuffer);
        reset();
        return spanBuffer;
    }

    private void updateTypefaceAndStyle(@NonNull TypedArray a) {
        style = a.getInt(R.styleable.MarkdownView_TextAppearance_android_textStyle, style);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            fontWeight = a.getInt(R.styleable.MarkdownView_TextAppearance_android_textFontWeight,
                    TEXT_FONT_WEIGHT_UNSPECIFIED);
            if (fontWeight != TEXT_FONT_WEIGHT_UNSPECIFIED) {
                style = Typeface.NORMAL | (style & Typeface.ITALIC);
            }
        }

        if (a.hasValue(R.styleable.MarkdownView_TextAppearance_android_fontFamily)
                || a.hasValue(R.styleable.MarkdownView_TextAppearance_fontFamily)) {
            fontTypeface = null;
            int fontFamilyId = a.hasValue(R.styleable.MarkdownView_TextAppearance_fontFamily)
                    ? R.styleable.MarkdownView_TextAppearance_fontFamily
                    : R.styleable.MarkdownView_TextAppearance_android_fontFamily;
//            final int fontWeight = this.fontWeight;
//            final int style = this.style;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                if (!context.isRestricted()) {
                    final Typeface typeface = a.getFont(fontFamilyId);
                    if (typeface != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                                && this.fontWeight != TEXT_FONT_WEIGHT_UNSPECIFIED) {
                            fontTypeface = Typeface.create(
                                    Typeface.create(typeface, Typeface.NORMAL), this.fontWeight,
                                    (this.style & Typeface.ITALIC) != 0);
                        } else {
                            fontTypeface = typeface;
                        }
                    }
                }
            } else {
                @FontRes final int fontRes = a.getResourceId(fontFamilyId, Constants.ID_NULL);
                if (fontRes != Constants.ID_NULL) {
                    fontTypeface = ResourcesCompat.getFont(context, fontRes);
                }
            }

            if (fontTypeface == null) {
                // Try with String. This is done by TextView JB+, but fails in ICS
                String fontFamilyName = a.getString(fontFamilyId);
                if (fontFamilyName != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                            && this.fontWeight != TEXT_FONT_WEIGHT_UNSPECIFIED) {
                        fontTypeface = Typeface.create(
                                Typeface.create(fontFamilyName, Typeface.NORMAL), this.fontWeight,
                                (this.style & Typeface.ITALIC) != 0);
                    } else {
                        fontTypeface = Typeface.create(fontFamilyName, this.style);
                    }
                }
            }
            return;
        }

        if (a.hasValue(R.styleable.MarkdownView_TextAppearance_android_typeface)) {
            int typefaceIndex = a.getInt(R.styleable.MarkdownView_TextAppearance_android_typeface, SANS);
            switch (typefaceIndex) {
                case SANS:
                    fontTypeface = Typeface.SANS_SERIF;
                    break;

                case SERIF:
                    fontTypeface = Typeface.SERIF;
                    break;

                case MONOSPACE:
                    fontTypeface = Typeface.MONOSPACE;
                    break;
            }
        }
    }

    private void reset() {
        this.style = Typeface.NORMAL;

        this.fontWeight = TEXT_FONT_WEIGHT_UNSPECIFIED;

        this.fontTypeface = null;
    }
}
