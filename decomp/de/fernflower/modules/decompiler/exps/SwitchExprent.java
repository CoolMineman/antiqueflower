/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.exps;

import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.vars.CheckTypesResult;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class SwitchExprent
extends Exprent {
    private Exprent value;
    private List caseValues = new ArrayList();

    public SwitchExprent(Exprent exprent) {
        this.type = 11;
        this.value = exprent;
    }

    public final Exprent copy() {
        SwitchExprent switchExprent = new SwitchExprent(((SwitchExprent)object).value.copy());
        ArrayList arrayList = new ArrayList();
        for (Object object : ((SwitchExprent)object).caseValues) {
            arrayList.add(new ArrayList(object));
        }
        switchExprent.caseValues = arrayList;
        return switchExprent;
    }

    public final CheckTypesResult checkExprTypeBounds() {
        CheckTypesResult checkTypesResult = new CheckTypesResult();
        checkTypesResult.addMinTypeExprent(this.value, VarType.VARTYPE_BYTECHAR);
        checkTypesResult.addMaxTypeExprent(this.value, VarType.VARTYPE_INT);
        VarType varType = this.value.getExprType();
        Iterator iterator = this.caseValues.iterator();
        while (iterator.hasNext()) {
            Object object2 = null;
            for (Object object2 : (List)iterator.next()) {
                if (object2 == null || ((VarType)(object2 = ((ConstExprent)object2).getExprType())).equals(varType)) continue;
                varType = VarType.getCommonSupertype((VarType)object2, varType);
                checkTypesResult.addMinTypeExprent(this.value, varType);
            }
        }
        return checkTypesResult;
    }

    public final List getAllExprents() {
        ArrayList<Exprent> arrayList = new ArrayList<Exprent>();
        arrayList.add(this.value);
        return arrayList;
    }

    public final String toJava(int n) {
        return "switch(" + this.value.toJava(n) + ")";
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof SwitchExprent) {
            object = (SwitchExprent)object;
            return InterpreterUtil.equalObjects(this.value, ((SwitchExprent)object).value);
        }
        return false;
    }

    public final void replaceExprent(Exprent exprent, Exprent exprent2) {
        if (exprent == this.value) {
            this.value = exprent2;
        }
    }

    public final void setCaseValues(List list) {
        this.caseValues = list;
    }
}

