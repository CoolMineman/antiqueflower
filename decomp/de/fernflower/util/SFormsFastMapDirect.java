/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.util;

import de.fernflower.util.FastSparseSetFactory$FastSparseSet;
import de.fernflower.util.SFormsFastMapDirect$1;
import java.util.ArrayList;
import java.util.List;

public final class SFormsFastMapDirect {
    private int size;
    private FastSparseSetFactory$FastSparseSet[][] elements = new FastSparseSetFactory$FastSparseSet[3][];
    private int[][] next = new int[3][];

    public SFormsFastMapDirect() {
        this(true);
    }

    private SFormsFastMapDirect(boolean n) {
        if (n != 0) {
            n = 2;
            while (n >= 0) {
                this.elements[n] = new FastSparseSetFactory$FastSparseSet[0];
                this.next[n] = new int[0];
                --n;
            }
        }
    }

    public SFormsFastMapDirect(SFormsFastMapDirect sFormsFastMapDirect) {
        int n = 2;
        while (n >= 0) {
            FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray = sFormsFastMapDirect.elements[n];
            int[] nArray = sFormsFastMapDirect.next[n];
            int n2 = fastSparseSetFactory$FastSparseSetArray.length;
            FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray2 = new FastSparseSetFactory$FastSparseSet[n2];
            int[] nArray2 = new int[n2];
            System.arraycopy(fastSparseSetFactory$FastSparseSetArray, 0, fastSparseSetFactory$FastSparseSetArray2, 0, n2);
            System.arraycopy(nArray, 0, nArray2, 0, n2);
            this.elements[n] = fastSparseSetFactory$FastSparseSetArray2;
            this.next[n] = nArray2;
            this.size = sFormsFastMapDirect.size;
            --n;
        }
    }

    public final SFormsFastMapDirect getCopy() {
        SFormsFastMapDirect sFormsFastMapDirect = new SFormsFastMapDirect(false);
        new SFormsFastMapDirect(false).size = this.size;
        FastSparseSetFactory$FastSparseSet[][] fastSparseSetFactory$FastSparseSetArray = sFormsFastMapDirect.elements;
        int[][] nArray = sFormsFastMapDirect.next;
        int n = 2;
        while (n >= 0) {
            FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray2 = this.elements[n];
            int n2 = fastSparseSetFactory$FastSparseSetArray2.length;
            if (n2 > 0) {
                int[] nArray2 = this.next[n];
                FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray3 = new FastSparseSetFactory$FastSparseSet[n2];
                Object object = new int[n2];
                System.arraycopy(nArray2, 0, object, 0, n2);
                fastSparseSetFactory$FastSparseSetArray[n] = fastSparseSetFactory$FastSparseSetArray3;
                nArray[n] = object;
                n2 = 0;
                do {
                    FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet = fastSparseSetFactory$FastSparseSetArray2[n2];
                    object = fastSparseSetFactory$FastSparseSet;
                    if (fastSparseSetFactory$FastSparseSet == null) continue;
                    fastSparseSetFactory$FastSparseSetArray3[n2] = ((FastSparseSetFactory$FastSparseSet)object).getCopy();
                } while ((n2 = nArray2[n2]) != 0);
            } else {
                fastSparseSetFactory$FastSparseSetArray[n] = new FastSparseSetFactory$FastSparseSet[0];
                nArray[n] = new int[0];
            }
            --n;
        }
        return sFormsFastMapDirect;
    }

    public final int size() {
        return this.size;
    }

    public final boolean isEmpty() {
        return this.size == 0;
    }

