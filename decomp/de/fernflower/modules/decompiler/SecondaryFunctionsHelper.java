/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler;

import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.ConcatenationHelper;
import de.fernflower.modules.decompiler.StatEdge;
import de.fernflower.modules.decompiler.exps.AssignmentExprent;
import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FunctionExprent;
import de.fernflower.modules.decompiler.exps.IfExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.stats.IfStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.vars.VarProcessor;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.gen.VarType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class SecondaryFunctionsHelper {
    private static final int[] funcsnot = new int[]{43, 42, 45, 44, 47, 46, 49, 48};
    private static final HashMap mapNumComparisons = new HashMap<Integer, Integer[]>();

    static {
        mapNumComparisons.put(42, new Integer[]{44, 42, 46});
        mapNumComparisons.put(43, new Integer[]{45, 43, 47});
        Integer[] integerArray = new Integer[3];
        integerArray[0] = 45;
        integerArray[1] = 46;
        mapNumComparisons.put(46, integerArray);
        Integer[] integerArray2 = new Integer[3];
        integerArray2[1] = 45;
        integerArray2[2] = 46;
        mapNumComparisons.put(45, integerArray2);
        Integer[] integerArray3 = new Integer[3];
        integerArray3[1] = 44;
        integerArray3[2] = 47;
        mapNumComparisons.put(44, integerArray3);
        Integer[] integerArray4 = new Integer[3];
        integerArray4[0] = 44;
        integerArray4[1] = 47;
        mapNumComparisons.put(47, integerArray4);
    }

    public static boolean identifySecondaryFunctions(Statement statement) {
        Object object;
        if (statement.getExprents() == null && statement.type == 2) {
            IfStatement ifStatement = (IfStatement)statement;
            object = ifStatement.getIfstat();
            if (ifStatement.iftype == IfStatement.IFTYPE_IFELSE && ((Statement)object).getExprents() != null && ((Statement)object).getExprents().isEmpty() && (((Statement)object).getAllSuccessorEdges().isEmpty() || !((StatEdge)object.getAllSuccessorEdges().get((int)0)).explicit)) {
                ifStatement.getStats().removeWithKey(((Statement)object).id);
                ifStatement.iftype = 0;
                ifStatement.setIfstat(ifStatement.getElsestat());
                ifStatement.setElsestat(null);
                if (ifStatement.getAllSuccessorEdges().isEmpty() && !((Statement)object).getAllSuccessorEdges().isEmpty()) {
                    StatEdge statEdge = (StatEdge)((Statement)object).getAllSuccessorEdges().get(0);
                    ((Statement)object).removeSuccessor(statEdge);
                    statEdge.setSource(ifStatement);
                    if (statEdge.closure != null) {
                        ifStatement.getParent().addLabeledEdge(statEdge);
                    }
                    ifStatement.addSuccessor(statEdge);
                }
                ifStatement.getFirst().removeSuccessor(ifStatement.getIfEdge());
                ifStatement.setIfEdge(ifStatement.getElseEdge());
                ifStatement.setElseEdge(null);
                ifStatement.setNegated(!ifStatement.isNegated());
                ifStatement.getHeadexprentList().set(0, ((IfExprent)ifStatement.getHeadexprent().copy()).negateIf());
                return true;
            }
        }
        boolean bl = true;
        block0: while (bl) {
            bl = false;
            object = new ArrayList(statement.getExprents() == null ? statement.getSequentialObjects() : statement.getExprents());
            int n = 0;
            while (n < object.size()) {
                Exprent exprent;
                Object e = object.get(n);
                if (e instanceof Statement) {
                    if (SecondaryFunctionsHelper.identifySecondaryFunctions((Statement)e)) {
                        bl = true;
                        continue block0;
                    }
                } else if (e instanceof Exprent && (exprent = SecondaryFunctionsHelper.identifySecondaryFunctions((Exprent)e)) != null) {
                    if (statement.getExprents() == null) {
                        statement.replaceExprent((Exprent)e, exprent);
                    } else {
                        statement.getExprents().set(n, exprent);
                    }
                    bl = true;
                    continue block0;
                }
                ++n;
            }
        }
        return false;
    }

    private static Exprent identifySecondaryFunctions(Exprent exprent) {
        Object object;
        Object object2;
        Exprent exprent2;
        Object object3;
        Object object4;
        Object object52;
        if (exprent.type == 6) {
            Object object6 = (FunctionExprent)exprent;
            switch (((FunctionExprent)object6).getFunctype()) {
                case 12: {
                    object52 = SecondaryFunctionsHelper.propagateBoolNot((Exprent)object6);
                    if (object52 == null) break;
                    return object52;
                }
                case 42: 
                case 43: 
                case 44: 
                case 45: 
                case 46: 
                case 47: {
                    int n;
                    object4 = (Exprent)((FunctionExprent)object6).getLstOperands().get(0);
                    object3 = (Exprent)((FunctionExprent)object6).getLstOperands().get(1);
                    if (((Exprent)object4).type == 3) {
                        object3 = object4;
                        object4 = (Exprent)((FunctionExprent)object6).getLstOperands().get(1);
                    }
                    if (((Exprent)object4).type != 6 || ((Exprent)object3).type != 3) break;
                    exprent2 = (FunctionExprent)object4;
                    object2 = (ConstExprent)object3;
                    int n2 = ((FunctionExprent)exprent2).getFunctype();
                    if (n2 != 37 && n2 != 39 && n2 != 38 && n2 != 41 && n2 != 40) break;
                    int n3 = -1;
                    object = (Integer[])mapNumComparisons.get(((FunctionExprent)object6).getFunctype());
                    if (object != null && (n = ((ConstExprent)object2).getIntValue() + 1) >= 0 && n <= 2 && (object6 = object[n]) != null) {
                        n3 = (Integer)object6;
                    }
                    if (n3 < 0) break;
                    return new FunctionExprent(n3, ((FunctionExprent)exprent2).getLstOperands());
                }
            }
        }
        boolean bl = true;
        block20: while (bl) {
            bl = false;
            for (Object object52 : exprent.getAllExprents()) {
                object3 = SecondaryFunctionsHelper.identifySecondaryFunctions((Exprent)object52);
                if (object3 == null) continue;
                exprent.replaceExprent((Exprent)object52, (Exprent)object3);
                bl = true;
                continue block20;
            }
        }
        block4 : switch (exprent.type) {
            case 6: {
                object52 = (FunctionExprent)exprent;
                object4 = ((FunctionExprent)object52).getLstOperands();
                switch (((FunctionExprent)object52).getFunctype()) {
                    case 6: {
                        int n = 0;
                        while (n < 2) {
                            exprent2 = (Exprent)object4.get(n);
                            object2 = exprent2.getExprType();
                            if (exprent2.type == 3 && ((VarType)object2).type != 7) {
                                object52 = (ConstExprent)exprent2;
                                long l = ((VarType)object2).type == 5 ? (Long)((ConstExprent)object52).getValue() : (long)((Integer)((ConstExprent)object52).getValue()).intValue();
                                if (l == -1L) {
                                    ArrayList<Exprent> arrayList = new ArrayList<Exprent>();
                                    arrayList.add((Exprent)object4.get(1 - n));
                                    return new FunctionExprent(11, arrayList);
                                }
                            }
                            ++n;
                        }
                        break block4;
                    }
                    case 42: 
                    case 43: {
                        if (((Exprent)object4.get((int)0)).getExprType().type != 7 || ((Exprent)object4.get((int)1)).getExprType().type != 7) break block4;
                        int n = 0;
                        while (n < 2) {
                            if (((Exprent)object4.get((int)n)).type == 3) {
                                exprent2 = null;
                                int n4 = (Integer)((ConstExprent)object4.get(n)).getValue();
                                if (((FunctionExprent)object52).getFunctype() == 42 && n4 == 1 || ((FunctionExprent)object52).getFunctype() == 43 && n4 == 0) {
                                    return (Exprent)object4.get(1 - n);
                                }
                                object52 = new ArrayList<Exprent>();
                                object52.add((Exprent)object4.get(1 - n));
                                return new FunctionExprent(12, (List)object52);
                            }
                            ++n;
                        }
                        break block4;
                    }
                    case 12: {
                        if (((Exprent)object4.get((int)0)).type != 3) break;
                        boolean bl2 = false;
                        if (((ConstExprent)object4.get(0)).getIntValue() == 0) {
                            return new ConstExprent(VarType.VARTYPE_BOOLEAN, new Integer(1));
                        }
                        return new ConstExprent(VarType.VARTYPE_BOOLEAN, new Integer(0));
                    }
                    case 36: {
                        object3 = (Exprent)object4.get(1);
                        exprent2 = (Exprent)object4.get(2);
                        if (((Exprent)object3).type != 3 || exprent2.type != 3) break block4;
                        object2 = (ConstExprent)object3;
                        object52 = (ConstExprent)exprent2;
                        if (object2.getExprType().type != 7 || object52.getExprType().type != 7) break block4;
                        if (((ConstExprent)object2).getIntValue() == 0 && ((ConstExprent)object52).getIntValue() != 0) {
                            return new FunctionExprent(12, Arrays.asList((Exprent)object4.get(0)));
                        }
                        if (((ConstExprent)object2).getIntValue() == 0 || ((ConstExprent)object52).getIntValue() != 0) break block4;
                        return (Exprent)object4.get(0);
                    }
                    case 37: 
                    case 38: 
                    case 39: 
                    case 40: 
                    case 41: {
                        int n = DecompilerContext.getCountercontainer().getCounterAndIncrement(2);
                        object52 = ((Exprent)object4.get(0)).getExprType();
                        VarProcessor varProcessor = (VarProcessor)DecompilerContext.getProperty("CURRENT_VAR_PROCESSOR");
                        object = new FunctionExprent(36, Arrays.asList(new FunctionExprent(44, Arrays.asList(new VarExprent(n, (VarType)object52, varProcessor), ConstExprent.getZeroConstant(((VarType)object52).type))), new ConstExprent(VarType.VARTYPE_INT, new Integer(-1)), new ConstExprent(VarType.VARTYPE_INT, new Integer(1))));
                        FunctionExprent functionExprent = new FunctionExprent(42, Arrays.asList(new AssignmentExprent(new VarExprent(n, (VarType)object52, varProcessor), new FunctionExprent(1, Arrays.asList((Exprent)object4.get(0), (Exprent)object4.get(1)))), ConstExprent.getZeroConstant(((VarType)object52).type)));
                        varProcessor.setVarType(new VarVersionPaar(n, 0), (VarType)object52);
                        return new FunctionExprent(36, Arrays.asList(functionExprent, new ConstExprent(VarType.VARTYPE_INT, new Integer(0)), object));
                    }
                }
                break;
            }
            case 2: {
                object3 = (AssignmentExprent)exprent;
                exprent2 = ((AssignmentExprent)object3).getRight();
                object2 = ((AssignmentExprent)object3).getLeft();
                if (exprent2.type != 6) break;
                object52 = (FunctionExprent)exprent2;
                VarType varType = null;
                if (((FunctionExprent)object52).getFunctype() >= 14 && ((FunctionExprent)object52).getFunctype() <= 28) {
                    exprent2 = (Exprent)((FunctionExprent)object52).getLstOperands().get(0);
                    varType = ((FunctionExprent)object52).getSimpleCastType();
                    if (exprent2.type == 6) {
                        object52 = (FunctionExprent)exprent2;
                    } else {
                        return null;
                    }
                }
                object = ((FunctionExprent)object52).getLstOperands();
                Exprent exprent3 = null;
                switch (((FunctionExprent)object52).getFunctype()) {
                    case 0: 
                    case 4: 
                    case 5: 
                    case 6: {
                        if (object2.equals(object.get(1))) {
                            exprent3 = (Exprent)object.get(0);
                            break;
                        }
                    }
                    case 1: 
                    case 2: 
                    case 3: 
                    case 7: 
                    case 8: 
                    case 9: 
                    case 10: {
                        if (!object2.equals(object.get(0))) break;
                        exprent3 = (Exprent)object.get(1);
                    }
                }
                if (exprent3 == null || varType != null && !varType.equals(exprent3.getExprType())) break;
                ((AssignmentExprent)object3).setRight(exprent3);
                ((AssignmentExprent)object3).setCondtype(((FunctionExprent)object52).getFunctype());
                break;
            }
            case 8: {
                object52 = ConcatenationHelper.contractStringConcat(exprent);
                if (exprent.equals(object52)) break;
                return object52;
            }
        }
        return null;
    }

    public static Exprent propagateBoolNot(Exprent exprent) {
        if (exprent.type == 6 && ((FunctionExprent)(exprent = (FunctionExprent)exprent)).getFunctype() == 12) {
            exprent = (Exprent)((FunctionExprent)exprent).getLstOperands().get(0);
            if (exprent.type == 6) {
                exprent = (FunctionExprent)exprent;
                int n = ((FunctionExprent)exprent).getFunctype();
                switch (n) {
                    case 12: {
                        exprent = (Exprent)((FunctionExprent)exprent).getLstOperands().get(0);
                        Exprent exprent2 = SecondaryFunctionsHelper.propagateBoolNot(exprent);
                        if (exprent2 == null) {
                            return exprent;
                        }
                        return exprent2;
                    }
                    case 48: 
                    case 49: {
                        List list = ((FunctionExprent)exprent).getLstOperands();
                        int n2 = 0;
                        while (n2 < list.size()) {
                            FunctionExprent functionExprent = new FunctionExprent(12, Arrays.asList((Exprent)list.get(n2)));
                            Exprent exprent3 = SecondaryFunctionsHelper.propagateBoolNot(functionExprent);
                            list.set(n2, exprent3 == null ? functionExprent : exprent3);
                            ++n2;
                        }
                    }
                    case 42: 
                    case 43: 
                    case 44: 
                    case 45: 
                    case 46: 
                    case 47: {
                        ((FunctionExprent)exprent).setFunctype(funcsnot[n - 42]);
                        return exprent;
                    }
                }
            }
        }
        return null;
    }
}

