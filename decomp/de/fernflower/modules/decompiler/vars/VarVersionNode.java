/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.vars;

import de.fernflower.modules.decompiler.decompose.IGraphNode;
import de.fernflower.modules.decompiler.vars.VarVersionEdge;
import de.fernflower.util.SFormsFastMapDirect;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class VarVersionNode
implements IGraphNode {
    public int var;
    public int version;
    public Set succs = new HashSet();
    public Set preds = new HashSet();
    public int flags;
    public SFormsFastMapDirect live = new SFormsFastMapDirect();

    public VarVersionNode(int n, int n2) {
        this.var = n;
        this.version = n2;
    }

    public final List getPredecessors() {
        ArrayList<VarVersionNode> arrayList = new ArrayList<VarVersionNode>(((VarVersionNode)object).preds.size());
        for (Object object : ((VarVersionNode)object).preds) {
            arrayList.add(((VarVersionEdge)object).source);
        }
        return arrayList;
    }

    public final void removeSuccessor(VarVersionEdge varVersionEdge) {
        this.succs.remove(varVersionEdge);
    }

    public final void removePredecessor(VarVersionEdge varVersionEdge) {
        this.preds.remove(varVersionEdge);
    }

    public final void addSuccessor(VarVersionEdge varVersionEdge) {
        this.succs.add(varVersionEdge);
    }

    public final void addPredecessor(VarVersionEdge varVersionEdge) {
        this.preds.add(varVersionEdge);
    }

    public final String toString() {
        return "(" + this.var + "_" + this.version + ")";
    }
}

