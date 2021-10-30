/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.sforms;

import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph$ExprentIterator;
import de.fernflower.modules.decompiler.sforms.DirectNode;
import de.fernflower.util.VBStyleCollection;
import java.util.AbstractSequentialList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class DirectGraph {
    public VBStyleCollection nodes = new VBStyleCollection();
    public DirectNode first;
    public HashMap mapShortRangeFinallyPaths = new HashMap();
    public HashMap mapLongRangeFinallyPaths = new HashMap();
    public HashMap mapNegIfBranch = new HashMap();
    public HashMap mapFinallyMonitorExceptionPathExits = new HashMap();

    public final void sortReversePostOrder() {
        Object object = new LinkedList();
        DirectGraph.addToReversePostOrderListIterative(this.first, object);
        this.nodes.clear();
        Iterator iterator = ((AbstractSequentialList)object).iterator();
        while (iterator.hasNext()) {
            object = (DirectNode)iterator.next();
            this.nodes.addWithKey(object, ((DirectNode)object).id);
        }
    }

    private static void addToReversePostOrderListIterative(DirectNode directNode, List list) {
        LinkedList<DirectNode> linkedList = new LinkedList<DirectNode>();
        LinkedList<Integer> linkedList2 = new LinkedList<Integer>();
        HashSet<DirectNode> hashSet = new HashSet<DirectNode>();
        linkedList.add(directNode);
        linkedList2.add(0);
        while (!linkedList.isEmpty()) {
            directNode = (DirectNode)linkedList.getLast();
            int n = (Integer)linkedList2.removeLast();
            hashSet.add(directNode);
            while (n < directNode.succs.size()) {
                DirectNode directNode2 = (DirectNode)directNode.succs.get(n);
                if (!hashSet.contains(directNode2)) {
                    linkedList2.add(n + 1);
                    linkedList.add(directNode2);
                    linkedList2.add(0);
                    break;
                }
                ++n;
            }
            if (n != directNode.succs.size()) continue;
            list.add(0, directNode);
            linkedList.removeLast();
        }
    }

    public final boolean iterateExprents(DirectGraph$ExprentIterator directGraph$ExprentIterator) {
        LinkedList<DirectNode> linkedList = new LinkedList<DirectNode>();
        linkedList.add(((DirectGraph)hashSet).first);
        HashSet hashSet = new HashSet();
        while (!linkedList.isEmpty()) {
            DirectNode directNode = (DirectNode)linkedList.removeFirst();
            if (hashSet.contains(directNode)) continue;
            hashSet.add(directNode);
            int n = 0;
            while (n < directNode.exprents.size()) {
                int n2 = directGraph$ExprentIterator.processExprent((Exprent)directNode.exprents.get(n));
                if (n2 == 1) {
                    return false;
                }
                if (n2 == 2) {
                    directNode.exprents.remove(n);
                    --n;
                }
                ++n;
            }
            linkedList.addAll(directNode.succs);
        }
        return true;
    }
}

