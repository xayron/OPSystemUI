package com.android.systemui.tuner;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.Preference.OnPreferenceClickListener;
import com.android.systemui.Dependency;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.R$xml;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.tuner.TunerService.Tunable;
import java.util.ArrayList;

public class NavBarTuner extends TunerPreferenceFragment {
    private static final int[][] ICONS = {new int[]{R$drawable.ic_qs_circle, R$string.tuner_circle}, new int[]{R$drawable.ic_add, R$string.tuner_plus}, new int[]{R$drawable.ic_remove, R$string.tuner_minus}, new int[]{R$drawable.ic_left, R$string.tuner_left}, new int[]{R$drawable.ic_right, R$string.tuner_right}, new int[]{R$drawable.ic_menu, R$string.tuner_menu}};
    private Handler mHandler;
    private final ArrayList<Tunable> mTunables = new ArrayList<>();

    public void onCreate(Bundle bundle) {
        this.mHandler = new Handler();
        super.onCreate(bundle);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(R$xml.nav_bar_tuner);
        bindLayout((ListPreference) findPreference("layout"));
        bindButton("sysui_nav_bar_left", "space", "left");
        bindButton("sysui_nav_bar_right", "menu_ime", "right");
    }

    public void onDestroy() {
        super.onDestroy();
        this.mTunables.forEach($$Lambda$NavBarTuner$tsKQ8HfwaDSvc3iDCsgHsW954hc.INSTANCE);
    }

    private void addTunable(Tunable tunable, String... strArr) {
        this.mTunables.add(tunable);
        ((TunerService) Dependency.get(TunerService.class)).addTunable(tunable, strArr);
    }

    private void bindLayout(ListPreference listPreference) {
        addTunable(new Tunable(listPreference) {
            private final /* synthetic */ ListPreference f$1;

            {
                this.f$1 = r2;
            }

            public final void onTuningChanged(String str, String str2) {
                NavBarTuner.this.lambda$bindLayout$2$NavBarTuner(this.f$1, str, str2);
            }
        }, "sysui_nav_bar");
        listPreference.setOnPreferenceChangeListener($$Lambda$NavBarTuner$xJajVHN9uODpq3muoNpXW6uxwc.INSTANCE);
    }

