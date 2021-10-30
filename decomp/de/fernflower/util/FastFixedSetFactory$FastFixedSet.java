/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.util;

import de.fernflower.util.FastFixedSetFactory;
import de.fernflower.util.FastFixedSetFactory$FastFixedSetIterator;
import de.fernflower.util.VBStyleCollection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class FastFixedSetFactory$FastFixedSet
implements Iterable {
    private FastFixedSetFactory factory;
    private VBStyleCollection colValuesInternal;
    private int[] data;

    private FastFixedSetFactory$FastFixedSet(FastFixedSetFactory fastFixedSetFactory) {
        this.factory = fastFixedSetFactory;
        this.colValuesInternal = FastFixedSetFactory.access$0(fastFixedSetFactory);
        this.data = new int[FastFixedSetFactory.access$1(fastFixedSetFactory)];
    }

    public final FastFixedSetFactory$FastFixedSet getCopy() {
        FastFixedSetFactory$FastFixedSet fastFixedSetFactory$FastFixedSet = new FastFixedSetFactory$FastFixedSet(this.factory);
        int n = this.data.length;
        int[] nArray = new int[n];
        System.arraycopy(this.data, 0, nArray, 0, n);
        fastFixedSetFactory$FastFixedSet.data = nArray;
        return fastFixedSetFactory$FastFixedSet;
    }

    public final void setAllElements() {
        int[] nArray = (int[])this.colValuesInternal.get(this.colValuesInternal.size() - 1);
        int n = nArray[0] - 1;
        while (n >= 0) {
            this.data[n] = -1;
            --n;
        }
        this.data[nArray[0]] = nArray[1] | nArray[1] - 1;
    }

    public final void add(Object object) {
        object = (int[])this.colValuesInternal.getWithKey(object);
        Object object2 = object[0];
        this.data[object2] = this.data[object2] | object[1];
    }

    public final void addAll(Collection collection2) {
        for (Collection collection2 : collection2) {
            this.add(collection2);
        }
    }

    public final void remove(Object object) {
        object = (int[])this.colValuesInternal.getWithKey(object);
        Object object2 = object[0];
        this.data[object2] = this.data[object2] & ~object[1];
    }

    public final boolean contains(Object object) {
        return (this.data[(object = (Object)((int[])this.colValuesInternal.getWithKey(object)))[0]] & object[1]) != 0;
    }

    public final boolean contains(FastFixedSetFactory$FastFixedSet object) {
        object = ((FastFixedSetFactory$FastFixedSet)object).data;
        Object object2 = ((FastFixedSetFactory$FastFixedSet)object2).data;
        int n = ((FastFixedSetFactory$FastFixedSet)object2).data.length - 1;
        while (n >= 0) {
            if ((object[n] & ~object2[n]) != 0) {
                return false;
            }
            --n;
        }
        return true;
    }

    public final void union(FastFixedSetFactory$FastFixedSet object) {
        object = ((FastFixedSetFactory$FastFixedSet)object).data;
        Object object2 = ((FastFixedSetFactory$FastFixedSet)object2).data;
        int n = ((FastFixedSetFactory$FastFixedSet)object2).data.length - 1;
        while (n >= 0) {
            Object object3 = object2;
            int n2 = n;
            object3[n2] = object3[n2] | object[n];
            --n;
        }
    }

    public final void intersection(FastFixedSetFactory$FastFixedSet object) {
        object = ((FastFixedSetFactory$FastFixedSet)object).data;
        Object object2 = ((FastFixedSetFactory$FastFixedSet)object2).data;
        int n = ((FastFixedSetFactory$FastFixedSet)object2).data.length - 1;
        while (n >= 0) {
            Object object3 = object2;
            int n2 = n;
            object3[n2] = object3[n2] & object[n];
            --n;
        }
    }

    public final void complement(FastFixedSetFactory$FastFixedSet object) {
        object = ((FastFixedSetFactory$FastFixedSet)object).data;
        Object object2 = ((FastFixedSetFactory$FastFixedSet)object2).data;
        int n = ((FastFixedSetFactory$FastFixedSet)object2).data.length - 1;
        while (n >= 0) {
            Object object3 = object2;
            int n2 = n;
            object3[n2] = object3[n2] & ~object[n];
            --n;
        }
    }

    public final boolean equals(Object object) {
        if (!(object instanceof FastFixedSetFactory$FastFixedSet)) {
            return false;
        }
        object = ((FastFixedSetFactory$FastFixedSet)object).data;
        Object object2 = ((FastFixedSetFactory$FastFixedSet)object2).data;
        int n = ((FastFixedSetFactory$FastFixedSet)object2).data.length - 1;
        while (n >= 0) {
            if (object2[n] != object[n]) {
                return false;
            }
            --n;
        }
        return true;
    }

    public final boolean isEmpty() {
        Object object = ((FastFixedSetFactory$FastFixedSet)object).data;
        int n = ((FastFixedSetFactory$FastFixedSet)object).data.length - 1;
        while (n >= 0) {
            if (object[n] != false) {
                return false;
            }
            --n;
        }
        return true;
    }

    public final Iterator iterator() {
        return new FastFixedSetFactory$FastFixedSetIterator(this, null);
    }

    public final Set toPlainSet() {
        return (Set)this.toPlainCollection(new HashSet());
    }

    private Collection toPlainCollection(Collection collection) {
        int[] nArray = this.data;
        int n = 0;
        while (n < nArray.length) {
            int n2 = nArray[n];
            if (n2 != 0) {
                int n3 = n << 5;
                int n4 = 31;
                while (n4 >= 0) {
                    if ((n2 & 1) != 0) {
                        collection.add(this.colValuesInternal.getKey(n3));
                    }
                    ++n3;
                    n2 >>>= 1;
                    --n4;
                }
            }
            ++n;
        }
        return collection;
    }

    public final String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        int[] nArray = this.data;
        boolean bl = true;
        int n = this.colValuesInternal.size() - 1;
        while (n >= 0) {
            int[] nArray2 = (int[])this.colValuesInternal.get(n);
            if ((nArray[nArray2[0]] & nArray2[1]) != 0) {
                if (bl) {
                    bl = false;
                } else {
                    stringBuilder.append(",");
                }
                stringBuilder.append(this.colValuesInternal.getKey(n));
            }
            --n;
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public final FastFixedSetFactory getFactory() {
        return this.factory;
    }

    static /* synthetic */ int[] access$0(FastFixedSetFactory$FastFixedSet fastFixedSetFactory$FastFixedSet) {
        return fastFixedSetFactory$FastFixedSet.data;
    }

    /* synthetic */ FastFixedSetFactory$FastFixedSet(FastFixedSetFactory fastFixedSetFactory, FastFixedSetFactory$FastFixedSet fastFixedSetFactory$FastFixedSet) {
        this(fastFixedSetFactory);
    }
}

