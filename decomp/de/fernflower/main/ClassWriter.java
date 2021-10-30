/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main;

import de.fernflower.main.AssertProcessor;
import de.fernflower.main.ClassReference14Processor;
import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.main.EnumProcessor;
import de.fernflower.main.InitializerProcessor;
import de.fernflower.main.rels.ClassWrapper;
import de.fernflower.main.rels.MethodWrapper;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.exps.AnnotationExprent;
import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.NewExprent;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.modules.renamer.PoolInterceptor;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.StructField;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.attr.StructAnnDefaultAttribute;
import de.fernflower.struct.attr.StructAnnotationAttribute;
import de.fernflower.struct.attr.StructAnnotationParameterAttribute;
import de.fernflower.struct.attr.StructConstantValueAttribute;
import de.fernflower.struct.attr.StructExceptionsAttribute;
import de.fernflower.struct.attr.StructGenericSignatureAttribute;
import de.fernflower.struct.consts.PrimitiveConstant;
import de.fernflower.struct.gen.FieldDescriptor;
import de.fernflower.struct.gen.MethodDescriptor;
import de.fernflower.struct.gen.VarType;
import de.fernflower.struct.gen.generics.GenericClassDescriptor;
import de.fernflower.struct.gen.generics.GenericFieldDescriptor;
import de.fernflower.struct.gen.generics.GenericMain;
import de.fernflower.struct.gen.generics.GenericMethodDescriptor;
import de.fernflower.struct.gen.generics.GenericType;
import de.fernflower.util.InterpreterUtil;
import de.fernflower.util.VBStyleCollection;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public final class ClassWriter {
    private static final int[] modval_class = new int[]{1, 4, 2, 1024, 8, 16, 2048};
    private static final String[] modstr_class = new String[]{"public ", "protected ", "private ", "abstract ", "static ", "final ", "strictfp "};
    private static final int[] modval_field = new int[]{1, 4, 2, 8, 16, 128, 64};
    private static final String[] modstr_field = new String[]{"public ", "protected ", "private ", "static ", "final ", "transient ", "volatile "};
    private static final int[] modval_meth = new int[]{1, 4, 2, 1024, 8, 16, 32, 256, 2048};
    private static final String[] modstr_meth = new String[]{"public ", "protected ", "private ", "abstract ", "static ", "final ", "synchronized ", "native ", "strictfp "};
    private static final HashSet mod_notinterface = new HashSet(Arrays.asList(1024, 8));
    private static final HashSet mod_notinterface_fields = new HashSet(Arrays.asList(1, 8, 16));
    private static final HashSet mod_notinterface_meth = new HashSet(Arrays.asList(1, 1024));
    private ClassReference14Processor ref14processor = new ClassReference14Processor();
    private PoolInterceptor interceptor = DecompilerContext.getPoolInterceptor();

    public final void classToJava(ClassesProcessor$ClassNode classesProcessor$ClassNode, BufferedWriter bufferedWriter, int n) {
        boolean bl;
        boolean bl2;
        int n2;
        Object object;
        Object object22;
        ClassWrapper classWrapper = classesProcessor$ClassNode.wrapper;
        StructClass structClass = classWrapper.getClassStruct();
        ClassesProcessor$ClassNode classesProcessor$ClassNode2 = (ClassesProcessor$ClassNode)DecompilerContext.getProperty("CURRENT_CLASSNODE");
        DecompilerContext.setProperty("CURRENT_CLASSNODE", classesProcessor$ClassNode);
        Object object3 = classesProcessor$ClassNode;
        Object object42 = this;
        Object object5 = ((ClassesProcessor$ClassNode)object3).wrapper;
        StructClass structClass2 = ((ClassWrapper)object5).getClassStruct();
        InitializerProcessor.extractDynamicInitializers((ClassWrapper)object5);
        if (((ClassesProcessor$ClassNode)object3).type == 0 && DecompilerContext.getOption("dc4")) {
            ((ClassWriter)object42).ref14processor.processClassReferences((ClassesProcessor$ClassNode)object3);
        }
        if (DecompilerContext.getOption("den") && (structClass2.access_flags & 0x4000) != 0) {
            EnumProcessor.clearEnum((ClassWrapper)object5);
        }
        if (DecompilerContext.getOption("das")) {
            AssertProcessor.findAssertionField((ClassWrapper)object5);
        }
        DecompilerContext.getLogger().startWriteClass(structClass.qualifiedName);
        int n3 = n;
        object5 = bufferedWriter;
        object3 = classesProcessor$ClassNode;
        object42 = this;
        if (((ClassesProcessor$ClassNode)object3).type == 2) {
            ((Writer)object5).write(" {");
            ((BufferedWriter)object5).newLine();
        } else {
            int[] nArray;
            StructGenericSignatureAttribute structGenericSignatureAttribute;
            Object object6;
            object22 = InterpreterUtil.getIndentString(n3);
            object = null;
            object = ((ClassesProcessor$ClassNode)object3).wrapper.getClassStruct();
            n2 = ((ClassesProcessor$ClassNode)object3).type == 0 ? ((StructClass)object).access_flags : ((ClassesProcessor$ClassNode)object3).access;
            bl2 = (n2 & 0x200) != 0;
            bl = (n2 & 0x2000) != 0;
            boolean bl3 = DecompilerContext.getOption("den") && (n2 & 0x4000) != 0;
            boolean bl4 = ((StructClass)object).getAttributes().containsKey("Deprecated");
            if (((ClassWriter)object42).interceptor != null && (object6 = ((ClassWriter)object42).interceptor.getOldName(((StructClass)object).qualifiedName)) != null) {
                ((Writer)object5).write((String)object22);
                ((Writer)object5).write("// $FF: renamed from: " + super.getDescriptorPrintOut((String)object6, 0));
                ((BufferedWriter)object5).newLine();
            }
            object6 = null;
            for (Object object42 : ClassWriter.getAllAnnotations(((StructClass)object).getAttributes())) {
                if ("java/lang/Deprecated".equals(((AnnotationExprent)object42).getClassname())) {
                    bl4 = false;
                }
                ((Writer)object5).write(((AnnotationExprent)object42).toJava(n3));
                ((BufferedWriter)object5).newLine();
            }
            boolean bl5 = false;
            if ((n2 & 0x1000) != 0 || ((StructClass)object).getAttributes().containsKey("Synthetic")) {
                ((Writer)object5).write((String)object22);
                ((Writer)object5).write("// $FF: synthetic class");
                ((BufferedWriter)object5).newLine();
            }
            if (bl4) {
                ((Writer)object5).write((String)object22);
                ((Writer)object5).write("@Deprecated");
                ((BufferedWriter)object5).newLine();
            }
            ((Writer)object5).write((String)object22);
            if (bl3) {
                n2 = n2 & 0xFFFFFBFF & 0xFFFFFFEF;
            }
            int n4 = 0;
            while (n4 < modval_class.length) {
                if (!(bl2 && mod_notinterface.contains(modval_class[n4]) || (n2 & modval_class[n4]) == 0)) {
                    ((Writer)object5).write(modstr_class[n4]);
                }
                ++n4;
            }
            if (bl3) {
                ((Writer)object5).write("enum ");
            } else if (bl2) {
                if (bl) {
                    ((Writer)object5).write("@");
                }
                ((Writer)object5).write("interface ");
            } else {
                ((Writer)object5).write("class ");
            }
            GenericClassDescriptor genericClassDescriptor = null;
            if (DecompilerContext.getOption("dgs") && (structGenericSignatureAttribute = (StructGenericSignatureAttribute)((StructClass)object).getAttributes().getWithKey("Signature")) != null) {
                genericClassDescriptor = GenericMain.parseClassSignature(structGenericSignatureAttribute.getSignature());
            }
            ((Writer)object5).write(((ClassesProcessor$ClassNode)object3).simpleName);
            if (genericClassDescriptor != null && !genericClassDescriptor.fparameters.isEmpty()) {
                ((Writer)object5).write("<");
                int n5 = 0;
                while (n5 < genericClassDescriptor.fparameters.size()) {
                    if (n5 > 0) {
                        ((Writer)object5).write(", ");
                    }
                    ((Writer)object5).write(String.valueOf((String)genericClassDescriptor.fparameters.get(n5)) + " extends ");
                    object3 = (List)genericClassDescriptor.fbounds.get(n5);
                    ((Writer)object5).write(GenericMain.getTypeName((GenericType)object3.get(0)));
                    n3 = 1;
                    while (n3 < object3.size()) {
                        ((Writer)object5).write(" & " + GenericMain.getTypeName((GenericType)object3.get(n3)));
                        ++n3;
                    }
                    ++n5;
                }
                ((Writer)object5).write(">");
            }
            ((Writer)object5).write(" ");
            if (!bl3 && !bl2 && ((StructClass)object).superClass != null) {
                VarType varType = null;
                varType = new VarType((String)object.superClass.value, true);
                if (!VarType.VARTYPE_OBJECT.equals(varType)) {
                    ((Writer)object5).write("extends ");
                    if (genericClassDescriptor != null) {
                        ((Writer)object5).write(GenericMain.getTypeName(genericClassDescriptor.superclass));
                    } else {
                        ((Writer)object5).write(ExprProcessor.getCastTypeName(varType));
                    }
                    ((Writer)object5).write(" ");
                }
            }
            if (!bl && (nArray = ((StructClass)object).getInterfaces()).length > 0) {
                ((Writer)object5).write(bl2 ? "extends " : "implements ");
                int n6 = 0;
                while (n6 < nArray.length) {
                    if (n6 > 0) {
                        ((Writer)object5).write(", ");
                    }
                    if (genericClassDescriptor != null) {
                        ((Writer)object5).write(GenericMain.getTypeName((GenericType)genericClassDescriptor.superinterfaces.get(n6)));
                    } else {
                        ((Writer)object5).write(ExprProcessor.getCastTypeName(new VarType(((StructClass)object).getInterface(n6), true)));
                    }
                    ++n6;
                }
                ((Writer)object5).write(" ");
            }
            ((Writer)object5).write("{");
            ((BufferedWriter)object5).newLine();
        }
        StringWriter stringWriter = new StringWriter();
        Iterator iterator = new BufferedWriter(stringWriter);
        int n7 = 1;
        n3 = 0;
        for (Object object22 : structClass.getMethods()) {
            n2 = ((StructMethod)object22).getAccessFlags();
            bl2 = (n2 & 0x1000) != 0 || ((StructMethod)object22).getAttributes().containsKey("Synthetic");
            boolean bl6 = bl = (n2 & 0x40) != 0;
            if (bl2 && DecompilerContext.getOption("rsy") || bl && DecompilerContext.getOption("rbr") || classWrapper.getHideMembers().contains(InterpreterUtil.makeUniqueKey(((StructMethod)object22).getName(), ((StructMethod)object22).getDescriptor()))) continue;
            if (n3 == 0 && (n7 == 0 || classesProcessor$ClassNode.type != 2)) {
                ((BufferedWriter)((Object)iterator)).newLine();
                n7 = 0;
            }
            int n8 = n3 = this.methodToJava(classesProcessor$ClassNode, (StructMethod)object22, (BufferedWriter)((Object)iterator), n + 1) ? 0 : 1;
        }
        ((BufferedWriter)((Object)iterator)).flush();
        object22 = new StringWriter();
        object = new BufferedWriter((Writer)object22);
        n2 = 0;
        bl2 = false;
        for (StructField structField : structClass.getFields()) {
            n7 = structField.access_flags;
            if (((n7 & 0x1000) != 0 || structField.getAttributes().containsKey("Synthetic")) && DecompilerContext.getOption("rsy") || classWrapper.getHideMembers().contains(InterpreterUtil.makeUniqueKey(structField.getName(), structField.getDescriptor()))) continue;
            if (DecompilerContext.getOption("den") && (n7 & 0x4000) != 0) {
                if (bl2) {
                    ((Writer)object).write(",");
                    ((BufferedWriter)object).newLine();
                } else {
                    bl2 = true;
                }
            } else if (bl2) {
                ((Writer)object).write(";");
                ((BufferedWriter)object).newLine();
                bl2 = false;
            }
            this.fieldToJava(classWrapper, structClass, structField, (BufferedWriter)object, n + 1);
            ++n2;
        }
        if (bl2) {
            ((Writer)object).write(";");
            ((BufferedWriter)object).newLine();
        }
        ((BufferedWriter)object).flush();
        if (n2 > 0) {
            bufferedWriter.newLine();
            bufferedWriter.write(((StringWriter)object22).toString());
            bufferedWriter.newLine();
        }
        bufferedWriter.write(stringWriter.toString());
        for (ClassesProcessor$ClassNode classesProcessor$ClassNode3 : classesProcessor$ClassNode.nested) {
            if (classesProcessor$ClassNode3.type != 1) continue;
            StructClass structClass3 = classesProcessor$ClassNode3.classStruct;
            if (((structClass3.access_flags & 0x1000) != 0 || structClass3.getAttributes().containsKey("Synthetic")) && DecompilerContext.getOption("rsy") || classWrapper.getHideMembers().contains(structClass3.qualifiedName)) continue;
            bufferedWriter.newLine();
            this.classToJava(classesProcessor$ClassNode3, bufferedWriter, n + 1);
        }
        bufferedWriter.write(InterpreterUtil.getIndentString(n));
        bufferedWriter.write("}");
        if (classesProcessor$ClassNode.type != 2) {
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        DecompilerContext.setProperty("CURRENT_CLASSNODE", classesProcessor$ClassNode2);
        DecompilerContext.getLogger().endWriteClass();
    }

    private void fieldToJava(ClassWrapper object, StructClass structClass, StructField structField, BufferedWriter bufferedWriter, int n) {
        StructGenericSignatureAttribute structGenericSignatureAttribute;
        boolean bl;
        String[] stringArray;
        String string;
        Object object2 = InterpreterUtil.getIndentString(n);
        boolean bl2 = (structClass.access_flags & 0x200) != 0;
        int n2 = structField.access_flags;
        if (((ClassWriter)object3).interceptor != null && (string = ((ClassWriter)object3).interceptor.getOldName(String.valueOf(structClass.qualifiedName) + " " + structField.getName() + " " + structField.getDescriptor())) != null) {
            stringArray = string.split(" ");
            bufferedWriter.write((String)object2);
            bufferedWriter.write("// $FF: renamed from: " + stringArray[1] + " " + super.getDescriptorPrintOut(stringArray[2], 1));
            bufferedWriter.newLine();
        }
        boolean bl3 = structField.getAttributes().containsKey("Deprecated");
        stringArray = null;
        for (Object object3 : ClassWriter.getAllAnnotations(structField.getAttributes())) {
            if ("java/lang/Deprecated".equals(((AnnotationExprent)object3).getClassname())) {
                bl3 = false;
            }
            bufferedWriter.write(((AnnotationExprent)object3).toJava(n));
            bufferedWriter.newLine();
        }
        int n3 = (n2 & 0x1000) == 0 && !structField.getAttributes().containsKey("Synthetic") ? 0 : 1;
        boolean bl4 = bl = DecompilerContext.getOption("den") && (n2 & 0x4000) != 0;
        if (n3 != 0) {
            bufferedWriter.write((String)object2);
            bufferedWriter.write("// $FF: synthetic field");
            bufferedWriter.newLine();
        }
        if (bl3) {
            bufferedWriter.write((String)object2);
            bufferedWriter.write("@Deprecated");
            bufferedWriter.newLine();
        }
        bufferedWriter.write((String)object2);
        if (!bl) {
            n3 = 0;
            while (n3 < modval_field.length) {
                if (!(bl2 && mod_notinterface_fields.contains(modval_field[n3]) || (n2 & modval_field[n3]) == 0)) {
                    bufferedWriter.write(modstr_field[n3]);
                }
                ++n3;
            }
        }
        VarType varType = new VarType(structField.getDescriptor(), false);
        object2 = null;
        if (DecompilerContext.getOption("dgs") && (structGenericSignatureAttribute = (StructGenericSignatureAttribute)structField.getAttributes().getWithKey("Signature")) != null) {
            object2 = GenericMain.parseFieldSignature(structGenericSignatureAttribute.getSignature());
        }
        if (!bl) {
            if (object2 != null) {
                bufferedWriter.write(GenericMain.getTypeName(((GenericFieldDescriptor)object2).type));
            } else {
                bufferedWriter.write(ExprProcessor.getCastTypeName(varType));
            }
            bufferedWriter.write(" ");
        }
        bufferedWriter.write(structField.getName());
        Exprent exprent = (n2 & 8) != 0 ? (Exprent)((ClassWrapper)object).getStaticFieldInitializers().getWithKey(InterpreterUtil.makeUniqueKey(structField.getName(), structField.getDescriptor())) : (Exprent)((ClassWrapper)object).getDynamicFieldInitializers().getWithKey(InterpreterUtil.makeUniqueKey(structField.getName(), structField.getDescriptor()));
        if (exprent != null) {
            if (bl && exprent.type == 10) {
                object = (NewExprent)exprent;
                ((NewExprent)object).setEnumconst();
                bufferedWriter.write(((NewExprent)object).toJava(n));
            } else {
                bufferedWriter.write(" = ");
                bufferedWriter.write(exprent.toJava(n));
            }
        } else if ((n2 & 0x10) != 0 && (n2 & 8) != 0 && (object = (StructConstantValueAttribute)structField.getAttributes().getWithKey("ConstantValue")) != null) {
            object = structClass.getPool().getPrimitiveConstant(((StructConstantValueAttribute)object).getIndex());
            bufferedWriter.write(" = ");
            bufferedWriter.write(new ConstExprent(varType, ((PrimitiveConstant)object).value).toJava(n));
        }
        if (!bl) {
            bufferedWriter.write(";");
            bufferedWriter.newLine();
        }
    }

    /*
     * WARNING - void declaration
     */
    private boolean methodToJava(ClassesProcessor$ClassNode classesProcessor$ClassNode, StructMethod structMethod, BufferedWriter bufferedWriter, int n) {
        void var11_25;
        int n2;
        Object object;
        Object object2;
        String string;
        StructGenericSignatureAttribute structGenericSignatureAttribute;
        int n3;
        boolean bl;
        Object object32;
        ClassWrapper classWrapper = classesProcessor$ClassNode.wrapper;
        StructClass structClass = classWrapper.getClassStruct();
        MethodWrapper methodWrapper = classWrapper.getMethodWrapper(structMethod.getName(), structMethod.getDescriptor());
        MethodWrapper methodWrapper2 = (MethodWrapper)DecompilerContext.getProperty("CURRENT_METHOD_WRAPPER");
        DecompilerContext.setProperty("CURRENT_METHOD_WRAPPER", methodWrapper);
        boolean bl2 = (structClass.access_flags & 0x200) != 0;
        boolean bl22 = (structClass.access_flags & 0x2000) != 0;
        boolean n4 = structMethod.getAttributes().containsKey("Deprecated");
        String string2 = InterpreterUtil.getIndentString(n);
        boolean bl3 = false;
        boolean bl4 = false;
        boolean bl5 = false;
        MethodDescriptor methodDescriptor = MethodDescriptor.parseDescriptor(structMethod.getDescriptor());
        StringWriter stringWriter = new StringWriter();
        BufferedWriter bufferedWriter2 = new BufferedWriter(stringWriter);
        int n5 = structMethod.getAccessFlags();
        if ((n5 & 0x100) != 0) {
            n5 &= 0xFFFFF7FF;
        }
        if ("<clinit>".equals(structMethod.getName())) {
            n5 &= 8;
        }
        if (((ClassWriter)this).interceptor != null && (object32 = ((ClassWriter)this).interceptor.getOldName(String.valueOf(structClass.qualifiedName) + " " + structMethod.getName() + " " + structMethod.getDescriptor())) != null) {
            object32 = object32.split(" ");
            bufferedWriter2.write(string2);
            bufferedWriter2.write("// $FF: renamed from: " + object32[1] + " " + super.getDescriptorPrintOut(object32[2], 2));
            bufferedWriter2.newLine();
        }
        object32 = null;
        for (Object object32 : ClassWriter.getAllAnnotations(structMethod.getAttributes())) {
            if ("java/lang/Deprecated".equals(object32.getClassname())) {
                bl = false;
            }
            bufferedWriter2.write(object32.toJava(n));
            bufferedWriter2.newLine();
        }
        boolean bl6 = (n5 & 0x1000) != 0 || structMethod.getAttributes().containsKey("Synthetic");
        int n6 = n3 = (n5 & 0x40) != 0 ? 1 : 0;
        if (bl6) {
            bufferedWriter2.write(string2);
            bufferedWriter2.write("// $FF: synthetic method");
            bufferedWriter2.newLine();
        }
        if (n3 != 0) {
            bufferedWriter2.write(string2);
            bufferedWriter2.write("// $FF: bridge method");
            bufferedWriter2.newLine();
        }
        if (bl) {
            bufferedWriter2.write(string2);
            bufferedWriter2.write("@Deprecated");
            bufferedWriter2.newLine();
        }
        bufferedWriter2.write(string2);
        n3 = 0;
        while (n3 < modval_meth.length) {
            if (!(bl2 && mod_notinterface_meth.contains(modval_meth[n3]) || (n5 & modval_meth[n3]) == 0)) {
                bufferedWriter2.write(modstr_meth[n3]);
            }
            ++n3;
        }
        GenericMethodDescriptor genericMethodDescriptor = null;
        if (DecompilerContext.getOption("dgs") && (structGenericSignatureAttribute = (StructGenericSignatureAttribute)structMethod.getAttributes().getWithKey("Signature")) != null) {
            genericMethodDescriptor = GenericMain.parseMethodSignature(structGenericSignatureAttribute.getSignature());
            if (methodDescriptor.params.length != genericMethodDescriptor.params.size()) {
                DecompilerContext.getLogger().writeMessage("Inconsistent generic signature in method " + structMethod.getName() + " " + structMethod.getDescriptor(), 3);
                genericMethodDescriptor = null;
            }
        }
        if ("<init>".equals(string = structMethod.getName())) {
            if (classesProcessor$ClassNode.type == 2) {
                string = "";
                bl5 = true;
            } else {
                string = classesProcessor$ClassNode.simpleName;
                bl4 = true;
            }
        } else if ("<clinit>".equals(string)) {
            string = "";
            bl3 = true;
        }
        boolean bl7 = false;
        boolean bl8 = false;
        if (!bl3 && !bl5) {
            int n7;
            boolean bl9 = bl6 = (structMethod.getAccessFlags() & 8) == 0;
            if (genericMethodDescriptor != null && !genericMethodDescriptor.fparameters.isEmpty()) {
                bufferedWriter2.write("<");
                int n8 = 0;
                while (n8 < genericMethodDescriptor.fparameters.size()) {
                    if (n8 > 0) {
                        bufferedWriter2.write(", ");
                    }
                    bufferedWriter2.write(String.valueOf((String)genericMethodDescriptor.fparameters.get(n8)) + " extends ");
                    object2 = (List)genericMethodDescriptor.fbounds.get(n8);
                    bufferedWriter2.write(GenericMain.getTypeName((GenericType)object2.get(0)));
                    n7 = 1;
                    while (n7 < object2.size()) {
                        bufferedWriter2.write(" & " + GenericMain.getTypeName((GenericType)object2.get(n7)));
                        ++n7;
                    }
                    ++n8;
                }
                bufferedWriter2.write("> ");
            }
            if (!bl4) {
                if (genericMethodDescriptor != null) {
                    bufferedWriter2.write(GenericMain.getTypeName(genericMethodDescriptor.ret));
                } else {
                    bufferedWriter2.write(ExprProcessor.getCastTypeName(methodDescriptor.ret));
                }
                bufferedWriter2.write(" ");
            }
            bufferedWriter2.write(string);
            bufferedWriter2.write("(");
            object = ClassWriter.getAllParameterAnnotations(structMethod.getAttributes());
            object2 = methodWrapper.signatureFields;
            n7 = -1;
            int n9 = 0;
            while (n9 < methodDescriptor.params.length) {
                if (object2 == null || object2.get(n9) == null) {
                    n7 = n9;
                }
                ++n9;
            }
            n9 = 1;
            n2 = bl6 ? 1 : 0;
            int n10 = 0;
            while (n10 < methodDescriptor.params.length) {
                if (object2 == null || object2.get(n10) == null) {
                    String string3;
                    int n11;
                    Object methodWrapper3;
                    if (n9 == 0) {
                        bufferedWriter2.write(", ");
                    }
                    if (object.size() > n10) {
                        methodWrapper3 = (List)object.get(n10);
                        n11 = 0;
                        while (n11 < methodWrapper3.size()) {
                            AnnotationExprent annotationExprent = (AnnotationExprent)methodWrapper3.get(n11);
                            if (annotationExprent.getAnnotationType() == 1) {
                                bufferedWriter2.newLine();
                                bufferedWriter2.write(annotationExprent.toJava(n + 1));
                            } else {
                                bufferedWriter2.write(annotationExprent.toJava(0));
                            }
                            bufferedWriter2.write(" ");
                            ++n11;
                        }
                    }
                    if (methodWrapper.varproc.getVarFinal(new VarVersionPaar(n2, 0)) == 2) {
                        bufferedWriter2.write("final ");
                    }
                    if (genericMethodDescriptor != null) {
                        methodWrapper3 = (GenericType)genericMethodDescriptor.params.get(n10);
                        n11 = n10 == n7 && (structMethod.getAccessFlags() & 0x80) != 0 && ((GenericType)methodWrapper3).arraydim > 0 ? 1 : 0;
                        if (n11 != 0) {
                            --((GenericType)methodWrapper3).arraydim;
                        }
                        if ("<undefinedtype>".equals(string3 = GenericMain.getTypeName((GenericType)methodWrapper3)) && DecompilerContext.getOption("uto")) {
                            string3 = ExprProcessor.getCastTypeName(VarType.VARTYPE_OBJECT);
                        }
                        bufferedWriter2.write(string3);
                        if (n11 != 0) {
                            bufferedWriter2.write(" ...");
                        }
                    } else {
                        methodWrapper3 = methodDescriptor.params[n10].copy();
                        n11 = n10 == n7 && (structMethod.getAccessFlags() & 0x80) != 0 && ((VarType)methodWrapper3).arraydim > 0 ? 1 : 0;
                        if (n11 != 0) {
                            ((VarType)methodWrapper3).decArrayDim();
                        }
                        if ("<undefinedtype>".equals(string3 = ExprProcessor.getCastTypeName((VarType)methodWrapper3)) && DecompilerContext.getOption("uto")) {
                            string3 = ExprProcessor.getCastTypeName(VarType.VARTYPE_OBJECT);
                        }
                        bufferedWriter2.write(string3);
                        if (n11 != 0) {
                            bufferedWriter2.write(" ...");
                        }
                    }
                    bufferedWriter2.write(" ");
                    methodWrapper3 = methodWrapper.varproc.getVarName(new VarVersionPaar(n2, 0));
                    bufferedWriter2.write((String)(methodWrapper3 == null ? "param" + n2 : methodWrapper3));
                    n9 = 0;
                    ++var11_25;
                }
                n2 += methodDescriptor.params[n10].stack_size;
                ++n10;
            }
            bufferedWriter2.write(")");
            StructExceptionsAttribute structExceptionsAttribute = (StructExceptionsAttribute)structMethod.getAttributes().getWithKey("Exceptions");
            if (genericMethodDescriptor != null && !genericMethodDescriptor.exceptions.isEmpty() || structExceptionsAttribute != null) {
                bl7 = true;
                bufferedWriter2.write(" throws ");
                int n12 = 0;
                while (n12 < structExceptionsAttribute.getThrowsExceptions().size()) {
                    if (n12 > 0) {
                        bufferedWriter2.write(", ");
                    }
                    if (genericMethodDescriptor != null && !genericMethodDescriptor.exceptions.isEmpty()) {
                        bufferedWriter2.write(GenericMain.getTypeName((GenericType)genericMethodDescriptor.exceptions.get(n12)));
                    } else {
                        VarType varType = new VarType(structExceptionsAttribute.getExcClassname(n12, structClass.getPool()), true);
                        bufferedWriter2.write(ExprProcessor.getCastTypeName(varType));
                    }
                    ++n12;
                }
            }
        }
        bl6 = false;
        if ((n5 & 0x500) != 0) {
            if (bl22 && (object = (StructAnnDefaultAttribute)structMethod.getAttributes().getWithKey("AnnotationDefault")) != null) {
                bufferedWriter2.write(" default ");
                bufferedWriter2.write(((StructAnnDefaultAttribute)object).getDefaultValue().toJava(n + 1));
            }
            bufferedWriter2.write(";");
            bufferedWriter2.newLine();
        } else {
            if (!bl3 && !bl5) {
                bufferedWriter2.write(" ");
            }
            bufferedWriter2.write("{");
            object = classWrapper.getMethodWrapper((String)structMethod.getName(), (String)structMethod.getDescriptor()).root;
            object2 = new StringWriter();
            BufferedWriter bufferedWriter3 = new BufferedWriter((Writer)object2);
            if (object != null && !methodWrapper.decompiledWithErrors) {
                try {
                    String string4 = ((RootStatement)object).toJava(n + 1);
                    n2 = 0;
                    if (bl4 && var11_25 == false && !bl7 && DecompilerContext.getOption("hdc")) {
                        int n13 = 0;
                        for (MethodWrapper methodWrapper3 : classWrapper.getMethods()) {
                            if (!"<init>".equals(methodWrapper3.methodStruct.getName())) continue;
                            ++n13;
                        }
                        n2 = n13 == 1 ? 1 : 0;
                    }
                    bl6 = (bl3 || bl5 || n2 != 0) && string4.length() == 0;
                    bufferedWriter3.write(string4);
                }
                catch (Throwable throwable) {
                    if (DecompilerContext.getLogger().getShowStacktrace()) {
                        throwable.printStackTrace();
                    }
                    DecompilerContext.getLogger().writeMessage("Method " + structMethod.getName() + " " + structMethod.getDescriptor() + " couldn't be written.", 4);
                    methodWrapper.decompiledWithErrors = true;
                }
            }
            if (methodWrapper.decompiledWithErrors) {
                bufferedWriter3.write(InterpreterUtil.getIndentString(n + 1));
                bufferedWriter3.write("// $FF: Couldn't be decompiled");
                bufferedWriter3.newLine();
            }
            bufferedWriter3.flush();
            String string5 = ((StringWriter)object2).toString();
            if (string5.isEmpty()) {
                bufferedWriter2.write("}");
            } else {
                bufferedWriter2.newLine();
                bufferedWriter2.write(string5);
                bufferedWriter2.write(String.valueOf(string2) + "}");
            }
            bufferedWriter2.newLine();
        }
        bufferedWriter2.flush();
        if (!bl6) {
            bufferedWriter.write(stringWriter.toString());
        }
        DecompilerContext.setProperty("CURRENT_METHOD_WRAPPER", methodWrapper2);
        return !bl6;
    }

    private static List getAllAnnotations(VBStyleCollection vBStyleCollection) {
        Object object = new String[]{"RuntimeVisibleAnnotations", "RuntimeInvisibleAnnotations"};
        ArrayList arrayList = new ArrayList();
        String[] stringArray = object;
        int n = ((String[])object).length;
        int n2 = 0;
        while (n2 < n) {
            object = stringArray[n2];
            if ((object = (StructAnnotationAttribute)vBStyleCollection.getWithKey(object)) != null) {
                arrayList.addAll(((StructAnnotationAttribute)object).getAnnotations());
            }
            ++n2;
        }
        return arrayList;
    }

    private static List getAllParameterAnnotations(VBStyleCollection vBStyleCollection) {
        Object object = new String[]{"RuntimeVisibleParameterAnnotations", "RuntimeInvisibleParameterAnnotations"};
        ArrayList arrayList = new ArrayList();
        String[] stringArray = object;
        int n = ((String[])object).length;
        int n2 = 0;
        while (n2 < n) {
            object = stringArray[n2];
            if ((object = (StructAnnotationParameterAttribute)vBStyleCollection.getWithKey(object)) != null) {
                int n3 = 0;
                while (n3 < ((StructAnnotationParameterAttribute)object).getParamAnnotations().size()) {
                    List list = new ArrayList();
                    boolean bl = arrayList.size() <= n3;
                    if (!bl) {
                        list = (List)arrayList.get(n3);
                    }
                    list.addAll((Collection)((StructAnnotationParameterAttribute)object).getParamAnnotations().get(n3));
                    if (bl) {
                        arrayList.add(list);
                    } else {
                        arrayList.set(n3, list);
                    }
                    ++n3;
                }
            }
            ++n2;
        }
        return arrayList;
    }

    private String getDescriptorPrintOut(String charSequence, int n) {
        switch (n) {
            case 0: {
                return ExprProcessor.buildJavaClassName((String)charSequence);
            }
            case 1: {
                return ClassWriter.getTypePrintOut(FieldDescriptor.parseDescriptor((String)charSequence).type);
            }
        }
        MethodDescriptor methodDescriptor = MethodDescriptor.parseDescriptor((String)charSequence);
        charSequence = new StringBuilder("(");
        n = 1;
        VarType[] varTypeArray = methodDescriptor.params;
        int n2 = methodDescriptor.params.length;
        int n3 = 0;
        while (n3 < n2) {
            VarType varType = varTypeArray[n3];
            if (n != 0) {
                n = 0;
            } else {
                ((StringBuilder)charSequence).append(", ");
            }
            ((StringBuilder)charSequence).append(ClassWriter.getTypePrintOut(varType));
            ++n3;
        }
        ((StringBuilder)charSequence).append(") ");
        ((StringBuilder)charSequence).append(ClassWriter.getTypePrintOut(methodDescriptor.ret));
        return ((StringBuilder)charSequence).toString();
    }

    private static String getTypePrintOut(VarType object) {
        if ("<undefinedtype>".equals(object = ExprProcessor.getCastTypeName((VarType)object, false)) && DecompilerContext.getOption("uto")) {
            object = ExprProcessor.getCastTypeName(VarType.VARTYPE_OBJECT, false);
        }
        return object;
    }
}