    public final void putInternal(int n, FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet) {
        boolean bl = false;
        SFormsFastMapDirect sFormsFastMapDirect = this;
        int n2 = 0;
        if (n < 0) {
            n2 = 2;
            n = -n;
        } else if (n >= 10000) {
            n2 = 1;
            n -= 10000;
        }
        FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray = sFormsFastMapDirect.elements[n2];
        if (n >= fastSparseSetFactory$FastSparseSetArray.length) {
            fastSparseSetFactory$FastSparseSetArray = sFormsFastMapDirect.ensureCapacity(n2, n + 1, false);
        }
        FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet2 = fastSparseSetFactory$FastSparseSetArray[n];
        fastSparseSetFactory$FastSparseSetArray[n] = fastSparseSetFactory$FastSparseSet;
        int[] nArray = sFormsFastMapDirect.next[n2];
        if (fastSparseSetFactory$FastSparseSet2 == null && fastSparseSetFactory$FastSparseSet != null) {
            ++sFormsFastMapDirect.size;
            SFormsFastMapDirect.changeNext(nArray, n, nArray[n], n);
            return;
        }
        if (fastSparseSetFactory$FastSparseSet2 != null && fastSparseSetFactory$FastSparseSet == null) {
            --sFormsFastMapDirect.size;
            SFormsFastMapDirect.changeNext(nArray, n, n, nArray[n]);
        }
    }

    public final void removeAllFields() {
        FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray = this.elements[2];
        int[] nArray = this.next[2];
        int n = fastSparseSetFactory$FastSparseSetArray.length - 1;
        while (n >= 0) {
            if (fastSparseSetFactory$FastSparseSetArray[n] != null) {
                fastSparseSetFactory$FastSparseSetArray[n] = null;
                --this.size;
            }
            nArray[n] = 0;
            --n;
        }
    }

    private static void changeNext(int[] nArray, int n, int n2, int n3) {
        while (--n >= 0) {
            if (nArray[n] != n2) break;
            nArray[n] = n3;
            --n;
        }
    }

    public final boolean containsKey(int n) {
        return this.get(n) != null;
    }

    public final FastSparseSetFactory$FastSparseSet get(int n) {
        int n2 = 0;
        if (n < 0) {
            n2 = 2;
            n = -n;
        } else if (n >= 10000) {
            n2 = 1;
            n -= 10000;
        }
        FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray = fastSparseSetFactory$FastSparseSetArray.elements[n2];
        if (n < fastSparseSetFactory$FastSparseSetArray.length) {
            return fastSparseSetFactory$FastSparseSetArray[n];
        }
        return null;
    }

    public final void complement(SFormsFastMapDirect sFormsFastMapDirect) {
        int n = 2;
        while (n >= 0) {
            FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray = this.elements[n];
            if (fastSparseSetFactory$FastSparseSetArray.length != 0) {
                FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray2 = sFormsFastMapDirect.elements[n];
                int[] nArray = this.next[n];
                int n2 = 0;
                do {
                    FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet;
                    if ((fastSparseSetFactory$FastSparseSet = fastSparseSetFactory$FastSparseSetArray[n2]) == null) continue;
                    if (n2 >= fastSparseSetFactory$FastSparseSetArray2.length) break;
                    FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet2 = fastSparseSetFactory$FastSparseSetArray2[n2];
                    if (fastSparseSetFactory$FastSparseSet2 == null) continue;
                    fastSparseSetFactory$FastSparseSet.complement(fastSparseSetFactory$FastSparseSet2);
                    if (!fastSparseSetFactory$FastSparseSet.isEmpty()) continue;
                    fastSparseSetFactory$FastSparseSetArray[n2] = null;
                    --this.size;
                    SFormsFastMapDirect.changeNext(nArray, n2, n2, nArray[n2]);
                } while ((n2 = nArray[n2]) != 0);
            }
            --n;
        }
    }

    public final void intersection(SFormsFastMapDirect sFormsFastMapDirect) {
        int n = 2;
        while (n >= 0) {
            FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray = this.elements[n];
            if (fastSparseSetFactory$FastSparseSetArray.length != 0) {
                FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray2 = sFormsFastMapDirect.elements[n];
                int[] nArray = this.next[n];
                int n2 = 0;
                do {
                    FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet;
                    if ((fastSparseSetFactory$FastSparseSet = fastSparseSetFactory$FastSparseSetArray[n2]) == null) continue;
                    FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet2 = null;
                    if (n2 < fastSparseSetFactory$FastSparseSetArray2.length) {
                        fastSparseSetFactory$FastSparseSet2 = fastSparseSetFactory$FastSparseSetArray2[n2];
                    }
                    if (fastSparseSetFactory$FastSparseSet2 != null) {
                        fastSparseSetFactory$FastSparseSet.intersection(fastSparseSetFactory$FastSparseSet2);
                    }
                    if (fastSparseSetFactory$FastSparseSet2 != null && !fastSparseSetFactory$FastSparseSet.isEmpty()) continue;
                    fastSparseSetFactory$FastSparseSetArray[n2] = null;
                    --this.size;
                    SFormsFastMapDirect.changeNext(nArray, n2, n2, nArray[n2]);
                } while ((n2 = nArray[n2]) != 0);
            }
            --n;
        }
    }

