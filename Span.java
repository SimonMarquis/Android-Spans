/*
 * Copyright (C) 2017 Simon Marquis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.smarquis.trusk;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.MaskFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.StyleRes;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.ClickableSpan;
import android.text.style.DrawableMarginSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.IconMarginSpan;
import android.text.style.ImageSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.MaskFilterSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

import java.lang.reflect.Array;

/**
 * Java wrapper around SpannableStringBuilder
 * Inspired by JakeWharton's Truss and Kotlin's kotlinx.html
 *
 * @see <a href="https://gist.github.com/JakeWharton/11274467">Truss</a>
 * @see <a href="https://github.com/Kotlin/kotlinx.html">kotlinx.html</a>
 */
@SuppressWarnings("WeakerAccess, unused")
public class Span {

    @Nullable
    private Object[] nodes;

    Span(@Nullable Object[] nodes) {
        this.nodes = nodes;
    }

    public static Span create(Object... nodes) {
        return new Span(nodes);
    }

    android.text.Spannable build() {
        return build(new SpannableStringBuilder());
    }

    android.text.Spannable build(SpannableStringBuilder builder) {
        if (nodes != null) {
            for (Object child : nodes) {
                if (child instanceof Span) {
                    ((Span) child).build(builder);
                } else {
                    builder.append(child.toString());
                }
            }
        }
        return builder;
    }

    public Span append(Object... nodes) {
        if (nodes == null || nodes.length == 0) {
            return this;
        } else if (this.nodes == null || this.nodes.length == 0) {
            this.nodes = nodes;
            return this;
        }
        final Object[] joinedArray = (Object[]) Array.newInstance(Object.class, this.nodes.length + nodes.length);
        System.arraycopy(this.nodes, 0, joinedArray, 0, this.nodes.length);
        System.arraycopy(nodes, 0, joinedArray, this.nodes.length, nodes.length);
        this.nodes = joinedArray;
        return this;
    }

    static class Node extends Span {

        Node(@Nullable Object[] nodes) {
            super(nodes);
        }

        public static Node span(Object span, Object... nodes) {
            return new SpanNode(span, nodes);
        }

        public static Span text(String content) {
            return new Leaf(content);
        }

        public static Node style(Integer style, Object... nodes) {
            return new SpanNode(new StyleSpan(style), nodes);
        }

        public static Node normal(Object... nodes) {
            return new SpanNode(new StyleSpan(Typeface.NORMAL), nodes);
        }

        public static Node bold(Object... nodes) {
            return new SpanNode(new StyleSpan(Typeface.BOLD), nodes);
        }

        public static Node italic(Object... nodes) {
            return new SpanNode(new StyleSpan(Typeface.ITALIC), nodes);
        }

        public static Node boldItalic(Object... nodes) {
            return new SpanNode(new StyleSpan(Typeface.BOLD_ITALIC), nodes);
        }

        public static Node underline(Object... nodes) {
            return new SpanNode(new UnderlineSpan(), nodes);
        }

        public static Node typeface(String family, Object... nodes) {
            return new SpanNode(new TypefaceSpan(family), nodes);
        }

        public static Node sansSerif(Object... nodes) {
            return typeface("sans-serif", nodes);
        }

        public static Node serif(Object... nodes) {
            return typeface("serif", nodes);
        }

        public static Node monospace(Object... nodes) {
            return typeface("monospace", nodes);
        }

        public static Node appearance(Context context, @StyleRes Integer appearance, Object... nodes) {
            return new SpanNode(new TextAppearanceSpan(context, appearance), nodes);
        }

        public static Node superscript(Object... nodes) {
            return new SpanNode(new SuperscriptSpan(), nodes);
        }

        public static Node subscript(Object... nodes) {
            return new SpanNode(new SubscriptSpan(), nodes);
        }

        public static Node strikethrough(Object... nodes) {
            return new SpanNode(new StrikethroughSpan(), nodes);
        }

        public static Node scaleX(Float proportion, Object... nodes) {
            return new SpanNode(new ScaleXSpan(proportion), nodes);
        }

        public static Node relativeSize(Float proportion, Object... nodes) {
            return new SpanNode(new RelativeSizeSpan(proportion), nodes);
        }

        public static Node absoluteSize(@Px Integer size, Object... nodes) {
            return new SpanNode(new AbsoluteSizeSpan(size), nodes);
        }

        public static Node absoluteSize(@Px Integer size, Boolean dip, Object... nodes) {
            return new SpanNode(new AbsoluteSizeSpan(size, dip), nodes);
        }

        public static Node quote(Object... nodes) {
            return new SpanNode(new QuoteSpan(), nodes);
        }

        public static Node quote(@ColorInt Integer color, Object... nodes) {
            return new SpanNode(new QuoteSpan(color), nodes);
        }

        public static Node mask(MaskFilter filter, Object... nodes) {
            return new SpanNode(new MaskFilterSpan(filter), nodes);
        }

        public static Node leadingMargin(Integer every, Object... nodes) {
            return new SpanNode(new LeadingMarginSpan.Standard(every), nodes);
        }

        public static Node leadingMargin(Integer first, Integer rest, Object... nodes) {
            return new SpanNode(new LeadingMarginSpan.Standard(first, rest), nodes);
        }

