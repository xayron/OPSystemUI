package com.android.systemui.p007qs.tiles;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import com.airbnb.lottie.C0526R;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.Utils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.graph.BluetoothDeviceLayerDrawable;
import com.android.systemui.R$drawable;
import com.android.systemui.R$plurals;
import com.android.systemui.R$string;
import com.android.systemui.p007qs.QSDetailItems;
import com.android.systemui.p007qs.QSDetailItems.Item;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.p006qs.DetailAdapter;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import com.android.systemui.plugins.p006qs.QSTile.Icon;
import com.android.systemui.plugins.p006qs.QSTile.SlashState;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.statusbar.policy.BluetoothController.Callback;
import com.oneplus.util.OpUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* renamed from: com.android.systemui.qs.tiles.BluetoothTile */
public class BluetoothTile extends QSTileImpl<BooleanState> {
    /* access modifiers changed from: private */
    public static final Intent BLUETOOTH_SETTINGS = new Intent("android.settings.BLUETOOTH_SETTINGS");
    private final ActivityStarter mActivityStarter;
    private final Callback mCallback = new Callback() {
        public void onBluetoothStateChange(boolean z) {
            BluetoothTile.this.refreshState();
            if (BluetoothTile.this.isShowingDetail()) {
                BluetoothTile.this.mDetailAdapter.updateItems();
                BluetoothTile bluetoothTile = BluetoothTile.this;
                bluetoothTile.fireToggleStateChanged(bluetoothTile.mDetailAdapter.getToggleState().booleanValue());
            }
        }

        public void onBluetoothDevicesChanged() {
            BluetoothTile.this.refreshState();
            if (BluetoothTile.this.isShowingDetail()) {
                BluetoothTile.this.mDetailAdapter.updateItems();
            }
        }
    };
    /* access modifiers changed from: private */
    public final BluetoothController mController;
    /* access modifiers changed from: private */
    public final BluetoothDetailAdapter mDetailAdapter;

    /* renamed from: com.android.systemui.qs.tiles.BluetoothTile$BluetoothBatteryTileIcon */
    private class BluetoothBatteryTileIcon extends Icon {
        private int mBatteryLevel;
        private float mIconScale;

        BluetoothBatteryTileIcon(int i, float f) {
            this.mBatteryLevel = i;
            this.mIconScale = f;
        }

        public Drawable getDrawable(Context context) {
            return BluetoothDeviceLayerDrawable.createLayerDrawable(context, R$drawable.ic_bluetooth_connected, this.mBatteryLevel, this.mIconScale);
        }
    }

    /* renamed from: com.android.systemui.qs.tiles.BluetoothTile$BluetoothConnectedTileIcon */
    private class BluetoothConnectedTileIcon extends Icon {
        BluetoothConnectedTileIcon() {
        }

        public Drawable getDrawable(Context context) {
            return context.getDrawable(R$drawable.ic_bluetooth_connected);
        }
    }

    /* renamed from: com.android.systemui.qs.tiles.BluetoothTile$BluetoothDetailAdapter */
    protected class BluetoothDetailAdapter implements DetailAdapter, QSDetailItems.Callback {
        private QSDetailItems mItems;

        public int getMetricsCategory() {
            return 150;
        }

        protected BluetoothDetailAdapter() {
        }

        public CharSequence getTitle() {
            return BluetoothTile.this.mContext.getString(R$string.quick_settings_bluetooth_label);
        }

        public Boolean getToggleState() {
            return Boolean.valueOf(((BooleanState) BluetoothTile.this.mState).value);
        }

        public boolean getToggleEnabled() {
            return BluetoothTile.this.mController.getBluetoothState() == 10 || BluetoothTile.this.mController.getBluetoothState() == 12;
        }

        public Intent getSettingsIntent() {
            return BluetoothTile.BLUETOOTH_SETTINGS;
        }

        public void setToggleState(boolean z) {
            MetricsLogger.action(BluetoothTile.this.mContext, 154, z);
            BluetoothTile.this.mController.setBluetoothEnabled(z);
        }

        public View createDetailView(Context context, View view, ViewGroup viewGroup) {
            this.mItems = QSDetailItems.convertOrInflate(context, view, viewGroup);
            this.mItems.setTagSuffix("Bluetooth");
            this.mItems.setCallback(this);
            updateItems();
            setItemsVisible(((BooleanState) BluetoothTile.this.mState).value);
            return this.mItems;
        }

        public void setItemsVisible(boolean z) {
            QSDetailItems qSDetailItems = this.mItems;
            if (qSDetailItems != null) {
                qSDetailItems.setItemsVisible(z);
            }
        }

