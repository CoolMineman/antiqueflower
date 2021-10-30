/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.code.cfg.BasicBlock;
import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.LabelHelper;
import de.fernflower.modules.decompiler.SequenceHelper;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.IfExprent;
import de.fernflower.modules.decompiler.stats.BasicBlockStatement;
import de.fernflower.modules.decompiler.stats.DoStatement;
import de.fernflower.modules.decompiler.stats.IfStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.stats.SwitchStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class MergeHelper {
    public static void enhanceLoops(Statement statement) {
        while (MergeHelper.matchWhile(statement)) {
        }
        SequenceHelper.condenseSequences(statement);
    }

    /*
     * Unable to fully structure code
     */
    private static boolean matchWhile(Statement var0) {
        block18: {
            var1_1 = 0;
            for (Statement var2_3 : var0.getStats()) {
                if (var2_3.getExprents() != null) continue;
                var1_1 |= MergeHelper.matchWhile(var2_3);
            }
            if (var0.type != 5) break block18;
            v0 = var1_1;
            var0 = (DoStatement)var0;
            var1_1 = var0.getLooptype();
            switch (var1_1) {
                case 0: {
                    var2_3 = var0;
                    var3_2 = var2_3.getFirst();
                    while (var3_2.type == 15) {
                        var3_2 = var3_2.getFirst();
                    }
                    if (var3_2.type != 2 || !(var4_4 = (IfStatement)var3_2).getFirst().getExprents().isEmpty() || var4_4.iftype != 0) ** GOTO lbl-1000
                    if (var4_4.getIfstat() != null) ** GOTO lbl43
                    var5_5 = var4_4.getIfEdge();
                    if (!MergeHelper.isDirectPath(var2_3, var5_5.getDestination())) ** GOTO lbl-1000
                    var2_3.setLooptype(2);
                    var6_7 = (IfExprent)var4_4.getHeadexprent().copy();
                    var6_7.negateIf();
                    var2_3.setConditionExprent(var6_7.getCondition());
                    var4_4.getFirst().removeSuccessor(var5_5);
                    var4_4.removeSuccessor((StatEdge)var4_4.getAllSuccessorEdges().get(0));
                    if (var2_3.getAllSuccessorEdges().isEmpty()) {
                        var5_5.setSource(var2_3);
                        if (var5_5.closure == var2_3) {
                            var5_5.closure = var2_3.getParent();
                        }
                        var2_3.addSuccessor(var5_5);
                    }
                    if (var4_4 == var2_3.getFirst()) {
                        var3_2 = new BasicBlockStatement(new BasicBlock(DecompilerContext.getCountercontainer().getCounterAndIncrement(0)));
                        var3_2.setExprents(new ArrayList<E>());
                        var2_3.replaceStatement((Statement)var4_4, (Statement)var3_2);
                    } else {
                        var3_2 = var4_4.getParent();
                        var3_2.getStats().removeWithKey(var4_4.id);
                        var3_2.setFirst((Statement)var3_2.getStats().get(0));
                    }
                    v1 = true;
                    ** GOTO lbl71
lbl43:
                    // 1 sources

                    var5_6 = (StatEdge)var4_4.getAllSuccessorEdges().get(0);
                    if (MergeHelper.isDirectPath(var2_3, var5_6.getDestination())) {
                        var2_3.setLooptype(2);
                        var2_3.setConditionExprent(((IfExprent)var4_4.getHeadexprent().copy()).getCondition());
                        var6_8 = var4_4.getIfEdge();
                        var4_4.getFirst().removeSuccessor(var6_8);
                        var4_4.removeSuccessor(var5_6);
                        if (var2_3.getAllSuccessorEdges().isEmpty()) {
                            var5_6.setSource(var2_3);
                            if (var5_6.closure == var2_3) {
                                var5_6.closure = var2_3.getParent();
                            }
                            var2_3.addSuccessor(var5_6);
                        }
                        if (var4_4.getIfstat() == null) {
                            var3_2 = new BasicBlockStatement(new BasicBlock(DecompilerContext.getCountercontainer().getCounterAndIncrement(0)));
                            var3_2.setExprents(new ArrayList<E>());
                            var6_8.setSource((Statement)var3_2);
                            var3_2.addSuccessor(var6_8);
                            var2_3.replaceStatement((Statement)var4_4, (Statement)var3_2);
                        } else {
                            var3_2.getParent().replaceStatement((Statement)var3_2, var4_4.getIfstat());
                            for (Object var3_2 : var5_6.getDestination().getPredecessorEdges(4)) {
                                if (!var2_3.containsStatementStrict(var3_2.closure)) continue;
                                var2_3.addLabeledEdge((StatEdge)var3_2);
                            }
                            LabelHelper.lowClosures(var2_3);
                        }
                        v1 = true;
                    } else lbl-1000:
                    // 3 sources

                    {
                        v1 = false;
                    }
lbl71:
                    // 3 sources

                    if (v1) {
                        MergeHelper.matchFor((DoStatement)var0);
                        break;
                    }
                    MergeHelper.matchDoWhile((DoStatement)var0);
                    break;
                }
                case 2: {
                    MergeHelper.matchFor((DoStatement)var0);
                }
            }
            var1_1 = v0 | (var0.getLooptype() != var1_1 ? 1 : 0);
        }
        return (boolean)var1_1;
    }

    private static boolean matchDoWhile(DoStatement doStatement) {
        Object object = doStatement.getFirst();
        while (((Statement)object).type == 15) {
            object = (Statement)((Statement)object).getStats().getLast();
        }
        if (((Statement)object).type == 2) {
            IfStatement ifStatement = (IfStatement)object;
            if (ifStatement.iftype == 0 && ifStatement.getIfstat() == null) {
                StatEdge statEdge = ifStatement.getIfEdge();
                StatEdge statEdge2 = (StatEdge)ifStatement.getAllSuccessorEdges().get(0);
                if (statEdge.getType() == 4 && statEdge2.getType() == 8 && statEdge2.closure == doStatement && MergeHelper.isDirectPath(doStatement, statEdge.getDestination()) || statEdge.getType() == 8 && statEdge2.getType() == 4 && statEdge.closure == doStatement && MergeHelper.isDirectPath(doStatement, statEdge2.getDestination())) {
                    Set set = doStatement.getNeighboursSet(8, 0);
                    set.remove(object);
                    if (!set.isEmpty()) {
                        return false;
                    }
                    doStatement.setLooptype(1);
                    object = (IfExprent)ifStatement.getHeadexprent().copy();
                    if (statEdge.getType() == 4) {
                        ((IfExprent)object).negateIf();
                    }
                    doStatement.setConditionExprent(((IfExprent)object).getCondition());
                    ifStatement.getFirst().removeSuccessor(statEdge);
                    ifStatement.removeSuccessor(statEdge2);
                    if (ifStatement.getFirst().getExprents().isEmpty()) {
                        MergeHelper.removeLastEmptyStatement(doStatement, ifStatement);
                    } else {
                        ifStatement.setExprents(ifStatement.getFirst().getExprents());
                        object = new StatEdge(8, (Statement)ifStatement, doStatement);
                        ifStatement.addSuccessor((StatEdge)object);
                        doStatement.addLabeledEdge((StatEdge)object);
                    }
                    if (doStatement.getAllSuccessorEdges().isEmpty()) {
                        object = statEdge2.getType() == 8 ? statEdge : statEdge2;
                        ((StatEdge)object).setSource(doStatement);
                        if (((StatEdge)object).closure == doStatement) {
                            ((StatEdge)object).closure = doStatement.getParent();
                        }
                        doStatement.addSuccessor((StatEdge)object);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDirectPath(Statement statement, Statement statement2) {
        Object object = statement.getNeighboursSet(0x40000000, 1);
        if (object.isEmpty()) {
            object = statement.getParent();
            if (object == null) {
                return false;
            }
            switch (((Statement)object).type) {
                case 13: {
                    return statement2.type == 14;
                }
                case 5: {
                    return statement2 == object;
                }
                case 6: {
                    SwitchStatement switchStatement = (SwitchStatement)object;
                    int n = 0;
                    while (n < switchStatement.getCaseStatements().size() - 1) {
                        if ((Statement)switchStatement.getCaseStatements().get(n) == statement) {
                            statement = (Statement)switchStatement.getCaseStatements().get(n + 1);
                            if (statement.getExprents() != null && statement.getExprents().isEmpty()) {
                                statement = ((StatEdge)statement.getAllSuccessorEdges().get(0)).getDestination();
                            }
                            return statement2 == statement;
                        }
                        ++n;
                    }
                    break;
                }
            }
            return MergeHelper.isDirectPath((Statement)object, statement2);
        }
        return object.contains(statement2);
    }

    private static boolean matchFor(DoStatement doStatement) {
        Object object;
        Exprent exprent = null;
        Statement statement = null;
        Statement statement2 = null;
        statement = MergeHelper.getLastDirectData(doStatement.getFirst());
        if (statement == null || statement.getExprents().isEmpty()) {
            return false;
        }
        List list = statement.getExprents();
        exprent = (Exprent)list.get(list.size() - 1);
        boolean bl = false;
        if (list.size() == 1 && statement.getAllPredecessorEdges().size() > 1) {
            bl = true;
        }
        boolean bl2 = false;
        if (!(bl || exprent.type == 2 || exprent.type == 6)) {
            return false;
        }
        boolean bl3 = false;
        Object object2 = doStatement;
        while ((object = ((Statement)object2).getParent()) != null && ((Statement)object).type == 15) {
            if (object2 == ((Statement)object).getFirst()) {
                object2 = object;
                continue;
            }
            statement2 = MergeHelper.getLastDirectData((Statement)((Statement)object2).getNeighbours(1, 0).get(0));
            if (statement2 == null || statement2.getExprents().isEmpty() || ((Exprent)statement2.getExprents().get((int)(statement2.getExprents().size() - 1))).type != 2) break;
            bl3 = true;
            break;
        }
        if (bl3 || bl) {
            object = doStatement.getNeighboursSet(8, 0);
            object.remove(statement);
            if (!object.isEmpty()) {
                return false;
            }
            doStatement.setLooptype(3);
            if (bl3) {
                doStatement.setInitExprent((Exprent)statement2.getExprents().remove(statement2.getExprents().size() - 1));
            }
            doStatement.setIncExprent((Exprent)statement.getExprents().remove(statement.getExprents().size() - 1));
        }
        if (statement.getExprents().isEmpty()) {
            object = statement.getAllSuccessorEdges();
            if (!object.isEmpty()) {
                statement.removeSuccessor((StatEdge)object.get(0));
            }
            MergeHelper.removeLastEmptyStatement(doStatement, statement);
        }
        return true;
    }

    private static void removeLastEmptyStatement(DoStatement doStatement, Statement statement) {
        if (statement == doStatement.getFirst()) {
            BasicBlockStatement basicBlockStatement = new BasicBlockStatement(new BasicBlock(DecompilerContext.getCountercontainer().getCounterAndIncrement(0)));
            basicBlockStatement.setExprents(new ArrayList());
            doStatement.replaceStatement(statement, basicBlockStatement);
            return;
        }
        for (StatEdge statEdge : statement.getAllPredecessorEdges()) {
            statEdge.getSource().changeEdgeType(1, statEdge, 8);
            statement.removePredecessor(statEdge);
            statEdge.getSource().changeEdgeNode(1, statEdge, doStatement);
            doStatement.addPredecessor(statEdge);
            doStatement.addLabeledEdge(statEdge);
        }
        statement.getParent().getStats().removeWithKey(statement.id);
    }

    private static Statement getLastDirectData(Statement statement) {
        if (statement.getExprents() != null) {
            return statement;
        }
        switch (statement.type) {
            case 15: {
                int n = statement.getStats().size() - 1;
                while (n >= 0) {
                    Statement statement2 = MergeHelper.getLastDirectData((Statement)statement.getStats().get(n));
                    if (statement2 == null || !statement2.getExprents().isEmpty()) {
                        return statement2;
                    }
                    --n;
                }
                break;
            }
        }
        return null;
    }
}

