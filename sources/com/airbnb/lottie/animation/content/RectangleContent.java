package com.airbnb.lottie.animation.content;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation.AnimationListener;
import com.airbnb.lottie.animation.keyframe.FloatKeyframeAnimation;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.model.content.RectangleShape;
import com.airbnb.lottie.model.content.ShapeTrimPath.Type;
import com.airbnb.lottie.model.layer.BaseLayer;
import com.airbnb.lottie.utils.MiscUtils;
import com.airbnb.lottie.value.LottieValueCallback;
import java.util.List;

public class RectangleContent implements AnimationListener, KeyPathElementContent, PathContent {
    private final BaseKeyframeAnimation<?, Float> cornerRadiusAnimation;
    private final boolean hidden;
    private boolean isPathValid;
    private final LottieDrawable lottieDrawable;
    private final String name;
    private final Path path = new Path();
    private final BaseKeyframeAnimation<?, PointF> positionAnimation;
    private final RectF rect = new RectF();
    private final BaseKeyframeAnimation<?, PointF> sizeAnimation;
    private CompoundTrimPathContent trimPaths = new CompoundTrimPathContent();

    public RectangleContent(LottieDrawable lottieDrawable2, BaseLayer baseLayer, RectangleShape rectangleShape) {
        this.name = rectangleShape.getName();
        this.hidden = rectangleShape.isHidden();
        this.lottieDrawable = lottieDrawable2;
        this.positionAnimation = rectangleShape.getPosition().createAnimation();
        this.sizeAnimation = rectangleShape.getSize().createAnimation();
        this.cornerRadiusAnimation = rectangleShape.getCornerRadius().createAnimation();
        baseLayer.addAnimation(this.positionAnimation);
        baseLayer.addAnimation(this.sizeAnimation);
        baseLayer.addAnimation(this.cornerRadiusAnimation);
        this.positionAnimation.addUpdateListener(this);
        this.sizeAnimation.addUpdateListener(this);
        this.cornerRadiusAnimation.addUpdateListener(this);
    }

    public String getName() {
        return this.name;
    }

    public void onValueChanged() {
        invalidate();
    }

    private void invalidate() {
        this.isPathValid = false;
        this.lottieDrawable.invalidateSelf();
    }

    public void setContents(List<Content> list, List<Content> list2) {
        for (int i = 0; i < list.size(); i++) {
            Content content = (Content) list.get(i);
            if (content instanceof TrimPathContent) {
                TrimPathContent trimPathContent = (TrimPathContent) content;
                if (trimPathContent.getType() == Type.SIMULTANEOUSLY) {
                    this.trimPaths.addTrimPath(trimPathContent);
                    trimPathContent.addListener(this);
                }
            }
        }
    }

    public Path getPath() {
        float f;
        if (this.isPathValid) {
            return this.path;
        }
        this.path.reset();
        if (this.hidden) {
            this.isPathValid = true;
            return this.path;
        }
        PointF pointF = (PointF) this.sizeAnimation.getValue();
        float f2 = pointF.x / 2.0f;
        float f3 = pointF.y / 2.0f;
        BaseKeyframeAnimation<?, Float> baseKeyframeAnimation = this.cornerRadiusAnimation;
        if (baseKeyframeAnimation == null) {
            f = 0.0f;
        } else {
            f = ((FloatKeyframeAnimation) baseKeyframeAnimation).getFloatValue();
        }
        float min = Math.min(f2, f3);
        if (f > min) {
            f = min;
        }
        PointF pointF2 = (PointF) this.positionAnimation.getValue();
        this.path.moveTo(pointF2.x + f2, (pointF2.y - f3) + f);
        this.path.lineTo(pointF2.x + f2, (pointF2.y + f3) - f);
        int i = (f > 0.0f ? 1 : (f == 0.0f ? 0 : -1));
        if (i > 0) {
            RectF rectF = this.rect;
            float f4 = pointF2.x;
            float f5 = f * 2.0f;
            float f6 = (f4 + f2) - f5;
            float f7 = pointF2.y;
            rectF.set(f6, (f7 + f3) - f5, f4 + f2, f7 + f3);
            this.path.arcTo(this.rect, 0.0f, 90.0f, false);
        }
        this.path.lineTo((pointF2.x - f2) + f, pointF2.y + f3);
        if (i > 0) {
            RectF rectF2 = this.rect;
            float f8 = pointF2.x;
            float f9 = f8 - f2;
            float f10 = pointF2.y;
            float f11 = f * 2.0f;
            rectF2.set(f9, (f10 + f3) - f11, (f8 - f2) + f11, f10 + f3);
            this.path.arcTo(this.rect, 90.0f, 90.0f, false);
        }
        this.path.lineTo(pointF2.x - f2, (pointF2.y - f3) + f);
        if (i > 0) {
            RectF rectF3 = this.rect;
            float f12 = pointF2.x;
            float f13 = f12 - f2;
            float f14 = pointF2.y;
            float f15 = f * 2.0f;
            rectF3.set(f13, f14 - f3, (f12 - f2) + f15, (f14 - f3) + f15);
            this.path.arcTo(this.rect, 180.0f, 90.0f, false);
        }
        this.path.lineTo((pointF2.x + f2) - f, pointF2.y - f3);
        if (i > 0) {
            RectF rectF4 = this.rect;
            float f16 = pointF2.x;
            float f17 = f * 2.0f;
            float f18 = (f16 + f2) - f17;
            float f19 = pointF2.y;
            rectF4.set(f18, f19 - f3, f16 + f2, (f19 - f3) + f17);
            this.path.arcTo(this.rect, 270.0f, 90.0f, false);
        }
        this.path.close();
        this.trimPaths.apply(this.path);
        this.isPathValid = true;
        return this.path;
    }

    public void resolveKeyPath(KeyPath keyPath, int i, List<KeyPath> list, KeyPath keyPath2) {
        MiscUtils.resolveKeyPath(keyPath, i, list, keyPath2, this);
    }

    public <T> void addValueCallback(T t, LottieValueCallback<T> lottieValueCallback) {
        if (t == LottieProperty.RECTANGLE_SIZE) {
            this.sizeAnimation.setValueCallback(lottieValueCallback);
        } else if (t == LottieProperty.POSITION) {
            this.positionAnimation.setValueCallback(lottieValueCallback);
        } else if (t == LottieProperty.CORNER_RADIUS) {
            this.cornerRadiusAnimation.setValueCallback(lottieValueCallback);
        }
    }
}
