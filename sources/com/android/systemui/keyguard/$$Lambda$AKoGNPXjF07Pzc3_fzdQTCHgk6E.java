package com.android.systemui.keyguard;

import com.android.systemui.keyguard.WakefulnessLifecycle.Observer;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.keyguard.-$$Lambda$AKoGNPXjF07Pzc3_fzdQTCHgk6E reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AKoGNPXjF07Pzc3_fzdQTCHgk6E implements Consumer {
    public static final /* synthetic */ $$Lambda$AKoGNPXjF07Pzc3_fzdQTCHgk6E INSTANCE = new $$Lambda$AKoGNPXjF07Pzc3_fzdQTCHgk6E();

    private /* synthetic */ $$Lambda$AKoGNPXjF07Pzc3_fzdQTCHgk6E() {
    }

    public final void accept(Object obj) {
        ((Observer) obj).onFinishedGoingToSleep();
    }
}
