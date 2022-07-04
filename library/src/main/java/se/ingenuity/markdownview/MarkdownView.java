package se.ingenuity.markdownview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.MarkwonPlugin;

public interface MarkdownView {
    void setMarkdown(@Nullable String markdown);

    void addMarkwonPlugins(boolean update, @NonNull MarkwonPlugin... plugins);

    void removeMarkwonPlugins(boolean update, @NonNull MarkwonPlugin... plugins);
}
