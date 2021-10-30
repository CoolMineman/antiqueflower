/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.vars;

import de.fernflower.modules.decompiler.exps.VarExprent;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class VarVersionPaar
implements Comparable<VarVersionPaar> {
    public int var;
    public int version;
    private int hashCode = -1;

    public VarVersionPaar(int n, int n2) {
        this.var = n;
        this.version = n2;
    }

    public VarVersionPaar(Integer n, Integer n2) {
        this.var = n;
        this.version = n2;
    }

    public VarVersionPaar(VarExprent varExprent) {
        this.var = varExprent.getIndex0();
        this.version = varExprent.getVersion();
    }

    public final boolean equals(Object object) {
        if (object == null || !(object instanceof VarVersionPaar)) {
            return false;
        }
        object = (VarVersionPaar)object;
        return this.var == ((VarVersionPaar)object).var && this.version == ((VarVersionPaar)object).version;
    }

    public final int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = this.var * 3 + this.version;
        }
        return this.hashCode;
    }

    public final String toString() {
        return "(" + this.var + "," + this.version + ")";
    }

    @Override
    public int compareTo(VarVersionPaar varVersionPaar) {
        if (this.var != varVersionPaar.var) {
            return this.var - varVersionPaar.var;
        }
        return this.version - varVersionPaar.version;
    }
}

