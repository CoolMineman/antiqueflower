/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.vars;

import de.fernflower.modules.decompiler.decompose.GenericDominatorEngine;
import de.fernflower.modules.decompiler.decompose.IGraphNode;
import de.fernflower.modules.decompiler.vars.VarVersionEdge;
import de.fernflower.modules.decompiler.vars.VarVersionNode;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.modules.decompiler.vars.VarVersionsGraph$1;
import de.fernflower.util.VBStyleCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class VarVersionsGraph {
    public VBStyleCollection nodes = new VBStyleCollection();
    private GenericDominatorEngine engine;

    public final VarVersionNode createNode(VarVersionPaar varVersionPaar) {
        VarVersionNode varVersionNode = new VarVersionNode(varVersionPaar.var, varVersionPaar.version);
        ((VarVersionsGraph)varVersionNode).nodes.addWithKey(varVersionNode, varVersionPaar);
        return varVersionNode;
    }

    public final boolean isDominatorSet(VarVersionNode varVersionNode, HashSet hashSet) {
        if (hashSet.size() == 1) {
            return ((VarVersionsGraph)hashSet2).engine.isDominator(varVersionNode, (IGraphNode)hashSet.iterator().next());
        }
        HashSet hashSet2 = new HashSet();
        if (hashSet.contains(varVersionNode)) {
            return true;
        }
        LinkedList<VarVersionNode> linkedList = new LinkedList<VarVersionNode>();
        linkedList.add(varVersionNode);
        while (!linkedList.isEmpty()) {
            varVersionNode = (VarVersionNode)linkedList.remove(0);
            if (hashSet2.contains(varVersionNode)) continue;
            hashSet2.add(varVersionNode);
            if (varVersionNode.preds.isEmpty()) {
                return false;
            }
            Iterator iterator = varVersionNode.preds.iterator();
            while (iterator.hasNext()) {
                varVersionNode = ((VarVersionEdge)iterator.next()).source;
                if (hashSet2.contains(varVersionNode) || hashSet.contains(varVersionNode)) continue;
                linkedList.add(varVersionNode);
            }
        }
        return true;
    }

    public final void initDominators() {
        HashSet<VarVersionNode> hashSet = new HashSet<VarVersionNode>();
        for (VarVersionNode varVersionNode : this.nodes) {
            if (!varVersionNode.preds.isEmpty()) continue;
            hashSet.add(varVersionNode);
        }
        this.engine = new GenericDominatorEngine(new VarVersionsGraph$1(this, hashSet));
        this.engine.orderNodes();
    }

    private static void addToReversePostOrderListIterative(VarVersionNode varVersionNode, List list, HashSet hashSet) {
        HashMap hashMap = new HashMap();
        LinkedList<VarVersionNode> linkedList = new LinkedList<VarVersionNode>();
        LinkedList<Integer> linkedList2 = new LinkedList<Integer>();
        linkedList.add(varVersionNode);
        linkedList2.add(0);
        while (!linkedList.isEmpty()) {
            varVersionNode = (VarVersionNode)linkedList.getLast();
            int n = (Integer)linkedList2.removeLast();
            hashSet.add(varVersionNode);
            ArrayList arrayList = (ArrayList)hashMap.get(varVersionNode);
            if (arrayList == null) {
                arrayList = new ArrayList(varVersionNode.succs);
                hashMap.put(varVersionNode, arrayList);
            }
            while (n < arrayList.size()) {
                VarVersionNode varVersionNode2 = ((VarVersionEdge)arrayList.get((int)n)).dest;
                if (!hashSet.contains(varVersionNode2)) {
                    linkedList2.add(n + 1);
                    linkedList.add(varVersionNode2);
                    linkedList2.add(0);
                    break;
                }
                ++n;
            }
            if (n != arrayList.size()) continue;
            list.add(0, varVersionNode);
            linkedList.removeLast();
        }
    }

    static /* synthetic */ LinkedList access$0(VarVersionsGraph object, Collection object2) {
        object = new LinkedList();
        HashSet hashSet = new HashSet();
        Iterator iterator = object2.iterator();
        while (iterator.hasNext()) {
            object2 = (VarVersionNode)iterator.next();
            LinkedList linkedList = new LinkedList();
            VarVersionsGraph.addToReversePostOrderListIterative((VarVersionNode)object2, linkedList, hashSet);
            ((LinkedList)object).addAll(linkedList);
        }
        return object;
    }
}

