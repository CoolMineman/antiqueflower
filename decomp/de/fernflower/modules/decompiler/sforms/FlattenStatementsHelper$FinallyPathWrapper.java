/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.sforms;

public final class FlattenStatementsHelper$FinallyPathWrapper {
    public String source;
    public String destination;
    public String entry;

    private FlattenStatementsHelper$FinallyPathWrapper(String string, String string2, String string3) {
        this.source = string;
        this.destination = string2;
        this.entry = string3;
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof FlattenStatementsHelper$FinallyPathWrapper) {
            object = (FlattenStatementsHelper$FinallyPathWrapper)object;
            return (String.valueOf(this.source) + ":" + this.destination + ":" + this.entry).equals(String.valueOf(((FlattenStatementsHelper$FinallyPathWrapper)object).source) + ":" + ((FlattenStatementsHelper$FinallyPathWrapper)object).destination + ":" + ((FlattenStatementsHelper$FinallyPathWrapper)object).entry);
        }
        return false;
    }

    public final int hashCode() {
        return (String.valueOf(this.source) + ":" + this.destination + ":" + this.entry).hashCode();
    }

    /* synthetic */ FlattenStatementsHelper$FinallyPathWrapper(String string, String string2, String string3, FlattenStatementsHelper$FinallyPathWrapper flattenStatementsHelper$FinallyPathWrapper) {
        this(string, string2, string3);
    }
}

