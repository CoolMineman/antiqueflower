/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.sforms;

import de.fernflower.modules.decompiler.exps.AssignmentExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FunctionExprent;
import de.fernflower.modules.decompiler.exps.NewExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph;
import de.fernflower.modules.decompiler.sforms.DirectNode;
import de.fernflower.modules.decompiler.sforms.FlattenStatementsHelper;
import de.fernflower.modules.decompiler.sforms.FlattenStatementsHelper$FinallyPathWrapper;
import de.fernflower.modules.decompiler.stats.CatchAllStatement;
import de.fernflower.modules.decompiler.stats.CatchStatement;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.stats.SynchronizedStatement;
import de.fernflower.modules.decompiler.vars.VarVersionEdge;
import de.fernflower.modules.decompiler.vars.VarVersionNode;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.modules.decompiler.vars.VarVersionsGraph;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.gen.MethodDescriptor;
import de.fernflower.util.FastSparseSetFactory;
import de.fernflower.util.FastSparseSetFactory$FastSparseSet;
import de.fernflower.util.InterpreterUtil;
import de.fernflower.util.SFormsFastMapDirect;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public final class SSAUConstructorSparseEx {
    private HashMap inVarVersions = new HashMap();
    private HashMap outVarVersions = new HashMap();
    private HashMap outNegVarVersions = new HashMap();
    private HashMap extraVarVersions = new HashMap();
    private HashMap phi = new HashMap();
    private HashMap lastversion = new HashMap();
    private HashMap mapVersionFirstRange = new HashMap();
    private HashMap phantomppnodes = new HashMap();
    private HashMap phantomexitnodes = new HashMap();
    private VarVersionsGraph ssuversions = new VarVersionsGraph();
    private HashMap mapFieldVars = new HashMap();
    private int fieldvarcounter = -1;
    private FastSparseSetFactory factory;

    public final void splitVariables(RootStatement rootStatement, StructMethod structMethod) {
        FlattenStatementsHelper flattenStatementsHelper = new FlattenStatementsHelper();
        DirectGraph directGraph = flattenStatementsHelper.buildDirectGraph(rootStatement);
        HashSet<Integer> hashSet = new HashSet<Integer>();
        int n = 0;
        while (n < 64) {
            hashSet.add(n);
            ++n;
        }
        this.factory = new FastSparseSetFactory(hashSet);
        this.extraVarVersions.put(directGraph.first.id, this.createFirstMap(structMethod, rootStatement));
        this.setCatchMaps(rootStatement, directGraph, flattenStatementsHelper);
        HashSet hashSet2 = new HashSet();
        do {
            this.ssaStatements(directGraph, hashSet2, false);
        } while (!hashSet2.isEmpty());
        this.ssaStatements(directGraph, hashSet2, true);
        this.ssuversions.initDominators();
    }

    private void ssaStatements(DirectGraph directGraph, HashSet hashSet, boolean bl) {
        for (Object object : directGraph.nodes) {
            hashSet.remove(((DirectNode)object).id);
            Object object2 = directGraph;
            Object object32 = object;
            SFormsFastMapDirect[] sFormsFastMapDirectArray = this;
            SFormsFastMapDirect sFormsFastMapDirect = new SFormsFastMapDirect();
            for (Object object4 : ((DirectNode)object32).preds) {
                object4 = sFormsFastMapDirectArray.getFilteredOutMap(((DirectNode)object32).id, ((DirectNode)object4).id, (DirectGraph)object2, ((DirectNode)object32).id);
                if (sFormsFastMapDirect.isEmpty()) {
                    sFormsFastMapDirect = ((SFormsFastMapDirect)object4).getCopy();
                    continue;
                }
                SSAUConstructorSparseEx.mergeMaps(sFormsFastMapDirect, (SFormsFastMapDirect)object4);
            }
            if (sFormsFastMapDirectArray.extraVarVersions.containsKey(((DirectNode)object32).id)) {
                Object object4;
                object4 = (SFormsFastMapDirect)sFormsFastMapDirectArray.extraVarVersions.get(((DirectNode)object32).id);
                if (sFormsFastMapDirect.isEmpty()) {
                    sFormsFastMapDirect = ((SFormsFastMapDirect)object4).getCopy();
                } else {
                    SSAUConstructorSparseEx.mergeMaps(sFormsFastMapDirect, (SFormsFastMapDirect)object4);
                }
            }
            sFormsFastMapDirectArray.inVarVersions.put(((DirectNode)object32).id, sFormsFastMapDirect);
            sFormsFastMapDirectArray = new SFormsFastMapDirect((SFormsFastMapDirect)this.inVarVersions.get(((DirectNode)object).id));
            SFormsFastMapDirect[] sFormsFastMapDirectArray2 = new SFormsFastMapDirect[2];
            sFormsFastMapDirectArray2[0] = sFormsFastMapDirectArray;
            sFormsFastMapDirectArray = sFormsFastMapDirectArray2;
            if (((DirectNode)object).exprents != null) {
                for (Object object32 : ((DirectNode)object).exprents) {
                    this.processExprent((Exprent)object32, sFormsFastMapDirectArray, ((DirectNode)object).statement, bl);
                }
            }
            if (sFormsFastMapDirectArray[1] == null) {
                sFormsFastMapDirectArray[1] = sFormsFastMapDirectArray[0];
            }
            if (!(!SSAUConstructorSparseEx.mapsEqual(sFormsFastMapDirectArray[0], (SFormsFastMapDirect)this.outVarVersions.get(((DirectNode)object).id)) || this.outNegVarVersions.containsKey(((DirectNode)object).id) && !SSAUConstructorSparseEx.mapsEqual(sFormsFastMapDirectArray[1], (SFormsFastMapDirect)this.outNegVarVersions.get(((DirectNode)object).id)))) continue;
            this.outVarVersions.put(((DirectNode)object).id, sFormsFastMapDirectArray[0]);
            if (directGraph.mapNegIfBranch.containsKey(((DirectNode)object).id)) {
                this.outNegVarVersions.put(((DirectNode)object).id, sFormsFastMapDirectArray[1]);
            }
            object = ((DirectNode)object).succs.iterator();
            while (object.hasNext()) {
                object2 = (DirectNode)object.next();
                hashSet.add(((DirectNode)object2).id);
            }
        }
    }

    private void processExprent(Exprent object, SFormsFastMapDirect[] object22, Statement statement, boolean bl) {
        block44: {
            Object object3;
            Object object4;
            Object object5;
            Object object6;
            block43: {
                Object object7;
                Exprent exprent;
                if (object == null) {
                    return;
                }
                object6 = null;
                boolean bl2 = false;
                block0 : switch (((Exprent)object).type) {
                    case 2: {
                        object5 = (AssignmentExprent)object;
                        if (((AssignmentExprent)object5).getCondtype() != -1) break;
                        exprent = ((AssignmentExprent)object5).getLeft();
                        if (exprent.type != 12) break;
                        object6 = (VarExprent)exprent;
                        break;
                    }
                    case 6: {
                        exprent = (FunctionExprent)object;
                        switch (((FunctionExprent)exprent).getFunctype()) {
                            case 36: {
                                SFormsFastMapDirect sFormsFastMapDirect;
                                this.processExprent((Exprent)((FunctionExprent)exprent).getLstOperands().get(0), (SFormsFastMapDirect[])object22, statement, bl);
                                if (object22[1] == null) {
                                    sFormsFastMapDirect = new SFormsFastMapDirect(object22[0]);
                                } else {
                                    sFormsFastMapDirect = object22[1];
                                    object22[1] = null;
                                }
                                this.processExprent((Exprent)((FunctionExprent)exprent).getLstOperands().get(1), (SFormsFastMapDirect[])object22, statement, bl);
                                SFormsFastMapDirect[] sFormsFastMapDirectArray = new SFormsFastMapDirect[2];
                                sFormsFastMapDirectArray[0] = sFormsFastMapDirect;
                                object4 = sFormsFastMapDirectArray;
                                this.processExprent((Exprent)((FunctionExprent)exprent).getLstOperands().get(2), (SFormsFastMapDirect[])object4, statement, bl);
                                SSAUConstructorSparseEx.mergeMaps(object22[0], (SFormsFastMapDirect)object4[0]);
                                object22[1] = null;
                                bl2 = true;
                                break block0;
                            }
                            case 48: {
                                this.processExprent((Exprent)((FunctionExprent)exprent).getLstOperands().get(0), (SFormsFastMapDirect[])object22, statement, bl);
                                SFormsFastMapDirect[] sFormsFastMapDirectArray = new SFormsFastMapDirect[2];
                                sFormsFastMapDirectArray[0] = new SFormsFastMapDirect(object22[0]);
                                object3 = sFormsFastMapDirectArray;
                                this.processExprent((Exprent)((FunctionExprent)exprent).getLstOperands().get(1), (SFormsFastMapDirect[])object3, statement, bl);
                                object22[1] = SSAUConstructorSparseEx.mergeMaps(object22[object22[1] == null ? 0 : 1], object3[object3[1] == null ? 0 : 1]);
                                object22[0] = object3[0];
                                bl2 = true;
                                break block0;
                            }
                            case 49: {
                                this.processExprent((Exprent)((FunctionExprent)exprent).getLstOperands().get(0), (SFormsFastMapDirect[])object22, statement, bl);
                                SFormsFastMapDirect[] sFormsFastMapDirectArray = new SFormsFastMapDirect[2];
                                sFormsFastMapDirectArray[0] = new SFormsFastMapDirect(object22[object22[1] == null ? 0 : 1]);
                                object7 = sFormsFastMapDirectArray;
                                this.processExprent((Exprent)((FunctionExprent)exprent).getLstOperands().get(1), (SFormsFastMapDirect[])object7, statement, bl);
                                object22[1] = object7[object7[1] == null ? 0 : 1];
                                object22[0] = SSAUConstructorSparseEx.mergeMaps(object22[0], object7[0]);
                                bl2 = true;
                            }
                        }
                    }
                }
                if (!bl2) {
                    object5 = ((Exprent)object).getAllExprents();
                    object5.remove(object6);
                    Iterator iterator = object5.iterator();
                    while (iterator.hasNext()) {
                        exprent = (Exprent)iterator.next();
                        this.processExprent(exprent, (SFormsFastMapDirect[])object22, statement, bl);
                    }
                }
                object5 = object22[0];
                if (((Exprent)object).type == 5) {
                    int n;
                    if (this.mapFieldVars.containsKey(((Exprent)object).id)) {
                        n = (Integer)this.mapFieldVars.get(((Exprent)object).id);
                    } else {
                        n = this.fieldvarcounter--;
                        this.mapFieldVars.put(((Exprent)object).id, n);
                        this.ssuversions.createNode(new VarVersionPaar(n, 1));
                    }
                    this.setCurrentVar((SFormsFastMapDirect)object5, n, 1);
                } else if (((Exprent)object).type == 8 || ((Exprent)object).type == 2 && ((AssignmentExprent)object).getLeft().type == 5 || ((Exprent)object).type == 10 && ((NewExprent)object).getNewtype().type == 8 || ((Exprent)object).type == 6) {
                    boolean bl3 = true;
                    if (((Exprent)object).type == 6) {
                        bl3 = false;
                        FunctionExprent functionExprent = (FunctionExprent)object;
                        if (functionExprent.getFunctype() >= 32 && functionExprent.getFunctype() <= 35 && ((Exprent)functionExprent.getLstOperands().get((int)0)).type == 5) {
                            bl3 = true;
                        }
                    }
                    if (bl3) {
                        ((SFormsFastMapDirect)object5).removeAllFields();
                    }
                }
                if (object6 != null) {
                    Integer n = ((VarExprent)object6).getIndex0();
                    if (((VarExprent)object6).getVersion() == 0) {
                        Integer n2 = this.getNextFreeVersion(n, statement);
                        ((VarExprent)object6).setVersion(n2);
                        this.ssuversions.createNode(new VarVersionPaar(n, n2));
                        this.setCurrentVar((SFormsFastMapDirect)object5, n, n2);
                        return;
                    }
                    if (bl) {
                        this.varMapToGraph(new VarVersionPaar((int)n, ((VarExprent)object6).getVersion()), (SFormsFastMapDirect)object5);
                    }
                    this.setCurrentVar((SFormsFastMapDirect)object5, n, ((VarExprent)object6).getVersion());
                    return;
                }
                if (((Exprent)object).type != 6) break block43;
                FunctionExprent functionExprent = (FunctionExprent)object;
                switch (functionExprent.getFunctype()) {
                    case 32: 
                    case 33: 
                    case 34: 
                    case 35: {
                        if (((Exprent)functionExprent.getLstOperands().get((int)0)).type != 12) break block44;
                        VarExprent varExprent = (VarExprent)functionExprent.getLstOperands().get(0);
                        object4 = varExprent.getIndex0();
                        object3 = new VarVersionPaar(((Integer)object4).intValue(), varExprent.getVersion());
                        object7 = null;
                        if ((VarVersionPaar)this.phantomppnodes.get(object3) == null) {
                            object = this.getNextFreeVersion((Integer)object4, null);
                            object7 = new VarVersionPaar((Integer)object4, (Integer)object);
                            this.ssuversions.createNode((VarVersionPaar)object7);
                            object22 = (VarVersionNode)this.ssuversions.nodes.getWithKey(object3);
                            object6 = this.factory.spawnEmptySet();
                            if (((VarVersionNode)object22).preds.size() == 1) {
                                ((FastSparseSetFactory$FastSparseSet)object6).add(((VarVersionEdge)object22.preds.iterator().next()).source.version);
                            } else {
                                for (Object object22 : ((VarVersionNode)object22).preds) {
                                    ((FastSparseSetFactory$FastSparseSet)object6).add(((VarVersionEdge)object22.source.preds.iterator().next()).source.version);
                                }
                            }
                            ((FastSparseSetFactory$FastSparseSet)object6).add(object);
                            this.createOrUpdatePhiNode((VarVersionPaar)object3, (FastSparseSetFactory$FastSparseSet)object6, statement);
                            this.phantomppnodes.put(object3, object7);
                        }
                        if (bl) {
                            this.varMapToGraph((VarVersionPaar)object3, (SFormsFastMapDirect)object5);
                        }
                        this.setCurrentVar((SFormsFastMapDirect)object5, (Integer)object4, varExprent.getVersion());
                    }
                    default: {
                        return;
                    }
                    {
                    }
                }
            }
            if (((Exprent)object).type == 12) {
                Object object8 = (VarExprent)object;
                Integer n = ((VarExprent)object8).getIndex0();
                object4 = Integer.valueOf(((VarExprent)object8).getVersion());
                object3 = ((SFormsFastMapDirect)object5).get(n);
                int n3 = ((FastSparseSetFactory$FastSparseSet)object3).getCardinality();
                if (n3 == 1) {
                    if ((Integer)object4 != 0) {
                        if (bl) {
                            this.varMapToGraph(new VarVersionPaar(n, (Integer)object4), (SFormsFastMapDirect)object5);
                        }
                        this.setCurrentVar((SFormsFastMapDirect)object5, n, (Integer)object4);
                        return;
                    }
                    object = this.getNextFreeVersion(n, statement);
                    ((VarExprent)object8).setVersion((Integer)object);
                    this.setCurrentVar((SFormsFastMapDirect)object5, n, (Integer)object);
                    object22 = (Integer)((FastSparseSetFactory$FastSparseSet)object3).iterator().next();
                    object6 = (VarVersionNode)this.ssuversions.nodes.getWithKey(new VarVersionPaar(n, (Integer)object22));
                    object22 = this.ssuversions.createNode(new VarVersionPaar(n, (Integer)object));
                    object8 = new VarVersionEdge(0, (VarVersionNode)object6, (VarVersionNode)object22);
                    ((VarVersionNode)object6).addSuccessor((VarVersionEdge)object8);
                    ((VarVersionNode)object22).addPredecessor((VarVersionEdge)object8);
                    return;
                }
                if (n3 == 2) {
                    if ((Integer)object4 != 0) {
                        if (bl) {
                            this.varMapToGraph(new VarVersionPaar(n, (Integer)object4), (SFormsFastMapDirect)object5);
                        }
                        this.setCurrentVar((SFormsFastMapDirect)object5, n, (Integer)object4);
                    } else {
                        object = this.getNextFreeVersion(n, statement);
                        ((VarExprent)object8).setVersion((Integer)object);
                        this.ssuversions.createNode(new VarVersionPaar(n, (Integer)object));
                        this.setCurrentVar((SFormsFastMapDirect)object5, n, (Integer)object);
                        object4 = object;
                    }
                    this.createOrUpdatePhiNode(new VarVersionPaar(n, (Integer)object4), (FastSparseSetFactory$FastSparseSet)object3, statement);
                }
            }
        }
    }

    private void createOrUpdatePhiNode(VarVersionPaar varVersionPaar, FastSparseSetFactory$FastSparseSet object, Statement object2) {
        Object object3 = ((FastSparseSetFactory$FastSparseSet)object).getCopy();
        HashSet<Integer> hashSet = new HashSet<Integer>();
        VarVersionNode varVersionNode = (VarVersionNode)this.ssuversions.nodes.getWithKey(varVersionPaar);
        ArrayList arrayList2 = new ArrayList(varVersionNode.preds);
        if (arrayList2.size() == 1) {
            arrayList2 = (VarVersionEdge)arrayList2.get(0);
            ((VarVersionEdge)arrayList2).source.removeSuccessor((VarVersionEdge)((Object)arrayList2));
            varVersionNode.removePredecessor((VarVersionEdge)((Object)arrayList2));
        } else {
            for (ArrayList arrayList2 : arrayList2) {
                int n = ((VarVersionEdge)arrayList2.source.preds.iterator().next()).source.version;
                if (!((FastSparseSetFactory$FastSparseSet)object).contains(n)) {
                    ((VarVersionEdge)arrayList2).source.removeSuccessor((VarVersionEdge)((Object)arrayList2));
                    varVersionNode.removePredecessor((VarVersionEdge)((Object)arrayList2));
                    continue;
                }
                ((FastSparseSetFactory$FastSparseSet)object3).remove(n);
                hashSet.add(n);
            }
        }
        arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        object = ((FastSparseSetFactory$FastSparseSet)object3).iterator();
        while (object.hasNext()) {
            Integer n = (Integer)object.next();
            object3 = (VarVersionNode)this.ssuversions.nodes.getWithKey(new VarVersionPaar(varVersionPaar.var, (int)n));
            n = this.getNextFreeVersion(varVersionPaar.var, (Statement)object2);
            VarVersionNode varVersionNode2 = new VarVersionNode(varVersionPaar.var, n);
            arrayList2.add(varVersionNode2);
            arrayList3.add(new VarVersionPaar(varVersionPaar.var, (int)n));
            VarVersionEdge varVersionEdge = new VarVersionEdge(0, (VarVersionNode)object3, varVersionNode2);
            ((VarVersionNode)object3).addSuccessor(varVersionEdge);
            varVersionNode2.addPredecessor(varVersionEdge);
            varVersionEdge = new VarVersionEdge(0, varVersionNode2, varVersionNode);
            varVersionNode2.addSuccessor(varVersionEdge);
            varVersionNode.addPredecessor(varVersionEdge);
            hashSet.add(n);
        }
        object3 = arrayList3;
        object2 = arrayList2;
        this.ssuversions.nodes.addAllWithKey((Collection)object2, (Collection)object3);
        this.phi.put(varVersionPaar, hashSet);
    }

    private void varMapToGraph(VarVersionPaar varVersionPaar, SFormsFastMapDirect sFormsFastMapDirect) {
        ((VarVersionNode)this.ssuversions.nodes.getWithKey((Object)varVersionPaar)).live = new SFormsFastMapDirect(sFormsFastMapDirect);
    }

    private Integer getNextFreeVersion(Integer n, Statement object) {
        Integer n2 = (Integer)this.lastversion.get(n);
        n2 = n2 == null ? new Integer(1) : new Integer(n2 + 1);
        this.lastversion.put(n, n2);
        if (object != null && (object = SSAUConstructorSparseEx.getFirstProtectedRange((Statement)object)) != null) {
            this.mapVersionFirstRange.put(new VarVersionPaar(n, n2), object);
        }
        return n2;
    }

    private SFormsFastMapDirect getFilteredOutMap(String object, String string, DirectGraph object2, String object3) {
        SFormsFastMapDirect sFormsFastMapDirect = new SFormsFastMapDirect();
        boolean bl = ((DirectGraph)object2).mapShortRangeFinallyPaths.containsKey(string);
        if (((String)object).equals(((DirectGraph)object2).mapNegIfBranch.get(string))) {
            if (this.outNegVarVersions.containsKey(string)) {
                sFormsFastMapDirect = ((SFormsFastMapDirect)this.outNegVarVersions.get(string)).getCopy();
            }
        } else if (this.outVarVersions.containsKey(string)) {
            sFormsFastMapDirect = ((SFormsFastMapDirect)this.outVarVersions.get(string)).getCopy();
        }
        if (bl) {
            HashMap hashMap2;
            Object object4 = sFormsFastMapDirect.getCopy();
            Object object5 = new SFormsFastMapDirect();
            String string2 = (String)((DirectGraph)object2).mapFinallyMonitorExceptionPathExits.get(string);
            boolean bl2 = string2 != null && !((String)object).equals(string2);
            Object object6 = new HashSet<String>();
            Object object7 = ((DirectGraph)object2).mapLongRangeFinallyPaths.values().iterator();
            while (object7.hasNext()) {
                hashMap2 = null;
                for (Object object8 : (List)object7.next()) {
                    ((HashSet)object6).add(String.valueOf(((FlattenStatementsHelper$FinallyPathWrapper)object8).destination) + "##" + ((FlattenStatementsHelper$FinallyPathWrapper)object8).source);
                }
            }
            for (HashMap hashMap2 : (List)((DirectGraph)object2).mapShortRangeFinallyPaths.get(string)) {
                Object object8;
                boolean bl3 = ((DirectGraph)object2).mapShortRangeFinallyPaths.containsKey(((FlattenStatementsHelper$FinallyPathWrapper)hashMap2).source);
                object8 = bl3 ? this.getFilteredOutMap(((FlattenStatementsHelper$FinallyPathWrapper)hashMap2).entry, ((FlattenStatementsHelper$FinallyPathWrapper)hashMap2).source, (DirectGraph)object2, (String)object3) : (((FlattenStatementsHelper$FinallyPathWrapper)hashMap2).entry.equals(((DirectGraph)object2).mapNegIfBranch.get(((FlattenStatementsHelper$FinallyPathWrapper)hashMap2).source)) ? (SFormsFastMapDirect)this.outNegVarVersions.get(((FlattenStatementsHelper$FinallyPathWrapper)hashMap2).source) : (SFormsFastMapDirect)this.outVarVersions.get(((FlattenStatementsHelper$FinallyPathWrapper)hashMap2).source));
                boolean bl4 = false;
                if (bl3) {
                    bl4 = !((FlattenStatementsHelper$FinallyPathWrapper)hashMap2).destination.equals(object);
                } else {
                    boolean bl5 = bl4 = !((HashSet)object6).contains(String.valueOf(object3) + "##" + ((FlattenStatementsHelper$FinallyPathWrapper)hashMap2).source);
                }
                if (bl4) {
                    ((SFormsFastMapDirect)object4).complement((SFormsFastMapDirect)object8);
                    continue;
                }
                if (((SFormsFastMapDirect)object5).isEmpty()) {
                    if (object8 == null) continue;
                    object5 = ((SFormsFastMapDirect)object8).getCopy();
                    continue;
                }
                SSAUConstructorSparseEx.mergeMaps((SFormsFastMapDirect)object5, (SFormsFastMapDirect)object8);
            }
            if (bl2) {
                sFormsFastMapDirect = object5;
            } else {
                ((SFormsFastMapDirect)object4).union((SFormsFastMapDirect)object5);
                sFormsFastMapDirect.intersection((SFormsFastMapDirect)object4);
                if (!((SFormsFastMapDirect)object5).isEmpty() && !sFormsFastMapDirect.isEmpty()) {
                    hashMap2 = (HashMap)this.phantomexitnodes.get(string);
                    if (hashMap2 == null) {
                        hashMap2 = new HashMap();
                    }
                    object7 = sFormsFastMapDirect.getCopy();
                    ((SFormsFastMapDirect)object7).complement((SFormsFastMapDirect)object5);
                    for (Object object8 : ((SFormsFastMapDirect)object7).entryList()) {
                        for (Integer n : (FastSparseSetFactory$FastSparseSet)object8.getValue()) {
                            object2 = (Integer)object8.getKey();
                            object3 = new VarVersionPaar((Integer)object2, n);
                            object2 = sFormsFastMapDirect.get((Integer)object2);
                            ((FastSparseSetFactory$FastSparseSet)object2).remove(n);
                            object4 = (VarVersionPaar)hashMap2.get(object3);
                            if (object4 == null) {
                                object4 = this.getNextFreeVersion(((VarVersionPaar)object3).var, null);
                                object4 = new VarVersionPaar(((VarVersionPaar)object3).var, ((Integer)object4).intValue());
                                object5 = (VarVersionNode)this.ssuversions.nodes.getWithKey(object3);
                                VarVersionNode varVersionNode = this.ssuversions.createNode((VarVersionPaar)object4);
                                varVersionNode.flags |= 2;
                                object6 = new VarVersionEdge(1, (VarVersionNode)object5, varVersionNode);
                                ((VarVersionNode)object5).addSuccessor((VarVersionEdge)object6);
                                varVersionNode.addPredecessor((VarVersionEdge)object6);
                                hashMap2.put(object3, object4);
                            }
                            ((FastSparseSetFactory$FastSparseSet)object2).add(((VarVersionPaar)object4).version);
                        }
                    }
                    if (!hashMap2.isEmpty()) {
                        this.phantomexitnodes.put(string, hashMap2);
                    }
                }
            }
        }
        return sFormsFastMapDirect;
    }

    private static SFormsFastMapDirect mergeMaps(SFormsFastMapDirect sFormsFastMapDirect, SFormsFastMapDirect sFormsFastMapDirect2) {
        if (sFormsFastMapDirect2 != null && !sFormsFastMapDirect2.isEmpty()) {
            sFormsFastMapDirect.union(sFormsFastMapDirect2);
        }
        return sFormsFastMapDirect;
    }

    private static boolean mapsEqual(SFormsFastMapDirect sFormsFastMapDirect, SFormsFastMapDirect object2) {
        if (sFormsFastMapDirect == null) {
            return object2 == null;
        }
        if (object2 == null) {
            return false;
        }
        if (sFormsFastMapDirect.size() != ((SFormsFastMapDirect)object2).size()) {
            return false;
        }
        for (Object object2 : ((SFormsFastMapDirect)object2).entryList()) {
            if (InterpreterUtil.equalObjects(sFormsFastMapDirect.get((Integer)object2.getKey()), object2.getValue())) continue;
            return false;
        }
        return true;
    }

    private void setCurrentVar(SFormsFastMapDirect sFormsFastMapDirect, Integer n, Integer n2) {
        FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet = ((SSAUConstructorSparseEx)fastSparseSetFactory$FastSparseSet).factory.spawnEmptySet();
        fastSparseSetFactory$FastSparseSet.add(n2);
        sFormsFastMapDirect.putInternal(n, fastSparseSetFactory$FastSparseSet);
    }

    private void setCatchMaps(Statement statement, DirectGraph directGraph, FlattenStatementsHelper flattenStatementsHelper) {
        switch (statement.type) {
            case 7: 
            case 12: {
                Object object = statement.type == 12 ? ((CatchAllStatement)statement).getVars() : ((CatchStatement)statement).getVars();
                int n = 1;
                while (n < statement.getStats().size()) {
                    int n2 = ((VarExprent)object.get(n - 1)).getIndex0();
                    int n3 = this.getNextFreeVersion(n2, statement);
                    SFormsFastMapDirect sFormsFastMapDirect = new SFormsFastMapDirect();
                    this.setCurrentVar(sFormsFastMapDirect, n2, n3);
                    this.extraVarVersions.put(((DirectNode)directGraph.nodes.getWithKey((Object)((String[])flattenStatementsHelper.getMapDestinationNodes().get((Object)((Statement)statement.getStats().get((int)n)).id))[0])).id, sFormsFastMapDirect);
                    this.ssuversions.createNode(new VarVersionPaar(n2, n3));
                    ++n;
                }
                break;
            }
        }
        for (Object object : statement.getStats()) {
            this.setCatchMaps((Statement)object, directGraph, flattenStatementsHelper);
        }
    }

    private SFormsFastMapDirect createFirstMap(StructMethod object, RootStatement rootStatement) {
        boolean bl = (((StructMethod)object).getAccessFlags() & 8) == 0;
        object = MethodDescriptor.parseDescriptor(((StructMethod)object).getDescriptor());
        int n = ((MethodDescriptor)object).params.length + (bl ? 1 : 0);
        int n2 = 0;
        SFormsFastMapDirect sFormsFastMapDirect = new SFormsFastMapDirect();
        int n3 = 0;
        while (n3 < n) {
            int n4 = this.getNextFreeVersion(n2, rootStatement);
            FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet = this.factory.spawnEmptySet();
            fastSparseSetFactory$FastSparseSet.add(n4);
            sFormsFastMapDirect.putInternal(n2, fastSparseSetFactory$FastSparseSet);
            this.ssuversions.createNode(new VarVersionPaar(n2, n4));
            n2 = bl ? (n3 == 0 ? ++n2 : (n2 += object.params[n3 - 1].stack_size)) : (n2 += object.params[n3].stack_size);
            ++n3;
        }
        return sFormsFastMapDirect;
    }

    private static Integer getFirstProtectedRange(Statement statement) {
        Statement statement2;
        while ((statement2 = statement.getParent()) != null) {
            if (statement2.type == 12 || statement2.type == 7 ? statement2.getFirst() == statement : statement2.type == 10 && ((SynchronizedStatement)statement2).getBody() == statement) {
                return statement2.id;
            }
            statement = statement2;
        }
        return null;
    }

    public final VarVersionsGraph getSsuversions() {
        return this.ssuversions;
    }

    public final SFormsFastMapDirect getLiveVarVersionsMap(VarVersionPaar varVersionPaar) {
        VarVersionNode varVersionNode = (VarVersionNode)varVersionNode.ssuversions.nodes.getWithKey(varVersionPaar);
        if (varVersionNode != null) {
            return varVersionNode.live;
        }
        return null;
    }

    public final HashMap getMapVersionFirstRange() {
        return this.mapVersionFirstRange;
    }

    public final HashMap getMapFieldVars() {
        return this.mapFieldVars;
    }
}

