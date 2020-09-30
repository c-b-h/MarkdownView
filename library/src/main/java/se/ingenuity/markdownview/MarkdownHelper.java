package se.ingenuity.markdownview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.AbsSavedState;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonPlugin;
import se.ingenuity.markdownview.util.MarkdownViewPlugin;
import se.ingenuity.markdownview.util.MarkwonBuilderFactory;

class MarkdownHelper {
    @NonNull
    private static final ThreadLocal<Map<String, Constructor<MarkwonBuilderFactory>>> CONSTRUCTORS =
            new ThreadLocal<>();

    @NonNull
    private final TextView textView;

    private String markdown;

    @Nullable
    private Markwon markwon;

    private MutableBuilder pending;

    private MarkwonPlugin plugin;

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

        final boolean hasMarkdown = a.hasValue(R.styleable.MarkdownView_markdown);
        final String markdown = a.getString(R.styleable.MarkdownView_markdown);

        @Nullable String markwonFactoryBuilderClassName = a.getString(
                R.styleable.MarkdownView_markwonFactoryBuilder);
        if (markwonFactoryBuilderClassName == null) {
            ensureBuilder();
        } else {
            try {
                pending = new MutableBuilder(parseMarkwonBuilder(
                        context,
                        attrs,
                        defStyleAttr,
                        defStyleRes,
                        markwonFactoryBuilderClassName));
                if (!pending.plugins.contains(plugin)) {
                    pending.plugins.add(0, plugin);
                }
            } finally {
                a.recycle();
            }
        }

        if (hasMarkdown) {
            setMarkdown(markdown);
        }
    }

    void addMarkwonPlugins(boolean update, @NonNull MarkwonPlugin[] includes) {
        if (includes.length == 0) {
            return;
        }

        ensureBuilder();
        pending.plugins.addAll(Arrays.asList(includes));

        if (update) setMarkdown(this.markdown);
    }

    void removeMarkwonPlugins(boolean update, @NonNull MarkwonPlugin[] excludes) {
        if (excludes.length == 0) {
            return;
        }
        // Note that it is assumed that Core and MarkdownViewPlugin were not part of the remove
        // candidates.
        ensureBuilder();
        final List<MarkwonPlugin> mutable = pending.plugins;
        mutable.removeAll(Arrays.asList(excludes));

        if (update) setMarkdown(this.markdown);
    }

    void setMarkdown(@Nullable String markdown) {
        if (markdown == null) {
            markdown = "";
        }

        if (pending != null) {
            markwon = pending.build();
            pending = null;
        }

        this.markdown = markdown;
        markwon.setMarkdown(textView, markdown);
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
            markdown = ((SS) state).markdown;
        }
    }

    private void ensureBuilder() {
        if (pending == null) {
            if (markwon != null) {
                pending = new MutableBuilder(Markwon.builderNoCore(textView.getContext()))
                        .usePlugins(markwon.getPlugins())
                        .usePlugin(plugin);
            } else {
                pending = new MutableBuilder(Markwon.builder(textView.getContext()).usePlugin(plugin));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Markwon.Builder parseMarkwonBuilder(
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
            return c.newInstance().createBuilder(context, attrs, defStyleAttr, defStyleRes);
        } catch (Exception e) {
            throw new RuntimeException("Could not inflate MarkwonBuilderFactory subclass " + name, e);
        }
    }

    private static final class MutableBuilder implements Markwon.Builder {
        @NonNull
        private final Markwon.Builder delegate;

        @NonNull
        final List<MarkwonPlugin> plugins = new ArrayList<>();

        MutableBuilder(@NonNull Markwon.Builder delegate) {
            this.delegate = delegate;
        }

        @NonNull
        @Override
        public Markwon.Builder bufferType(@NonNull TextView.BufferType bufferType) {
            return delegate.bufferType(bufferType);
        }

        @NonNull
        @Override
        public Markwon.Builder textSetter(@NonNull Markwon.TextSetter textSetter) {
            return delegate.textSetter(textSetter);
        }

        @NonNull
        @Override
        public MutableBuilder usePlugin(@NonNull MarkwonPlugin plugin) {
            plugins.add(plugin);
            return this;
        }

        @NonNull
        @Override
        public MutableBuilder usePlugins(@NonNull Iterable<? extends MarkwonPlugin> plugins) {
            for (final MarkwonPlugin plugin : plugins) {
                this.plugins.add(plugin);
            }
            return this;
        }

        @NonNull
        @Override
        public Markwon.Builder fallbackToRawInputWhenEmpty(boolean fallbackToRawInputWhenEmpty) {
            return delegate.fallbackToRawInputWhenEmpty(fallbackToRawInputWhenEmpty);
        }

        @NonNull
        @Override
        public Markwon build() {
            delegate.usePlugins(plugins);
            return delegate.build();
        }
    }

    private static final class SS extends AbsSavedState {
        @Nullable
        final String markdown;

        protected SS(@NonNull Parcelable superState, @Nullable String markdown) {
            super(superState);
            this.markdown = markdown;
        }

        protected SS(@NonNull Parcel source) {
            super(source);
            markdown = source.readString();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected SS(@NonNull Parcel source, @Nullable ClassLoader loader) {
            super(source, loader);
            markdown = source.readString();
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(markdown);
        }

        public static final Creator<SS> CREATOR = new ClassLoaderCreator<SS>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
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
