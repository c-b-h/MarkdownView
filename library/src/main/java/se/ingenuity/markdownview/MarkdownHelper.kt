package se.ingenuity.markdownview

import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.AbsSavedState
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.RequiresApi
import androidx.annotation.StyleRes
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonPlugin
import io.noties.markwon.core.CorePlugin
import se.ingenuity.markdownview.util.MarkdownViewPlugin
import se.ingenuity.markdownview.util.MarkwonBuilderFactory
import se.ingenuity.markdownview.util.MarkwonBuilderFactory2
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

internal class MarkdownHelper(private val textView: TextView) {
    private lateinit var plugin: MarkwonPlugin

    private var markdown: String? = null
    private var markwon: Markwon? = null

    private var staticBuilder: MutableBuilder? = null
    private var dynamicBuilder: MutableBuilder? = null

    fun loadFromAttributes(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) {
        plugin = MarkdownViewPlugin(context, attrs, defStyleAttr, defStyleRes)
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.MarkdownTextView,
            defStyleAttr,
            defStyleRes
        )
        val hasMarkdown = a.hasValue(R.styleable.MarkdownTextView_markdown)
        val markdown = a.getString(R.styleable.MarkdownTextView_markdown)
        val markwonFactoryBuilderClassName = a.getString(
            R.styleable.MarkdownTextView_markwonFactoryBuilder
        )
        if (markwonFactoryBuilderClassName == null) {
            ensureStaticBuilder()
        } else {
            try {
                val builder = parseMarkwonBuilder(
                    context,
                    attrs,
                    defStyleAttr,
                    defStyleRes,
                    markwonFactoryBuilderClassName
                )
                staticBuilder = if (builder is MutableBuilder) {
                    builder
                } else {
                    MutableBuilder(builder)
                }
                val indexOfCorePlugin = staticBuilder!!.indexOfFirst { plugin ->
                    plugin is CorePlugin
                }

                val factoryContainsMarkdownViewPlugin = staticBuilder!!.find { plugin ->
                    plugin is MarkdownViewPlugin
                } != null

                if (!factoryContainsMarkdownViewPlugin) {
                    // Ensure MarkdownViewPlugin if factory not pointing to one already otherwise
                    // `this` class has no purpose.
                    staticBuilder!!.usePlugin(indexOfCorePlugin.coerceAtLeast(0), plugin)
                }
            } finally {
                a.recycle()
            }
        }
        dynamicBuilder = staticBuilder
        if (hasMarkdown) {
            setMarkdown(markdown)
        }
    }

    fun addMarkwonPlugins(update: Boolean, vararg includes: MarkwonPlugin) {
        if (includes.isEmpty()) {
            return
        }
        ensureDynamicBuilder()
        dynamicBuilder!!.usePlugins(includes.toList())
        if (update) setMarkdown(markdown)
    }

    fun removeMarkwonPlugins(update: Boolean, vararg excludes: MarkwonPlugin) {
        if (excludes.isEmpty()) {
            return
        }
        // Note that it is assumed that Core and MarkdownViewPlugin were not part of the remove
        // candidates.
        ensureDynamicBuilder()
        dynamicBuilder!!.removePlugins(excludes.toSet())
        if (update) setMarkdown(markdown)
    }

    fun setMarkdown(markdown: String?) {
        if (dynamicBuilder != null) {
            markwon = dynamicBuilder!!.build()
            dynamicBuilder = null
        }

        this.markdown = markdown.orEmpty()
        markwon!!.setMarkdown(textView, markdown.orEmpty())
    }

    fun onSaveInstanceState(superState: Parcelable): Parcelable {
        return SS(superState, markdown)
    }

    fun getSuperState(state: Parcelable): Parcelable? {
        return (state as? AbsSavedState)?.superState
    }

    fun onRestoreInstanceState(state: Parcelable) {
        if (state is SS) {
            markdown = state.markdown
        }
    }

    private fun ensureStaticBuilder() {
        if (staticBuilder == null) {
            staticBuilder = MutableBuilder(
                Markwon.builder(textView.context).usePlugin(plugin)
            )
        }
    }

    private fun ensureDynamicBuilder() {
        if (dynamicBuilder == null) {
            dynamicBuilder = if (markwon != null) {
                MutableBuilder(
                    Markwon.builderNoCore(textView.context).usePlugins(
                        markwon!!.plugins
                    )
                )
            } else {
                MutableBuilder(Markwon.builder(textView.context).usePlugin(plugin))
            }
        }
    }

    private class MutableBuilder(
        private val delegate: Markwon.Builder
    ) : Markwon.Builder by delegate, Iterable<MarkwonPlugin> {
        private val plugins = mutableListOf<MarkwonPlugin>()
        override fun usePlugin(plugin: MarkwonPlugin): MutableBuilder {
            plugins.add(plugin)
            return this
        }

        override fun usePlugins(plugins: Iterable<MarkwonPlugin>): MutableBuilder {
            this.plugins.addAll(plugins)
            return this
        }

        override fun build(): Markwon {
            delegate.usePlugins(plugins)
            return delegate.build()
        }

        override fun iterator(): Iterator<MarkwonPlugin> = plugins.iterator()


        fun usePlugin(index: Int, plugin: MarkwonPlugin): MutableBuilder {
            plugins.add(index, plugin)
            return this
        }

        fun removePlugins(plugins: Iterable<MarkwonPlugin>): MutableBuilder {
            this.plugins.removeAll(plugins.toSet())
            return this
        }
    }

    private class SS : AbsSavedState {
        val markdown: String?

        constructor(superState: Parcelable, markdown: String?) : super(superState) {
            this.markdown = markdown
        }

        constructor(source: Parcel) : super(source) {
            markdown = source.readString()
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        constructor(source: Parcel, loader: ClassLoader?) : super(source, loader) {
            markdown = source.readString()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeString(markdown)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SS> = object : Parcelable.ClassLoaderCreator<SS> {
                @RequiresApi(api = Build.VERSION_CODES.N)
                override fun createFromParcel(`in`: Parcel, loader: ClassLoader?): SS {
                    return SS(`in`, loader)
                }

                override fun createFromParcel(`in`: Parcel): SS {
                    return SS(`in`)
                }

                override fun newArray(size: Int): Array<SS?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        private val CONSTRUCTORS = ThreadLocal<MutableMap<String, Constructor<Any>?>>()
        private val SINGLETONS = ThreadLocal<MutableMap<String, Any?>>()

        @Suppress("UNCHECKED_CAST", "DEPRECATION")
        private fun parseMarkwonBuilder(
            context: Context,
            attrs: AttributeSet?,
            @AttrRes defStyleAttr: Int,
            @StyleRes defStyleRes: Int,
            name: String
        ): Markwon.Builder {
            var singletons = SINGLETONS.get()
            if (singletons == null) {
                singletons = LinkedHashMap()
                SINGLETONS.set(singletons)
            }
            return try {
                // try kotlin object first
                var singleton = singletons[name]
                if (singleton == null) {
                    singleton = Class
                        .forName(name, false, context.classLoader)
                        .getDeclaredField("INSTANCE")[null] as Any
                    singletons[name] = singleton
                }
                if (singleton is MarkwonBuilderFactory2) {
                    val builder: Markwon.Builder = MutableBuilder(Markwon.builderNoCore(context))
                    singleton.initBuilder(
                        context,
                        attrs,
                        defStyleAttr,
                        defStyleRes,
                        builder
                    )
                    builder
                } else {
                    (singleton as MarkwonBuilderFactory)
                        .createBuilder(context, attrs, defStyleAttr, defStyleRes)
                }
            } catch (ignore: IllegalAccessException) {
                var constructors = CONSTRUCTORS.get()
                if (constructors == null) {
                    constructors = LinkedHashMap()
                    CONSTRUCTORS.set(constructors)
                }
                try {
                    var constructor = constructors[name]
                    if (constructor == null) {
                        val clazz = Class
                            .forName(name, false, context.classLoader) as Class<Any>
                        constructor = clazz.getConstructor()
                        constructor.isAccessible = true
                        constructors[name] = constructor
                    }
                    val newInstance = constructor!!.newInstance()
                    if (newInstance is MarkwonBuilderFactory2) {
                        val builder: Markwon.Builder =
                            MutableBuilder(Markwon.builderNoCore(context))
                        newInstance.initBuilder(
                            context,
                            attrs,
                            defStyleAttr,
                            defStyleRes,
                            builder
                        )
                        builder
                    } else {
                        (newInstance as MarkwonBuilderFactory)
                            .createBuilder(context, attrs, defStyleAttr, defStyleRes)
                    }
                } catch (c: ClassNotFoundException) {
                    throw IllegalArgumentException(
                        "Could not inflate MarkwonBuilderFactory $name",
                        c
                    )
                } catch (c: NoSuchMethodException) {
                    throw IllegalArgumentException(
                        "Could not inflate MarkwonBuilderFactory $name",
                        c
                    )
                } catch (c: IllegalAccessException) {
                    throw IllegalArgumentException(
                        "Could not inflate MarkwonBuilderFactory $name",
                        c
                    )
                } catch (c: InvocationTargetException) {
                    throw IllegalArgumentException(
                        "Could not inflate MarkwonBuilderFactory $name",
                        c
                    )
                } catch (c: InstantiationException) {
                    throw IllegalArgumentException(
                        "Could not inflate MarkwonBuilderFactory $name",
                        c
                    )
                }
            } catch (ignore: ClassNotFoundException) {
                var constructors = CONSTRUCTORS.get()
                if (constructors == null) {
                    constructors = LinkedHashMap()
                    CONSTRUCTORS.set(constructors)
                }
                try {
                    var constructor = constructors[name]
                    if (constructor == null) {
                        val clazz = Class
                            .forName(name, false, context.classLoader) as Class<Any>
                        constructor = clazz.getConstructor()
                        constructor.isAccessible = true
                        constructors[name] = constructor
                    }
                    val newInstance = constructor!!.newInstance()
                    if (newInstance is MarkwonBuilderFactory2) {
                        val builder: Markwon.Builder =
                            MutableBuilder(Markwon.builderNoCore(context))
                        newInstance.initBuilder(
                            context,
                            attrs,
                            defStyleAttr,
                            defStyleRes,
                            builder
                        )
                        builder
                    } else {
                        (newInstance as MarkwonBuilderFactory)
                            .createBuilder(context, attrs, defStyleAttr, defStyleRes)
                    }
                } catch (c: ClassNotFoundException) {
                    throw IllegalArgumentException(
                        "Could not inflate MarkwonBuilderFactory $name",
                        c
                    )
                } catch (c: NoSuchMethodException) {
                    throw IllegalArgumentException(
                        "Could not inflate MarkwonBuilderFactory $name",
                        c
                    )
                } catch (c: IllegalAccessException) {
                    throw IllegalArgumentException(
                        "Could not inflate MarkwonBuilderFactory $name",
                        c
                    )
                } catch (c: InvocationTargetException) {
                    throw IllegalArgumentException(
                        "Could not inflate MarkwonBuilderFactory $name",
                        c
                    )
                } catch (c: InstantiationException) {
                    throw IllegalArgumentException(
                        "Could not inflate MarkwonBuilderFactory $name",
                        c
                    )
                }
            } catch (ignore: NoSuchFieldException) {
                var constructors = CONSTRUCTORS.get()
                if (constructors == null) {
                    constructors = LinkedHashMap()
                    CONSTRUCTORS.set(constructors)
                }
                try {
                    var constructor = constructors[name]
                    if (constructor == null) {
                        val clazz = Class
                            .forName(name, false, context.classLoader) as Class<Any>
                        constructor = clazz.getConstructor()
                        constructor.isAccessible = true
                        constructors[name] = constructor
                    }
                    val newInstance = constructor!!.newInstance()
                    if (newInstance is MarkwonBuilderFactory2) {
                        val builder: Markwon.Builder =
                            MutableBuilder(Markwon.builderNoCore(context))
                        newInstance.initBuilder(
                            context,
                            attrs,
                            defStyleAttr,
                            defStyleRes,
                            builder
                        )
                        builder
                    } else {
                        (newInstance as MarkwonBuilderFactory)
                            .createBuilder(context, attrs, defStyleAttr, defStyleRes)
                    }
                } catch (c: ClassNotFoundException) {
                    throw IllegalArgumentException(
                        "Could not inflate MarkwonBuilderFactory $name",
                        c
                    )
                } catch (c: NoSuchMethodException) {
                    throw IllegalArgumentException(
                        "Could not inflate MarkwonBuilderFactory $name",
                        c
                    )
                } catch (c: IllegalAccessException) {
                    throw IllegalArgumentException(
                        "Could not inflate MarkwonBuilderFactory $name",
                        c
                    )
                } catch (c: InvocationTargetException) {
                    throw IllegalArgumentException(
                        "Could not inflate MarkwonBuilderFactory $name",
                        c
                    )
                } catch (c: InstantiationException) {
                    throw IllegalArgumentException(
                        "Could not inflate MarkwonBuilderFactory $name",
                        c
                    )
                }
            }
        }
    }
}
