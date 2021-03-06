package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;
import android.util.Log;
import com.android.systemui.ConfigurationChangedReceiver;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import com.oneplus.util.OpUtils;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ConfigurationControllerImpl.kt */
public final class ConfigurationControllerImpl implements ConfigurationController, ConfigurationChangedReceiver {
    private final Context context;
    private int density;
    private float fontScale;
    private final boolean inCarMode;
    private final Configuration lastConfig = new Configuration();
    private final List<ConfigurationListener> listeners = new ArrayList();
    private LocaleList localeList;
    private int mSmallestScreenWidthDp;
    private int uiMode;

    public ConfigurationControllerImpl(Context context2) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        Resources resources = context2.getResources();
        Intrinsics.checkExpressionValueIsNotNull(resources, "context.resources");
        Configuration configuration = resources.getConfiguration();
        this.context = context2;
        this.fontScale = configuration.fontScale;
        this.density = configuration.densityDpi;
        this.inCarMode = (configuration.uiMode & 15) == 3;
        this.uiMode = configuration.uiMode & 48;
        Intrinsics.checkExpressionValueIsNotNull(configuration, "currentConfig");
        this.localeList = configuration.getLocales();
        this.mSmallestScreenWidthDp = configuration.smallestScreenWidthDp;
    }

    public void notifyThemeChanged() {
        for (ConfigurationListener configurationListener : new ArrayList(this.listeners)) {
            if (this.listeners.contains(configurationListener)) {
                configurationListener.onThemeChanged();
            }
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        Intrinsics.checkParameterIsNotNull(configuration, "newConfig");
        ArrayList<ConfigurationListener> arrayList = new ArrayList<>(this.listeners);
        for (ConfigurationListener configurationListener : arrayList) {
            if (this.listeners.contains(configurationListener)) {
                configurationListener.onConfigChanged(configuration);
            }
        }
        float f = configuration.fontScale;
        int i = configuration.densityDpi;
        int i2 = configuration.uiMode & 48;
        boolean z = i2 != this.uiMode;
        StringBuilder sb = new StringBuilder();
        sb.append("onConfigurationChanged, oldUiMode: ");
        sb.append(this.uiMode);
        sb.append(", newUiMode: ");
        sb.append(i2);
        String str = "ConfigurationController";
        Log.d(str, sb.toString());
        if (!(i == this.density && f == this.fontScale && ((!this.inCarMode || !z) && this.mSmallestScreenWidthDp == configuration.smallestScreenWidthDp))) {
            OpUtils.updateDensityDpi(i);
            for (ConfigurationListener configurationListener2 : arrayList) {
                if (this.listeners.contains(configurationListener2)) {
                    configurationListener2.onDensityOrFontScaleChanged();
                }
            }
            this.density = i;
            this.fontScale = f;
            this.mSmallestScreenWidthDp = configuration.smallestScreenWidthDp;
        }
        LocaleList locales = configuration.getLocales();
        if (!Intrinsics.areEqual(locales, this.localeList)) {
            this.localeList = locales;
            for (ConfigurationListener configurationListener3 : arrayList) {
                if (this.listeners.contains(configurationListener3)) {
                    configurationListener3.onLocaleListChanged();
                }
            }
        }
        if (z) {
            Log.d(str, "onConfigurationChanged, trigger onUiModeChanged for listeners");
            this.context.getTheme().applyStyle(this.context.getThemeResId(), true);
            this.uiMode = i2;
            for (ConfigurationListener configurationListener4 : arrayList) {
                if (this.listeners.contains(configurationListener4)) {
                    configurationListener4.onUiModeChanged();
                }
            }
        }
        if ((this.lastConfig.updateFrom(configuration) & Integer.MIN_VALUE) != 0) {
            for (ConfigurationListener configurationListener5 : arrayList) {
                if (this.listeners.contains(configurationListener5)) {
                    configurationListener5.onOverlayChanged();
                }
            }
        }
    }

    public void addCallback(ConfigurationListener configurationListener) {
        Intrinsics.checkParameterIsNotNull(configurationListener, "listener");
        this.listeners.add(configurationListener);
        configurationListener.onDensityOrFontScaleChanged();
    }

    public void removeCallback(ConfigurationListener configurationListener) {
        Intrinsics.checkParameterIsNotNull(configurationListener, "listener");
        this.listeners.remove(configurationListener);
    }
}
