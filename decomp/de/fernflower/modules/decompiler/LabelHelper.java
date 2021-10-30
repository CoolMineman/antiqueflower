/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.modules.decompiler.InlineSingleBlockHelper;
import de.fernflower.modules.decompiler.MergeHelper;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.stats.DoStatement;
import de.fernflower.modules.decompiler.stats.IfStatement;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.SequenceStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.stats.SwitchStatement;
import de.fernflower.modules.decompiler.stats.SynchronizedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class LabelHelper {
    public static void cleanUpEdges(RootStatement rootStatement) {
        LabelHelper.resetAllEdges(rootStatement);
        LabelHelper.removeNonImmediateEdges(rootStatement);
        LabelHelper.liftClosures(rootStatement);
        LabelHelper.lowContinueLabels(rootStatement, new HashSet());
        LabelHelper.lowClosures(rootStatement);
    }

    public static void setRetEdgesUnlabeled(RootStatement object2) {
        LabelHelper.setExplicitEdges((Statement)object2);
        LabelHelper.hideDefaultSwitchEdges((Statement)object2);
        LabelHelper.processStatementLabel((Statement)object2);
        for (Object object2 : ((RootStatement)object2).getDummyExit().getAllPredecessorEdges()) {
            List list = ((StatEdge)object2).getSource().getExprents();
            if (((StatEdge)object2).getType() != 32 && (list == null || list.isEmpty() || ((Exprent)list.get((int)(list.size() - 1))).type != 4)) continue;
            ((StatEdge)object2).labeled = false;
        }
    }

    private static void liftClosures(Statement statement) {
        block4: for (StatEdge statEdge : statement.getAllSuccessorEdges()) {
            block0 : switch (statEdge.getType()) {
                case 8: {
                    if (statEdge.getDestination() == statEdge.closure) break;
                    statEdge.getDestination().addLabeledEdge(statEdge);
                    break;
                }
                case 4: {
                    Statement statement2 = statEdge.getDestination();
                    if (statement2.type == 14) break;
                    Statement statement3 = statement2.getParent();
                    ArrayList arrayList = new ArrayList();
                    if (statement3.type == 15) {
                        arrayList.addAll(((SequenceStatement)statement3).getStats());
                    } else if (statement3.type == 6) {
                        arrayList.addAll(((SwitchStatement)statement3).getCaseStatements());
                    }
                    int n = 0;
                    while (n < arrayList.size()) {
                        if (arrayList.get(n) == statement2) {
                            ((Statement)arrayList.get(n - 1)).addLabeledEdge(statEdge);
                            break block0;
                        }
                        ++n;
                    }
                    continue block4;
                }
            }
        }
        Iterator iterator = statement.getStats().iterator();
        while (iterator.hasNext()) {
            LabelHelper.liftClosures((Statement)iterator.next());
        }
    }

    private static void removeNonImmediateEdges(Statement statement) {
        Iterator iterator = statement.getStats().iterator();
        while (iterator.hasNext()) {
            StatEdge statEdge = null;
            LabelHelper.removeNonImmediateEdges((Statement)iterator.next());
        }
        if (!statement.hasBasicSuccEdge()) {
            for (StatEdge statEdge : statement.getSuccessorEdges(12)) {
                statement.removeSuccessor(statEdge);
            }
        }
    }

    public static void lowContinueLabels(Statement statement, HashSet hashSet) {
        boolean bl = statement.type != 5;
        if (!bl) {
            DoStatement doStatement = (DoStatement)statement;
            boolean bl2 = bl = doStatement.getLooptype() == 0 || doStatement.getLooptype() == 2 || doStatement.getLooptype() == 3 && doStatement.getIncExprent() == null;
        }
        if (bl) {
            hashSet.addAll(statement.getPredecessorEdges(8));
        }
        if (bl && statement.type == 5) {
            for (StatEdge statEdge : hashSet) {
                if (!statement.containsStatementStrict(statEdge.getSource())) continue;
                statEdge.getDestination().removePredecessor(statEdge);
                statEdge.getSource().changeEdgeNode(1, statEdge, statement);
                statement.addPredecessor(statEdge);
                statement.addLabeledEdge(statEdge);
            }
        }
        for (Statement statement2 : statement.getStats()) {
            if (statement2 == statement.getFirst()) {
                LabelHelper.lowContinueLabels(statement2, hashSet);
                continue;
            }
            LabelHelper.lowContinueLabels(statement2, new HashSet());
        }
    }

    public static void lowClosures(Statement statement) {
        for (StatEdge statEdge : new ArrayList(statement.getLabelEdges())) {
            if (statEdge.getType() != 4) continue;
            for (Statement statement2 : statement.getStats()) {
                if (!statement2.containsStatementStrict(statEdge.getSource()) || !MergeHelper.isDirectPath(statement2, statEdge.getDestination())) continue;
                statement2.addLabeledEdge(statEdge);
            }
        }
        Iterator iterator = statement.getStats().iterator();
        while (iterator.hasNext()) {
            LabelHelper.lowClosures((Statement)iterator.next());
        }
    }

    private static void resetAllEdges(Statement statement) {
        Iterator iterator = statement.getStats().iterator();
        while (iterator.hasNext()) {
            StatEdge statEdge = null;
            LabelHelper.resetAllEdges((Statement)iterator.next());
        }
        for (StatEdge statEdge : statement.getAllSuccessorEdges()) {
            ((StatEdge)iterator.next()).explicit = true;
            statEdge.labeled = true;
        }
    }

    /*
     * WARNING - void declaration
     */
    private static HashMap setExplicitEdges(Statement object) {
        HashMap hashMap = new HashMap();
        if (((Statement)object).getExprents() != null) {
            return hashMap;
        }
        switch (((Statement)object).type) {
            case 7: 
            case 12: {
                for (Statement ifStatement : ((Statement)object).getStats()) {
                    HashMap hashMap2 = LabelHelper.setExplicitEdges(ifStatement);
                    LabelHelper.processEdgesWithNext(ifStatement, hashMap2, null);
                    if (((Statement)object).type != 7 && ifStatement != ((Statement)object).getFirst() || hashMap2 == null) continue;
                    for (Map.Entry switchStatement : hashMap2.entrySet()) {
                        if (hashMap.containsKey(switchStatement.getKey())) {
                            ((List)hashMap.get(switchStatement.getKey())).addAll((Collection)switchStatement.getValue());
                            continue;
                        }
                        hashMap.put((Statement)switchStatement.getKey(), (List)switchStatement.getValue());
                    }
                }
                break;
            }
            case 5: {
                hashMap = LabelHelper.setExplicitEdges(((Statement)object).getFirst());
                LabelHelper.processEdgesWithNext(((Statement)object).getFirst(), hashMap, (Statement)object);
                break;
            }
            case 2: {
                IfStatement ifStatement = (IfStatement)object;
                if (ifStatement.getIfstat() == null) {
                    LabelHelper.processEdgesWithNext(ifStatement.getFirst(), hashMap, null);
                    break;
                }
                if (ifStatement.getIfstat() != null) {
                    hashMap = LabelHelper.setExplicitEdges(ifStatement.getIfstat());
                    LabelHelper.processEdgesWithNext(ifStatement.getIfstat(), hashMap, null);
                }
                HashMap hashMap3 = null;
                if (ifStatement.getElsestat() != null) {
                    hashMap3 = LabelHelper.setExplicitEdges(ifStatement.getElsestat());
                    LabelHelper.processEdgesWithNext(ifStatement.getElsestat(), hashMap3, null);
                }
                if (hashMap3 == null) break;
                for (Map.Entry entry : hashMap3.entrySet()) {
                    if (hashMap.containsKey(entry.getKey())) {
                        ((List)hashMap.get(entry.getKey())).addAll((Collection)entry.getValue());
                        continue;
                    }
                    hashMap.put((Statement)entry.getKey(), (List)entry.getValue());
                }
                break;
            }
            case 13: {
                hashMap = LabelHelper.setExplicitEdges(((Statement)object).getFirst());
                LabelHelper.processEdgesWithNext(((Statement)object).getFirst(), hashMap, ((RootStatement)object).getDummyExit());
                break;
            }
            case 15: {
                int n = 0;
                while (n < ((Statement)object).getStats().size() - 1) {
                    Statement statement = (Statement)((Statement)object).getStats().get(n);
                    Object var4_17 = null;
                    LabelHelper.processEdgesWithNext(statement, LabelHelper.setExplicitEdges(statement), (Statement)((Statement)object).getStats().get(n + 1));
                    ++n;
                }
                Statement statement = (Statement)((Statement)object).getStats().get(n);
                hashMap = LabelHelper.setExplicitEdges(statement);
                LabelHelper.processEdgesWithNext(statement, hashMap, null);
                break;
            }
            case 6: {
                Statement statement;
                void var4_20;
                SwitchStatement switchStatement = (SwitchStatement)object;
                boolean bl = false;
                while (++var4_20 < switchStatement.getCaseStatements().size() - 1) {
                    statement = (Statement)switchStatement.getCaseStatements().get((int)var4_20);
                    object = (Statement)switchStatement.getCaseStatements().get((int)(var4_20 + true));
                    if (((Statement)object).getExprents() != null && ((Statement)object).getExprents().isEmpty()) {
                        object = ((StatEdge)((Statement)object).getAllSuccessorEdges().get(0)).getDestination();
                    }
                    LabelHelper.processEdgesWithNext(statement, LabelHelper.setExplicitEdges(statement), (Statement)object);
                }
                int n = switchStatement.getCaseStatements().size() - 1;
                if (n < 0) break;
                statement = (Statement)switchStatement.getCaseStatements().get(n);
                if (statement.getExprents() != null && statement.getExprents().isEmpty()) {
                    object = (StatEdge)statement.getAllSuccessorEdges().get(0);
                    hashMap.put(((StatEdge)object).getDestination(), new ArrayList(Arrays.asList(object)));
                    break;
                }
                hashMap = LabelHelper.setExplicitEdges(statement);
                LabelHelper.processEdgesWithNext(statement, hashMap, null);
                break;
            }
            case 10: {
                SynchronizedStatement synchronizedStatement = (SynchronizedStatement)object;
                LabelHelper.processEdgesWithNext(synchronizedStatement.getFirst(), LabelHelper.setExplicitEdges(((Statement)object).getFirst()), synchronizedStatement.getBody());
                hashMap = LabelHelper.setExplicitEdges(synchronizedStatement.getBody());
                LabelHelper.processEdgesWithNext(synchronizedStatement.getBody(), hashMap, null);
            }
        }
        return hashMap;
    }

    /*
     * WARNING - void declaration
     */
    private static void processEdgesWithNext(Statement object, HashMap hashMap, Statement object2) {
        StatEdge statEdge = null;
        List list = ((Statement)((Object)object)).getAllSuccessorEdges();
        if (!list.isEmpty()) {
            statEdge = (StatEdge)list.get(0);
            if (statEdge.getDestination() == object2) {
                statEdge.explicit = false;
                statEdge = null;
            } else {
                object2 = statEdge.getDestination();
            }
        }
        if (((Statement)object).type == 5 && ((DoStatement)((Object)object)).getLooptype() == 0) {
            object2 = null;
        }
        if (object2 == null) {
            if (hashMap.size() == 1 && (list = (List)hashMap.values().iterator().next()).size() > 1 && ((Statement)hashMap.keySet().iterator().next()).type != 14) {
                void var6_12;
                StatEdge statEdge2 = (StatEdge)list.get(0);
                Statement statement = ((Statement)((Object)object)).getParent();
                if (!statement.containsStatementStrict(statEdge2.closure)) {
                    Statement object3 = statEdge2.closure;
                }
                object2 = new StatEdge(statEdge2.getType(), (Statement)((Object)object), statEdge2.getDestination(), (Statement)var6_12);
                ((Statement)((Object)object)).addSuccessor((StatEdge)object2);
                object = list.iterator();
                while (object.hasNext()) {
                    ((StatEdge)object.next()).explicit = false;
                }
                hashMap.put(((StatEdge)object2).getDestination(), new ArrayList(Arrays.asList(object2)));
            }
        } else {
            boolean bl = false;
            for (Map.Entry entry : hashMap.entrySet()) {
                if (entry.getKey() != object2) continue;
                Iterator iterator = ((List)entry.getValue()).iterator();
                while (iterator.hasNext()) {
                    ((StatEdge)iterator.next()).explicit = false;
                }
                bl = true;
                break;
            }
            if (((Statement)((Object)object)).getAllSuccessorEdges().isEmpty() && !bl) {
                Map.Entry entry;
                entry = null;
                for (Map.Entry entry2 : hashMap.entrySet()) {
                    if (((Statement)entry2.getKey()).type == 14 || entry != null && ((List)entry2.getValue()).size() <= entry.size()) continue;
                    entry = (List)entry2.getValue();
                }
                if (entry != null && entry.size() > 1) {
                    StatEdge statEdge2 = (StatEdge)entry.get(0);
                    object2 = ((Statement)((Object)object)).getParent();
                    if (!((Statement)object2).containsStatementStrict(statEdge2.closure)) {
                        object2 = statEdge2.closure;
                    }
                    StatEdge statEdge3 = new StatEdge(statEdge2.getType(), (Statement)((Object)object), statEdge2.getDestination(), (Statement)object2);
                    ((Statement)((Object)object)).addSuccessor(statEdge3);
                    object2 = entry.iterator();
                    while (object2.hasNext()) {
                        ((StatEdge)object2.next()).explicit = false;
                    }
                }
            }
            hashMap.clear();
        }
        if (statEdge != null) {
            hashMap.put(statEdge.getDestination(), new ArrayList(Arrays.asList(statEdge)));
        }
    }

    private static void hideDefaultSwitchEdges(Statement statement) {
        Object object;
        SwitchStatement switchStatement;
        int n;
        if (statement.type == 6 && (n = (switchStatement = (SwitchStatement)statement).getCaseStatements().size() - 1) >= 0 && ((Statement)(object = (Statement)switchStatement.getCaseStatements().get(n))).getExprents() != null && ((Statement)object).getExprents().isEmpty() && !((StatEdge)object.getAllSuccessorEdges().get((int)0)).explicit) {
            object = (List)switchStatement.getCaseEdges().get(n);
            object.remove(switchStatement.getDefault_edge());
            if (object.isEmpty()) {
                switchStatement.getCaseStatements().remove(n);
                switchStatement.getCaseEdges().remove(n);
            }
        }
        Iterator iterator = statement.getStats().iterator();
        while (iterator.hasNext()) {
            LabelHelper.hideDefaultSwitchEdges((Statement)iterator.next());
        }
    }

    private static HashSet[] processStatementLabel(Statement statement) {
        HashSet<Statement> hashSet = new HashSet<Statement>();
        HashSet<Statement> hashSet2 = new HashSet<Statement>();
        if (statement.getExprents() == null) {
            Object object2 = statement.getStats().iterator();
            while (object2.hasNext()) {
                Object var3_4 = null;
                HashSet[] hashSetArray = LabelHelper.processStatementLabel((Statement)object2.next());
                hashSet.addAll(hashSetArray[0]);
                hashSet2.addAll((Collection<Statement>)hashSetArray[1]);
            }
            boolean bl = statement.type == 5 || statement.type == 6;
            for (Object object2 : statement.getLabelEdges()) {
                if (!((StatEdge)object2).explicit || !bl || (((StatEdge)object2).getType() != 4 || !hashSet.contains(((StatEdge)object2).getSource())) && (((StatEdge)object2).getType() != 8 || !hashSet2.contains(((StatEdge)object2).getSource()))) continue;
                ((StatEdge)object2).labeled = false;
            }
            switch (statement.type) {
                case 5: {
                    hashSet2.clear();
                }
                case 6: {
                    hashSet.clear();
                }
            }
        }
        hashSet.add(statement);
        hashSet2.add(statement);
        return new HashSet[]{hashSet, hashSet2};
    }

    public static void replaceContinueWithBreak(Statement statement) {
        Object object2;
        if (statement.type == 5) {
            Iterator iterator = null;
            for (Object object2 : statement.getPredecessorEdges(8)) {
                boolean bl;
                if (!((StatEdge)object2).explicit) continue;
                Object object3 = object2;
                Statement statement2 = ((StatEdge)object3).closure;
                block1: do {
                    bl = false;
                    for (Statement statement3 : statement2.getStats()) {
                        if (!statement3.containsStatementStrict(((StatEdge)object3).getSource()) || !MergeHelper.isDirectPath(statement3, ((StatEdge)object3).getDestination())) continue;
                        statement2 = statement3;
                        bl = true;
                        continue block1;
                    }
                } while (bl);
                object3 = statement2;
                if (object3 == ((StatEdge)object2).closure || InlineSingleBlockHelper.isBreakEdgeLabeled(((StatEdge)object2).getSource(), (Statement)object3)) continue;
                ((StatEdge)object2).getSource().changeEdgeType(1, (StatEdge)object2, 4);
                ((StatEdge)object2).labeled = false;
                ((Statement)object3).addLabeledEdge((StatEdge)object2);
            }
        }
        object2 = statement.getStats().iterator();
        while (object2.hasNext()) {
            LabelHelper.replaceContinueWithBreak((Statement)object2.next());
        }
    }
}

