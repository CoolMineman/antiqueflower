/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.vars;

import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph$ExprentIterator;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import java.util.HashMap;

final class VarVersionsProcessor$1
implements DirectGraph$ExprentIterator {
    private final /* synthetic */ HashMap val$phivers;

    VarVersionsProcessor$1(HashMap hashMap) {
        this.val$phivers = hashMap;
    }

    public final int processExprent(Exprent exprent) {
        Object object = exprent.getAllExprents(true);
        object.add(exprent);
        object = object.iterator();
        while (object.hasNext()) {
            Integer n;
            exprent = (Exprent)object.next();
            if (exprent.type != 12 || (n = (Integer)this.val$phivers.get(new VarVersionPaar((VarExprent)(exprent = (VarExprent)exprent)))) == null) continue;
            ((VarExprent)exprent).setVersion(n);
        }
        return 0;
    }
}

