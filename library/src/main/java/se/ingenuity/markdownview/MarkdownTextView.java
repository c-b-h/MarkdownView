package se.ingenuity.markdownview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import io.noties.markwon.MarkwonPlugin;

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

    @Override
    public Parcelable onSaveInstanceState() {
        return markdownHelper.onSaveInstanceState(super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(@NonNull Parcelable state) {
        super.onRestoreInstanceState(markdownHelper.getSuperState(state));
        markdownHelper.onRestoreInstanceState(state);
    }

    public void setMarkdown(@Nullable String markdown) {
        markdownHelper.setMarkdown(markdown);
    }

    public void addMarkwonPlugins(boolean update, @NonNull MarkwonPlugin... plugins) {
        markdownHelper.addMarkwonPlugins(update, plugins);
    }

    public void removeMarkwonPlugins(boolean update, @NonNull MarkwonPlugin... plugins) {
        markdownHelper.removeMarkwonPlugins(update, plugins);
    }
}
