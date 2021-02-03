package se.ingenuity.markdownview.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.style.BackgroundColorSpan;
import android.text.style.LineBackgroundSpan;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.annotation.StyleRes;

import java.util.Arrays;

import se.ingenuity.markdownview.R;
import se.ingenuity.markdownview.util.span.TextAppearanceSpanCompat;

class SpanGenerator {
    @NonNull
    private static final int[] TEXT_APPEARANCE_ATTR = {android.R.attr.textAppearance};

    @NonNull
    private final Context context;

    SpanGenerator(@NonNull Context context) {
        this.context = context;
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

        @NonNull final Object[] buffer = new Object[4];

        int index = 0;
        if (textAppearance != Constants.ID_NULL && !styleIsTextAppearance) {
            buffer[index++] = new TextAppearanceSpanCompat(context, textAppearance);
        }

        buffer[index++] = new TextAppearanceSpanCompat(context, style);

        // Custom attributes
        attributes = context.obtainStyledAttributes(style, R.styleable.MarkdownTextView_Style);
        if (attributes.hasValue(R.styleable.MarkdownTextView_Style_mdBackgroundColor)) {
            buffer[index++] = new BackgroundColorSpan(attributes.getColor(0, Color.TRANSPARENT));
        }

        attributes.recycle();

        Arrays.fill(buffer, index, buffer.length, null);

        ArrayUtils.reverse(buffer);
        return buffer;
    }

    private static class Standard implements LineBackgroundSpan {
        private final int mColor;
        private Rect mBgRect = new Rect();
        private int mPadding = 20;

        /**
         * Constructor taking a color integer.
         *
         * @param color Color integer that defines the background color.
         */
        public Standard(@ColorInt int color) {
            mColor = color;
        }

        /**
         * @return the color of this span.
         * @see Standard#Standard(int)
         */
        @ColorInt
        public final int getColor() {
            return mColor;
        }

        @Override
        public void drawBackground(@NonNull Canvas canvas, @NonNull Paint paint,
                                   @Px int left, @Px int right,
                                   @Px int top, @Px int baseline, @Px int bottom,
                                   @NonNull CharSequence text, int start, int end,
                                   int lineNumber) {
            final int textWidth = Math.round(paint.measureText(text, start, end));
            final int paintColor = paint.getColor();
            // Draw the background
            mBgRect.set(left - mPadding,
                    top - (lineNumber == 0 ? mPadding / 2 : -(mPadding / 2)),
                    left + textWidth + mPadding,
                    bottom + mPadding / 2);
            paint.setColor(mColor);
            canvas.drawRect(mBgRect, paint);
            paint.setColor(paintColor);
        }
    }
}
