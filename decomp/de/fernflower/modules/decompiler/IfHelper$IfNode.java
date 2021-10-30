/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.modules.decompiler.stats.Statement;
import java.util.ArrayList;
import java.util.List;

final class IfHelper$IfNode {
    public Statement value;
    public List succs = new ArrayList();
    public List edgetypes = new ArrayList();

    public IfHelper$IfNode(Statement statement) {
        this.value = statement;
    }

    public final void addChild(IfHelper$IfNode ifHelper$IfNode, int n) {
        this.succs.add(ifHelper$IfNode);
        this.edgetypes.add(new Integer(n));
    }
}

