package se.ingenuity.markdownview;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.AppCompatTextView;

import io.noties.markwon.MarkwonPlugin;

public class MarkdownAppCompatTextView extends AppCompatTextView implements MarkdownView {
    @NonNull
    private final MarkdownHelper markdownHelper;

    public MarkdownAppCompatTextView(@NonNull Context context) {
        this(context, null);
    }

    public MarkdownAppCompatTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public MarkdownAppCompatTextView(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MarkdownAppCompatTextView(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr);

        markdownHelper = new MarkdownHelper(this);
        markdownHelper.loadFromAttributes(context, attrs, R.attr.markdownStyles, defStyleRes);
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

    @Override
    public void setMarkdown(@Nullable String markdown) {
        markdownHelper.setMarkdown(markdown);
    }

    @Override
    public void addMarkwonPlugins(boolean update, @NonNull MarkwonPlugin... plugins) {
        markdownHelper.addMarkwonPlugins(update, plugins);
    }

    @Override
    public void removeMarkwonPlugins(boolean update, @NonNull MarkwonPlugin... plugins) {
        markdownHelper.removeMarkwonPlugins(update, plugins);
    }
}
