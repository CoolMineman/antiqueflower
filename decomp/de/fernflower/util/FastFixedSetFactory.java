/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.util;

import de.fernflower.util.FastFixedSetFactory$FastFixedSet;
import de.fernflower.util.VBStyleCollection;
import java.util.Collection;

public final class FastFixedSetFactory {
    private VBStyleCollection colValuesInternal = new VBStyleCollection();
    private int dataLength;

    public FastFixedSetFactory(Collection collection2) {
        this.dataLength = collection2.size() / 32 + 1;
        int n = 0;
        int n2 = 1;
        for (Collection collection2 : collection2) {
            int n3 = n / 32;
            if (n % 32 == 0) {
                n2 = 1;
            }
            this.colValuesInternal.putWithKey(new int[]{n3, n2}, collection2);
            ++n;
            n2 <<= 1;
        }
    }

    public final FastFixedSetFactory$FastFixedSet spawnEmptySet() {
        return new FastFixedSetFactory$FastFixedSet(this, null);
    }

    static /* synthetic */ VBStyleCollection access$0(FastFixedSetFactory fastFixedSetFactory) {
        return fastFixedSetFactory.colValuesInternal;
    }

    static /* synthetic */ int access$1(FastFixedSetFactory fastFixedSetFactory) {
        return fastFixedSetFactory.dataLength;
    }
}

