/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.exps;

import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.vars.CheckTypesResult;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.gen.VarType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Exprent {
    public int type;
    public int id = DecompilerContext.getCountercontainer().getCounterAndIncrement(1);

    public int getPrecedence() {
        return 0;
    }

    public VarType getExprType() {
        return VarType.VARTYPE_VOID;
    }

    public int getExprentUse() {
        return 0;
    }

    public CheckTypesResult checkExprTypeBounds() {
        return new CheckTypesResult();
    }

    public final boolean containsExprent(Exprent exprent) {
        Object object = new ArrayList<Exprent>(this.getAllExprents(true));
        object.add(this);
        object = object.iterator();
        while (object.hasNext()) {
            if (!((Exprent)object.next()).equals(exprent)) continue;
            return true;
        }
        return false;
    }

    public final List getAllExprents(boolean n) {
        List list = ((Exprent)((Object)list)).getAllExprents();
        n = list.size() - 1;
        while (n >= 0) {
            list.addAll(((Exprent)list.get(n)).getAllExprents(true));
            --n;
        }
        return list;
    }

    public final Set getAllVariables() {
        HashSet<VarVersionPaar> hashSet = new HashSet<VarVersionPaar>();
        Object object = exprent.getAllExprents(true);
        object.add(exprent);
        object = object.iterator();
        while (object.hasNext()) {
            Exprent exprent = (Exprent)object.next();
            if (exprent.type != 12) continue;
            hashSet.add(new VarVersionPaar((VarExprent)exprent));
        }
        return hashSet;
    }

    public List getAllExprents() {
        throw new RuntimeException("not implemented");
    }

    public Exprent copy() {
        throw new RuntimeException("not implemented");
    }

    public String toJava(int n) {
        throw new RuntimeException("not implemented");
    }

    public void replaceExprent(Exprent exprent, Exprent exprent2) {
    }
}

