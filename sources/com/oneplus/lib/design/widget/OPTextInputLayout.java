package com.oneplus.lib.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.oneplus.commonctrl.R$color;
import com.oneplus.commonctrl.R$id;
import com.oneplus.commonctrl.R$layout;
import com.oneplus.commonctrl.R$string;
import com.oneplus.commonctrl.R$style;
import com.oneplus.commonctrl.R$styleable;
import com.oneplus.lib.util.AnimatorUtils;
import com.oneplus.support.core.content.ContextCompat;
import com.oneplus.support.core.graphics.drawable.DrawableCompat;
import com.oneplus.support.core.view.AbsSavedState;
import com.oneplus.support.core.view.AccessibilityDelegateCompat;
import com.oneplus.support.core.view.ViewCompat;
import com.oneplus.support.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.oneplus.support.core.widget.Space;
import com.oneplus.support.core.widget.TextViewCompat;

public class OPTextInputLayout extends LinearLayout {
    private ValueAnimator mAnimator;
    final CollapsingTextHelper mCollapsingTextHelper;
    boolean mCounterEnabled;
    private int mCounterMaxLength;
    private int mCounterOverflowTextAppearance;
    private boolean mCounterOverflowed;
    private int mCounterTextAppearance;
    private TextView mCounterView;
    private ColorStateList mDefaultTextColor;
    EditText mEditText;
    private CharSequence mError;
    private boolean mErrorEnabled;
    private boolean mErrorShown;
    private int mErrorTextAppearance;
    TextView mErrorView;
    private ColorStateList mFocusedTextColor;
    private boolean mHasPasswordToggleTintList;
    private boolean mHasPasswordToggleTintMode;
    private boolean mHasReconstructedEditTextBackground;
    private CharSequence mHint;
    private boolean mHintAnimationEnabled;
    private boolean mHintEnabled;
    private boolean mHintExpanded;
    private boolean mInDrawableStateChanged;
    private LinearLayout mIndicatorArea;
    private int mIndicatorsAdded;
    private final FrameLayout mInputFrame;
    private Drawable mOriginalEditTextEndDrawable;
    private CharSequence mOriginalHint;
    private CharSequence mPasswordToggleContentDesc;
    private Drawable mPasswordToggleDrawable;
    private Drawable mPasswordToggleDummyDrawable;
    private boolean mPasswordToggleEnabled;
    private ColorStateList mPasswordToggleTintList;
    private Mode mPasswordToggleTintMode;
    private OPCheckableImageButton mPasswordToggleView;
    private boolean mPasswordToggledVisible;
    /* access modifiers changed from: private */
    public boolean mRestoringSavedState;
    private Paint mTmpPaint;
    private final Rect mTmpRect;
    private Typeface mTypeface;

