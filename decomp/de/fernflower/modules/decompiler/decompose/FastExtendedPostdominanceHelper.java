/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.decompose;

import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.decompose.DominatorEngine;
import de.fernflower.modules.decompiler.decompose.DominatorTreeExceptionFilter;
import de.fernflower.modules.decompiler.decompose.FastExtendedPostdominanceHelper$1;
import de.fernflower.modules.decompiler.decompose.FastExtendedPostdominanceHelper$2;
import de.fernflower.modules.decompiler.decompose.FastExtendedPostdominanceHelper$3;
import de.fernflower.modules.decompiler.decompose.FastExtendedPostdominanceHelper$IReachabilityAction;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.util.FastFixedSetFactory;
import de.fernflower.util.FastFixedSetFactory$FastFixedSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class FastExtendedPostdominanceHelper {
    private List lstReversePostOrderList;
    private HashMap mapSupportPoints = new HashMap();
    private HashMap mapExtPostdominators = new HashMap();
    private Statement statement;
    private FastFixedSetFactory factory;

    public final HashMap getExtendedPostdominators(Statement object) {
        FastExtendedPostdominanceHelper object3;
        Object object22;
        Iterator iterator;
        object3.statement = iterator;
        HashSet<Integer> hashSet = new HashSet<Integer>();
        for (Object object22 : ((Statement)((Object)iterator)).getStats()) {
            hashSet.add(((Statement)object22).id);
        }
        object3.factory = new FastFixedSetFactory(hashSet);
        object3.lstReversePostOrderList = ((Statement)((Object)iterator)).getReversePostOrderList();
        object3.calcDefaultReachableSets();
        object3.removeErroneousNodes();
        object22 = new DominatorTreeExceptionFilter((Statement)((Object)iterator));
        ((DominatorTreeExceptionFilter)object22).initialize();
        object3.filterOnExceptionRanges((DominatorTreeExceptionFilter)object22);
        object3.filterOnDominance((DominatorTreeExceptionFilter)object22);
        HashMap hashMap = new HashMap();
        for (Map.Entry entry : object3.mapExtPostdominators.entrySet()) {
            hashMap.put((Integer)entry.getKey(), ((FastFixedSetFactory$FastFixedSet)entry.getValue()).toPlainSet());
        }
        return hashMap;
    }

    private void filterOnDominance(DominatorTreeExceptionFilter object) {
        object = ((DominatorTreeExceptionFilter)object).getDomEngine();
        for (Integer n : new HashSet(this.mapExtPostdominators.keySet())) {
            FastFixedSetFactory$FastFixedSet fastFixedSetFactory$FastFixedSet = (FastFixedSetFactory$FastFixedSet)this.mapExtPostdominators.get(n);
            LinkedList<Statement> linkedList = new LinkedList<Statement>();
            LinkedList<FastFixedSetFactory$FastFixedSet> linkedList2 = new LinkedList<FastFixedSetFactory$FastFixedSet>();
            linkedList.add((Statement)this.statement.getStats().getWithKey(n));
            linkedList2.add(this.factory.spawnEmptySet());
            HashSet<Statement> hashSet = new HashSet<Statement>();
            while (!linkedList.isEmpty()) {
                Statement statement = (Statement)linkedList.removeFirst();
                FastFixedSetFactory$FastFixedSet fastFixedSetFactory$FastFixedSet2 = (FastFixedSetFactory$FastFixedSet)linkedList2.removeFirst();
                if (fastFixedSetFactory$FastFixedSet.contains(statement.id)) {
                    fastFixedSetFactory$FastFixedSet2.add(statement.id);
                }
                if (fastFixedSetFactory$FastFixedSet2.contains(fastFixedSetFactory$FastFixedSet)) continue;
                hashSet.add(statement);
                int n2 = 0;
                Object object22 = fastFixedSetFactory$FastFixedSet.iterator();
                while (object22.hasNext()) {
                    Integer n3 = (Integer)object22.next();
                    if (fastFixedSetFactory$FastFixedSet2.contains(n3)) continue;
                    if (n2 == 0) {
                        int n4 = n2 = ((DominatorEngine)object).isDominator(statement.id, n) ? 2 : 1;
                    }
                    if (n2 != true) continue;
                    object22.remove();
                }
                for (Object object22 : statement.getSuccessorEdges(1)) {
                    if (hashSet.contains(((StatEdge)object22).getDestination())) continue;
                    linkedList.add(((StatEdge)object22).getDestination());
                    linkedList2.add(fastFixedSetFactory$FastFixedSet2.getCopy());
                }
            }
            if (!fastFixedSetFactory$FastFixedSet.isEmpty()) continue;
            this.mapExtPostdominators.remove(n);
        }
    }

    private void filterOnExceptionRanges(DominatorTreeExceptionFilter dominatorTreeExceptionFilter) {
        for (Integer n : new HashSet(this.mapExtPostdominators.keySet())) {
            FastFixedSetFactory$FastFixedSet fastFixedSetFactory$FastFixedSet = (FastFixedSetFactory$FastFixedSet)this.mapExtPostdominators.get(n);
            Iterator iterator = fastFixedSetFactory$FastFixedSet.iterator();
            while (iterator.hasNext()) {
                if (dominatorTreeExceptionFilter.acceptStatementPair(n, (Integer)iterator.next())) continue;
                iterator.remove();
            }
            if (!fastFixedSetFactory$FastFixedSet.isEmpty()) continue;
            this.mapExtPostdominators.remove(n);
        }
    }

    private void removeErroneousNodes() {
        this.mapSupportPoints = new HashMap();
        this.calcReachabilitySuppPoints(1);
        this.iterateReachability(new FastExtendedPostdominanceHelper$1(this), 1);
        FastFixedSetFactory$FastFixedSet fastFixedSetFactory$FastFixedSet = this.factory.spawnEmptySet();
        boolean bl = false;
        for (Statement statement : this.statement.getStats()) {
            if (!statement.getPredecessorEdges(0x40000000).isEmpty() || statement.getPredecessorEdges(2).isEmpty()) continue;
            fastFixedSetFactory$FastFixedSet.add(statement.id);
            bl = true;
        }
        if (bl) {
            Iterator<Object> iterator = this.mapExtPostdominators.values().iterator();
            while (iterator.hasNext()) {
                ((FastFixedSetFactory$FastFixedSet)iterator.next()).complement(fastFixedSetFactory$FastFixedSet);
            }
        }
    }

    private void calcDefaultReachableSets() {
        this.calcReachabilitySuppPoints(3);
        for (Statement statement : this.statement.getStats()) {
            this.mapExtPostdominators.put(statement.id, this.factory.spawnEmptySet());
        }
        this.iterateReachability(new FastExtendedPostdominanceHelper$2(this), 3);
    }

    private void calcReachabilitySuppPoints(int n) {
        this.iterateReachability(new FastExtendedPostdominanceHelper$3(this, n), n);
    }

    private void iterateReachability(FastExtendedPostdominanceHelper$IReachabilityAction fastExtendedPostdominanceHelper$IReachabilityAction, int n) {
        boolean bl;
        do {
            bl = false;
            HashMap<Integer, FastFixedSetFactory$FastFixedSet> hashMap = new HashMap<Integer, FastFixedSetFactory$FastFixedSet>();
            for (Object object3 : this.lstReversePostOrderList) {
                FastFixedSetFactory$FastFixedSet fastFixedSetFactory$FastFixedSet = this.factory.spawnEmptySet();
                fastFixedSetFactory$FastFixedSet.add(((Statement)object3).id);
                for (Object object2 : ((Statement)object3).getAllPredecessorEdges()) {
                    if ((((StatEdge)object2).getType() & n) == 0) continue;
                    object2 = ((StatEdge)object2).getSource();
                    FastFixedSetFactory$FastFixedSet fastFixedSetFactory$FastFixedSet2 = (FastFixedSetFactory$FastFixedSet)hashMap.get(((Statement)object2).id);
                    if (fastFixedSetFactory$FastFixedSet2 == null) {
                        fastFixedSetFactory$FastFixedSet2 = (FastFixedSetFactory$FastFixedSet)this.mapSupportPoints.get(((Statement)object2).id);
                    }
                    if (fastFixedSetFactory$FastFixedSet2 == null) continue;
                    fastFixedSetFactory$FastFixedSet.union(fastFixedSetFactory$FastFixedSet2);
                }
                hashMap.put(((Statement)object3).id, fastFixedSetFactory$FastFixedSet);
                bl |= fastExtendedPostdominanceHelper$IReachabilityAction.action((Statement)object3, hashMap);
                for (Object object2 : ((Statement)object3).getAllPredecessorEdges()) {
                    if ((((StatEdge)object2).getType() & n) == 0) continue;
                    object2 = ((StatEdge)object2).getSource();
                    if (!hashMap.containsKey(((Statement)object2).id)) continue;
                    boolean bl2 = true;
                    for (Object object3 : ((Statement)object2).getAllSuccessorEdges()) {
                        if ((((StatEdge)object3).getType() & n) == 0 || hashMap.containsKey(object3.getDestination().id)) continue;
                        bl2 = false;
                        break;
                    }
                    if (!bl2) continue;
                    hashMap.put(((Statement)object2).id, null);
                }
            }
        } while (bl);
    }

    static /* synthetic */ HashMap access$0(FastExtendedPostdominanceHelper fastExtendedPostdominanceHelper) {
        return fastExtendedPostdominanceHelper.mapSupportPoints;
    }

    static /* synthetic */ FastFixedSetFactory access$1(FastExtendedPostdominanceHelper fastExtendedPostdominanceHelper) {
        return fastExtendedPostdominanceHelper.factory;
    }

    static /* synthetic */ HashMap access$2(FastExtendedPostdominanceHelper fastExtendedPostdominanceHelper) {
        return fastExtendedPostdominanceHelper.mapExtPostdominators;
    }
}

