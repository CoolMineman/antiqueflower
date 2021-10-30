/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.stats;

import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.stats.Statement;

public final class RootStatement
extends Statement {
    private Statement dummyExit;

    public RootStatement(Statement statement, Statement statement2) {
        this.type = 13;
        this.first = statement;
        this.dummyExit = statement2;
        this.stats.addWithKey(this.first, this.first.id);
        this.first.setParent(this);
    }

    public final String toJava(int n) {
        return String.valueOf(ExprProcessor.listToJava(this.varDefinitions, n)) + this.first.toJava(n);
    }

    public final Statement getDummyExit() {
        return this.dummyExit;
    }
}

