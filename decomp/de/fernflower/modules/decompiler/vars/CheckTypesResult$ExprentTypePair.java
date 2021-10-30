/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.vars;

import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.struct.gen.VarType;

public final class CheckTypesResult$ExprentTypePair {
    public Exprent exprent;
    public VarType type;

    public CheckTypesResult$ExprentTypePair(Exprent exprent, VarType varType) {
        this.exprent = exprent;
        this.type = varType;
    }
}

