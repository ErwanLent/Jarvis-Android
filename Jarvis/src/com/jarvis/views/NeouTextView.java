package com.jarvis.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.jarvis.utility.FontCache;

public class NeouTextView extends TextView {
	 
    public NeouTextView(Context context) {
        super(context);
    }
 
    public NeouTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public NeouTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    @Override
    public void setTypeface(Typeface tf, int style) {
        if (!this.isInEditMode()) {
            if (style == Typeface.NORMAL) {
                super.setTypeface(FontCache.getFont(getContext(), "fonts/Neou-Thin.ttf"));
            } else if (style == Typeface.ITALIC) {
                super.setTypeface(FontCache.getFont(getContext(), "fonts/Neou-Thin.ttf"));
            } else if (style == Typeface.BOLD) {
                super.setTypeface(FontCache.getFont(getContext(), "fonts/Neou-Thin.ttf"));
            }
        }
    }
}