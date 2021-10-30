/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.vars;

import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph$ExprentIterator;
import de.fernflower.struct.gen.VarType;

final class VarTypeProcessor$1
implements DirectGraph$ExprentIterator {
    VarTypeProcessor$1() {
    }

    public final int processExprent(Exprent object) {
        Object object2 = ((Exprent)object).getAllExprents(true);
        object2.add(object);
        object = object2.iterator();
        while (object.hasNext()) {
            object2 = (Exprent)object.next();
            if (((Exprent)object2).type == 12) {
                ((VarExprent)object2).setVartype(VarType.VARTYPE_UNKNOWN);
                continue;
            }
            if (((Exprent)object2).type != 3) continue;
            object2 = (ConstExprent)object2;
            if (object2.getConsttype().type_family != 2) continue;
            ((ConstExprent)object2).setConsttype(new ConstExprent(((ConstExprent)object2).getIntValue(), ((ConstExprent)object2).isBoolPermitted()).getConsttype());
        }
        return 0;
    }
}