        public static Node foregroundColor(@ColorInt Integer color, Object... nodes) {
            return new SpanNode(new ForegroundColorSpan(color), nodes);
        }

        public static Node backgroundColor(@ColorInt Integer color, Object... nodes) {
            return new SpanNode(new BackgroundColorSpan(color), nodes);
        }

        public static Node bullet(Object... nodes) {
            return new SpanNode(new BulletSpan(), nodes);
        }

        public static Node bullet(@Px Integer gapWidth, Object... nodes) {
            return new SpanNode(new BulletSpan(gapWidth), nodes);
        }

        public static Node bullet(Integer gapWidth, @ColorInt Integer color, Object... nodes) {
            return new SpanNode(new BulletSpan(gapWidth, color), nodes);
        }

        public static Node align(Layout.Alignment align, Object... nodes) {
            return new SpanNode(new AlignmentSpan.Standard(align), nodes);
        }

        public static Node drawableMargin(Drawable drawable, Object... nodes) {
            return new SpanNode(new DrawableMarginSpan(drawable), nodes);
        }

        public static Node drawableMargin(Drawable drawable, @Px Integer padding, Object... nodes) {
            return new SpanNode(new DrawableMarginSpan(drawable, padding), nodes);
        }

        public static Node iconMargin(Bitmap bitmap, Object... nodes) {
            return new SpanNode(new IconMarginSpan(bitmap), nodes);
        }

        public static Node iconMargin(Bitmap bitmap, @Px Integer padding, Object... nodes) {
            return new SpanNode(new IconMarginSpan(bitmap, padding), nodes);
        }

        public static Node image(Context context, Bitmap bitmap, Object... nodes) {
            return new SpanNode(new ImageSpan(context, bitmap), nodes);
        }

        public static Node image(Context context, Bitmap bitmap, Integer verticalAlignment, Object... nodes) {
            return new SpanNode(new ImageSpan(context, bitmap, verticalAlignment), nodes);
        }

        public static Node image(Drawable drawable, Object... nodes) {
            return new SpanNode(new ImageSpan(drawable), nodes);
        }

        public static Node image(Drawable drawable, Integer verticalAlignment, Object... nodes) {
            return new SpanNode(new ImageSpan(drawable, verticalAlignment), nodes);
        }

        public static Node image(Context context, Uri uri, Object... nodes) {
            return new SpanNode(new ImageSpan(context, uri), nodes);
        }

        public static Node image(Context context, Uri uri, Integer verticalAlignment, Object... nodes) {
            return new SpanNode(new ImageSpan(context, uri, verticalAlignment), nodes);
        }

        public static Node image(Context context, @DrawableRes Integer resourceId, Object... nodes) {
            return new SpanNode(new ImageSpan(context, resourceId), nodes);
        }

        public static Node image(Context context, @DrawableRes Integer resourceId, Integer verticalAlignment, Object... nodes) {
            return new SpanNode(new ImageSpan(context, resourceId, verticalAlignment), nodes);
        }

        public static ClickableSpanNode clickable(Object... nodes) {
            return new ClickableSpanNode(nodes);
        }

        static class ClickableSpanNode extends SpanNode {

            private static class InternalClickableSpan extends ClickableSpan {

                @Nullable
                private OnClickCallback callback;

                @Override
                public void onClick(View widget) {
                    if (callback != null) {
                        callback.onClick(this);
                    }
                }
            }

            interface OnClickCallback {
                void onClick(ClickableSpan span);
            }

            ClickableSpanNode(Object... nodes) {
                super(new InternalClickableSpan(), nodes);
            }

            public ClickableSpanNode onClick(OnClickCallback callback) {
                if (span instanceof InternalClickableSpan) {
                    ((InternalClickableSpan) span).callback = callback;
                }
                return this;
            }
        }


        public static UrlSpanNode url(String url, Object... nodes) {
            return new UrlSpanNode(url, nodes);
        }

        static class UrlSpanNode extends SpanNode {

            private static class InternalUrlSpan extends URLSpan {

                @Nullable
                private OnClickCallback callback;

                InternalUrlSpan(String url) {
                    super(url);
                }

                @Override
                public void onClick(View widget) {
                    if (callback == null || callback.onClick(this)) {
                        super.onClick(widget);
                    }
                }
            }

            interface OnClickCallback {
                boolean onClick(URLSpan span);
            }

            UrlSpanNode(String url, Object... nodes) {
                super(new InternalUrlSpan(url), nodes);
            }

            public UrlSpanNode onClick(OnClickCallback callback) {
                if (span instanceof InternalUrlSpan) {
                    ((InternalUrlSpan) span).callback = callback;
                }
                return this;
            }
        }

        static class SpanNode extends Node {

            @NonNull
            final protected Object span;

            SpanNode(@NonNull Object span, Object... nodes) {
                super(nodes);
                this.span = span;
            }

            android.text.Spannable build(SpannableStringBuilder builder) {
                int start = builder.length();
                super.build(builder);
                builder.setSpan(span, start, builder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                return builder;
            }
        }

        static class Leaf extends Span {

            @Nullable
            final private Object content;

            Leaf(@Nullable Object content) {
                super(null);
                this.content = content;
            }

            android.text.Spannable build(SpannableStringBuilder builder) {
                if (content != null) {
                    builder.append(content.toString());
                }
                return builder;
            }
        }

    }
}