        /* access modifiers changed from: private */
        public void updateItems() {
            if (this.mItems != null) {
                if (BluetoothTile.this.mController.isBluetoothEnabled()) {
                    this.mItems.setEmptyState(R$drawable.ic_qs_bluetooth_detail_empty, R$string.quick_settings_bluetooth_detail_empty_text);
                } else {
                    this.mItems.setEmptyState(R$drawable.ic_qs_bluetooth_detail_empty, R$string.bt_is_off);
                }
                ArrayList arrayList = new ArrayList();
                Collection<CachedBluetoothDevice> devices = BluetoothTile.this.mController.getDevices();
                if (devices != null) {
                    int i = 0;
                    int i2 = 0;
                    for (CachedBluetoothDevice cachedBluetoothDevice : devices) {
                        if (BluetoothTile.this.mController.getBondState(cachedBluetoothDevice) != 10) {
                            Item item = new Item();
                            item.iconResId = R$drawable.op_ic_qs_bluetooth;
                            item.line1 = cachedBluetoothDevice.getName();
                            item.tag = cachedBluetoothDevice;
                            int maxConnectionState = cachedBluetoothDevice.getMaxConnectionState();
                            if (maxConnectionState == 2) {
                                item.iconResId = R$drawable.ic_bluetooth_connected;
                                int batteryLevel = cachedBluetoothDevice.getBatteryLevel();
                                if (batteryLevel != -1) {
                                    item.icon = new BluetoothBatteryTileIcon(batteryLevel, 1.0f);
                                    item.line2 = BluetoothTile.this.mContext.getString(R$string.quick_settings_connected_battery_level, new Object[]{Utils.formatPercentage(batteryLevel)});
                                } else {
                                    item.line2 = BluetoothTile.this.mContext.getString(R$string.quick_settings_connected);
                                }
                                item.canDisconnect = true;
                                arrayList.add(i, item);
                                i++;
                            } else if (maxConnectionState == 1) {
                                item.iconResId = R$drawable.ic_qs_bluetooth_connecting;
                                item.line2 = BluetoothTile.this.mContext.getString(R$string.quick_settings_connecting);
                                arrayList.add(i, item);
                            } else {
                                arrayList.add(item);
                            }
                            i2++;
                            if (i2 == 20) {
                                break;
                            }
                        }
                    }
                }
                this.mItems.setItems((Item[]) arrayList.toArray(new Item[arrayList.size()]));
            }
        }

        public void onDetailItemClick(Item item) {
            if (item != null) {
                Object obj = item.tag;
                if (obj != null) {
                    CachedBluetoothDevice cachedBluetoothDevice = (CachedBluetoothDevice) obj;
                    if (cachedBluetoothDevice != null && cachedBluetoothDevice.getMaxConnectionState() == 0) {
                        BluetoothTile.this.mController.connect(cachedBluetoothDevice);
                    }
                }
            }
        }

        public void onDetailItemDisconnect(Item item) {
            if (item != null) {
                Object obj = item.tag;
                if (obj != null) {
                    CachedBluetoothDevice cachedBluetoothDevice = (CachedBluetoothDevice) obj;
                    if (cachedBluetoothDevice != null) {
                        BluetoothTile.this.mController.disconnect(cachedBluetoothDevice);
                    }
                }
            }
        }
    }

    public int getMetricsCategory() {
        return C0526R.styleable.AppCompatTheme_toolbarStyle;
    }

    public void handleSetListening(boolean z) {
    }

