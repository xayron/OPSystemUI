package com.oneplus.lib.widget.cardview;

import android.graphics.drawable.Drawable;

interface CardViewDelegate {
    Drawable getBackground();

    boolean getPreventCornerOverlap();

    boolean getUseCompatPadding();

    void setBackgroundDrawable(Drawable drawable);

    void setShadowPadding(int i, int i2, int i3, int i4);
}
