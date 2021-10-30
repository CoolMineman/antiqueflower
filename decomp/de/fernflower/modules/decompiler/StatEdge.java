/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.modules.decompiler.stats.Statement;

public final class StatEdge {
    public static final int[] TYPES = new int[]{1, 2, 4, 8, 32};
    private int type;
    private Statement source;
    private Statement destination;
    private String exception;
    public Statement closure;
    public boolean labeled = true;
    public boolean explicit = true;

    public StatEdge(int n, Statement statement, Statement statement2, Statement statement3) {
        this(n, statement, statement2);
        this.closure = statement3;
    }

    public StatEdge(int n, Statement statement, Statement statement2) {
        this.type = n;
        this.source = statement;
        this.destination = statement2;
    }

    public StatEdge(Statement statement, Statement statement2, String string) {
        this(2, statement, statement2);
        this.exception = string;
    }

    public final int getType() {
        return this.type;
    }

    public final void setType(int n) {
        this.type = n;
    }

    public final Statement getSource() {
        return this.source;
    }

    public final void setSource(Statement statement) {
        this.source = statement;
    }

    public final Statement getDestination() {
        return this.destination;
    }

    public final void setDestination(Statement statement) {
        this.destination = statement;
    }

    public final String getException() {
        return this.exception;
    }
}

