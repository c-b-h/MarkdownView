package se.ingenuity.markdownview.util.span;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextPaint;
import android.text.style.LineHeightSpan;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import se.ingenuity.markdownview.R;

/**
 * Sets the text appearance using the given
 * {@link android.R.styleable#TextAppearance TextAppearance} attributes.
 * By default {@link android.text.style.TextAppearanceSpan} only changes the specified attributes in XML.
 * {@link android.R.styleable#TextAppearance_textColorHighlight textColorHighlight},
 * {@link android.R.styleable#TextAppearance_textColorHint textColorHint},
 * {@link android.R.styleable#TextAppearance_textAllCaps textAllCaps} and
 * {@link android.R.styleable#TextAppearance_fallbackLineSpacing fallbackLineSpacing}
 * are not supported by {@link android.text.style.TextAppearanceSpan}.
 * <p>
 * {@see android.widget.TextView#setTextAppearance(int)}
 *
 * @attr ref android.R.styleable#TextAppearance_fontFamily
 * @attr ref android.R.styleable#TextAppearance_textColor
 * @attr ref android.R.styleable#TextAppearance_textColorLink
 * @attr ref android.R.styleable#TextAppearance_textFontWeight
 * @attr ref android.R.styleable#TextAppearance_textSize
 * @attr ref android.R.styleable#TextAppearance_textStyle
 * @attr ref android.R.styleable#TextAppearance_typeface
 * @attr ref android.R.styleable#TextAppearance_shadowColor
 * @attr ref android.R.styleable#TextAppearance_shadowDx
 * @attr ref android.R.styleable#TextAppearance_shadowDy
 * @attr ref android.R.styleable#TextAppearance_shadowRadius
 * @attr ref android.R.styleable#TextAppearance_elegantTextHeight
 * @attr ref android.R.styleable#TextAppearance_letterSpacing
 * @attr ref android.R.styleable#TextAppearance_fontFeatureSettings
 * @attr ref android.R.styleable#TextAppearance_fontVariationSettings
 */
public class TextAppearanceSpanCompat extends MetricAffectingSpan implements LineHeightSpan {
    private static final int FONT_WEIGHT_MAX = 1000;

    private final String mFamilyName;
    private final int mStyle;
    private final int mTextSize;
    private final ColorStateList mTextColor;
    private final ColorStateList mTextColorLink;
    private final Typeface mTypeface;

    private final int mTextFontWeight;
    private final LocaleList mTextLocales;

    private final float mShadowRadius;
    private final float mShadowDx;
    private final float mShadowDy;
    private final int mShadowColor;

    private final boolean mHasElegantTextHeight = false;
    private final boolean mElegantTextHeight = false;
    private final boolean mHasLetterSpacing;
    private final float mLetterSpacing;

    private final int mLineHeight;

    private final String mFontFeatureSettings = null;
    private final String mFontVariationSettings;

    /**
     * Uses the specified TextAppearance resource to determine the
     * text appearance.  The <code>appearance</code> should be, for example,
     * <code>android.R.style.TextAppearance_Small</code>.
     */
    public TextAppearanceSpanCompat(Context context, int appearance) {
        this(context, appearance, -1);
    }

    /**
     * Uses the specified TextAppearance resource to determine the
     * text appearance, and the specified text color resource
     * to determine the color.  The <code>appearance</code> should be,
     * for example, <code>android.R.style.TextAppearance_Small</code>,
     * and the <code>colorList</code> should be, for example,
     * <code>android.R.styleable.Theme_textColorPrimary</code>.
     */
    public TextAppearanceSpanCompat(Context context, int appearance, int colorList) {
        ColorStateList textColor;

        TypedArray a = context.obtainStyledAttributes(appearance, R.styleable.TextAppearance);
        final int fontFamilyId = a.hasValue(R.styleable.TextAppearance_fontFamily)
                ? R.styleable.TextAppearance_fontFamily
                : R.styleable.TextAppearance_android_fontFamily;

        textColor = a.getColorStateList(R.styleable.TextAppearance_android_textColor);
        mTextColorLink = a.getColorStateList(R.styleable.TextAppearance_android_textColorLink);
        mTextSize = a.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, -1);

        mStyle = a.getInt(R.styleable.TextAppearance_android_textStyle, Typeface.NORMAL);

        @FontRes final int fontRes = a.getResourceId(fontFamilyId, -1);
        if (fontRes != -1) {
            mTypeface = ResourcesCompat.getFont(context, fontRes);
        } else {
            mTypeface = null;
        }

        if (mTypeface != null) {
            mFamilyName = null;
        } else {
            String family = a.getString(fontFamilyId);
            if (family != null) {
                mFamilyName = family;
            } else {
                final int tf = a.getInt(R.styleable.TextAppearance_android_typeface, 0);

                switch (tf) {
                    case 1:
                        mFamilyName = "sans";
                        break;

                    case 2:
                        mFamilyName = "serif";
                        break;

                    case 3:
                        mFamilyName = "monospace";
                        break;

                    default:
                        mFamilyName = null;
                        break;
                }
            }
        }

        mTextFontWeight = a.getInt(R.styleable.TextAppearance_android_textFontWeight, -1);

