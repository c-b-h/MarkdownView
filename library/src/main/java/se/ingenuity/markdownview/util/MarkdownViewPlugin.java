package se.ingenuity.markdownview.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import org.commonmark.node.BlockQuote;
import org.commonmark.node.Code;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Heading;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.core.CoreProps;
import io.noties.markwon.core.MarkwonTheme;

final class MarkdownViewPlugin extends AbstractMarkwonPlugin {
    @NonNull
    private final Context context;
    @NonNull
    private final ResolvedAttributes resolvedAttributes;

    MarkdownViewPlugin(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes
    ) {
        this.context = context;
        this.resolvedAttributes = new ResolvedAttributes(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
        @Nullable ColorStateList color;
        if ((color = resolvedAttributes.getListItemColor()) != null) {
            builder.listItemColor(color.getDefaultColor());
        }

        if ((color = resolvedAttributes.getCodeBlockBackgroundColor()) != null) {
            builder.codeBlockBackgroundColor(color.getDefaultColor());
        }
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(SoftLineBreak.class, (visitor, softLineBreak) -> visitor.forceNewLine());
    }

    @Override
    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
        maybeSetHeadingFactory(builder);
        maybeSetFactory(
                builder,
                Emphasis.class,
                resolvedAttributes.getEmphasisStyle());
        maybeSetFactory(
                builder,
                StrongEmphasis.class,
                resolvedAttributes.getStrongEmphasisStyle());
        maybeSetFactory(
                builder,
                BlockQuote.class,
                resolvedAttributes.getBlockQuoteStyle());
        maybeSetFactory(builder,
                Code.class,
                resolvedAttributes.getCodeStyle());
        maybeSetFactory(builder,
                FencedCodeBlock.class,
                resolvedAttributes.getFencedCodeBlockStyle());
        maybeSetFactory(builder,
                IndentedCodeBlock.class,
                resolvedAttributes.getIndentedCodeBlockStyle());
        maybeSetListFactory(builder);
        maybeSetFactory(
                builder,
                Paragraph.class,
                resolvedAttributes.getParagraphStyle());
        maybeSetFactory(
                builder,
                Link.class,
                resolvedAttributes.getLinkStyle());
    }

    private <N extends Node> void maybeSetFactory(
            @NonNull MarkwonSpansFactory.Builder builder,
            @NonNull Class<N> node,
            @StyleRes int style) {
        if (style != Constants.ID_NULL) {
            builder.setFactory(
                    node,
                    ((configuration, props) -> SpanGenerator.createSpansForStyle(context, style))
            );
        }
    }

    private void maybeSetHeadingFactory(@NonNull MarkwonSpansFactory.Builder builder) {
        @NonNull final int[] headingStyles = resolvedAttributes.getHeadingStyles();

        if (ArrayUtils.any(headingStyles, (style) -> style != Constants.ID_NULL)) {
            @Nullable final SpanFactory originalFactory = builder.getFactory(Heading.class);

            builder.setFactory(Heading.class, (configuration, props) -> {
                @StyleRes int style = headingStyles[CoreProps.HEADING_LEVEL.require(props) - 1];
                if (style != Constants.ID_NULL) {
                    return SpanGenerator.createSpansForStyle(context, style);
                } else if (originalFactory != null) {
                    return originalFactory.getSpans(configuration, props);
                }

                return null;
            });
        }
    }

    private void maybeSetListFactory(@NonNull MarkwonSpansFactory.Builder builder) {
        @StyleRes final int orderedListStyle = resolvedAttributes.getOrderedListItemStyle();
        @StyleRes final int unorderedListStyle = resolvedAttributes.getUnorderedListItemStyle();

        if (orderedListStyle != Constants.ID_NULL || unorderedListStyle != Constants.ID_NULL) {
            @Nullable final SpanFactory originalFactory = builder.getFactory(ListItem.class);
            builder.setFactory(ListItem.class, (configuration, props) -> {
                if (CoreProps.ListItemType.ORDERED == CoreProps.LIST_ITEM_TYPE.require(props)) {
                    if (orderedListStyle != Constants.ID_NULL) {
                        return SpanGenerator.createSpansForStyle(context, orderedListStyle);
                    } else if (originalFactory != null) {
                        return originalFactory.getSpans(configuration, props);
                    }
                } else {
                    if (unorderedListStyle != Constants.ID_NULL) {
                        return SpanGenerator.createSpansForStyle(context, unorderedListStyle);
                    } else if (originalFactory != null) {
                        return originalFactory.getSpans(configuration, props);
                    }
                }

                return null;
            });
        }
    }

//    private <N extends Node> void maybeAppendFactory(
//            @NonNull MarkwonSpansFactory.Builder builder,
//            @NonNull Class<N> node,
//            @StyleableRes int styleable) {
//        @StyleRes int style = resolvedStyles.get(styleable, Constants.ID_NULL);
//        if (style != Constants.ID_NULL) {
//            builder.appendFactory(
//                    node,
//                    ((configuration, props) -> SpanGenerator.createSpansForStyle(context, style))
//            );
//        }
//    }
}