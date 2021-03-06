package com.android.systemui.p007qs.tiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.SystemProperties;
import android.widget.Switch;
import com.airbnb.lottie.C0526R;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$string;
import com.android.systemui.p007qs.GlobalSetting;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import com.android.systemui.plugins.p006qs.QSTile.Icon;
import com.android.systemui.plugins.p006qs.QSTile.SlashState;

/* renamed from: com.android.systemui.qs.tiles.AirplaneModeTile */
public class AirplaneModeTile extends QSTileImpl<BooleanState> {
    private final ActivityStarter mActivityStarter;
    private final Icon mIcon = ResourceIcon.get(17302777);
    private boolean mListening;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.AIRPLANE_MODE".equals(intent.getAction())) {
                AirplaneModeTile.this.refreshState();
            }
        }
    };
    private final GlobalSetting mSetting;

    public int getMetricsCategory() {
        return C0526R.styleable.AppCompatTheme_toolbarNavigationButtonStyle;
    }

    public AirplaneModeTile(QSHost qSHost, ActivityStarter activityStarter) {
        super(qSHost);
        this.mActivityStarter = activityStarter;
        this.mSetting = new GlobalSetting(this.mContext, null, "airplane_mode_on") {
            /* access modifiers changed from: protected */
            public void handleValueChanged(int i) {
                AirplaneModeTile.this.handleRefreshState(Integer.valueOf(i));
            }
        };
    }

    public BooleanState newTileState() {
        BooleanState booleanState = new BooleanState();
        booleanState.lottiePrefix = "qs_airplane_tile";
        booleanState.lottieSupport = 63;
        return booleanState;
    }

    public void handleClick() {
        boolean z = ((BooleanState) this.mState).value;
        MetricsLogger.action(this.mContext, getMetricsCategory(), !z);
        if (z || !Boolean.parseBoolean(SystemProperties.get("ril.cdma.inecmmode"))) {
            setEnabled(!z);
        } else {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("com.android.internal.intent.action.ACTION_SHOW_NOTICE_ECM_BLOCK_OTHERS"), 0);
        }
    }

    private void setEnabled(boolean z) {
        ((ConnectivityManager) this.mContext.getSystemService("connectivity")).setAirplaneMode(z);
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.airplane_mode);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(BooleanState booleanState, Object obj) {
        checkIfRestrictionEnforcedByAdminOnly(booleanState, "no_airplane_mode");
        int i = 1;
        boolean z = (obj instanceof Integer ? ((Integer) obj).intValue() : this.mSetting.getValue()) != 0;
        booleanState.value = z;
        booleanState.label = this.mContext.getString(R$string.airplane_mode);
        booleanState.icon = this.mIcon;
        if (booleanState.slash == null) {
            booleanState.slash = new SlashState();
        }
        booleanState.slash.isSlashed = !z;
        if (z) {
            i = 2;
        }
        booleanState.state = i;
        booleanState.contentDescription = booleanState.label;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((BooleanState) this.mState).value) {
            return this.mContext.getString(R$string.accessibility_quick_settings_airplane_changed_on);
        }
        return this.mContext.getString(R$string.accessibility_quick_settings_airplane_changed_off);
    }

    public void handleSetListening(boolean z) {
        if (this.mListening != z) {
            this.mListening = z;
            if (z) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
                this.mContext.registerReceiver(this.mReceiver, intentFilter);
            } else {
                this.mContext.unregisterReceiver(this.mReceiver);
            }
            this.mSetting.setListening(z);
        }
    }
}
