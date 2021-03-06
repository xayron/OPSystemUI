package com.oneplus.lib.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageButton;
import com.oneplus.commonctrl.R$id;
import com.oneplus.commonctrl.R$layout;
import com.oneplus.commonctrl.R$style;
import com.oneplus.commonctrl.R$styleable;
import com.oneplus.lib.util.MathUtils;
import com.oneplus.lib.widget.util.ViewUtils;
import com.oneplus.support.viewpager.widget.ViewPager;
import com.oneplus.support.viewpager.widget.ViewPager.OnPageChangeListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class DayPickerView extends ViewGroup {
    private static final int[] ATTRS_TEXT_COLOR = {16842904};
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy");
    private static final int DEFAULT_LAYOUT = R$layout.op_day_picker_content_material;
    /* access modifiers changed from: private */
    public final AccessibilityManager mAccessibilityManager;
    private final DayPickerPagerAdapter mAdapter;
    private Context mContext;
    private final Calendar mMaxDate;
    private final Calendar mMinDate;
    /* access modifiers changed from: private */
    public final ImageButton mNextButton;
    private final OnClickListener mOnClickListener;
    /* access modifiers changed from: private */
    public OnDaySelectedListener mOnDaySelectedListener;
    private final OnPageChangeListener mOnPageChangedListener;
    /* access modifiers changed from: private */
    public final ImageButton mPrevButton;
    private final Calendar mSelectedDay;
    private Calendar mTempCalendar;
    /* access modifiers changed from: private */
    public final ViewPager mViewPager;

    public interface OnDaySelectedListener {
        void onDaySelected(DayPickerView dayPickerView, Calendar calendar);
    }

    public DayPickerView(Context context) {
        this(context, null);
    }

    public DayPickerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16843613);
    }

    public DayPickerView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public DayPickerView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mSelectedDay = Calendar.getInstance();
        this.mMinDate = Calendar.getInstance();
        this.mMaxDate = Calendar.getInstance();
        this.mOnPageChangedListener = new OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
                float abs = Math.abs(0.5f - f) * 2.0f;
                DayPickerView.this.mPrevButton.setAlpha(abs);
                DayPickerView.this.mNextButton.setAlpha(abs);
            }

            public void onPageSelected(int i) {
                DayPickerView.this.updateButtonVisibility(i);
            }
        };
        this.mOnClickListener = new OnClickListener() {
            public void onClick(View view) {
                int i;
                if (view == DayPickerView.this.mPrevButton) {
                    i = -1;
                } else if (view == DayPickerView.this.mNextButton) {
                    i = 1;
                } else {
                    return;
                }
                int currentItem = DayPickerView.this.mViewPager.getCurrentItem() + i;
                DayPickerView.this.mViewPager.setCurrentItem(currentItem, !DayPickerView.this.mAccessibilityManager.isEnabled());
            }
        };
        this.mContext = context;
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.OPCalendarView, i, i2);
        int i3 = obtainStyledAttributes.getInt(R$styleable.OPCalendarView_android_firstDayOfWeek, Calendar.getInstance().get(7));
        String string = obtainStyledAttributes.getString(R$styleable.OPCalendarView_android_minDate);
        String string2 = obtainStyledAttributes.getString(R$styleable.OPCalendarView_android_maxDate);
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.OPCalendarView_monthTextAppearance, R$style.TextAppearance_Material_Widget_Calendar_Month);
        int resourceId2 = obtainStyledAttributes.getResourceId(R$styleable.OPCalendarView_weekDayTextAppearance, R$style.TextAppearance_Material_Widget_Calendar_DayOfWeek);
        int resourceId3 = obtainStyledAttributes.getResourceId(R$styleable.OPCalendarView_dateTextAppearance, R$style.TextAppearance_Material_Widget_Calendar_Day);
        ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R$styleable.OPCalendarView_daySelectorColor);
        obtainStyledAttributes.recycle();
        this.mAdapter = new DayPickerPagerAdapter(context, R$layout.op_date_picker_month_item_material, R$id.month_view);
        this.mAdapter.setMonthTextAppearance(resourceId);
        this.mAdapter.setDayOfWeekTextAppearance(resourceId2);
        this.mAdapter.setDayTextAppearance(resourceId3);
        this.mAdapter.setDaySelectorColor(colorStateList);
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(DEFAULT_LAYOUT, this, false);
        while (viewGroup.getChildCount() > 0) {
            View childAt = viewGroup.getChildAt(0);
            viewGroup.removeViewAt(0);
            addView(childAt);
        }
        this.mPrevButton = (ImageButton) findViewById(R$id.prev);
        this.mPrevButton.setOnClickListener(this.mOnClickListener);
        this.mNextButton = (ImageButton) findViewById(R$id.next);
        this.mNextButton.setOnClickListener(this.mOnClickListener);
        this.mViewPager = (ViewPager) findViewById(R$id.day_picker_view_pager);
        this.mViewPager.setAdapter(this.mAdapter);
        this.mViewPager.setOnPageChangeListener(this.mOnPageChangedListener);
        if (resourceId != 0) {
            TypedArray obtainStyledAttributes2 = this.mContext.obtainStyledAttributes(null, ATTRS_TEXT_COLOR, 0, resourceId);
            ColorStateList colorStateList2 = obtainStyledAttributes2.getColorStateList(0);
            if (colorStateList2 != null) {
                this.mPrevButton.setImageTintList(colorStateList2);
                this.mNextButton.setImageTintList(colorStateList2);
            }
            obtainStyledAttributes2.recycle();
        }
        Calendar instance = Calendar.getInstance();
        if (!parseDate(string, instance)) {
            instance.set(1900, 0, 1);
        }
        long timeInMillis = instance.getTimeInMillis();
        if (!parseDate(string2, instance)) {
            instance.set(2100, 11, 31);
        }
        long timeInMillis2 = instance.getTimeInMillis();
        if (timeInMillis2 >= timeInMillis) {
            long constrain = MathUtils.constrain(System.currentTimeMillis(), timeInMillis, timeInMillis2);
            setFirstDayOfWeek(i3);
            setMinDate(timeInMillis);
            setMaxDate(timeInMillis2);
            setDate(constrain, false);
            this.mAdapter.setOnDaySelectedListener(new com.oneplus.lib.widget.DayPickerPagerAdapter.OnDaySelectedListener() {
                public void onDaySelected(DayPickerPagerAdapter dayPickerPagerAdapter, Calendar calendar) {
                    if (DayPickerView.this.mOnDaySelectedListener != null) {
                        DayPickerView.this.mOnDaySelectedListener.onDaySelected(DayPickerView.this, calendar);
                    }
                }
            });
            return;
        }
        throw new IllegalArgumentException("maxDate must be >= minDate");
    }

    public static boolean parseDate(String str, Calendar calendar) {
        if (str != null && !str.isEmpty()) {
            try {
                calendar.setTime(DATE_FORMATTER.parse(str));
                return true;
            } catch (ParseException unused) {
                StringBuilder sb = new StringBuilder();
                sb.append("Date: ");
                sb.append(str);
                sb.append(" not in format: ");
                sb.append("MM/dd/yyyy");
                Log.w("SearchView", sb.toString());
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void updateButtonVisibility(int i) {
        boolean z = true;
        int i2 = 0;
        boolean z2 = i > 0;
        if (i >= this.mAdapter.getCount() - 1) {
            z = false;
        }
        this.mPrevButton.setVisibility(z2 ? 0 : 4);
        ImageButton imageButton = this.mNextButton;
        if (!z) {
            i2 = 4;
        }
        imageButton.setVisibility(i2);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        ViewPager viewPager = this.mViewPager;
        measureChild(viewPager, i, i2);
        setMeasuredDimension(viewPager.getMeasuredWidthAndState(), viewPager.getMeasuredHeightAndState());
        int measuredWidth = viewPager.getMeasuredWidth();
        int measuredHeight = viewPager.getMeasuredHeight();
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(measuredWidth, Integer.MIN_VALUE);
        int makeMeasureSpec2 = MeasureSpec.makeMeasureSpec(measuredHeight, Integer.MIN_VALUE);
        this.mPrevButton.measure(makeMeasureSpec, makeMeasureSpec2);
        this.mNextButton.measure(makeMeasureSpec, makeMeasureSpec2);
    }

    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        ImageButton imageButton;
        ImageButton imageButton2;
        if (ViewUtils.isLayoutRtl(this)) {
            imageButton = this.mNextButton;
            imageButton2 = this.mPrevButton;
        } else {
            imageButton = this.mPrevButton;
            imageButton2 = this.mNextButton;
        }
        int i5 = i3 - i;
        this.mViewPager.layout(0, 0, i5, i4 - i2);
        SimpleMonthView simpleMonthView = (SimpleMonthView) this.mViewPager.getChildAt(0);
        int monthHeight = simpleMonthView.getMonthHeight();
        int cellWidth = simpleMonthView.getCellWidth();
        int measuredWidth = imageButton.getMeasuredWidth();
        int measuredHeight = imageButton.getMeasuredHeight();
        int paddingTop = simpleMonthView.getPaddingTop() + ((monthHeight - measuredHeight) / 2);
        int paddingLeft = simpleMonthView.getPaddingLeft() + ((cellWidth - measuredWidth) / 2);
        imageButton.layout(paddingLeft, paddingTop, measuredWidth + paddingLeft, measuredHeight + paddingTop);
        int measuredWidth2 = imageButton2.getMeasuredWidth();
        int measuredHeight2 = imageButton2.getMeasuredHeight();
        int paddingTop2 = simpleMonthView.getPaddingTop() + ((monthHeight - measuredHeight2) / 2);
        int paddingRight = (i5 - simpleMonthView.getPaddingRight()) - ((cellWidth - measuredWidth2) / 2);
        imageButton2.layout(paddingRight - measuredWidth2, paddingTop2, paddingRight, measuredHeight2 + paddingTop2);
    }

    public void setDate(long j) {
        setDate(j, false);
    }

    public void setDate(long j, boolean z) {
        setDate(j, z, true);
    }

    private void setDate(long j, boolean z, boolean z2) {
        boolean z3 = true;
        if (j < this.mMinDate.getTimeInMillis()) {
            j = this.mMinDate.getTimeInMillis();
        } else if (j > this.mMaxDate.getTimeInMillis()) {
            j = this.mMaxDate.getTimeInMillis();
        } else {
            z3 = false;
        }
        getTempCalendarForTime(j);
        if (z2 || z3) {
            this.mSelectedDay.setTimeInMillis(j);
        }
        int positionFromDay = getPositionFromDay(j);
        if (positionFromDay != this.mViewPager.getCurrentItem()) {
            this.mViewPager.setCurrentItem(positionFromDay, z);
        }
        this.mAdapter.setSelectedDay(this.mTempCalendar);
    }

    public void setFirstDayOfWeek(int i) {
        this.mAdapter.setFirstDayOfWeek(i);
    }

    public void setMinDate(long j) {
        this.mMinDate.setTimeInMillis(j);
        onRangeChanged();
    }

    public void setMaxDate(long j) {
        this.mMaxDate.setTimeInMillis(j);
        onRangeChanged();
    }

    public void onRangeChanged() {
        this.mAdapter.setRange(this.mMinDate, this.mMaxDate);
        setDate(this.mSelectedDay.getTimeInMillis(), false, false);
        updateButtonVisibility(this.mViewPager.getCurrentItem());
    }

    public void setOnDaySelectedListener(OnDaySelectedListener onDaySelectedListener) {
        this.mOnDaySelectedListener = onDaySelectedListener;
    }

    private int getDiffMonths(Calendar calendar, Calendar calendar2) {
        return (calendar2.get(2) - calendar.get(2)) + ((calendar2.get(1) - calendar.get(1)) * 12);
    }

    private int getPositionFromDay(long j) {
        return MathUtils.constrain(getDiffMonths(this.mMinDate, getTempCalendarForTime(j)), 0, getDiffMonths(this.mMinDate, this.mMaxDate));
    }

    private Calendar getTempCalendarForTime(long j) {
        if (this.mTempCalendar == null) {
            this.mTempCalendar = Calendar.getInstance();
        }
        this.mTempCalendar.setTimeInMillis(j);
        return this.mTempCalendar;
    }

    public int getMostVisiblePosition() {
        return this.mViewPager.getCurrentItem();
    }

    public void setPosition(int i) {
        this.mViewPager.setCurrentItem(i, false);
    }
}
