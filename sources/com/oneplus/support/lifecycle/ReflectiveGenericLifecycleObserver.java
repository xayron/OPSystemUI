package com.oneplus.support.lifecycle;

import com.oneplus.support.lifecycle.Lifecycle.Event;

class ReflectiveGenericLifecycleObserver implements GenericLifecycleObserver {
    private final CallbackInfo mInfo = ClassesInfoCache.sInstance.getInfo(this.mWrapped.getClass());
    private final Object mWrapped;

    ReflectiveGenericLifecycleObserver(Object obj) {
        this.mWrapped = obj;
    }

    public void onStateChanged(LifecycleOwner lifecycleOwner, Event event) {
        this.mInfo.invokeCallbacks(lifecycleOwner, event, this.mWrapped);
    }
}
