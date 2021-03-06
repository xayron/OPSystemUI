package com.oneplus.lib.widget.recyclerview;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import java.util.ArrayList;
import java.util.List;

class ChildHelper {
    final Bucket mBucket = new Bucket();
    final Callback mCallback;
    final List<View> mHiddenViews = new ArrayList();

    static class Bucket {
        long mData = 0;
        Bucket next;

        Bucket() {
        }

        /* access modifiers changed from: 0000 */
        public void set(int i) {
            if (i >= 64) {
                ensureNext();
                this.next.set(i - 64);
                return;
            }
            this.mData |= 1 << i;
        }

        private void ensureNext() {
            if (this.next == null) {
                this.next = new Bucket();
            }
        }

        /* access modifiers changed from: 0000 */
        public void clear(int i) {
            if (i >= 64) {
                Bucket bucket = this.next;
                if (bucket != null) {
                    bucket.clear(i - 64);
                    return;
                }
                return;
            }
            this.mData &= ~(1 << i);
        }

        /* access modifiers changed from: 0000 */
        public boolean get(int i) {
            if (i >= 64) {
                ensureNext();
                return this.next.get(i - 64);
            }
            return ((1 << i) & this.mData) != 0;
        }

        /* access modifiers changed from: 0000 */
        public void reset() {
            this.mData = 0;
            Bucket bucket = this.next;
            if (bucket != null) {
                bucket.reset();
            }
        }

        /* access modifiers changed from: 0000 */
        public void insert(int i, boolean z) {
            if (i >= 64) {
                ensureNext();
                this.next.insert(i - 64, z);
                return;
            }
            boolean z2 = (this.mData & Long.MIN_VALUE) != 0;
            long j = (1 << i) - 1;
            long j2 = this.mData;
            this.mData = ((j2 & (~j)) << 1) | (j2 & j);
            if (z) {
                set(i);
            } else {
                clear(i);
            }
            if (z2 || this.next != null) {
                ensureNext();
                this.next.insert(0, z2);
            }
        }

        /* access modifiers changed from: 0000 */
        public boolean remove(int i) {
            if (i >= 64) {
                ensureNext();
                return this.next.remove(i - 64);
            }
            long j = 1 << i;
            boolean z = (this.mData & j) != 0;
            this.mData &= ~j;
            long j2 = j - 1;
            long j3 = this.mData;
            this.mData = Long.rotateRight(j3 & (~j2), 1) | (j3 & j2);
            Bucket bucket = this.next;
            if (bucket != null) {
                if (bucket.get(0)) {
                    set(63);
                }
                this.next.remove(0);
            }
            return z;
        }

        /* access modifiers changed from: 0000 */
        public int countOnesBefore(int i) {
            Bucket bucket = this.next;
            if (bucket == null) {
                if (i >= 64) {
                    return Long.bitCount(this.mData);
                }
                return Long.bitCount(((1 << i) - 1) & this.mData);
            } else if (i >= 64) {
                return bucket.countOnesBefore(i - 64) + Long.bitCount(this.mData);
            } else {
                return Long.bitCount(((1 << i) - 1) & this.mData);
            }
        }

        public String toString() {
            if (this.next == null) {
                return Long.toBinaryString(this.mData);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(this.next.toString());
            sb.append("xx");
            sb.append(Long.toBinaryString(this.mData));
            return sb.toString();
        }
    }

    interface Callback {
        void addView(View view, int i);

        void attachViewToParent(View view, int i, LayoutParams layoutParams);

        View getChildAt(int i);

        int getChildCount();

        int indexOfChild(View view);

        void onEnteredHiddenState(View view);

        void onLeftHiddenState(View view);

        void removeAllViews();

        void removeViewAt(int i);
    }

    ChildHelper(Callback callback) {
        this.mCallback = callback;
    }

    private void hideViewInternal(View view) {
        this.mHiddenViews.add(view);
        this.mCallback.onEnteredHiddenState(view);
    }

