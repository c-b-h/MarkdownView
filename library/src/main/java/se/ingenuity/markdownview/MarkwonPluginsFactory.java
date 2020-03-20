package se.ingenuity.markdownview;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.List;

import io.noties.markwon.MarkwonPlugin;

public interface MarkwonPluginsFactory {
    @NonNull
    List<MarkwonPlugin> createMarkwonPlugins(@NonNull Context context);
}
