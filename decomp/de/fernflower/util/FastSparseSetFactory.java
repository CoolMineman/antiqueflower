/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.util;

import de.fernflower.util.FastSparseSetFactory$FastSparseSet;
import de.fernflower.util.VBStyleCollection;
import java.util.Collection;

public final class FastSparseSetFactory {
    private VBStyleCollection colValuesInternal = new VBStyleCollection();
    private int lastBlock;
    private int lastMask;

    public FastSparseSetFactory(Collection collection2) {
        int n = -1;
        int n2 = -1;
        int n3 = 0;
        for (Collection collection2 : collection2) {
            n = n3 / 32;
            n2 = n3 % 32 == 0 ? 1 : (n2 <<= 1);
            this.colValuesInternal.putWithKey(new int[]{n, n2}, collection2);
            ++n3;
        }
        this.lastBlock = n;
        this.lastMask = n2;
    }

    public final FastSparseSetFactory$FastSparseSet spawnEmptySet() {
        return new FastSparseSetFactory$FastSparseSet(this, null);
    }

    public final int getLastBlock() {
        return this.lastBlock;
    }

    static /* synthetic */ VBStyleCollection access$0(FastSparseSetFactory fastSparseSetFactory) {
        return fastSparseSetFactory.colValuesInternal;
    }

    static /* synthetic */ int[] access$1(FastSparseSetFactory fastSparseSetFactory, Object object) {
        fastSparseSetFactory.lastMask = fastSparseSetFactory.lastMask == -1 || fastSparseSetFactory.lastMask == Integer.MIN_VALUE ? 1 : (fastSparseSetFactory.lastMask <<= 1);
        int[] nArray = new int[]{++fastSparseSetFactory.lastBlock, fastSparseSetFactory.lastMask};
        fastSparseSetFactory.colValuesInternal.putWithKey(nArray, object);
        return nArray;
    }
}

