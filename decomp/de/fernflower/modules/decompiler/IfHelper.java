/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.modules.decompiler.IfHelper$IfNode;
import de.fernflower.modules.decompiler.MergeHelper;
import de.fernflower.modules.decompiler.SequenceHelper;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FunctionExprent;
import de.fernflower.modules.decompiler.exps.IfExprent;
import de.fernflower.modules.decompiler.stats.IfStatement;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.SequenceStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public final class IfHelper {
    public static boolean mergeAllIfs(RootStatement rootStatement) {
        boolean bl = IfHelper.mergeAllIfsRec(rootStatement, new HashSet());
        if (bl) {
            SequenceHelper.condenseSequences(rootStatement);
        }
        return bl;
    }

    private static boolean mergeAllIfsRec(Statement statement, HashSet hashSet) {
        boolean bl = false;
        if (statement.getExprents() == null) {
            boolean bl2;
            do {
                bl2 = false;
                for (Statement statement2 : statement.getStats()) {
                    bl |= IfHelper.mergeAllIfsRec(statement2, hashSet);
                    bl2 = IfHelper.mergeIfs(statement2, hashSet);
                    if (bl2) break;
                }
                bl |= bl2;
            } while (bl2);
        }
        return bl;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public static boolean mergeIfs(Statement var0, HashSet var1_1) {
        if (var0.type != 2 && var0.type != 15) {
            return false;
        }
        var2_2 = false;
        while (true) {
            var3_3 = false;
            var4_4 = new ArrayList<Statement>();
            if (var0.type == 2) {
                var4_4.add(var0);
            } else {
                var4_4.addAll(var0.getStats());
            }
            var5_5 = var4_4.size() == 1;
            var6_6 = var4_4.iterator();
            while (var6_6.hasNext()) {
                block29: {
                    var4_4 = (Statement)var6_6.next();
                    if (var4_4.type != 2) continue;
                    var9_9 = var5_5;
                    var8_8 /* !! */  = (IfStatement)var4_4;
                    if (var8_8 /* !! */ .iftype == IfStatement.IFTYPE_IFELSE) {
                        v0 = null;
                    } else {
                        var10_12 = new IfHelper$IfNode(var8_8 /* !! */ );
                        var11_13 = var8_8 /* !! */ .getIfstat();
                        if (var11_13 == null) {
                            var12_14 = var8_8 /* !! */ .getIfEdge();
                            var10_12.addChild(new IfHelper$IfNode(var12_14.getDestination()), 1);
                        } else {
                            var12_14 = new IfHelper$IfNode(var11_13);
                            var10_12.addChild((IfHelper$IfNode)var12_14, 0);
                            if (var11_13.type == 2 && ((IfStatement)var11_13).iftype == 0) {
                                var13_15 = (IfStatement)var11_13;
                                var14_16 = var13_15.getIfstat();
                                if (var14_16 == null) {
                                    var7_7 = var13_15.getIfEdge();
                                    var12_14.addChild(new IfHelper$IfNode(var7_7.getDestination()), 1);
                                } else {
                                    var12_14.addChild(new IfHelper$IfNode((Statement)var14_16), 0);
                                }
                            }
                            if (!var11_13.getAllSuccessorEdges().isEmpty()) {
                                var12_14.addChild(new IfHelper$IfNode(((StatEdge)var11_13.getAllSuccessorEdges().get(0)).getDestination()), 1);
                            }
                        }
                        var12_14 = (StatEdge)var8_8 /* !! */ .getAllSuccessorEdges().get(0);
                        var13_15 = var12_14.getDestination();
                        var14_16 = new IfHelper$IfNode((Statement)var13_15);
                        if (var9_9 || var12_14.getType() != 1) {
                            var10_12.addChild((IfHelper$IfNode)var14_16, 1);
                        } else {
                            var10_12.addChild((IfHelper$IfNode)var14_16, 0);
                            if (var13_15.type == 2 && ((IfStatement)var13_15).iftype == 0) {
                                var7_7 = (IfStatement)var13_15;
                                var8_8 /* !! */  = var7_7.getIfstat();
                                if (var8_8 /* !! */  == null) {
                                    var14_16.addChild(new IfHelper$IfNode(var7_7.getIfEdge().getDestination()), 1);
                                } else {
                                    var14_16.addChild(new IfHelper$IfNode(var8_8 /* !! */ ), 0);
                                }
                            }
                            if (!var13_15.getAllSuccessorEdges().isEmpty()) {
                                var14_16.addChild(new IfHelper$IfNode(((StatEdge)var13_15.getAllSuccessorEdges().get(0)).getDestination()), 1);
                            }
                        }
                        v0 = var7_7 = var10_12;
                    }
                    if (v0 == null) continue;
                    var8_8 /* !! */  = var7_7;
                    if ((Integer)var8_8 /* !! */ .edgetypes.get(0) != 0) ** GOTO lbl-1000
                    var9_10 = (IfHelper$IfNode)var8_8 /* !! */ .succs.get(0);
                    if (var9_10.succs.size() != 2 || ((IfHelper$IfNode)var9_10.succs.get((int)1)).value != ((IfHelper$IfNode)var8_8 /* !! */ .succs.get((int)1)).value) ** GOTO lbl-1000
                    var10_12 = (IfStatement)var8_8 /* !! */ .value;
                    var11_13 = (IfStatement)var9_10.value;
                    var12_14 = ((IfHelper$IfNode)var9_10.succs.get((int)0)).value;
                    if (var11_13.getFirst().getExprents().isEmpty()) {
                        var10_12.getFirst().removeSuccessor(var10_12.getIfEdge());
                        var11_13.removeSuccessor((StatEdge)var11_13.getAllSuccessorEdges().get(0));
                        var10_12.getStats().removeWithKey(var11_13.id);
                        if ((Integer)var9_10.edgetypes.get(0) == 1) {
                            var10_12.setIfstat(null);
                            var13_15 = var11_13.getIfEdge();
                            var11_13.getFirst().removeSuccessor((StatEdge)var13_15);
                            var13_15.setSource(var10_12.getFirst());
                            if (var13_15.closure == var11_13) {
                                var13_15.closure = null;
                            }
                            var10_12.getFirst().addSuccessor((StatEdge)var13_15);
                            var10_12.setIfEdge((StatEdge)var13_15);
                        } else {
                            var11_13.getFirst().removeSuccessor(var11_13.getIfEdge());
                            var13_15 = new StatEdge(1, var10_12.getFirst(), (Statement)var12_14);
                            var10_12.getFirst().addSuccessor((StatEdge)var13_15);
                            var10_12.setIfEdge((StatEdge)var13_15);
                            var10_12.setIfstat((Statement)var12_14);
                            var10_12.getStats().addWithKey(var12_14, var12_14.id);
                            var12_14.setParent((Statement)var10_12);
                            if (!var12_14.getAllSuccessorEdges().isEmpty()) {
                                var14_16 = (StatEdge)var12_14.getAllSuccessorEdges().get(0);
                                if (var14_16.closure == var11_13) {
                                    var14_16.closure = null;
                                }
                            }
                        }
                        var13_15 = var10_12.getHeadexprent();
                        var14_16 = new ArrayList<Exprent>();
                        var14_16.add(var13_15.getCondition());
                        var14_16.add(var11_13.getHeadexprent().getCondition());
                        var13_15.setCondition(new FunctionExprent(48, (List)var14_16));
                        v1 = true;
                    } else lbl-1000:
                    // 3 sources

                    {
                        v1 = var3_3 = false;
                    }
                    if (v1) break;
                    if (var1_1.contains(var4_4.id)) break block29;
                    var8_8 /* !! */  = var7_7;
                    if ((Integer)var8_8 /* !! */ .edgetypes.get(0) != 0) ** GOTO lbl-1000
                    var9_11 = (IfHelper$IfNode)var8_8 /* !! */ .succs.get(0);
                    if (var9_11.succs.size() != 2 || ((IfHelper$IfNode)var9_11.succs.get((int)0)).value != ((IfHelper$IfNode)var8_8 /* !! */ .succs.get((int)1)).value) ** GOTO lbl-1000
                    var10_12 = (IfStatement)var8_8 /* !! */ .value;
                    var11_13 = (IfStatement)var9_11.value;
                    if (var11_13.getFirst().getExprents().isEmpty()) {
                        var10_12.getFirst().removeSuccessor(var10_12.getIfEdge());
                        var11_13.getFirst().removeSuccessor(var11_13.getIfEdge());
                        var10_12.getStats().removeWithKey(var11_13.id);
                        if ((Integer)var9_11.edgetypes.get(1) != 1 || (Integer)var9_11.edgetypes.get(0) != 1) {
                            throw new RuntimeException("inconsistent if structure!");
                        }
                        var10_12.setIfstat(null);
                        var12_14 = (StatEdge)var11_13.getAllSuccessorEdges().get(0);
                        var11_13.removeSuccessor((StatEdge)var12_14);
                        var12_14.setSource(var10_12.getFirst());
                        var10_12.getFirst().addSuccessor((StatEdge)var12_14);
                        var10_12.setIfEdge((StatEdge)var12_14);
                        var12_14 = var10_12.getHeadexprent();
                        var13_15 = new ArrayList<Exprent>();
                        var13_15.add(var12_14.getCondition());
                        var13_15.add(new FunctionExprent(12, Arrays.asList(new Exprent[]{var11_13.getHeadexprent().getCondition()})));
                        var12_14.setCondition(new FunctionExprent(48, (List)var13_15));
                        v2 = true;
                    } else lbl-1000:
                    // 3 sources

                    {
                        v2 = var3_3 = false;
                    }
                    if (v2 || (var3_3 = IfHelper.collapseElse(var7_7))) break;
                }
                if (!(var3_3 = IfHelper.reorderIf((IfStatement)var4_4))) continue;
                var1_1.add(var4_4.id);
                break;
            }
            if (!var3_3) break;
            var2_2 |= var3_3;
        }
        return var2_2;
    }

    private static boolean collapseElse(IfHelper$IfNode object) {
        if ((Integer)((IfHelper$IfNode)object).edgetypes.get(1) == 0) {
            Object object22 = (IfHelper$IfNode)((IfHelper$IfNode)object).succs.get(1);
            if (((IfHelper$IfNode)object22).succs.size() == 2) {
                int n;
                if ((((IfHelper$IfNode)object22.succs.get((int)1)).value == ((IfHelper$IfNode)object.succs.get((int)0)).value ? 2 : (n = ((IfHelper$IfNode)object22.succs.get((int)0)).value == ((IfHelper$IfNode)object.succs.get((int)0)).value ? 1 : 0)) > 0) {
                    object = (IfStatement)((IfHelper$IfNode)object).value;
                    object22 = (IfStatement)((IfHelper$IfNode)object22).value;
                    Statement statement = ((Statement)object).getParent();
                    if (((Statement)object22).getFirst().getExprents().isEmpty()) {
                        Object object32;
                        ((Statement)object).getFirst().removeSuccessor(((IfStatement)object).getIfEdge());
                        ((Statement)object).removeAllSuccessors((Statement)object22);
                        for (Object object32 : ((Statement)object).getAllPredecessorEdges()) {
                            if (((Statement)object).containsStatementStrict(((StatEdge)object32).getSource())) continue;
                            ((Statement)object).removePredecessor((StatEdge)object32);
                            ((StatEdge)object32).getSource().changeEdgeNode(1, (StatEdge)object32, (Statement)object22);
                            ((Statement)object22).addPredecessor((StatEdge)object32);
                        }
                        statement.getStats().removeWithKey(((IfStatement)object).id);
                        if (statement.getFirst() == object) {
                            statement.setFirst((Statement)object22);
                        }
                        object32 = ((IfStatement)object22).getHeadexprent();
                        ArrayList<Exprent> arrayList = new ArrayList<Exprent>();
                        arrayList.add(((IfStatement)object).getHeadexprent().getCondition());
                        if (n == 2) {
                            arrayList.set(0, new FunctionExprent(12, Arrays.asList((Exprent)arrayList.get(0))));
                        }
                        arrayList.add(((IfExprent)object32).getCondition());
                        ((IfExprent)object32).setCondition(new FunctionExprent(n == 1 ? 49 : 48, arrayList));
                        if (((Statement)object22).getFirst().getExprents().isEmpty() && !((Statement)object).getFirst().getExprents().isEmpty()) {
                            ((IfStatement)object22).replaceStatement(((Statement)object22).getFirst(), ((Statement)object).getFirst());
                        }
                        return true;
                    }
                }
            } else if (((IfHelper$IfNode)object22).succs.size() == 1 && ((IfHelper$IfNode)object22.succs.get((int)0)).value == ((IfHelper$IfNode)object.succs.get((int)0)).value) {
                IfStatement ifStatement = (IfStatement)((IfHelper$IfNode)object).value;
                object = ((IfHelper$IfNode)object22).value;
                ifStatement.removeAllSuccessors((Statement)object);
                for (Object object22 : ((Statement)object).getAllSuccessorEdges()) {
                    ((Statement)object).removeSuccessor((StatEdge)object22);
                    ((StatEdge)object22).setSource(ifStatement);
                    ifStatement.addSuccessor((StatEdge)object22);
                }
                object22 = ifStatement.getIfEdge();
                ifStatement.getFirst().removeSuccessor((StatEdge)object22);
                ((Statement)object).addSuccessor(new StatEdge(((StatEdge)object22).getType(), (Statement)object, ((StatEdge)object22).getDestination(), ((StatEdge)object22).closure));
                StatEdge statEdge = new StatEdge(1, ifStatement.getFirst(), (Statement)object);
                ifStatement.getFirst().addSuccessor(statEdge);
                ifStatement.setIfstat((Statement)object);
                ifStatement.getStats().addWithKey(object, ((Statement)object).id);
                ((Statement)object).setParent(ifStatement);
                ifStatement.getParent().getStats().removeWithKey(((Statement)object).id);
                IfExprent ifExprent = ifStatement.getHeadexprent();
                ifExprent.setCondition(new FunctionExprent(12, Arrays.asList(ifExprent.getCondition())));
                return true;
            }
        }
        return false;
    }

    private static boolean reorderIf(IfStatement ifStatement) {
        Object object;
        Object object2;
        if (ifStatement.iftype == IfStatement.IFTYPE_IFELSE) {
            return false;
        }
        int n = 0;
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = false;
        boolean bl4 = false;
        boolean bl5 = false;
        Statement statement = ifStatement.getParent();
        Statement statement2 = statement.type == 15 ? statement : ifStatement;
        Statement statement3 = IfHelper.getNextStatement(statement2);
        if (ifStatement.getIfstat() == null) {
            bl2 = true;
            n = ifStatement.getIfEdge().getType() == 32 ? 1 : MergeHelper.isDirectPath(statement2, ifStatement.getIfEdge().getDestination());
        } else {
            object2 = ifStatement.getIfstat().getAllSuccessorEdges();
            n = !object2.isEmpty() && ((StatEdge)object2.get(0)).getType() == 32 ? 1 : IfHelper.hasDirectEndEdge(ifStatement.getIfstat(), statement2);
        }
        object2 = statement.type == 15 ? (Statement)((SequenceStatement)statement).getStats().getLast() : ifStatement;
        bl3 = object2 == ifStatement;
        boolean bl6 = !((Statement)object2).getAllSuccessorEdges().isEmpty() && ((StatEdge)((Statement)object2).getAllSuccessorEdges().get(0)).getType() == 32 ? true : IfHelper.hasDirectEndEdge((Statement)object2, statement2);
        if (!bl3 && IfHelper.existsPath(ifStatement, ((StatEdge)ifStatement.getAllSuccessorEdges().get(0)).getDestination())) {
            return false;
        }
        if (n == 0 && !bl2) {
            bl4 = IfHelper.existsPath(ifStatement, statement3);
        }
        if (!bl6 && !bl3) {
            object2 = (SequenceStatement)statement;
            int n2 = ((Statement)object2).getStats().size() - 1;
            while (n2 >= 0) {
                object = (Statement)((Statement)object2).getStats().get(n2);
                if (object == ifStatement || (bl5 = IfHelper.existsPath((Statement)object, statement3))) break;
                --n2;
            }
        }
        if (!(n == 0 && !bl4 || !bl6 && !bl5 || bl2 || bl3)) {
            Statement statement4;
            object2 = (SequenceStatement)statement;
            ArrayList<Statement> arrayList = new ArrayList<Statement>();
            int n3 = ((Statement)object2).getStats().size() - 1;
            while (n3 >= 0) {
                Statement statement5 = (Statement)((Statement)object2).getStats().get(n3);
                if (statement5 == ifStatement) break;
                arrayList.add(0, statement5);
                --n3;
            }
            if (arrayList.size() == 1) {
                statement4 = (Statement)arrayList.get(0);
            } else {
                statement4 = new SequenceStatement(arrayList);
                statement4.setAllParent();
            }
            ifStatement.removeSuccessor((StatEdge)ifStatement.getAllSuccessorEdges().get(0));
            for (Statement statement6 : arrayList) {
                ((Statement)object2).getStats().removeWithKey(statement6.id);
            }
            StatEdge statEdge = new StatEdge(1, ifStatement.getFirst(), statement4);
            ifStatement.getFirst().addSuccessor(statEdge);
            ifStatement.setElsestat(statement4);
            ifStatement.setElseEdge(statEdge);
            ifStatement.getStats().addWithKey(statement4, statement4.id);
            statement4.setParent(ifStatement);
            ifStatement.iftype = IfStatement.IFTYPE_IFELSE;
        } else if (n != 0 && (!bl6 || bl2 && !bl3)) {
            object2 = ifStatement.getHeadexprent();
            ((IfExprent)object2).setCondition(new FunctionExprent(12, Arrays.asList(((IfExprent)object2).getCondition())));
            if (bl3) {
                StatEdge statEdge = ifStatement.getIfEdge();
                object = (StatEdge)ifStatement.getAllSuccessorEdges().get(0);
                if (bl2) {
                    ifStatement.getFirst().removeSuccessor(statEdge);
                    ifStatement.removeSuccessor((StatEdge)object);
                    statEdge.setSource(ifStatement);
                    ((StatEdge)object).setSource(ifStatement.getFirst());
                    ifStatement.addSuccessor(statEdge);
                    ifStatement.getFirst().addSuccessor((StatEdge)object);
                    ifStatement.setIfEdge((StatEdge)object);
                } else {
                    Statement statement7 = ifStatement.getIfstat();
                    SequenceStatement sequenceStatement = new SequenceStatement(Arrays.asList(ifStatement, statement7));
                    ifStatement.getFirst().removeSuccessor(statEdge);
                    ifStatement.getStats().removeWithKey(statement7.id);
                    ifStatement.setIfstat(null);
                    ifStatement.removeSuccessor((StatEdge)object);
                    ((StatEdge)object).setSource(ifStatement.getFirst());
                    ifStatement.getFirst().addSuccessor((StatEdge)object);
                    ifStatement.setIfEdge((StatEdge)object);
                    ifStatement.getParent().replaceStatement(ifStatement, sequenceStatement);
                    sequenceStatement.setAllParent();
                    ifStatement.addSuccessor(new StatEdge(1, (Statement)ifStatement, statement7));
                }
            } else {
                Object object3;
                Statement statement8;
                SequenceStatement sequenceStatement = (SequenceStatement)statement;
                object = new ArrayList();
                n = sequenceStatement.getStats().size() - 1;
                while (n >= 0) {
                    Statement statement9 = (Statement)sequenceStatement.getStats().get(n);
                    if (statement9 == ifStatement) break;
                    object.add(0, statement9);
                    --n;
                }
                if (object.size() == 1) {
                    statement8 = (Statement)object.get(0);
                } else {
                    statement8 = new SequenceStatement((List)object);
                    statement8.setAllParent();
                }
                ifStatement.removeSuccessor((StatEdge)ifStatement.getAllSuccessorEdges().get(0));
                Iterator iterator = object.iterator();
                while (iterator.hasNext()) {
                    Statement statement10 = (Statement)iterator.next();
                    sequenceStatement.getStats().removeWithKey(statement10.id);
                }
                if (bl2) {
                    object3 = ifStatement.getIfEdge();
                    ifStatement.getFirst().removeSuccessor((StatEdge)object3);
                    ((StatEdge)object3).setSource(ifStatement);
                    ifStatement.addSuccessor((StatEdge)object3);
                } else {
                    object3 = ifStatement.getIfstat();
                    ifStatement.getFirst().removeSuccessor(ifStatement.getIfEdge());
                    ifStatement.getStats().removeWithKey(((Statement)object3).id);
                    ifStatement.addSuccessor(new StatEdge(1, (Statement)ifStatement, (Statement)object3));
                    sequenceStatement.getStats().addWithKey(object3, ((Statement)object3).id);
                    ((Statement)object3).setParent(sequenceStatement);
                }
                object3 = new StatEdge(1, ifStatement.getFirst(), statement8);
                ifStatement.getFirst().addSuccessor((StatEdge)object3);
                ifStatement.setIfstat(statement8);
                ifStatement.setIfEdge((StatEdge)object3);
                ifStatement.getStats().addWithKey(statement8, statement8.id);
                statement8.setParent(ifStatement);
            }
        } else {
            return false;
        }
        return true;
    }

    private static boolean hasDirectEndEdge(Statement object, Statement statement) {
        for (Object object2 : ((Statement)object).getAllSuccessorEdges()) {
            if (!MergeHelper.isDirectPath(statement, ((StatEdge)object2).getDestination())) continue;
            return true;
        }
        if (((Statement)object).getExprents() == null) {
            Object object2;
            switch (((Statement)object).type) {
                case 15: {
                    return IfHelper.hasDirectEndEdge((Statement)((Statement)object).getStats().getLast(), statement);
                }
                case 7: 
                case 12: {
                    Iterator iterator = ((Statement)object).getStats().iterator();
                    while (iterator.hasNext()) {
                        object2 = null;
                        if (!IfHelper.hasDirectEndEdge((Statement)iterator.next(), statement)) continue;
                        return true;
                    }
                    break;
                }
                case 2: {
                    object2 = (IfStatement)object;
                    if (((IfStatement)object2).iftype != IfStatement.IFTYPE_IFELSE) break;
                    return IfHelper.hasDirectEndEdge(((IfStatement)object2).getIfstat(), statement) || IfHelper.hasDirectEndEdge(((IfStatement)object2).getElsestat(), statement);
                }
                case 10: {
                    return IfHelper.hasDirectEndEdge((Statement)((Statement)object).getStats().get(1), statement);
                }
                case 6: {
                    object = ((Statement)object).getStats().iterator();
                    while (object.hasNext()) {
                        if (!IfHelper.hasDirectEndEdge((Statement)object.next(), statement)) continue;
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    private static Statement getNextStatement(Statement statement) {
        Statement statement2 = statement.getParent();
        switch (statement2.type) {
            case 13: {
                return ((RootStatement)statement2).getDummyExit();
            }
            case 5: {
                return statement2;
            }
            case 15: {
                SequenceStatement sequenceStatement = (SequenceStatement)statement2;
                if (sequenceStatement.getStats().getLast() == statement) break;
                int n = sequenceStatement.getStats().size() - 1;
                while (n >= 0) {
                    if (sequenceStatement.getStats().get(n) == statement) {
                        return (Statement)sequenceStatement.getStats().get(n + 1);
                    }
                    --n;
                }
                break;
            }
        }
        return IfHelper.getNextStatement(statement2);
    }

    private static boolean existsPath(Statement statement, Statement object2) {
        for (Object object2 : ((Statement)object2).getAllPredecessorEdges()) {
            if (!statement.containsStatementStrict(((StatEdge)object2).getSource())) continue;
            return true;
        }
        return false;
    }
}

