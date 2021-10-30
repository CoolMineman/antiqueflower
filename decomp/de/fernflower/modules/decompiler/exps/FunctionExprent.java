/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.exps;

import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.vars.CheckTypesResult;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import de.fernflower.util.ListStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public final class FunctionExprent
extends Exprent {
    private static final VarType[] types = new VarType[]{VarType.VARTYPE_LONG, VarType.VARTYPE_FLOAT, VarType.VARTYPE_DOUBLE, VarType.VARTYPE_INT, VarType.VARTYPE_FLOAT, VarType.VARTYPE_DOUBLE, VarType.VARTYPE_INT, VarType.VARTYPE_LONG, VarType.VARTYPE_DOUBLE, VarType.VARTYPE_INT, VarType.VARTYPE_LONG, VarType.VARTYPE_FLOAT, VarType.VARTYPE_BYTE, VarType.VARTYPE_CHAR, VarType.VARTYPE_SHORT};
    private static final String[] operators = new String[]{" + ", " - ", " * ", " / ", " & ", " | ", " ^ ", " % ", " << ", " >> ", " >>> ", " == ", " != ", " < ", " >= ", " > ", " <= ", " && ", " || ", " + "};
    private static final int[] precedence;
    private static final HashSet associativity;
    private int functype;
    private VarType implicitType;
    private List lstOperands = new ArrayList();

    static {
        int[] nArray = new int[51];
        nArray[0] = 3;
        nArray[1] = 3;
        nArray[2] = 2;
        nArray[3] = 2;
        nArray[4] = 7;
        nArray[5] = 9;
        nArray[6] = 8;
        nArray[7] = 2;
        nArray[8] = 4;
        nArray[9] = 4;
        nArray[10] = 4;
        nArray[11] = 1;
        nArray[12] = 1;
        nArray[13] = 1;
        nArray[14] = 1;
        nArray[15] = 1;
        nArray[16] = 1;
        nArray[17] = 1;
        nArray[18] = 1;
        nArray[19] = 1;
        nArray[20] = 1;
        nArray[21] = 1;
        nArray[22] = 1;
        nArray[23] = 1;
        nArray[24] = 1;
        nArray[25] = 1;
        nArray[26] = 1;
        nArray[27] = 1;
        nArray[28] = 1;
        nArray[29] = 1;
        nArray[30] = 6;
        nArray[32] = 1;
        nArray[33] = 1;
        nArray[34] = 1;
        nArray[35] = 1;
        nArray[36] = 12;
        nArray[37] = -1;
        nArray[38] = -1;
        nArray[39] = -1;
        nArray[40] = -1;
        nArray[41] = -1;
        nArray[42] = 6;
        nArray[43] = 6;
        nArray[44] = 5;
        nArray[45] = 5;
        nArray[46] = 5;
        nArray[47] = 5;
        nArray[48] = 10;
        nArray[49] = 11;
        nArray[50] = 3;
        precedence = nArray;
        associativity = new HashSet(Arrays.asList(0, 2, 4, 5, 6, 48, 49, 50));
    }

    public FunctionExprent(int n, ListStack listStack) {
        this.type = 6;
        this.functype = n;
        if (n >= 11 && n <= 35 && n != 29 && n != 30) {
            this.lstOperands.add((Exprent)listStack.pop());
            return;
        }
        if (n == 36) {
            throw new RuntimeException("no direct instantiation possible");
        }
        Exprent exprent = (Exprent)listStack.pop();
        this.lstOperands.add((Exprent)listStack.pop());
        this.lstOperands.add(exprent);
    }

    public FunctionExprent(int n, List list) {
        this.type = 6;
        this.functype = n;
        this.lstOperands = list;
    }

    public final VarType getExprType() {
        Object object = null;
        if (((FunctionExprent)varType).functype <= 13 || ((FunctionExprent)varType).functype == 34 || ((FunctionExprent)varType).functype == 35 || ((FunctionExprent)varType).functype == 32 || ((FunctionExprent)varType).functype == 33) {
            VarType varType = ((Exprent)((FunctionExprent)varType).lstOperands.get(0)).getExprType();
            VarType varType2 = null;
            if (((FunctionExprent)varType).lstOperands.size() > 1) {
                varType2 = ((Exprent)((FunctionExprent)varType).lstOperands.get(1)).getExprType();
            }
            switch (((FunctionExprent)varType).functype) {
                case 32: 
                case 33: 
                case 34: 
                case 35: {
                    object = ((FunctionExprent)varType).implicitType;
                    break;
                }
                case 12: {
                    object = VarType.VARTYPE_BOOLEAN;
                    break;
                }
                case 8: 
                case 9: 
                case 10: 
                case 11: 
                case 13: {
                    object = FunctionExprent.getMaxVarType(new VarType[]{varType});
                    break;
                }
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 7: {
                    object = FunctionExprent.getMaxVarType(new VarType[]{varType, varType2});
                    break;
                }
                case 4: 
                case 5: 
                case 6: {
                    if (varType.type == 7 & varType2.type == 7) {
                        object = VarType.VARTYPE_BOOLEAN;
                        break;
                    }
                    object = FunctionExprent.getMaxVarType(new VarType[]{varType, varType2});
                }
            }
        } else if (((FunctionExprent)varType).functype == 29) {
            object = ((Exprent)((FunctionExprent)varType).lstOperands.get(1)).getExprType();
        } else if (((FunctionExprent)varType).functype == 36) {
            Exprent exprent = (Exprent)((FunctionExprent)varType).lstOperands.get(1);
            Exprent exprent2 = (Exprent)((FunctionExprent)varType).lstOperands.get(2);
            VarType varType = VarType.getCommonSupertype(exprent.getExprType(), exprent2.getExprType());
            object = exprent.type == 3 && exprent2.type == 3 && varType.type != 7 && VarType.VARTYPE_INT.isSuperset(varType) ? VarType.VARTYPE_INT : varType;
        } else {
            object = ((FunctionExprent)varType).functype == 50 ? VarType.VARTYPE_STRING : (((FunctionExprent)varType).functype >= 42 ? VarType.VARTYPE_BOOLEAN : (((FunctionExprent)varType).functype == 30 ? VarType.VARTYPE_BOOLEAN : (((FunctionExprent)varType).functype >= 31 ? VarType.VARTYPE_INT : types[((FunctionExprent)varType).functype - 14])));
        }
        return object;
    }

    public final int getExprentUse() {
        if (((FunctionExprent)exprent).functype >= 32 && ((FunctionExprent)exprent).functype <= 35) {
            return 0;
        }
        int n = 3;
        for (Exprent exprent : ((FunctionExprent)exprent).lstOperands) {
            n &= exprent.getExprentUse();
        }
        return n;
    }

    public final CheckTypesResult checkExprTypeBounds() {
        CheckTypesResult checkTypesResult = new CheckTypesResult();
        Exprent exprent = (Exprent)this.lstOperands.get(0);
        VarType varType = exprent.getExprType();
        Exprent exprent2 = null;
        VarType varType2 = null;
        if (this.lstOperands.size() > 1) {
            exprent2 = (Exprent)this.lstOperands.get(1);
            varType2 = exprent2.getExprType();
        }
        switch (this.functype) {
            case 36: {
                varType = this.getExprType();
                if (varType == null) {
                    varType = this.getExprType();
                }
                checkTypesResult.addMinTypeExprent(exprent, VarType.VARTYPE_BOOLEAN);
                checkTypesResult.addMinTypeExprent(exprent2, VarType.getMinTypeInFamily(varType.type_family));
                checkTypesResult.addMinTypeExprent((Exprent)this.lstOperands.get(2), VarType.getMinTypeInFamily(varType.type_family));
                break;
            }
            case 14: 
            case 15: 
            case 16: 
            case 26: 
            case 27: 
            case 28: {
                checkTypesResult.addMinTypeExprent(exprent, VarType.VARTYPE_BYTECHAR);
                checkTypesResult.addMaxTypeExprent(exprent, VarType.VARTYPE_INT);
                break;
            }
            case 32: 
            case 33: 
            case 34: 
            case 35: {
                checkTypesResult.addMinTypeExprent(exprent, this.implicitType);
                checkTypesResult.addMaxTypeExprent(exprent, this.implicitType);
                break;
            }
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 44: 
            case 45: 
            case 46: 
            case 47: {
                checkTypesResult.addMinTypeExprent(exprent2, VarType.VARTYPE_BYTECHAR);
            }
            case 11: 
            case 13: {
                checkTypesResult.addMinTypeExprent(exprent, VarType.VARTYPE_BYTECHAR);
                break;
            }
            case 4: 
            case 5: 
            case 6: {
                if (varType.type == 7 && ((varType.convinfo & 1) != 0 || varType2.type != 7)) {
                    checkTypesResult.addMinTypeExprent(exprent, VarType.VARTYPE_BYTECHAR);
                }
                if (varType2.type != 7 || (varType2.convinfo & 1) == 0 && varType.type == 7) break;
                checkTypesResult.addMinTypeExprent(exprent2, VarType.VARTYPE_BYTECHAR);
                break;
            }
            case 42: 
            case 43: {
                if (!(varType.type != 7 || !varType2.isStrictSuperset(varType) && (exprent.type != 3 || ((ConstExprent)exprent).hasBooleanValue() || exprent2.type == 3 && ((ConstExprent)exprent2).hasBooleanValue()))) {
                    checkTypesResult.addMinTypeExprent(exprent, VarType.VARTYPE_BYTECHAR);
                }
                if (varType2.type != 7 || !varType.isStrictSuperset(varType2) && (exprent2.type != 3 || ((ConstExprent)exprent2).hasBooleanValue() || exprent.type == 3 && ((ConstExprent)exprent).hasBooleanValue())) break;
                checkTypesResult.addMinTypeExprent(exprent2, VarType.VARTYPE_BYTECHAR);
            }
        }
        return checkTypesResult;
    }

    public final List getAllExprents() {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(this.lstOperands);
        return arrayList;
    }

    public final Exprent copy() {
        Exprent exprent2;
        ArrayList<Exprent> arrayList = new ArrayList<Exprent>();
        for (Exprent exprent2 : this.lstOperands) {
            arrayList.add(exprent2.copy());
        }
        exprent2 = new FunctionExprent(this.functype, arrayList);
        new FunctionExprent(this.functype, arrayList).implicitType = this.implicitType;
        return exprent2;
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof FunctionExprent) {
            object = (FunctionExprent)object;
            return this.functype == ((FunctionExprent)object).functype && InterpreterUtil.equalLists(this.lstOperands, ((FunctionExprent)object).lstOperands);
        }
        return false;
    }

    public final void replaceExprent(Exprent exprent, Exprent exprent2) {
        int n = 0;
        while (n < this.lstOperands.size()) {
            if (exprent == this.lstOperands.get(n)) {
                this.lstOperands.set(n, exprent2);
            }
            ++n;
        }
    }

    public final String toJava(int n) {
        if (((FunctionExprent)object).functype <= 10) {
            return String.valueOf(((FunctionExprent)object).wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(0), false, n)) + operators[((FunctionExprent)object).functype] + ((FunctionExprent)object).wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(1), true, n);
        }
        if (((FunctionExprent)object).functype >= 42) {
            return String.valueOf(((FunctionExprent)object).wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(0), false, n)) + operators[((FunctionExprent)object).functype - 42 + 11] + super.wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(1), true, n);
        }
        switch (((FunctionExprent)object).functype) {
            case 11: {
                return "~" + ((FunctionExprent)object).wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(0), true, n);
            }
            case 12: {
                return "!" + ((FunctionExprent)object).wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(0), true, n);
            }
            case 13: {
                return "-" + ((FunctionExprent)object).wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(0), true, n);
            }
            case 29: {
                return "(" + ((Exprent)((FunctionExprent)object).lstOperands.get(1)).toJava(n) + ")" + super.wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(0), true, n);
            }
            case 31: {
                Exprent exprent = (Exprent)((FunctionExprent)object).lstOperands.get(0);
                Object object = ((FunctionExprent)object).wrapOperandString(exprent, false, n);
                if (exprent.getExprType().arraydim == 0) {
                    VarType varType = VarType.VARTYPE_OBJECT.copy();
                    VarType.VARTYPE_OBJECT.copy().arraydim = 1;
                    object = "((" + ExprProcessor.getCastTypeName(varType) + ")" + (String)object + ")";
                }
                return String.valueOf(object) + ".length";
            }
            case 36: {
                return String.valueOf(((FunctionExprent)object).wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(0), true, n)) + "?" + super.wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(1), true, n) + ":" + super.wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(2), true, n);
            }
            case 34: {
                return String.valueOf(((FunctionExprent)object).wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(0), true, n)) + "++";
            }
            case 35: {
                return "++" + ((FunctionExprent)object).wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(0), true, n);
            }
            case 32: {
                return String.valueOf(((FunctionExprent)object).wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(0), true, n)) + "--";
            }
            case 33: {
                return "--" + ((FunctionExprent)object).wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(0), true, n);
            }
            case 30: {
                return String.valueOf(((FunctionExprent)object).wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(0), true, n)) + " instanceof " + super.wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(1), true, n);
            }
        }
        if (((FunctionExprent)object).functype <= 28) {
            return "(" + ExprProcessor.getTypeName(types[((FunctionExprent)object).functype - 14]) + ")" + super.wrapOperandString((Exprent)((FunctionExprent)object).lstOperands.get(0), true, n);
        }
        throw new RuntimeException("invalid function");
    }

    public final int getPrecedence() {
        int n = this.functype;
        return precedence[n];
    }

    public static int g() {
        return precedence[29];
    }

    public final VarType getSimpleCastType() {
        return types[this.functype - 14];
    }

    private String wrapOperandString(Exprent exprent, boolean bl, int n) {
        int n2 = ((FunctionExprent)object).getPrecedence();
        int n3 = exprent.getPrecedence();
        boolean bl2 = n3 > n2;
        if (!bl2 && bl && (bl2 = n3 == n2) && exprent.type == 6 && ((FunctionExprent)exprent).functype == ((FunctionExprent)object).functype) {
            bl2 = !associativity.contains(((FunctionExprent)object).functype);
        }
        Object object = exprent.toJava(n);
        if (bl2) {
            object = "(" + (String)object + ")";
        }
        return object;
    }

    private static VarType getMaxVarType(VarType[] varTypeArray) {
        int[] nArray = new int[]{2, 3, 5};
        VarType[] varTypeArray2 = new VarType[]{VarType.VARTYPE_DOUBLE, VarType.VARTYPE_FLOAT, VarType.VARTYPE_LONG};
        int n = 0;
        while (n < nArray.length) {
            int n2 = 0;
            while (n2 < varTypeArray.length) {
                if (varTypeArray[n2].type == nArray[n]) {
                    return varTypeArray2[n];
                }
                ++n2;
            }
            ++n;
        }
        return VarType.VARTYPE_INT;
    }

    public final int getFunctype() {
        return this.functype;
    }

    public final void setFunctype(int n) {
        this.functype = n;
    }

    public final List getLstOperands() {
        return this.lstOperands;
    }

    public final void setImplicitType(VarType varType) {
        this.implicitType = varType;
    }
}

