/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.vars;

import de.fernflower.modules.decompiler.decompose.IGraph;
import de.fernflower.modules.decompiler.vars.VarVersionsGraph;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class VarVersionsGraph$1
implements IGraph {
    private /* synthetic */ VarVersionsGraph this$0;
    private final /* synthetic */ HashSet val$roots;

    VarVersionsGraph$1(VarVersionsGraph varVersionsGraph, HashSet hashSet) {
        this.this$0 = varVersionsGraph;
        this.val$roots = hashSet;
    }

    public final List getReversePostOrderList() {
        return VarVersionsGraph.access$0(this.this$0, this.val$roots);
    }

    public final Set getRoots() {
        return new HashSet(this.val$roots);
    }
}

