package android.support.p000v4.app;

/* renamed from: android.support.v4.app.FragmentTransaction */
public abstract class FragmentTransaction {
    public abstract FragmentTransaction add(int i, Fragment fragment, String str);

    public abstract FragmentTransaction attach(Fragment fragment);

    public abstract int commit();

    public abstract FragmentTransaction detach(Fragment fragment);
}
