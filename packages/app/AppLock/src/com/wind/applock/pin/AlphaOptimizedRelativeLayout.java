package com.wind.applock.pin;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * A frame layout which does not have overlapping renderings commands and therefore does not need a
 * layer when alpha is changed.
 */
public class AlphaOptimizedRelativeLayout extends RelativeLayout {

    public AlphaOptimizedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
}
