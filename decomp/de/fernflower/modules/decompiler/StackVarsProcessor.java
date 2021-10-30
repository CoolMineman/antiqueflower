/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.modules.decompiler.SequenceHelper;
import de.fernflower.modules.decompiler.SimplifyExprentsHelper;
import de.fernflower.modules.decompiler.exps.AssignmentExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.MonitorExprent;
import de.fernflower.modules.decompiler.exps.NewExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph;
import de.fernflower.modules.decompiler.sforms.DirectNode;
import de.fernflower.modules.decompiler.sforms.FlattenStatementsHelper;
import de.fernflower.modules.decompiler.sforms.SSAConstructorSparseEx;
import de.fernflower.modules.decompiler.sforms.SSAUConstructorSparseEx;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.vars.VarVersionEdge;
import de.fernflower.modules.decompiler.vars.VarVersionNode;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.modules.decompiler.vars.VarVersionsGraph;
import de.fernflower.struct.StructMethod;
import de.fernflower.util.FastSparseSetFactory$FastSparseSet;
import de.fernflower.util.InterpreterUtil;
import de.fernflower.util.SFormsFastMapDirect;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class StackVarsProcessor {
    public final void simplifyStackVars(RootStatement rootStatement, StructMethod structMethod) {
        boolean bl;
        HashSet hashSet = new HashSet();
        Object object = null;
        do {
            bl = false;
            SSAConstructorSparseEx sSAConstructorSparseEx = new SSAConstructorSparseEx();
            sSAConstructorSparseEx.splitVariables(rootStatement, structMethod);
            object = new SimplifyExprentsHelper(object == null);
            while (((SimplifyExprentsHelper)object).buildIff(rootStatement, hashSet, sSAConstructorSparseEx)) {
                bl = true;
            }
            this.setVersionsToNull(rootStatement);
            SequenceHelper.condenseSequences(rootStatement);
            object = new SSAUConstructorSparseEx();
            ((SSAUConstructorSparseEx)object).splitVariables(rootStatement, structMethod);
            if (this.iterateStatements(rootStatement, (SSAUConstructorSparseEx)object)) {
                bl = true;
            }
            this.setVersionsToNull(rootStatement);
        } while (bl);
        object = new SSAUConstructorSparseEx();
        ((SSAUConstructorSparseEx)object).splitVariables(rootStatement, structMethod);
        this.iterateStatements(rootStatement, (SSAUConstructorSparseEx)object);
        this.setVersionsToNull(rootStatement);
    }

    private void setVersionsToNull(Statement statement2) {
        if (statement2.getExprents() == null) {
            for (Statement statement2 : statement2.getSequentialObjects()) {
                if (statement2 instanceof Statement) {
                    this.setVersionsToNull(statement2);
                    continue;
                }
                if (!(statement2 instanceof Exprent)) continue;
                StackVarsProcessor.setExprentVersionsToNull((Exprent)((Object)statement2));
            }
            return;
        }
        Iterator iterator = statement2.getExprents().iterator();
        while (iterator.hasNext()) {
            StackVarsProcessor.setExprentVersionsToNull((Exprent)iterator.next());
        }
    }

    private static void setExprentVersionsToNull(Exprent exprent) {
        Object object = exprent.getAllExprents(true);
        object.add(exprent);
        object = object.iterator();
        while (object.hasNext()) {
            exprent = (Exprent)object.next();
            if (exprent.type != 12) continue;
            ((VarExprent)exprent).setVersion(0);
        }
    }

    private boolean iterateStatements(RootStatement object, SSAUConstructorSparseEx sSAUConstructorSparseEx) {
        Object var3_3 = null;
        object = new FlattenStatementsHelper().buildDirectGraph((RootStatement)object);
        boolean bl = false;
        HashSet<Object> hashSet = new HashSet<Object>();
        LinkedList<DirectNode> linkedList = new LinkedList<DirectNode>();
        LinkedList linkedList2 = new LinkedList();
        linkedList.add(((DirectGraph)object).first);
        linkedList2.add(new HashMap());
        while (!linkedList.isEmpty()) {
            object = (DirectNode)linkedList.removeFirst();
            HashMap hashMap = (HashMap)linkedList2.removeFirst();
            if (hashSet.contains(object)) continue;
            hashSet.add(object);
            ArrayList<List> arrayList = new ArrayList<List>();
            if (!((DirectNode)object).exprents.isEmpty()) {
                arrayList.add(((DirectNode)object).exprents);
            }
            if (((DirectNode)object).succs.size() == 1) {
                DirectNode directNode = (DirectNode)((DirectNode)object).succs.get(0);
                if (directNode.type == 2 && !directNode.exprents.isEmpty()) {
                    arrayList.add(((DirectNode)object.succs.get((int)0)).exprents);
                    object = directNode;
                }
            }
            int n = 0;
            while (n < arrayList.size()) {
                List list = (List)arrayList.get(n);
                Object object2 = 0;
                while (object2 < list.size()) {
                    Object object3 = null;
                    if (object2 == list.size() - 1) {
                        if (n < arrayList.size() - 1) {
                            object3 = (Exprent)((List)arrayList.get(n + 1)).get(0);
                        }
                    } else {
                        object3 = (Exprent)list.get(object2 + 1);
                    }
                    int[] nArray = this.iterateExprent(list, (int)object2, (Exprent)object3, hashMap, sSAUConstructorSparseEx);
                    object3 = nArray;
                    object2 = nArray[0] >= 0 ? (Object)object3[0] : ++object2;
                    bl |= object3[1] == true;
                }
                ++n;
            }
            for (DirectNode directNode : ((DirectNode)object).succs) {
                linkedList.add(directNode);
                linkedList2.add(new HashMap(hashMap));
            }
        }
        return bl;
    }

    private void replaceSingleVar(Exprent iterator2, VarExprent object, Exprent object2, SSAUConstructorSparseEx sSAUConstructorSparseEx) {
        ((Exprent)((Object)iterator2)).replaceExprent((Exprent)object, (Exprent)object2);
        SFormsFastMapDirect sFormsFastMapDirect = sSAUConstructorSparseEx.getLiveVarVersionsMap(new VarVersionPaar((VarExprent)object));
        for (Iterator iterator2 : StackVarsProcessor.getAllVersions((Exprent)object2)) {
            iterator2 = ((VarVersionNode)sSAUConstructorSparseEx.getSsuversions().nodes.getWithKey((Object)iterator2)).live.entryList().iterator();
            while (iterator2.hasNext()) {
                object2 = (Map.Entry)iterator2.next();
                Integer n = (Integer)object2.getKey();
                if (!sFormsFastMapDirect.containsKey(n)) {
                    iterator2.remove();
                    continue;
                }
                object2 = (FastSparseSetFactory$FastSparseSet)object2.getValue();
                ((FastSparseSetFactory$FastSparseSet)object2).complement(sFormsFastMapDirect.get(n));
                if (!((FastSparseSetFactory$FastSparseSet)object2).isEmpty()) continue;
                iterator2.remove();
            }
        }
    }

    private int[] iterateExprent(List list, int n, Exprent object, HashMap hashMap, SSAUConstructorSparseEx sSAUConstructorSparseEx) {
        Object object2;
        Object object3;
        boolean bl;
        Object object422;
        Exprent exprent = (Exprent)list.get(n);
        int n2 = 0;
        for (Object object422 : exprent.getAllExprents()) {
            do {
                object3 = ((StackVarsProcessor)this).iterateChildExprent((Exprent)object422, exprent, (Exprent)object, hashMap, sSAUConstructorSparseEx);
                Exprent arrayList2 = (Exprent)object3[0];
                n2 |= (Boolean)object3[1] != false ? 1 : 0;
                bl = (Boolean)object3[2];
                if (arrayList2 == null) continue;
                if (bl) {
                    ((StackVarsProcessor)this).replaceSingleVar(exprent, (VarExprent)object422, arrayList2, sSAUConstructorSparseEx);
                    object422 = arrayList2;
                } else {
                    exprent.replaceExprent((Exprent)object422, arrayList2);
                }
                n2 = 1;
            } while (bl);
        }
        object422 = null;
        Object object5 = null;
        if (exprent.type == 2) {
            object3 = (Object[])exprent;
            if (object3.getLeft().type == 12) {
                object422 = (VarExprent)((AssignmentExprent)object3).getLeft();
                object5 = ((AssignmentExprent)object3).getRight();
            }
        }
        if (object422 == null) {
            return new int[]{-1, n2};
        }
        object3 = new VarVersionPaar((VarExprent)object422);
        ArrayList arrayList = new ArrayList();
        bl = StackVarsProcessor.getUsedVersions(sSAUConstructorSparseEx, (VarVersionPaar)object3, arrayList);
        if (!bl && arrayList.isEmpty()) {
            if (((VarExprent)object422).isStack() && (((Exprent)object5).type == 8 || ((Exprent)object5).type == 2 || ((Exprent)object5).type == 10)) {
                if (((Exprent)object5).type == 10 && (((NewExprent)(this = (NewExprent)object5)).isAnonymous() || this.getNewtype().arraydim > 0 || this.getNewtype().type != 8)) {
                    return new int[]{-1, n2};
                }
                list.set(n, object5);
                return new int[]{n + 1, 1};
            }
            if (((Exprent)object5).type == 12) {
                list.remove(n);
                return new int[]{n, 1};
            }
            return new int[]{-1, n2};
        }
        int n3 = ((Exprent)object5).getExprentUse();
        if (!((VarExprent)object422).isStack() && (((Exprent)object5).type != 12 || ((VarExprent)object5).isStack())) {
            return new int[]{-1, n2};
        }
        if ((n3 & 1) == 0 && (bl || arrayList.size() > 1)) {
            return new int[]{-1, n2};
        }
        HashMap hashMap2 = StackVarsProcessor.getAllVarVersions((VarVersionPaar)object3, (Exprent)object5, sSAUConstructorSparseEx);
        boolean bl2 = hashMap2.containsKey(((VarVersionPaar)object3).var);
        if (bl2 && bl) {
            return new int[]{-1, n2};
        }
        Object object4 = object = object == null ? null : StackVarsProcessor.getAllVersions((Exprent)object);
        if (((Exprent)object5).type != 3 && ((Exprent)object5).type != 12 && object != null && hashMap2.containsKey(((VarVersionPaar)object3).var)) {
            for (Object object422 : arrayList) {
                if (((HashSet)object).contains(new VarVersionPaar(((VarVersionNode)object422).var, ((VarVersionNode)object422).version))) continue;
                return new int[]{-1, n2};
            }
        }
        hashMap2.remove(((VarVersionPaar)object3).var);
        boolean bl3 = false;
        boolean bl4 = false;
        HashSet<Object> hashSet = new HashSet<Object>();
        for (VarVersionNode varVersionNode : arrayList) {
            object2 = new VarVersionPaar(varVersionNode.var, varVersionNode.version);
            if (StackVarsProcessor.isVersionToBeReplaced((VarVersionPaar)object2, hashMap2, sSAUConstructorSparseEx, (VarVersionPaar)object3) && (((Exprent)object5).type == 3 || ((Exprent)object5).type == 12 || ((Exprent)object5).type == 5 || object == null || ((HashSet)object).contains(object2))) {
                hashSet.add(object2);
                bl4 = true;
                continue;
            }
            bl3 = true;
        }
        if (bl2 && bl3) {
            return new int[]{-1, n2};
        }
        for (VarVersionPaar varVersionPaar : hashSet) {
            object2 = ((Exprent)object5).copy();
            if (((Exprent)object5).type == 5 && sSAUConstructorSparseEx.getMapFieldVars().containsKey(((Exprent)object5).id)) {
                sSAUConstructorSparseEx.getMapFieldVars().put(((Exprent)object2).id, (Integer)sSAUConstructorSparseEx.getMapFieldVars().get(((Exprent)object5).id));
            }
            hashMap.put(varVersionPaar, object2);
        }
        if (!bl && !bl3) {
            list.remove(n);
            return new int[]{n, 1};
        }
        if (bl4) {
            return new int[]{n + 1, n2};
        }
        return new int[]{-1, n2};
    }

    private static HashSet getAllVersions(Exprent exprent) {
        HashSet<VarVersionPaar> hashSet = new HashSet<VarVersionPaar>();
        Object object = new ArrayList<Exprent>(exprent.getAllExprents(true));
        object.add(exprent);
        object = object.iterator();
        while (object.hasNext()) {
            exprent = (Exprent)object.next();
            if (exprent.type != 12) continue;
            exprent = (VarExprent)exprent;
            hashSet.add(new VarVersionPaar((VarExprent)exprent));
        }
        return hashSet;
    }

    /*
     * WARNING - void declaration
     */
    private Object[] iterateChildExprent(Exprent exprent, Exprent object, Exprent exprent2, HashMap hashMap, SSAUConstructorSparseEx sSAUConstructorSparseEx) {
        Object object2;
        MonitorExprent monitorExprent;
        Exprent exprent3;
        Object object3;
        void var5_15;
        void var4_14;
        void var3_12;
        HashSet hashSet;
        Object object42;
        void var1_9;
        boolean bl = false;
        for (Object object42 : var1_9.getAllExprents()) {
            boolean bl2;
            do {
                StackVarsProcessor arrayList2;
                object3 = arrayList2.iterateChildExprent((Exprent)object42, (Exprent)((Object)hashSet), (Exprent)var3_12, (HashMap)var4_14, (SSAUConstructorSparseEx)var5_15);
                exprent3 = (Exprent)object3[0];
                bl |= ((Boolean)object3[1]).booleanValue();
                bl2 = (Boolean)object3[2];
                if (exprent3 == null) continue;
                if (bl2) {
                    arrayList2.replaceSingleVar((Exprent)var1_9, (VarExprent)object42, exprent3, (SSAUConstructorSparseEx)var5_15);
                    object42 = exprent3;
                } else {
                    var1_9.replaceExprent((Exprent)object42, exprent3);
                }
                bl = true;
            } while (bl2);
        }
        object42 = var4_14;
        void var0_1 = var1_9;
        HashSet<Object> hashSet2 = null;
        if (var0_1.type == 12) {
            VarExprent varExprent = (VarExprent)var0_1;
            hashSet2 = (Exprent)((HashMap)object42).get(new VarVersionPaar(varExprent));
        }
        if ((object42 = hashSet2) != null) {
            return new Object[]{object42, true, true};
        }
        hashSet2 = null;
        object3 = null;
        if (var1_9.type == 2) {
            exprent3 = (AssignmentExprent)var1_9;
            if (exprent3.getLeft().type == 12) {
                hashSet2 = (VarExprent)((AssignmentExprent)exprent3).getLeft();
                object3 = ((AssignmentExprent)exprent3).getRight();
            }
        }
        if (hashSet2 == null) {
            Object[] objectArray = new Object[3];
            objectArray[1] = bl;
            objectArray[2] = false;
            return objectArray;
        }
        boolean bl3 = false;
        if (var3_12 == null && ((Exprent)hashSet).type == 9 && (monitorExprent = (MonitorExprent)((Object)hashSet)).getMontype() == 0 && var1_9.equals(monitorExprent.getValue())) {
            bl3 = true;
        }
        if (!((VarExprent)((Object)hashSet2)).isStack() && !bl3) {
            Object[] objectArray = new Object[3];
            objectArray[1] = bl;
            objectArray[2] = false;
            return objectArray;
        }
        VarVersionPaar varVersionPaar = new VarVersionPaar((VarExprent)((Object)hashSet2));
        ArrayList arrayList = new ArrayList();
        boolean bl4 = StackVarsProcessor.getUsedVersions((SSAUConstructorSparseEx)var5_15, varVersionPaar, arrayList);
        if (!bl4 && arrayList.isEmpty()) {
            return new Object[]{object3, bl, false};
        }
        if (!((VarExprent)((Object)hashSet2)).isStack()) {
            Object[] objectArray = new Object[3];
            objectArray[1] = bl;
            objectArray[2] = false;
            return objectArray;
        }
        boolean bl5 = false;
        if ((((Exprent)object3).getExprentUse() & 3) != 3) {
            Object[] objectArray = new Object[3];
            objectArray[1] = bl;
            objectArray[2] = false;
            return objectArray;
        }
        HashMap hashMap2 = StackVarsProcessor.getAllVarVersions(varVersionPaar, (Exprent)object3, (SSAUConstructorSparseEx)var5_15);
        if (hashMap2.containsKey(varVersionPaar.var) && bl4) {
            Object[] objectArray = new Object[3];
            objectArray[1] = bl;
            objectArray[2] = false;
            return objectArray;
        }
        hashMap2.remove(varVersionPaar.var);
        hashSet = StackVarsProcessor.getAllVersions((Exprent)((Object)hashSet));
        if (var3_12 != null) {
            hashSet.addAll(StackVarsProcessor.getAllVersions((Exprent)var3_12));
        }
        boolean bl6 = false;
        hashSet2 = new HashSet<Object>();
        for (VarVersionNode varVersionNode : arrayList) {
            object2 = new VarVersionPaar(varVersionNode.var, varVersionNode.version);
            if (StackVarsProcessor.isVersionToBeReplaced((VarVersionPaar)object2, hashMap2, (SSAUConstructorSparseEx)var5_15, varVersionPaar) && (((Exprent)object3).type == 12 || hashSet.contains(object2))) {
                hashSet2.add(object2);
                continue;
            }
            bl6 = true;
        }
        if (!bl4 && !bl6) {
            for (VarVersionPaar varVersionPaar2 : hashSet2) {
                object2 = ((Exprent)object3).copy();
                if (((Exprent)object3).type == 5 && var5_15.getMapFieldVars().containsKey(((Exprent)object3).id)) {
                    var5_15.getMapFieldVars().put(((Exprent)object2).id, (Integer)var5_15.getMapFieldVars().get(((Exprent)object3).id));
                }
                var4_14.put(varVersionPaar2, object2);
            }
            return new Object[]{object3, bl, false};
        }
        Object[] objectArray = new Object[3];
        objectArray[1] = bl;
        objectArray[2] = false;
        return objectArray;
    }

    private static boolean getUsedVersions(SSAUConstructorSparseEx object, VarVersionPaar object2, List list) {
        object = (VarVersionNode)object.getSsuversions().nodes.getWithKey(object2);
        object2 = new HashSet();
        HashSet<VarVersionNode> hashSet = new HashSet<VarVersionNode>();
        LinkedList<Object> linkedList = new LinkedList<Object>();
        linkedList.add(object);
        while (!linkedList.isEmpty()) {
            VarVersionNode varVersionNode = (VarVersionNode)linkedList.remove(0);
            ((HashSet)object2).add(varVersionNode);
            if (varVersionNode != object && (varVersionNode.flags & 2) == 0) {
                list.add(varVersionNode);
            }
            for (VarVersionEdge varVersionEdge : varVersionNode.succs) {
                VarVersionNode varVersionNode2 = varVersionEdge.dest;
                if (((HashSet)object2).contains(varVersionEdge.dest)) continue;
                boolean bl = true;
                for (VarVersionEdge varVersionEdge2 : varVersionNode2.preds) {
                    if (((HashSet)object2).contains(varVersionEdge2.source)) continue;
                    bl = false;
                    break;
                }
                if (bl) {
                    linkedList.add(varVersionNode2);
                    continue;
                }
                hashSet.add(varVersionNode2);
            }
        }
        hashSet.removeAll((Collection<?>)object2);
        return !hashSet.isEmpty();
    }

    private static boolean isVersionToBeReplaced(VarVersionPaar object5, HashMap object2, SSAUConstructorSparseEx object3, VarVersionPaar object4) {
        Iterator iterator;
        HashSet<VarVersionNode> hashSet;
        Object object;
        VarVersionsGraph varVersionsGraph = ((SSAUConstructorSparseEx)object).getSsuversions();
        SFormsFastMapDirect sFormsFastMapDirect = ((SSAUConstructorSparseEx)object).getLiveVarVersionsMap((VarVersionPaar)object5);
        if (sFormsFastMapDirect == null) {
            return false;
        }
        if (!InterpreterUtil.equalObjects(((SSAUConstructorSparseEx)object).getMapVersionFirstRange().get(hashSet), ((SSAUConstructorSparseEx)object).getMapVersionFirstRange().get(object5))) {
            return false;
        }
        for (Map.Entry entry : ((HashMap)((Object)iterator)).entrySet()) {
            object = sFormsFastMapDirect.get((Integer)entry.getKey());
            if (object == null) {
                return false;
            }
            hashSet = new HashSet<VarVersionNode>();
            for (VarVersionPaar varVersionPaar : (HashSet)entry.getValue()) {
                hashSet.add((VarVersionNode)varVersionsGraph.nodes.getWithKey(varVersionPaar));
            }
            boolean bl = false;
            object = ((FastSparseSetFactory$FastSparseSet)object).iterator();
            while (object.hasNext()) {
                Object object6 = (Integer)object.next();
                object6 = (VarVersionNode)varVersionsGraph.nodes.getWithKey(new VarVersionPaar((int)((Integer)entry.getKey()), ((Integer)object6).intValue()));
                if (!varVersionsGraph.isDominatorSet((VarVersionNode)object6, hashSet)) continue;
                bl = true;
                break;
            }
            if (bl) continue;
            return false;
        }
        return true;
    }

    private static HashMap getAllVarVersions(VarVersionPaar varVersionPaar, Exprent exprent, SSAUConstructorSparseEx sSAUConstructorSparseEx) {
        HashMap hashMap = new HashMap();
        SFormsFastMapDirect sFormsFastMapDirect = sSAUConstructorSparseEx.getLiveVarVersionsMap(varVersionPaar);
        Object object = exprent.getAllExprents(true);
        object.add(exprent);
        object = object.iterator();
        while (object.hasNext()) {
            int n;
            HashSet<VarVersionPaar> hashSet;
            Exprent exprent2 = (Exprent)object.next();
            if (exprent2.type == 12) {
                int n2 = ((VarExprent)exprent2).getIndex0();
                if (varVersionPaar.var != n2) {
                    if (sFormsFastMapDirect.containsKey(n2)) {
                        hashSet = new HashSet<VarVersionPaar>();
                        for (Integer n3 : sFormsFastMapDirect.get(n2)) {
                            hashSet.add(new VarVersionPaar(n2, (int)n3));
                        }
                        hashMap.put(n2, hashSet);
                        continue;
                    }
                    throw new RuntimeException("inkonsistent live map!");
                }
                hashMap.put(n2, null);
                continue;
            }
            if (exprent2.type != 5 || !sSAUConstructorSparseEx.getMapFieldVars().containsKey(exprent2.id) || !sFormsFastMapDirect.containsKey(n = ((Integer)sSAUConstructorSparseEx.getMapFieldVars().get(exprent2.id)).intValue())) continue;
            hashSet = new HashSet();
            for (Integer n3 : sFormsFastMapDirect.get(n)) {
                hashSet.add(new VarVersionPaar(n, (int)n3));
            }
            hashMap.put(n, hashSet);
        }
        return hashMap;
    }
}

