package com.oneplus.lib.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class OPFeaturesUtils {
    private static Method sIsSupport;
    private static Class sOPFeatures;

    static {
        loadFeatures();
    }

    private static void loadFeatures() {
        try {
            sOPFeatures = Class.forName("android.util.OpFeatures");
            sIsSupport = sOPFeatures.getDeclaredMethod("isSupport", new Class[]{int[].class});
        } catch (Exception unused) {
        }
    }

    public static boolean isSupportXVibrate() {
        try {
            if (sOPFeatures == null || sIsSupport == null) {
                loadFeatures();
            }
            Field declaredField = sOPFeatures.getDeclaredField("OP_FEATURE_X_LINEAR_VIBRATION_MOTOR");
            sIsSupport.setAccessible(true);
            declaredField.setAccessible(true);
            return ((Boolean) sIsSupport.invoke(null, new Object[]{new int[]{declaredField.getInt(null)}})).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isSupportZVibrate() {
        try {
            if (sOPFeatures == null || sIsSupport == null) {
                loadFeatures();
            }
            Field declaredField = sOPFeatures.getDeclaredField("OP_FEATURE_Z_VIBRATION_MOTOR");
            sIsSupport.setAccessible(true);
            declaredField.setAccessible(true);
            return ((Boolean) sIsSupport.invoke(null, new Object[]{new int[]{declaredField.getInt(null)}})).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }
}
