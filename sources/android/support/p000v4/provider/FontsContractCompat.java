package android.support.p000v4.provider;

import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Build.VERSION;
import android.os.CancellationSignal;
import android.support.p000v4.content.res.FontResourcesParserCompat;
import android.support.p000v4.graphics.TypefaceCompat;
import android.support.p000v4.graphics.TypefaceCompatUtil;
import android.support.p000v4.provider.SelfDestructiveThread.ReplyCallback;
import android.support.p000v4.util.LruCache;
import android.support.p000v4.util.Preconditions;
import android.support.p000v4.util.SimpleArrayMap;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* renamed from: android.support.v4.provider.FontsContractCompat */
public class FontsContractCompat {
    private static final SelfDestructiveThread sBackgroundThread = new SelfDestructiveThread("fonts", 10, 10000);
    private static final Comparator<byte[]> sByteArrayComparator = new Comparator<byte[]>() {
        public int compare(byte[] bArr, byte[] bArr2) {
            int i;
            int i2;
            if (bArr.length != bArr2.length) {
                i2 = bArr.length;
                i = bArr2.length;
            } else {
                int i3 = 0;
                while (i3 < bArr.length) {
                    if (bArr[i3] != bArr2[i3]) {
                        i2 = bArr[i3];
                        i = bArr2[i3];
                    } else {
                        i3++;
                    }
                }
                return 0;
            }
            return i2 - i;
        }
    };
    static final Object sLock = new Object();
    static final SimpleArrayMap<String, ArrayList<ReplyCallback<TypefaceResult>>> sPendingReplies = new SimpleArrayMap<>();
    static final LruCache<String, Typeface> sTypefaceCache = new LruCache<>(16);

    /* renamed from: android.support.v4.provider.FontsContractCompat$FontFamilyResult */
    public static class FontFamilyResult {
        private final FontInfo[] mFonts;
        private final int mStatusCode;

        public FontFamilyResult(int i, FontInfo[] fontInfoArr) {
            this.mStatusCode = i;
            this.mFonts = fontInfoArr;
        }

        public int getStatusCode() {
            return this.mStatusCode;
        }

        public FontInfo[] getFonts() {
            return this.mFonts;
        }
    }

    /* renamed from: android.support.v4.provider.FontsContractCompat$FontInfo */
    public static class FontInfo {
        private final boolean mItalic;
        private final int mResultCode;
        private final int mTtcIndex;
        private final Uri mUri;
        private final int mWeight;

        public FontInfo(Uri uri, int i, int i2, boolean z, int i3) {
            Preconditions.checkNotNull(uri);
            this.mUri = uri;
            this.mTtcIndex = i;
            this.mWeight = i2;
            this.mItalic = z;
            this.mResultCode = i3;
        }

        public Uri getUri() {
            return this.mUri;
        }

        public int getTtcIndex() {
            return this.mTtcIndex;
        }

        public int getWeight() {
            return this.mWeight;
        }

        public boolean isItalic() {
            return this.mItalic;
        }

        public int getResultCode() {
            return this.mResultCode;
        }
    }

    /* renamed from: android.support.v4.provider.FontsContractCompat$TypefaceResult */
    private static final class TypefaceResult {
        final int mResult;
        final Typeface mTypeface;

        TypefaceResult(Typeface typeface, int i) {
            this.mTypeface = typeface;
            this.mResult = i;
        }
    }

