package se.ingenuity.markdownview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

@SuppressLint("AppCompatCustomView")
public class MarkdownTextView extends TextView {
    @NonNull
    private final MarkdownHelper markdownHelper;

    public MarkdownTextView(@NonNull Context context) {
        this(context, null);
    }

    public MarkdownTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public MarkdownTextView(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MarkdownTextView(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        markdownHelper = new MarkdownHelper(this);
        markdownHelper.loadFromAttributes(context, attrs, R.attr.markdownStyles, 0);
    }

    public void setMarkdown(@Nullable String markdown) {
        markdownHelper.setMarkdown(markdown);
    }
}
