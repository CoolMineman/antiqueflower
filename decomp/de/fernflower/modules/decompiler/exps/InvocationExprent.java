/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.exps;

import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.main.rels.MethodWrapper;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FunctionExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.vars.CheckTypesResult;
import de.fernflower.modules.decompiler.vars.VarProcessor;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.consts.LinkConstant;
import de.fernflower.struct.gen.MethodDescriptor;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import de.fernflower.util.ListStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class InvocationExprent
extends Exprent {
    private String name;
    private String classname;
    private boolean isStatic;
    private int functype = 1;
    private Exprent instance;
    private MethodDescriptor descriptor;
    private String stringDescriptor;
    private int invocationTyp = 2;
    private List lstParameters = new ArrayList();

    public InvocationExprent() {
        this.type = 8;
    }

    public InvocationExprent(int n, LinkConstant linkConstant, ListStack listStack) {
        this.type = 8;
        this.name = linkConstant.elementname;
        this.classname = linkConstant.classname;
        switch (n) {
            case 184: {
                this.invocationTyp = 3;
                break;
            }
            case 183: {
                this.invocationTyp = 1;
                break;
            }
            case 182: {
                this.invocationTyp = 2;
                break;
            }
            case 185: {
                this.invocationTyp = 4;
            }
        }
        if ("<init>".equals(this.name)) {
            this.functype = 2;
        } else if ("<clinit>".equals(this.name)) {
            this.functype = 3;
        }
        this.stringDescriptor = linkConstant.descriptor;
        this.descriptor = MethodDescriptor.parseDescriptor(linkConstant.descriptor);
        int n2 = 0;
        while (n2 < this.descriptor.params.length) {
            this.lstParameters.add(0, (Exprent)listStack.pop());
            ++n2;
        }
        if (n == 184) {
            this.isStatic = true;
            return;
        }
        this.instance = (Exprent)listStack.pop();
    }

    private InvocationExprent(InvocationExprent invocationExprent) {
        this.type = 8;
        this.name = invocationExprent.name;
        this.classname = invocationExprent.classname;
        this.isStatic = invocationExprent.isStatic;
        this.functype = invocationExprent.functype;
        this.instance = invocationExprent.instance;
        if (this.instance != null) {
            this.instance = this.instance.copy();
        }
        this.invocationTyp = invocationExprent.invocationTyp;
        this.stringDescriptor = invocationExprent.stringDescriptor;
        this.descriptor = invocationExprent.descriptor;
        this.lstParameters = new ArrayList(invocationExprent.lstParameters);
        int n = 0;
        while (n < this.lstParameters.size()) {
            this.lstParameters.set(n, ((Exprent)this.lstParameters.get(n)).copy());
            ++n;
        }
    }

    public final VarType getExprType() {
        return this.descriptor.ret;
    }

    public final CheckTypesResult checkExprTypeBounds() {
        CheckTypesResult checkTypesResult = new CheckTypesResult();
        int n = 0;
        while (n < this.lstParameters.size()) {
            Exprent exprent = (Exprent)this.lstParameters.get(n);
            VarType varType = this.descriptor.params[n];
            checkTypesResult.addMinTypeExprent(exprent, VarType.getMinTypeInFamily(varType.type_family));
            checkTypesResult.addMaxTypeExprent(exprent, varType);
            ++n;
        }
        return checkTypesResult;
    }

    public final List getAllExprents() {
        ArrayList<Exprent> arrayList = new ArrayList<Exprent>();
        if (this.instance != null) {
            arrayList.add(this.instance);
        }
        arrayList.addAll(this.lstParameters);
        return arrayList;
    }

    public final Exprent copy() {
        return new InvocationExprent(this);
    }

    public final String toJava(int n) {
        Object object;
        Object object2;
        StringBuilder stringBuilder = new StringBuilder("");
        Object object3 = null;
        boolean bl = false;
        if (this.isStatic) {
            object2 = (ClassesProcessor$ClassNode)DecompilerContext.getProperty("CURRENT_CLASSNODE");
            if (object2 == null || !this.classname.equals(object2.classStruct.qualifiedName)) {
                object = null;
                stringBuilder.append(DecompilerContext.getImpcollector().getShortName(ExprProcessor.buildJavaClassName(this.classname), true));
            }
        } else {
            Object object4;
            if (this.instance != null && this.instance.type == 12) {
                Object object5;
                object2 = (VarExprent)this.instance;
                object = new VarVersionPaar((VarExprent)object2);
                object4 = ((VarExprent)object2).getProcessor();
                if (object4 == null && (object5 = (MethodWrapper)DecompilerContext.getProperty("CURRENT_METHOD_WRAPPER")) != null) {
                    object4 = ((MethodWrapper)object5).varproc;
                }
                object5 = null;
                if (object4 != null) {
                    object5 = (String)((VarProcessor)object4).getThisvars().get(object);
                }
                if (object5 != null) {
                    bl = true;
                    if (this.invocationTyp == 1 && !this.classname.equals(object5)) {
                        object3 = object5;
                    }
                }
            }
            if (this.functype == 1) {
                if (object3 != null) {
                    object2 = ((ClassesProcessor$ClassNode)DecompilerContext.getProperty((String)"CURRENT_CLASSNODE")).classStruct;
                    if (!((String)object3).equals(((StructClass)object2).qualifiedName)) {
                        object = null;
                        stringBuilder.append(DecompilerContext.getImpcollector().getShortName(ExprProcessor.buildJavaClassName((String)object3), true));
                        stringBuilder.append(".");
                    }
                    stringBuilder.append("super");
                } else {
                    object2 = this.instance.toJava(n);
                    object = this.instance.getExprType();
                    object4 = new VarType(8, 0, this.classname);
                    if (((VarType)object).equals(VarType.VARTYPE_OBJECT) && !((VarType)object4).equals(object)) {
                        stringBuilder.append("((" + ExprProcessor.getCastTypeName((VarType)object4) + ")");
                        if (this.instance.getPrecedence() >= FunctionExprent.g()) {
                            object2 = "(" + object2 + ")";
                        }
                        stringBuilder.append(String.valueOf(object2) + ")");
                    } else if (this.instance.getPrecedence() > this.getPrecedence()) {
                        stringBuilder.append("(" + (String)object2 + ")");
                    } else {
                        stringBuilder.append((String)object2);
                    }
                }
            }
        }
        switch (this.functype) {
            case 1: {
                if ("<VAR_NAMELESS_ENCLOSURE>".equals(stringBuilder.toString())) {
                    stringBuilder = new StringBuilder("");
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(".");
                }
                stringBuilder.append(String.valueOf(this.name) + "(");
                break;
            }
            case 3: {
                throw new RuntimeException("Explicite invocation of <clinit>");
            }
            case 2: {
                if (object3 != null) {
                    stringBuilder.append("super(");
                    break;
                }
                if (bl) {
                    stringBuilder.append("this(");
                    break;
                }
                stringBuilder.append(this.instance.toJava(n));
                stringBuilder.append(".<init>(");
            }
        }
        object2 = null;
        if (this.functype == 2 && (object = (ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get(this.classname)) != null) {
            if (((ClassesProcessor$ClassNode)object).wrapper != null) {
                object2 = object.wrapper.getMethodWrapper((String)"<init>", (String)this.stringDescriptor).signatureFields;
            } else if (((ClassesProcessor$ClassNode)object).type == 1 && (((ClassesProcessor$ClassNode)object).access & 8) == 0) {
                object2 = new ArrayList<VarVersionPaar>((Collection)Collections.nCopies(this.lstParameters.size(), null));
                object2.set(0, new VarVersionPaar(-1, 0));
            }
        }
        object = this.getAmbiguousParameters();
        boolean bl2 = true;
        int n2 = 0;
        while (n2 < this.lstParameters.size()) {
            if (object2 == null || object2.get(n2) == null) {
                if (!bl2) {
                    stringBuilder.append(", ");
                }
                object3 = new StringBuilder();
                ExprProcessor.getCastedExprent((Exprent)this.lstParameters.get(n2), this.descriptor.params[n2], (StringBuilder)object3, n, true, object.contains(n2));
                stringBuilder.append((CharSequence)object3);
                bl2 = false;
            }
            ++n2;
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    private Set getAmbiguousParameters() {
        HashSet<Integer> hashSet = new HashSet<Integer>();
        StructClass structClass = DecompilerContext.getStructcontext().getClass(this.classname);
        if (structClass != null) {
            ArrayList<MethodDescriptor> arrayList = new ArrayList<MethodDescriptor>();
            for (StructMethod structMethod : structClass.getMethods()) {
                if (!this.name.equals(structMethod.getName())) continue;
                MethodDescriptor methodDescriptor = MethodDescriptor.parseDescriptor(structMethod.getDescriptor());
                if (methodDescriptor.params.length != this.descriptor.params.length) continue;
                boolean bl = true;
                int n = 0;
                while (n < methodDescriptor.params.length) {
                    if (methodDescriptor.params[n].type_family != this.descriptor.params[n].type_family) {
                        bl = false;
                        break;
                    }
                    ++n;
                }
                if (!bl) continue;
                arrayList.add(methodDescriptor);
            }
            if (arrayList.size() > 1) {
                int n = 0;
                while (n < this.descriptor.params.length) {
                    VarType varType = this.descriptor.params[n];
                    for (MethodDescriptor methodDescriptor : arrayList) {
                        if (varType.equals(methodDescriptor.params[n])) continue;
                        hashSet.add(n);
                        break;
                    }
                    ++n;
                }
            }
        }
        return hashSet;
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof InvocationExprent) {
            object = (InvocationExprent)object;
            return InterpreterUtil.equalObjects(this.name, ((InvocationExprent)object).name) && InterpreterUtil.equalObjects(this.classname, ((InvocationExprent)object).classname) && this.isStatic == ((InvocationExprent)object).isStatic && InterpreterUtil.equalObjects(this.instance, ((InvocationExprent)object).instance) && InterpreterUtil.equalObjects(this.descriptor, ((InvocationExprent)object).descriptor) && this.functype == ((InvocationExprent)object).functype && InterpreterUtil.equalLists(this.lstParameters, ((InvocationExprent)object).lstParameters);
        }
        return false;
    }

    public final void replaceExprent(Exprent exprent, Exprent exprent2) {
        if (exprent == this.instance) {
            this.instance = exprent2;
        }
        int n = 0;
        while (n < this.lstParameters.size()) {
            if (exprent == this.lstParameters.get(n)) {
                this.lstParameters.set(n, exprent2);
            }
            ++n;
        }
    }

    public final List getLstParameters() {
        return this.lstParameters;
    }

    public final void setLstParameters(List list) {
        this.lstParameters = list;
    }

    public final MethodDescriptor getDescriptor() {
        return this.descriptor;
    }

    public final void setDescriptor(MethodDescriptor methodDescriptor) {
        this.descriptor = methodDescriptor;
    }

    public final String getClassname() {
        return this.classname;
    }

    public final void setClassname(String string) {
        this.classname = string;
    }

    public final int getFunctype() {
        return this.functype;
    }

    public final void setFunctype() {
        this.functype = 2;
    }

    public final Exprent getInstance() {
        return this.instance;
    }

    public final void setInstance(Exprent exprent) {
        this.instance = exprent;
    }

    public final boolean isStatic() {
        return this.isStatic;
    }

    public final void setStatic() {
        this.isStatic = true;
    }

    public final String getName() {
        return this.name;
    }

    public final void setName(String string) {
        this.name = string;
    }

    public final String getStringDescriptor() {
        return this.stringDescriptor;
    }

    public final void setStringDescriptor(String string) {
        this.stringDescriptor = string;
    }
}

