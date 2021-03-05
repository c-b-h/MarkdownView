package se.ingenuity.markdownview.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.style.BackgroundColorSpan;
import android.text.style.LineBackgroundSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.annotation.StyleRes;

import java.util.Arrays;

import io.noties.markwon.core.spans.TextViewSpan;
import se.ingenuity.markdownview.R;
import se.ingenuity.markdownview.util.span.TextAppearanceSpanCompat;

class SpanGenerator {
    private static final int INTRINSIC_LINE_BACKGROUND_PADDING = -1;

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

        @NonNull final Object[] buffer = new Object[5];

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

        if (attributes.hasValue(R.styleable.MarkdownTextView_Style_mdLineBackground)) {
            final int lineBackground = attributes.getColor(
                    R.styleable.MarkdownTextView_Style_mdLineBackground, Color.TRANSPARENT);

            final int padAttrIndex = R.styleable.MarkdownTextView_Style_mdLineBackgroundPadding;
            int padding = INTRINSIC_LINE_BACKGROUND_PADDING;
            if (attributes.getType(padAttrIndex) == TypedValue.TYPE_DIMENSION) {
                padding = attributes.getDimensionPixelSize(padAttrIndex,
                        INTRINSIC_LINE_BACKGROUND_PADDING);
            }

            buffer[index++] = new PaddedLineBackgroundSpan(lineBackground, padding);
        }

        attributes.recycle();

        Arrays.fill(buffer, index, buffer.length, null);

        ArrayUtils.reverse(buffer);
        return buffer;
    }

    private static class PaddedLineBackgroundSpan implements LineBackgroundSpan {
        private final int mColor;
        private final Rect mTextRect = new Rect();
        private final Rect mContainerRect = new Rect();
        private final int mPadding;

        /**
         * Constructor taking a color integer.
         *
         * @param color Color integer that defines the background color.
         */
        PaddedLineBackgroundSpan(@ColorInt int color, @Px int padding) {
            mColor = color;
            mPadding = padding;
        }

        @Override
        public void drawBackground(@NonNull Canvas canvas, @NonNull Paint paint,
                                   @Px int left, @Px int right,
                                   @Px int top, @Px int baseline, @Px int bottom,
                                   @NonNull CharSequence text, int start, int end,
                                   int lineNumber) {
            final TextView textView = TextViewSpan.textViewOf(text);

            final Layout layout = textView.getLayout();
            final boolean firstLine = lineNumber == 0;

            final boolean useIntrinsicPadding = mPadding == INTRINSIC_LINE_BACKGROUND_PADDING;

            // Draw the background
            mTextRect.set(
                    left - (useIntrinsicPadding ? textView.getPaddingLeft() : mPadding),
                    top - (firstLine ? (useIntrinsicPadding ? textView.getPaddingTop() : mPadding) / 2 : 0),
                    left + Math.round(layout.getLineWidth(lineNumber)) + (useIntrinsicPadding ? textView.getPaddingRight() : mPadding),
                    bottom + (useIntrinsicPadding ? textView.getPaddingBottom() : mPadding)
            );

            mContainerRect.set(
                    Math.min(left, mTextRect.left),
                    Math.min(top, mTextRect.top),
                    Math.max(right, mTextRect.right),
                    Math.max(bottom, mTextRect.bottom)
            );

            Gravity.apply(
                    textView.getGravity(),
                    mTextRect.width(),
                    mTextRect.height(),
                    mContainerRect,
                    0,
                    0,
                    mTextRect
            );

            final int paintColor = paint.getColor();

            paint.setColor(mColor);
            canvas.drawRect(mTextRect, paint);
            paint.setColor(paintColor);
        }
    }
}
