/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.util;

import de.fernflower.util.FastSparseSetFactory;
import de.fernflower.util.FastSparseSetFactory$FastSparseSetIterator;
import de.fernflower.util.VBStyleCollection;
import java.util.Iterator;

public final class FastSparseSetFactory$FastSparseSet
implements Iterable {
    private FastSparseSetFactory factory;
    private VBStyleCollection colValuesInternal;
    private int[] data;
    private int[] next;

    private FastSparseSetFactory$FastSparseSet(FastSparseSetFactory fastSparseSetFactory) {
        this.factory = fastSparseSetFactory;
        this.colValuesInternal = FastSparseSetFactory.access$0(fastSparseSetFactory);
        int n = fastSparseSetFactory.getLastBlock() + 1;
        this.data = new int[n];
        this.next = new int[n];
    }

    private FastSparseSetFactory$FastSparseSet(FastSparseSetFactory fastSparseSetFactory, int[] nArray, int[] nArray2) {
        this.factory = fastSparseSetFactory;
        this.colValuesInternal = FastSparseSetFactory.access$0(fastSparseSetFactory);
        this.data = nArray;
        this.next = nArray2;
    }

    public final FastSparseSetFactory$FastSparseSet getCopy() {
        int n = this.data.length;
        int[] nArray = new int[n];
        int[] nArray2 = new int[n];
        System.arraycopy(this.data, 0, nArray, 0, n);
        System.arraycopy(this.next, 0, nArray2, 0, n);
        return new FastSparseSetFactory$FastSparseSet(this.factory, nArray, nArray2);
    }

    private int[] ensureCapacity(int n) {
        int n2 = this.data.length;
        if (n2 == 0) {
            n2 = 1;
        }
        while (n2 <= n) {
            n2 <<= 1;
        }
        int[] nArray = new int[n2];
        System.arraycopy(this.data, 0, nArray, 0, this.data.length);
        this.data = nArray;
        int[] nArray2 = new int[n2];
        System.arraycopy(this.next, 0, nArray2, 0, this.next.length);
        this.next = nArray2;
        return nArray;
    }

    public final void add(Object object) {
        int n;
        int[] nArray = (int[])this.colValuesInternal.getWithKey(object);
        if (nArray == null) {
            nArray = FastSparseSetFactory.access$1(this.factory, object);
        }
        if ((n = nArray[0]) >= this.data.length) {
            this.ensureCapacity(n);
        }
        int n2 = n;
        this.data[n2] = this.data[n2] | nArray[1];
        FastSparseSetFactory$FastSparseSet.changeNext(this.next, n, this.next[n], n);
    }

    public final void remove(Object object) {
        int n;
        int[] nArray = (int[])this.colValuesInternal.getWithKey(object);
        if (nArray == null) {
            nArray = FastSparseSetFactory.access$1(this.factory, object);
        }
        if ((n = nArray[0]) < this.data.length) {
            int n2 = n;
            this.data[n2] = this.data[n2] & ~nArray[1];
            if (this.data[n] == 0) {
                FastSparseSetFactory$FastSparseSet.changeNext(this.next, n, n, this.next[n]);
            }
        }
    }

    public final boolean contains(Object object) {
        int[] nArray = (int[])this.colValuesInternal.getWithKey(object);
        if (nArray == null) {
            nArray = FastSparseSetFactory.access$1(this.factory, object);
        }
        return nArray[0] < this.data.length && (this.data[nArray[0]] & nArray[1]) != 0;
    }

    private static void changeNext(int[] nArray, int n, int n2, int n3) {
        while (--n >= 0) {
            if (nArray[n] != n2) break;
            nArray[n] = n3;
            --n;
        }
    }

    public final void union(FastSparseSetFactory$FastSparseSet object) {
        Object object2;
        int[] nArray = null;
        int[] nArray2 = ((FastSparseSetFactory$FastSparseSet)object).data;
        object = ((FastSparseSetFactory$FastSparseSet)object).next;
        nArray = this.data;
        int n = this.data.length;
        Object object3 = 0;
        do {
            if (object3 >= n) {
                nArray = this.ensureCapacity(nArray2.length - 1);
            }
            boolean bl = nArray[object3] == 0;
            int n2 = object3;
            nArray[n2] = nArray[n2] | nArray2[object3];
            if (bl) {
                FastSparseSetFactory$FastSparseSet.changeNext(this.next, object3, this.next[object3], object3);
            }
            object2 = object[object3];
            object3 = object2;
        } while (object2 != false);
    }

    public final void intersection(FastSparseSetFactory$FastSparseSet object) {
        object = ((FastSparseSetFactory$FastSparseSet)object).data;
        int[] nArray = this.data;
        int n = Math.min(((Object)object).length, nArray.length);
        int n2 = n - 1;
        while (n2 >= 0) {
            int n3 = n2;
            nArray[n3] = nArray[n3] & object[n2];
            --n2;
        }
        n2 = nArray.length - 1;
        while (n2 >= n) {
            nArray[n2] = 0;
            --n2;
        }
        int n4 = 0;
        int n5 = this.data.length - 1;
        while (n5 >= 0) {
            this.next[n5] = n4;
            if (this.data[n5] != 0) {
                n4 = n5;
            }
            --n5;
        }
    }

    public final void complement(FastSparseSetFactory$FastSparseSet object) {
        object = ((FastSparseSetFactory$FastSparseSet)object).data;
        int[] nArray = this.data;
        int n = ((Object)object).length;
        int n2 = 0;
        while (n2 < n) {
            int n3 = n2;
            nArray[n3] = nArray[n3] & ~object[n2];
            if (nArray[n2] == 0) {
                FastSparseSetFactory$FastSparseSet.changeNext(this.next, n2, n2, this.next[n2]);
            }
            if ((n2 = this.next[n2]) != 0) continue;
        }
    }

    public final boolean equals(Object object) {
        if (!(object instanceof FastSparseSetFactory$FastSparseSet)) {
            return false;
        }
        object = ((FastSparseSetFactory$FastSparseSet)object).data;
        Object object2 = this.data;
        if (this.data.length > ((Object)object).length) {
            object2 = object;
            object = this.data;
        }
        int n = ((int[])object2).length - 1;
        while (n >= 0) {
            if (object2[n] != object[n]) {
                return false;
            }
            --n;
        }
        n = ((Object)object).length - 1;
        while (n >= ((int[])object2).length) {
            if (object[n] != false) {
                return false;
            }
            --n;
        }
        return true;
    }

    public final int getCardinality() {
        boolean bl = false;
        Object object = ((FastSparseSetFactory$FastSparseSet)object).data;
        int n = ((FastSparseSetFactory$FastSparseSet)object).data.length - 1;
        while (n >= 0) {
            Object object2 = object[n];
            if (object2 != false) {
                if (bl) {
                    return 2;
                }
                if ((object2 & object2 - true) == 0) {
                    bl = true;
                } else {
                    return 2;
                }
            }
            --n;
        }
        if (bl) {
            return 1;
        }
        return 0;
    }

    public final boolean isEmpty() {
        return this.data.length == 0 || this.next[0] == 0 && this.data[0] == 0;
    }

    public final Iterator iterator() {
        return new FastSparseSetFactory$FastSparseSetIterator(this, null);
    }

    public final FastSparseSetFactory getFactory() {
        return this.factory;
    }

    static /* synthetic */ int[] access$0(FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet) {
        return fastSparseSetFactory$FastSparseSet.data;
    }

    static /* synthetic */ int[] access$1(FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet) {
        return fastSparseSetFactory$FastSparseSet.next;
    }

    /* synthetic */ FastSparseSetFactory$FastSparseSet(FastSparseSetFactory fastSparseSetFactory, FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet) {
        this(fastSparseSetFactory);
    }
}

