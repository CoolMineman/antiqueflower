/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.exps;

import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.main.rels.MethodWrapper;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.consts.LinkConstant;
import de.fernflower.struct.gen.FieldDescriptor;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.List;

public final class FieldExprent
extends Exprent {
    private String name;
    private String classname;
    private boolean isStatic;
    private Exprent instance;
    private FieldDescriptor descriptor;
    private String h;

    public FieldExprent(LinkConstant linkConstant, Exprent exprent) {
        this.type = 5;
        this.instance = exprent;
        if (exprent == null) {
            this.isStatic = true;
        }
        this.classname = linkConstant.classname;
        this.name = linkConstant.elementname;
        this.h = linkConstant.descriptor;
        this.descriptor = FieldDescriptor.parseDescriptor(linkConstant.descriptor);
    }

    public FieldExprent(String string, String string2, boolean bl, Exprent exprent, FieldDescriptor fieldDescriptor) {
        this.type = 5;
        this.name = string;
        this.classname = string2;
        this.isStatic = bl;
        this.instance = exprent;
        this.descriptor = fieldDescriptor;
        this.h = fieldDescriptor.descriptorString;
    }

    public final VarType getExprType() {
        return this.descriptor.type;
    }

    public final int getExprentUse() {
        if (this.instance == null) {
            return 1;
        }
        return this.instance.getExprentUse() & 1;
    }

    public final List getAllExprents() {
        ArrayList<Exprent> arrayList = new ArrayList<Exprent>();
        if (this.instance != null) {
            arrayList.add(this.instance);
        }
        return arrayList;
    }

    public final Exprent copy() {
        return new FieldExprent(this.name, this.classname, this.isStatic, this.instance == null ? null : this.instance.copy(), this.descriptor);
    }

    public final String toJava(int n) {
        StringBuffer stringBuffer = new StringBuffer();
        if (this.isStatic) {
            ClassesProcessor$ClassNode classesProcessor$ClassNode = (ClassesProcessor$ClassNode)DecompilerContext.getProperty("CURRENT_CLASSNODE");
            if (classesProcessor$ClassNode == null || !this.classname.equals(classesProcessor$ClassNode.classStruct.qualifiedName)) {
                stringBuffer.append(DecompilerContext.getImpcollector().getShortName(ExprProcessor.buildJavaClassName(this.classname), true));
                stringBuffer.append(".");
            }
        } else {
            Object object;
            Object object2 = null;
            if (this.instance != null && this.instance.type == 12) {
                object = (VarExprent)this.instance;
                VarVersionPaar varVersionPaar = new VarVersionPaar((VarExprent)object);
                object = (MethodWrapper)DecompilerContext.getProperty("CURRENT_METHOD_WRAPPER");
                if (object != null && (object = (String)((MethodWrapper)object).varproc.getThisvars().get(varVersionPaar)) != null && !this.classname.equals(object)) {
                    object2 = object;
                }
            }
            if (object2 != null) {
                object = ((ClassesProcessor$ClassNode)DecompilerContext.getProperty((String)"CURRENT_CLASSNODE")).classStruct;
                if (!((String)object2).equals(((StructClass)object).qualifiedName)) {
                    stringBuffer.append(DecompilerContext.getImpcollector().getShortName(ExprProcessor.buildJavaClassName((String)object2), true));
                    stringBuffer.append(".");
                }
                stringBuffer.append("super");
            } else {
                object = new StringBuilder();
                boolean bl = ExprProcessor.getCastedExprent(this.instance, new VarType(8, 0, this.classname), (StringBuilder)object, n, true);
                object = ((StringBuilder)object).toString();
                if (bl || this.instance.getPrecedence() > this.getPrecedence()) {
                    object = "(" + (String)object + ")";
                }
                stringBuffer.append((String)object);
            }
            stringBuffer.append(".");
        }
        stringBuffer.append(this.name);
        return stringBuffer.toString();
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof FieldExprent) {
            object = (FieldExprent)object;
            return InterpreterUtil.equalObjects(this.name, ((FieldExprent)object).name) && InterpreterUtil.equalObjects(this.classname, ((FieldExprent)object).classname) && this.isStatic == ((FieldExprent)object).isStatic && InterpreterUtil.equalObjects(this.instance, ((FieldExprent)object).instance) && InterpreterUtil.equalObjects(this.h, ((FieldExprent)object).h);
        }
        return false;
    }

    public final void replaceExprent(Exprent exprent, Exprent exprent2) {
        if (exprent == this.instance) {
            this.instance = exprent2;
        }
    }

    public final String getClassname() {
        return this.classname;
    }

    public final String getDescriptor() {
        return this.h;
    }

    public final Exprent getInstance() {
        return this.instance;
    }

    public final boolean isStatic() {
        return this.isStatic;
    }

    public final String getName() {
        return this.name;
    }
}

