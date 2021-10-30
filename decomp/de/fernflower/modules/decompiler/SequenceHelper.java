/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.code.cfg.BasicBlock;
import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.DecHelper;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.stats.BasicBlockStatement;
import de.fernflower.modules.decompiler.stats.SequenceStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public final class SequenceHelper {
    public static void condenseSequences(Statement statement) {
        SequenceHelper.condenseSequencesRec(statement);
    }

    private static void condenseSequencesRec(Statement object) {
        Object object2;
        boolean bl;
        Object object32;
        if (((Statement)object).type == 15) {
            object32 = new ArrayList();
            object32.addAll(((Statement)object).getStats());
            bl = false;
            int n = 0;
            while (n < object32.size()) {
                object2 = (Statement)object32.get(n);
                if (((Statement)object2).type == 15) {
                    SequenceHelper.removeEmptyStatements((SequenceStatement)object2);
                    if (n == object32.size() - 1 || SequenceHelper.isSequenceDisbandable((Statement)object2, (Statement)object32.get(n + 1))) {
                        Object object42;
                        Iterator iterator = ((Statement)object2).getFirst();
                        for (Object object42 : ((Statement)object2).getAllPredecessorEdges()) {
                            ((Statement)object2).removePredecessor((StatEdge)object42);
                            ((StatEdge)object42).getSource().changeEdgeNode(1, (StatEdge)object42, (Statement)((Object)iterator));
                            ((Statement)((Object)iterator)).addPredecessor((StatEdge)object42);
                        }
                        object42 = (Statement)((Statement)object2).getStats().getLast();
                        if (((Statement)object42).getAllSuccessorEdges().isEmpty() && n < object32.size() - 1) {
                            ((Statement)object42).addSuccessor(new StatEdge(1, (Statement)object42, (Statement)object32.get(n + 1)));
                        } else {
                            for (Object object5 : ((Statement)object42).getAllSuccessorEdges()) {
                                if (n == object32.size() - 1) {
                                    if (((StatEdge)object5).closure != object2) continue;
                                    ((Statement)object).addLabeledEdge((StatEdge)object5);
                                    continue;
                                }
                                ((StatEdge)object5).getSource().changeEdgeType(1, (StatEdge)object5, 1);
                                ((StatEdge)object5).closure.getLabelEdges().remove(object5);
                                ((StatEdge)object5).closure = null;
                            }
                        }
                        for (Object object5 : ((Statement)object2).getAllSuccessorEdges()) {
                            ((Statement)object2).removeSuccessor((StatEdge)object5);
                        }
                        for (Object object5 : new HashSet(((Statement)object2).getLabelEdges())) {
                            if (((StatEdge)object5).getSource() == object42) continue;
                            ((Statement)object42).addLabeledEdge((StatEdge)object5);
                        }
                        object32.remove(n);
                        object32.addAll(n, ((Statement)object2).getStats());
                        --n;
                        bl = true;
                    }
                }
                ++n;
            }
            if (bl) {
                SequenceStatement sequenceStatement = new SequenceStatement((List)object32);
                sequenceStatement.setAllParent();
                ((Statement)object).getParent().replaceStatement((Statement)object, sequenceStatement);
                object = sequenceStatement;
            }
        }
        if (((Statement)object).type == 15) {
            SequenceHelper.removeEmptyStatements((SequenceStatement)object);
            if (((Statement)object).getStats().size() == 1) {
                object32 = ((Statement)object).getFirst();
                bl = ((Statement)object32).getAllSuccessorEdges().isEmpty();
                if (!bl) {
                    StatEdge statEdge = (StatEdge)((Statement)object32).getAllSuccessorEdges().get(0);
                    bl = ((Statement)object).getAllSuccessorEdges().isEmpty();
                    if (!bl) {
                        object2 = (StatEdge)((Statement)object).getAllSuccessorEdges().get(0);
                        bl = statEdge.getDestination() == ((StatEdge)object2).getDestination();
                        if (bl) {
                            ((Statement)object32).removeSuccessor(statEdge);
                        }
                    }
                }
                if (bl) {
                    ((Statement)object).getParent().replaceStatement((Statement)object, (Statement)object32);
                    object = object32;
                }
            }
        }
        block5: while (true) {
            for (Object object32 : ((Statement)object).getStats()) {
                if (!((Statement)object32).getStats().isEmpty() && ((Statement)object32).getExprents() == null || ((Statement)object32).type == 8) continue;
                SequenceHelper.destroyAndFlattenStatement((Statement)object32);
                continue block5;
            }
            break;
        }
        int n = 0;
        while (n < ((Statement)object).getStats().size()) {
            SequenceHelper.condenseSequencesRec((Statement)((Statement)object).getStats().get(n));
            ++n;
        }
    }

    private static boolean isSequenceDisbandable(Statement statement, Statement object2) {
        List list = (statement = (Statement)statement.getStats().getLast()).getAllSuccessorEdges();
        if (!list.isEmpty() && ((StatEdge)list.get(0)).getDestination() != object2) {
            return false;
        }
        for (Object object2 : ((Statement)object2).getPredecessorEdges(4)) {
            if (statement == ((StatEdge)object2).getSource() || statement.containsStatementStrict(((StatEdge)object2).getSource())) continue;
            return false;
        }
        return true;
    }

    private static void removeEmptyStatements(SequenceStatement sequenceStatement) {
        boolean bl;
        boolean bl2;
        if (sequenceStatement.getStats().size() <= 1) {
            return;
        }
        SequenceStatement sequenceStatement2 = sequenceStatement;
        do {
            Object object = null;
            Object object2 = null;
            bl2 = false;
            int n = sequenceStatement2.getStats().size() - 1;
            while (n >= 0) {
                object = object2;
                object2 = (Statement)sequenceStatement2.getStats().get(n);
                if (object != null && ((Statement)object2).getExprents() != null && !((Statement)object2).getExprents().isEmpty()) {
                    if (((Statement)object).getExprents() != null) {
                        ((Statement)object).getExprents().addAll(0, ((Statement)object2).getExprents());
                        ((Statement)object2).getExprents().clear();
                        bl2 = true;
                    } else if ((object = SequenceHelper.getFirstExprentlist((Statement)object)) != null) {
                        ((Statement)object).getExprents().addAll(0, ((Statement)object2).getExprents());
                        ((Statement)object2).getExprents().clear();
                        bl2 = true;
                    }
                }
                --n;
            }
        } while (bl2);
        block2: do {
            bl = false;
            for (Object object : sequenceStatement.getStats()) {
                StatEdge statEdge;
                if (((Statement)object).getExprents() == null || !((Statement)object).getExprents().isEmpty()) continue;
                if (((Statement)object).getAllSuccessorEdges().isEmpty()) {
                    statEdge = null;
                    if (((Statement)object).getPredecessorEdges(4).isEmpty()) {
                        for (StatEdge statEdge2 : ((Statement)object).getAllPredecessorEdges()) {
                            statEdge2.getSource().removeSuccessor(statEdge2);
                        }
                        bl = true;
                    }
                } else {
                    statEdge = (StatEdge)((Statement)object).getAllSuccessorEdges().get(0);
                    if (statEdge.getType() != 32) {
                        ((Statement)object).removeSuccessor(statEdge);
                        for (StatEdge statEdge3 : ((Statement)object).getAllPredecessorEdges()) {
                            if (statEdge.getType() != 1) {
                                statEdge3.getSource().changeEdgeType(1, statEdge3, statEdge.getType());
                            }
                            ((Statement)object).removePredecessor(statEdge3);
                            statEdge3.getSource().changeEdgeNode(1, statEdge3, statEdge.getDestination());
                            statEdge.getDestination().addPredecessor(statEdge3);
                            if (statEdge.closure == null) continue;
                            statEdge.closure.addLabeledEdge(statEdge3);
                        }
                        bl = true;
                    }
                }
                if (!bl) continue;
                sequenceStatement.getStats().removeWithKey(((Statement)object).id);
                continue block2;
            }
        } while (bl);
        sequenceStatement.setFirst((Statement)sequenceStatement.getStats().get(0));
    }

    private static Statement getFirstExprentlist(Statement statement) {
        if (statement.getExprents() != null) {
            return statement;
        }
        switch (statement.type) {
            case 2: 
            case 6: 
            case 10: 
            case 15: {
                return SequenceHelper.getFirstExprentlist(statement.getFirst());
            }
        }
        return null;
    }

    public static void destroyAndFlattenStatement(Statement statement) {
        SequenceHelper.destroyStatementContent(statement, false);
        BasicBlockStatement basicBlockStatement = new BasicBlockStatement(new BasicBlock(DecompilerContext.getCountercontainer().getCounterAndIncrement(0)));
        if (statement.getExprents() == null) {
            basicBlockStatement.setExprents(new ArrayList());
        } else {
            basicBlockStatement.setExprents(DecHelper.copyExprentList(statement.getExprents()));
        }
        statement.getParent().replaceStatement(statement, basicBlockStatement);
    }

    public static void destroyStatementContent(Statement statement, boolean bl) {
        Iterator iterator = statement.getStats().iterator();
        while (iterator.hasNext()) {
            StatEdge statEdge = null;
            SequenceHelper.destroyStatementContent((Statement)iterator.next(), true);
        }
        statement.getStats().clear();
        if (bl) {
            for (StatEdge statEdge : statement.getAllSuccessorEdges()) {
                statement.removeSuccessor(statEdge);
            }
        }
    }
}