    public /* synthetic */ void lambda$bindLayout$2$NavBarTuner(ListPreference listPreference, String str, String str2) {
        this.mHandler.post(new Runnable(str2, listPreference) {
            private final /* synthetic */ String f$0;
            private final /* synthetic */ ListPreference f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                NavBarTuner.lambda$bindLayout$1(this.f$0, this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$bindLayout$1(String str, ListPreference listPreference) {
        if (str == null) {
            str = "default";
        }
        listPreference.setValue(str);
    }

    static /* synthetic */ boolean lambda$bindLayout$3(Preference preference, Object obj) {
        String str = (String) obj;
        if ("default".equals(str)) {
            str = null;
        }
        ((TunerService) Dependency.get(TunerService.class)).setValue("sysui_nav_bar", str);
        return true;
    }

    private void bindButton(String str, String str2, String str3) {
        StringBuilder sb = new StringBuilder();
        sb.append("type_");
        sb.append(str3);
        ListPreference listPreference = (ListPreference) findPreference(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("keycode_");
        sb2.append(str3);
        Preference findPreference = findPreference(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("icon_");
        sb3.append(str3);
        ListPreference listPreference2 = (ListPreference) findPreference(sb3.toString());
        setupIcons(listPreference2);
        ListPreference listPreference3 = listPreference;
        $$Lambda$NavBarTuner$AtqwC3eDMLXM8PvQu0SrBbBcxZQ r1 = new Tunable(str2, listPreference3, listPreference2, findPreference) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ ListPreference f$2;
            private final /* synthetic */ ListPreference f$3;
            private final /* synthetic */ Preference f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onTuningChanged(String str, String str2) {
                NavBarTuner.this.lambda$bindButton$5$NavBarTuner(this.f$1, this.f$2, this.f$3, this.f$4, str, str2);
            }
        };
        addTunable(r1, str);
        ListPreference listPreference4 = listPreference2;
        $$Lambda$NavBarTuner$5vkJoJwaFUhdGZ7Fp4qtkLVqooo r12 = new OnPreferenceChangeListener(str, listPreference3, findPreference, listPreference4) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ ListPreference f$2;
            private final /* synthetic */ Preference f$3;
            private final /* synthetic */ ListPreference f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return NavBarTuner.this.lambda$bindButton$7$NavBarTuner(this.f$1, this.f$2, this.f$3, this.f$4, preference, obj);
            }
        };
        listPreference.setOnPreferenceChangeListener(r12);
        listPreference2.setOnPreferenceChangeListener(r12);
        $$Lambda$NavBarTuner$VEefG8gxDDp8OSjE4w47bWNl4eQ r13 = new OnPreferenceClickListener(findPreference, str, listPreference, listPreference4) {
            private final /* synthetic */ Preference f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ ListPreference f$3;
            private final /* synthetic */ ListPreference f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final boolean onPreferenceClick(Preference preference) {
                return NavBarTuner.this.lambda$bindButton$9$NavBarTuner(this.f$1, this.f$2, this.f$3, this.f$4, preference);
            }
        };
        findPreference.setOnPreferenceClickListener(r13);
    }

    public /* synthetic */ void lambda$bindButton$5$NavBarTuner(String str, ListPreference listPreference, ListPreference listPreference2, Preference preference, String str2, String str3) {
        Handler handler = this.mHandler;
        $$Lambda$NavBarTuner$sQQgaEvmFdhni6jwm3oIAJf94Eo r0 = new Runnable(str3, str, listPreference, listPreference2, preference) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ ListPreference f$3;
            private final /* synthetic */ ListPreference f$4;
            private final /* synthetic */ Preference f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                NavBarTuner.this.lambda$bindButton$4$NavBarTuner(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        };
        handler.post(r0);
    }

    public /* synthetic */ void lambda$bindButton$4$NavBarTuner(String str, String str2, ListPreference listPreference, ListPreference listPreference2, Preference preference) {
        if (str == null) {
            str = str2;
        }
        String extractButton = NavigationBarInflaterView.extractButton(str);
        String str3 = "key";
        if (extractButton.startsWith(str3)) {
            listPreference.setValue(str3);
            String extractImage = NavigationBarInflaterView.extractImage(extractButton);
            int extractKeycode = NavigationBarInflaterView.extractKeycode(extractButton);
            listPreference2.setValue(extractImage);
            updateSummary(listPreference2);
            StringBuilder sb = new StringBuilder();
            sb.append(extractKeycode);
            sb.append("");
            preference.setSummary((CharSequence) sb.toString());
            preference.setVisible(true);
            listPreference2.setVisible(true);
            return;
        }
        listPreference.setValue(extractButton);
        preference.setVisible(false);
        listPreference2.setVisible(false);
    }

    public /* synthetic */ boolean lambda$bindButton$7$NavBarTuner(String str, ListPreference listPreference, Preference preference, ListPreference listPreference2, Preference preference2, Object obj) {
        Handler handler = this.mHandler;
        $$Lambda$NavBarTuner$Q4QuzL1NB7uGZ3GCFspFwSEMA8g r0 = new Runnable(str, listPreference, preference, listPreference2) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ ListPreference f$2;
            private final /* synthetic */ Preference f$3;
            private final /* synthetic */ ListPreference f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                NavBarTuner.this.lambda$bindButton$6$NavBarTuner(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        };
        handler.post(r0);
        return true;
    }

    public /* synthetic */ void lambda$bindButton$6$NavBarTuner(String str, ListPreference listPreference, Preference preference, ListPreference listPreference2) {
        setValue(str, listPreference, preference, listPreference2);
        updateSummary(listPreference2);
    }

    public /* synthetic */ boolean lambda$bindButton$9$NavBarTuner(Preference preference, String str, ListPreference listPreference, ListPreference listPreference2, Preference preference2) {
        EditText editText = new EditText(getContext());
        Builder negativeButton = new Builder(getContext()).setTitle(preference2.getTitle()).setView(editText).setNegativeButton(17039360, null);
        $$Lambda$NavBarTuner$oFwpdLrZA2BGC8nFWvjJ8NeCnQE r0 = new OnClickListener(editText, preference, str, listPreference, listPreference2) {
            private final /* synthetic */ EditText f$1;
            private final /* synthetic */ Preference f$2;
            private final /* synthetic */ String f$3;
            private final /* synthetic */ ListPreference f$4;
            private final /* synthetic */ ListPreference f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                NavBarTuner.this.lambda$bindButton$8$NavBarTuner(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
            }
        };
        negativeButton.setPositiveButton(17039370, r0).show();
        return true;
    }

    public /* synthetic */ void lambda$bindButton$8$NavBarTuner(EditText editText, Preference preference, String str, ListPreference listPreference, ListPreference listPreference2, DialogInterface dialogInterface, int i) {
        int i2;
        try {
            i2 = Integer.parseInt(editText.getText().toString());
        } catch (Exception unused) {
            i2 = 66;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(i2);
        sb.append("");
        preference.setSummary((CharSequence) sb.toString());
        setValue(str, listPreference, preference, listPreference2);
    }

    private void updateSummary(ListPreference listPreference) {
        String str = "/";
        try {
            int applyDimension = (int) TypedValue.applyDimension(1, 14.0f, getContext().getResources().getDisplayMetrics());
            String str2 = listPreference.getValue().split(str)[0];
            int parseInt = Integer.parseInt(listPreference.getValue().split(str)[1]);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            Drawable loadDrawable = Icon.createWithResource(str2, parseInt).loadDrawable(getContext());
            loadDrawable.setTint(-16777216);
            loadDrawable.setBounds(0, 0, applyDimension, applyDimension);
            spannableStringBuilder.append("  ", new ImageSpan(loadDrawable, 1), 0);
            spannableStringBuilder.append(" ");
            for (int i = 0; i < ICONS.length; i++) {
                if (ICONS[i][0] == parseInt) {
                    spannableStringBuilder.append(getString(ICONS[i][1]));
                }
            }
            listPreference.setSummary(spannableStringBuilder);
        } catch (Exception e) {
            Log.d("NavButton", "Problem with summary", e);
            listPreference.setSummary(null);
        }
    }

    private void setValue(String str, ListPreference listPreference, Preference preference, ListPreference listPreference2) {
        String value = listPreference.getValue();
        if ("key".equals(value)) {
            String value2 = listPreference2.getValue();
            int i = 66;
            try {
                i = Integer.parseInt(preference.getSummary().toString());
            } catch (Exception unused) {
            }
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            sb.append("(");
            sb.append(i);
            sb.append(":");
            sb.append(value2);
            sb.append(")");
            value = sb.toString();
        }
        ((TunerService) Dependency.get(TunerService.class)).setValue(str, value);
    }

    private void setupIcons(ListPreference listPreference) {
        int[][] iArr = ICONS;
        CharSequence[] charSequenceArr = new CharSequence[iArr.length];
        CharSequence[] charSequenceArr2 = new CharSequence[iArr.length];
        int applyDimension = (int) TypedValue.applyDimension(1, 14.0f, getContext().getResources().getDisplayMetrics());
        for (int i = 0; i < ICONS.length; i++) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            Drawable loadDrawable = Icon.createWithResource(getContext().getPackageName(), ICONS[i][0]).loadDrawable(getContext());
            loadDrawable.setTint(-16777216);
            loadDrawable.setBounds(0, 0, applyDimension, applyDimension);
            spannableStringBuilder.append("  ", new ImageSpan(loadDrawable, 1), 0);
            spannableStringBuilder.append(" ");
            spannableStringBuilder.append(getString(ICONS[i][1]));
            charSequenceArr[i] = spannableStringBuilder;
            StringBuilder sb = new StringBuilder();
            sb.append(getContext().getPackageName());
            sb.append("/");
            sb.append(ICONS[i][0]);
            charSequenceArr2[i] = sb.toString();
        }
        listPreference.setEntries(charSequenceArr);
        listPreference.setEntryValues(charSequenceArr2);
    }
}
