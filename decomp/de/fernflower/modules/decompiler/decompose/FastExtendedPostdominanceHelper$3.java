/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.decompose;

import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.decompose.FastExtendedPostdominanceHelper;
import de.fernflower.modules.decompiler.decompose.FastExtendedPostdominanceHelper$IReachabilityAction;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.util.FastFixedSetFactory$FastFixedSet;
import de.fernflower.util.InterpreterUtil;
import java.util.HashMap;

final class FastExtendedPostdominanceHelper$3
implements FastExtendedPostdominanceHelper$IReachabilityAction {
    private /* synthetic */ FastExtendedPostdominanceHelper this$0;
    private final /* synthetic */ int val$edgetype;

    FastExtendedPostdominanceHelper$3(FastExtendedPostdominanceHelper fastExtendedPostdominanceHelper, int n) {
        this.this$0 = fastExtendedPostdominanceHelper;
        this.val$edgetype = n;
    }

    public final boolean action(Statement statement, HashMap hashMap) {
        for (Object object : statement.getAllSuccessorEdges()) {
            if ((((StatEdge)object).getType() & this.val$edgetype) == 0 || !hashMap.containsKey(object.getDestination().id) || InterpreterUtil.equalObjects(object = (FastFixedSetFactory$FastFixedSet)hashMap.get(statement.id), FastExtendedPostdominanceHelper.access$0(this.this$0).get(statement.id))) continue;
            FastExtendedPostdominanceHelper.access$0(this.this$0).put(statement.id, object);
            return true;
        }
        return false;
    }
}

