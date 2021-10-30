/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.sforms;

import de.fernflower.modules.decompiler.stats.BasicBlockStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import java.util.ArrayList;
import java.util.List;

public final class DirectNode {
    public int type;
    public String id;
    public BasicBlockStatement block;
    public Statement statement;
    public List exprents = new ArrayList();
    public List succs = new ArrayList();
    public List preds = new ArrayList();

    public DirectNode(int n, Statement statement, String string) {
        this.type = n;
        this.statement = statement;
        this.id = string;
    }

    public DirectNode(Statement statement, BasicBlockStatement basicBlockStatement) {
        this.type = 1;
        this.statement = statement;
        this.id = basicBlockStatement.id.toString();
        this.block = basicBlockStatement;
    }

    public final String toString() {
        return this.id;
    }
}

