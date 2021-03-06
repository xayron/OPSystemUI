package androidx.slice;

import android.content.Context;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import androidx.slice.widget.ListContent;
import androidx.slice.widget.RowContent;
import java.util.ArrayList;
import java.util.List;

public class SliceMetadata {
    private Context mContext;
    private long mExpiry;
    private RowContent mHeaderContent;
    private long mLastUpdated;
    private ListContent mListContent;
    private SliceAction mPrimaryAction;
    private Slice mSlice;
    private List<SliceAction> mSliceActions;
    private int mTemplateType;

    public static SliceMetadata from(Context context, Slice slice) {
        return new SliceMetadata(context, slice);
    }

    private SliceMetadata(Context context, Slice slice) {
        this.mSlice = slice;
        this.mContext = context;
        String str = "long";
        SliceItem find = SliceQuery.find(slice, str, "ttl", (String) null);
        if (find != null) {
            this.mExpiry = find.getLong();
        }
        SliceItem find2 = SliceQuery.find(slice, str, "last_updated", (String) null);
        if (find2 != null) {
            this.mLastUpdated = find2.getLong();
        }
        this.mListContent = new ListContent(slice);
        this.mHeaderContent = this.mListContent.getHeader();
        this.mTemplateType = this.mListContent.getHeaderTemplateType();
        this.mPrimaryAction = this.mListContent.getShortcut(this.mContext);
        this.mSliceActions = this.mListContent.getSliceActions();
        if (this.mSliceActions == null) {
            RowContent rowContent = this.mHeaderContent;
            if (rowContent != null && SliceQuery.hasHints(rowContent.getSliceItem(), "list_item")) {
                ArrayList endItems = this.mHeaderContent.getEndItems();
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < endItems.size(); i++) {
                    if (SliceQuery.find((SliceItem) endItems.get(i), "action") != null) {
                        arrayList.add(new SliceActionImpl((SliceItem) endItems.get(i)));
                    }
                }
                if (arrayList.size() > 0) {
                    this.mSliceActions = arrayList;
                }
            }
        }
    }

    public List<SliceAction> getSliceActions() {
        return this.mSliceActions;
    }

    public int getLoadingState() {
        boolean z = SliceQuery.find(this.mSlice, (String) null, "partial", (String) null) != null;
        if (!this.mListContent.isValid()) {
            return 0;
        }
        if (z) {
            return 1;
        }
        return 2;
    }

    public long getLastUpdatedTime() {
        return this.mLastUpdated;
    }

    public boolean isPermissionSlice() {
        return this.mSlice.hasHint("permission_request");
    }

    public static List<SliceAction> getSliceActions(Slice slice) {
        String str = "actions";
        String str2 = "slice";
        SliceItem find = SliceQuery.find(slice, str2, str, (String) null);
        List findAll = find != null ? SliceQuery.findAll(find, str2, new String[]{str, "shortcut"}, (String[]) null) : null;
        if (findAll == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList(findAll.size());
        for (int i = 0; i < findAll.size(); i++) {
            arrayList.add(new SliceActionImpl((SliceItem) findAll.get(i)));
        }
        return arrayList;
    }

    public boolean isExpired() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = this.mExpiry;
        return (j == 0 || j == -1 || currentTimeMillis <= j) ? false : true;
    }

    public boolean neverExpires() {
        return this.mExpiry == -1;
    }

    public long getTimeToExpiry() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = this.mExpiry;
        if (j == 0 || j == -1 || currentTimeMillis > j) {
            return 0;
        }
        return j - currentTimeMillis;
    }

    public ListContent getListContent() {
        return this.mListContent;
    }
}
