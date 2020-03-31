package se.ingenuity.markdownview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.google.android.material.textview.MaterialTextView;

public class MarkdownMaterialTextView extends MaterialTextView {
    @NonNull
    private final MarkdownHelper markdownHelper;

    public MarkdownMaterialTextView(@NonNull Context context) {
        this(context, null);
    }

    public MarkdownMaterialTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public MarkdownMaterialTextView(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MarkdownMaterialTextView(
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
