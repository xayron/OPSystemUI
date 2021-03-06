package androidx.slice;

import android.net.Uri;

public class SliceStructure {
    private final String mStructure;
    private final Uri mUri;

    public SliceStructure(Slice slice) {
        StringBuilder sb = new StringBuilder();
        getStructure(slice, sb);
        this.mStructure = sb.toString();
        this.mUri = slice.getUri();
    }

    public SliceStructure(SliceItem sliceItem) {
        StringBuilder sb = new StringBuilder();
        getStructure(sliceItem, sb);
        this.mStructure = sb.toString();
        if (!"action".equals(sliceItem.getFormat())) {
            if (!"slice".equals(sliceItem.getFormat())) {
                this.mUri = null;
                return;
            }
        }
        this.mUri = sliceItem.getSlice().getUri();
    }

    public Uri getUri() {
        return this.mUri;
    }

    public int hashCode() {
        return this.mStructure.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SliceStructure)) {
            return false;
        }
        return this.mStructure.equals(((SliceStructure) obj).mStructure);
    }

    private static void getStructure(Slice slice, StringBuilder sb) {
        sb.append("s{");
        for (SliceItem structure : slice.getItems()) {
            getStructure(structure, sb);
        }
        sb.append("}");
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void getStructure(androidx.slice.SliceItem r5, java.lang.StringBuilder r6) {
        /*
            java.lang.String r0 = r5.getFormat()
            int r1 = r0.hashCode()
            r2 = 3
            r3 = 2
            r4 = 1
            switch(r1) {
                case -1422950858: goto L_0x0056;
                case -1377881982: goto L_0x004c;
                case 104431: goto L_0x0042;
                case 3327612: goto L_0x0038;
                case 3556653: goto L_0x002d;
                case 100313435: goto L_0x0023;
                case 100358090: goto L_0x0019;
                case 109526418: goto L_0x000f;
                default: goto L_0x000e;
            }
        L_0x000e:
            goto L_0x0060
        L_0x000f:
            java.lang.String r1 = "slice"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0060
            r0 = 0
            goto L_0x0061
        L_0x0019:
            java.lang.String r1 = "input"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0060
            r0 = 6
            goto L_0x0061
        L_0x0023:
            java.lang.String r1 = "image"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0060
            r0 = r2
            goto L_0x0061
        L_0x002d:
            java.lang.String r1 = "text"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0060
            r0 = r3
            goto L_0x0061
        L_0x0038:
            java.lang.String r1 = "long"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0060
            r0 = 5
            goto L_0x0061
        L_0x0042:
            java.lang.String r1 = "int"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0060
            r0 = 4
            goto L_0x0061
        L_0x004c:
            java.lang.String r1 = "bundle"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0060
            r0 = 7
            goto L_0x0061
        L_0x0056:
            java.lang.String r1 = "action"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0060
            r0 = r4
            goto L_0x0061
        L_0x0060:
            r0 = -1
        L_0x0061:
            if (r0 == 0) goto L_0x0094
            if (r0 == r4) goto L_0x0076
            if (r0 == r3) goto L_0x0070
            if (r0 == r2) goto L_0x006a
            goto L_0x009b
        L_0x006a:
            r5 = 105(0x69, float:1.47E-43)
            r6.append(r5)
            goto L_0x009b
        L_0x0070:
            r5 = 116(0x74, float:1.63E-43)
            r6.append(r5)
            goto L_0x009b
        L_0x0076:
            r0 = 97
            r6.append(r0)
            java.lang.String r0 = r5.getSubType()
            java.lang.String r1 = "range"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x008c
            r0 = 114(0x72, float:1.6E-43)
            r6.append(r0)
        L_0x008c:
            androidx.slice.Slice r5 = r5.getSlice()
            getStructure(r5, r6)
            goto L_0x009b
        L_0x0094:
            androidx.slice.Slice r5 = r5.getSlice()
            getStructure(r5, r6)
        L_0x009b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.SliceStructure.getStructure(androidx.slice.SliceItem, java.lang.StringBuilder):void");
    }
}
