/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.exps;

import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.main.rels.MethodWrapper;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.vars.CheckTypesResult;
import de.fernflower.struct.attr.StructExceptionsAttribute;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.List;

public final class ExitExprent
extends Exprent {
    private int exittype;
    private Exprent value;
    private VarType rettype;

    public ExitExprent(int n, Exprent exprent, VarType varType) {
        this.type = 4;
        this.exittype = n;
        this.value = exprent;
        this.rettype = varType;
    }

    public final Exprent copy() {
        return new ExitExprent(this.exittype, this.value == null ? null : this.value.copy(), this.rettype);
    }

    public final CheckTypesResult checkExprTypeBounds() {
        CheckTypesResult checkTypesResult = new CheckTypesResult();
        if (this.exittype == 0 && this.rettype.type != 10) {
            checkTypesResult.addMinTypeExprent(this.value, VarType.getMinTypeInFamily(this.rettype.type_family));
            checkTypesResult.addMaxTypeExprent(this.value, this.rettype);
        }
        return checkTypesResult;
    }

    public final List getAllExprents() {
        ArrayList<Exprent> arrayList = new ArrayList<Exprent>();
        if (this.value != null) {
            arrayList.add(this.value);
        }
        return arrayList;
    }

    public final String toJava(int n) {
        if (this.exittype == 0) {
            StringBuilder stringBuilder = new StringBuilder();
            if (this.rettype.type != 10) {
                stringBuilder.append(" ");
                ExprProcessor.getCastedExprent(this.value, this.rettype, stringBuilder, n, false);
            }
            return "return" + stringBuilder.toString();
        }
        Object object = (MethodWrapper)DecompilerContext.getProperty("CURRENT_METHOD_WRAPPER");
        ClassesProcessor$ClassNode classesProcessor$ClassNode = (ClassesProcessor$ClassNode)DecompilerContext.getProperty("CURRENT_CLASSNODE");
        if (object != null && classesProcessor$ClassNode != null && (object = (StructExceptionsAttribute)((MethodWrapper)object).methodStruct.getAttributes().getWithKey("Exceptions")) != null) {
            CharSequence charSequence;
            CharSequence charSequence2 = null;
            int n2 = 0;
            while (n2 < ((StructExceptionsAttribute)object).getThrowsExceptions().size()) {
                charSequence = ((StructExceptionsAttribute)object).getExcClassname(n2, classesProcessor$ClassNode.classStruct.getPool());
                if ("java/lang/Throwable".equals(charSequence)) {
                    charSequence2 = charSequence;
                    break;
                }
                if ("java/lang/Exception".equals(charSequence)) {
                    charSequence2 = charSequence;
                }
                ++n2;
            }
            if (charSequence2 != null) {
                VarType varType = new VarType((String)charSequence2, true);
                charSequence = new StringBuilder();
                ExprProcessor.getCastedExprent(this.value, varType, (StringBuilder)charSequence, n, false);
                return "throw " + ((StringBuilder)charSequence).toString();
            }
        }
        return "throw " + this.value.toJava(n);
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof ExitExprent) {
            object = (ExitExprent)object;
            return this.exittype == ((ExitExprent)object).exittype && InterpreterUtil.equalObjects(this.value, ((ExitExprent)object).value);
        }
        return false;
    }

    public final void replaceExprent(Exprent exprent, Exprent exprent2) {
        if (exprent == this.value) {
            this.value = exprent2;
        }
    }

    public final int getExittype() {
        return this.exittype;
    }

    public final Exprent getValue() {
        return this.value;
    }

    public final VarType getRettype() {
        return this.rettype;
    }
}

