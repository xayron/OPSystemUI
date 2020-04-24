package com.android.settingslib.wifi;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Keep;
import java.util.ArrayList;

@Keep
public class TestAccessPointBuilder {
    private static final int MAX_RSSI = -55;
    private static final int MIN_RSSI = -100;
    private String mBssid = null;
    private String mCarrierName = null;
    Context mContext;
    private String mFqdn = null;
    private boolean mIsCarrierAp = false;
    private int mNetworkId = -1;
    private NetworkInfo mNetworkInfo = null;
    private String mProviderFriendlyName = null;
    private int mRssi = Integer.MIN_VALUE;
    private ArrayList<ScanResult> mScanResults;
    private ArrayList<TimestampedScoredNetwork> mScoredNetworkCache;
    private int mSecurity = 0;
    private int mSpeed = 0;
    private WifiConfiguration mWifiConfig;
    private WifiInfo mWifiInfo;
    private String ssid = "TestSsid";

    @Keep
    public TestAccessPointBuilder(Context context) {
        this.mContext = context;
    }

    @Keep
    public AccessPoint build() {
        WifiConfiguration wifiConfiguration;
        Bundle bundle = new Bundle();
        if (this.mNetworkId != -1) {
            wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.networkId = this.mNetworkId;
            wifiConfiguration.BSSID = this.mBssid;
        } else {
            wifiConfiguration = null;
        }
        bundle.putString("key_ssid", this.ssid);
        bundle.putParcelable("key_config", wifiConfiguration);
        bundle.putParcelable("key_networkinfo", this.mNetworkInfo);
        bundle.putParcelable("key_wifiinfo", this.mWifiInfo);
        String str = this.mFqdn;
        if (str != null) {
            bundle.putString("key_fqdn", str);
        }
        String str2 = this.mProviderFriendlyName;
        if (str2 != null) {
            bundle.putString("key_provider_friendly_name", str2);
        }
        ArrayList<ScanResult> arrayList = this.mScanResults;
        if (arrayList != null) {
            bundle.putParcelableArray("key_scanresults", (Parcelable[]) arrayList.toArray(new Parcelable[arrayList.size()]));
        }
        ArrayList<TimestampedScoredNetwork> arrayList2 = this.mScoredNetworkCache;
        if (arrayList2 != null) {
            bundle.putParcelableArrayList("key_scorednetworkcache", arrayList2);
        }
        bundle.putInt("key_security", this.mSecurity);
        bundle.putInt("key_speed", this.mSpeed);
        bundle.putBoolean("key_is_carrier_ap", this.mIsCarrierAp);
        String str3 = this.mCarrierName;
        if (str3 != null) {
            bundle.putString("key_carrier_name", str3);
        }
        AccessPoint accessPoint = new AccessPoint(this.mContext, bundle);
        accessPoint.setRssi(this.mRssi);
        return accessPoint;
    }

    @Keep
    public TestAccessPointBuilder setActive(boolean z) {
        if (z) {
            String str = "TestNetwork";
            this.mNetworkInfo = new NetworkInfo(8, 8, str, str);
        } else {
            this.mNetworkInfo = null;
        }
        return this;
    }

    @Keep
    public TestAccessPointBuilder setLevel(int i) {
        if (i == 0) {
            this.mRssi = MIN_RSSI;
        } else if (i >= 5) {
            this.mRssi = MAX_RSSI;
        } else {
            this.mRssi = (int) (((((float) i) * 45.0f) / 4.0f) - 0.044921875f);
        }
        return this;
    }

    @Keep
    public TestAccessPointBuilder setNetworkInfo(NetworkInfo networkInfo) {
        this.mNetworkInfo = networkInfo;
        return this;
    }

    @Keep
    public TestAccessPointBuilder setRssi(int i) {
        this.mRssi = i;
        return this;
    }

    public TestAccessPointBuilder setSpeed(int i) {
        this.mSpeed = i;
        return this;
    }

    @Keep
    public TestAccessPointBuilder setReachable(boolean z) {
        if (!z) {
            this.mRssi = Integer.MIN_VALUE;
        } else if (this.mRssi == Integer.MIN_VALUE) {
            this.mRssi = MIN_RSSI;
        }
        return this;
    }

    @Keep
    public TestAccessPointBuilder setSaved(boolean z) {
        if (z) {
            this.mNetworkId = 1;
        } else {
            this.mNetworkId = -1;
        }
        return this;
    }

    @Keep
    public TestAccessPointBuilder setSecurity(int i) {
        this.mSecurity = i;
        return this;
    }

    @Keep
    public TestAccessPointBuilder setSsid(String str) {
        this.ssid = str;
        return this;
    }

    @Keep
    public TestAccessPointBuilder setFqdn(String str) {
        this.mFqdn = str;
        return this;
    }

    @Keep
    public TestAccessPointBuilder setProviderFriendlyName(String str) {
        this.mProviderFriendlyName = str;
        return this;
    }

    @Keep
    public TestAccessPointBuilder setWifiInfo(WifiInfo wifiInfo) {
        this.mWifiInfo = wifiInfo;
        return this;
    }

    @Keep
    public TestAccessPointBuilder setNetworkId(int i) {
        this.mNetworkId = i;
        return this;
    }

    public TestAccessPointBuilder setBssid(String str) {
        this.mBssid = str;
        return this;
    }

    public TestAccessPointBuilder setScanResults(ArrayList<ScanResult> arrayList) {
        this.mScanResults = arrayList;
        return this;
    }

    public TestAccessPointBuilder setIsCarrierAp(boolean z) {
        this.mIsCarrierAp = z;
        return this;
    }

    public TestAccessPointBuilder setCarrierName(String str) {
        this.mCarrierName = str;
        return this;
    }

    public TestAccessPointBuilder setScoredNetworkCache(ArrayList<TimestampedScoredNetwork> arrayList) {
        this.mScoredNetworkCache = arrayList;
        return this;
    }
}