package com.android.systemui;

import android.content.Context;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.InternalInsetsInfo;
import android.view.ViewTreeObserver.OnComputeInternalInsetsListener;
import android.widget.FrameLayout;

public class RegionInterceptingFrameLayout extends FrameLayout {
    private final OnComputeInternalInsetsListener mInsetsListener = new OnComputeInternalInsetsListener() {
        public final void onComputeInternalInsets(InternalInsetsInfo internalInsetsInfo) {
            RegionInterceptingFrameLayout.this.lambda$new$0$RegionInterceptingFrameLayout(internalInsetsInfo);
        }
    };

    public interface RegionInterceptableView {
        Region getInterceptRegion();

        boolean shouldInterceptTouch() {
            return false;
        }
    }

    public RegionInterceptingFrameLayout(Context context) {
        super(context);
    }

    public RegionInterceptingFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public RegionInterceptingFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public RegionInterceptingFrameLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnComputeInternalInsetsListener(this.mInsetsListener);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mInsetsListener);
    }

    public /* synthetic */ void lambda$new$0$RegionInterceptingFrameLayout(InternalInsetsInfo internalInsetsInfo) {
        internalInsetsInfo.setTouchableInsets(3);
        internalInsetsInfo.touchableRegion.setEmpty();
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof RegionInterceptableView) {
                RegionInterceptableView regionInterceptableView = (RegionInterceptableView) childAt;
                if (regionInterceptableView.shouldInterceptTouch()) {
                    Region interceptRegion = regionInterceptableView.getInterceptRegion();
                    if (interceptRegion != null) {
                        internalInsetsInfo.touchableRegion.op(interceptRegion, Op.UNION);
                    }
                }
            }
        }
    }
}
