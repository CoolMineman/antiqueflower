/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.vars;

import de.fernflower.modules.decompiler.vars.VarVersionNode;

public final class VarVersionEdge {
    private int type;
    public VarVersionNode source;
    public VarVersionNode dest;
    private int hashCode;

    public VarVersionEdge(int n, VarVersionNode varVersionNode, VarVersionNode varVersionNode2) {
        this.type = n;
        this.source = varVersionNode;
        this.dest = varVersionNode2;
        this.hashCode = varVersionNode.hashCode() ^ varVersionNode2.hashCode() + n;
    }

    public final boolean equals(Object object) {
        if (object == null || !(object instanceof VarVersionEdge)) {
            return false;
        }
        object = (VarVersionEdge)object;
        return this.type == ((VarVersionEdge)object).type && this.source == ((VarVersionEdge)object).source && this.dest == ((VarVersionEdge)object).dest;
    }

    public final int hashCode() {
        return this.hashCode;
    }
}

