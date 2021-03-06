package com.oneplus.support.lifecycle;

import com.oneplus.support.lifecycle.Lifecycle.Event;

class FullLifecycleObserverAdapter implements GenericLifecycleObserver {
    private final FullLifecycleObserver mObserver;

    /* renamed from: com.oneplus.support.lifecycle.FullLifecycleObserverAdapter$1 */
    static /* synthetic */ class C20381 {
        static final /* synthetic */ int[] $SwitchMap$com$oneplus$support$lifecycle$Lifecycle$Event = new int[Event.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|(3:13|14|16)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|16) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0040 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x004b */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0035 */
        static {
            /*
                com.oneplus.support.lifecycle.Lifecycle$Event[] r0 = com.oneplus.support.lifecycle.Lifecycle.Event.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$oneplus$support$lifecycle$Lifecycle$Event = r0
                int[] r0 = $SwitchMap$com$oneplus$support$lifecycle$Lifecycle$Event     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.oneplus.support.lifecycle.Lifecycle$Event r1 = com.oneplus.support.lifecycle.Lifecycle.Event.ON_CREATE     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$oneplus$support$lifecycle$Lifecycle$Event     // Catch:{ NoSuchFieldError -> 0x001f }
                com.oneplus.support.lifecycle.Lifecycle$Event r1 = com.oneplus.support.lifecycle.Lifecycle.Event.ON_START     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$oneplus$support$lifecycle$Lifecycle$Event     // Catch:{ NoSuchFieldError -> 0x002a }
                com.oneplus.support.lifecycle.Lifecycle$Event r1 = com.oneplus.support.lifecycle.Lifecycle.Event.ON_RESUME     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$com$oneplus$support$lifecycle$Lifecycle$Event     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.oneplus.support.lifecycle.Lifecycle$Event r1 = com.oneplus.support.lifecycle.Lifecycle.Event.ON_PAUSE     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                int[] r0 = $SwitchMap$com$oneplus$support$lifecycle$Lifecycle$Event     // Catch:{ NoSuchFieldError -> 0x0040 }
                com.oneplus.support.lifecycle.Lifecycle$Event r1 = com.oneplus.support.lifecycle.Lifecycle.Event.ON_STOP     // Catch:{ NoSuchFieldError -> 0x0040 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0040 }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0040 }
            L_0x0040:
                int[] r0 = $SwitchMap$com$oneplus$support$lifecycle$Lifecycle$Event     // Catch:{ NoSuchFieldError -> 0x004b }
                com.oneplus.support.lifecycle.Lifecycle$Event r1 = com.oneplus.support.lifecycle.Lifecycle.Event.ON_DESTROY     // Catch:{ NoSuchFieldError -> 0x004b }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x004b }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x004b }
            L_0x004b:
                int[] r0 = $SwitchMap$com$oneplus$support$lifecycle$Lifecycle$Event     // Catch:{ NoSuchFieldError -> 0x0056 }
                com.oneplus.support.lifecycle.Lifecycle$Event r1 = com.oneplus.support.lifecycle.Lifecycle.Event.ON_ANY     // Catch:{ NoSuchFieldError -> 0x0056 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0056 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0056 }
            L_0x0056:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.oneplus.support.lifecycle.FullLifecycleObserverAdapter.C20381.<clinit>():void");
        }
    }

    FullLifecycleObserverAdapter(FullLifecycleObserver fullLifecycleObserver) {
        this.mObserver = fullLifecycleObserver;
    }

    public void onStateChanged(LifecycleOwner lifecycleOwner, Event event) {
        switch (C20381.$SwitchMap$com$oneplus$support$lifecycle$Lifecycle$Event[event.ordinal()]) {
            case 1:
                this.mObserver.onCreate(lifecycleOwner);
                return;
            case 2:
                this.mObserver.onStart(lifecycleOwner);
                return;
            case 3:
                this.mObserver.onResume(lifecycleOwner);
                return;
            case 4:
                this.mObserver.onPause(lifecycleOwner);
                return;
            case 5:
                this.mObserver.onStop(lifecycleOwner);
                return;
            case 6:
                this.mObserver.onDestroy(lifecycleOwner);
                return;
            case 7:
                throw new IllegalArgumentException("ON_ANY must not been send by anybody");
            default:
                return;
        }
    }
}
