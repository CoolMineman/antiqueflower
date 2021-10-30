/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.vars;

import de.fernflower.main.DecompilerContext;
import de.fernflower.main.collectors.CounterContainer;
import de.fernflower.modules.decompiler.sforms.DirectGraph;
import de.fernflower.modules.decompiler.sforms.FlattenStatementsHelper;
import de.fernflower.modules.decompiler.sforms.SSAConstructorSparseEx;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.vars.VarTypeProcessor;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.modules.decompiler.vars.VarVersionsProcessor$1;
import de.fernflower.modules.decompiler.vars.VarVersionsProcessor$2;
import de.fernflower.modules.decompiler.vars.VarVersionsProcessor$3;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.FastSparseSetFactory$FastSparseSet;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import net.minecraftforge.lex.fffixer.Util;

public final class VarVersionsProcessor {
    private HashMap mapOriginalVarIndices = new HashMap();
    private VarTypeProcessor typeproc;

    public final void setVarVersions(RootStatement rootStatement) {
        Object object;
        Object object2;
        int n;
        Object object3 = (StructMethod)DecompilerContext.getProperty("CURRENT_METHOD");
        SSAConstructorSparseEx entry2 = new SSAConstructorSparseEx();
        entry2.splitVariables(rootStatement, (StructMethod)object3);
        Object object4 = object3 = new FlattenStatementsHelper().buildDirectGraph(rootStatement);
        SSAConstructorSparseEx sSAConstructorSparseEx = entry2;
        ArrayList<Object> arrayList = new ArrayList<Object>();
        for (Map.Entry entry : sSAConstructorSparseEx.getPhi().entrySet()) {
            HashSet<VarVersionPaar> hashSet = new HashSet<VarVersionPaar>();
            hashSet.add((VarVersionPaar)entry.getKey());
            for (Integer n2 : (FastSparseSetFactory$FastSparseSet)entry.getValue()) {
                hashSet.add(new VarVersionPaar(((VarVersionPaar)entry.getKey()).var, (int)n2));
            }
            n = arrayList.size() - 1;
            while (n >= 0) {
                object2 = (HashSet)arrayList.get(n);
                object = new HashSet(hashSet);
                ((AbstractCollection)object).retainAll((Collection<?>)object2);
                if (!((HashSet)object).isEmpty()) {
                    hashSet.addAll((Collection<VarVersionPaar>)object2);
                    arrayList.remove(n);
                }
                --n;
            }
            arrayList.add(hashSet);
        }
        HashMap<VarVersionPaar, Integer> hashMap = new HashMap<VarVersionPaar, Integer>();
        for (Object object5 : arrayList) {
            n = Integer.MAX_VALUE;
            object = ((HashSet)object5).iterator();
            while (object.hasNext()) {
                object2 = (VarVersionPaar)object.next();
                if (((VarVersionPaar)object2).version >= n) continue;
                n = ((VarVersionPaar)object2).version;
            }
            object = ((HashSet)object5).iterator();
            while (object.hasNext()) {
                object2 = (VarVersionPaar)object.next();
                hashMap.put(new VarVersionPaar(((VarVersionPaar)object2).var, ((VarVersionPaar)object2).version), n);
            }
        }
        ((DirectGraph)object4).iterateExprents(new VarVersionsProcessor$1(hashMap));
        this.typeproc = new VarTypeProcessor();
        this.typeproc.setInitVars(rootStatement, (DirectGraph)object3);
        this.simpleMerge(this.typeproc, (DirectGraph)object3);
        VarVersionsProcessor.eliminateNonJavaTypes(this.typeproc);
        this.setNewVarIndices(this.typeproc, (DirectGraph)object3);
    }

    private static void eliminateNonJavaTypes(VarTypeProcessor object) {
        HashMap hashMap = ((VarTypeProcessor)object).getMapExprentMaxTypes();
        object = ((VarTypeProcessor)object).getMapExprentMinTypes();
        VarVersionPaar varVersionPaar2 = null;
        for (VarVersionPaar varVersionPaar2 : new HashSet(((HashMap)object).keySet())) {
            VarType varType = (VarType)((HashMap)object).get(varVersionPaar2);
            VarType varType2 = (VarType)hashMap.get(varVersionPaar2);
            if (varType.type == 15 || varType.type == 16) {
                varType = varType2 != null && varType2.type == 1 ? VarType.VARTYPE_CHAR : (varType.type == 15 ? VarType.VARTYPE_BYTE : VarType.VARTYPE_SHORT);
                ((HashMap)object).put(varVersionPaar2, varType);
                continue;
            }
            if (varType.type != 13) continue;
            ((HashMap)object).put(varVersionPaar2, VarType.VARTYPE_OBJECT);
        }
    }

