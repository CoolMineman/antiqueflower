/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.modules.decompiler.SequenceHelper;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.stats.IfStatement;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.SequenceStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.stats.SwitchStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class InlineSingleBlockHelper {
    public static boolean inlineSingleBlocks(RootStatement rootStatement) {
        boolean bl = InlineSingleBlockHelper.inlineSingleBlocksRec(rootStatement);
        if (bl) {
            SequenceHelper.condenseSequences(rootStatement);
        }
        return bl;
    }

    /*
     * Unable to fully structure code
     */
    private static boolean inlineSingleBlocksRec(Statement var0) {
        block21: {
            var1_1 = false;
            for (Statement var2_4 : var0.getStats()) {
                var1_1 |= InlineSingleBlockHelper.inlineSingleBlocksRec(var2_4);
            }
            if (var0.type != 15) break block21;
            var2_4 = (SequenceStatement)var0;
            var3_3 = 1;
            while (var3_3 < var2_4.getStats().size()) {
                block19: {
                    var4_5 = var3_3;
                    var0 = var2_4;
                    var5_6 = (Statement)var0.getStats().get(var4_5);
                    var6_7 = null;
                    if (((Statement)var0.getStats().get(var4_5 - 1)).hasBasicSuccEdge() || (var6_7 = var5_6.getPredecessorEdges(4)).size() != 1) ** GOTO lbl-1000
                    var6_7 = var7_8 = (StatEdge)var6_7.get(0);
                    var9_11 = var7_8.getSource();
                    var6_7 = var6_7.getDestination();
                    while (!(var10_15 = var9_11.getParent()).containsStatementStrict((Statement)var6_7)) {
                        if (var10_15.type == 7 || var10_15.type == 12) {
                            if (var10_15.getFirst() == var9_11) {
                                v0 = false;
                                break block19;
                            }
                        } else if (var10_15.type == 10 && var10_15.getStats().get(1) == var9_11) {
                            v0 = false;
                            break block19;
                        }
                        var9_11 = var10_15;
                    }
                    v0 = true;
                }
                if (v0) {
                    if (var7_8.explicit) {
                        v1 = true;
                    } else {
                        var8_9 = var4_5;
                        while (var8_9 < var0.getStats().size()) {
                            if (InlineSingleBlockHelper.noExitLabels((Statement)var0.getStats().get(var8_9), var0)) {
                                ++var8_9;
                                continue;
                            }
                            ** break block20
                        }
                        v1 = true;
                    }
                } else lbl-1000:
                // 3 sources

                {
                    v1 = false;
                }
                if (v1) {
                    var4_5 = var3_3;
                    var0 = var2_4;
                    var5_6 = (Statement)var0.getStats().get(var4_5);
                    v2 = (Statement)var0.getStats().get(var4_5 - 1);
                    v2.removeSuccessor((StatEdge)v2.getAllSuccessorEdges().get(0));
                    var6_7 = (StatEdge)var5_6.getPredecessorEdges(4).get(0);
                    var7_8 = var6_7.getSource();
                    var8_10 = var7_8.getParent();
                    var7_8.removeSuccessor((StatEdge)var6_7);
                    var6_7 = new ArrayList<Object>();
                    var9_12 = var0.getStats().size() - 1;
                    while (var9_12 >= var4_5) {
                        var6_7.add(0, (Statement)var0.getStats().remove(var9_12));
                        --var9_12;
                    }
                    if (var8_10.type == 2 && ((IfStatement)var8_10).iftype == 0 && var7_8 == var8_10.getFirst()) {
                        var9_13 = (IfStatement)var8_10;
                        var6_7 = new SequenceStatement((List)var6_7);
                        var6_7.setAllParent();
                        var10_15 = new StatEdge(1, (Statement)var7_8, (Statement)var6_7);
                        var7_8.addSuccessor((StatEdge)var10_15);
                        var9_13.setIfEdge((StatEdge)var10_15);
                        var9_13.setIfstat((Statement)var6_7);
                        var9_13.getStats().addWithKey(var6_7, var6_7.id);
                        var6_7.setParent(var9_13);
                    } else {
                        var6_7.add(0, var7_8);
                        var9_14 = new SequenceStatement((List)var6_7);
                        var9_14.setAllParent();
                        var8_10.replaceStatement((Statement)var7_8, var9_14);
                        for (Object var6_7 : var9_14.getPredecessorEdges(8)) {
                            var9_14.removePredecessor((StatEdge)var6_7);
                            var6_7.getSource().changeEdgeNode(1, (StatEdge)var6_7, (Statement)var7_8);
                            var7_8.addPredecessor((StatEdge)var6_7);
                            var7_8.addLabeledEdge((StatEdge)var6_7);
                        }
                        if (var8_10.type == 6) {
                            ((SwitchStatement)var8_10).sortEdgesAndNodes();
                        }
                        var7_8.addSuccessor(new StatEdge(1, (Statement)var7_8, var5_6));
                    }
                    return true;
                }
                ++var3_3;
            }
        }
        return var1_1;
    }

    private static boolean noExitLabels(Statement statement, Statement statement2) {
        for (StatEdge statEdge : statement.getAllSuccessorEdges()) {
            if (statEdge.getType() == 1 || statEdge.getDestination().type == 14 || statement2.containsStatementStrict(statEdge.getDestination())) continue;
            return false;
        }
        Iterator iterator = statement.getStats().iterator();
        while (iterator.hasNext()) {
            if (InlineSingleBlockHelper.noExitLabels((Statement)iterator.next(), statement2)) continue;
            return false;
        }
        return true;
    }

    public static boolean isBreakEdgeLabeled(Statement statement, Statement statement2) {
        if (statement2.type == 5 || statement2.type == 6) {
            if ((statement = statement.getParent()) == statement2) {
                return false;
            }
            return statement.type == 5 || statement.type == 6 || InlineSingleBlockHelper.isBreakEdgeLabeled(statement, statement2);
        }
        return true;
    }
}

