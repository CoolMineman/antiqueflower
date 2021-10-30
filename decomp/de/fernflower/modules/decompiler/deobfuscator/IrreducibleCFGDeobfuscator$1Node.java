/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.deobfuscator;

import java.util.HashSet;
import java.util.Set;

final class IrreducibleCFGDeobfuscator$1Node {
    public Integer id;
    public Set preds = new HashSet();
    public Set succs = new HashSet();

    public IrreducibleCFGDeobfuscator$1Node(Integer n) {
        this.id = n;
    }
}