    public final void union(SFormsFastMapDirect sFormsFastMapDirect) {
        int n = 2;
        while (n >= 0) {
            FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray = sFormsFastMapDirect.elements[n];
            if (fastSparseSetFactory$FastSparseSetArray.length != 0) {
                FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray2 = this.elements[n];
                int[] nArray = this.next[n];
                int[] nArray2 = sFormsFastMapDirect.next[n];
                int n2 = 0;
                do {
                    FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet;
                    if (n2 >= fastSparseSetFactory$FastSparseSetArray2.length) {
                        fastSparseSetFactory$FastSparseSetArray2 = this.ensureCapacity(n, fastSparseSetFactory$FastSparseSetArray.length, true);
                        nArray = this.next[n];
                    }
                    if ((fastSparseSetFactory$FastSparseSet = fastSparseSetFactory$FastSparseSetArray[n2]) == null) continue;
                    FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet2 = fastSparseSetFactory$FastSparseSetArray2[n2];
                    if (fastSparseSetFactory$FastSparseSet2 == null) {
                        fastSparseSetFactory$FastSparseSetArray2[n2] = fastSparseSetFactory$FastSparseSet.getCopy();
                        ++this.size;
                        SFormsFastMapDirect.changeNext(nArray, n2, nArray[n2], n2);
                        continue;
                    }
                    fastSparseSetFactory$FastSparseSet2.union(fastSparseSetFactory$FastSparseSet);
                } while ((n2 = nArray2[n2]) != 0);
            }
            --n;
        }
    }

    public final List entryList() {
        ArrayList<SFormsFastMapDirect$1> arrayList = new ArrayList<SFormsFastMapDirect$1>();
        int n = 2;
        while (n >= 0) {
            int n2 = 0;
            FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray = this.elements[n];
            int n3 = fastSparseSetFactory$FastSparseSetArray.length;
            int n4 = 0;
            while (n4 < n3) {
                FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet = fastSparseSetFactory$FastSparseSetArray[n4];
                if (fastSparseSetFactory$FastSparseSet != null) {
                    int n5 = n == 0 ? n2 : (n == 1 ? n2 + 10000 : -n2);
                    arrayList.add(new SFormsFastMapDirect$1(n5, fastSparseSetFactory$FastSparseSet));
                }
                ++n2;
                ++n4;
            }
            --n;
        }
        return arrayList;
    }

    private FastSparseSetFactory$FastSparseSet[] ensureCapacity(int n, int n2, boolean bl) {
        FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray = this.elements[n];
        int[] nArray = this.next[n];
        int n3 = n2;
        if (!bl && n2 > (n3 = 2 * fastSparseSetFactory$FastSparseSetArray.length / 3 + 1)) {
            n3 = n2;
        }
        FastSparseSetFactory$FastSparseSet[] fastSparseSetFactory$FastSparseSetArray2 = new FastSparseSetFactory$FastSparseSet[n3];
        System.arraycopy(fastSparseSetFactory$FastSparseSetArray, 0, fastSparseSetFactory$FastSparseSetArray2, 0, fastSparseSetFactory$FastSparseSetArray.length);
        int[] nArray2 = new int[n3];
        System.arraycopy(nArray, 0, nArray2, 0, nArray.length);
        this.elements[n] = fastSparseSetFactory$FastSparseSetArray2;
        this.next[n] = nArray2;
        return fastSparseSetFactory$FastSparseSetArray2;
    }
}

