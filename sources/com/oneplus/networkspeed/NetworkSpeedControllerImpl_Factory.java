package com.oneplus.networkspeed;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NetworkSpeedControllerImpl_Factory implements Factory<NetworkSpeedControllerImpl> {
    private final Provider<Context> contextProvider;

    public NetworkSpeedControllerImpl_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public NetworkSpeedControllerImpl get() {
        return provideInstance(this.contextProvider);
    }

    public static NetworkSpeedControllerImpl provideInstance(Provider<Context> provider) {
        return new NetworkSpeedControllerImpl((Context) provider.get());
    }

    public static NetworkSpeedControllerImpl_Factory create(Provider<Context> provider) {
        return new NetworkSpeedControllerImpl_Factory(provider);
    }
}