    static TypefaceResult getFontInternal(Context context, FontRequest fontRequest, int i) {
        try {
            FontFamilyResult fetchFonts = fetchFonts(context, null, fontRequest);
            int i2 = -3;
            if (fetchFonts.getStatusCode() == 0) {
                Typeface createFromFontInfo = TypefaceCompat.createFromFontInfo(context, null, fetchFonts.getFonts(), i);
                if (createFromFontInfo != null) {
                    i2 = 0;
                }
                return new TypefaceResult(createFromFontInfo, i2);
            }
            if (fetchFonts.getStatusCode() == 1) {
                i2 = -2;
            }
            return new TypefaceResult(null, i2);
        } catch (NameNotFoundException unused) {
            return new TypefaceResult(null, -1);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0078, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0089, code lost:
        sBackgroundThread.postAndReply(r1, new android.support.p000v4.provider.FontsContractCompat.C00593());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0093, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Typeface getFontSync(final android.content.Context r2, final android.support.p000v4.provider.FontRequest r3, final android.support.p000v4.content.res.ResourcesCompat.FontCallback r4, final android.os.Handler r5, boolean r6, int r7, final int r8) {
        /*
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r3.getIdentifier()
            r0.append(r1)
            java.lang.String r1 = "-"
            r0.append(r1)
            r0.append(r8)
            java.lang.String r0 = r0.toString()
            android.support.v4.util.LruCache<java.lang.String, android.graphics.Typeface> r1 = sTypefaceCache
            java.lang.Object r1 = r1.get(r0)
            android.graphics.Typeface r1 = (android.graphics.Typeface) r1
            if (r1 == 0) goto L_0x0028
            if (r4 == 0) goto L_0x0027
            r4.onFontRetrieved(r1)
        L_0x0027:
            return r1
        L_0x0028:
            if (r6 == 0) goto L_0x0043
            r1 = -1
            if (r7 != r1) goto L_0x0043
            android.support.v4.provider.FontsContractCompat$TypefaceResult r2 = getFontInternal(r2, r3, r8)
            if (r4 == 0) goto L_0x0040
            int r3 = r2.mResult
            if (r3 != 0) goto L_0x003d
            android.graphics.Typeface r3 = r2.mTypeface
            r4.callbackSuccessAsync(r3, r5)
            goto L_0x0040
        L_0x003d:
            r4.callbackFailAsync(r3, r5)
        L_0x0040:
            android.graphics.Typeface r2 = r2.mTypeface
            return r2
        L_0x0043:
            android.support.v4.provider.FontsContractCompat$1 r1 = new android.support.v4.provider.FontsContractCompat$1
            r1.<init>(r2, r3, r8, r0)
            r2 = 0
            if (r6 == 0) goto L_0x0056
            android.support.v4.provider.SelfDestructiveThread r3 = sBackgroundThread     // Catch:{ InterruptedException -> 0x0055 }
            java.lang.Object r3 = r3.postAndWait(r1, r7)     // Catch:{ InterruptedException -> 0x0055 }
            android.support.v4.provider.FontsContractCompat$TypefaceResult r3 = (android.support.p000v4.provider.FontsContractCompat.TypefaceResult) r3     // Catch:{ InterruptedException -> 0x0055 }
            android.graphics.Typeface r2 = r3.mTypeface     // Catch:{ InterruptedException -> 0x0055 }
        L_0x0055:
            return r2
        L_0x0056:
            if (r4 != 0) goto L_0x005a
            r3 = r2
            goto L_0x005f
        L_0x005a:
            android.support.v4.provider.FontsContractCompat$2 r3 = new android.support.v4.provider.FontsContractCompat$2
            r3.<init>(r4, r5)
        L_0x005f:
            java.lang.Object r4 = sLock
            monitor-enter(r4)
            android.support.v4.util.SimpleArrayMap<java.lang.String, java.util.ArrayList<android.support.v4.provider.SelfDestructiveThread$ReplyCallback<android.support.v4.provider.FontsContractCompat$TypefaceResult>>> r5 = sPendingReplies     // Catch:{ all -> 0x0094 }
            boolean r5 = r5.containsKey(r0)     // Catch:{ all -> 0x0094 }
            if (r5 == 0) goto L_0x0079
            if (r3 == 0) goto L_0x0077
            android.support.v4.util.SimpleArrayMap<java.lang.String, java.util.ArrayList<android.support.v4.provider.SelfDestructiveThread$ReplyCallback<android.support.v4.provider.FontsContractCompat$TypefaceResult>>> r5 = sPendingReplies     // Catch:{ all -> 0x0094 }
            java.lang.Object r5 = r5.get(r0)     // Catch:{ all -> 0x0094 }
            java.util.ArrayList r5 = (java.util.ArrayList) r5     // Catch:{ all -> 0x0094 }
            r5.add(r3)     // Catch:{ all -> 0x0094 }
        L_0x0077:
            monitor-exit(r4)     // Catch:{ all -> 0x0094 }
            return r2
        L_0x0079:
            if (r3 == 0) goto L_0x0088
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x0094 }
            r5.<init>()     // Catch:{ all -> 0x0094 }
            r5.add(r3)     // Catch:{ all -> 0x0094 }
            android.support.v4.util.SimpleArrayMap<java.lang.String, java.util.ArrayList<android.support.v4.provider.SelfDestructiveThread$ReplyCallback<android.support.v4.provider.FontsContractCompat$TypefaceResult>>> r3 = sPendingReplies     // Catch:{ all -> 0x0094 }
            r3.put(r0, r5)     // Catch:{ all -> 0x0094 }
        L_0x0088:
            monitor-exit(r4)     // Catch:{ all -> 0x0094 }
            android.support.v4.provider.SelfDestructiveThread r3 = sBackgroundThread
            android.support.v4.provider.FontsContractCompat$3 r4 = new android.support.v4.provider.FontsContractCompat$3
            r4.<init>(r0)
            r3.postAndReply(r1, r4)
            return r2
        L_0x0094:
            r2 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0094 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p000v4.provider.FontsContractCompat.getFontSync(android.content.Context, android.support.v4.provider.FontRequest, android.support.v4.content.res.ResourcesCompat$FontCallback, android.os.Handler, boolean, int, int):android.graphics.Typeface");
    }

    public static Map<Uri, ByteBuffer> prepareFontData(Context context, FontInfo[] fontInfoArr, CancellationSignal cancellationSignal) {
        HashMap hashMap = new HashMap();
        for (FontInfo fontInfo : fontInfoArr) {
            if (fontInfo.getResultCode() == 0) {
                Uri uri = fontInfo.getUri();
                if (!hashMap.containsKey(uri)) {
                    hashMap.put(uri, TypefaceCompatUtil.mmap(context, cancellationSignal, uri));
                }
            }
        }
        return Collections.unmodifiableMap(hashMap);
    }

    public static FontFamilyResult fetchFonts(Context context, CancellationSignal cancellationSignal, FontRequest fontRequest) throws NameNotFoundException {
        ProviderInfo provider = getProvider(context.getPackageManager(), fontRequest, context.getResources());
        if (provider == null) {
            return new FontFamilyResult(1, null);
        }
        return new FontFamilyResult(0, getFontFromProvider(context, fontRequest, provider.authority, cancellationSignal));
    }

    public static ProviderInfo getProvider(PackageManager packageManager, FontRequest fontRequest, Resources resources) throws NameNotFoundException {
        String providerAuthority = fontRequest.getProviderAuthority();
        ProviderInfo resolveContentProvider = packageManager.resolveContentProvider(providerAuthority, 0);
        if (resolveContentProvider == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("No package found for authority: ");
            sb.append(providerAuthority);
            throw new NameNotFoundException(sb.toString());
        } else if (resolveContentProvider.packageName.equals(fontRequest.getProviderPackage())) {
            List convertToByteArrayList = convertToByteArrayList(packageManager.getPackageInfo(resolveContentProvider.packageName, 64).signatures);
            Collections.sort(convertToByteArrayList, sByteArrayComparator);
            List certificates = getCertificates(fontRequest, resources);
            for (int i = 0; i < certificates.size(); i++) {
                ArrayList arrayList = new ArrayList((Collection) certificates.get(i));
                Collections.sort(arrayList, sByteArrayComparator);
                if (equalsByteArrayList(convertToByteArrayList, arrayList)) {
                    return resolveContentProvider;
                }
            }
            return null;
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Found content provider ");
            sb2.append(providerAuthority);
            sb2.append(", but package was not ");
            sb2.append(fontRequest.getProviderPackage());
            throw new NameNotFoundException(sb2.toString());
        }
    }

    private static List<List<byte[]>> getCertificates(FontRequest fontRequest, Resources resources) {
        if (fontRequest.getCertificates() != null) {
            return fontRequest.getCertificates();
        }
        return FontResourcesParserCompat.readCerts(resources, fontRequest.getCertificatesArrayResId());
    }

    private static boolean equalsByteArrayList(List<byte[]> list, List<byte[]> list2) {
        if (list.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (!Arrays.equals((byte[]) list.get(i), (byte[]) list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static List<byte[]> convertToByteArrayList(Signature[] signatureArr) {
        ArrayList arrayList = new ArrayList();
        for (Signature byteArray : signatureArr) {
            arrayList.add(byteArray.toByteArray());
        }
        return arrayList;
    }

    /* JADX INFO: finally extract failed */
    static FontInfo[] getFontFromProvider(Context context, FontRequest fontRequest, String str, CancellationSignal cancellationSignal) {
        Cursor cursor;
        Uri uri;
        String str2 = str;
        ArrayList arrayList = new ArrayList();
        String str3 = "content";
        Uri build = new Builder().scheme(str3).authority(str2).build();
        Uri build2 = new Builder().scheme(str3).authority(str2).appendPath("file").build();
        Cursor cursor2 = null;
        try {
            if (VERSION.SDK_INT > 16) {
                cursor = context.getContentResolver().query(build, new String[]{"_id", "file_id", "font_ttc_index", "font_variation_settings", "font_weight", "font_italic", "result_code"}, "query = ?", new String[]{fontRequest.getQuery()}, null, cancellationSignal);
            } else {
                cursor = context.getContentResolver().query(build, new String[]{"_id", "file_id", "font_ttc_index", "font_variation_settings", "font_weight", "font_italic", "result_code"}, "query = ?", new String[]{fontRequest.getQuery()}, null);
            }
            if (cursor != null && cursor.getCount() > 0) {
                int columnIndex = cursor.getColumnIndex("result_code");
                ArrayList arrayList2 = new ArrayList();
                int columnIndex2 = cursor.getColumnIndex("_id");
                int columnIndex3 = cursor.getColumnIndex("file_id");
                int columnIndex4 = cursor.getColumnIndex("font_ttc_index");
                int columnIndex5 = cursor.getColumnIndex("font_weight");
                int columnIndex6 = cursor.getColumnIndex("font_italic");
                while (cursor.moveToNext()) {
                    int i = columnIndex != -1 ? cursor.getInt(columnIndex) : 0;
                    int i2 = columnIndex4 != -1 ? cursor.getInt(columnIndex4) : 0;
                    if (columnIndex3 == -1) {
                        uri = ContentUris.withAppendedId(build, cursor.getLong(columnIndex2));
                    } else {
                        uri = ContentUris.withAppendedId(build2, cursor.getLong(columnIndex3));
                    }
                    FontInfo fontInfo = new FontInfo(uri, i2, columnIndex5 != -1 ? cursor.getInt(columnIndex5) : 400, columnIndex6 != -1 && cursor.getInt(columnIndex6) == 1, i);
                    arrayList2.add(fontInfo);
                }
                arrayList = arrayList2;
            }
            if (cursor != null) {
                cursor.close();
            }
            return (FontInfo[]) arrayList.toArray(new FontInfo[0]);
        } catch (Throwable th) {
            if (cursor2 != null) {
                cursor2.close();
            }
            throw th;
        }
    }
}
