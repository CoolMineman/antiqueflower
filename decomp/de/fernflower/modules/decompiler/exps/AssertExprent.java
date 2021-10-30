/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.exps;

import de.fernflower.modules.decompiler.exps.Exprent;
import java.util.List;

public final class AssertExprent
extends Exprent {
    private List parameters;

    public AssertExprent(List list) {
        this.type = 14;
        this.parameters = list;
    }

    public final String toJava(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("assert ");
        if (this.parameters.get(0) == null) {
            stringBuilder.append("false");
        } else {
            stringBuilder.append(((Exprent)this.parameters.get(0)).toJava(n));
        }
        if (this.parameters.size() > 1) {
            stringBuilder.append(" : ");
            stringBuilder.append(((Exprent)this.parameters.get(1)).toJava(n));
        }
        return stringBuilder.toString();
    }
}

