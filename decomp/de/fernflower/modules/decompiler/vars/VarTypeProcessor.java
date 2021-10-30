/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.vars;

import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.exps.AssignmentExprent;
import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FunctionExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph;
import de.fernflower.modules.decompiler.stats.CatchAllStatement;
import de.fernflower.modules.decompiler.stats.CatchStatement;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.vars.CheckTypesResult;
import de.fernflower.modules.decompiler.vars.CheckTypesResult$ExprentTypePair;
import de.fernflower.modules.decompiler.vars.VarTypeProcessor$1;
import de.fernflower.modules.decompiler.vars.VarTypeProcessor$2;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.gen.MethodDescriptor;
import de.fernflower.struct.gen.VarType;
import java.util.HashMap;
import java.util.LinkedList;

public final class VarTypeProcessor {
    private HashMap mapExprentMinTypes = new HashMap();
    private HashMap mapExprentMaxTypes = new HashMap();
    private HashMap mapFinalVars = new HashMap();

    public final void setInitVars(RootStatement object, DirectGraph directGraph) {
        Object object2 = object;
        object = this;
        Object var4_4 = null;
        boolean bl = (((StructMethod)DecompilerContext.getProperty("CURRENT_METHOD")).getAccessFlags() & 8) == 0;
        Object object3 = (MethodDescriptor)DecompilerContext.getProperty("CURRENT_METHOD_DESCRIPTOR");
        if (bl) {
            VarType varType = new VarType(8, 0, ((StructClass)DecompilerContext.getProperty((String)"CURRENT_CLASS")).qualifiedName);
            ((VarTypeProcessor)object).mapExprentMinTypes.put(new VarVersionPaar(0, 1), varType);
            ((VarTypeProcessor)object).mapExprentMaxTypes.put(new VarVersionPaar(0, 1), varType);
        }
        int n = 0;
        int n2 = 0;
        while (n2 < ((MethodDescriptor)object3).params.length) {
            ((VarTypeProcessor)object).mapExprentMinTypes.put(new VarVersionPaar(n + (bl ? 1 : 0), 1), ((MethodDescriptor)object3).params[n2]);
            ((VarTypeProcessor)object).mapExprentMaxTypes.put(new VarVersionPaar(n + (bl ? 1 : 0), 1), ((MethodDescriptor)object3).params[n2]);
            n += object3.params[n2].stack_size;
            ++n2;
        }
        LinkedList<RootStatement> linkedList = new LinkedList<RootStatement>();
        linkedList.add((RootStatement)object2);
        while (!linkedList.isEmpty()) {
            object2 = (Statement)linkedList.removeFirst();
            Object object4 = null;
            if (((Statement)object2).type == 12) {
                object4 = ((CatchAllStatement)object2).getVars();
            } else if (((Statement)object2).type == 7) {
                object4 = ((CatchStatement)object2).getVars();
            }
            if (object4 != null) {
                object3 = object4.iterator();
                while (object3.hasNext()) {
                    object4 = (VarExprent)object3.next();
                    ((VarTypeProcessor)object).mapExprentMinTypes.put(new VarVersionPaar(((VarExprent)object4).getIndex0(), 1), ((VarExprent)object4).getVartype());
                    ((VarTypeProcessor)object).mapExprentMaxTypes.put(new VarVersionPaar(((VarExprent)object4).getIndex0(), 1), ((VarExprent)object4).getVartype());
                }
            }
            linkedList.addAll(((Statement)object2).getStats());
        }
        directGraph.iterateExprents(new VarTypeProcessor$1());
        while (!((DirectGraph)(object2 = directGraph)).iterateExprents(new VarTypeProcessor$2((VarTypeProcessor)(object = this)))) {
        }
    }

