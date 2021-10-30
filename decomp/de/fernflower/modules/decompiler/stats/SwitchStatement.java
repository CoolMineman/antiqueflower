/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.stats;

import de.fernflower.code.SwitchInstruction;
import de.fernflower.code.cfg.BasicBlock;
import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.DecHelper;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.SwitchExprent;
import de.fernflower.modules.decompiler.stats.BasicBlockStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public final class SwitchStatement
extends Statement {
    private List caseStatements = new ArrayList();
    private List caseEdges = new ArrayList();
    private List caseValues = new ArrayList();
    private StatEdge default_edge;
    private List headexprent = new ArrayList();

    private SwitchStatement() {
        this.type = 6;
        this.headexprent.add(null);
    }

    private SwitchStatement(Statement statement2, Statement object) {
        this();
        this.first = statement2;
        this.stats.addWithKey(statement2, statement2.id);
        HashSet hashSet = new HashSet(statement2.getNeighbours(1, 1));
        if (object != null) {
            this.post = object;
            hashSet.remove(this.post);
        }
        this.default_edge = (StatEdge)statement2.getSuccessorEdges(0x40000000).get(0);
        for (Statement statement2 : hashSet) {
            this.stats.addWithKey(statement2, statement2.id);
        }
    }

    public static Statement isHead(Statement statement) {
        ArrayList arrayList;
        if (statement.type == 8 && statement.getLastBasicType() == 1 && DecHelper.isChoiceStatement(statement, arrayList = new ArrayList())) {
            Statement statement2 = (Statement)arrayList.remove(0);
            Iterator iterator = arrayList.iterator();
            while (iterator.hasNext()) {
                if (!((Statement)iterator.next()).isMonitorEnter()) continue;
                return null;
            }
            if (DecHelper.checkStatementExceptions(arrayList)) {
                return new SwitchStatement(statement, statement2);
            }
        }
        return null;
    }

    public final String toJava(int n) {
        String string = InterpreterUtil.getIndentString(n);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ExprProcessor.listToJava(this.varDefinitions, n));
        stringBuilder.append(this.first.toJava(n));
        if (this.isLabeled()) {
            stringBuilder.append(String.valueOf(string) + "label" + this.id + ":\r\n");
        }
        stringBuilder.append(String.valueOf(string) + ((Exprent)this.headexprent.get(0)).toJava(n) + " {\r\n");
        int n2 = 0;
        while (n2 < this.caseStatements.size()) {
            Statement statement = (Statement)this.caseStatements.get(n2);
            List list = (List)this.caseEdges.get(n2);
            List list2 = (List)this.caseValues.get(n2);
            int n3 = 0;
            while (n3 < list.size()) {
                if (list.get(n3) == this.default_edge) {
                    stringBuilder.append(String.valueOf(string) + "default:\r\n");
                } else {
                    stringBuilder.append(String.valueOf(string) + "case " + ((ConstExprent)list2.get(n3)).toJava(n) + ":\r\n");
                }
                ++n3;
            }
            stringBuilder.append(ExprProcessor.jmpWrapper(statement, n + 1, false));
            ++n2;
        }
        stringBuilder.append(String.valueOf(string) + "}\r\n");
        return stringBuilder.toString();
    }

    public final void initExprents() {
        SwitchExprent switchExprent = (SwitchExprent)this.first.getExprents().remove(this.first.getExprents().size() - 1);
        switchExprent.setCaseValues(this.caseValues);
        this.headexprent.set(0, switchExprent);
    }

    public final List getSequentialObjects() {
        ArrayList arrayList = new ArrayList(this.stats);
        arrayList.add(1, this.headexprent.get(0));
        return arrayList;
    }

    public final void replaceExprent(Exprent exprent, Exprent exprent2) {
        if (this.headexprent.get(0) == exprent) {
            this.headexprent.set(0, exprent2);
        }
    }

    public final void replaceStatement(Statement statement, Statement statement2) {
        int n = 0;
        while (n < this.caseStatements.size()) {
            if (this.caseStatements.get(n) == statement) {
                this.caseStatements.set(n, statement2);
            }
            ++n;
        }
        super.replaceStatement(statement, statement2);
    }

    public final Statement getSimpleCopy() {
        return new SwitchStatement();
    }

    public final void initSimpleCopy() {
        this.first = (Statement)this.stats.get(0);
        this.default_edge = (StatEdge)this.first.getSuccessorEdges(0x40000000).get(0);
        this.sortEdgesAndNodes();
    }

    public final void sortEdgesAndNodes() {
        Object object;
        ArrayList<Integer> arrayList;
        Object object2;
        Object object3 = new HashMap<StatEdge, Integer>();
        Object object4 = this.first.getSuccessorEdges(0x40000000);
        int n = 0;
        while (n < object4.size()) {
            ((HashMap)object3).put((StatEdge)object4.get(n), n == 0 ? object4.size() : n);
            ++n;
        }
        ArrayList<Object> arrayList2 = null;
        object4 = ((SwitchInstruction)((BasicBlockStatement)this.first).getBlock().getLastInstruction()).getValues();
        arrayList2 = new ArrayList<Object>();
        Object object52 = new ArrayList<Object>();
        int n2 = 1;
        while (n2 < this.stats.size()) {
            object2 = (Statement)this.stats.get(n2);
            arrayList = new ArrayList<Integer>();
            for (Object object6 : ((Statement)object2).getPredecessorEdges(1)) {
                if (((StatEdge)object6).getSource() != this.first) continue;
                arrayList.add((Integer)((HashMap)object3).get(object6));
            }
            Collections.sort(arrayList);
            arrayList2.add(object2);
            object52.add(arrayList);
            ++n2;
        }
        Object object7 = this.first.getSuccessorEdges(12);
        while (!object7.isEmpty()) {
            object2 = (StatEdge)object7.get(0);
            arrayList = new ArrayList();
            int n3 = object7.size() - 1;
            while (n3 >= 0) {
                object = (StatEdge)object7.get(n3);
                if (((StatEdge)object).getDestination() == ((StatEdge)object2).getDestination() && ((StatEdge)object).getType() == ((StatEdge)object2).getType()) {
                    arrayList.add((Integer)((HashMap)object3).get(object));
                    object7.remove(n3);
                }
                --n3;
            }
            Collections.sort(arrayList);
            arrayList2.add(null);
            object52.add(arrayList);
        }
        int n4 = 0;
        while (n4 < object52.size() - 1) {
            int n5 = object52.size() - 1;
            while (n5 > n4) {
                if ((Integer)((List)object52.get(n5 - 1)).get(0) > (Integer)((List)object52.get(n5)).get(0)) {
                    object52.set(n5, object52.set(n5 - 1, (List)object52.get(n5)));
                    arrayList2.set(n5, arrayList2.set(n5 - 1, (Statement)arrayList2.get(n5)));
                }
                --n5;
            }
            ++n4;
        }
        n4 = 0;
        while (n4 < arrayList2.size()) {
            Statement statement = (Statement)arrayList2.get(n4);
            if (statement != null) {
                Object object6;
                object6 = new HashSet(statement.getNeighbours(1, 0));
                ((HashSet)object6).remove(this.first);
                if (!((HashSet)object6).isEmpty()) {
                    object = (Statement)((HashSet)object6).iterator().next();
                    int n6 = n4 + 1;
                    while (n6 < arrayList2.size()) {
                        if (arrayList2.get(n6) == object) {
                            arrayList2.add(n6 + 1, statement);
                            object52.add(n6 + 1, (List)object52.get(n4));
                            arrayList2.remove(n4);
                            object52.remove(n4);
                            --n4;
                            break;
                        }
                        ++n6;
                    }
                }
            }
            ++n4;
        }
        ArrayList<HashMap<StatEdge, Integer>> arrayList3 = new ArrayList<HashMap<StatEdge, Integer>>();
        ArrayList<ArrayList<Object>> arrayList4 = new ArrayList<ArrayList<Object>>();
        object = object52.iterator();
        while (object.hasNext()) {
            List list = (List)object.next();
            object3 = new ArrayList();
            object52 = new ArrayList();
            object7 = this.first.getSuccessorEdges(0x40000000);
            for (Integer n7 : list) {
                int n8 = n7.intValue() == object7.size() ? 0 : n7;
                object3.add((StatEdge)object7.get(n8));
                object52.add(n8 == 0 ? null : new ConstExprent((int)object4[n8 - 1], false));
            }
            arrayList3.add((HashMap<StatEdge, Integer>)object3);
            arrayList4.add((ArrayList<Object>)object52);
        }
        int n9 = 0;
        while (n9 < arrayList2.size()) {
            if (arrayList2.get(n9) == null) {
                object = new BasicBlockStatement(new BasicBlock(DecompilerContext.getCountercontainer().getCounterAndIncrement(0)));
                object3 = (StatEdge)((List)arrayList3.get(n9)).get(0);
                ((Statement)object).addSuccessor(new StatEdge(((StatEdge)object3).getType(), (Statement)object, ((StatEdge)object3).getDestination(), ((StatEdge)object3).closure));
                for (Object object52 : (List)arrayList3.get(n9)) {
                    ((StatEdge)object52).getSource().changeEdgeType(1, (StatEdge)object52, 1);
                    ((StatEdge)object52).closure.getLabelEdges().remove(object52);
                    ((StatEdge)object52).getDestination().removePredecessor((StatEdge)object52);
                    ((StatEdge)object52).getSource().changeEdgeNode(1, (StatEdge)object52, (Statement)object);
                    ((Statement)object).addPredecessor((StatEdge)object52);
                }
                arrayList2.set(n9, object);
                this.stats.addWithKey(object, ((BasicBlockStatement)object).id);
                ((Statement)object).setParent(this);
            }
            ++n9;
        }
        this.caseStatements = arrayList2;
        this.caseEdges = arrayList3;
        this.caseValues = arrayList4;
    }

    public final List getHeadexprentList() {
        return this.headexprent;
    }

    public final List getCaseEdges() {
        return this.caseEdges;
    }

    public final List getCaseStatements() {
        return this.caseStatements;
    }

    public final StatEdge getDefault_edge() {
        return this.default_edge;
    }
}