    private boolean unhideViewInternal(View view) {
        if (!this.mHiddenViews.remove(view)) {
            return false;
        }
        this.mCallback.onLeftHiddenState(view);
        return true;
    }

    /* access modifiers changed from: 0000 */
    public void addView(View view, boolean z) {
        addView(view, -1, z);
    }

    /* access modifiers changed from: 0000 */
    public void addView(View view, int i, boolean z) {
        int i2;
        if (i < 0) {
            i2 = this.mCallback.getChildCount();
        } else {
            i2 = getOffset(i);
        }
        this.mBucket.insert(i2, z);
        if (z) {
            hideViewInternal(view);
        }
        this.mCallback.addView(view, i2);
    }

    private int getOffset(int i) {
        if (i < 0) {
            return -1;
        }
        int childCount = this.mCallback.getChildCount();
        int i2 = i;
        while (i2 < childCount) {
            int countOnesBefore = i - (i2 - this.mBucket.countOnesBefore(i2));
            if (countOnesBefore == 0) {
                while (this.mBucket.get(i2)) {
                    i2++;
                }
                return i2;
            }
            i2 += countOnesBefore;
        }
        return -1;
    }

    /* access modifiers changed from: 0000 */
    public void removeView(View view) {
        int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild >= 0) {
            if (this.mBucket.remove(indexOfChild)) {
                unhideViewInternal(view);
            }
            this.mCallback.removeViewAt(indexOfChild);
        }
    }

    /* access modifiers changed from: 0000 */
    public View getChildAt(int i) {
        return this.mCallback.getChildAt(getOffset(i));
    }

    /* access modifiers changed from: 0000 */
    public void removeAllViewsUnfiltered() {
        this.mBucket.reset();
        for (int size = this.mHiddenViews.size() - 1; size >= 0; size--) {
            this.mCallback.onLeftHiddenState((View) this.mHiddenViews.get(size));
            this.mHiddenViews.remove(size);
        }
        this.mCallback.removeAllViews();
    }

    /* access modifiers changed from: 0000 */
    public void attachViewToParent(View view, int i, LayoutParams layoutParams, boolean z) {
        int i2;
        if (i < 0) {
            i2 = this.mCallback.getChildCount();
        } else {
            i2 = getOffset(i);
        }
        this.mBucket.insert(i2, z);
        if (z) {
            hideViewInternal(view);
        }
        this.mCallback.attachViewToParent(view, i2, layoutParams);
    }

    /* access modifiers changed from: 0000 */
    public int getChildCount() {
        return this.mCallback.getChildCount() - this.mHiddenViews.size();
    }

    /* access modifiers changed from: 0000 */
    public int getUnfilteredChildCount() {
        return this.mCallback.getChildCount();
    }

    /* access modifiers changed from: 0000 */
    public View getUnfilteredChildAt(int i) {
        return this.mCallback.getChildAt(i);
    }

    /* access modifiers changed from: 0000 */
    public boolean isHidden(View view) {
        return this.mHiddenViews.contains(view);
    }

    /* access modifiers changed from: 0000 */
    public void hide(View view) {
        int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild >= 0) {
            this.mBucket.set(indexOfChild);
            hideViewInternal(view);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("view is not a child, cannot hide ");
        sb.append(view);
        throw new IllegalArgumentException(sb.toString());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.mBucket.toString());
        sb.append(", hidden list:");
        sb.append(this.mHiddenViews.size());
        return sb.toString();
    }

    /* access modifiers changed from: 0000 */
    public boolean removeViewIfHidden(View view) {
        int indexOfChild = this.mCallback.indexOfChild(view);
        if (indexOfChild == -1) {
            unhideViewInternal(view);
            return true;
        } else if (!this.mBucket.get(indexOfChild)) {
            return false;
        } else {
            this.mBucket.remove(indexOfChild);
            unhideViewInternal(view);
            this.mCallback.removeViewAt(indexOfChild);
            return true;
        }
    }
}
