package com.android.systemui.statusbar.notification;

import android.util.Pools.SimplePool;

public class ProgressTransformState extends TransformState {
    private static SimplePool<ProgressTransformState> sInstancePool = new SimplePool<>(40);

    /* access modifiers changed from: protected */
    public boolean sameAs(TransformState transformState) {
        if (transformState instanceof ProgressTransformState) {
            return true;
        }
        return super.sameAs(transformState);
    }

    public static ProgressTransformState obtain() {
        ProgressTransformState progressTransformState = (ProgressTransformState) sInstancePool.acquire();
        if (progressTransformState != null) {
            return progressTransformState;
        }
        return new ProgressTransformState();
    }

    public void recycle() {
        super.recycle();
        sInstancePool.release(this);
    }
}
