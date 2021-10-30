/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.exps;

import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FieldExprent;
import de.fernflower.modules.decompiler.exps.FunctionExprent;
import de.fernflower.modules.decompiler.vars.CheckTypesResult;
import de.fernflower.struct.StructField;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.List;

public final class AssignmentExprent
extends Exprent {
    private static final String[] funceq = new String[]{" += ", " -= ", " *= ", " /= ", " &= ", " |= ", " ^= ", " %= ", " <<= ", " >>= ", " >>>= "};
    private Exprent left;
    private Exprent right;
    private int condtype = -1;

    public AssignmentExprent(Exprent exprent, Exprent exprent2) {
        this.type = 2;
        this.left = exprent;
        this.right = exprent2;
    }

    public final VarType getExprType() {
        return this.right.getExprType();
    }

    public final CheckTypesResult checkExprTypeBounds() {
        CheckTypesResult checkTypesResult = new CheckTypesResult();
        VarType varType = this.left.getExprType();
        VarType varType2 = this.right.getExprType();
        if (varType.type_family > varType2.type_family) {
            checkTypesResult.addMinTypeExprent(this.right, VarType.getMinTypeInFamily(varType.type_family));
        } else if (varType.type_family < varType2.type_family) {
            checkTypesResult.addMinTypeExprent(this.left, varType2);
        } else {
            checkTypesResult.addMinTypeExprent(this.left, VarType.getCommonSupertype(varType, varType2));
        }
        return checkTypesResult;
    }

    public final List getAllExprents() {
        ArrayList<Exprent> arrayList = new ArrayList<Exprent>();
        arrayList.add(this.left);
        arrayList.add(this.right);
        return arrayList;
    }

    public final Exprent copy() {
        return new AssignmentExprent(this.left.copy(), this.right.copy());
    }

    public final int getPrecedence() {
        return 13;
    }

    public final String toJava(int n) {
        ClassesProcessor$ClassNode classesProcessor$ClassNode;
        Object object;
        Object object2 = this.left.getExprType();
        VarType varType = this.right.getExprType();
        String string = this.right.toJava(n);
        if (this.condtype == -1 && !((VarType)object2).isSuperset(varType) && (varType.equals(VarType.VARTYPE_OBJECT) || ((VarType)object2).type != 8)) {
            if (this.right.getPrecedence() >= FunctionExprent.g()) {
                string = "(" + string + ")";
            }
            string = "(" + ExprProcessor.getCastTypeName((VarType)object2) + ")" + string;
        }
        object2 = new StringBuilder();
        boolean bl = false;
        if (this.left.type == 5 && ((FieldExprent)(object = (FieldExprent)this.left)).isStatic() && (classesProcessor$ClassNode = (ClassesProcessor$ClassNode)DecompilerContext.getProperty("CURRENT_CLASSNODE")) != null && (object = classesProcessor$ClassNode.classStruct.getField(((FieldExprent)object).getName(), ((FieldExprent)object).getDescriptor())) != null && (((StructField)object).access_flags & 0x10) != 0) {
            bl = true;
        }
        if (bl) {
            ((StringBuilder)object2).append(((FieldExprent)this.left).getName());
        } else {
            ((StringBuilder)object2).append(this.left.toJava(n));
        }
        ((StringBuilder)object2).append(String.valueOf(this.condtype == -1 ? " = " : funceq[this.condtype]) + string);
        return ((StringBuilder)object2).toString();
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof AssignmentExprent) {
            object = (AssignmentExprent)object;
            return InterpreterUtil.equalObjects(this.left, ((AssignmentExprent)object).left) && InterpreterUtil.equalObjects(this.right, ((AssignmentExprent)object).right) && this.condtype == ((AssignmentExprent)object).condtype;
        }
        return false;
    }

    public final void replaceExprent(Exprent exprent, Exprent exprent2) {
        if (exprent == this.left) {
            this.left = exprent2;
        }
        if (exprent == this.right) {
            this.right = exprent2;
        }
    }

    public final Exprent getLeft() {
        return this.left;
    }

    public final Exprent getRight() {
        return this.right;
    }

    public final void setRight(Exprent exprent) {
        this.right = exprent;
    }

    public final int getCondtype() {
        return this.condtype;
    }

    public final void setCondtype(int n) {
        this.condtype = n;
    }
}

