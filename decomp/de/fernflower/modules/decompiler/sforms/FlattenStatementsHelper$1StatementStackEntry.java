/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.sforms;

import de.fernflower.modules.decompiler.stats.Statement;
import java.util.LinkedList;
import java.util.List;

final class FlattenStatementsHelper$1StatementStackEntry {
    public Statement statement;
    public LinkedList stackFinally;
    public List tailExprents;
    public int statementIndex;
    public int edgeIndex;
    public List succEdges;

    public FlattenStatementsHelper$1StatementStackEntry(Statement statement, LinkedList linkedList, List list) {
        this.statement = statement;
        this.stackFinally = linkedList;
        this.tailExprents = list;
    }
}

