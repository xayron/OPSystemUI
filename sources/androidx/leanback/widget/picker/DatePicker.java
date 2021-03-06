package androidx.leanback.widget.picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import androidx.leanback.R$attr;
import androidx.leanback.R$styleable;
import androidx.leanback.widget.picker.PickerUtility.DateConstant;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DatePicker extends Picker {
    private static final int[] DATE_FIELDS = {5, 2, 1};
    private int mColDayIndex;
    private int mColMonthIndex;
    private int mColYearIndex;
    private DateConstant mConstant;
    private Calendar mCurrentDate;
    private final DateFormat mDateFormat;
    private String mDatePickerFormat;
    private PickerColumn mDayColumn;
    private Calendar mMaxDate;
    private Calendar mMinDate;
    private PickerColumn mMonthColumn;
    private Calendar mTempDate;
    private PickerColumn mYearColumn;

    public DatePicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.datePickerStyle);
    }

    /* JADX INFO: finally extract failed */
    public DatePicker(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        updateCurrentLocale();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.lbDatePicker);
        if (VERSION.SDK_INT >= 29) {
            saveAttributeDataForStyleable(context, R$styleable.lbDatePicker, attributeSet, obtainStyledAttributes, 0, 0);
        }
        try {
            String string = obtainStyledAttributes.getString(R$styleable.lbDatePicker_android_minDate);
            String string2 = obtainStyledAttributes.getString(R$styleable.lbDatePicker_android_maxDate);
            String string3 = obtainStyledAttributes.getString(R$styleable.lbDatePicker_datePickerFormat);
            obtainStyledAttributes.recycle();
            this.mTempDate.clear();
            if (TextUtils.isEmpty(string)) {
                this.mTempDate.set(1900, 0, 1);
            } else if (!parseDate(string, this.mTempDate)) {
                this.mTempDate.set(1900, 0, 1);
            }
            this.mMinDate.setTimeInMillis(this.mTempDate.getTimeInMillis());
            this.mTempDate.clear();
            if (TextUtils.isEmpty(string2)) {
                this.mTempDate.set(2100, 0, 1);
            } else if (!parseDate(string2, this.mTempDate)) {
                this.mTempDate.set(2100, 0, 1);
            }
            this.mMaxDate.setTimeInMillis(this.mTempDate.getTimeInMillis());
            if (TextUtils.isEmpty(string3)) {
                string3 = new String(android.text.format.DateFormat.getDateFormatOrder(context));
            }
            setDatePickerFormat(string3);
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    private boolean parseDate(String str, Calendar calendar) {
        try {
            calendar.setTime(this.mDateFormat.parse(str));
            return true;
        } catch (ParseException unused) {
            StringBuilder sb = new StringBuilder();
            sb.append("Date: ");
            sb.append(str);
            sb.append(" not in format: ");
            sb.append("MM/dd/yyyy");
            Log.w("DatePicker", sb.toString());
            return false;
        }
    }

    /* access modifiers changed from: 0000 */
    public String getBestYearMonthDayPattern(String str) {
        String str2;
        String str3 = "MM/dd/yyyy";
        if (PickerUtility.SUPPORTS_BEST_DATE_TIME_PATTERN) {
            str2 = android.text.format.DateFormat.getBestDateTimePattern(this.mConstant.locale, str);
        } else {
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
            str2 = dateFormat instanceof SimpleDateFormat ? ((SimpleDateFormat) dateFormat).toLocalizedPattern() : str3;
        }
        return TextUtils.isEmpty(str2) ? str3 : str2;
    }

    /* access modifiers changed from: 0000 */
    public List<CharSequence> extractSeparators() {
        String bestYearMonthDayPattern = getBestYearMonthDayPattern(this.mDatePickerFormat);
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        char[] cArr = {'Y', 'y', 'M', 'm', 'D', 'd'};
        boolean z = false;
        char c = 0;
        for (int i = 0; i < bestYearMonthDayPattern.length(); i++) {
            char charAt = bestYearMonthDayPattern.charAt(i);
            if (charAt != ' ') {
                if (charAt != '\'') {
                    if (z) {
                        sb.append(charAt);
                    } else if (!isAnyOf(charAt, cArr)) {
                        sb.append(charAt);
                    } else if (charAt != c) {
                        arrayList.add(sb.toString());
                        sb.setLength(0);
                    }
                    c = charAt;
                } else if (!z) {
                    sb.setLength(0);
                    z = true;
                } else {
                    z = false;
                }
            }
        }
        arrayList.add(sb.toString());
        return arrayList;
    }

    private static boolean isAnyOf(char c, char[] cArr) {
        for (char c2 : cArr) {
            if (c == c2) {
                return true;
            }
        }
        return false;
    }

    public void setDatePickerFormat(String str) {
        if (TextUtils.isEmpty(str)) {
            str = new String(android.text.format.DateFormat.getDateFormatOrder(getContext()));
        }
        if (!TextUtils.equals(this.mDatePickerFormat, str)) {
            this.mDatePickerFormat = str;
            List extractSeparators = extractSeparators();
            if (extractSeparators.size() == str.length() + 1) {
                setSeparators(extractSeparators);
                this.mDayColumn = null;
                this.mMonthColumn = null;
                this.mYearColumn = null;
                this.mColMonthIndex = -1;
                this.mColDayIndex = -1;
                this.mColYearIndex = -1;
                String upperCase = str.toUpperCase();
                ArrayList arrayList = new ArrayList(3);
                for (int i = 0; i < upperCase.length(); i++) {
                    char charAt = upperCase.charAt(i);
                    String str2 = "datePicker format error";
                    if (charAt != 'D') {
                        if (charAt != 'M') {
                            if (charAt != 'Y') {
                                throw new IllegalArgumentException(str2);
                            } else if (this.mYearColumn == null) {
                                PickerColumn pickerColumn = new PickerColumn();
                                this.mYearColumn = pickerColumn;
                                arrayList.add(pickerColumn);
                                this.mColYearIndex = i;
                                this.mYearColumn.setLabelFormat("%d");
                            } else {
                                throw new IllegalArgumentException(str2);
                            }
                        } else if (this.mMonthColumn == null) {
                            PickerColumn pickerColumn2 = new PickerColumn();
                            this.mMonthColumn = pickerColumn2;
                            arrayList.add(pickerColumn2);
                            this.mMonthColumn.setStaticLabels(this.mConstant.months);
                            this.mColMonthIndex = i;
                        } else {
                            throw new IllegalArgumentException(str2);
                        }
                    } else if (this.mDayColumn == null) {
                        PickerColumn pickerColumn3 = new PickerColumn();
                        this.mDayColumn = pickerColumn3;
                        arrayList.add(pickerColumn3);
                        this.mDayColumn.setLabelFormat("%02d");
                        this.mColDayIndex = i;
                    } else {
                        throw new IllegalArgumentException(str2);
                    }
                }
                setColumns(arrayList);
                updateSpinners(false);
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Separators size: ");
            sb.append(extractSeparators.size());
            sb.append(" must equal the size of datePickerFormat: ");
            sb.append(str.length());
            sb.append(" + 1");
            throw new IllegalStateException(sb.toString());
        }
    }

    private void updateCurrentLocale() {
        this.mConstant = PickerUtility.getDateConstantInstance(Locale.getDefault(), getContext().getResources());
        this.mTempDate = PickerUtility.getCalendarForLocale(this.mTempDate, this.mConstant.locale);
        this.mMinDate = PickerUtility.getCalendarForLocale(this.mMinDate, this.mConstant.locale);
        this.mMaxDate = PickerUtility.getCalendarForLocale(this.mMaxDate, this.mConstant.locale);
        this.mCurrentDate = PickerUtility.getCalendarForLocale(this.mCurrentDate, this.mConstant.locale);
        PickerColumn pickerColumn = this.mMonthColumn;
        if (pickerColumn != null) {
            pickerColumn.setStaticLabels(this.mConstant.months);
            setColumnAt(this.mColMonthIndex, this.mMonthColumn);
        }
    }

    public final void onColumnValueChanged(int i, int i2) {
        this.mTempDate.setTimeInMillis(this.mCurrentDate.getTimeInMillis());
        int currentValue = getColumnAt(i).getCurrentValue();
        if (i == this.mColDayIndex) {
            this.mTempDate.add(5, i2 - currentValue);
        } else if (i == this.mColMonthIndex) {
            this.mTempDate.add(2, i2 - currentValue);
        } else if (i == this.mColYearIndex) {
            this.mTempDate.add(1, i2 - currentValue);
        } else {
            throw new IllegalArgumentException();
        }
        setDate(this.mTempDate.get(1), this.mTempDate.get(2), this.mTempDate.get(5));
    }

    private void setDate(int i, int i2, int i3) {
        setDate(i, i2, i3, false);
    }

    public void setDate(int i, int i2, int i3, boolean z) {
        if (isNewDate(i, i2, i3)) {
            this.mCurrentDate.set(i, i2, i3);
            if (this.mCurrentDate.before(this.mMinDate)) {
                this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
            } else if (this.mCurrentDate.after(this.mMaxDate)) {
                this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
            }
            updateSpinners(z);
        }
    }

    private boolean isNewDate(int i, int i2, int i3) {
        if (this.mCurrentDate.get(1) == i && this.mCurrentDate.get(2) == i3 && this.mCurrentDate.get(5) == i2) {
            return false;
        }
        return true;
    }

    private static boolean updateMin(PickerColumn pickerColumn, int i) {
        if (i == pickerColumn.getMinValue()) {
            return false;
        }
        pickerColumn.setMinValue(i);
        return true;
    }

    private static boolean updateMax(PickerColumn pickerColumn, int i) {
        if (i == pickerColumn.getMaxValue()) {
            return false;
        }
        pickerColumn.setMaxValue(i);
        return true;
    }

    /* access modifiers changed from: 0000 */
    public void updateSpinnersImpl(boolean z) {
        boolean z2;
        boolean z3;
        int[] iArr = {this.mColDayIndex, this.mColMonthIndex, this.mColYearIndex};
        boolean z4 = true;
        boolean z5 = true;
        for (int length = DATE_FIELDS.length - 1; length >= 0; length--) {
            if (iArr[length] >= 0) {
                int i = DATE_FIELDS[length];
                PickerColumn columnAt = getColumnAt(iArr[length]);
                if (z4) {
                    z2 = updateMin(columnAt, this.mMinDate.get(i));
                } else {
                    z2 = updateMin(columnAt, this.mCurrentDate.getActualMinimum(i));
                }
                boolean z6 = z2 | false;
                if (z5) {
                    z3 = updateMax(columnAt, this.mMaxDate.get(i));
                } else {
                    z3 = updateMax(columnAt, this.mCurrentDate.getActualMaximum(i));
                }
                z4 &= this.mCurrentDate.get(i) == this.mMinDate.get(i);
                z5 &= this.mCurrentDate.get(i) == this.mMaxDate.get(i);
                if (z6 || z3) {
                    setColumnAt(iArr[length], columnAt);
                }
                setColumnValue(iArr[length], this.mCurrentDate.get(i), z);
            }
        }
    }

    private void updateSpinners(final boolean z) {
        post(new Runnable() {
            public void run() {
                DatePicker.this.updateSpinnersImpl(z);
            }
        });
    }
}
