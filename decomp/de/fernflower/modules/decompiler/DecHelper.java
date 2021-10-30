/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.stats.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class DecHelper {
    public static boolean checkStatementExceptions(List list) {
        HashSet hashSet = new HashSet(list);
        HashSet hashSet2 = new HashSet();
        Object object = null;
        Object object2 = list.iterator();
        while (object2.hasNext()) {
            Object object3 = null;
            object3 = ((Statement)object2.next()).getNeighboursSet(2, 1);
            if (object == null) {
                object = object3;
                continue;
            }
            HashSet hashSet3 = new HashSet(object);
            hashSet3.removeAll((Collection<?>)object3);
            object.retainAll((Collection<?>)object3);
            object3.removeAll((Collection<?>)object);
            hashSet2.addAll(hashSet3);
            hashSet2.addAll(object3);
        }
        for (Object object3 : hashSet2) {
            if (hashSet.contains(object3) && hashSet.containsAll(((Statement)object3).getNeighbours(2, 0))) continue;
            return false;
        }
        int n = 1;
        while (n < list.size()) {
            object2 = (Statement)list.get(n);
            if (!((Statement)object2).getPredecessorEdges(2).isEmpty() && !hashSet2.contains(object2)) {
                return false;
            }
            ++n;
        }
        return true;
    }

    public static boolean isChoiceStatement(Statement statement, List list) {
        boolean bl;
        Object object = null;
        Set set = statement.getNeighboursSet(1, 1);
        if (set.contains(statement)) {
            return false;
        }
        block0: do {
            list.clear();
            bl = false;
            set.remove(object);
            for (Statement statement2 : set) {
                if (statement2.getLastBasicType() != 2) {
                    if (object == null) {
                        object = statement2;
                        bl = true;
                        continue block0;
                    }
                    return false;
                }
                Object object2 = statement2.getNeighboursSet(1, 0);
                object2.remove(statement);
                if (object2.contains(statement2)) {
                    return false;
                }
                if (!set.containsAll((Collection<?>)object2) || object2.size() > 1) {
                    if (object == null) {
                        object = statement2;
                        bl = true;
                        continue block0;
                    }
                    return false;
                }
                if (object2.size() == 1) {
                    object2 = (Statement)object2.iterator().next();
                    while (list.contains(object2)) {
                        object2 = ((Statement)object2).getNeighboursSet(1, 0);
                        object2.remove(statement);
                        if (object2.isEmpty()) break;
                        if ((object2 = (Statement)object2.iterator().next()) != statement2) continue;
                        return false;
                    }
                }
                if ((object2 = statement2.getSuccessorEdges(0x40000000)).size() > 1) {
                    object2 = statement2.getNeighboursSet(0x40000000, 1);
                    object2.retainAll(set);
                    if (object2.size() > 0) {
                        return false;
                    }
                    if (object == null) {
                        object = statement2;
                        bl = true;
                        continue block0;
                    }
                    return false;
                }
                if (object2.size() == 1 && ((StatEdge)(object2 = (StatEdge)object2.get(0))).getType() == 1) {
                    if (statement == (object2 = ((StatEdge)object2).getDestination())) {
                        return false;
                    }
                    if (!set.contains(object2) && object != object2) {
                        if (object != null) {
                            return false;
                        }
                        if (((Statement)object2).getNeighboursSet(1, 0).size() > 1) {
                            object = object2;
                            bl = true;
                            continue block0;
                        }
                        return false;
                    }
                }
                list.add(statement2);
            }
        } while (bl);
        list.add(statement);
        list.remove(object);
        list.add(0, object);
        return true;
    }

    public static HashSet getUniquePredExceptions(Statement object) {
        object = new HashSet(((Statement)object).getNeighbours(2, 1));
        Iterator iterator = ((HashSet)object).iterator();
        while (iterator.hasNext()) {
            if (((Statement)iterator.next()).getPredecessorEdges(2).size() <= 1) continue;
            iterator.remove();
        }
        return object;
    }

    public static List copyExprentList(List object) {
        ArrayList<Exprent> arrayList = new ArrayList<Exprent>();
        Iterator iterator = object.iterator();
        while (iterator.hasNext()) {
            object = (Exprent)iterator.next();
            arrayList.add(((Exprent)object).copy());
        }
        return arrayList;
    }
}

