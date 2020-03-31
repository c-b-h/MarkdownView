package se.ingenuity.markdownview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

import io.noties.markwon.Markwon;
import se.ingenuity.markdownview.util.MarkwonBuilderFactory;

class MarkdownHelper {
    @NonNull
    private static final ThreadLocal<Map<String, Constructor<MarkwonBuilderFactory>>> CONSTRUCTORS =
            new ThreadLocal<>();

    @NonNull
    private final TextView textView;

    private Markwon markwon;

    MarkdownHelper(@NonNull TextView textView) {
        this.textView = textView;
    }

    void loadFromAttributes(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes) {
        @NonNull TypedArray a = context.obtainStyledAttributes(
                attrs,
                R.styleable.MarkdownView,
                defStyleAttr,
                defStyleRes);

        @Nullable String markwonFactoryBuilderClassName = a.getString(
                R.styleable.MarkdownView_markwonFactoryBuilder);
        if (markwonFactoryBuilderClassName == null) {
            markwonFactoryBuilderClassName = MarkwonBuilderFactory.class.getName();
        }

        try {
            markwon = parseMarkwon(
                    context,
                    attrs,
                    defStyleAttr,
                    defStyleRes,
                    markwonFactoryBuilderClassName);

            if (a.hasValue(R.styleable.MarkdownView_markdown)) {
                setMarkdown(a.getString(R.styleable.MarkdownView_markdown));
            }
        } finally {
            a.recycle();
        }
    }

    void setMarkdown(@Nullable String markdown) {
        if (markdown == null) {
            markdown = "";
        }

        markwon.setMarkdown(textView, markdown);
    }

    @SuppressWarnings("unchecked")
    private static Markwon parseMarkwon(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes,
            @NonNull String name) {
        try {
            @Nullable Map<String, Constructor<MarkwonBuilderFactory>> constructors =
                    CONSTRUCTORS.get();
            if (constructors == null) {
                constructors = new LinkedHashMap<>();
                CONSTRUCTORS.set(constructors);
            }
            @Nullable Constructor<MarkwonBuilderFactory> c = constructors.get(name);
            if (c == null) {
                final Class<MarkwonBuilderFactory> clazz =
                        (Class<MarkwonBuilderFactory>) Class.forName(
                                name, false, context.getClassLoader());
                c = clazz.getConstructor();
                c.setAccessible(true);
                constructors.put(name, c);
            }
            return c.newInstance().createBuilder(context, attrs, defStyleAttr, defStyleRes).build();
        } catch (Exception e) {
            throw new RuntimeException("Could not inflate MarkwonBuilderFactory subclass " + name, e);
        }
    }
}
