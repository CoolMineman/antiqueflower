/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.util;

import de.fernflower.util.FastFixedSetFactory;
import de.fernflower.util.FastFixedSetFactory$FastFixedSet;
import de.fernflower.util.VBStyleCollection;
import java.util.Iterator;

public final class FastFixedSetFactory$FastFixedSetIterator
implements Iterator {
    private VBStyleCollection colValuesInternal;
    private int[] data;
    private int size;
    private int pointer = -1;
    private int next_pointer = -1;

    private FastFixedSetFactory$FastFixedSetIterator(FastFixedSetFactory$FastFixedSet fastFixedSetFactory$FastFixedSet) {
        this.colValuesInternal = FastFixedSetFactory.access$0(fastFixedSetFactory$FastFixedSet.getFactory());
        this.data = FastFixedSetFactory$FastFixedSet.access$0(fastFixedSetFactory$FastFixedSet);
        this.size = this.colValuesInternal.size();
    }

    private int getNextIndex(int n) {
        int n2 = ++n;
        int n3 = n / 32;
        n %= 32;
        while (n3 < this.data.length) {
            int n4 = this.data[n3];
            if (n4 != 0) {
                n4 >>>= n;
                while (n < 32) {
                    if ((n4 & 1) != 0) {
                        return n2;
                    }
                    n4 >>>= 1;
                    ++n;
                    ++n2;
                }
            } else {
                n2 += 32 - n;
            }
            n = 0;
            ++n3;
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

    /* synthetic */ FastFixedSetFactory$FastFixedSetIterator(FastFixedSetFactory$FastFixedSet fastFixedSet, FastFixedSetFactory$FastFixedSetIterator fastFixedSetFactory$FastFixedSetIterator) {
        this(fastFixedSet);
    }
}

