/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.decompose;

import de.fernflower.modules.decompiler.decompose.DominatorEngine;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.util.VBStyleCollection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DominatorTreeExceptionFilter {
    private Statement statement;
    private Map mapTreeBranches = new HashMap();
    private Map mapExceptionRanges = new HashMap();
    private Map mapExceptionDoms = new HashMap();
    private Map mapExceptionRangeUniqueExit = new HashMap();
    private DominatorEngine domEngine;

    public DominatorTreeExceptionFilter(Statement statement) {
        this.statement = statement;
    }

    public final void initialize() {
        this.domEngine = new DominatorEngine(this.statement);
        this.domEngine.calcIDoms();
        DominatorTreeExceptionFilter dominatorTreeExceptionFilter = this;
        VBStyleCollection vBStyleCollection = dominatorTreeExceptionFilter.domEngine.getOrderedIDoms();
        List list = vBStyleCollection.getLstKeys();
        int n = list.size() - 1;
        while (n >= 0) {
            Integer n2 = (Integer)list.get(n);
            Integer n3 = (Integer)vBStyleCollection.get(n);
            HashSet<Integer> hashSet = (HashSet<Integer>)dominatorTreeExceptionFilter.mapTreeBranches.get(n3);
            if (hashSet == null) {
                hashSet = new HashSet<Integer>();
                dominatorTreeExceptionFilter.mapTreeBranches.put(n3, hashSet);
            }
            hashSet.add(n2);
            --n;
        }
        Integer n4 = dominatorTreeExceptionFilter.statement.getFirst().id;
        ((Set)dominatorTreeExceptionFilter.mapTreeBranches.get(n4)).remove(n4);
        this.buildExceptionRanges();
        this.buildFilter(this.statement.getFirst().id);
        this.mapTreeBranches.clear();
        this.mapExceptionRanges.clear();
    }

    public final boolean acceptStatementPair(Integer n, Integer n2) {
        Map.Entry entry2 = null;
        for (Map.Entry entry2 : ((Map)this.mapExceptionRangeUniqueExit.get(n)).entrySet()) {
            if (n.equals(this.mapExceptionDoms.get(entry2.getKey())) || (Integer)((Object)(entry2 = (Integer)entry2.getValue())) != -1 && ((Integer)((Object)entry2)).equals(n2)) continue;
            return false;
        }
        return true;
    }

    private void buildExceptionRanges() {
        for (Statement statement : this.statement.getStats()) {
            Object object = statement.getNeighbours(2, 0);
            if (object.isEmpty()) continue;
            HashSet<Integer> hashSet = new HashSet<Integer>();
            Iterator iterator = object.iterator();
            while (iterator.hasNext()) {
                object = (Statement)iterator.next();
                hashSet.add(((Statement)object).id);
            }
            this.mapExceptionRanges.put(statement.id, hashSet);
        }
        this.mapExceptionDoms = this.buildExceptionDoms(this.statement.getFirst().id);
    }

    private Map buildExceptionDoms(Integer n) {
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        Set object2 = (Set)this.mapTreeBranches.get(n);
        if (object2 != null) {
            for (Integer n2 : object2) {
                Map map = this.buildExceptionDoms(n2);
                Iterator iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    Integer n3;
                    hashMap.put(n3, hashMap.containsKey(n3 = (Integer)iterator.next()) ? n : (Integer)map.get(n3));
                }
            }
        }
        for (Map.Entry entry : this.mapExceptionRanges.entrySet()) {
            if (!((Set)entry.getValue()).contains(n)) continue;
            hashMap.put((Integer)entry.getKey(), n);
        }
        return hashMap;
    }

    private void buildFilter(Integer n) {
        HashMap<Integer, Object> hashMap = new HashMap<Integer, Object>();
        Object object = (Set)this.mapTreeBranches.get(n);
        if (object != null) {
            Iterator iterator = object.iterator();
            while (iterator.hasNext()) {
                object = (Integer)iterator.next();
                this.buildFilter((Integer)object);
                Map map = (Map)this.mapExceptionRangeUniqueExit.get(object);
                for (Map.Entry entry : this.mapExceptionRanges.entrySet()) {
                    Integer n2 = (Integer)entry.getKey();
                    Set object2 = (Set)entry.getValue();
                    if (!object2.contains(n)) continue;
                    Object object3 = null;
                    if ((!object2.contains(object) ? object : (object3 = hashMap.containsKey(n2) ? new Integer(-1) : (Integer)map.get(n2))) == null) continue;
                    hashMap.put(n2, object3);
                }
            }
        }
        this.mapExceptionRangeUniqueExit.put(n, hashMap);
    }

    public final DominatorEngine getDomEngine() {
        return this.domEngine;
    }
}

