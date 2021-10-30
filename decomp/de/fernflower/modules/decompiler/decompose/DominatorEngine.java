/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.decompose;

import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.util.VBStyleCollection;
import java.util.List;

public final class DominatorEngine {
    private Statement statement;
    private VBStyleCollection colOrderedIDoms = new VBStyleCollection();

    public DominatorEngine(Statement statement) {
        this.statement = statement;
    }

    public final void calcIDoms() {
        boolean bl;
        Object object3 = this;
        for (Object object2 : this.statement.getReversePostOrderList()) {
            ((DominatorEngine)object3).colOrderedIDoms.addWithKey(null, ((Statement)object2).id);
        }
        this.colOrderedIDoms.putWithKey(this.statement.getFirst().id, this.statement.getFirst().id);
        List list = this.colOrderedIDoms.getLstKeys().subList(1, this.colOrderedIDoms.getLstKeys().size());
        do {
            bl = false;
            for (Integer n : list) {
                Object object2;
                object3 = (Statement)this.statement.getStats().getWithKey(n);
                object2 = null;
                for (Object object3 : ((Statement)object3).getAllPredecessorEdges()) {
                    Object object4;
                    if (this.colOrderedIDoms.getWithKey(object3.getSource().id) == null) continue;
                    Statement statement = object2;
                    VBStyleCollection vBStyleCollection = this.colOrderedIDoms;
                    object2 = object3.getSource().id;
                    object3 = statement;
                    if (statement == null) {
                        object4 = object2;
                        continue;
                    }
                    if (object2 == null) {
                        object4 = object3;
                        continue;
                    }
                    int n2 = vBStyleCollection.getIndexByKey(object3);
                    int n3 = vBStyleCollection.getIndexByKey(object2);
                    while (n2 != n3) {
                        if (n2 > n3) {
                            object3 = (Integer)vBStyleCollection.getWithKey(object3);
                            n2 = vBStyleCollection.getIndexByKey(object3);
                            continue;
                        }
                        object2 = (Integer)vBStyleCollection.getWithKey(object2);
                        n3 = vBStyleCollection.getIndexByKey(object2);
                    }
                    object4 = object2 = object3;
                }
                object3 = (Integer)this.colOrderedIDoms.putWithKey(object2, n);
                if (((Integer)object2).equals(object3)) continue;
                bl = true;
            }
        } while (bl);
    }

    public final VBStyleCollection getOrderedIDoms() {
        return this.colOrderedIDoms;
    }

    public final boolean isDominator(Integer n, Integer n2) {
        while (!n.equals(n2)) {
            Integer n3 = (Integer)this.colOrderedIDoms.getWithKey(n);
            if (n3.equals(n)) {
                return false;
            }
            n = n3;
        }
        return true;
    }
}

