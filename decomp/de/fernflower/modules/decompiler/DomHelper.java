/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.code.cfg.BasicBlock;
import de.fernflower.code.cfg.ControlFlowGraph;
import de.fernflower.code.cfg.ExceptionRangeCFG;
import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.DomHelper$1;
import de.fernflower.modules.decompiler.LabelHelper;
import de.fernflower.modules.decompiler.SequenceHelper;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.StrongConnectivityHelper;
import de.fernflower.modules.decompiler.decompose.FastExtendedPostdominanceHelper;
import de.fernflower.modules.decompiler.deobfuscator.IrreducibleCFGDeobfuscator;
import de.fernflower.modules.decompiler.stats.BasicBlockStatement;
import de.fernflower.modules.decompiler.stats.CatchAllStatement;
import de.fernflower.modules.decompiler.stats.CatchStatement;
import de.fernflower.modules.decompiler.stats.DoStatement;
import de.fernflower.modules.decompiler.stats.GeneralStatement;
import de.fernflower.modules.decompiler.stats.IfStatement;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.SequenceStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.stats.SwitchStatement;
import de.fernflower.modules.decompiler.stats.SynchronizedStatement;
import de.fernflower.util.FastFixedSetFactory;
import de.fernflower.util.FastFixedSetFactory$FastFixedSet;
import de.fernflower.util.InterpreterUtil;
import de.fernflower.util.VBStyleCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class DomHelper {
    private static VBStyleCollection calcPostDominators(Statement statement) {
        Object object;
        Object object2;
        Object object3;
        HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
        Object object42 = null;
        object42 = new StrongConnectivityHelper(statement).getComponents();
        List list = statement.getPostReversePostOrderList(StrongConnectivityHelper.getExitReps((List)object42));
        FastFixedSetFactory fastFixedSetFactory = new FastFixedSetFactory(list);
        FastFixedSetFactory$FastFixedSet fastFixedSetFactory$FastFixedSet = fastFixedSetFactory.spawnEmptySet();
        fastFixedSetFactory$FastFixedSet.setAllElements();
        Iterable iterable = fastFixedSetFactory.spawnEmptySet();
        ((FastFixedSetFactory$FastFixedSet)iterable).setAllElements();
        Object object5 = object42.iterator();
        while (object5.hasNext()) {
            object42 = (List)object5.next();
            if (StrongConnectivityHelper.isExitComponent((List)object42)) {
                object3 = fastFixedSetFactory.spawnEmptySet();
                ((FastFixedSetFactory$FastFixedSet)object3).addAll((Collection)object42);
            } else {
                object3 = ((FastFixedSetFactory$FastFixedSet)iterable).getCopy();
            }
            object2 = object42.iterator();
            while (object2.hasNext()) {
                object = (Statement)object2.next();
                hashMap.put(object, object3);
            }
        }
        do {
            for (Object object42 : list) {
                if (!fastFixedSetFactory$FastFixedSet.contains(object42)) continue;
                fastFixedSetFactory$FastFixedSet.remove(object42);
                object3 = (FastFixedSetFactory$FastFixedSet)hashMap.get(object42);
                object = fastFixedSetFactory.spawnEmptySet();
                object2 = ((Statement)object42).getNeighbours(1, 1);
                int n = 0;
                while (n < object2.size()) {
                    Statement statement2 = (Statement)object2.get(n);
                    Iterator iterator = (FastFixedSetFactory$FastFixedSet)hashMap.get(statement2);
                    if (n == 0) {
                        ((FastFixedSetFactory$FastFixedSet)object).union((FastFixedSetFactory$FastFixedSet)((Object)iterator));
                    } else {
                        ((FastFixedSetFactory$FastFixedSet)object).intersection((FastFixedSetFactory$FastFixedSet)((Object)iterator));
                    }
                    ++n;
                }
                if (!((FastFixedSetFactory$FastFixedSet)object).contains(object42)) {
                    ((FastFixedSetFactory$FastFixedSet)object).add(object42);
                }
                if (InterpreterUtil.equalObjects(object, object3)) continue;
                hashMap.put(object42, object);
                Object var6_8 = null;
                for (Statement statement2 : ((Statement)object42).getNeighbours(1, 0)) {
                    fastFixedSetFactory$FastFixedSet.add(statement2);
                }
            }
        } while (!fastFixedSetFactory$FastFixedSet.isEmpty());
        object42 = new VBStyleCollection();
        object5 = statement.getReversePostOrderList();
        object3 = new HashMap();
        int n = 0;
        while (n < object5.size()) {
            ((HashMap)object3).put(((Statement)object5.get((int)n)).id, n);
            ++n;
        }
        for (Statement statement3 : list) {
            iterable = new ArrayList();
            for (Statement statement2 : (FastFixedSetFactory$FastFixedSet)hashMap.get(statement3)) {
                iterable.add(statement2.id);
            }
            Collections.sort(iterable, new DomHelper$1((HashMap)object3));
            if (iterable.size() > 1 && ((Integer)iterable.get(0)).intValue() == statement3.id.intValue()) {
                iterable.add((Integer)iterable.remove(0));
            }
            ((VBStyleCollection)object42).addWithKey(iterable, statement3.id);
        }
        return object42;
    }

    public static RootStatement graphToStatement(ControlFlowGraph object) {
        Object object2;
        Object object3;
        VBStyleCollection vBStyleCollection = new VBStyleCollection();
        Object object4 = ((ControlFlowGraph)object).getBlocks();
        Object object5 = ((ArrayList)object4).iterator();
        while (object5.hasNext()) {
            object3 = (BasicBlock)object5.next();
            vBStyleCollection.addWithKey(new BasicBlockStatement((BasicBlock)object3), ((BasicBlock)object3).id);
        }
        object3 = (Statement)vBStyleCollection.getWithKey(object.getFirst().id);
        object5 = new Statement();
        new Statement().type = 14;
        if (vBStyleCollection.size() <= 1) {
            object4 = new RootStatement((Statement)object3, (Statement)object5);
            ((Statement)object3).addSuccessor(new StatEdge(4, (Statement)object3, (Statement)object5, (Statement)object4));
            object2 = object4;
        } else {
            GeneralStatement generalStatement = new GeneralStatement((Statement)object3, vBStyleCollection, null);
            Iterator iterator = ((ArrayList)object4).iterator();
            while (iterator.hasNext()) {
                Object object6;
                object4 = (BasicBlock)iterator.next();
                Statement statement = (Statement)vBStyleCollection.getWithKey(((BasicBlock)object4).id);
                for (BasicBlock basicBlock : ((BasicBlock)object4).getSuccs()) {
                    int n;
                    object6 = (Statement)vBStyleCollection.getWithKey(basicBlock.id);
                    if (object6 == object3) {
                        n = 8;
                    } else if (((ControlFlowGraph)object).getFinallyExits().contains(object4)) {
                        n = 32;
                        object6 = object5;
                    } else if (basicBlock.id == object.getLast().id) {
                        n = 4;
                        object6 = object5;
                    } else {
                        n = 1;
                    }
                    statement.addSuccessor(new StatEdge(n, statement, (Statement)(n == 8 ? generalStatement : object6), n == 1 ? null : generalStatement));
                }
                for (Object object7 : ((BasicBlock)object4).getSuccExceptions()) {
                    object6 = (Statement)vBStyleCollection.getWithKey(((BasicBlock)object7).id);
                    if (((ExceptionRangeCFG)(object7 = ((ControlFlowGraph)object).getExceptionRange((BasicBlock)object7, (BasicBlock)object4))).isCircular()) continue;
                    statement.addSuccessor(new StatEdge(statement, (Statement)object6, ((ExceptionRangeCFG)object7).getExceptionType()));
                }
            }
            generalStatement.buildContinueSet();
            generalStatement.buildMonitorFlags();
            object2 = object = new RootStatement(generalStatement, (Statement)object5);
        }
        if (!DomHelper.processStatement((Statement)object2, new HashMap())) {
            DecompilerContext.getLogger().writeMessage("parsing failure!", 4);
            throw new RuntimeException("parsing failure!");
        }
        LabelHelper.lowContinueLabels((Statement)object, new HashSet());
        SequenceHelper.condenseSequences((Statement)object);
        ((Statement)object).buildMonitorFlags();
        DomHelper.buildSynchronized((Statement)object);
        return object;
    }

    public static void removeSynchronizedHandler(Statement statement) {
        Iterator iterator = statement.getStats().iterator();
        while (iterator.hasNext()) {
            DomHelper.removeSynchronizedHandler((Statement)iterator.next());
        }
        if (statement.type == 10) {
            ((SynchronizedStatement)statement).removeExc();
        }
    }

    private static void buildSynchronized(Statement statement) {
        Object object2 = statement.getStats().iterator();
        while (object2.hasNext()) {
            Object var1_2 = null;
            DomHelper.buildSynchronized((Statement)object2.next());
        }
        if (statement.type == 15) {
            boolean bl;
            block1: do {
                bl = false;
                object2 = statement.getStats();
                int n = 0;
                while (n < object2.size() - 1) {
                    Statement statement2 = (Statement)object2.get(n);
                    if (statement2.isMonitorEnter()) {
                        Statement statement3;
                        Statement statement4 = statement3 = (Statement)object2.get(n + 1);
                        while (statement3.type == 15) {
                            statement3 = statement3.getFirst();
                        }
                        if (statement3.type == 12 && (statement3 = (CatchAllStatement)statement3).getFirst().isContainsMonitorExit() && ((CatchAllStatement)statement3).getHandler().isContainsMonitorExit()) {
                            statement2.removeSuccessor((StatEdge)statement2.getSuccessorEdges(0x40000000).get(0));
                            for (StatEdge statEdge : statement2.getPredecessorEdges(0x40000000)) {
                                statement2.removePredecessor(statEdge);
                                statEdge.getSource().changeEdgeNode(1, statEdge, statement4);
                                statement4.addPredecessor(statEdge);
                            }
                            statement.getStats().removeWithKey(statement2.id);
                            statement.setFirst((Statement)statement.getStats().get(0));
                            SynchronizedStatement synchronizedStatement = new SynchronizedStatement(statement2, statement3.getFirst(), ((CatchAllStatement)statement3).getHandler());
                            synchronizedStatement.setAllParent();
                            for (Object object2 : new HashSet(statement3.getLabelEdges())) {
                                synchronizedStatement.addLabeledEdge((StatEdge)object2);
                            }
                            statement2.addSuccessor(new StatEdge(1, statement2, statement3.getFirst()));
                            statement3.getParent().replaceStatement(statement3, synchronizedStatement);
                            bl = true;
                            continue block1;
                        }
                    }
                    ++n;
                }
            } while (bl);
        }
    }

    private static boolean processStatement(Statement statement, HashMap hashMap) {
        if (statement.type == 13) {
            Statement statement2 = statement.getFirst();
            if (statement2.type != 0) {
                return true;
            }
            boolean bl = DomHelper.processStatement(statement2, hashMap);
            if (bl) {
                statement.replaceStatement(statement2, statement2.getFirst());
            }
            return bl;
        }
        boolean bl = hashMap.isEmpty();
        int n = 0;
        while (n < 2) {
            int n2 = 0;
            while (n2 < 5) {
                if (n2 > 0) {
                    if (IrreducibleCFGDeobfuscator.isStatementIrreducible(statement)) {
                        if (!IrreducibleCFGDeobfuscator.splitIrreducibleNode(statement)) {
                            DecompilerContext.getLogger().writeMessage("Irreducible statement cannot be decomposed!", 4);
                            break;
                        }
                    } else {
                        if (n != 2 && !bl) break;
                        DecompilerContext.getLogger().writeMessage("Statement cannot be decomposed although reducible!", 4);
                        break;
                    }
                    hashMap = new HashMap();
                    bl = true;
                }
                int n3 = 0;
                while (n3 < 2) {
                    boolean bl2;
                    boolean bl3 = bl2 = n3 != 0;
                    while (true) {
                        if (DomHelper.findSimpleStatements(statement, hashMap)) {
                            n2 = 0;
                        }
                        if (statement.type == 11) {
                            return true;
                        }
                        Statement statement3 = DomHelper.findGeneralStatement(statement, bl2, hashMap);
                        if (statement3 == null) break;
                        boolean bl4 = false;
                        if (!DomHelper.processStatement(statement3, statement.getFirst() == statement3 ? hashMap : new HashMap())) {
                            return false;
                        }
                        statement.replaceStatement(statement3, statement3.getFirst());
                        hashMap = new HashMap();
                        bl = true;
                        n2 = 0;
                    }
                    ++n3;
                }
                ++n2;
            }
            if (bl) break;
            hashMap = new HashMap();
            ++n;
        }
        return false;
    }

    private static Statement findGeneralStatement(Statement statement, boolean bl, HashMap hashMap) {
        Collection<Object> collection;
        Object object;
        Object object2;
        Object object322;
        VBStyleCollection vBStyleCollection;
        FastExtendedPostdominanceHelper fastExtendedPostdominanceHelper;
        VBStyleCollection vBStyleCollection2 = statement.getStats();
        if (hashMap.isEmpty()) {
            fastExtendedPostdominanceHelper = new FastExtendedPostdominanceHelper();
            hashMap.putAll(fastExtendedPostdominanceHelper.getExtendedPostdominators(statement));
        }
        if (bl) {
            vBStyleCollection = new VBStyleCollection();
            fastExtendedPostdominanceHelper = null;
            for (Object object322 : statement.getPostReversePostOrderList(null)) {
                object2 = (Set)hashMap.get(((Statement)object322).id);
                if (object2 == null) continue;
                vBStyleCollection.addWithKey(new ArrayList(object2), ((Statement)object322).id);
            }
            object322 = (Set)hashMap.get(statement.getFirst().id);
            if (object322 != null) {
                object2 = object322.iterator();
                while (object2.hasNext()) {
                    object = (Integer)object2.next();
                    collection = (List)vBStyleCollection.getWithKey(object);
                    if (collection == null) {
                        collection = new ArrayList();
                        vBStyleCollection.addWithKey(collection, object);
                    }
                    collection.add(object);
                }
            }
        } else {
            vBStyleCollection = DomHelper.calcPostDominators(statement);
        }
        int n = 0;
        while (n < vBStyleCollection.size()) {
            object322 = (Integer)vBStyleCollection.getKey(n);
            object = (List)vBStyleCollection.get(n);
            if (hashMap.containsKey(object322) || object.size() == 1 && ((Integer)object.get(0)).equals(object322)) {
                object2 = (Statement)vBStyleCollection2.getWithKey(object322);
                collection = (Set)hashMap.get(object322);
                int n2 = 0;
                while (n2 < object.size()) {
                    Object object4 = (Integer)object.get(n2);
                    if ((((Integer)object4).equals(object322) || collection.contains(object4)) && (object4 = (Statement)vBStyleCollection2.getWithKey(object4)) != null) {
                        boolean bl2;
                        boolean bl3 = object4 == object2;
                        HashSet<Statement> hashSet = new HashSet<Statement>();
                        HashSet hashSet2 = new HashSet();
                        HashSet<Object> hashSet3 = new HashSet<Object>();
                        hashSet3.add(object2);
                        block4: do {
                            bl2 = false;
                            for (Statement statement2 : hashSet3) {
                                if (hashSet.contains(statement2)) continue;
                                boolean bl4 = hashSet.size() == 0;
                                if (!bl4) {
                                    List list = statement2.getNeighbours(2, 0);
                                    boolean bl5 = bl4 = hashSet.containsAll(list) && (hashSet.size() > list.size() || hashSet.size() == 1);
                                }
                                if (!bl4) continue;
                                LinkedList<Statement> linkedList = new LinkedList<Statement>();
                                linkedList.add(statement2);
                                while (!linkedList.isEmpty()) {
                                    Statement statement3 = (Statement)linkedList.remove(0);
                                    if (hashSet.contains(statement3) || !bl3 && statement3 == object4) continue;
                                    hashSet.add(statement3);
                                    if (statement3 != object2) {
                                        hashSet2.addAll(statement3.getNeighbours(1, 0));
                                    }
                                    linkedList.addAll(statement3.getNeighbours(1, 1));
                                    hashSet3.addAll(statement3.getNeighbours(2, 1));
                                }
                                bl2 = true;
                                hashSet3.remove(statement2);
                                continue block4;
                            }
                        } while (bl2);
                        hashSet3.clear();
                        for (Statement statement4 : hashSet) {
                            hashSet3.addAll(statement4.getNeighbours(2, 1));
                        }
                        hashSet3.removeAll(hashSet);
                        bl2 = true;
                        Iterator iterator = hashSet3.iterator();
                        while (iterator.hasNext()) {
                            Object var17_24 = null;
                            if (((Statement)iterator.next()).getNeighbours(2, 0).containsAll(hashSet)) continue;
                            bl2 = false;
                            break;
                        }
                        if (bl2) {
                            Object var17_25 = null;
                            hashSet2.removeAll(hashSet);
                            if (hashSet2.size() == 0 && (hashSet.size() > 1 || ((Statement)object2).getNeighbours(1, 0).contains(object2)) && hashSet.size() < vBStyleCollection2.size() && DomHelper.checkSynchronizedCompleteness(hashSet)) {
                                GeneralStatement generalStatement = new GeneralStatement((Statement)object2, hashSet, (Statement)(bl3 ? null : object4));
                                statement.collapseNodesToStatement(generalStatement);
                                return generalStatement;
                            }
                        }
                    }
                    ++n2;
                }
            }
            ++n;
        }
        return null;
    }

    private static boolean checkSynchronizedCompleteness(HashSet hashSet) {
        for (Object object : hashSet) {
            if (!((Statement)object).isMonitorEnter()) continue;
            if ((object = ((Statement)object).getSuccessorEdges(0x40000000)).size() != 1 || ((StatEdge)object.get(0)).getType() != 1) {
                return false;
            }
            if (hashSet.contains(((StatEdge)object.get(0)).getDestination())) continue;
            return false;
        }
        return true;
    }

    private static boolean findSimpleStatements(Statement statement, HashMap hashMap) {
        boolean bl;
        boolean bl2 = false;
        do {
            bl = false;
            Object object = null;
            Object object22 = statement.getPostReversePostOrderList(null).iterator();
            while (object22.hasNext()) {
                object = (Statement)object22.next();
                Object object32 = DoStatement.isHead((Statement)object);
                if ((object32 != null ? object32 : ((object32 = SwitchStatement.isHead((Statement)object)) != null ? object32 : ((object32 = IfStatement.isHead((Statement)object)) != null ? object32 : ((object32 = SequenceStatement.isHead2Block((Statement)object)) != null ? object32 : ((object32 = CatchStatement.isHead((Statement)object)) != null ? object32 : (object = (object32 = CatchAllStatement.isHead((Statement)object)) != null ? object32 : null)))))) == null) continue;
                if (statement.type == 0 && ((Statement)object).getFirst() == statement.getFirst() && statement.getStats().size() == ((Statement)object).getStats().size()) {
                    statement.type = 11;
                }
                statement.collapseNodesToStatement((Statement)object);
                if (!hashMap.isEmpty()) {
                    HashSet<Integer> hashSet = new HashSet<Integer>();
                    for (Object object22 : ((Statement)object).getStats()) {
                        hashSet.add(((Statement)object22).id);
                    }
                    object22 = ((Statement)object).id;
                    for (Object object32 : new ArrayList(hashMap.keySet())) {
                        Set set = (Set)hashMap.get(object32);
                        int n = set.size();
                        set.removeAll(hashSet);
                        if (hashSet.contains(object32)) {
                            HashSet hashSet2 = (HashSet)hashMap.get(object22);
                            if (hashSet2 == null) {
                                hashSet2 = new HashSet();
                                hashMap.put(object22, hashSet2);
                            }
                            hashSet2.addAll(set);
                            hashMap.remove(object32);
                            continue;
                        }
                        if (set.size() >= n) continue;
                        set.add(object22);
                    }
                }
                bl = true;
                break;
            }
            if (!bl) continue;
            bl2 = true;
        } while (bl);
        return bl2;
    }
}