    public BluetoothTile(QSHost qSHost, BluetoothController bluetoothController, ActivityStarter activityStarter) {
        super(qSHost);
        this.mController = bluetoothController;
        this.mActivityStarter = activityStarter;
        this.mDetailAdapter = (BluetoothDetailAdapter) createDetailAdapter();
        this.mController.observe(getLifecycle(), this.mCallback);
    }

    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }

    public BooleanState newTileState() {
        return new BooleanState();
    }

    /* access modifiers changed from: protected */
    public void handleClick() {
        Object obj;
        if (this.mController.isBluetoothTransientEnabling()) {
            Log.d(this.TAG, "transient enabling. skip.");
            return;
        }
        boolean z = ((BooleanState) this.mState).value;
        if (z) {
            obj = null;
        } else {
            obj = QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        }
        refreshState(obj);
        if (!z) {
            this.mController.setBluetoothTransientEnabling();
        }
        if (OpUtils.DEBUG_ONEPLUS) {
            String str = this.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("handleClick, !isEnabled: ");
            sb.append(!z);
            Log.i(str, sb.toString());
        }
        this.mController.setBluetoothEnabled(!z);
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.BLUETOOTH_SETTINGS");
    }

    /* access modifiers changed from: protected */
    public void handleSecondaryClick() {
        if (!this.mController.canConfigBluetooth()) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.settings.BLUETOOTH_SETTINGS"), 0);
            return;
        }
        showDetail(true);
        if (!((BooleanState) this.mState).value) {
            this.mController.setBluetoothEnabled(true);
        }
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.quick_settings_bluetooth_label);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(BooleanState booleanState, Object obj) {
        boolean z = obj == QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING || this.mController.isBluetoothConnecting() || this.mController.isBluetoothTransientEnabling();
        boolean z2 = z || this.mController.isBluetoothEnabled();
        boolean isBluetoothConnected = this.mController.isBluetoothConnected();
        boolean isBluetoothConnecting = this.mController.isBluetoothConnecting();
        booleanState.isTransient = z || isBluetoothConnecting || this.mController.getBluetoothState() == 11;
        booleanState.dualTarget = true;
        booleanState.value = z2;
        if (booleanState.slash == null) {
            booleanState.slash = new SlashState();
        }
        booleanState.slash.isSlashed = !z2;
        booleanState.label = this.mContext.getString(R$string.quick_settings_bluetooth_label);
        booleanState.secondaryLabel = TextUtils.emptyIfNull(getSecondaryLabel(z2, isBluetoothConnecting, isBluetoothConnected, booleanState.isTransient));
        if (z2) {
            if (isBluetoothConnected) {
                booleanState.icon = new BluetoothConnectedTileIcon();
                if (!TextUtils.isEmpty(this.mController.getConnectedDeviceName())) {
                    booleanState.label = this.mController.getConnectedDeviceName();
                }
                StringBuilder sb = new StringBuilder();
                sb.append(this.mContext.getString(R$string.accessibility_bluetooth_name, new Object[]{booleanState.label}));
                sb.append(", ");
                sb.append(booleanState.secondaryLabel);
                booleanState.contentDescription = sb.toString();
            } else if (booleanState.isTransient) {
                booleanState.icon = ResourceIcon.get(17302303);
                booleanState.contentDescription = booleanState.secondaryLabel;
            } else {
                booleanState.icon = ResourceIcon.get(R$drawable.op_ic_qs_bluetooth);
                StringBuilder sb2 = new StringBuilder();
                sb2.append(this.mContext.getString(R$string.accessibility_quick_settings_bluetooth));
                sb2.append(",");
                sb2.append(this.mContext.getString(R$string.accessibility_not_connected));
                booleanState.contentDescription = sb2.toString();
            }
            booleanState.state = 2;
        } else {
            booleanState.icon = ResourceIcon.get(R$drawable.op_ic_qs_bluetooth);
            booleanState.contentDescription = this.mContext.getString(R$string.accessibility_quick_settings_bluetooth);
            booleanState.state = 1;
        }
        booleanState.dualLabelContentDescription = this.mContext.getResources().getString(R$string.accessibility_quick_settings_open_settings, new Object[]{getTileLabel()});
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        if (OpUtils.DEBUG_ONEPLUS) {
            String str = this.TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Bt handleUpdateState end, state.state");
            sb3.append(booleanState.state);
            sb3.append(", enabled:");
            sb3.append(z2);
            sb3.append(", connected:");
            sb3.append(isBluetoothConnected);
            sb3.append(", connecting:");
            sb3.append(isBluetoothConnecting);
            sb3.append(", transientEnabling:");
            sb3.append(z);
            sb3.append(", bluetoothState: ");
            sb3.append(this.mController.getBluetoothState());
            Log.i(str, sb3.toString());
        }
    }

    private String getSecondaryLabel(boolean z, boolean z2, boolean z3, boolean z4) {
        if (z2) {
            return this.mContext.getString(R$string.quick_settings_connecting);
        }
        if (z4) {
            return this.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_transient);
        }
        List connectedDevices = this.mController.getConnectedDevices();
        if (z && z3 && !connectedDevices.isEmpty()) {
            if (connectedDevices.size() > 1) {
                return this.mContext.getResources().getQuantityString(R$plurals.quick_settings_hotspot_secondary_label_num_devices, connectedDevices.size(), new Object[]{Integer.valueOf(connectedDevices.size())});
            }
            CachedBluetoothDevice cachedBluetoothDevice = (CachedBluetoothDevice) connectedDevices.get(0);
            int batteryLevel = cachedBluetoothDevice.getBatteryLevel();
            if (batteryLevel != -1) {
                return this.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_battery_level, new Object[]{Utils.formatPercentage(batteryLevel)});
            }
            BluetoothClass btClass = cachedBluetoothDevice.getBtClass();
            if (btClass != null) {
                if (cachedBluetoothDevice.isHearingAidDevice()) {
                    return this.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_hearing_aids);
                }
                if (btClass.doesClassMatch(1)) {
                    return this.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_audio);
                }
                if (btClass.doesClassMatch(0)) {
                    return this.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_headset);
                }
                if (btClass.doesClassMatch(3)) {
                    return this.mContext.getString(R$string.quick_settings_bluetooth_secondary_label_input);
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((BooleanState) this.mState).value) {
            return this.mContext.getString(R$string.accessibility_quick_settings_bluetooth_changed_on);
        }
        return this.mContext.getString(R$string.accessibility_quick_settings_bluetooth_changed_off);
    }

    public boolean isAvailable() {
        return this.mController.isBluetoothSupported();
    }

    /* access modifiers changed from: protected */
    public DetailAdapter createDetailAdapter() {
        return new BluetoothDetailAdapter();
    }
}
