package se.ingenuity.markdownview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.customview.view.AbsSavedState;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonPlugin;

class MarkdownHelper {
    @NonNull
    private static final ThreadLocal<Map<String, Constructor<MarkwonPluginsFactory>>> CONSTRUCTORS =
            new ThreadLocal<>();

    @NonNull
    private final TextView textView;

    @NonNull
    private MarkwonPlugin plugin;

    @NonNull
    private Markwon markwon;

    @Nullable
    private String markdown;

    MarkdownHelper(@NonNull TextView textView) {
        this.textView = textView;
    }

    void loadFromAttributes(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes) {
        plugin = new MarkdownViewPlugin(context, attrs, defStyleAttr, defStyleRes);

        @NonNull TypedArray a = context.obtainStyledAttributes(
                attrs,
                R.styleable.MarkdownView,
                defStyleAttr,
                defStyleRes);

        List<MarkwonPlugin> plugins = Collections.emptyList();
        if (a.hasValue(R.styleable.MarkdownView_markwonPluginsFactory)) {
            final String markwonPluginsFactoryClassName = a.getString(
                    R.styleable.MarkdownView_markwonPluginsFactory);

            try {
                plugins = parseMarkwonPluginsFactory(
                        context,
                        markwonPluginsFactoryClassName);
                setMarkwonPlugins(plugins);
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        setMarkwonPlugins(plugins);

        if (a.hasValue(R.styleable.MarkdownView_markdown)) {
            setMarkdown(a.getString(R.styleable.MarkdownView_markdown));
        }

        a.recycle();
    }

    @NonNull
    Parcelable onSaveInstanceState(@NonNull Parcelable superState) {
        return new SS(superState, markdown);
    }

    @NonNull
    Parcelable getSuperState(@NonNull Parcelable state) {
        return ((AbsSavedState) state).getSuperState();
    }

    void onRestoreInstanceState(@NonNull Parcelable state) {
        if (state instanceof SS) {
            markdown = ((SS) state).getMarkdown();
        }
    }

    void setMarkdown(@Nullable String markdown) {
        if (markdown == null) {
            markdown = "";
        }

        this.markdown = markdown;
        markwon.setMarkdown(textView, markdown);
    }

    @NonNull
    List<? extends MarkwonPlugin> getMarkwonPlugins() {
        final List<? extends MarkwonPlugin> plugins = new ArrayList(markwon.getPlugins());
        plugins.remove(plugin);
        return plugins;
    }

    void setMarkwonPlugins(@NonNull List<MarkwonPlugin> plugins) {
        plugins = new ArrayList<>(plugins);
        plugins.add(plugin);

        markwon = Markwon.builderNoCore(textView.getContext()).usePlugins(plugins).build();

        if (markdown == null) {
            return;
        }

        setMarkdown(markdown);
    }

    @SuppressWarnings("unchecked")
    private static List<MarkwonPlugin> parseMarkwonPluginsFactory(@NonNull Context context,
                                                                  @NonNull String name) {
        try {
            @Nullable Map<String, Constructor<MarkwonPluginsFactory>> constructors =
                    CONSTRUCTORS.get();
            if (constructors == null) {
                constructors = new LinkedHashMap<>();
                CONSTRUCTORS.set(constructors);
            }
            @Nullable Constructor<MarkwonPluginsFactory> c = constructors.get(name);
            if (c == null) {
                final Class<MarkwonPluginsFactory> clazz =
                        (Class<MarkwonPluginsFactory>) Class.forName(
                                name, false, context.getClassLoader());
                c = clazz.getConstructor();
                c.setAccessible(true);
                constructors.put(name, c);
            }
            return c.newInstance().createMarkwonPlugins(context);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Could not inflate MarkwonPluginsFactory subclass " + name, e);
        }
    }

    private static final class SS extends AbsSavedState {
        @Nullable
        private final String markdown;

        protected SS(@NonNull Parcelable superState, @Nullable String markdown) {
            super(superState);
            this.markdown = markdown;
        }

        protected SS(@NonNull Parcel source) {
            this(source, null);
        }

        protected SS(@NonNull Parcel source, @Nullable ClassLoader loader) {
            super(source, loader);
            markdown = source.readString();
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(markdown);
        }

        @Nullable
        public String getMarkdown() {
            return markdown;
        }

        public static final Creator<SS> CREATOR = new ClassLoaderCreator<SS>() {
            @Override
            public SS createFromParcel(@NonNull Parcel in, @Nullable ClassLoader loader) {
                return new SS(in, loader);
            }

            @Override
            public SS createFromParcel(Parcel in) {
                return new SS(in);
            }

            @Override
            public SS[] newArray(int size) {
                return new SS[size];
            }
        };
    }
}
