package se.ingenuity.markdownview;

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
    static Object[] createSpansForStyle(@NonNull Context context, @StyleRes int style) {
        TypedArray attributes = context.obtainStyledAttributes(style, TEXT_APPEARANCE_ATTR);
        @StyleRes final int textAppearance = attributes.getResourceId(0, Constants.ID_NULL);
        attributes.recycle();

        attributes = context.obtainStyledAttributes(TEXT_APPEARANCE_ATTR);
        final boolean styleIsTextAppearance =
                attributes.getResourceId(0, Constants.ID_NULL) == textAppearance;
        attributes.recycle();

        final Object[] spans = new Object[2];
        int index = 0;
        if (textAppearance != Constants.ID_NULL && !styleIsTextAppearance) {
            spans[index++] = new TextAppearanceSpan(context, textAppearance);
        }

        spans[index++] = new TextAppearanceSpan(context, style);

        if (index < spans.length) {
            Arrays.fill(spans, index, spans.length, null);
        }

        ArrayUtils.reverse(spans);
        return spans;
    }
}
