/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.modules.decompiler.SequenceHelper;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.stats.DoStatement;
import de.fernflower.modules.decompiler.stats.IfStatement;
import de.fernflower.modules.decompiler.stats.SequenceStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public final class LoopExtractHelper {
    public static boolean extractLoops(Statement statement) {
        boolean bl = LoopExtractHelper.extractLoopsRec(statement) != 0;
        if (bl) {
            SequenceHelper.condenseSequences(statement);
        }
        return bl;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static int extractLoopsRec(Statement statement) {
        Object object;
        boolean bl;
        boolean bl2 = false;
        block0: do {
            bl = false;
            object = statement.getStats().iterator();
            while (object.hasNext()) {
                Object var3_7 = null;
                int n = LoopExtractHelper.extractLoopsRec((Statement)object.next());
                bl2 |= n != 0;
                if (n != 2) continue;
                bl = true;
                continue block0;
            }
        } while (bl);
        if (statement.type == 5) {
            boolean bl3;
            block15: {
                if (((DoStatement)(statement = (DoStatement)statement)).getLooptype() != 0) {
                    bl3 = false;
                } else {
                    Statement statement2;
                    for (StatEdge statEdge : statement.getLabelEdges()) {
                        if (statEdge.getType() == 8 || statEdge.getDestination().type == 14) continue;
                        bl3 = false;
                        break block15;
                    }
                    Statement statement3 = statement;
                    Object object2 = statement3.getFirst();
                    while (((Statement)object2).type == 15) {
                        object2 = (Statement)((Statement)object2).getStats().getLast();
                    }
                    if (((Statement)object2).type == 2) {
                        object = (IfStatement)object2;
                        if (((IfStatement)object).iftype == 0 && ((IfStatement)object).getIfstat() != null) {
                            statement2 = ((IfStatement)object).getIfstat();
                            Object object3 = (StatEdge)((Statement)object).getAllSuccessorEdges().get(0);
                            if (((StatEdge)object3).getType() == 8 && ((StatEdge)object3).closure == statement3) {
                                object3 = statement3.getNeighboursSet(8, 0);
                                object3.remove(object2);
                                if (object3.isEmpty() && LoopExtractHelper.isExternStatement((DoStatement)statement3, statement2, statement2)) {
                                    LoopExtractHelper.extractIfBlock((DoStatement)statement3, (IfStatement)object);
                                    return 2;
                                }
                            }
                        }
                    }
                    boolean bl4 = false;
                    if (bl4) return 2;
                    statement3 = statement;
                    object2 = statement3.getFirst();
                    while (((Statement)object2).type == 15) {
                        object2 = ((Statement)object2).getFirst();
                    }
                    if (((Statement)object2).type != 2 || !((Statement)(object = (IfStatement)object2)).getFirst().getExprents().isEmpty() || ((IfStatement)object).iftype != 0 || ((IfStatement)object).getIfstat() == null || !LoopExtractHelper.isExternStatement((DoStatement)statement3, statement2 = ((IfStatement)object).getIfstat(), statement2)) {
                        bl3 = false;
                    } else {
                        LoopExtractHelper.extractIfBlock((DoStatement)statement3, (IfStatement)object);
                        return 2;
                    }
                }
            }
            if (bl3) {
                return 2;
            }
        }
        if (!bl2) return 0;
        return 1;
    }

    private static boolean isExternStatement(DoStatement doStatement, Statement statement, Statement statement2) {
        for (Object object : statement2.getAllSuccessorEdges()) {
            if (!doStatement.containsStatement(((StatEdge)object).getDestination()) || statement.containsStatement(((StatEdge)object).getDestination())) continue;
            return false;
        }
        for (Object object : statement2.getStats()) {
            if (LoopExtractHelper.isExternStatement(doStatement, statement, (Statement)object)) continue;
            return false;
        }
        return true;
    }

    private static void extractIfBlock(DoStatement doStatement, IfStatement statement) {
        Object object2 = ((IfStatement)statement).getIfstat();
        StatEdge statEdge = ((IfStatement)statement).getIfEdge();
        ((IfStatement)statement).setIfstat(null);
        statEdge.getSource().changeEdgeType(1, statEdge, 4);
        statEdge.closure = doStatement;
        statement.getStats().removeWithKey(((Statement)object2).id);
        doStatement.addLabeledEdge(statEdge);
        statement = new SequenceStatement(Arrays.asList(doStatement, object2));
        doStatement.getParent().replaceStatement(doStatement, statement);
        statement.setAllParent();
        doStatement.addSuccessor(new StatEdge(1, (Statement)doStatement, (Statement)object2));
        for (Object object2 : new ArrayList(statement.getLabelEdges())) {
            if (((StatEdge)object2).getType() != 8 && object2 != statEdge) continue;
            doStatement.addLabeledEdge((StatEdge)object2);
        }
        for (Object object2 : statement.getPredecessorEdges(8)) {
            if (!doStatement.containsStatementStrict(((StatEdge)object2).getSource())) continue;
            statement.removePredecessor((StatEdge)object2);
            ((StatEdge)object2).getSource().changeEdgeNode(1, (StatEdge)object2, doStatement);
            doStatement.addPredecessor((StatEdge)object2);
        }
    }
}