        final String localeString = a.getString(R.styleable.TextAppearance_textLocale);
        if (localeString != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = LocaleList.forLanguageTags(localeString);
            if (!localeList.isEmpty()) {
                mTextLocales = localeList;
            } else {
                mTextLocales = null;
            }
        } else {
            mTextLocales = null;
        }

        mShadowRadius = a.getFloat(R.styleable.TextAppearance_android_shadowRadius, 0.0f);
        mShadowDx = a.getFloat(R.styleable.TextAppearance_android_shadowDx, 0.0f);
        mShadowDy = a.getFloat(R.styleable.TextAppearance_android_shadowDy, 0.0f);
        mShadowColor = a.getInt(R.styleable.TextAppearance_android_shadowColor, 0);

//        mHasElegantTextHeight = a.hasValue(R.styleable
//                .TextAppearance_elegantTextHeight);
//        mElegantTextHeight = a.getBoolean(R.styleable
//                .TextAppearance_elegantTextHeight, false);
        mFontVariationSettings = a.getString(R.styleable.TextAppearance_fontVariationSettings);
        a.recycle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            a = context.obtainStyledAttributes(appearance, R.styleable.MaterialTextAppearance);
            mHasLetterSpacing = a.hasValue(R.styleable.MaterialTextAppearance_android_letterSpacing);
            mLetterSpacing = a.getFloat(R.styleable.MaterialTextAppearance_android_letterSpacing, 0);
            a.recycle();
        } else {
            mHasLetterSpacing = false;
            mLetterSpacing = 0;
        }

        a = context.obtainStyledAttributes(appearance, R.styleable.MaterialTextAppearance);
        final int lineHeightId = a.hasValue(R.styleable.MaterialTextAppearance_lineHeight)
                ? R.styleable.MaterialTextAppearance_lineHeight
                : R.styleable.MaterialTextAppearance_android_lineHeight;
        mLineHeight = a.getDimensionPixelSize(lineHeightId, -1);

        a.recycle();

//        if (colorList >= 0) {
//            a = context.obtainStyledAttributes(R.style.Theme, R.styleable.Theme);
//
//            textColor = a.getColorStateList(colorList);
//            a.recycle();
//        }

        mTextColor = textColor;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        updateMeasureState(ds);

        if (mTextColor != null) {
            ds.setColor(mTextColor.getColorForState(ds.drawableState, 0));
        }

        if (mTextColorLink != null) {
            ds.linkColor = mTextColorLink.getColorForState(ds.drawableState, 0);
        }

        if (mShadowColor != 0) {
            ds.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mShadowColor);
        }
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint ds) {
        final Typeface currentTypeface = ds.getTypeface();

        final int style;
        if (currentTypeface != null) {
            style = currentTypeface.getStyle() | mStyle;
        } else {
            style = mStyle;
        }

        final Typeface styledTypeface;
        if (mTypeface != null) {
            styledTypeface = Typeface.create(mTypeface, style);
        } else if (mFamilyName != null || style != Typeface.NORMAL) {
            if (mFamilyName != null) {
                styledTypeface = Typeface.create(mFamilyName, style);
            } else if (currentTypeface == null) {
                styledTypeface = Typeface.defaultFromStyle(style);
            } else {
                styledTypeface = Typeface.create(currentTypeface, style);
            }
        } else {
            styledTypeface = null;
        }

        if (styledTypeface != null) {
            final Typeface readyTypeface;
            if (mTextFontWeight >= 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                final int weight = Math.min(FONT_WEIGHT_MAX, mTextFontWeight);
                final boolean italic = (style & Typeface.ITALIC) != 0;
                readyTypeface = ds.setTypeface(Typeface.create(styledTypeface, weight, italic));
            } else {
                readyTypeface = styledTypeface;
            }

            int fake = style & ~readyTypeface.getStyle();

            if ((fake & Typeface.BOLD) != 0) {
                ds.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                ds.setTextSkewX(-0.25f);
            }

            ds.setTypeface(readyTypeface);
        }

        if (mTextSize > 0) {
            ds.setTextSize(mTextSize);
        }

        if (mTextLocales != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ds.setTextLocales(mTextLocales);
            }
        }

        if (mHasElegantTextHeight) {
            ds.setElegantTextHeight(mElegantTextHeight);
        }

        if (mHasLetterSpacing) {
            ds.setLetterSpacing(mLetterSpacing);
        }

        if (mFontFeatureSettings != null) {
            ds.setFontFeatureSettings(mFontFeatureSettings);
        }

        if (mFontVariationSettings != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ds.setFontVariationSettings(mFontVariationSettings);
            }
        }
    }

    @Override
    public void chooseHeight(
            CharSequence text,
            int start,
            int end,
            int spanstartv,
            int lineHeight,
            Paint.FontMetricsInt fm
    ) {
        final int originHeight = fm.descent - fm.ascent;
        // If original height is negative, do nothing.
        if (originHeight <= 0 || mLineHeight < 0) {
            return;
        }
        final float ratio = mLineHeight * 1.0f / originHeight;
        fm.descent = Math.round(fm.descent * ratio);
        fm.ascent = fm.descent - mLineHeight;
    }
}