    private boolean checkTypeExprent(Exprent object4) {
        Object object2;
        Object object32 = ((Exprent)object4).getAllExprents().iterator();
        while (object32.hasNext()) {
            object2 = (Exprent)object32.next();
            if (this.checkTypeExprent((Exprent)object2)) continue;
            return false;
        }
        if (((Exprent)object4).type == 3) {
            object2 = (ConstExprent)object4;
            if (object2.getConsttype().type_family <= 2 && !this.mapExprentMinTypes.containsKey(object32 = new VarVersionPaar(((ConstExprent)object2).id, -1))) {
                this.mapExprentMinTypes.put(object32, ((ConstExprent)object2).getConsttype());
            }
        }
        object2 = ((Exprent)object4).checkExprTypeBounds();
        for (Object object32 : ((CheckTypesResult)object2).getLstMaxTypeExprents()) {
            if (object32.type.type_family == 6) continue;
            this.changeExprentType(((CheckTypesResult$ExprentTypePair)object32).exprent, ((CheckTypesResult$ExprentTypePair)object32).type, 1);
        }
        boolean bl = true;
        for (Object object4 : ((CheckTypesResult)object2).getLstMinTypeExprents()) {
            bl &= this.changeExprentType(((CheckTypesResult$ExprentTypePair)object4).exprent, ((CheckTypesResult$ExprentTypePair)object4).type, 0);
        }
        return bl;
    }

    private boolean changeExprentType(Exprent exprent, VarType object, int n) {
        boolean bl = true;
        block0 : switch (exprent.type) {
            case 3: {
                Object object2 = null;
                object2 = ((ConstExprent)exprent).getConsttype();
                if (((VarType)object).type_family > 2 || ((VarType)object2).type_family > 2) {
                    return true;
                }
                if (((VarType)object).type_family == 2 && ((VarType)(object2 = new ConstExprent((Integer)((ConstExprent)exprent).getValue(), false).getConsttype())).isStrictSuperset((VarType)object)) {
                    object = object2;
                }
            }
            case 12: {
                Object object2 = null;
                if (exprent.type == 3) {
                    object2 = new VarVersionPaar(((ConstExprent)exprent).id, -1);
                } else if (exprent.type == 12) {
                    object2 = new VarVersionPaar((VarExprent)exprent);
                }
                if (n == 0) {
                    VarType varType = (VarType)this.mapExprentMinTypes.get(object2);
                    if (varType != null && ((VarType)object).type_family <= varType.type_family) {
                        if (((VarType)object).type_family < varType.type_family) {
                            return true;
                        }
                        object = VarType.getCommonSupertype(varType, (VarType)object);
                    }
                    this.mapExprentMinTypes.put(object2, object);
                    if (exprent.type == 3) {
                        ((ConstExprent)exprent).setConsttype((VarType)object);
                    }
                    if (varType == null || ((VarType)object).type_family <= varType.type_family && !((VarType)object).isStrictSuperset(varType)) break;
                    return false;
                }
                VarType varType = (VarType)this.mapExprentMaxTypes.get(object2);
                if (varType != null && ((VarType)object).type_family >= varType.type_family) {
                    if (((VarType)object).type_family > varType.type_family) {
                        return true;
                    }
                    object = VarType.getCommonMinType(varType, (VarType)object);
                }
                this.mapExprentMaxTypes.put(object2, object);
                break;
            }
            case 2: {
                return this.changeExprentType(((AssignmentExprent)exprent).getRight(), (VarType)object, n);
            }
            case 6: {
                FunctionExprent functionExprent = (FunctionExprent)exprent;
                switch (functionExprent.getFunctype()) {
                    case 36: {
                        bl = true & this.changeExprentType((Exprent)functionExprent.getLstOperands().get(1), (VarType)object, n) & this.changeExprentType((Exprent)functionExprent.getLstOperands().get(2), (VarType)object, n);
                        break block0;
                    }
                    case 4: 
                    case 5: 
                    case 6: {
                        bl = true & this.changeExprentType((Exprent)functionExprent.getLstOperands().get(0), (VarType)object, n) & this.changeExprentType((Exprent)functionExprent.getLstOperands().get(1), (VarType)object, n);
                    }
                }
            }
        }
        return bl;
    }

    public final HashMap getMapExprentMaxTypes() {
        return this.mapExprentMaxTypes;
    }

    public final HashMap getMapExprentMinTypes() {
        return this.mapExprentMinTypes;
    }

    public final HashMap getMapFinalVars() {
        return this.mapFinalVars;
    }

    public final void setVarType(VarVersionPaar varVersionPaar, VarType varType) {
        this.mapExprentMinTypes.put(varVersionPaar, varType);
    }

    public final VarType getVarType(VarVersionPaar varVersionPaar) {
        return (VarType)this.mapExprentMinTypes.get(varVersionPaar);
    }

    static /* synthetic */ boolean access$0(VarTypeProcessor varTypeProcessor, Exprent exprent) {
        return varTypeProcessor.checkTypeExprent(exprent);
    }
}

