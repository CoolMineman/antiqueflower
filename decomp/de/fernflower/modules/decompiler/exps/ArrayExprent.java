/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.exps;

import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.vars.CheckTypesResult;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.List;

public final class ArrayExprent
extends Exprent {
    private Exprent array;
    private Exprent index;
    private VarType hardtype;

    public ArrayExprent(Exprent exprent, Exprent exprent2, VarType varType) {
        this.type = 1;
        this.array = exprent;
        this.index = exprent2;
        this.hardtype = varType;
    }

    public final Exprent copy() {
        return new ArrayExprent(this.array.copy(), this.index.copy(), this.hardtype);
    }

    public final VarType getExprType() {
        VarType varType = this.array.getExprType().copy();
        if (varType.equals(VarType.VARTYPE_NULL)) {
            varType = this.hardtype.copy();
        } else {
            varType.decArrayDim();
        }
        return varType;
    }

    public final int getExprentUse() {
        return this.array.getExprentUse() & this.index.getExprentUse() & 1;
    }

    public final CheckTypesResult checkExprTypeBounds() {
        CheckTypesResult checkTypesResult = new CheckTypesResult();
        checkTypesResult.addMinTypeExprent(this.index, VarType.VARTYPE_BYTECHAR);
        checkTypesResult.addMaxTypeExprent(this.index, VarType.VARTYPE_INT);
        return checkTypesResult;
    }

    public final List getAllExprents() {
        ArrayList<Exprent> arrayList = new ArrayList<Exprent>();
        arrayList.add(this.array);
        arrayList.add(this.index);
        return arrayList;
    }

    public final String toJava(int n) {
        String string = this.array.toJava(n);
        if (this.array.getPrecedence() > this.getPrecedence()) {
            string = "(" + string + ")";
        }
        VarType varType = null;
        if (this.array.getExprType().arraydim == 0) {
            varType = VarType.VARTYPE_OBJECT.copy();
            VarType.VARTYPE_OBJECT.copy().arraydim = 1;
            string = "((" + ExprProcessor.getCastTypeName(varType) + ")" + string + ")";
        }
        return String.valueOf(string) + "[" + this.index.toJava(n) + "]";
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof ArrayExprent) {
            object = (ArrayExprent)object;
            return InterpreterUtil.equalObjects(this.array, ((ArrayExprent)object).array) && InterpreterUtil.equalObjects(this.index, ((ArrayExprent)object).index);
        }
        return false;
    }

    public final void replaceExprent(Exprent exprent, Exprent exprent2) {
        if (exprent == this.array) {
            this.array = exprent2;
        }
        if (exprent == this.index) {
            this.index = exprent2;
        }
    }

    public final Exprent getArray() {
        return this.array;
    }

    public final Exprent getIndex() {
        return this.index;
    }
}

