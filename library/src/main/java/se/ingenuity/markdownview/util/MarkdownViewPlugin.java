package se.ingenuity.markdownview.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import androidx.annotation.AttrRes;
import androidx.annotation.Dimension;
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

import java.util.List;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.RenderProps;
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
        if ((color = resolvedAttributes.listItemColor) != null) {
            builder.listItemColor(color.getDefaultColor());
        }

        if ((color = resolvedAttributes.codeBlockBackgroundColor) != null) {
            builder.codeBlockBackgroundColor(color.getDefaultColor());
        }

        if ((color = resolvedAttributes.codeBlockTextColor) != null) {
            builder.codeBlockTextColor(color.getDefaultColor());
        }

        @Dimension int dimension;
        if ((dimension = resolvedAttributes.codeBlockMargin) != Constants.UNDEFINED_DIMEN) {
            builder.codeBlockMargin(dimension);
        }

        if ((color = resolvedAttributes.thematicBreakColor) != null) {
            builder.thematicBreakColor(color.getDefaultColor());
        }

        if ((dimension = resolvedAttributes.thematicBreakHeight) != Constants.UNDEFINED_DIMEN) {
            builder.thematicBreakHeight(dimension);
        }
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(SoftLineBreak.class, (visitor, softLineBreak) -> visitor.forceNewLine());
    }

    @Override
    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
        maybeApplyHeadingFactories(builder);
        maybeApplyFactories(Emphasis.class, builder, resolvedAttributes.emphasisStyleGroup);
        maybeApplyFactories(StrongEmphasis.class, builder,
                resolvedAttributes.strongEmphasisStyleGroup);
        maybeApplyFactories(BlockQuote.class, builder, resolvedAttributes.blockQuoteStyleGroup);
        maybeApplyFactories(Code.class, builder, resolvedAttributes.codeStyleGroup);
        maybeApplyFactories(FencedCodeBlock.class, builder,
                resolvedAttributes.fencedCodeBlockStyleGroup);
        maybeApplyFactories(IndentedCodeBlock.class, builder,
                resolvedAttributes.indentedCodeBlockStyleGroup);
        maybeApplyListFactories(builder);
        maybeApplyFactories(Paragraph.class, builder, resolvedAttributes.paragraphStyleGroup);
        maybeApplyFactories(Link.class, builder, resolvedAttributes.linkStyleGroup);
    }

    private <N extends Node> void maybeApplyFactories(
            @NonNull Class<N> node,
            @NonNull MarkwonSpansFactory.Builder builder,
            @NonNull ResolvedAttributes.StyleGroup styleGroup) {
        if (styleGroup.hasPreStyle()) {
            builder.appendFactory(
                    node,
                    ((configuration, props) -> SpanGenerator
                            .createSpansForStyle(context, styleGroup.preStyle))
            );
        }

        if (styleGroup.hasStyle()) {
            builder.setFactory(
                    node,
                    ((configuration, props) -> SpanGenerator
                            .createSpansForStyle(context, styleGroup.style))
            );
        }

        if (styleGroup.hasPostStyle()) {
            builder.prependFactory(
                    node,
                    ((configuration, props) -> SpanGenerator
                            .createSpansForStyle(context, styleGroup.postStyle))
            );
        }
    }

    private void maybeApplyHeadingFactories(@NonNull MarkwonSpansFactory.Builder builder) {
        @NonNull final List<ResolvedAttributes.StyleGroup> headingStyleGroups =
                resolvedAttributes.headingStyleGroups;

        @Nullable final SpanFactory originalFactory = builder.getFactory(Heading.class);
        if (CollectionUtils.any(headingStyleGroups, ResolvedAttributes.StyleGroup::hasPreStyle)) {
            builder.appendFactory(Heading.class, new HeadingSpanFactory(
                    context,
                    null,
                    ArrayUtils.map(headingStyleGroups, (styleGroup) -> styleGroup.preStyle)));
        }

        if (CollectionUtils.any(headingStyleGroups, ResolvedAttributes.StyleGroup::hasStyle)) {
            builder.setFactory(Heading.class, new HeadingSpanFactory(
                    context,
                    originalFactory,
                    ArrayUtils.map(headingStyleGroups, (styleGroup) -> styleGroup.style)));
        }

        if (CollectionUtils.any(headingStyleGroups, ResolvedAttributes.StyleGroup::hasPostStyle)) {
            builder.setFactory(Heading.class, new HeadingSpanFactory(
                    context,
                    null,
                    ArrayUtils.map(headingStyleGroups, (styleGroup) -> styleGroup.postStyle)));
        }
    }

    private void maybeApplyListFactories(@NonNull MarkwonSpansFactory.Builder builder) {
        @NonNull final ResolvedAttributes.StyleGroup ordered =
                resolvedAttributes.orderedListItemStyleGroup;
        @NonNull final ResolvedAttributes.StyleGroup unordered =
                resolvedAttributes.unorderedListItemStyleGroup;

        @Nullable final SpanFactory originalFactory = builder.getFactory(ListItem.class);
        if (ordered.hasPreStyle() || unordered.hasPreStyle()) {
            builder.appendFactory(ListItem.class, new ListItemSpanFactory(
                    context, null, ordered.preStyle, unordered.preStyle));
        }
        if (ordered.hasStyle() || unordered.hasStyle()) {
            builder.setFactory(ListItem.class, new ListItemSpanFactory(
                    context, originalFactory, ordered.style, unordered.style));
        }
        if (ordered.hasPostStyle() || unordered.hasPostStyle()) {
            builder.prependFactory(ListItem.class, new ListItemSpanFactory(
                    context, null, ordered.postStyle, unordered.postStyle));
        }
    }

    private final static class ListItemSpanFactory implements SpanFactory {
        @NonNull
        private final Context context;
        @Nullable
        private final SpanFactory originalFactory;
        @StyleRes
        private final int orderedStyle;
        @StyleRes
        private final int unorderedStyle;

        ListItemSpanFactory(@NonNull Context context,
                            @Nullable SpanFactory originalFactory,
                            @StyleRes int orderedStyle,
                            @StyleRes int unorderedStyle) {
            this.context = context;
            this.originalFactory = originalFactory;
            this.orderedStyle = orderedStyle;
            this.unorderedStyle = unorderedStyle;
        }

        @Nullable
        @Override
        public Object getSpans(@NonNull MarkwonConfiguration configuration,
                               @NonNull RenderProps props) {
            if (CoreProps.ListItemType.ORDERED == CoreProps.LIST_ITEM_TYPE.require(props) &&
                    ResolvedAttributes.StyleGroup.isStyleValid(orderedStyle)) {
                return SpanGenerator.createSpansForStyle(context, orderedStyle);
            } else if (CoreProps.ListItemType.BULLET == CoreProps.LIST_ITEM_TYPE.require(props) &&
                    ResolvedAttributes.StyleGroup.isStyleValid(unorderedStyle)) {
                return SpanGenerator.createSpansForStyle(context, unorderedStyle);
            } else if (originalFactory != null) {
                return originalFactory.getSpans(configuration, props);
            }
            return null;
        }
    }

    private static final class HeadingSpanFactory implements SpanFactory {
        @NonNull
        private final Context context;
        @Nullable
        private final SpanFactory originalFactory;
        @NonNull
        private final int[] headingStyles;

        HeadingSpanFactory(@NonNull Context context,
                           @Nullable SpanFactory originalFactory,
                           @NonNull int[] headingStyles) {
            this.context = context;
            this.originalFactory = originalFactory;
            this.headingStyles = headingStyles;
        }

        @Nullable
        @Override
        public Object getSpans(@NonNull MarkwonConfiguration configuration,
                               @NonNull RenderProps props) {

            final int indexOfLevel = CoreProps.HEADING_LEVEL.require(props) - 1;
            @StyleRes final int style = headingStyles[indexOfLevel];
            if (ResolvedAttributes.StyleGroup.isStyleValid(style)) {
                return SpanGenerator.createSpansForStyle(context, style);
            } else if (originalFactory != null) {
                return originalFactory.getSpans(configuration, props);
            }

            return null;
        }
    }
}