/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.rels;

import de.fernflower.modules.decompiler.vars.VarVersionPaar;

final class NestedClassProcessor$VarFieldPair {
    public String keyfield = "";
    public VarVersionPaar varpaar;

    public NestedClassProcessor$VarFieldPair(String string, VarVersionPaar varVersionPaar) {
        this.keyfield = string;
        this.varpaar = varVersionPaar;
    }

    public final boolean equals(Object object) {
        if (!(object instanceof NestedClassProcessor$VarFieldPair)) {
            return false;
        }
        object = (NestedClassProcessor$VarFieldPair)object;
        return this.keyfield.equals(((NestedClassProcessor$VarFieldPair)object).keyfield) && this.varpaar.equals(((NestedClassProcessor$VarFieldPair)object).varpaar);
    }

    public final int hashCode() {
        return this.keyfield.hashCode() + this.varpaar.hashCode();
    }
}

