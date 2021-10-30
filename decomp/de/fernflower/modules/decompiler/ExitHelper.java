/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.code.cfg.BasicBlock;
import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.DecHelper;
import de.fernflower.modules.decompiler.MergeHelper;
import de.fernflower.modules.decompiler.SequenceHelper;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.exps.ExitExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.stats.BasicBlockStatement;
import de.fernflower.modules.decompiler.stats.DoStatement;
import de.fernflower.modules.decompiler.stats.IfStatement;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.SequenceStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public final class ExitHelper {
    public static boolean condenseExits(RootStatement rootStatement) {
        int n = ExitHelper.integrateExits(rootStatement);
        if (n > 0) {
            ExitHelper.cleanUpUnreachableBlocks(rootStatement);
            SequenceHelper.condenseSequences(rootStatement);
        }
        return n > 0;
    }

    private static void cleanUpUnreachableBlocks(Statement statement) {
        boolean bl;
        block0: do {
            bl = false;
            int n = 0;
            while (n < statement.getStats().size()) {
                Statement statement2 = (Statement)statement.getStats().get(n);
                ExitHelper.cleanUpUnreachableBlocks(statement2);
                if (statement2.type == 15 && statement2.getStats().size() > 1) {
                    Statement statement3 = (Statement)statement2.getStats().getLast();
                    statement2 = (Statement)statement2.getStats().get(statement2.getStats().size() - 2);
                    if (!(statement3.getExprents() != null && statement3.getExprents().isEmpty() || statement2.hasBasicSuccEdge())) {
                        Set set = statement3.getNeighboursSet(0x40000000, 0);
                        set.remove(statement2);
                        if (set.isEmpty()) {
                            statement3.setExprents(new ArrayList());
                            bl = true;
                            continue block0;
                        }
                    }
                }
                ++n;
            }
        } while (bl);
    }

    private static int integrateExits(Statement statement) {
        Object object4;
        Iterator iterator;
        Object object2;
        int n = 0;
        Statement statement2 = null;
        if (statement.getExprents() == null) {
            int n2;
            block3: do {
                n2 = 0;
                object2 = statement.getStats().iterator();
                while (object2.hasNext()) {
                    iterator = null;
                    n2 = ExitHelper.integrateExits((Statement)object2.next());
                    if (n2 <= 0) continue;
                    n = 1;
                    continue block3;
                }
            } while (n2 != 0);
            switch (statement.type) {
                case 2: {
                    IfStatement ifStatement = (IfStatement)statement;
                    if (ifStatement.getIfstat() != null || (statement2 = ExitHelper.isExitEdge((StatEdge)((Object)(iterator = ifStatement.getIfEdge())))) == null) break;
                    object2 = new BasicBlockStatement(new BasicBlock(DecompilerContext.getCountercontainer().getCounterAndIncrement(0)));
                    ((Statement)object2).setExprents(DecHelper.copyExprentList(statement2.getExprents()));
                    ifStatement.getFirst().removeSuccessor((StatEdge)((Object)iterator));
                    StatEdge statEdge = new StatEdge(1, ifStatement.getFirst(), (Statement)object2);
                    ifStatement.getFirst().addSuccessor(statEdge);
                    ifStatement.setIfEdge(statEdge);
                    ifStatement.setIfstat((Statement)object2);
                    ifStatement.getStats().addWithKey(object2, ((BasicBlockStatement)object2).id);
                    ((Statement)object2).setParent(ifStatement);
                    iterator = (StatEdge)statement2.getAllSuccessorEdges().get(0);
                    statEdge = new StatEdge(4, (Statement)object2, ((StatEdge)((Object)iterator)).getDestination());
                    ((Statement)object2).addSuccessor(statEdge);
                    ((StatEdge)iterator).closure.addLabeledEdge(statEdge);
                    n = 1;
                }
            }
        }
        if (statement.getAllSuccessorEdges().size() == 1 && ((StatEdge)statement.getAllSuccessorEdges().get(0)).getType() == 4 && statement.getLabelEdges().isEmpty() && (statement != ((Statement)(object4 = statement.getParent())).getFirst() || ((Statement)object4).type != 2 && ((Statement)object4).type != 6) && (statement2 = ExitHelper.isExitEdge((StatEdge)((Object)(iterator = (StatEdge)statement.getAllSuccessorEdges().get(0))))) != null) {
            statement.removeSuccessor((StatEdge)((Object)iterator));
            object2 = new BasicBlockStatement(new BasicBlock(DecompilerContext.getCountercontainer().getCounterAndIncrement(0)));
            ((Statement)object2).setExprents(DecHelper.copyExprentList(statement2.getExprents()));
            Object object3 = (StatEdge)statement2.getAllSuccessorEdges().get(0);
            iterator = new StatEdge(4, (Statement)object2, ((StatEdge)object3).getDestination());
            ((Statement)object2).addSuccessor((StatEdge)((Object)iterator));
            ((StatEdge)object3).closure.addLabeledEdge((StatEdge)((Object)iterator));
            object3 = new SequenceStatement(Arrays.asList(statement, object2));
            ((Statement)object3).setAllParent();
            ((Statement)object4).replaceStatement(statement, (Statement)object3);
            for (Object object4 : ((Statement)object3).getPredecessorEdges(8)) {
                ((Statement)object3).removePredecessor((StatEdge)object4);
                ((StatEdge)object4).getSource().changeEdgeNode(1, (StatEdge)object4, statement);
                statement.addPredecessor((StatEdge)object4);
                statement.addLabeledEdge((StatEdge)object4);
            }
            statement.addSuccessor(new StatEdge(1, statement, (Statement)object2));
            for (Object object4 : statement2.getAllPredecessorEdges()) {
                if (((StatEdge)object4).explicit || !statement.containsStatementStrict(((StatEdge)object4).getSource()) || !MergeHelper.isDirectPath(((StatEdge)object4).getSource().getParent(), (Statement)object2)) continue;
                statement2.removePredecessor((StatEdge)object4);
                ((StatEdge)object4).getSource().changeEdgeNode(1, (StatEdge)object4, (Statement)object2);
                ((Statement)object2).addPredecessor((StatEdge)object4);
                if (statement.containsStatementStrict(((StatEdge)object4).closure)) continue;
                statement.addLabeledEdge((StatEdge)object4);
            }
            n = 2;
        }
        return n;
    }

    private static Statement isExitEdge(StatEdge object) {
        block8: {
            Statement statement;
            block9: {
                boolean bl;
                block7: {
                    statement = ((StatEdge)object).getDestination();
                    if (((StatEdge)object).getType() != 4 || statement.type != 8 || !((StatEdge)object).explicit) break block8;
                    if (((StatEdge)object).labeled) break block9;
                    Object object22 = null;
                    for (Object object22 : ((StatEdge)object).getDestination().getAllPredecessorEdges()) {
                        if (object22 == object) continue;
                        if (((StatEdge)object22).getType() == 1) {
                            object22 = ((StatEdge)object22).getSource();
                            if (((Statement)object22).type != 8 && (((Statement)object22).type != 2 || ((IfStatement)object22).iftype != 0) && (((Statement)object22).type != 5 || ((DoStatement)object22).getLooptype() == 0)) continue;
                            bl = false;
                        } else {
                            bl = false;
                        }
                        break block7;
                    }
                    bl = true;
                }
                if (!bl) break block8;
            }
            if ((object = statement.getExprents()).size() == 1 && ((Exprent)object.get((int)0)).type == 4) {
                return statement;
            }
        }
        return null;
    }

    public static boolean removeRedundantReturns(RootStatement object2) {
        boolean bl = false;
        for (Object object2 : ((RootStatement)object2).getDummyExit().getAllPredecessorEdges()) {
            if (((StatEdge)object2).explicit || ((Statement)(object2 = ((StatEdge)object2).getSource())).getExprents() == null || ((Statement)object2).getExprents().isEmpty()) continue;
            object2 = ((Statement)object2).getExprents();
            Exprent exprent = (Exprent)object2.get(object2.size() - 1);
            if (exprent.type != 4 || ((ExitExprent)(exprent = (ExitExprent)exprent)).getExittype() != 0 || ((ExitExprent)exprent).getValue() != null) continue;
            object2.remove(object2.size() - 1);
            bl = true;
        }
        return bl;
    }
}

