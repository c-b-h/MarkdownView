package se.ingenuity.markdownview.util;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import io.noties.markwon.Markwon;

/**
 * @deprecated Initialize builder using MarkwonBuilderFactory2
 */
@Deprecated
public interface MarkwonBuilderFactory {
    @NonNull
    Markwon.Builder createBuilder(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes);
}
