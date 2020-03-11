package se.ingenuity.markdownview.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.style.TextAppearanceSpan;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import java.util.Arrays;

class SpanGenerator {
    private SpanGenerator() {
    }

    @NonNull
    private static final int[] TEXT_APPEARANCE_ATTR = {android.R.attr.textAppearance};

    @NonNull
    private static final Object[] SPANS = new Object[2];

    @NonNull
    static Object[] createSpansForStyle(@NonNull Context context, @StyleRes int style) {
        TypedArray attributes = context.obtainStyledAttributes(style, TEXT_APPEARANCE_ATTR);
        @StyleRes final int textAppearance = attributes.getResourceId(0, Constants.ID_NULL);
        attributes.recycle();

        attributes = context.obtainStyledAttributes(TEXT_APPEARANCE_ATTR);
        final boolean styleIsTextAppearance =
                attributes.getResourceId(0, Constants.ID_NULL) == textAppearance;
        attributes.recycle();

        int index = 0;
        if (textAppearance != Constants.ID_NULL && !styleIsTextAppearance) {
            SPANS[index++] = new TextAppearanceSpan(context, textAppearance);
        }

        SPANS[index++] = new TextAppearanceSpan(context, style);

        if (index < SPANS.length) {
            Arrays.fill(SPANS, index, SPANS.length, null);
        }

        ArrayUtils.reverse(SPANS);
        return SPANS;
    }
}
