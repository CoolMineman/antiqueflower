/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.util;

import de.fernflower.util.FastSparseSetFactory;
import de.fernflower.util.FastSparseSetFactory$FastSparseSet;
import de.fernflower.util.VBStyleCollection;
import java.util.Iterator;

public final class FastSparseSetFactory$FastSparseSetIterator
implements Iterator {
    private VBStyleCollection colValuesInternal;
    private int[] data;
    private int[] next;
    private int size;
    private int pointer = -1;
    private int next_pointer = -1;

    private FastSparseSetFactory$FastSparseSetIterator(FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet) {
        this.colValuesInternal = FastSparseSetFactory.access$0(fastSparseSetFactory$FastSparseSet.getFactory());
        this.data = FastSparseSetFactory$FastSparseSet.access$0(fastSparseSetFactory$FastSparseSet);
        this.next = FastSparseSetFactory$FastSparseSet.access$1(fastSparseSetFactory$FastSparseSet);
        this.size = this.colValuesInternal.size();
    }

    private int getNextIndex(int n) {
        int n2 = ++n >>> 5;
        n &= 0x1F;
        while (n2 < this.data.length) {
            int n3 = this.data[n2];
            if (n3 != 0) {
                n3 >>>= n;
                while (n < 32) {
                    if ((n3 & 1) != 0) {
                        return (n2 << 5) + n;
                    }
                    n3 >>>= 1;
                    ++n;
                }
            }
            n = 0;
            if ((n2 = this.next[n2]) == 0) break;
        }
        return -1;
    }

    public final boolean hasNext() {
        this.next_pointer = this.getNextIndex(this.pointer);
        return this.next_pointer >= 0;
    }

    public final Object next() {
        if (this.next_pointer >= 0) {
            this.pointer = this.next_pointer;
        } else {
            this.pointer = this.getNextIndex(this.pointer);
            if (this.pointer == -1) {
                this.pointer = this.size;
            }
        }
        this.next_pointer = -1;
        if (this.pointer < this.size) {
            return this.colValuesInternal.getKey(this.pointer);
        }
        return null;
    }

    public final void remove() {
        int[] nArray = (int[])this.colValuesInternal.get(this.pointer);
        int n = nArray[0];
        this.data[n] = this.data[n] & ~nArray[1];
    }

    /* synthetic */ FastSparseSetFactory$FastSparseSetIterator(FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet, FastSparseSetFactory$FastSparseSetIterator fastSparseSetFactory$FastSparseSetIterator) {
        this(fastSparseSetFactory$FastSparseSet);
    }
}

