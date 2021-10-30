/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.util;

import de.fernflower.util.FastSparseSetFactory$FastSparseSet;
import java.util.Map;

final class SFormsFastMapDirect$1
implements Map.Entry {
    private Integer var;
    private FastSparseSetFactory$FastSparseSet val;

    SFormsFastMapDirect$1(int n, FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet) {
        this.var = n;
        this.val = fastSparseSetFactory$FastSparseSet;
    }
}

