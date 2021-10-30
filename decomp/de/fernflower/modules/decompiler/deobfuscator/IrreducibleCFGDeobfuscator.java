/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.deobfuscator;

import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.deobfuscator.IrreducibleCFGDeobfuscator$1Node;
import de.fernflower.modules.decompiler.stats.BasicBlockStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import java.util.HashMap;
import java.util.Set;

public final class IrreducibleCFGDeobfuscator {
    public static boolean isStatementIrreducible(Statement object5) {
        HashMap<Integer, IrreducibleCFGDeobfuscator$1Node> hashMap = new HashMap<Integer, IrreducibleCFGDeobfuscator$1Node>();
        for (Object object2 : ((Statement)object5).getStats()) {
            if (!((Statement)object2).getSuccessorEdges(2).isEmpty()) {
                return false;
            }
            hashMap.put(((Statement)object2).id, new IrreducibleCFGDeobfuscator$1Node(((Statement)object2).id));
        }
        for (Object object2 : ((Statement)object5).getStats()) {
            object5 = (IrreducibleCFGDeobfuscator$1Node)hashMap.get(((Statement)object2).id);
            for (Object object3 : ((Statement)object2).getNeighbours(1, 1)) {
                object3 = (IrreducibleCFGDeobfuscator$1Node)hashMap.get(((Statement)object3).id);
                ((IrreducibleCFGDeobfuscator$1Node)object5).succs.add(object3);
                ((IrreducibleCFGDeobfuscator$1Node)object3).preds.add(object5);
            }
        }
        while (true) {
            int n = 0;
            Object object4 = null;
            for (Object object5 : hashMap.values()) {
                if (((IrreducibleCFGDeobfuscator$1Node)object5).succs.contains(object5)) {
                    n = 1;
                } else if (((IrreducibleCFGDeobfuscator$1Node)object5).preds.size() == 1) {
                    n = 2;
                }
                if (n == 0) continue;
                object4 = object5;
                break;
            }
            if (object4 == null) break;
            if (n == 1) {
                ((IrreducibleCFGDeobfuscator$1Node)object4).succs.remove(object4);
                ((IrreducibleCFGDeobfuscator$1Node)object4).preds.remove(object4);
                continue;
            }
            object5 = (IrreducibleCFGDeobfuscator$1Node)((IrreducibleCFGDeobfuscator$1Node)object4).preds.iterator().next();
            ((IrreducibleCFGDeobfuscator$1Node)object5).succs.addAll(((IrreducibleCFGDeobfuscator$1Node)object4).succs);
            ((IrreducibleCFGDeobfuscator$1Node)object5).succs.remove(object4);
            for (Object object3 : ((IrreducibleCFGDeobfuscator$1Node)object4).succs) {
                ((IrreducibleCFGDeobfuscator$1Node)object3).preds.remove(object4);
                ((IrreducibleCFGDeobfuscator$1Node)object3).preds.add(object5);
            }
            hashMap.remove(((IrreducibleCFGDeobfuscator$1Node)object4).id);
        }
        return hashMap.size() > 1;
    }

    public static boolean splitIrreducibleNode(Statement object4) {
        Object object22 = object4;
        Object object3 = null;
        int n = Integer.MAX_VALUE;
        int n2 = Integer.MAX_VALUE;
        for (Object object22 : ((Statement)object22).getStats()) {
            int n3;
            Set set = ((Statement)object22).getNeighboursSet(1, 0);
            if (set.size() <= 1 || (n3 = ((Statement)object22).getNeighboursSet(1, 1).size()) > n2) continue;
            int n4 = IrreducibleCFGDeobfuscator.getStatementSize((Statement)object22) * (set.size() - 1);
            if (n3 >= n2 && n4 >= n) continue;
            object3 = object22;
            n = n4;
            n2 = n3;
        }
        object22 = object3;
        if (object22 == null) {
            return false;
        }
        object3 = (StatEdge)((Statement)object22).getPredecessorEdges(1).iterator().next();
        Statement statement = IrreducibleCFGDeobfuscator.copyStatement((Statement)object22, null, new HashMap());
        IrreducibleCFGDeobfuscator.initCopiedStatement(statement);
        statement.setParent((Statement)object4);
        ((Statement)object4).getStats().addWithKey(statement, statement.id);
        for (Object object4 : ((Statement)object22).getPredecessorEdges(0x40000000)) {
            if (((StatEdge)object4).getSource() != ((StatEdge)object3).getSource() && ((StatEdge)object4).closure != ((StatEdge)object3).getSource()) continue;
            ((Statement)object22).removePredecessor((StatEdge)object4);
            ((StatEdge)object4).getSource().changeEdgeNode(1, (StatEdge)object4, statement);
            statement.addPredecessor((StatEdge)object4);
        }
        for (Object object4 : ((Statement)object22).getSuccessorEdges(0x40000000)) {
            statement.addSuccessor(new StatEdge(((StatEdge)object4).getType(), statement, ((StatEdge)object4).getDestination(), ((StatEdge)object4).closure));
        }
        return true;
    }

    private static int getStatementSize(Statement statement2) {
        int n = 0;
        if (statement2.type == 8) {
            n = ((BasicBlockStatement)statement2).getBlock().getSeq().length();
        } else {
            for (Statement statement2 : statement2.getStats()) {
                n += IrreducibleCFGDeobfuscator.getStatementSize(statement2);
            }
        }
        return n;
    }

    private static Statement copyStatement(Statement statement, Statement statement2, HashMap hashMap) {
        Object object2;
        Statement statement3;
        if (statement2 == null) {
            statement2 = statement.getSimpleCopy();
            hashMap.put(statement, statement2);
        }
        for (Statement statement4 : statement.getStats()) {
            statement3 = statement4.getSimpleCopy();
            statement2.getStats().addWithKey(statement3, statement3.id);
            hashMap.put(statement4, statement3);
        }
        int n = 0;
        while (n < statement.getStats().size()) {
            object2 = (Statement)statement.getStats().get(n);
            statement3 = (Statement)statement2.getStats().get(n);
            for (Object object2 : ((Statement)object2).getSuccessorEdges(0x40000000)) {
                object2 = new StatEdge(((StatEdge)object2).getType(), statement3, hashMap.containsKey(((StatEdge)object2).getDestination()) ? (Statement)hashMap.get(((StatEdge)object2).getDestination()) : ((StatEdge)object2).getDestination(), hashMap.containsKey(((StatEdge)object2).closure) ? (Statement)hashMap.get(((StatEdge)object2).closure) : ((StatEdge)object2).closure);
                statement3.addSuccessor((StatEdge)object2);
            }
            ++n;
        }
        n = 0;
        while (n < statement.getStats().size()) {
            object2 = (Statement)statement.getStats().get(n);
            statement3 = (Statement)statement2.getStats().get(n);
            IrreducibleCFGDeobfuscator.copyStatement((Statement)object2, statement3, hashMap);
            ++n;
        }
        return statement2;
    }

    private static void initCopiedStatement(Statement statement) {
        statement.initSimpleCopy();
        statement.setCopied();
        for (Statement statement2 : statement.getStats()) {
            statement2.setParent(statement);
            IrreducibleCFGDeobfuscator.initCopiedStatement(statement2);
        }
    }
}

