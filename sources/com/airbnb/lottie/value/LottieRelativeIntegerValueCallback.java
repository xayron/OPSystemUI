package com.airbnb.lottie.value;

import com.airbnb.lottie.utils.MiscUtils;

public class LottieRelativeIntegerValueCallback extends LottieValueCallback<Integer> {
    public Integer getValue(LottieFrameInfo<Integer> lottieFrameInfo) {
        return Integer.valueOf(MiscUtils.lerp(((Integer) lottieFrameInfo.getStartValue()).intValue(), ((Integer) lottieFrameInfo.getEndValue()).intValue(), lottieFrameInfo.getInterpolatedKeyframeProgress()) + getOffset(lottieFrameInfo).intValue());
    }

    public Integer getOffset(LottieFrameInfo<Integer> lottieFrameInfo) {
        T t = this.value;
        if (t != null) {
            return (Integer) t;
        }
        throw new IllegalArgumentException("You must provide a static value in the constructor , call setValue, or override getValue.");
    }
}
