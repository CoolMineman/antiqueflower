/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.util.ListStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public final class StrongConnectivityHelper {
    private ListStack lstack;
    private int ncounter;
    private HashSet tset;
    private HashMap dfsnummap;
    private HashMap lowmap;
    private List components;
    private HashSet setProcessed;

    public StrongConnectivityHelper() {
    }

    public StrongConnectivityHelper(Statement statement) {
        this.findComponents(statement);
    }

    private List findComponents(Statement statement) {
        this.components = new ArrayList();
        this.setProcessed = new HashSet();
        this.visitTree(statement.getFirst());
        for (Statement statement2 : statement.getStats()) {
            if (this.setProcessed.contains(statement2) || !statement2.getPredecessorEdges(0x40000000).isEmpty()) continue;
            this.visitTree(statement2);
        }
        for (Statement statement2 : statement.getStats()) {
            if (this.setProcessed.contains(statement2)) continue;
            this.visitTree(statement2);
        }
        return this.components;
    }

    public static boolean isExitComponent(List list) {
        HashSet hashSet = new HashSet();
        for (Statement statement : list) {
            hashSet.addAll(statement.getNeighbours(1, 1));
        }
        hashSet.removeAll(list);
        return hashSet.size() == 0;
    }

    public static List getExitReps(List list2) {
        ArrayList<Statement> arrayList = new ArrayList<Statement>();
        for (List list2 : list2) {
            if (!StrongConnectivityHelper.isExitComponent(list2)) continue;
            arrayList.add((Statement)list2.get(0));
        }
        return arrayList;
    }

    private void visitTree(Statement statement) {
        this.lstack = new ListStack();
        this.ncounter = 0;
        this.tset = new HashSet();
        this.dfsnummap = new HashMap();
        this.lowmap = new HashMap();
        this.visit(statement);
        this.setProcessed.addAll(this.tset);
        this.setProcessed.add(statement);
    }

    private void visit(Statement statement) {
        this.lstack.push(statement);
        this.dfsnummap.put(statement, this.ncounter);
        this.lowmap.put(statement, this.ncounter);
        ++this.ncounter;
        List list = statement.getNeighbours(1, 1);
        list.removeAll(this.setProcessed);
        int n = 0;
        while (n < list.size()) {
            int n2;
            Statement statement2 = (Statement)list.get(n);
            if (this.tset.contains(statement2)) {
                n2 = (Integer)this.dfsnummap.get(statement2);
            } else {
                this.tset.add(statement2);
                this.visit(statement2);
                n2 = (Integer)this.lowmap.get(statement2);
            }
            this.lowmap.put(statement, Math.min((Integer)this.lowmap.get(statement), n2));
            ++n;
        }
        if (((Integer)this.lowmap.get(statement)).intValue() == ((Integer)this.dfsnummap.get(statement)).intValue()) {
            Statement statement3;
            ArrayList<Statement> arrayList = new ArrayList<Statement>();
            do {
                statement3 = (Statement)this.lstack.pop();
                arrayList.add(statement3);
            } while (statement3 != statement);
            this.components.add(arrayList);
        }
    }

    public final List getComponents() {
        return this.components;
    }
}

