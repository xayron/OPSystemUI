package androidx.slice.widget;

import android.text.TextUtils;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import java.util.ArrayList;
import java.util.List;

public class RowContent extends SliceContent {
    private ArrayList<SliceItem> mEndItems = new ArrayList<>();
    private boolean mIsHeader;
    private int mLineCount;
    private SliceItem mPrimaryAction;
    private SliceItem mRange;
    private SliceItem mSelection;
    private boolean mShowActionDivider;
    private boolean mShowBottomDivider;
    private boolean mShowTitleItems;
    private SliceItem mStartItem;
    private SliceItem mSubtitleItem;
    private SliceItem mSummaryItem;
    private SliceItem mTitleItem;
    private ArrayList<SliceAction> mToggleItems = new ArrayList<>();

    public RowContent(SliceItem sliceItem, int i) {
        super(sliceItem, i);
        boolean z = false;
        this.mLineCount = 0;
        if (i == 0) {
            z = true;
        }
        populate(sliceItem, z);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0044, code lost:
        if ("slice".equals(((androidx.slice.SliceItem) r10.get(0)).getFormat()) != false) goto L_0x0046;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean populate(androidx.slice.SliceItem r9, boolean r10) {
        /*
            r8 = this;
            r8.mIsHeader = r10
            boolean r10 = isValidRow(r9)
            r0 = 0
            if (r10 != 0) goto L_0x0011
            java.lang.String r8 = "RowContent"
            java.lang.String r9 = "Provided SliceItem is invalid for RowContent"
            android.util.Log.w(r8, r9)
            return r0
        L_0x0011:
            r8.determineStartAndPrimaryAction(r9)
            java.util.ArrayList r10 = filterInvalidItems(r9)
            int r1 = r10.size()
            java.lang.String r2 = "action"
            java.lang.String r3 = "title"
            r4 = 1
            if (r1 != r4) goto L_0x006e
            java.lang.Object r1 = r10.get(r0)
            androidx.slice.SliceItem r1 = (androidx.slice.SliceItem) r1
            java.lang.String r1 = r1.getFormat()
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0046
            java.lang.Object r1 = r10.get(r0)
            androidx.slice.SliceItem r1 = (androidx.slice.SliceItem) r1
            java.lang.String r1 = r1.getFormat()
            java.lang.String r5 = "slice"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x006e
        L_0x0046:
            java.lang.Object r1 = r10.get(r0)
            androidx.slice.SliceItem r1 = (androidx.slice.SliceItem) r1
            java.lang.String r5 = "shortcut"
            java.lang.String[] r5 = new java.lang.String[]{r5, r3}
            boolean r1 = r1.hasAnyHints(r5)
            if (r1 != 0) goto L_0x006e
            java.lang.Object r1 = r10.get(r0)
            androidx.slice.SliceItem r1 = (androidx.slice.SliceItem) r1
            boolean r1 = isValidRow(r1)
            if (r1 == 0) goto L_0x006e
            java.lang.Object r9 = r10.get(r0)
            androidx.slice.SliceItem r9 = (androidx.slice.SliceItem) r9
            java.util.ArrayList r10 = filterInvalidItems(r9)
        L_0x006e:
            java.lang.String r1 = r9.getSubType()
            java.lang.String r5 = "range"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x007c
            r8.mRange = r9
        L_0x007c:
            java.lang.String r1 = r9.getSubType()
            java.lang.String r5 = "selection"
            boolean r1 = r5.equals(r1)
            if (r1 == 0) goto L_0x008a
            r8.mSelection = r9
        L_0x008a:
            int r9 = r10.size()
            if (r9 <= 0) goto L_0x0156
            androidx.slice.SliceItem r9 = r8.mStartItem
            if (r9 == 0) goto L_0x0097
            r10.remove(r9)
        L_0x0097:
            androidx.slice.SliceItem r9 = r8.mPrimaryAction
            if (r9 == 0) goto L_0x009e
            r10.remove(r9)
        L_0x009e:
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            r1 = r0
        L_0x00a4:
            int r5 = r10.size()
            if (r1 >= r5) goto L_0x00f9
            java.lang.Object r5 = r10.get(r1)
            androidx.slice.SliceItem r5 = (androidx.slice.SliceItem) r5
            java.lang.String r6 = r5.getFormat()
            java.lang.String r7 = "text"
            boolean r6 = r7.equals(r6)
            if (r6 == 0) goto L_0x00f3
            androidx.slice.SliceItem r6 = r8.mTitleItem
            java.lang.String r7 = "summary"
            if (r6 == 0) goto L_0x00ca
            boolean r6 = r6.hasHint(r3)
            if (r6 != 0) goto L_0x00d9
        L_0x00ca:
            boolean r6 = r5.hasHint(r3)
            if (r6 == 0) goto L_0x00d9
            boolean r6 = r5.hasHint(r7)
            if (r6 != 0) goto L_0x00d9
            r8.mTitleItem = r5
            goto L_0x00f6
        L_0x00d9:
            androidx.slice.SliceItem r6 = r8.mSubtitleItem
            if (r6 != 0) goto L_0x00e6
            boolean r6 = r5.hasHint(r7)
            if (r6 != 0) goto L_0x00e6
            r8.mSubtitleItem = r5
            goto L_0x00f6
        L_0x00e6:
            androidx.slice.SliceItem r6 = r8.mSummaryItem
            if (r6 != 0) goto L_0x00f6
            boolean r6 = r5.hasHint(r7)
            if (r6 == 0) goto L_0x00f6
            r8.mSummaryItem = r5
            goto L_0x00f6
        L_0x00f3:
            r9.add(r5)
        L_0x00f6:
            int r1 = r1 + 1
            goto L_0x00a4
        L_0x00f9:
            androidx.slice.SliceItem r10 = r8.mTitleItem
            boolean r10 = hasText(r10)
            if (r10 == 0) goto L_0x0106
            int r10 = r8.mLineCount
            int r10 = r10 + r4
            r8.mLineCount = r10
        L_0x0106:
            androidx.slice.SliceItem r10 = r8.mSubtitleItem
            boolean r10 = hasText(r10)
            if (r10 == 0) goto L_0x0113
            int r10 = r8.mLineCount
            int r10 = r10 + r4
            r8.mLineCount = r10
        L_0x0113:
            androidx.slice.SliceItem r10 = r8.mStartItem
            java.lang.String r1 = "long"
            if (r10 == 0) goto L_0x0125
            java.lang.String r10 = r10.getFormat()
            boolean r10 = r1.equals(r10)
            if (r10 == 0) goto L_0x0125
            r10 = r4
            goto L_0x0126
        L_0x0125:
            r10 = r0
        L_0x0126:
            r3 = r10
            r10 = r0
        L_0x0128:
            int r5 = r9.size()
            if (r10 >= r5) goto L_0x0156
            java.lang.Object r5 = r9.get(r10)
            androidx.slice.SliceItem r5 = (androidx.slice.SliceItem) r5
            androidx.slice.SliceItem r6 = androidx.slice.core.SliceQuery.find(r5, r2)
            if (r6 == 0) goto L_0x013c
            r6 = r4
            goto L_0x013d
        L_0x013c:
            r6 = r0
        L_0x013d:
            java.lang.String r7 = r5.getFormat()
            boolean r7 = r1.equals(r7)
            if (r7 == 0) goto L_0x0150
            if (r3 != 0) goto L_0x0153
            java.util.ArrayList<androidx.slice.SliceItem> r3 = r8.mEndItems
            r3.add(r5)
            r3 = r4
            goto L_0x0153
        L_0x0150:
            r8.processContent(r5, r6)
        L_0x0153:
            int r10 = r10 + 1
            goto L_0x0128
        L_0x0156:
            boolean r8 = r8.isValid()
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.RowContent.populate(androidx.slice.SliceItem, boolean):boolean");
    }

    private void processContent(SliceItem sliceItem, boolean z) {
        if (z) {
            SliceActionImpl sliceActionImpl = new SliceActionImpl(sliceItem);
            if (sliceActionImpl.isToggle()) {
                this.mToggleItems.add(sliceActionImpl);
            }
        }
        this.mEndItems.add(sliceItem);
    }

    private void determineStartAndPrimaryAction(SliceItem sliceItem) {
        String str = "title";
        List findAll = SliceQuery.findAll(sliceItem, (String) null, str, (String) null);
        String str2 = "slice";
        String str3 = "action";
        if (findAll.size() > 0) {
            String format = ((SliceItem) findAll.get(0)).getFormat();
            String str4 = "image";
            if ((str3.equals(format) && SliceQuery.find((SliceItem) findAll.get(0), str4) != null) || str2.equals(format) || "long".equals(format) || str4.equals(format)) {
                this.mStartItem = (SliceItem) findAll.get(0);
            }
        }
        String[] strArr = {"shortcut", str};
        List findAll2 = SliceQuery.findAll(sliceItem, str2, strArr, (String[]) null);
        findAll2.addAll(SliceQuery.findAll(sliceItem, str3, strArr, (String[]) null));
        if (findAll2.isEmpty() && str3.equals(sliceItem.getFormat()) && sliceItem.getSlice().getItems().size() == 1) {
            this.mPrimaryAction = sliceItem;
        } else if (this.mStartItem != null && findAll2.size() > 1 && findAll2.get(0) == this.mStartItem) {
            this.mPrimaryAction = (SliceItem) findAll2.get(1);
        } else if (findAll2.size() > 0) {
            this.mPrimaryAction = (SliceItem) findAll2.get(0);
        }
    }

    public boolean isValid() {
        return super.isValid() && !(this.mStartItem == null && this.mPrimaryAction == null && this.mTitleItem == null && this.mSubtitleItem == null && this.mEndItems.size() <= 0 && this.mRange == null && this.mSelection == null && !isDefaultSeeMore());
    }

    public boolean getIsHeader() {
        return this.mIsHeader;
    }

    public void setIsHeader(boolean z) {
        this.mIsHeader = z;
    }

    public SliceItem getRange() {
        return this.mRange;
    }

    public SliceItem getSelection() {
        return this.mSelection;
    }

    public SliceItem getInputRangeThumb() {
        SliceItem sliceItem = this.mRange;
        if (sliceItem != null) {
            List items = sliceItem.getSlice().getItems();
            for (int i = 0; i < items.size(); i++) {
                if ("image".equals(((SliceItem) items.get(i)).getFormat())) {
                    return (SliceItem) items.get(i);
                }
            }
        }
        return null;
    }

    public SliceItem getPrimaryAction() {
        return this.mPrimaryAction;
    }

    public SliceItem getStartItem() {
        if (!this.mIsHeader || this.mShowTitleItems) {
            return this.mStartItem;
        }
        return null;
    }

    public SliceItem getTitleItem() {
        return this.mTitleItem;
    }

    public SliceItem getSubtitleItem() {
        return this.mSubtitleItem;
    }

    public SliceItem getSummaryItem() {
        SliceItem sliceItem = this.mSummaryItem;
        return sliceItem == null ? this.mSubtitleItem : sliceItem;
    }

    public ArrayList<SliceItem> getEndItems() {
        return this.mEndItems;
    }

    public ArrayList<SliceAction> getToggleItems() {
        return this.mToggleItems;
    }

    public int getLineCount() {
        return this.mLineCount;
    }

    public int getHeight(SliceStyle sliceStyle, SliceViewPolicy sliceViewPolicy) {
        return sliceStyle.getRowHeight(this, sliceViewPolicy);
    }

    public boolean isDefaultSeeMore() {
        return "action".equals(this.mSliceItem.getFormat()) && this.mSliceItem.getSlice().hasHint("see_more") && this.mSliceItem.getSlice().getItems().isEmpty();
    }

    public void showTitleItems(boolean z) {
        this.mShowTitleItems = z;
    }

    public boolean hasTitleItems() {
        return this.mShowTitleItems;
    }

    public void showBottomDivider(boolean z) {
        this.mShowBottomDivider = z;
    }

    public boolean hasBottomDivider() {
        return this.mShowBottomDivider;
    }

    public void showActionDivider(boolean z) {
        this.mShowActionDivider = z;
    }

    public boolean hasActionDivider() {
        return this.mShowActionDivider;
    }

    private static boolean hasText(SliceItem sliceItem) {
        return sliceItem != null && (sliceItem.hasHint("partial") || !TextUtils.isEmpty(sliceItem.getText()));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x001a, code lost:
        if ("action".equals(r5.getFormat()) != false) goto L_0x001c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean isValidRow(androidx.slice.SliceItem r5) {
        /*
            r0 = 0
            if (r5 != 0) goto L_0x0004
            return r0
        L_0x0004:
            java.lang.String r1 = r5.getFormat()
            java.lang.String r2 = "slice"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x001c
            java.lang.String r1 = r5.getFormat()
            java.lang.String r2 = "action"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x004b
        L_0x001c:
            androidx.slice.Slice r1 = r5.getSlice()
            java.util.List r1 = r1.getItems()
            java.lang.String r2 = "see_more"
            boolean r2 = r5.hasHint(r2)
            r3 = 1
            if (r2 == 0) goto L_0x0034
            boolean r2 = r1.isEmpty()
            if (r2 == 0) goto L_0x0034
            return r3
        L_0x0034:
            r2 = r0
        L_0x0035:
            int r4 = r1.size()
            if (r2 >= r4) goto L_0x004b
            java.lang.Object r4 = r1.get(r2)
            androidx.slice.SliceItem r4 = (androidx.slice.SliceItem) r4
            boolean r4 = isValidRowContent(r5, r4)
            if (r4 == 0) goto L_0x0048
            return r3
        L_0x0048:
            int r2 = r2 + 1
            goto L_0x0035
        L_0x004b:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.RowContent.isValidRow(androidx.slice.SliceItem):boolean");
    }

    private static ArrayList<SliceItem> filterInvalidItems(SliceItem sliceItem) {
        ArrayList<SliceItem> arrayList = new ArrayList<>();
        for (SliceItem sliceItem2 : sliceItem.getSlice().getItems()) {
            if (isValidRowContent(sliceItem, sliceItem2)) {
                arrayList.add(sliceItem2);
            }
        }
        return arrayList;
    }

    private static boolean isValidRowContent(SliceItem sliceItem, SliceItem sliceItem2) {
        if (sliceItem2.hasAnyHints("keywords", "ttl", "last_updated", "horizontal")) {
            return false;
        }
        if ("content_description".equals(sliceItem2.getSubType())) {
            return false;
        }
        if ("selection_option_key".equals(sliceItem2.getSubType())) {
            return false;
        }
        if ("selection_option_value".equals(sliceItem2.getSubType())) {
            return false;
        }
        String format = sliceItem2.getFormat();
        if (!"image".equals(format) && !"text".equals(format) && !"long".equals(format) && !"action".equals(format) && !"input".equals(format) && !"slice".equals(format)) {
            if (!"int".equals(format)) {
                return false;
            }
            if (!"range".equals(sliceItem.getSubType())) {
                return false;
            }
        }
        return true;
    }
}
