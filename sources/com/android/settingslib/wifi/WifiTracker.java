package com.android.settingslib.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkKey;
import android.net.NetworkRequest;
import android.net.NetworkRequest.Builder;
import android.net.NetworkScoreManager;
import android.net.ScoredNetwork;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.net.wifi.WifiNetworkScoreCache.CacheListener;
import android.net.wifi.hotspot2.OsuProvider;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings.Global;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import com.android.settingslib.R$string;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.wifi.WifiTracker.WifiListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class WifiTracker implements LifecycleObserver, OnStart, OnStop, OnDestroy {
    static final long MAX_SCAN_RESULT_AGE_MILLIS = 15000;
    public static boolean sVerboseLogging;
    private final AtomicBoolean mConnected;
    /* access modifiers changed from: private */
    public final ConnectivityManager mConnectivityManager;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final IntentFilter mFilter;
    private final List<AccessPoint> mInternalAccessPoints;
    private WifiInfo mLastInfo;
    private NetworkInfo mLastNetworkInfo;
    /* access modifiers changed from: private */
    public boolean mLastScanSucceeded;
    private final WifiListenerExecutor mListener;
    private final Object mLock;
    private long mMaxSpeedLabelScoreCacheAge;
    private WifiTrackerNetworkCallback mNetworkCallback;
    private final NetworkRequest mNetworkRequest;
    private final NetworkScoreManager mNetworkScoreManager;
    private boolean mNetworkScoringUiEnabled;
    final BroadcastReceiver mReceiver;
    /* access modifiers changed from: private */
    public boolean mRegistered;
    private final Set<NetworkKey> mRequestedScores;
    private final HashMap<String, ScanResult> mScanResultCache;
    Scanner mScanner;
    private WifiNetworkScoreCache mScoreCache;
    /* access modifiers changed from: private */
    public boolean mStaleScanResults;
    /* access modifiers changed from: private */
    public final WifiManager mWifiManager;
    Handler mWorkHandler;
    private HandlerThread mWorkThread;

    private static class Multimap<K, V> {
        private final HashMap<K, List<V>> store;

        private Multimap() {
            this.store = new HashMap<>();
        }

        /* access modifiers changed from: 0000 */
        public List<V> getAll(K k) {
            List<V> list = (List) this.store.get(k);
            return list != null ? list : Collections.emptyList();
        }

        /* access modifiers changed from: 0000 */
        public void put(K k, V v) {
            List list = (List) this.store.get(k);
            if (list == null) {
                list = new ArrayList(3);
                this.store.put(k, list);
            }
            list.add(v);
        }
    }

    class Scanner extends Handler {
        private int mRetry = 0;

        Scanner() {
        }

        /* access modifiers changed from: 0000 */
        public void resume() {
            if (WifiTracker.isVerboseLoggingEnabled()) {
                Log.d("WifiTracker", "Scanner resume");
            }
            if (!hasMessages(0)) {
                sendEmptyMessage(0);
            }
        }

        /* access modifiers changed from: 0000 */
        public void pause() {
            if (WifiTracker.isVerboseLoggingEnabled()) {
                Log.d("WifiTracker", "Scanner pause");
            }
            this.mRetry = 0;
            removeMessages(0);
        }

        /* access modifiers changed from: 0000 */
        public boolean isScanning() {
            return hasMessages(0);
        }

        public void handleMessage(Message message) {
            if (message.what == 0) {
                if (WifiTracker.this.mWifiManager.startScan()) {
                    this.mRetry = 0;
                } else {
                    int i = this.mRetry + 1;
                    this.mRetry = i;
                    if (i >= 3) {
                        this.mRetry = 0;
                        if (WifiTracker.this.mContext != null) {
                            Toast.makeText(WifiTracker.this.mContext, R$string.wifi_fail_to_scan, 1).show();
                        }
                        return;
                    }
                }
                sendEmptyMessageDelayed(0, 10000);
            }
        }
    }

    public interface WifiListener {
        void onAccessPointsChanged();

        void onConnectedChanged();

        void onWifiStateChanged(int i);
    }

    class WifiListenerExecutor implements WifiListener {
        private final WifiListener mDelegatee;

        public WifiListenerExecutor(WifiListener wifiListener) {
            this.mDelegatee = wifiListener;
        }

        public /* synthetic */ void lambda$onWifiStateChanged$0$WifiTracker$WifiListenerExecutor(int i) {
            this.mDelegatee.onWifiStateChanged(i);
        }

        public void onWifiStateChanged(int i) {
            runAndLog(new Runnable(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    WifiListenerExecutor.this.lambda$onWifiStateChanged$0$WifiTracker$WifiListenerExecutor(this.f$1);
                }
            }, String.format("Invoking onWifiStateChanged callback with state %d", new Object[]{Integer.valueOf(i)}));
        }

        public void onConnectedChanged() {
            WifiListener wifiListener = this.mDelegatee;
            Objects.requireNonNull(wifiListener);
            runAndLog(new Runnable() {
                public final void run() {
                    WifiListener.this.onConnectedChanged();
                }
            }, "Invoking onConnectedChanged callback");
        }

        public void onAccessPointsChanged() {
            WifiListener wifiListener = this.mDelegatee;
            Objects.requireNonNull(wifiListener);
            runAndLog(new Runnable() {
                public final void run() {
                    WifiListener.this.onAccessPointsChanged();
                }
            }, "Invoking onAccessPointsChanged callback");
        }

        private void runAndLog(Runnable runnable, String str) {
            ThreadUtils.postOnMainThread(new Runnable(str, runnable) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ Runnable f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    WifiListenerExecutor.this.lambda$runAndLog$1$WifiTracker$WifiListenerExecutor(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$runAndLog$1$WifiTracker$WifiListenerExecutor(String str, Runnable runnable) {
            if (WifiTracker.this.mRegistered) {
                if (WifiTracker.isVerboseLoggingEnabled()) {
                    Log.i("WifiTracker", str);
                }
                runnable.run();
            }
        }
    }

    private final class WifiTrackerNetworkCallback extends NetworkCallback {
        private WifiTrackerNetworkCallback() {
        }

        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            if (network.equals(WifiTracker.this.mWifiManager.getCurrentNetwork())) {
                WifiTracker.this.updateNetworkInfo(null);
            }
        }
    }

    private static final boolean DBG() {
        return Log.isLoggable("WifiTracker", 3);
    }

    /* access modifiers changed from: private */
    public static boolean isVerboseLoggingEnabled() {
        return sVerboseLogging || Log.isLoggable("WifiTracker", 2);
    }

    private static IntentFilter newIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        intentFilter.addAction("android.net.wifi.NETWORK_IDS_CHANGED");
        intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
        intentFilter.addAction("android.net.wifi.LINK_CONFIGURATION_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.RSSI_CHANGED");
        intentFilter.addAction("android.intent.action.AUTH_PASSWORD_WRONG");
        return intentFilter;
    }

    @Deprecated
    public WifiTracker(Context context, WifiListener wifiListener, boolean z, boolean z2) {
        this(context, wifiListener, (WifiManager) context.getSystemService(WifiManager.class), (ConnectivityManager) context.getSystemService(ConnectivityManager.class), (NetworkScoreManager) context.getSystemService(NetworkScoreManager.class), newIntentFilter());
    }

    WifiTracker(Context context, WifiListener wifiListener, WifiManager wifiManager, ConnectivityManager connectivityManager, NetworkScoreManager networkScoreManager, IntentFilter intentFilter) {
        boolean z = false;
        this.mConnected = new AtomicBoolean(false);
        this.mLock = new Object();
        this.mInternalAccessPoints = new ArrayList();
        this.mRequestedScores = new ArraySet();
        this.mStaleScanResults = true;
        this.mLastScanSucceeded = true;
        this.mScanResultCache = new HashMap<>();
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                WifiTracker.sVerboseLogging = WifiTracker.this.mWifiManager.getVerboseLoggingLevel() > 0;
                if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action)) {
                    WifiTracker.this.updateWifiState(intent.getIntExtra("wifi_state", 4));
                } else if ("android.net.wifi.SCAN_RESULTS".equals(action)) {
                    WifiTracker.this.mStaleScanResults = false;
                    WifiTracker.this.mLastScanSucceeded = intent.getBooleanExtra("resultsUpdated", true);
                    WifiTracker.this.fetchScansAndConfigsAndUpdateAccessPoints();
                } else if ("android.net.wifi.CONFIGURED_NETWORKS_CHANGE".equals(action) || "android.net.wifi.LINK_CONFIGURATION_CHANGED".equals(action)) {
                    WifiTracker.this.fetchScansAndConfigsAndUpdateAccessPoints();
                } else if ("android.net.wifi.STATE_CHANGE".equals(action)) {
                    WifiTracker.this.updateNetworkInfo((NetworkInfo) intent.getParcelableExtra("networkInfo"));
                    WifiTracker.this.fetchScansAndConfigsAndUpdateAccessPoints();
                } else if ("android.net.wifi.RSSI_CHANGED".equals(action)) {
                    WifiTracker.this.updateNetworkInfo(WifiTracker.this.mConnectivityManager.getNetworkInfo(WifiTracker.this.mWifiManager.getCurrentNetwork()));
                } else if ("android.intent.action.AUTH_PASSWORD_WRONG".equals(action)) {
                    Toast.makeText(context, context.getString(84869378), 0).show();
                }
            }
        };
        this.mContext = context;
        this.mWifiManager = wifiManager;
        this.mListener = new WifiListenerExecutor(wifiListener);
        this.mConnectivityManager = connectivityManager;
        WifiManager wifiManager2 = this.mWifiManager;
        if (wifiManager2 != null && wifiManager2.getVerboseLoggingLevel() > 0) {
            z = true;
        }
        sVerboseLogging = z;
        this.mFilter = intentFilter;
        this.mNetworkRequest = new Builder().clearCapabilities().addCapability(15).addTransportType(1).build();
        this.mNetworkScoreManager = networkScoreManager;
        StringBuilder sb = new StringBuilder();
        sb.append("WifiTracker{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append("}");
        HandlerThread handlerThread = new HandlerThread(sb.toString(), 10);
        handlerThread.start();
        setWorkThread(handlerThread);
    }

    /* access modifiers changed from: 0000 */
    public void setWorkThread(HandlerThread handlerThread) {
        this.mWorkThread = handlerThread;
        this.mWorkHandler = new Handler(handlerThread.getLooper());
        this.mScoreCache = new WifiNetworkScoreCache(this.mContext, new CacheListener(this.mWorkHandler) {
            public void networkCacheUpdated(List<ScoredNetwork> list) {
                if (WifiTracker.this.mRegistered) {
                    String str = "WifiTracker";
                    if (Log.isLoggable(str, 2)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Score cache was updated with networks: ");
                        sb.append(list);
                        Log.v(str, sb.toString());
                    }
                    WifiTracker.this.updateNetworkScores();
                }
            }
        });
    }

    public void onDestroy() {
        this.mWorkThread.quit();
    }

    private void pauseScanning() {
        synchronized (this.mLock) {
            if (this.mScanner != null) {
                this.mScanner.pause();
                this.mScanner = null;
            }
        }
        this.mStaleScanResults = true;
    }

    public void resumeScanning() {
        synchronized (this.mLock) {
            if (this.mScanner == null) {
                this.mScanner = new Scanner();
            }
            if (isWifiEnabled()) {
                this.mScanner.resume();
            }
        }
    }

    public void onStart() {
        forceUpdate();
        registerScoreCache();
        boolean z = false;
        if (Global.getInt(this.mContext.getContentResolver(), "network_scoring_ui_enabled", 0) == 1) {
            z = true;
        }
        this.mNetworkScoringUiEnabled = z;
        this.mMaxSpeedLabelScoreCacheAge = Global.getLong(this.mContext.getContentResolver(), "speed_label_cache_eviction_age_millis", 1200000);
        resumeScanning();
        if (!this.mRegistered) {
            this.mContext.registerReceiver(this.mReceiver, this.mFilter, null, this.mWorkHandler);
            this.mNetworkCallback = new WifiTrackerNetworkCallback();
            this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, this.mNetworkCallback, this.mWorkHandler);
            this.mRegistered = true;
        }
    }

    /* access modifiers changed from: 0000 */
    public void forceUpdate() {
        this.mLastInfo = this.mWifiManager.getConnectionInfo();
        this.mLastNetworkInfo = this.mConnectivityManager.getNetworkInfo(this.mWifiManager.getCurrentNetwork());
        fetchScansAndConfigsAndUpdateAccessPoints();
    }

    private void registerScoreCache() {
        this.mNetworkScoreManager.registerNetworkScoreCache(1, this.mScoreCache, 2);
    }

    private void requestScoresForNetworkKeys(Collection<NetworkKey> collection) {
        if (!collection.isEmpty()) {
            if (DBG()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Requesting scores for Network Keys: ");
                sb.append(collection);
                Log.d("WifiTracker", sb.toString());
            }
            this.mNetworkScoreManager.requestScores((NetworkKey[]) collection.toArray(new NetworkKey[collection.size()]));
            synchronized (this.mLock) {
                this.mRequestedScores.addAll(collection);
            }
        }
    }

    public void onStop() {
        if (this.mRegistered) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
            this.mRegistered = false;
        }
        unregisterScoreCache();
        pauseScanning();
        this.mWorkHandler.removeCallbacksAndMessages(null);
    }

    private void unregisterScoreCache() {
        this.mNetworkScoreManager.unregisterNetworkScoreCache(1, this.mScoreCache);
        synchronized (this.mLock) {
            this.mRequestedScores.clear();
        }
    }

    public List<AccessPoint> getAccessPoints() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mInternalAccessPoints);
        }
        return arrayList;
    }

    public WifiManager getManager() {
        return this.mWifiManager;
    }

    public boolean isWifiEnabled() {
        WifiManager wifiManager = this.mWifiManager;
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("  - wifi tracker ------");
        for (AccessPoint accessPoint : getAccessPoints()) {
            StringBuilder sb = new StringBuilder();
            sb.append("  ");
            sb.append(accessPoint);
            printWriter.println(sb.toString());
        }
    }

    private ArrayMap<String, List<ScanResult>> updateScanResultCache(List<ScanResult> list) {
        List list2;
        for (ScanResult scanResult : list) {
            String str = scanResult.SSID;
            if (str != null && !str.isEmpty()) {
                this.mScanResultCache.put(scanResult.BSSID, scanResult);
            }
        }
        evictOldScans();
        ArrayMap<String, List<ScanResult>> arrayMap = new ArrayMap<>();
        for (ScanResult scanResult2 : this.mScanResultCache.values()) {
            String str2 = scanResult2.SSID;
            if (!(str2 == null || str2.length() == 0 || scanResult2.capabilities.contains("[IBSS]"))) {
                String key = AccessPoint.getKey(scanResult2);
                if (arrayMap.containsKey(key)) {
                    list2 = (List) arrayMap.get(key);
                } else {
                    List arrayList = new ArrayList();
                    arrayMap.put(key, arrayList);
                    list2 = arrayList;
                }
                list2.add(scanResult2);
            }
        }
        return arrayMap;
    }

    private void evictOldScans() {
        long j = this.mLastScanSucceeded ? MAX_SCAN_RESULT_AGE_MILLIS : 30000;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        Iterator it = this.mScanResultCache.values().iterator();
        while (it.hasNext()) {
            if (elapsedRealtime - (((ScanResult) it.next()).timestamp / 1000) > j) {
                it.remove();
            }
        }
    }

    private WifiConfiguration getWifiConfigurationForNetworkId(int i, List<WifiConfiguration> list) {
        if (list != null) {
            for (WifiConfiguration wifiConfiguration : list) {
                if (this.mLastInfo != null && i == wifiConfiguration.networkId) {
                    if (!wifiConfiguration.selfAdded || wifiConfiguration.numAssociation != 0) {
                        return wifiConfiguration;
                    }
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void fetchScansAndConfigsAndUpdateAccessPoints() {
        List filterScanResultsByCapabilities = filterScanResultsByCapabilities(this.mWifiManager.getScanResults());
        if (isVerboseLoggingEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Fetched scan results: ");
            sb.append(filterScanResultsByCapabilities);
            Log.i("WifiTracker", sb.toString());
        }
        updateAccessPoints(filterScanResultsByCapabilities, this.mWifiManager.getConfiguredNetworks());
    }

    private boolean SecurityMatch(AccessPoint accessPoint, ScanResult scanResult) {
        boolean z = true;
        if (AccessPoint.getSecurity(scanResult) == 7 && (accessPoint.getSecurity() == 5 || accessPoint.getSecurity() == 2)) {
            return true;
        }
        if (AccessPoint.getSecurity(scanResult) == 8 && accessPoint.getSecurity() == 4) {
            return true;
        }
        if (accessPoint.getSecurity() != AccessPoint.getSecurity(scanResult)) {
            z = false;
        }
        return z;
    }

    private void updateAccessPoints(List<ScanResult> list, List<WifiConfiguration> list2) {
        boolean z;
        boolean z2;
        boolean z3;
        WifiConfiguration wifiConfiguration = null;
        Multimap multimap = new Multimap();
        WifiInfo wifiInfo = this.mLastInfo;
        if (wifiInfo != null) {
            wifiConfiguration = getWifiConfigurationForNetworkId(wifiInfo.getNetworkId(), list2);
        }
        synchronized (this.mLock) {
            ArrayMap updateScanResultCache = updateScanResultCache(list);
            ArrayList<AccessPoint> arrayList = new ArrayList<>(this.mInternalAccessPoints);
            for (AccessPoint clearConfig : arrayList) {
                clearConfig.clearConfig();
            }
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            if (list2 != null) {
                for (WifiConfiguration wifiConfiguration2 : list2) {
                    if (!wifiConfiguration2.selfAdded || wifiConfiguration2.numAssociation != 0) {
                        AccessPoint cachedOrCreate = getCachedOrCreate(wifiConfiguration2, (List<AccessPoint>) arrayList);
                        if (!(this.mLastInfo == null || this.mLastNetworkInfo == null)) {
                            cachedOrCreate.update(wifiConfiguration, this.mLastInfo, this.mLastNetworkInfo);
                        }
                        Iterator it = updateScanResultCache.entrySet().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                z3 = false;
                                break;
                            }
                            ScanResult scanResult = (ScanResult) ((List) ((Entry) it.next()).getValue()).get(0);
                            if (scanResult.SSID.equals(cachedOrCreate.getSsidStr()) && SecurityMatch(cachedOrCreate, scanResult)) {
                                z3 = true;
                                break;
                            }
                        }
                        if (!z3) {
                            cachedOrCreate.setUnreachable();
                        }
                        arrayList2.add(cachedOrCreate);
                        multimap.put(cachedOrCreate.getSsidStr(), cachedOrCreate);
                    }
                }
            }
            for (Entry entry : updateScanResultCache.entrySet()) {
                for (ScanResult createFromScanResult : (List) entry.getValue()) {
                    NetworkKey createFromScanResult2 = NetworkKey.createFromScanResult(createFromScanResult);
                    if (createFromScanResult2 != null && !this.mRequestedScores.contains(createFromScanResult2)) {
                        arrayList3.add(createFromScanResult2);
                    }
                }
                ScanResult scanResult2 = (ScanResult) ((List) entry.getValue()).get(0);
                Iterator it2 = multimap.getAll(scanResult2.SSID).iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        z2 = false;
                        break;
                    }
                    AccessPoint accessPoint = (AccessPoint) it2.next();
                    if (accessPoint.matches(scanResult2)) {
                        accessPoint.setScanResults((Collection) entry.getValue());
                        z2 = true;
                        break;
                    }
                }
                if (!z2) {
                    AccessPoint cachedOrCreate2 = getCachedOrCreate((List) entry.getValue(), (List<AccessPoint>) arrayList);
                    if (!(this.mLastInfo == null || this.mLastNetworkInfo == null)) {
                        cachedOrCreate2.update(wifiConfiguration, this.mLastInfo, this.mLastNetworkInfo);
                    }
                    arrayList2.add(cachedOrCreate2);
                    multimap.put(cachedOrCreate2.getSsidStr(), cachedOrCreate2);
                }
            }
            ArrayList arrayList4 = new ArrayList(this.mScanResultCache.values());
            arrayList2.addAll(updatePasspointAccessPoints(this.mWifiManager.getAllMatchingWifiConfigs(arrayList4), arrayList));
            arrayList2.addAll(updateOsuAccessPoints(this.mWifiManager.getMatchingOsuProviders(arrayList4), arrayList));
            if (!(this.mLastInfo == null || this.mLastNetworkInfo == null)) {
                Iterator it3 = arrayList2.iterator();
                while (it3.hasNext()) {
                    ((AccessPoint) it3.next()).update(wifiConfiguration, this.mLastInfo, this.mLastNetworkInfo);
                }
            }
            if (arrayList2.isEmpty() && wifiConfiguration != null) {
                AccessPoint accessPoint2 = new AccessPoint(this.mContext, wifiConfiguration);
                accessPoint2.update(wifiConfiguration, this.mLastInfo, this.mLastNetworkInfo);
                arrayList2.add(accessPoint2);
                arrayList3.add(NetworkKey.createFromWifiInfo(this.mLastInfo));
            }
            requestScoresForNetworkKeys(arrayList3);
            Iterator it4 = arrayList2.iterator();
            while (it4.hasNext()) {
                ((AccessPoint) it4.next()).update(this.mScoreCache, this.mNetworkScoringUiEnabled, this.mMaxSpeedLabelScoreCacheAge);
            }
            Collections.sort(arrayList2);
            if (DBG()) {
                Log.d("WifiTracker", "------ Dumping AccessPoints that were not seen on this scan ------");
                for (AccessPoint title : this.mInternalAccessPoints) {
                    String title2 = title.getTitle();
                    Iterator it5 = arrayList2.iterator();
                    while (true) {
                        if (!it5.hasNext()) {
                            z = false;
                            break;
                        }
                        AccessPoint accessPoint3 = (AccessPoint) it5.next();
                        if (accessPoint3.getTitle() != null && accessPoint3.getTitle().equals(title2)) {
                            z = true;
                            break;
                        }
                    }
                    if (!z) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Did not find ");
                        sb.append(title2);
                        sb.append(" in this scan");
                        Log.d("WifiTracker", sb.toString());
                    }
                }
                Log.d("WifiTracker", "---- Done dumping AccessPoints that were not seen on this scan ----");
            }
            this.mInternalAccessPoints.clear();
            this.mInternalAccessPoints.addAll(arrayList2);
        }
        conditionallyNotifyListeners();
    }

    /* access modifiers changed from: 0000 */
    public List<AccessPoint> updatePasspointAccessPoints(List<Pair<WifiConfiguration, Map<Integer, List<ScanResult>>>> list, List<AccessPoint> list2) {
        ArrayList arrayList = new ArrayList();
        ArraySet arraySet = new ArraySet();
        for (Pair pair : list) {
            WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
            if (arraySet.add(wifiConfiguration.FQDN)) {
                arrayList.add(getCachedOrCreatePasspoint(wifiConfiguration, (List) ((Map) pair.second).get(Integer.valueOf(0)), (List) ((Map) pair.second).get(Integer.valueOf(1)), list2));
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: 0000 */
    public List<AccessPoint> updateOsuAccessPoints(Map<OsuProvider, List<ScanResult>> map, List<AccessPoint> list) {
        ArrayList arrayList = new ArrayList();
        Set keySet = this.mWifiManager.getMatchingPasspointConfigsForOsuProviders(map.keySet()).keySet();
        for (OsuProvider osuProvider : map.keySet()) {
            if (!keySet.contains(osuProvider)) {
                arrayList.add(getCachedOrCreateOsu(osuProvider, (List) map.get(osuProvider), list));
            }
        }
        return arrayList;
    }

    private AccessPoint getCachedOrCreate(List<ScanResult> list, List<AccessPoint> list2) {
        AccessPoint cachedByKey = getCachedByKey(list2, AccessPoint.getKey((ScanResult) list.get(0)));
        if (cachedByKey == null) {
            return new AccessPoint(this.mContext, (Collection<ScanResult>) list);
        }
        cachedByKey.setScanResults(list);
        return cachedByKey;
    }

    /* access modifiers changed from: 0000 */
    public AccessPoint getCachedOrCreate(WifiConfiguration wifiConfiguration, List<AccessPoint> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (((AccessPoint) list.get(i)).matches(wifiConfiguration)) {
                AccessPoint accessPoint = (AccessPoint) list.remove(i);
                accessPoint.loadConfig(wifiConfiguration);
                return accessPoint;
            }
        }
        AccessPoint accessPoint2 = new AccessPoint(this.mContext, wifiConfiguration);
        StringBuilder sb = new StringBuilder();
        sb.append("getCachedOrCreate Create AccessPoint, ssid :");
        sb.append(accessPoint2.getSsidStr());
        Log.d("WifiTracker", sb.toString());
        return accessPoint2;
    }

    private AccessPoint getCachedOrCreatePasspoint(WifiConfiguration wifiConfiguration, List<ScanResult> list, List<ScanResult> list2, List<AccessPoint> list3) {
        AccessPoint cachedByKey = getCachedByKey(list3, AccessPoint.getKey(wifiConfiguration));
        if (cachedByKey == null) {
            return new AccessPoint(this.mContext, wifiConfiguration, list, list2);
        }
        cachedByKey.update(wifiConfiguration);
        cachedByKey.setScanResultsPasspoint(list, list2);
        return cachedByKey;
    }

    private AccessPoint getCachedOrCreateOsu(OsuProvider osuProvider, List<ScanResult> list, List<AccessPoint> list2) {
        AccessPoint cachedByKey = getCachedByKey(list2, AccessPoint.getKey(osuProvider));
        if (cachedByKey == null) {
            return new AccessPoint(this.mContext, osuProvider, list);
        }
        cachedByKey.setScanResults(list);
        return cachedByKey;
    }

    private AccessPoint getCachedByKey(List<AccessPoint> list, String str) {
        ListIterator listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            AccessPoint accessPoint = (AccessPoint) listIterator.next();
            if (accessPoint.getKey().equals(str)) {
                listIterator.remove();
                return accessPoint;
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void updateNetworkInfo(NetworkInfo networkInfo) {
        if (!isWifiEnabled()) {
            clearAccessPointsAndConditionallyUpdate();
        } else if (networkInfo != null) {
            this.mLastNetworkInfo = networkInfo;
            if (DBG()) {
                StringBuilder sb = new StringBuilder();
                sb.append("mLastNetworkInfo set: ");
                sb.append(this.mLastNetworkInfo);
                Log.d("WifiTracker", sb.toString());
            }
            if (networkInfo.isConnected() != this.mConnected.getAndSet(networkInfo.isConnected())) {
                this.mListener.onConnectedChanged();
            }
        }
        WifiConfiguration wifiConfiguration = null;
        this.mLastInfo = this.mWifiManager.getConnectionInfo();
        if (DBG()) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("mLastInfo set as: ");
            sb2.append(this.mLastInfo);
            Log.d("WifiTracker", sb2.toString());
        }
        WifiInfo wifiInfo = this.mLastInfo;
        if (wifiInfo != null) {
            wifiConfiguration = getWifiConfigurationForNetworkId(wifiInfo.getNetworkId(), this.mWifiManager.getConfiguredNetworks());
        }
        synchronized (this.mLock) {
            boolean z = false;
            boolean z2 = false;
            for (int size = this.mInternalAccessPoints.size() - 1; size >= 0; size--) {
                AccessPoint accessPoint = (AccessPoint) this.mInternalAccessPoints.get(size);
                boolean isActive = accessPoint.isActive();
                if (accessPoint.update(wifiConfiguration, this.mLastInfo, this.mLastNetworkInfo)) {
                    if (isActive != accessPoint.isActive()) {
                        z = true;
                        z2 = true;
                    } else {
                        z2 = true;
                    }
                }
                if (accessPoint.update(this.mScoreCache, this.mNetworkScoringUiEnabled, this.mMaxSpeedLabelScoreCacheAge)) {
                    z = true;
                    z2 = true;
                }
            }
            if (z) {
                Collections.sort(this.mInternalAccessPoints);
            }
            if (z2) {
                conditionallyNotifyListeners();
            }
        }
    }

    private void clearAccessPointsAndConditionallyUpdate() {
        synchronized (this.mLock) {
            if (!this.mInternalAccessPoints.isEmpty()) {
                this.mInternalAccessPoints.clear();
                conditionallyNotifyListeners();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateNetworkScores() {
        synchronized (this.mLock) {
            boolean z = false;
            for (int i = 0; i < this.mInternalAccessPoints.size(); i++) {
                if (((AccessPoint) this.mInternalAccessPoints.get(i)).update(this.mScoreCache, this.mNetworkScoringUiEnabled, this.mMaxSpeedLabelScoreCacheAge)) {
                    z = true;
                }
            }
            if (z) {
                Collections.sort(this.mInternalAccessPoints);
                conditionallyNotifyListeners();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateWifiState(int i) {
        if (isVerboseLoggingEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("updateWifiState: ");
            sb.append(i);
            Log.d("WifiTracker", sb.toString());
        }
        if (i == 3) {
            synchronized (this.mLock) {
                if (this.mScanner != null) {
                    this.mScanner.resume();
                }
            }
        } else {
            clearAccessPointsAndConditionallyUpdate();
            this.mLastInfo = null;
            this.mLastNetworkInfo = null;
            synchronized (this.mLock) {
                if (this.mScanner != null) {
                    this.mScanner.pause();
                }
            }
            this.mStaleScanResults = true;
        }
        this.mListener.onWifiStateChanged(i);
    }

    private void conditionallyNotifyListeners() {
        if (!this.mStaleScanResults) {
            this.mListener.onAccessPointsChanged();
        }
    }

    private List<ScanResult> filterScanResultsByCapabilities(List<ScanResult> list) {
        if (list == null) {
            return null;
        }
        boolean isEnhancedOpenSupported = this.mWifiManager.isEnhancedOpenSupported();
        boolean isWpa3SaeSupported = this.mWifiManager.isWpa3SaeSupported();
        boolean isWpa3SuiteBSupported = this.mWifiManager.isWpa3SuiteBSupported();
        ArrayList arrayList = new ArrayList();
        for (ScanResult scanResult : list) {
            if (scanResult.capabilities.contains("PSK")) {
                arrayList.add(scanResult);
            } else if ((!scanResult.capabilities.contains("SUITE_B_192") || isWpa3SuiteBSupported) && ((!scanResult.capabilities.contains("SAE") || isWpa3SaeSupported) && (!scanResult.capabilities.contains("OWE") || isEnhancedOpenSupported))) {
                arrayList.add(scanResult);
            } else if (isVerboseLoggingEnabled()) {
                StringBuilder sb = new StringBuilder();
                sb.append("filterScanResultsByCapabilities: Filtering SSID ");
                sb.append(scanResult.SSID);
                sb.append(" with capabilities: ");
                sb.append(scanResult.capabilities);
                Log.v("WifiTracker", sb.toString());
            }
        }
        return arrayList;
    }
}
