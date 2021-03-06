package androidx.mediarouter.app;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import androidx.appcompat.R$attr;
import androidx.appcompat.widget.AppCompatSeekBar;

class MediaRouteVolumeSlider extends AppCompatSeekBar {
    private int mBackgroundColor;
    private final float mDisabledAlpha;
    private boolean mHideThumb;
    private int mProgressAndThumbColor;
    private Drawable mThumb;

    public MediaRouteVolumeSlider(Context context) {
        this(context, null);
    }

    public MediaRouteVolumeSlider(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.seekBarStyle);
    }

    public MediaRouteVolumeSlider(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDisabledAlpha = MediaRouterThemeHelper.getDisabledAlpha(context);
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        int i = isEnabled() ? 255 : (int) (this.mDisabledAlpha * 255.0f);
        this.mThumb.setColorFilter(this.mProgressAndThumbColor, Mode.SRC_IN);
        this.mThumb.setAlpha(i);
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) getProgressDrawable();
            Drawable findDrawableByLayerId = layerDrawable.findDrawableByLayerId(16908301);
            layerDrawable.findDrawableByLayerId(16908288).setColorFilter(this.mBackgroundColor, Mode.SRC_IN);
            progressDrawable = findDrawableByLayerId;
        }
        progressDrawable.setColorFilter(this.mProgressAndThumbColor, Mode.SRC_IN);
        progressDrawable.setAlpha(i);
    }

    public void setThumb(Drawable drawable) {
        this.mThumb = drawable;
        super.setThumb(this.mHideThumb ? null : this.mThumb);
    }

    public void setHideThumb(boolean z) {
        if (this.mHideThumb != z) {
            this.mHideThumb = z;
            super.setThumb(this.mHideThumb ? null : this.mThumb);
        }
    }

    public void setColor(int i) {
        setColor(i, i);
    }

    public void setColor(int i, int i2) {
        String str = "MediaRouteVolumeSlider";
        if (this.mProgressAndThumbColor != i) {
            if (Color.alpha(i) != 255) {
                StringBuilder sb = new StringBuilder();
                sb.append("Volume slider progress and thumb color cannot be translucent: #");
                sb.append(Integer.toHexString(i));
                Log.e(str, sb.toString());
            }
            this.mProgressAndThumbColor = i;
        }
        if (this.mBackgroundColor != i2) {
            if (Color.alpha(i2) != 255) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Volume slider background color cannot be translucent: #");
                sb2.append(Integer.toHexString(i2));
                Log.e(str, sb2.toString());
            }
            this.mBackgroundColor = i2;
        }
    }
}
