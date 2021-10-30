/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.collectors;

import java.util.HashSet;

public final class VarNamesCollector {
    private HashSet usedNames = new HashSet();

    public VarNamesCollector() {
    }

    public VarNamesCollector(HashSet hashSet) {
        this.usedNames.addAll(hashSet);
    }

    public final void addName(String string) {
        this.usedNames.add(string);
    }

    public final String getFreeName(int n) {
        return this.getFreeName("var" + n);
    }

    public final String getFreeName(String string) {
        while (this.usedNames.contains(string)) {
            string = String.valueOf(string) + "x";
        }
        this.usedNames.add(string);
        return string;
    }
}

