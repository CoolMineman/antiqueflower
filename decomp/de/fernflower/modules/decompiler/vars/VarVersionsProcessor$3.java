/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.vars;

import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph$ExprentIterator;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.gen.VarType;
import java.util.HashMap;

final class VarVersionsProcessor$3
implements DirectGraph$ExprentIterator {
    private final /* synthetic */ HashMap val$mapVarPaar;
    private final /* synthetic */ HashMap val$mapExprentMaxTypes;

    VarVersionsProcessor$3(HashMap hashMap, HashMap hashMap2) {
        this.val$mapVarPaar = hashMap;
        this.val$mapExprentMaxTypes = hashMap2;
    }

    public final int processExprent(Exprent object) {
        Object object2 = ((Exprent)object).getAllExprents(true);
        object2.add(object);
        object2 = object2.iterator();
        while (object2.hasNext()) {
            Object object3;
            object = (Exprent)object2.next();
            if (((Exprent)object).type == 12) {
                object3 = (VarExprent)object;
                if ((object = (Integer)this.val$mapVarPaar.get(new VarVersionPaar((VarExprent)object3))) == null) continue;
                ((VarExprent)object3).setIndex((Integer)object);
                ((VarExprent)object3).setVersion(0);
                continue;
            }
            if (((Exprent)object).type != 3 || (object3 = (VarType)this.val$mapExprentMaxTypes.get(new VarVersionPaar(((Exprent)object).id, -1))) == null || !((VarType)object3).equals(VarType.VARTYPE_CHAR)) continue;
            ((ConstExprent)object).setConsttype((VarType)object3);
        }
        return 0;
    }
}

