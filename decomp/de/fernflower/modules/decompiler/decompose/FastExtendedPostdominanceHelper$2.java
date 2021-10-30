/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.decompose;

import de.fernflower.modules.decompiler.decompose.FastExtendedPostdominanceHelper;
import de.fernflower.modules.decompiler.decompose.FastExtendedPostdominanceHelper$IReachabilityAction;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.util.FastFixedSetFactory$FastFixedSet;
import java.io.Serializable;
import java.util.HashMap;

final class FastExtendedPostdominanceHelper$2
implements FastExtendedPostdominanceHelper$IReachabilityAction {
    private /* synthetic */ FastExtendedPostdominanceHelper this$0;

    FastExtendedPostdominanceHelper$2(FastExtendedPostdominanceHelper fastExtendedPostdominanceHelper) {
        this.this$0 = fastExtendedPostdominanceHelper;
    }

    public final boolean action(Statement object, HashMap serializable2) {
        object = ((Statement)object).id;
        for (Serializable serializable2 : ((FastFixedSetFactory$FastFixedSet)serializable2.get(object)).toPlainSet()) {
            ((FastFixedSetFactory$FastFixedSet)FastExtendedPostdominanceHelper.access$2(this.this$0).get(serializable2)).add(object);
        }
        return false;
    }
}

