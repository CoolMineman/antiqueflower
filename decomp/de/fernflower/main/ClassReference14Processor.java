/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main;

import de.fernflower.main.ClassReference14Processor$1;
import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.main.rels.ClassWrapper;
import de.fernflower.main.rels.MethodWrapper;
import de.fernflower.modules.decompiler.exps.AssignmentExprent;
import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.ExitExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FieldExprent;
import de.fernflower.modules.decompiler.exps.FunctionExprent;
import de.fernflower.modules.decompiler.exps.InvocationExprent;
import de.fernflower.modules.decompiler.exps.NewExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.stats.BasicBlockStatement;
import de.fernflower.modules.decompiler.stats.CatchStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.struct.StructField;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.gen.MethodDescriptor;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import de.fernflower.util.VBStyleCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class ClassReference14Processor {
    private ExitExprent bodyexprent;
    private ExitExprent handlerexprent;

    public ClassReference14Processor() {
        InvocationExprent invocationExprent = new InvocationExprent();
        invocationExprent.setName("forName");
        invocationExprent.setClassname("java/lang/Class");
        invocationExprent.setStringDescriptor("(Ljava/lang/String;)Ljava/lang/Class;");
        invocationExprent.setDescriptor(MethodDescriptor.parseDescriptor("(Ljava/lang/String;)Ljava/lang/Class;"));
        invocationExprent.setStatic();
        invocationExprent.setLstParameters(Arrays.asList(new VarExprent(0, VarType.VARTYPE_STRING, null)));
        this.bodyexprent = new ExitExprent(0, invocationExprent, VarType.VARTYPE_CLASS);
        invocationExprent = new InvocationExprent();
        invocationExprent.setName("<init>");
        invocationExprent.setClassname("java/lang/NoClassDefFoundError");
        invocationExprent.setStringDescriptor("()V");
        invocationExprent.setFunctype();
        invocationExprent.setDescriptor(MethodDescriptor.parseDescriptor("()V"));
        NewExprent newExprent = new NewExprent(new VarType(8, 0, "java/lang/NoClassDefFoundError"), new ArrayList());
        newExprent.setConstructor(invocationExprent);
        invocationExprent = new InvocationExprent();
        invocationExprent.setName("initCause");
        invocationExprent.setClassname("java/lang/NoClassDefFoundError");
        invocationExprent.setStringDescriptor("(Ljava/lang/Throwable;)Ljava/lang/Throwable;");
        invocationExprent.setDescriptor(MethodDescriptor.parseDescriptor("(Ljava/lang/Throwable;)Ljava/lang/Throwable;"));
        invocationExprent.setInstance(newExprent);
        invocationExprent.setLstParameters(Arrays.asList(new VarExprent(2, new VarType(8, 0, "java/lang/ClassNotFoundException"), null)));
        this.handlerexprent = new ExitExprent(1, invocationExprent, null);
    }

    public final void processClassReferences(ClassesProcessor$ClassNode object) {
        ClassWrapper classWrapper = ((ClassesProcessor$ClassNode)object).wrapper;
        int n = classWrapper.getClassStruct().major_version;
        int n2 = classWrapper.getClassStruct().minor_version;
        if (n > 48 || n == 48 && n2 > 0) {
            return;
        }
        HashMap hashMap = new HashMap();
        ((ClassReference14Processor)((Object)classWrapper2)).findClassMethod((ClassesProcessor$ClassNode)object, hashMap);
        if (hashMap.isEmpty()) {
            return;
        }
        Object object2 = new HashSet();
        ((ClassReference14Processor)((Object)classWrapper2)).processClassRec((ClassesProcessor$ClassNode)object, hashMap, (HashSet)object2);
        if (!((HashSet)object2).isEmpty()) {
            object = ((HashSet)object2).iterator();
            while (object.hasNext()) {
                ClassWrapper classWrapper2 = (ClassWrapper)object.next();
                object2 = ((MethodWrapper)hashMap.get((Object)classWrapper2)).methodStruct;
                classWrapper2.getHideMembers().add(InterpreterUtil.makeUniqueKey(((StructMethod)object2).getName(), ((StructMethod)object2).getDescriptor()));
            }
        }
    }

    private void processClassRec(ClassesProcessor$ClassNode classesProcessor$ClassNode, HashMap hashMap, HashSet hashSet) {
        ClassWrapper classWrapper = classesProcessor$ClassNode.wrapper;
        for (MethodWrapper methodWrapper : classWrapper.getMethods()) {
            Object var7_9 = null;
            if (methodWrapper.root == null) continue;
            Map.Entry entry = null;
            methodWrapper.getOrBuildGraph().iterateExprents(new ClassReference14Processor$1(this, hashMap, hashSet));
        }
        int n = 0;
        while (n < 2) {
            VBStyleCollection vBStyleCollection = n == 0 ? classWrapper.getStaticFieldInitializers() : classWrapper.getDynamicFieldInitializers();
            int n2 = 0;
            while (n2 < vBStyleCollection.size()) {
                for (Map.Entry entry : hashMap.entrySet()) {
                    Object object = (Exprent)vBStyleCollection.get(n2);
                    if (this.replaceInvocations((Exprent)object, (ClassWrapper)entry.getKey(), (MethodWrapper)entry.getValue())) {
                        hashSet.add((ClassWrapper)entry.getKey());
                    }
                    if ((object = ClassReference14Processor.isClass14Invocation((Exprent)object, (ClassWrapper)entry.getKey(), (MethodWrapper)entry.getValue())) == null) continue;
                    vBStyleCollection.set(n2, new ConstExprent(VarType.VARTYPE_CLASS, ((String)object).replace('.', '/')));
                    hashSet.add((ClassWrapper)entry.getKey());
                }
                ++n2;
            }
            ++n;
        }
        for (ClassesProcessor$ClassNode classesProcessor$ClassNode2 : classesProcessor$ClassNode.nested) {
            this.processClassRec(classesProcessor$ClassNode2, hashMap, hashSet);
        }
    }

    private void findClassMethod(ClassesProcessor$ClassNode classesProcessor$ClassNode, HashMap hashMap) {
        boolean bl = DecompilerContext.getOption("nns");
        ClassWrapper classWrapper = classesProcessor$ClassNode.wrapper;
        for (Object object : classWrapper.getMethods()) {
            Object object2 = ((MethodWrapper)object).methodStruct;
            if ((((StructMethod)object2).getAccessFlags() & 0x1000) == 0 && !((StructMethod)object2).getAttributes().containsKey("Synthetic") && !bl || !((StructMethod)object2).getDescriptor().equals("(Ljava/lang/String;)Ljava/lang/Class;") || (((StructMethod)object2).getAccessFlags() & 8) == 0 || (object2 = ((MethodWrapper)object).root) == null || object2.getFirst().type != 7 || ((Statement)(object2 = (CatchStatement)((Statement)object2).getFirst())).getStats().size() != 2 || object2.getFirst().type != 8 || ((Statement)object2.getStats().get((int)1)).type != 8 || !((VarExprent)((CatchStatement)object2).getVars().get(0)).getVartype().equals(new VarType(8, 0, "java/lang/ClassNotFoundException"))) continue;
            BasicBlockStatement basicBlockStatement = (BasicBlockStatement)((Statement)object2).getFirst();
            object2 = (BasicBlockStatement)((Statement)object2).getStats().get(1);
            if (basicBlockStatement.getExprents().size() != 1 || ((Statement)object2).getExprents().size() != 1 || !this.bodyexprent.equals(basicBlockStatement.getExprents().get(0)) || !this.handlerexprent.equals(((Statement)object2).getExprents().get(0))) continue;
            hashMap.put(classWrapper, object);
            break;
        }
        for (Object object : classesProcessor$ClassNode.nested) {
            this.findClassMethod((ClassesProcessor$ClassNode)object, hashMap);
        }
    }

    private boolean replaceInvocations(Exprent exprent, ClassWrapper classWrapper, MethodWrapper methodWrapper) {
        boolean bl;
        boolean bl2 = false;
        block0: do {
            bl = false;
            for (Exprent exprent2 : exprent.getAllExprents()) {
                String string = ClassReference14Processor.isClass14Invocation(exprent2, classWrapper, methodWrapper);
                if (string != null) {
                    exprent.replaceExprent(exprent2, new ConstExprent(VarType.VARTYPE_CLASS, string.replace('.', '/')));
                    bl = true;
                    bl2 = true;
                    continue block0;
                }
                bl2 |= this.replaceInvocations(exprent2, classWrapper, methodWrapper);
            }
        } while (bl);
        return bl2;
    }

    private static String isClass14Invocation(Exprent exprent, ClassWrapper classWrapper, MethodWrapper methodWrapper) {
        Exprent exprent2;
        if (exprent.type == 6 && ((FunctionExprent)(exprent = (FunctionExprent)exprent)).getFunctype() == 36 && ((Exprent)exprent.getLstOperands().get((int)0)).type == 6 && ((FunctionExprent)(exprent2 = (FunctionExprent)((FunctionExprent)exprent).getLstOperands().get(0))).getFunctype() == 42 && ((Exprent)exprent2.getLstOperands().get((int)0)).type == 5 && ((Exprent)exprent2.getLstOperands().get((int)1)).type == 3 && ((ConstExprent)((FunctionExprent)exprent2).getLstOperands().get(1)).getConsttype().equals(VarType.VARTYPE_NULL)) {
            exprent2 = (FieldExprent)((FunctionExprent)exprent2).getLstOperands().get(0);
            Object object = (ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get(((FieldExprent)exprent2).getClassname());
            if (object != null && object.classStruct.qualifiedName.equals(classWrapper.getClassStruct().qualifiedName) && (object = classWrapper.getClassStruct().getField(((FieldExprent)exprent2).getName(), ((FieldExprent)exprent2).getDescriptor())) != null && (((StructField)object).access_flags & 8) != 0 && ((((StructField)object).access_flags & 0x1000) != 0 || ((StructField)object).getAttributes().containsKey("Synthetic") || DecompilerContext.getOption("nns")) && ((Exprent)exprent.getLstOperands().get((int)1)).type == 2 && ((Exprent)((FunctionExprent)exprent).getLstOperands().get(2)).equals(exprent2) && ((AssignmentExprent)(exprent = (AssignmentExprent)((FunctionExprent)exprent).getLstOperands().get(1))).getLeft().equals(exprent2) && exprent.getRight().type == 8 && ((InvocationExprent)(exprent = (InvocationExprent)((AssignmentExprent)exprent).getRight())).getClassname().equals(classWrapper.getClassStruct().qualifiedName) && ((InvocationExprent)exprent).getName().equals(methodWrapper.methodStruct.getName()) && ((InvocationExprent)exprent).getStringDescriptor().equals(methodWrapper.methodStruct.getDescriptor()) && ((Exprent)exprent.getLstParameters().get((int)0)).type == 3) {
                classWrapper.getHideMembers().add(InterpreterUtil.makeUniqueKey(((StructField)object).getName(), ((StructField)object).getDescriptor()));
                return ((ConstExprent)((InvocationExprent)exprent).getLstParameters().get(0)).getValue().toString();
            }
        }
        return null;
    }

    static /* synthetic */ boolean access$0(ClassReference14Processor classReference14Processor, Exprent exprent, ClassWrapper classWrapper, MethodWrapper methodWrapper) {
        return classReference14Processor.replaceInvocations(exprent, classWrapper, methodWrapper);
    }
}