    static class SavedState extends AbsSavedState {
        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, null);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        CharSequence error;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.error = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            TextUtils.writeToParcel(this.error, parcel, i);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("TextInputLayout.SavedState{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" error=");
            sb.append(this.error);
            sb.append("}");
            return sb.toString();
        }
    }

    private class TextInputAccessibilityDelegate extends AccessibilityDelegateCompat {
        TextInputAccessibilityDelegate() {
        }

        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName(OPTextInputLayout.class.getSimpleName());
        }

        public void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onPopulateAccessibilityEvent(view, accessibilityEvent);
            CharSequence text = OPTextInputLayout.this.mCollapsingTextHelper.getText();
            if (!TextUtils.isEmpty(text)) {
                accessibilityEvent.getText().add(text);
            }
        }

        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            accessibilityNodeInfoCompat.setClassName(OPTextInputLayout.class.getSimpleName());
            CharSequence text = OPTextInputLayout.this.mCollapsingTextHelper.getText();
            if (!TextUtils.isEmpty(text)) {
                accessibilityNodeInfoCompat.setText(text);
            }
            EditText editText = OPTextInputLayout.this.mEditText;
            if (editText != null) {
                accessibilityNodeInfoCompat.setLabelFor(editText);
            }
            TextView textView = OPTextInputLayout.this.mErrorView;
            CharSequence text2 = textView != null ? textView.getText() : null;
            if (!TextUtils.isEmpty(text2)) {
                accessibilityNodeInfoCompat.setContentInvalid(true);
                accessibilityNodeInfoCompat.setError(text2);
            }
        }
    }

    public OPTextInputLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OPTextInputLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet);
        this.mTmpRect = new Rect();
        this.mCollapsingTextHelper = new CollapsingTextHelper(this);
        setOrientation(1);
        setWillNotDraw(false);
        setAddStatesFromChildren(true);
        this.mInputFrame = new FrameLayout(context);
        this.mInputFrame.setAddStatesFromChildren(true);
        addView(this.mInputFrame);
        this.mCollapsingTextHelper.setTextSizeInterpolator(OPAnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        this.mCollapsingTextHelper.setPositionInterpolator(new AccelerateInterpolator());
        this.mCollapsingTextHelper.setCollapsedTextGravity(8388659);
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.OPTextInputLayout, i, R$style.Widget_Design_OPTextInputLayout);
        this.mHintEnabled = obtainStyledAttributes.getBoolean(R$styleable.OPTextInputLayout_opHintEnabled, true);
        setHint(obtainStyledAttributes.getText(R$styleable.OPTextInputLayout_android_hint));
        this.mHintAnimationEnabled = obtainStyledAttributes.getBoolean(R$styleable.OPTextInputLayout_opHintAnimationEnabled, true);
        if (obtainStyledAttributes.hasValue(R$styleable.OPTextInputLayout_android_textColorHint)) {
            ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R$styleable.OPTextInputLayout_android_textColorHint);
            this.mFocusedTextColor = colorStateList;
            this.mDefaultTextColor = colorStateList;
        }
        if (obtainStyledAttributes.getResourceId(R$styleable.OPTextInputLayout_opHintTextAppearance, 0) != 0) {
            setHintTextAppearance(obtainStyledAttributes.getResourceId(R$styleable.OPTextInputLayout_opHintTextAppearance, 0));
        }
        this.mErrorTextAppearance = obtainStyledAttributes.getResourceId(R$styleable.OPTextInputLayout_opErrorTextAppearance, 0);
        boolean z = obtainStyledAttributes.getBoolean(R$styleable.OPTextInputLayout_opErrorEnabled, false);
        boolean z2 = obtainStyledAttributes.getBoolean(R$styleable.OPTextInputLayout_opCounterEnabled, false);
        setCounterMaxLength(obtainStyledAttributes.getInt(R$styleable.OPTextInputLayout_opCounterMaxLength, -1));
        this.mCounterTextAppearance = obtainStyledAttributes.getResourceId(R$styleable.OPTextInputLayout_opCounterTextAppearance, 0);
        this.mCounterOverflowTextAppearance = obtainStyledAttributes.getResourceId(R$styleable.OPTextInputLayout_opCounterOverflowTextAppearance, 0);
        this.mPasswordToggleEnabled = obtainStyledAttributes.getBoolean(R$styleable.OPTextInputLayout_opPasswordToggleEnabled, false);
        this.mPasswordToggleDrawable = obtainStyledAttributes.getDrawable(R$styleable.OPTextInputLayout_opPasswordToggleDrawable);
        this.mPasswordToggleContentDesc = obtainStyledAttributes.getText(R$styleable.OPTextInputLayout_opPasswordToggleContentDescription);
        if (obtainStyledAttributes.hasValue(R$styleable.OPTextInputLayout_opPasswordToggleTint)) {
            this.mHasPasswordToggleTintList = true;
            this.mPasswordToggleTintList = obtainStyledAttributes.getColorStateList(R$styleable.OPTextInputLayout_opPasswordToggleTint);
        }
        if (obtainStyledAttributes.hasValue(R$styleable.OPTextInputLayout_opPasswordToggleTintMode)) {
            this.mHasPasswordToggleTintMode = true;
            this.mPasswordToggleTintMode = OPViewUtils.parseTintMode(obtainStyledAttributes.getInt(R$styleable.OPTextInputLayout_opPasswordToggleTintMode, -1), null);
        }
        obtainStyledAttributes.recycle();
        setErrorEnabled(z);
        setCounterEnabled(z2);
        applyPasswordToggleTint();
        if (ViewCompat.getImportantForAccessibility(this) == 0) {
            ViewCompat.setImportantForAccessibility(this, 1);
        }
        ViewCompat.setAccessibilityDelegate(this, new TextInputAccessibilityDelegate());
    }

    public void addView(View view, int i, LayoutParams layoutParams) {
        if (view instanceof EditText) {
            FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(layoutParams);
            layoutParams2.gravity = (layoutParams2.gravity & -113) | 16;
            this.mInputFrame.addView(view, layoutParams2);
            this.mInputFrame.setLayoutParams(layoutParams);
            updateInputLayoutMargins();
            setEditText((EditText) view);
            return;
        }
        super.addView(view, i, layoutParams);
    }

    public void dispatchProvideAutofillStructure(ViewStructure viewStructure, int i) {
        if (this.mOriginalHint != null) {
            EditText editText = this.mEditText;
            if (editText != null) {
                CharSequence hint = editText.getHint();
                this.mEditText.setHint(this.mOriginalHint);
                try {
                    super.dispatchProvideAutofillStructure(viewStructure, i);
                    return;
                } finally {
                    this.mEditText.setHint(hint);
                }
            }
        }
        super.dispatchProvideAutofillStructure(viewStructure, i);
    }

    private void setEditText(EditText editText) {
        if (this.mEditText == null) {
            if (!(editText instanceof OPTextInputEditText)) {
                Log.i("TextInputLayout", "EditText added is not a OPTextInputEditText. Please switch to using that class instead.");
            }
            this.mEditText = editText;
            this.mEditText.setTypeface(Typeface.DEFAULT);
            if (!hasPasswordTransformation()) {
                this.mCollapsingTextHelper.setTypefaces(this.mEditText.getTypeface());
            }
            this.mCollapsingTextHelper.setExpandedTextSize(this.mEditText.getTextSize());
            int gravity = this.mEditText.getGravity();
            this.mCollapsingTextHelper.setCollapsedTextGravity((gravity & -113) | 48);
            this.mCollapsingTextHelper.setExpandedTextGravity(gravity);
            this.mEditText.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    OPTextInputLayout oPTextInputLayout = OPTextInputLayout.this;
                    oPTextInputLayout.updateLabelState(!oPTextInputLayout.mRestoringSavedState);
                    OPTextInputLayout oPTextInputLayout2 = OPTextInputLayout.this;
                    if (oPTextInputLayout2.mCounterEnabled) {
                        oPTextInputLayout2.updateCounter(editable.length());
                    }
                }
            });
            if (this.mDefaultTextColor == null) {
                this.mDefaultTextColor = this.mEditText.getHintTextColors();
            }
            if (this.mHintEnabled && TextUtils.isEmpty(this.mHint)) {
                this.mOriginalHint = this.mEditText.getHint();
                setHint(this.mOriginalHint);
                this.mEditText.setHint(null);
            }
            if (this.mCounterView != null) {
                updateCounter(this.mEditText.getText().length());
            }
            if (this.mIndicatorArea != null) {
                adjustIndicatorPadding();
            }
            updatePasswordToggleView();
            updateLabelState(false, true);
            return;
        }
        throw new IllegalArgumentException("We already have an EditText, can only have one");
    }

    private void updateInputLayoutMargins() {
        int i;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mInputFrame.getLayoutParams();
        if (this.mHintEnabled) {
            if (this.mTmpPaint == null) {
                this.mTmpPaint = new Paint();
            }
            this.mTmpPaint.setTypeface(this.mCollapsingTextHelper.getCollapsedTypeface());
            this.mTmpPaint.setTextSize(this.mCollapsingTextHelper.getCollapsedTextSize());
            i = (int) (-this.mTmpPaint.ascent());
        } else {
            i = 0;
        }
        if (i != layoutParams.topMargin) {
            layoutParams.topMargin = i;
            this.mInputFrame.requestLayout();
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateLabelState(boolean z) {
        updateLabelState(z, false);
    }

    /* access modifiers changed from: 0000 */
    public void updateLabelState(boolean z, boolean z2) {
        boolean isEnabled = isEnabled();
        EditText editText = this.mEditText;
        boolean z3 = editText != null && !TextUtils.isEmpty(editText.getText());
        boolean arrayContains = arrayContains(getDrawableState(), 16842908);
        boolean isEmpty = true ^ TextUtils.isEmpty(getError());
        ColorStateList colorStateList = this.mDefaultTextColor;
        if (colorStateList != null) {
            this.mCollapsingTextHelper.setExpandedTextColor(colorStateList);
        }
        if (isEnabled && this.mCounterOverflowed) {
            TextView textView = this.mCounterView;
            if (textView != null) {
                this.mCollapsingTextHelper.setCollapsedTextColor(textView.getTextColors());
                if (!z3 || (isEnabled() && (arrayContains || isEmpty))) {
                    if (!z2 || this.mHintExpanded) {
                        collapseHint(z);
                    }
                    return;
                } else if (z2 || !this.mHintExpanded) {
                    expandHint(z);
                    return;
                } else {
                    return;
                }
            }
        }
        if (!isEnabled || !arrayContains || this.mFocusedTextColor == null) {
            ColorStateList colorStateList2 = this.mDefaultTextColor;
            if (colorStateList2 != null) {
                this.mCollapsingTextHelper.setCollapsedTextColor(colorStateList2);
            }
        }
        if (!z3) {
        }
        if (!z2) {
        }
        collapseHint(z);
    }

    public void setHint(CharSequence charSequence) {
        if (this.mHintEnabled) {
            setHintInternal(charSequence);
            sendAccessibilityEvent(2048);
        }
    }

    private void setHintInternal(CharSequence charSequence) {
        this.mHint = charSequence;
        this.mCollapsingTextHelper.setText(charSequence);
    }

    public CharSequence getHint() {
        if (this.mHintEnabled) {
            return this.mHint;
        }
        return null;
    }

    public void setHintTextAppearance(int i) {
        this.mCollapsingTextHelper.setCollapsedTextAppearance(i);
        this.mFocusedTextColor = this.mCollapsingTextHelper.getCollapsedTextColor();
        if (this.mEditText != null) {
            updateLabelState(false);
            updateInputLayoutMargins();
        }
    }

    private void addIndicator(TextView textView, int i) {
        if (this.mIndicatorArea == null) {
            this.mIndicatorArea = new LinearLayout(getContext());
            this.mIndicatorArea.setOrientation(0);
            addView(this.mIndicatorArea, -1, -2);
            this.mIndicatorArea.addView(new Space(getContext()), new LinearLayout.LayoutParams(0, 0, 1.0f));
            if (this.mEditText != null) {
                adjustIndicatorPadding();
            }
        }
        this.mIndicatorArea.setVisibility(0);
        this.mIndicatorArea.addView(textView, i);
        this.mIndicatorsAdded++;
    }

    private void adjustIndicatorPadding() {
        ViewCompat.setPaddingRelative(this.mIndicatorArea, ViewCompat.getPaddingStart(this.mEditText), 0, ViewCompat.getPaddingEnd(this.mEditText), this.mEditText.getPaddingBottom());
    }

    private void removeIndicator(TextView textView) {
        LinearLayout linearLayout = this.mIndicatorArea;
        if (linearLayout != null) {
            linearLayout.removeView(textView);
            int i = this.mIndicatorsAdded - 1;
            this.mIndicatorsAdded = i;
            if (i == 0) {
                this.mIndicatorArea.setVisibility(8);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0050  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setErrorEnabled(boolean r6) {
        /*
            r5 = this;
            boolean r0 = r5.mErrorEnabled
            if (r0 == r6) goto L_0x0087
            android.widget.TextView r0 = r5.mErrorView
            if (r0 == 0) goto L_0x000f
            android.view.ViewPropertyAnimator r0 = r0.animate()
            r0.cancel()
        L_0x000f:
            r0 = 0
            if (r6 == 0) goto L_0x0078
            android.widget.TextView r1 = new android.widget.TextView
            android.content.Context r2 = r5.getContext()
            r1.<init>(r2)
            r5.mErrorView = r1
            android.widget.TextView r1 = r5.mErrorView
            int r2 = com.oneplus.commonctrl.R$id.op_textinput_error
            r1.setId(r2)
            android.graphics.Typeface r1 = r5.mTypeface
            if (r1 == 0) goto L_0x002d
            android.widget.TextView r2 = r5.mErrorView
            r2.setTypeface(r1)
        L_0x002d:
            r1 = 1
            android.widget.TextView r2 = r5.mErrorView     // Catch:{ Exception -> 0x004d }
            int r3 = r5.mErrorTextAppearance     // Catch:{ Exception -> 0x004d }
            com.oneplus.support.core.widget.TextViewCompat.setTextAppearance(r2, r3)     // Catch:{ Exception -> 0x004d }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x004d }
            r3 = 23
            if (r2 < r3) goto L_0x004b
            android.widget.TextView r2 = r5.mErrorView     // Catch:{ Exception -> 0x004d }
            android.content.res.ColorStateList r2 = r2.getTextColors()     // Catch:{ Exception -> 0x004d }
            int r2 = r2.getDefaultColor()     // Catch:{ Exception -> 0x004d }
            r3 = -65281(0xffffffffffff00ff, float:NaN)
            if (r2 != r3) goto L_0x004b
            goto L_0x004d
        L_0x004b:
            r2 = r0
            goto L_0x004e
        L_0x004d:
            r2 = r1
        L_0x004e:
            if (r2 == 0) goto L_0x0067
            android.widget.TextView r2 = r5.mErrorView
            r3 = 16974321(0x10301f1, float:2.4062293E-38)
            com.oneplus.support.core.widget.TextViewCompat.setTextAppearance(r2, r3)
            android.widget.TextView r2 = r5.mErrorView
            android.content.Context r3 = r5.getContext()
            int r4 = com.oneplus.commonctrl.R$color.op_error_color_material_default
            int r3 = com.oneplus.support.core.content.ContextCompat.getColor(r3, r4)
            r2.setTextColor(r3)
        L_0x0067:
            android.widget.TextView r2 = r5.mErrorView
            r3 = 4
            r2.setVisibility(r3)
            android.widget.TextView r2 = r5.mErrorView
            com.oneplus.support.core.view.ViewCompat.setAccessibilityLiveRegion(r2, r1)
            android.widget.TextView r1 = r5.mErrorView
            r5.addIndicator(r1, r0)
            goto L_0x0085
        L_0x0078:
            r5.mErrorShown = r0
            r5.updateEditTextBackground()
            android.widget.TextView r0 = r5.mErrorView
            r5.removeIndicator(r0)
            r0 = 0
            r5.mErrorView = r0
        L_0x0085:
            r5.mErrorEnabled = r6
        L_0x0087:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.lib.design.widget.OPTextInputLayout.setErrorEnabled(boolean):void");
    }

    public void setError(CharSequence charSequence) {
        boolean z;
        if (ViewCompat.isLaidOut(this) && isEnabled()) {
            TextView textView = this.mErrorView;
            if (textView == null || !TextUtils.equals(textView.getText(), charSequence)) {
                z = true;
                setError(charSequence, z, true);
            }
        }
        z = false;
        setError(charSequence, z, true);
    }

    private void setError(final CharSequence charSequence, boolean z, boolean z2) {
        this.mError = charSequence;
        if (!this.mErrorEnabled) {
            if (!TextUtils.isEmpty(charSequence)) {
                setErrorEnabled(true);
            } else {
                return;
            }
        }
        this.mErrorShown = !TextUtils.isEmpty(charSequence);
        this.mErrorView.animate().cancel();
        if (this.mErrorShown) {
            this.mErrorView.setText(charSequence);
            this.mErrorView.setVisibility(0);
            if (z2) {
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mErrorView, View.TRANSLATION_X, new float[]{0.0f, 15.0f, 0.0f});
                ofFloat.setDuration(30).setInterpolator(AnimatorUtils.FastOutLinearInInterpolatorSine);
                ofFloat.setRepeatCount(4);
                ofFloat.start();
            }
            if (z) {
                if (this.mErrorView.getAlpha() == 1.0f) {
                    this.mErrorView.setAlpha(0.0f);
                }
                this.mErrorView.animate().alpha(1.0f).setDuration(200).setInterpolator(AnimatorUtils.LinearOutSlowInInterpolator).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        OPTextInputLayout.this.mErrorView.setVisibility(0);
                    }
                }).start();
            } else {
                this.mErrorView.setAlpha(1.0f);
            }
        } else if (this.mErrorView.getVisibility() == 0) {
            if (z) {
                this.mErrorView.animate().alpha(0.0f).setDuration(200).setInterpolator(AnimatorUtils.FastOutLinearInInterpolator).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        OPTextInputLayout.this.mErrorView.setText(charSequence);
                        OPTextInputLayout.this.mErrorView.setVisibility(4);
                    }
                }).start();
            } else {
                this.mErrorView.setText(charSequence);
                this.mErrorView.setVisibility(4);
            }
        }
        updateEditTextBackground();
        updateLabelState(z);
    }

    public void setCounterEnabled(boolean z) {
        if (this.mCounterEnabled != z) {
            if (z) {
                this.mCounterView = new TextView(getContext());
                this.mCounterView.setId(R$id.op_textinput_counter);
                Typeface typeface = this.mTypeface;
                if (typeface != null) {
                    this.mCounterView.setTypeface(typeface);
                }
                this.mCounterView.setMaxLines(1);
                try {
                    TextViewCompat.setTextAppearance(this.mCounterView, this.mCounterTextAppearance);
                } catch (Exception unused) {
                    TextViewCompat.setTextAppearance(this.mCounterView, 16974321);
                    this.mCounterView.setTextColor(ContextCompat.getColor(getContext(), R$color.op_error_color_material_default));
                }
                addIndicator(this.mCounterView, -1);
                EditText editText = this.mEditText;
                if (editText == null) {
                    updateCounter(0);
                } else {
                    updateCounter(editText.getText().length());
                }
            } else {
                removeIndicator(this.mCounterView);
                this.mCounterView = null;
            }
            this.mCounterEnabled = z;
        }
    }

    public void setCounterMaxLength(int i) {
        if (this.mCounterMaxLength != i) {
            if (i > 0) {
                this.mCounterMaxLength = i;
            } else {
                this.mCounterMaxLength = -1;
            }
            if (this.mCounterEnabled) {
                EditText editText = this.mEditText;
                updateCounter(editText == null ? 0 : editText.getText().length());
            }
        }
    }

    public void setEnabled(boolean z) {
        recursiveSetEnabled(this, z);
        super.setEnabled(z);
    }

    private static void recursiveSetEnabled(ViewGroup viewGroup, boolean z) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            childAt.setEnabled(z);
            if (childAt instanceof ViewGroup) {
                recursiveSetEnabled((ViewGroup) childAt, z);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateCounter(int i) {
        boolean z = this.mCounterOverflowed;
        int i2 = this.mCounterMaxLength;
        if (i2 == -1) {
            this.mCounterView.setText(String.valueOf(i));
            this.mCounterOverflowed = false;
        } else {
            this.mCounterOverflowed = i > i2;
            boolean z2 = this.mCounterOverflowed;
            if (z != z2) {
                TextViewCompat.setTextAppearance(this.mCounterView, z2 ? this.mCounterOverflowTextAppearance : this.mCounterTextAppearance);
            }
            this.mCounterView.setText(getContext().getString(R$string.op_character_counter_pattern, new Object[]{Integer.valueOf(i), Integer.valueOf(this.mCounterMaxLength)}));
        }
        if (this.mEditText != null && z != this.mCounterOverflowed) {
            updateLabelState(false);
            updateEditTextBackground();
        }
    }

    private void updateEditTextBackground() {
        EditText editText = this.mEditText;
        if (editText != null) {
            Drawable background = editText.getBackground();
            if (background != null) {
                ensureBackgroundDrawableStateWorkaround();
                if (OPDrawableUtils.canSafelyMutateDrawable(background)) {
                    background = background.mutate();
                }
                if ((!this.mErrorShown || this.mErrorView == null) && (!this.mCounterOverflowed || this.mCounterView == null)) {
                    DrawableCompat.clearColorFilter(background);
                    this.mEditText.refreshDrawableState();
                }
            }
        }
    }

    private void ensureBackgroundDrawableStateWorkaround() {
        int i = VERSION.SDK_INT;
        if (i == 21 || i == 22) {
            Drawable background = this.mEditText.getBackground();
            if (background != null && !this.mHasReconstructedEditTextBackground) {
                Drawable newDrawable = background.getConstantState().newDrawable();
                if (background instanceof DrawableContainer) {
                    this.mHasReconstructedEditTextBackground = OPDrawableUtils.setContainerConstantState((DrawableContainer) background, newDrawable.getConstantState());
                }
                if (!this.mHasReconstructedEditTextBackground) {
                    ViewCompat.setBackground(this.mEditText, newDrawable);
                    this.mHasReconstructedEditTextBackground = true;
                }
            }
        }
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        if (this.mErrorShown) {
            savedState.error = getError();
        }
        return savedState;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        setError(savedState.error);
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        this.mRestoringSavedState = true;
        super.dispatchRestoreInstanceState(sparseArray);
        this.mRestoringSavedState = false;
    }

    public CharSequence getError() {
        if (this.mErrorEnabled) {
            return this.mError;
        }
        return null;
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mHintEnabled) {
            this.mCollapsingTextHelper.draw(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        updatePasswordToggleView();
        super.onMeasure(i, i2);
    }

    private void updatePasswordToggleView() {
        if (this.mEditText != null) {
            if (shouldShowPasswordIcon()) {
                if (this.mPasswordToggleView == null) {
                    this.mPasswordToggleView = (OPCheckableImageButton) LayoutInflater.from(getContext()).inflate(R$layout.op_design_text_input_password_icon, this.mInputFrame, false);
                    this.mPasswordToggleView.setImageDrawable(this.mPasswordToggleDrawable);
                    this.mPasswordToggleView.setContentDescription(this.mPasswordToggleContentDesc);
                    this.mInputFrame.addView(this.mPasswordToggleView);
                    this.mPasswordToggleView.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            OPTextInputLayout.this.passwordVisibilityToggleRequested();
                        }
                    });
                }
                EditText editText = this.mEditText;
                if (editText != null && ViewCompat.getMinimumHeight(editText) <= 0) {
                    this.mEditText.setMinimumHeight(ViewCompat.getMinimumHeight(this.mPasswordToggleView));
                }
                this.mPasswordToggleView.setVisibility(0);
                this.mPasswordToggleView.setChecked(this.mPasswordToggledVisible);
                if (this.mPasswordToggleDummyDrawable == null) {
                    this.mPasswordToggleDummyDrawable = new ColorDrawable();
                }
                this.mPasswordToggleDummyDrawable.setBounds(0, 0, this.mPasswordToggleView.getMeasuredWidth(), 1);
                Drawable[] compoundDrawablesRelative = TextViewCompat.getCompoundDrawablesRelative(this.mEditText);
                if (compoundDrawablesRelative[2] != this.mPasswordToggleDummyDrawable) {
                    this.mOriginalEditTextEndDrawable = compoundDrawablesRelative[2];
                }
                TextViewCompat.setCompoundDrawablesRelative(this.mEditText, compoundDrawablesRelative[0], compoundDrawablesRelative[1], this.mPasswordToggleDummyDrawable, compoundDrawablesRelative[3]);
                this.mPasswordToggleView.setPadding(this.mEditText.getPaddingLeft(), this.mEditText.getPaddingTop(), this.mEditText.getPaddingRight(), this.mEditText.getPaddingBottom());
            } else {
                OPCheckableImageButton oPCheckableImageButton = this.mPasswordToggleView;
                if (oPCheckableImageButton != null && oPCheckableImageButton.getVisibility() == 0) {
                    this.mPasswordToggleView.setVisibility(8);
                }
                if (this.mPasswordToggleDummyDrawable != null) {
                    Drawable[] compoundDrawablesRelative2 = TextViewCompat.getCompoundDrawablesRelative(this.mEditText);
                    if (compoundDrawablesRelative2[2] == this.mPasswordToggleDummyDrawable) {
                        TextViewCompat.setCompoundDrawablesRelative(this.mEditText, compoundDrawablesRelative2[0], compoundDrawablesRelative2[1], this.mOriginalEditTextEndDrawable, compoundDrawablesRelative2[3]);
                        this.mPasswordToggleDummyDrawable = null;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void passwordVisibilityToggleRequested() {
        if (this.mPasswordToggleEnabled) {
            int selectionEnd = this.mEditText.getSelectionEnd();
            if (hasPasswordTransformation()) {
                this.mEditText.setTransformationMethod(null);
                this.mPasswordToggledVisible = true;
            } else {
                this.mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.mPasswordToggledVisible = false;
            }
            this.mPasswordToggleView.setChecked(this.mPasswordToggledVisible);
            this.mEditText.setSelection(selectionEnd);
        }
    }

    private boolean hasPasswordTransformation() {
        EditText editText = this.mEditText;
        return editText != null && (editText.getTransformationMethod() instanceof PasswordTransformationMethod);
    }

    private boolean shouldShowPasswordIcon() {
        return this.mPasswordToggleEnabled && (hasPasswordTransformation() || this.mPasswordToggledVisible);
    }

    private void applyPasswordToggleTint() {
        if (this.mPasswordToggleDrawable == null) {
            return;
        }
        if (this.mHasPasswordToggleTintList || this.mHasPasswordToggleTintMode) {
            this.mPasswordToggleDrawable = DrawableCompat.wrap(this.mPasswordToggleDrawable).mutate();
            if (this.mHasPasswordToggleTintList) {
                DrawableCompat.setTintList(this.mPasswordToggleDrawable, this.mPasswordToggleTintList);
            }
            if (this.mHasPasswordToggleTintMode) {
                DrawableCompat.setTintMode(this.mPasswordToggleDrawable, this.mPasswordToggleTintMode);
            }
            OPCheckableImageButton oPCheckableImageButton = this.mPasswordToggleView;
            if (oPCheckableImageButton != null) {
                Drawable drawable = oPCheckableImageButton.getDrawable();
                Drawable drawable2 = this.mPasswordToggleDrawable;
                if (drawable != drawable2) {
                    this.mPasswordToggleView.setImageDrawable(drawable2);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mHintEnabled) {
            EditText editText = this.mEditText;
            if (editText != null) {
                Rect rect = this.mTmpRect;
                OPViewGroupUtils.getDescendantRect(this, editText, rect);
                int compoundPaddingLeft = rect.left + this.mEditText.getCompoundPaddingLeft();
                int compoundPaddingRight = rect.right - this.mEditText.getCompoundPaddingRight();
                this.mCollapsingTextHelper.setExpandedBounds(compoundPaddingLeft, rect.top + this.mEditText.getCompoundPaddingTop(), compoundPaddingRight, rect.bottom - this.mEditText.getCompoundPaddingBottom());
                this.mCollapsingTextHelper.setCollapsedBounds(compoundPaddingLeft, getPaddingTop(), compoundPaddingRight, (i4 - i2) - getPaddingBottom());
                this.mCollapsingTextHelper.recalculate();
            }
        }
    }

    private void collapseHint(boolean z) {
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.mAnimator.cancel();
        }
        if (!z || !this.mHintAnimationEnabled) {
            this.mCollapsingTextHelper.setExpansionFraction(1.0f);
        } else {
            animateToExpansionFraction(1.0f);
        }
        this.mHintExpanded = false;
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        if (!this.mInDrawableStateChanged) {
            boolean z = true;
            this.mInDrawableStateChanged = true;
            super.drawableStateChanged();
            int[] drawableState = getDrawableState();
            if (!ViewCompat.isLaidOut(this) || !isEnabled()) {
                z = false;
            }
            updateLabelState(z);
            updateEditTextBackground();
            CollapsingTextHelper collapsingTextHelper = this.mCollapsingTextHelper;
            if (collapsingTextHelper != null ? collapsingTextHelper.setState(drawableState) | false : false) {
                invalidate();
            }
            this.mInDrawableStateChanged = false;
        }
    }

    private void expandHint(boolean z) {
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.mAnimator.cancel();
        }
        if (!z || !this.mHintAnimationEnabled) {
            this.mCollapsingTextHelper.setExpansionFraction(0.0f);
        } else {
            animateToExpansionFraction(0.0f);
        }
        this.mHintExpanded = true;
    }

    /* access modifiers changed from: 0000 */
    public void animateToExpansionFraction(float f) {
        if (this.mCollapsingTextHelper.getExpansionFraction() != f) {
            if (this.mAnimator == null) {
                this.mAnimator = new ValueAnimator();
                this.mAnimator.setInterpolator(AnimatorUtils.FastOutSlowInInterpolator);
                this.mAnimator.setDuration(225);
                this.mAnimator.addUpdateListener(new AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        OPTextInputLayout.this.mCollapsingTextHelper.setExpansionFraction(((Float) valueAnimator.getAnimatedValue()).floatValue());
                    }
                });
            }
            this.mAnimator.setFloatValues(new float[]{this.mCollapsingTextHelper.getExpansionFraction(), f});
            this.mAnimator.start();
        }
    }

    /* access modifiers changed from: 0000 */
    public final boolean isHintExpanded() {
        return this.mHintExpanded;
    }

    private static boolean arrayContains(int[] iArr, int i) {
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }
}
