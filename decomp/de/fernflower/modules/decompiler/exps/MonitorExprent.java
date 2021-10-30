/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.exps;

import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.List;

public final class MonitorExprent
extends Exprent {
    private int montype;
    private Exprent value;

    public MonitorExprent(int n, Exprent exprent) {
        this.type = 9;
        this.montype = n;
        this.value = exprent;
    }

    public final Exprent copy() {
        return new MonitorExprent(this.montype, this.value.copy());
    }

    public final List getAllExprents() {
        ArrayList<Exprent> arrayList = new ArrayList<Exprent>();
        arrayList.add(this.value);
        return arrayList;
    }

    public final String toJava(int n) {
        if (this.montype == 0) {
            return "synchronized(" + this.value.toJava(n) + ")";
        }
        return "";
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof MonitorExprent) {
            object = (MonitorExprent)object;
            return this.montype == ((MonitorExprent)object).montype && InterpreterUtil.equalObjects(this.value, ((MonitorExprent)object).value);
        }
        return false;
    }

    public final void replaceExprent(Exprent exprent, Exprent exprent2) {
        if (exprent == this.value) {
            this.value = exprent2;
        }
    }

    public final int getMontype() {
        return this.montype;
    }

    public final Exprent getValue() {
        return this.value;
    }
}

