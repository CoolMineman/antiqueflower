/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.sforms;

import de.fernflower.modules.decompiler.exps.AssignmentExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FunctionExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph;
import de.fernflower.modules.decompiler.sforms.DirectNode;
import de.fernflower.modules.decompiler.sforms.FlattenStatementsHelper;
import de.fernflower.modules.decompiler.sforms.FlattenStatementsHelper$FinallyPathWrapper;
import de.fernflower.modules.decompiler.stats.CatchAllStatement;
import de.fernflower.modules.decompiler.stats.CatchStatement;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.gen.MethodDescriptor;
import de.fernflower.util.FastSparseSetFactory;
import de.fernflower.util.FastSparseSetFactory$FastSparseSet;
import de.fernflower.util.InterpreterUtil;
import de.fernflower.util.SFormsFastMapDirect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public final class SSAConstructorSparseEx {
    private HashMap inVarVersions = new HashMap();
    private HashMap outVarVersions = new HashMap();
    private HashMap outNegVarVersions = new HashMap();
    private HashMap extraVarVersions = new HashMap();
    private HashMap phi = new HashMap();
    private HashMap lastversion = new HashMap();
    private List startVars = new ArrayList();
    private FastSparseSetFactory factory;

    public final void splitVariables(RootStatement object, StructMethod structMethod) {
        FlattenStatementsHelper flattenStatementsHelper = new FlattenStatementsHelper();
        DirectGraph directGraph = flattenStatementsHelper.buildDirectGraph((RootStatement)object);
        HashSet<Integer> hashSet = new HashSet<Integer>();
        int n = 0;
        while (n < 64) {
            hashSet.add(n);
            ++n;
        }
        this.factory = new FastSparseSetFactory(hashSet);
        SFormsFastMapDirect sFormsFastMapDirect = this.createFirstMap(structMethod);
        this.extraVarVersions.put(directGraph.first.id, sFormsFastMapDirect);
        this.setCatchMaps((Statement)object, directGraph, flattenStatementsHelper);
        object = new HashSet();
        do {
            this.ssaStatements(directGraph, (HashSet)object);
        } while (!((HashSet)object).isEmpty());
    }

    private void ssaStatements(DirectGraph directGraph, HashSet hashSet) {
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
                SSAConstructorSparseEx.mergeMaps(sFormsFastMapDirect, (SFormsFastMapDirect)object4);
            }
            if (sFormsFastMapDirectArray.extraVarVersions.containsKey(((DirectNode)object32).id)) {
                Object object4;
                object4 = (SFormsFastMapDirect)sFormsFastMapDirectArray.extraVarVersions.get(((DirectNode)object32).id);
                if (sFormsFastMapDirect.isEmpty()) {
                    sFormsFastMapDirect = ((SFormsFastMapDirect)object4).getCopy();
                } else {
                    SSAConstructorSparseEx.mergeMaps(sFormsFastMapDirect, (SFormsFastMapDirect)object4);
                }
            }
            sFormsFastMapDirectArray.inVarVersions.put(((DirectNode)object32).id, sFormsFastMapDirect);
            sFormsFastMapDirectArray = (SFormsFastMapDirect)this.inVarVersions.get(((DirectNode)object).id);
            sFormsFastMapDirectArray = new SFormsFastMapDirect((SFormsFastMapDirect)sFormsFastMapDirectArray);
            SFormsFastMapDirect[] sFormsFastMapDirectArray2 = new SFormsFastMapDirect[2];
            sFormsFastMapDirectArray2[0] = sFormsFastMapDirectArray;
            sFormsFastMapDirectArray = sFormsFastMapDirectArray2;
            if (((DirectNode)object).exprents != null) {
                for (Object object32 : ((DirectNode)object).exprents) {
                    this.processExprent((Exprent)object32, sFormsFastMapDirectArray);
                }
            }
            if (sFormsFastMapDirectArray[1] == null) {
                sFormsFastMapDirectArray[1] = sFormsFastMapDirectArray[0];
            }
            if (!(!SSAConstructorSparseEx.mapsEqual(sFormsFastMapDirectArray[0], (SFormsFastMapDirect)this.outVarVersions.get(((DirectNode)object).id)) || this.outNegVarVersions.containsKey(((DirectNode)object).id) && !SSAConstructorSparseEx.mapsEqual(sFormsFastMapDirectArray[1], (SFormsFastMapDirect)this.outNegVarVersions.get(((DirectNode)object).id)))) continue;
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

    private void processExprent(Exprent object, SFormsFastMapDirect[] object2) {
        Object object3;
        Object object4;
        Object object5;
        if (object == null) {
            return;
        }
        VarExprent varExprent = null;
        boolean bl = false;
        block0 : switch (((Exprent)object).type) {
            case 2: {
                object5 = (AssignmentExprent)object;
                if (((AssignmentExprent)object5).getCondtype() != -1) break;
                object5 = ((AssignmentExprent)object5).getLeft();
                if (((Exprent)object5).type != 12) break;
                varExprent = (VarExprent)object5;
                break;
            }
            case 6: {
                object5 = (FunctionExprent)object;
                switch (((FunctionExprent)object5).getFunctype()) {
                    case 36: {
                        SFormsFastMapDirect sFormsFastMapDirect;
                        this.processExprent((Exprent)((FunctionExprent)object5).getLstOperands().get(0), (SFormsFastMapDirect[])object2);
                        if (object2[1] == null) {
                            sFormsFastMapDirect = new SFormsFastMapDirect(object2[0]);
                        } else {
                            sFormsFastMapDirect = object2[1];
                            object2[1] = null;
                        }
                        this.processExprent((Exprent)((FunctionExprent)object5).getLstOperands().get(1), (SFormsFastMapDirect[])object2);
                        SFormsFastMapDirect[] sFormsFastMapDirectArray = new SFormsFastMapDirect[2];
                        sFormsFastMapDirectArray[0] = sFormsFastMapDirect;
                        object4 = sFormsFastMapDirectArray;
                        this.processExprent((Exprent)((FunctionExprent)object5).getLstOperands().get(2), (SFormsFastMapDirect[])object4);
                        SSAConstructorSparseEx.mergeMaps(object2[0], object4[0]);
                        object2[1] = null;
                        bl = true;
                        break block0;
                    }
                    case 48: {
                        this.processExprent((Exprent)((FunctionExprent)object5).getLstOperands().get(0), (SFormsFastMapDirect[])object2);
                        SFormsFastMapDirect[] sFormsFastMapDirectArray = new SFormsFastMapDirect[2];
                        sFormsFastMapDirectArray[0] = new SFormsFastMapDirect(object2[0]);
                        object3 = sFormsFastMapDirectArray;
                        this.processExprent((Exprent)((FunctionExprent)object5).getLstOperands().get(1), (SFormsFastMapDirect[])object3);
                        object2[1] = SSAConstructorSparseEx.mergeMaps(object2[object2[1] == null ? 0 : 1], (SFormsFastMapDirect)object3[object3[1] == null ? 0 : 1]);
                        object2[0] = object3[0];
                        bl = true;
                        break block0;
                    }
                    case 49: {
                        this.processExprent((Exprent)((FunctionExprent)object5).getLstOperands().get(0), (SFormsFastMapDirect[])object2);
                        SFormsFastMapDirect[] sFormsFastMapDirectArray = new SFormsFastMapDirect[2];
                        sFormsFastMapDirectArray[0] = new SFormsFastMapDirect(object2[object2[1] == null ? 0 : 1]);
                        SFormsFastMapDirect[] sFormsFastMapDirectArray2 = sFormsFastMapDirectArray;
                        this.processExprent((Exprent)((FunctionExprent)object5).getLstOperands().get(1), sFormsFastMapDirectArray2);
                        object2[1] = sFormsFastMapDirectArray2[sFormsFastMapDirectArray2[1] == null ? 0 : 1];
                        object2[0] = SSAConstructorSparseEx.mergeMaps(object2[0], sFormsFastMapDirectArray2[0]);
                        bl = true;
                    }
                }
            }
        }
        if (bl) {
            return;
        }
        object5 = ((Exprent)object).getAllExprents();
        object5.remove(varExprent);
        Object object6 = object5.iterator();
        while (object6.hasNext()) {
            object5 = (Exprent)object6.next();
            this.processExprent((Exprent)object5, (SFormsFastMapDirect[])object2);
        }
        object5 = object2[0];
        if (varExprent != null) {
            object6 = varExprent.getIndex0();
            if (varExprent.getVersion() == 0) {
                object4 = this.getNextFreeVersion((Integer)object6);
                varExprent.setVersion(object4.intValue());
                this.setCurrentVar((SFormsFastMapDirect)object5, (Integer)object6, (Integer)object4);
                return;
            }
            this.setCurrentVar((SFormsFastMapDirect)object5, (Integer)object6, varExprent.getVersion());
            return;
        }
        if (((Exprent)object).type == 12) {
            object6 = (VarExprent)object;
            object4 = ((VarExprent)object6).getIndex0();
            object3 = ((SFormsFastMapDirect)object5).get(object4.intValue());
            int n = ((FastSparseSetFactory$FastSparseSet)object3).getCardinality();
            if (n == 1) {
                object = (Integer)((FastSparseSetFactory$FastSparseSet)object3).iterator().next();
                ((VarExprent)object6).setVersion((Integer)object);
                return;
            }
            if (n == 2) {
                object = ((VarExprent)object6).getVersion();
                object2 = new VarVersionPaar((Integer)object4, (Integer)object);
                if ((Integer)object != 0 && this.phi.containsKey(object2)) {
                    this.setCurrentVar((SFormsFastMapDirect)object5, (Integer)object4, (Integer)object);
                    ((FastSparseSetFactory$FastSparseSet)this.phi.get(object2)).union((FastSparseSetFactory$FastSparseSet)object3);
                    return;
                }
                object = this.getNextFreeVersion((Integer)object4);
                ((VarExprent)object6).setVersion((Integer)object);
                this.setCurrentVar((SFormsFastMapDirect)object5, (Integer)object4, (Integer)object);
                this.phi.put(new VarVersionPaar((Integer)object4, (Integer)object), object3);
            }
        }
    }

    private Integer getNextFreeVersion(Integer n) {
        Integer n2 = (Integer)this.lastversion.get(n);
        n2 = n2 == null ? new Integer(1) : new Integer(n2 + 1);
        this.lastversion.put(n, n2);
        return n2;
    }

    private SFormsFastMapDirect getFilteredOutMap(String string, String object, DirectGraph directGraph, String string2) {
        SFormsFastMapDirect sFormsFastMapDirect = new SFormsFastMapDirect();
        if (string.equals(directGraph.mapNegIfBranch.get(object))) {
            if (this.outNegVarVersions.containsKey(object)) {
                sFormsFastMapDirect = ((SFormsFastMapDirect)this.outNegVarVersions.get(object)).getCopy();
            }
        } else if (this.outVarVersions.containsKey(object)) {
            sFormsFastMapDirect = ((SFormsFastMapDirect)this.outVarVersions.get(object)).getCopy();
        }
        boolean bl = false;
        if (directGraph.mapShortRangeFinallyPaths.containsKey(object) && !sFormsFastMapDirect.isEmpty()) {
            SFormsFastMapDirect sFormsFastMapDirect2 = sFormsFastMapDirect.getCopy();
            SFormsFastMapDirect sFormsFastMapDirect3 = new SFormsFastMapDirect();
            String string3 = (String)directGraph.mapFinallyMonitorExceptionPathExits.get(object);
            boolean bl2 = string3 != null && !string.equals(string3);
            HashSet<String> hashSet = new HashSet<String>();
            for (Object object2 : (List)directGraph.mapLongRangeFinallyPaths.get(object)) {
                hashSet.add(String.valueOf(((FlattenStatementsHelper$FinallyPathWrapper)object2).destination) + "##" + ((FlattenStatementsHelper$FinallyPathWrapper)object2).source);
            }
            for (Object object2 : (List)directGraph.mapShortRangeFinallyPaths.get(object)) {
                boolean bl3 = directGraph.mapShortRangeFinallyPaths.containsKey(((FlattenStatementsHelper$FinallyPathWrapper)object2).source);
                object = bl3 ? this.getFilteredOutMap(((FlattenStatementsHelper$FinallyPathWrapper)object2).entry, ((FlattenStatementsHelper$FinallyPathWrapper)object2).source, directGraph, string2) : (((FlattenStatementsHelper$FinallyPathWrapper)object2).entry.equals(directGraph.mapNegIfBranch.get(((FlattenStatementsHelper$FinallyPathWrapper)object2).source)) ? (SFormsFastMapDirect)this.outNegVarVersions.get(((FlattenStatementsHelper$FinallyPathWrapper)object2).source) : (SFormsFastMapDirect)this.outVarVersions.get(((FlattenStatementsHelper$FinallyPathWrapper)object2).source));
                boolean bl4 = false;
                if (bl3) {
                    bl4 = !((FlattenStatementsHelper$FinallyPathWrapper)object2).destination.equals(string);
                } else {
                    boolean bl5 = bl4 = !hashSet.contains(String.valueOf(string2) + "##" + ((FlattenStatementsHelper$FinallyPathWrapper)object2).source);
                }
                if (bl4) {
                    sFormsFastMapDirect2.complement((SFormsFastMapDirect)object);
                    continue;
                }
                if (sFormsFastMapDirect3.isEmpty()) {
                    if (object == null) continue;
                    sFormsFastMapDirect3 = ((SFormsFastMapDirect)object).getCopy();
                    continue;
                }
                SSAConstructorSparseEx.mergeMaps(sFormsFastMapDirect3, (SFormsFastMapDirect)object);
            }
            if (bl2) {
                sFormsFastMapDirect = sFormsFastMapDirect3;
            } else {
                Object object2;
                sFormsFastMapDirect2.union(sFormsFastMapDirect3);
                object2 = (SFormsFastMapDirect)this.inVarVersions.get(string);
                if (object2 != null) {
                    sFormsFastMapDirect2.union((SFormsFastMapDirect)object2);
                }
                sFormsFastMapDirect.intersection(sFormsFastMapDirect2);
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
        FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet = ((SSAConstructorSparseEx)fastSparseSetFactory$FastSparseSet).factory.spawnEmptySet();
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
                    int n3 = this.getNextFreeVersion(n2);
                    SFormsFastMapDirect sFormsFastMapDirect = new SFormsFastMapDirect();
                    this.setCurrentVar(sFormsFastMapDirect, n2, n3);
                    this.extraVarVersions.put(((DirectNode)directGraph.nodes.getWithKey((Object)((String[])flattenStatementsHelper.getMapDestinationNodes().get((Object)((Statement)statement.getStats().get((int)n)).id))[0])).id, sFormsFastMapDirect);
                    this.startVars.add(new VarVersionPaar(n2, n3));
                    ++n;
                }
                break;
            }
        }
        for (Object object : statement.getStats()) {
            this.setCatchMaps((Statement)object, directGraph, flattenStatementsHelper);
        }
    }

    private SFormsFastMapDirect createFirstMap(StructMethod object) {
        boolean bl = (((StructMethod)object).getAccessFlags() & 8) == 0;
        object = MethodDescriptor.parseDescriptor(((StructMethod)object).getDescriptor());
        int n = ((MethodDescriptor)object).params.length + (bl ? 1 : 0);
        int n2 = 0;
        SFormsFastMapDirect sFormsFastMapDirect = new SFormsFastMapDirect();
        int n3 = 0;
        while (n3 < n) {
            int n4 = this.getNextFreeVersion(n2);
            FastSparseSetFactory$FastSparseSet fastSparseSetFactory$FastSparseSet = this.factory.spawnEmptySet();
            fastSparseSetFactory$FastSparseSet.add(n4);
            sFormsFastMapDirect.putInternal(n2, fastSparseSetFactory$FastSparseSet);
            this.startVars.add(new VarVersionPaar(n2, n4));
            n2 = bl ? (n3 == 0 ? ++n2 : (n2 += object.params[n3 - 1].stack_size)) : (n2 += object.params[n3].stack_size);
            ++n3;
        }
        return sFormsFastMapDirect;
    }

    public final HashMap getPhi() {
        return this.phi;
    }
}

