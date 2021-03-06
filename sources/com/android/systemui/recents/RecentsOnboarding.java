package com.android.systemui.recents;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.Prefs;
import com.android.systemui.R$array;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.recents.OverviewProxyService.OverviewProxyListener;
import com.android.systemui.shared.recents.IOverviewProxy;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.TaskStackChangeListener;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@TargetApi(28)
public class RecentsOnboarding {
    private final View mArrowView;
    /* access modifiers changed from: private */
    public Set<String> mBlacklistedPackages;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final ImageView mDismissView;
    /* access modifiers changed from: private */
    public boolean mHasDismissedQuickScrubTip;
    /* access modifiers changed from: private */
    public boolean mHasDismissedSwipeUpTip;
    /* access modifiers changed from: private */
    public final View mLayout;
    /* access modifiers changed from: private */
    public boolean mLayoutAttachedToWindow;
    private int mNavBarHeight;
    private int mNavBarMode = 0;
    /* access modifiers changed from: private */
    public int mNumAppsLaunchedSinceSwipeUpTipDismiss;
    private final OnAttachStateChangeListener mOnAttachStateChangeListener = new OnAttachStateChangeListener() {
        public void onViewAttachedToWindow(View view) {
            if (view == RecentsOnboarding.this.mLayout) {
                RecentsOnboarding.this.mContext.registerReceiver(RecentsOnboarding.this.mReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
                RecentsOnboarding.this.mLayoutAttachedToWindow = true;
                if (view.getTag().equals(Integer.valueOf(R$string.recents_swipe_up_onboarding))) {
                    RecentsOnboarding.this.mHasDismissedSwipeUpTip = false;
                } else {
                    RecentsOnboarding.this.mHasDismissedQuickScrubTip = false;
                }
            }
        }

        public void onViewDetachedFromWindow(View view) {
            if (view == RecentsOnboarding.this.mLayout) {
                RecentsOnboarding.this.mLayoutAttachedToWindow = false;
                if (view.getTag().equals(Integer.valueOf(R$string.recents_quick_scrub_onboarding))) {
                    RecentsOnboarding.this.mHasDismissedQuickScrubTip = true;
                    if (RecentsOnboarding.this.hasDismissedQuickScrubOnboardingOnce()) {
                        RecentsOnboarding.this.setHasSeenQuickScrubOnboarding(true);
                    } else {
                        RecentsOnboarding.this.setHasDismissedQuickScrubOnboardingOnce(true);
                    }
                    RecentsOnboarding.this.mOverviewOpenedCountSinceQuickScrubTipDismiss = 0;
                }
                RecentsOnboarding.this.mContext.unregisterReceiver(RecentsOnboarding.this.mReceiver);
            }
        }
    };
    private final int mOnboardingToastArrowRadius;
    private final int mOnboardingToastColor;
    /* access modifiers changed from: private */
    public int mOverviewOpenedCountSinceQuickScrubTipDismiss;
    private OverviewProxyListener mOverviewProxyListener = new OverviewProxyListener() {
        public void onOverviewShown(boolean z) {
            if (!RecentsOnboarding.this.hasSeenSwipeUpOnboarding() && !z) {
                RecentsOnboarding.this.setHasSeenSwipeUpOnboarding(true);
            }
            if (z) {
                RecentsOnboarding.this.incrementOpenedOverviewFromHomeCount();
            }
            RecentsOnboarding.this.incrementOpenedOverviewCount();
            if (RecentsOnboarding.this.getOpenedOverviewCount() >= 10 && RecentsOnboarding.this.mHasDismissedQuickScrubTip) {
                RecentsOnboarding.this.mOverviewOpenedCountSinceQuickScrubTipDismiss = RecentsOnboarding.this.mOverviewOpenedCountSinceQuickScrubTipDismiss + 1;
            }
        }
    };
    private boolean mOverviewProxyListenerRegistered;
    private final OverviewProxyService mOverviewProxyService;
    /* access modifiers changed from: private */
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                RecentsOnboarding.this.hide(false);
            }
        }
    };
    private final TaskStackChangeListener mTaskListener = new TaskStackChangeListener() {
        private String mLastPackageName;

        public void onTaskCreated(int i, ComponentName componentName) {
            onAppLaunch();
        }

        public void onTaskMovedToFront(int i) {
            onAppLaunch();
        }

        private void onAppLaunch() {
            boolean z;
            boolean z2;
            RunningTaskInfo runningTask = ActivityManagerWrapper.getInstance().getRunningTask(0);
            if (runningTask != null) {
                if (RecentsOnboarding.this.mBlacklistedPackages.contains(runningTask.baseActivity.getPackageName())) {
                    RecentsOnboarding.this.hide(true);
                } else if (!runningTask.baseActivity.getPackageName().equals(this.mLastPackageName)) {
                    this.mLastPackageName = runningTask.baseActivity.getPackageName();
                    if (runningTask.configuration.windowConfiguration.getActivityType() == 1) {
                        boolean access$100 = RecentsOnboarding.this.hasSeenSwipeUpOnboarding();
                        boolean access$200 = RecentsOnboarding.this.hasSeenQuickScrubOnboarding();
                        if (access$100 && access$200) {
                            RecentsOnboarding.this.onDisconnectedFromLauncher();
                        } else if (!access$100) {
                            if (RecentsOnboarding.this.getOpenedOverviewFromHomeCount() >= 3) {
                                if (RecentsOnboarding.this.mHasDismissedSwipeUpTip) {
                                    int access$500 = RecentsOnboarding.this.getDismissedSwipeUpOnboardingCount();
                                    if (access$500 <= 2) {
                                        int i = access$500 <= 1 ? 5 : 40;
                                        RecentsOnboarding.this.mNumAppsLaunchedSinceSwipeUpTipDismiss = RecentsOnboarding.this.mNumAppsLaunchedSinceSwipeUpTipDismiss + 1;
                                        if (RecentsOnboarding.this.mNumAppsLaunchedSinceSwipeUpTipDismiss >= i) {
                                            RecentsOnboarding.this.mNumAppsLaunchedSinceSwipeUpTipDismiss = 0;
                                            z2 = RecentsOnboarding.this.show(R$string.recents_swipe_up_onboarding);
                                        } else {
                                            z2 = false;
                                        }
                                    } else {
                                        return;
                                    }
                                } else {
                                    z2 = RecentsOnboarding.this.show(R$string.recents_swipe_up_onboarding);
                                }
                                if (z2) {
                                    RecentsOnboarding.this.notifyOnTip(0, 0);
                                }
                            }
                        } else if (RecentsOnboarding.this.getOpenedOverviewCount() >= 10) {
                            if (!RecentsOnboarding.this.mHasDismissedQuickScrubTip) {
                                z = RecentsOnboarding.this.show(R$string.recents_quick_scrub_onboarding);
                            } else if (RecentsOnboarding.this.mOverviewOpenedCountSinceQuickScrubTipDismiss >= 10) {
                                RecentsOnboarding.this.mOverviewOpenedCountSinceQuickScrubTipDismiss = 0;
                                z = RecentsOnboarding.this.show(R$string.recents_quick_scrub_onboarding);
                            } else {
                                z = false;
                            }
                            if (z) {
                                RecentsOnboarding.this.notifyOnTip(0, 1);
                            }
                        }
                    } else {
                        RecentsOnboarding.this.hide(false);
                    }
                }
            }
        }
    };
    private boolean mTaskListenerRegistered;
    private final TextView mTextView;
    private final WindowManager mWindowManager;

    public RecentsOnboarding(Context context, OverviewProxyService overviewProxyService) {
        this.mContext = context;
        this.mOverviewProxyService = overviewProxyService;
        Resources resources = context.getResources();
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        this.mBlacklistedPackages = new HashSet();
        Collections.addAll(this.mBlacklistedPackages, resources.getStringArray(R$array.recents_onboarding_blacklisted_packages));
        this.mLayout = LayoutInflater.from(this.mContext).inflate(R$layout.recents_onboarding, null);
        this.mTextView = (TextView) this.mLayout.findViewById(R$id.onboarding_text);
        this.mDismissView = (ImageView) this.mLayout.findViewById(R$id.dismiss);
        this.mArrowView = this.mLayout.findViewById(R$id.arrow);
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843829, typedValue, true);
        this.mOnboardingToastColor = resources.getColor(typedValue.resourceId);
        this.mOnboardingToastArrowRadius = resources.getDimensionPixelSize(R$dimen.recents_onboarding_toast_arrow_corner_radius);
        this.mLayout.addOnAttachStateChangeListener(this.mOnAttachStateChangeListener);
        this.mDismissView.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                RecentsOnboarding.this.lambda$new$0$RecentsOnboarding(view);
            }
        });
        LayoutParams layoutParams = this.mArrowView.getLayoutParams();
        ShapeDrawable shapeDrawable = new ShapeDrawable(TriangleShape.create((float) layoutParams.width, (float) layoutParams.height, false));
        Paint paint = shapeDrawable.getPaint();
        paint.setColor(this.mOnboardingToastColor);
        paint.setPathEffect(new CornerPathEffect((float) this.mOnboardingToastArrowRadius));
        this.mArrowView.setBackground(shapeDrawable);
    }

    public /* synthetic */ void lambda$new$0$RecentsOnboarding(View view) {
        hide(true);
        if (view.getTag().equals(Integer.valueOf(R$string.recents_swipe_up_onboarding))) {
            this.mHasDismissedSwipeUpTip = true;
            this.mNumAppsLaunchedSinceSwipeUpTipDismiss = 0;
            setDismissedSwipeUpOnboardingCount(getDismissedSwipeUpOnboardingCount() + 1);
            if (getDismissedSwipeUpOnboardingCount() > 2) {
                setHasSeenSwipeUpOnboarding(true);
            }
            notifyOnTip(1, 0);
            return;
        }
        notifyOnTip(1, 1);
    }

    /* access modifiers changed from: private */
    public void notifyOnTip(int i, int i2) {
        try {
            IOverviewProxy proxy = this.mOverviewProxyService.getProxy();
            if (proxy != null) {
                proxy.onTip(i, i2);
            }
        } catch (RemoteException unused) {
        }
    }

    public void onNavigationModeChanged(int i) {
        this.mNavBarMode = i;
    }

    public void onConnectedToLauncher() {
        if (!QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            if (!hasSeenSwipeUpOnboarding() || !hasSeenQuickScrubOnboarding()) {
                if (!this.mOverviewProxyListenerRegistered) {
                    this.mOverviewProxyService.addCallback(this.mOverviewProxyListener);
                    this.mOverviewProxyListenerRegistered = true;
                }
                if (!this.mTaskListenerRegistered) {
                    ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskListener);
                    this.mTaskListenerRegistered = true;
                }
            }
        }
    }

    public void onDisconnectedFromLauncher() {
        if (this.mOverviewProxyListenerRegistered) {
            this.mOverviewProxyService.removeCallback(this.mOverviewProxyListener);
            this.mOverviewProxyListenerRegistered = false;
        }
        if (this.mTaskListenerRegistered) {
            ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mTaskListener);
            this.mTaskListenerRegistered = false;
        }
        this.mHasDismissedSwipeUpTip = false;
        this.mHasDismissedQuickScrubTip = false;
        this.mNumAppsLaunchedSinceSwipeUpTipDismiss = 0;
        this.mOverviewOpenedCountSinceQuickScrubTipDismiss = 0;
        hide(true);
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (configuration.orientation != 1) {
            hide(false);
        }
    }

    public boolean show(int i) {
        int i2;
        int i3 = 0;
        if (!shouldShow()) {
            return false;
        }
        this.mDismissView.setTag(Integer.valueOf(i));
        this.mLayout.setTag(Integer.valueOf(i));
        this.mTextView.setText(i);
        int i4 = this.mContext.getResources().getConfiguration().orientation;
        if (this.mLayoutAttachedToWindow || i4 != 1) {
            return false;
        }
        this.mLayout.setSystemUiVisibility(256);
        if (i == R$string.recents_swipe_up_onboarding) {
            i2 = 81;
        } else {
            i2 = (this.mContext.getResources().getConfiguration().getLayoutDirection() == 0 ? 3 : 5) | 80;
            i3 = this.mContext.getResources().getDimensionPixelSize(R$dimen.recents_quick_scrub_onboarding_margin_start);
        }
        this.mWindowManager.addView(this.mLayout, getWindowLayoutParams(i2, i3));
        this.mLayout.setAlpha(0.0f);
        this.mLayout.animate().alpha(1.0f).withLayer().setStartDelay(500).setDuration(300).setInterpolator(new DecelerateInterpolator()).start();
        return true;
    }

    private boolean shouldShow() {
        return SystemProperties.getBoolean("persist.quickstep.onboarding.enabled", !((UserManager) this.mContext.getSystemService(UserManager.class)).isDemoUser() && !ActivityManager.isRunningInTestHarness());
    }

    public void hide(boolean z) {
        if (!this.mLayoutAttachedToWindow) {
            return;
        }
        if (z) {
            this.mLayout.animate().alpha(0.0f).withLayer().setStartDelay(0).setDuration(100).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                public final void run() {
                    RecentsOnboarding.this.lambda$hide$1$RecentsOnboarding();
                }
            }).start();
            return;
        }
        this.mLayout.animate().cancel();
        this.mWindowManager.removeViewImmediate(this.mLayout);
    }

    public /* synthetic */ void lambda$hide$1$RecentsOnboarding() {
        this.mWindowManager.removeViewImmediate(this.mLayout);
    }

    public void setNavBarHeight(int i) {
        this.mNavBarHeight = i;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("RecentsOnboarding {");
        StringBuilder sb = new StringBuilder();
        sb.append("      mTaskListenerRegistered: ");
        sb.append(this.mTaskListenerRegistered);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("      mOverviewProxyListenerRegistered: ");
        sb2.append(this.mOverviewProxyListenerRegistered);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("      mLayoutAttachedToWindow: ");
        sb3.append(this.mLayoutAttachedToWindow);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("      mHasDismissedSwipeUpTip: ");
        sb4.append(this.mHasDismissedSwipeUpTip);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("      mHasDismissedQuickScrubTip: ");
        sb5.append(this.mHasDismissedQuickScrubTip);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append("      mNumAppsLaunchedSinceSwipeUpTipDismiss: ");
        sb6.append(this.mNumAppsLaunchedSinceSwipeUpTipDismiss);
        printWriter.println(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append("      hasSeenSwipeUpOnboarding: ");
        sb7.append(hasSeenSwipeUpOnboarding());
        printWriter.println(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append("      hasSeenQuickScrubOnboarding: ");
        sb8.append(hasSeenQuickScrubOnboarding());
        printWriter.println(sb8.toString());
        StringBuilder sb9 = new StringBuilder();
        sb9.append("      getDismissedSwipeUpOnboardingCount: ");
        sb9.append(getDismissedSwipeUpOnboardingCount());
        printWriter.println(sb9.toString());
        StringBuilder sb10 = new StringBuilder();
        sb10.append("      hasDismissedQuickScrubOnboardingOnce: ");
        sb10.append(hasDismissedQuickScrubOnboardingOnce());
        printWriter.println(sb10.toString());
        StringBuilder sb11 = new StringBuilder();
        sb11.append("      getOpenedOverviewCount: ");
        sb11.append(getOpenedOverviewCount());
        printWriter.println(sb11.toString());
        StringBuilder sb12 = new StringBuilder();
        sb12.append("      getOpenedOverviewFromHomeCount: ");
        sb12.append(getOpenedOverviewFromHomeCount());
        printWriter.println(sb12.toString());
        printWriter.println("    }");
    }

    private WindowManager.LayoutParams getWindowLayoutParams(int i, int i2) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, i2, (-this.mNavBarHeight) / 2, 2038, 520, -3);
        layoutParams.privateFlags |= 16;
        layoutParams.setTitle("RecentsOnboarding");
        layoutParams.gravity = i;
        return layoutParams;
    }

    /* access modifiers changed from: private */
    public boolean hasSeenSwipeUpOnboarding() {
        return Prefs.getBoolean(this.mContext, "HasSeenRecentsSwipeUpOnboarding", false);
    }

    /* access modifiers changed from: private */
    public void setHasSeenSwipeUpOnboarding(boolean z) {
        Prefs.putBoolean(this.mContext, "HasSeenRecentsSwipeUpOnboarding", z);
        if (z && hasSeenQuickScrubOnboarding()) {
            onDisconnectedFromLauncher();
        }
    }

    /* access modifiers changed from: private */
    public boolean hasSeenQuickScrubOnboarding() {
        return Prefs.getBoolean(this.mContext, "HasSeenRecentsQuickScrubOnboarding", false);
    }

    /* access modifiers changed from: private */
    public void setHasSeenQuickScrubOnboarding(boolean z) {
        Prefs.putBoolean(this.mContext, "HasSeenRecentsQuickScrubOnboarding", z);
        if (z && hasSeenSwipeUpOnboarding()) {
            onDisconnectedFromLauncher();
        }
    }

    /* access modifiers changed from: private */
    public int getDismissedSwipeUpOnboardingCount() {
        return Prefs.getInt(this.mContext, "DismissedRecentsSwipeUpOnboardingCount", 0);
    }

    private void setDismissedSwipeUpOnboardingCount(int i) {
        Prefs.putInt(this.mContext, "DismissedRecentsSwipeUpOnboardingCount", i);
    }

    /* access modifiers changed from: private */
    public boolean hasDismissedQuickScrubOnboardingOnce() {
        return Prefs.getBoolean(this.mContext, "HasDismissedRecentsQuickScrubOnboardingOnce", false);
    }

    /* access modifiers changed from: private */
    public void setHasDismissedQuickScrubOnboardingOnce(boolean z) {
        Prefs.putBoolean(this.mContext, "HasDismissedRecentsQuickScrubOnboardingOnce", z);
    }

    /* access modifiers changed from: private */
    public int getOpenedOverviewFromHomeCount() {
        return Prefs.getInt(this.mContext, "OverviewOpenedFromHomeCount", 0);
    }

    /* access modifiers changed from: private */
    public void incrementOpenedOverviewFromHomeCount() {
        int openedOverviewFromHomeCount = getOpenedOverviewFromHomeCount();
        if (openedOverviewFromHomeCount < 3) {
            setOpenedOverviewFromHomeCount(openedOverviewFromHomeCount + 1);
        }
    }

    private void setOpenedOverviewFromHomeCount(int i) {
        Prefs.putInt(this.mContext, "OverviewOpenedFromHomeCount", i);
    }

    /* access modifiers changed from: private */
    public int getOpenedOverviewCount() {
        return Prefs.getInt(this.mContext, "OverviewOpenedCount", 0);
    }

    /* access modifiers changed from: private */
    public void incrementOpenedOverviewCount() {
        int openedOverviewCount = getOpenedOverviewCount();
        if (openedOverviewCount < 10) {
            setOpenedOverviewCount(openedOverviewCount + 1);
        }
    }

    private void setOpenedOverviewCount(int i) {
        Prefs.putInt(this.mContext, "OverviewOpenedCount", i);
    }
}
