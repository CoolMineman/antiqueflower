/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.vars;

import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.vars.CheckTypesResult$ExprentTypePair;
import de.fernflower.struct.gen.VarType;
import java.util.ArrayList;
import java.util.List;

public final class CheckTypesResult {
    private List lstMaxTypeExprents = new ArrayList();
    private List lstMinTypeExprents = new ArrayList();

    public final void addMaxTypeExprent(Exprent exprent, VarType varType) {
        this.lstMaxTypeExprents.add(new CheckTypesResult$ExprentTypePair(exprent, varType));
    }

    public final void addMinTypeExprent(Exprent exprent, VarType varType) {
        this.lstMinTypeExprents.add(new CheckTypesResult$ExprentTypePair(exprent, varType));
    }

    public final List getLstMaxTypeExprents() {
        return this.lstMaxTypeExprents;
    }

    public final List getLstMinTypeExprents() {
        return this.lstMinTypeExprents;
    }
}

