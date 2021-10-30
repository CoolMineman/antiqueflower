/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.decompose;

import de.fernflower.modules.decompiler.decompose.FastExtendedPostdominanceHelper;
import de.fernflower.modules.decompiler.decompose.FastExtendedPostdominanceHelper$IReachabilityAction;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.util.FastFixedSetFactory$FastFixedSet;
import java.util.ArrayList;
import java.util.HashMap;

final class FastExtendedPostdominanceHelper$1
implements FastExtendedPostdominanceHelper$IReachabilityAction {
    private /* synthetic */ FastExtendedPostdominanceHelper this$0;

    FastExtendedPostdominanceHelper$1(FastExtendedPostdominanceHelper fastExtendedPostdominanceHelper) {
        this.this$0 = fastExtendedPostdominanceHelper;
    }

    public final boolean action(Statement object3, HashMap object2) {
        FastFixedSetFactory$FastFixedSet fastFixedSetFactory$FastFixedSet;
        Integer n = ((Statement)object3).id;
        FastFixedSetFactory$FastFixedSet fastFixedSetFactory$FastFixedSet2 = (FastFixedSetFactory$FastFixedSet)((HashMap)object2).get(n);
        ArrayList<FastFixedSetFactory$FastFixedSet> arrayList = new ArrayList<FastFixedSetFactory$FastFixedSet>();
        for (Object object3 : ((Statement)object3).getPredecessorEdges(1)) {
            fastFixedSetFactory$FastFixedSet = (FastFixedSetFactory$FastFixedSet)((HashMap)object2).get(object3.getSource().id);
            if (fastFixedSetFactory$FastFixedSet == null) {
                fastFixedSetFactory$FastFixedSet = (FastFixedSetFactory$FastFixedSet)FastExtendedPostdominanceHelper.access$0(this.this$0).get(object3.getSource().id);
            }
            arrayList.add(fastFixedSetFactory$FastFixedSet);
        }
        for (Object object3 : fastFixedSetFactory$FastFixedSet2.toPlainSet()) {
            fastFixedSetFactory$FastFixedSet = fastFixedSetFactory$FastFixedSet2.getCopy();
            object2 = FastExtendedPostdominanceHelper.access$1(this.this$0).spawnEmptySet();
            boolean bl = false;
            for (FastFixedSetFactory$FastFixedSet fastFixedSetFactory$FastFixedSet3 : arrayList) {
                if (!fastFixedSetFactory$FastFixedSet3.contains(object3)) continue;
                if (!bl) {
                    ((FastFixedSetFactory$FastFixedSet)object2).union(fastFixedSetFactory$FastFixedSet3);
                    bl = true;
                    continue;
                }
                ((FastFixedSetFactory$FastFixedSet)object2).intersection(fastFixedSetFactory$FastFixedSet3);
            }
            if (n.intValue() != ((Integer)object3).intValue()) {
                ((FastFixedSetFactory$FastFixedSet)object2).add(n);
            } else {
                ((FastFixedSetFactory$FastFixedSet)object2).remove(n);
            }
            fastFixedSetFactory$FastFixedSet.complement((FastFixedSetFactory$FastFixedSet)object2);
            ((FastFixedSetFactory$FastFixedSet)FastExtendedPostdominanceHelper.access$2(this.this$0).get(object3)).complement(fastFixedSetFactory$FastFixedSet);
        }
        return false;
    }
}

