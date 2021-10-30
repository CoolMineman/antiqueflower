/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.exps;

import de.fernflower.main.ClassWriter;
import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.InvocationExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.vars.CheckTypesResult;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import de.fernflower.util.ListStack;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class NewExprent
extends Exprent {
    private InvocationExprent constructor;
    private VarType newtype;
    private List lstDims = new ArrayList();
    private List lstArrayElements = new ArrayList();
    private boolean directArrayInit;
    private boolean anonymous;
    private boolean enumconst;

    public NewExprent(VarType varType, ListStack listStack, int n) {
        this.type = 10;
        this.newtype = varType;
        int n2 = 0;
        while (n2 < n) {
            this.lstDims.add(0, (Exprent)listStack.pop());
            ++n2;
        }
        this.setAnonymous();
    }

    public NewExprent(VarType varType, List list) {
        this.type = 10;
        this.newtype = varType;
        this.lstDims = list;
        this.setAnonymous();
    }

    private void setAnonymous() {
        ClassesProcessor$ClassNode classesProcessor$ClassNode;
        this.anonymous = false;
        if (this.newtype.type == 8 && this.newtype.arraydim == 0 && (classesProcessor$ClassNode = (ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get(this.newtype.value)) != null && classesProcessor$ClassNode.type == 2) {
            this.anonymous = true;
        }
    }

    public final VarType getExprType() {
        if (this.anonymous) {
            return ((ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get((Object)this.newtype.value)).anonimousClassType;
        }
        return this.newtype;
    }

    public final CheckTypesResult checkExprTypeBounds() {
        CheckTypesResult checkTypesResult = new CheckTypesResult();
        if (this.newtype.arraydim != 0) {
            for (Object object : ((NewExprent)this).lstDims) {
                checkTypesResult.addMinTypeExprent((Exprent)object, VarType.VARTYPE_BYTECHAR);
                checkTypesResult.addMaxTypeExprent((Exprent)object, VarType.VARTYPE_INT);
            }
            if (this.newtype.arraydim == 1) {
                Object object;
                object = ((NewExprent)this).newtype.copy();
                ((VarType)object).decArrayDim();
                for (Object object2 : ((NewExprent)this).lstArrayElements) {
                    checkTypesResult.addMinTypeExprent((Exprent)object2, VarType.getMinTypeInFamily(((VarType)object).type_family));
                    checkTypesResult.addMaxTypeExprent((Exprent)object2, (VarType)object);
                }
            }
        } else if (((NewExprent)this).constructor != null) {
            return ((NewExprent)this).constructor.checkExprTypeBounds();
        }
        return checkTypesResult;
    }

    public final List getAllExprents() {
        ArrayList arrayList = new ArrayList();
        if (this.newtype.arraydim == 0) {
            if (this.constructor != null) {
                arrayList.addAll(this.constructor.getLstParameters());
            }
        } else {
            arrayList.addAll(this.lstDims);
            arrayList.addAll(this.lstArrayElements);
        }
        return arrayList;
    }

    public final Exprent copy() {
        Exprent exprent2;
        ArrayList<Exprent> arrayList = new ArrayList<Exprent>();
        for (Exprent exprent2 : this.lstDims) {
            arrayList.add(exprent2.copy());
        }
        exprent2 = new NewExprent(this.newtype, arrayList);
        new NewExprent(this.newtype, arrayList).constructor = this.constructor == null ? null : (InvocationExprent)this.constructor.copy();
        ((NewExprent)exprent2).lstArrayElements = this.lstArrayElements;
        ((NewExprent)exprent2).directArrayInit = this.directArrayInit;
        ((NewExprent)exprent2).anonymous = this.anonymous;
        ((NewExprent)exprent2).enumconst = this.enumconst;
        return exprent2;
    }

    public final int getPrecedence() {
        return 1;
    }

    public final String toJava(int n) {
        StringBuffer stringBuffer = new StringBuffer();
        if (this.anonymous) {
            Object object;
            Object object2;
            Object object3;
            ClassesProcessor$ClassNode classesProcessor$ClassNode = (ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get(this.newtype.value);
            stringBuffer.append("(");
            if (this.constructor != null) {
                object3 = classesProcessor$ClassNode.superInvocation;
                object2 = (ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get(((InvocationExprent)object3).getClassname());
                object = null;
                if (object2 != null) {
                    if (((ClassesProcessor$ClassNode)object2).wrapper != null) {
                        object = object2.wrapper.getMethodWrapper((String)"<init>", (String)object3.getStringDescriptor()).signatureFields;
                    } else if (((ClassesProcessor$ClassNode)object2).type == 1 && (((ClassesProcessor$ClassNode)object2).access & 8) == 0 && !this.constructor.getLstParameters().isEmpty()) {
                        object = new ArrayList<VarVersionPaar>((Collection)Collections.nCopies(this.constructor.getLstParameters().size(), null));
                        object.set(0, new VarVersionPaar(-1, 0));
                    }
                }
                int n2 = 1;
                int n3 = 0;
                while (n3 < ((InvocationExprent)object3).getLstParameters().size()) {
                    if (object == null || object.get(n3) == null) {
                        if (n2 == 0) {
                            stringBuffer.append(", ");
                        }
                        object2 = (Exprent)((InvocationExprent)object3).getLstParameters().get(n3);
                        if (((Exprent)object2).type == 12 && (n2 = ((VarExprent)object2).getIndex0()) > 0 && n2 <= this.constructor.getLstParameters().size()) {
                            object2 = (Exprent)this.constructor.getLstParameters().get(n2 - 1);
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        ExprProcessor.getCastedExprent((Exprent)object2, object3.getDescriptor().params[n3], stringBuilder, n, true);
                        stringBuffer.append((CharSequence)stringBuilder);
                        n2 = 0;
                    }
                    ++n3;
                }
            }
            if (!this.enumconst) {
                object3 = null;
                if (this.constructor != null) {
                    object3 = NewExprent.getQualifiedNewInstance(classesProcessor$ClassNode.anonimousClassType.value, this.constructor.getLstParameters(), n);
                }
                object2 = ExprProcessor.getCastTypeName(classesProcessor$ClassNode.anonimousClassType);
                if (object3 != null) {
                    object = (ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get(classesProcessor$ClassNode.anonimousClassType.value);
                    object2 = object != null ? ((ClassesProcessor$ClassNode)object).simpleName : ((String)object2).substring(((String)object2).lastIndexOf(46) + 1);
                }
                stringBuffer.insert(0, "new " + (String)object2);
                if (object3 != null) {
                    stringBuffer.insert(0, String.valueOf(object3) + ".");
                }
            }
            stringBuffer.append(")");
            object3 = new StringWriter();
            object2 = new BufferedWriter((Writer)object3);
            object = new ClassWriter();
            try {
                ((ClassWriter)object).classToJava(classesProcessor$ClassNode, (BufferedWriter)object2, n);
                ((BufferedWriter)object2).flush();
            }
            catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
            stringBuffer.append(((StringWriter)object3).toString());
        } else if (this.directArrayInit) {
            VarType varType = this.newtype.copy();
            varType.decArrayDim();
            stringBuffer.append("{");
            int n4 = 0;
            while (n4 < this.lstArrayElements.size()) {
                if (n4 > 0) {
                    stringBuffer.append(", ");
                }
                StringBuilder stringBuilder = new StringBuilder();
                ExprProcessor.getCastedExprent((Exprent)this.lstArrayElements.get(n4), varType, stringBuilder, n, false);
                stringBuffer.append((CharSequence)stringBuilder);
                ++n4;
            }
            stringBuffer.append("}");
        } else if (this.newtype.arraydim == 0) {
            Object object;
            Object object4;
            Object object5;
            if (this.constructor != null) {
                object5 = this.constructor.getLstParameters();
                object4 = (ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get(this.constructor.getClassname());
                object = null;
                if (object4 != null) {
                    if (((ClassesProcessor$ClassNode)object4).wrapper != null) {
                        object = object4.wrapper.getMethodWrapper((String)"<init>", (String)this.constructor.getStringDescriptor()).signatureFields;
                    } else if (((ClassesProcessor$ClassNode)object4).type == 1 && (((ClassesProcessor$ClassNode)object4).access & 8) == 0 && !this.constructor.getLstParameters().isEmpty()) {
                        object = new ArrayList<VarVersionPaar>((Collection)Collections.nCopies(object5.size(), null));
                        object.set(0, new VarVersionPaar(-1, 0));
                    }
                }
                stringBuffer.append("(");
                boolean bl = true;
                int n5 = 0;
                while (n5 < object5.size()) {
                    if (object == null || object.get(n5) == null) {
                        if (!bl) {
                            stringBuffer.append(", ");
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        ExprProcessor.getCastedExprent((Exprent)object5.get(n5), this.constructor.getDescriptor().params[n5], stringBuilder, n, true);
                        stringBuffer.append((CharSequence)stringBuilder);
                        bl = false;
                    }
                    ++n5;
                }
                stringBuffer.append(")");
            }
            if (!this.enumconst) {
                object5 = null;
                if (this.constructor != null) {
                    object5 = NewExprent.getQualifiedNewInstance(this.newtype.value, this.constructor.getLstParameters(), n);
                }
                object4 = ExprProcessor.getTypeName(this.newtype);
                if (object5 != null) {
                    object = (ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get(this.newtype.value);
                    object4 = object != null ? ((ClassesProcessor$ClassNode)object).simpleName : ((String)object4).substring(((String)object4).lastIndexOf(46) + 1);
                }
                stringBuffer.insert(0, "new " + (String)object4);
                if (object5 != null) {
                    stringBuffer.insert(0, String.valueOf(object5) + ".");
                }
            }
        } else {
            stringBuffer.append("new " + ExprProcessor.getTypeName(this.newtype));
            if (this.lstArrayElements.isEmpty()) {
                int n6 = 0;
                while (n6 < this.newtype.arraydim) {
                    stringBuffer.append("[" + (n6 < this.lstDims.size() ? ((Exprent)this.lstDims.get(n6)).toJava(n) : "") + "]");
                    ++n6;
                }
            } else {
                int n7 = 0;
                while (n7 < this.newtype.arraydim) {
                    stringBuffer.append("[]");
                    ++n7;
                }
                VarType varType = this.newtype.copy();
                varType.decArrayDim();
                stringBuffer.append("{");
                int n8 = 0;
                while (n8 < this.lstArrayElements.size()) {
                    if (n8 > 0) {
                        stringBuffer.append(", ");
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    ExprProcessor.getCastedExprent((Exprent)this.lstArrayElements.get(n8), varType, stringBuilder, n, false);
                    stringBuffer.append((CharSequence)stringBuilder);
                    ++n8;
                }
                stringBuffer.append("}");
            }
        }
        return stringBuffer.toString();
    }

    private static String getQualifiedNewInstance(String object, List list, int n) {
        block4: {
            boolean bl;
            block6: {
                block5: {
                    object = (ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get(object);
                    if (object == null || ((ClassesProcessor$ClassNode)object).type == 0 || (((ClassesProcessor$ClassNode)object).access & 8) != 0 || list.isEmpty()) break block4;
                    object = (Exprent)list.get(0);
                    bl = false;
                    if (((Exprent)object).type != 12) break block5;
                    Object object2 = (VarExprent)object;
                    StructClass structClass = ((ClassesProcessor$ClassNode)DecompilerContext.getProperty((String)"CURRENT_CLASSNODE")).classStruct;
                    if (structClass.qualifiedName.equals(object2 = (String)((VarExprent)object2).getProcessor().getThisvars().get(new VarVersionPaar((VarExprent)object2)))) break block6;
                }
                bl = true;
            }
            if (bl) {
                return ((Exprent)object).toJava(n);
            }
        }
        return null;
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof NewExprent) {
            object = (NewExprent)object;
            return InterpreterUtil.equalObjects(this.newtype, ((NewExprent)object).newtype) && InterpreterUtil.equalLists(this.lstDims, ((NewExprent)object).lstDims) && InterpreterUtil.equalObjects(this.constructor, ((NewExprent)object).constructor) && this.directArrayInit == ((NewExprent)object).directArrayInit && InterpreterUtil.equalLists(this.lstArrayElements, ((NewExprent)object).lstArrayElements);
        }
        return false;
    }

    public final void replaceExprent(Exprent exprent, Exprent exprent2) {
        if (exprent == this.constructor) {
            this.constructor = (InvocationExprent)exprent2;
        }
        if (this.constructor != null) {
            this.constructor.replaceExprent(exprent, exprent2);
        }
        int n = 0;
        while (n < this.lstDims.size()) {
            if (exprent == this.lstDims.get(n)) {
                this.lstDims.set(n, exprent2);
            }
            ++n;
        }
        n = 0;
        while (n < this.lstArrayElements.size()) {
            if (exprent == this.lstArrayElements.get(n)) {
                this.lstArrayElements.set(n, exprent2);
            }
            ++n;
        }
    }

    public final InvocationExprent getConstructor() {
        return this.constructor;
    }

    public final void setConstructor(InvocationExprent invocationExprent) {
        this.constructor = invocationExprent;
    }

    public final List getLstDims() {
        return this.lstDims;
    }

    public final VarType getNewtype() {
        return this.newtype;
    }

    public final List getLstArrayElements() {
        return this.lstArrayElements;
    }

    public final void setLstArrayElements(List list) {
        this.lstArrayElements = list;
    }

    public final void setDirectArrayInit() {
        this.directArrayInit = true;
    }

    public final boolean isAnonymous() {
        return this.anonymous;
    }

    public final void setEnumconst() {
        this.enumconst = true;
    }
}