    private void simpleMerge(VarTypeProcessor varTypeProcessor, DirectGraph directGraph) {
        Object object2;
        HashMap hashMap = varTypeProcessor.getMapExprentMaxTypes();
        HashMap hashMap2 = varTypeProcessor.getMapExprentMinTypes();
        Cloneable cloneable = new HashMap<Integer, HashSet<Integer>>();
        for (Object object2 : hashMap2.keySet()) {
            if (((VarVersionPaar)object2).version < 0) continue;
            HashSet hashSet = (HashSet)((HashMap)cloneable).get(((VarVersionPaar)object2).var);
            if (hashSet == null) {
                hashSet = new HashSet();
                ((HashMap)cloneable).put(((VarVersionPaar)object2).var, hashSet);
            }
            hashSet.add(((VarVersionPaar)object2).version);
        }
        object2 = new HashMap();
        for (Map.Entry entry : ((HashMap)cloneable).entrySet()) {
            if (((HashSet)entry.getValue()).size() <= 1) continue;
            cloneable = new ArrayList((Collection)entry.getValue());
            Collections.sort(cloneable);
            int n = 0;
            while (n < cloneable.size()) {
                VarVersionPaar varVersionPaar = new VarVersionPaar((Integer)entry.getKey(), (Integer)cloneable.get(n));
                VarType varType = (VarType)hashMap2.get(varVersionPaar);
                int n2 = n + 1;
                while (n2 < cloneable.size()) {
                    VarVersionPaar varVersionPaar2 = new VarVersionPaar((Integer)entry.getKey(), (Integer)cloneable.get(n2));
                    VarType varType2 = (VarType)hashMap2.get(varVersionPaar2);
                    if (varType.equals(varType2) || varType.equals(VarType.VARTYPE_NULL) && varType2.type == 8 || varType2.equals(VarType.VARTYPE_NULL) && varType.type == 8) {
                        VarType varType3 = (VarType)hashMap.get(varVersionPaar);
                        VarType varType4 = (VarType)hashMap.get(varVersionPaar2);
                        hashMap.put(varVersionPaar, varType3 == null ? varType4 : (varType4 == null ? varType3 : VarType.getCommonMinType(varType3, varType4)));
                        ((HashMap)object2).put(varVersionPaar2, varVersionPaar.version);
                        hashMap.remove(varVersionPaar2);
                        hashMap2.remove(varVersionPaar2);
                        if (varType.equals(VarType.VARTYPE_NULL)) {
                            hashMap2.put(varVersionPaar, varType2);
                            varType = varType2;
                        }
                        varTypeProcessor.getMapFinalVars().put(varVersionPaar, 1);
                        cloneable.remove(n2);
                        --n2;
                    }
                    ++n2;
                }
                ++n;
            }
        }
        if (!((HashMap)object2).isEmpty()) {
            directGraph.iterateExprents(new VarVersionsProcessor$2((HashMap)object2));
        }
    }

    private void setNewVarIndices(VarTypeProcessor object, DirectGraph directGraph) {
        HashMap hashMap = ((VarTypeProcessor)object).getMapExprentMaxTypes();
        HashMap hashMap2 = ((VarTypeProcessor)object).getMapExprentMinTypes();
        object = ((VarTypeProcessor)object).getMapFinalVars();
        CounterContainer counterContainer = DecompilerContext.getCountercontainer();
        HashMap<VarVersionPaar, Integer> hashMap3 = new HashMap<VarVersionPaar, Integer>();
        HashMap<Integer, Integer> hashMap4 = new HashMap<Integer, Integer>();
        VarVersionPaar varVersionPaar = null;
        Iterator iterator = new HashSet(hashMap2.keySet()).iterator();
        iterator = Util.sortComparable(iterator);
        while (iterator.hasNext()) {
            varVersionPaar = (VarVersionPaar)iterator.next();
            if (varVersionPaar.version < 0) continue;
            int n = varVersionPaar.version == 1 ? varVersionPaar.var : counterContainer.getCounterAndIncrement(2);
            VarVersionPaar varVersionPaar2 = new VarVersionPaar(n, 0);
            hashMap2.put(varVersionPaar2, (VarType)hashMap2.get(varVersionPaar));
            hashMap.put(varVersionPaar2, (VarType)hashMap.get(varVersionPaar));
            if (((HashMap)object).containsKey(varVersionPaar)) {
                ((HashMap)object).put(varVersionPaar2, (Integer)((HashMap)object).remove(varVersionPaar));
            }
            hashMap3.put(varVersionPaar, n);
            hashMap4.put(n, varVersionPaar.var);
        }
        directGraph.iterateExprents(new VarVersionsProcessor$3(hashMap3, hashMap));
        this.mapOriginalVarIndices = hashMap4;
    }

    public final VarType getVarType(VarVersionPaar varVersionPaar) {
        if (this.typeproc == null) {
            return null;
        }
        return this.typeproc.getVarType(varVersionPaar);
    }

    public final void setVarType(VarVersionPaar varVersionPaar, VarType varType) {
        this.typeproc.setVarType(varVersionPaar, varType);
    }

    public final int getVarFinal(VarVersionPaar varVersionPaar) {
        int n = 3;
        if (((VarVersionsProcessor)n2).typeproc != null) {
            Integer n2 = (Integer)((VarVersionsProcessor)n2).typeproc.getMapFinalVars().get(varVersionPaar);
            n = n2 == null ? 3 : n2;
        }
        return n;
    }

    public final void setVarFinal(VarVersionPaar varVersionPaar) {
        this.typeproc.getMapFinalVars().put(varVersionPaar, 2);
    }

    public final HashMap getMapOriginalVarIndices() {
        return this.mapOriginalVarIndices;
    }
}

