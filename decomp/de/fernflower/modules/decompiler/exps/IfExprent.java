/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.exps;

import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FunctionExprent;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import de.fernflower.util.ListStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class IfExprent
extends Exprent {
    private static final int[] functypes = new int[]{42, 43, 44, 45, 46, 47, 42, 43, 42, 43, 44, 45, 46, 47, 42, 43, 48, 49, 12, -1};
    private Exprent condition;

    public IfExprent(int n, ListStack listStack) {
        this.type = 7;
        if (n <= 5) {
            listStack.push(new ConstExprent(0, true));
        } else if (n <= 7) {
            listStack.push(new ConstExprent(VarType.VARTYPE_NULL, null));
        }
        if (n == 19) {
            this.condition = (Exprent)listStack.pop();
            return;
        }
        this.condition = new FunctionExprent(functypes[n], listStack);
    }

    private IfExprent(Exprent exprent) {
        this.type = 7;
        this.condition = exprent;
    }

    public final Exprent copy() {
        return new IfExprent(this.condition.copy());
    }

    public final List getAllExprents() {
        ArrayList<Exprent> arrayList = new ArrayList<Exprent>();
        arrayList.add(this.condition);
        return arrayList;
    }

    public final String toJava(int n) {
        StringBuffer stringBuffer = new StringBuffer("if(");
        stringBuffer.append(this.condition.toJava(n));
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof IfExprent) {
            object = (IfExprent)object;
            return InterpreterUtil.equalObjects(this.condition, ((IfExprent)object).condition);
        }
        return false;
    }

    public final void replaceExprent(Exprent exprent, Exprent exprent2) {
        if (exprent == this.condition) {
            this.condition = exprent2;
        }
    }

    public final IfExprent negateIf() {
        this.condition = new FunctionExprent(12, Arrays.asList(this.condition));
        return this;
    }

    public final Exprent getCondition() {
        return this.condition;
    }

    public final void setCondition(Exprent exprent) {
        this.condition = exprent;
    }
}

