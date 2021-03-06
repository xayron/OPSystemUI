package android.support.p000v4.provider;

import android.support.p000v4.util.Preconditions;
import android.util.Base64;
import java.util.List;

/* renamed from: android.support.v4.provider.FontRequest */
public final class FontRequest {
    private final List<List<byte[]>> mCertificates;
    private final int mCertificatesArray = 0;
    private final String mIdentifier;
    private final String mProviderAuthority;
    private final String mProviderPackage;
    private final String mQuery;

    public FontRequest(String str, String str2, String str3, List<List<byte[]>> list) {
        Preconditions.checkNotNull(str);
        this.mProviderAuthority = str;
        Preconditions.checkNotNull(str2);
        this.mProviderPackage = str2;
        Preconditions.checkNotNull(str3);
        this.mQuery = str3;
        Preconditions.checkNotNull(list);
        this.mCertificates = list;
        StringBuilder sb = new StringBuilder(this.mProviderAuthority);
        String str4 = "-";
        sb.append(str4);
        sb.append(this.mProviderPackage);
        sb.append(str4);
        sb.append(this.mQuery);
        this.mIdentifier = sb.toString();
    }

    public String getProviderAuthority() {
        return this.mProviderAuthority;
    }

    public String getProviderPackage() {
        return this.mProviderPackage;
    }

    public String getQuery() {
        return this.mQuery;
    }

    public List<List<byte[]>> getCertificates() {
        return this.mCertificates;
    }

    public int getCertificatesArrayResId() {
        return this.mCertificatesArray;
    }

    public String getIdentifier() {
        return this.mIdentifier;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("FontRequest {mProviderAuthority: ");
        sb2.append(this.mProviderAuthority);
        sb2.append(", mProviderPackage: ");
        sb2.append(this.mProviderPackage);
        sb2.append(", mQuery: ");
        sb2.append(this.mQuery);
        sb2.append(", mCertificates:");
        sb.append(sb2.toString());
        for (int i = 0; i < this.mCertificates.size(); i++) {
            sb.append(" [");
            List list = (List) this.mCertificates.get(i);
            for (int i2 = 0; i2 < list.size(); i2++) {
                sb.append(" \"");
                sb.append(Base64.encodeToString((byte[]) list.get(i2), 0));
                sb.append("\"");
            }
            sb.append(" ]");
        }
        sb.append("}");
        StringBuilder sb3 = new StringBuilder();
        sb3.append("mCertificatesArray: ");
        sb3.append(this.mCertificatesArray);
        sb.append(sb3.toString());
        return sb.toString();
    }
}
