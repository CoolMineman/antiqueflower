/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main;

import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.main.rels.ClassWrapper;
import de.fernflower.main.rels.MethodWrapper;
import de.fernflower.modules.decompiler.exps.AssignmentExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FieldExprent;
import de.fernflower.modules.decompiler.exps.InvocationExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.stats.RootStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.StructField;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class InitializerProcessor {
    public static void extractDynamicInitializers(ClassWrapper classWrapper) {
        block19: {
            Object object;
            Object object2;
            Object object32;
            Object object4;
            Object object5;
            Object object6;
            Object object7 = classWrapper.getMethodWrapper("<clinit>", "()V");
            if (object7 != null && ((MethodWrapper)object7).root != null) {
                object6 = object7;
                object7 = classWrapper;
                RootStatement rootStatement = ((MethodWrapper)object6).root;
                object5 = ((ClassWrapper)object7).getClassStruct();
                object4 = InitializerProcessor.findFirstData(rootStatement);
                if (object4 != null) {
                    while (!((Statement)object4).getExprents().isEmpty()) {
                        object32 = (Exprent)((Statement)object4).getExprents().get(0);
                        boolean bl = false;
                        if (((Exprent)object32).type == 2) {
                            object2 = (AssignmentExprent)object32;
                            if (object2.getLeft().type == 5 && ((FieldExprent)(object = (FieldExprent)((AssignmentExprent)object2).getLeft())).isStatic() && ((FieldExprent)object).getClassname().equals(((StructClass)object5).qualifiedName)) {
                                object = InterpreterUtil.makeUniqueKey(((FieldExprent)object).getName(), ((FieldExprent)object).getDescriptor());
                                if (!((ClassWrapper)object7).getStaticFieldInitializers().containsKey(object)) {
                                    ((ClassWrapper)object7).getStaticFieldInitializers().addWithKey(((AssignmentExprent)object2).getRight(), object);
                                    ((Statement)object4).getExprents().remove(0);
                                    bl = true;
                                }
                            }
                        }
                        if (!bl) break;
                    }
                }
            }
            object7 = classWrapper;
            object6 = ((ClassWrapper)object7).getClassStruct();
            boolean bl = ((ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get((Object)object6.qualifiedName)).type == 2;
            object5 = new ArrayList();
            object4 = new ArrayList();
            for (Object object32 : ((ClassWrapper)object7).getMethods()) {
                if (!"<init>".equals(((MethodWrapper)object32).methodStruct.getName()) || ((MethodWrapper)object32).root == null) continue;
                object2 = InitializerProcessor.findFirstData(((MethodWrapper)object32).root);
                if (object2 != null && !((Statement)object2).getExprents().isEmpty()) {
                    object5.add(((Statement)object2).getExprents());
                    object4.add(object32);
                    object = (Exprent)((Statement)object2).getExprents().get(0);
                    if (bl || ((Exprent)object).type == 8 && InitializerProcessor.isInvocationInitConstructor((InvocationExprent)object, (MethodWrapper)object32, (ClassWrapper)object7, false)) {
                        continue;
                    }
                }
                break block19;
            }
            if (!object5.isEmpty()) {
                block2: while (true) {
                    object32 = null;
                    Object object8 = null;
                    int n = 0;
                    while (n < object5.size()) {
                        object = (List)object5.get(n);
                        if (object.size() < (bl ? 1 : 2)) break block2;
                        object = (Exprent)object.get(bl ? 0 : 1);
                        boolean bl2 = false;
                        if (((Exprent)object).type == 2) {
                            object = (AssignmentExprent)object;
                            if (object.getLeft().type == 5) {
                                FieldExprent fieldExprent = (FieldExprent)((AssignmentExprent)object).getLeft();
                                String string = InterpreterUtil.makeUniqueKey(fieldExprent.getName(), fieldExprent.getDescriptor());
                                if (!fieldExprent.isStatic() && fieldExprent.getClassname().equals(((StructClass)object6).qualifiedName) && ((ClassWrapper)object7).getClassStruct().getFields().containsKey(string) && InitializerProcessor.isExprentIndependent(((AssignmentExprent)object).getRight(), (MethodWrapper)object4.get(n))) {
                                    if (object32 == null) {
                                        object32 = string;
                                        object8 = ((AssignmentExprent)object).getRight();
                                    } else if (!((String)object32).equals(string) || !object8.equals(((AssignmentExprent)object).getRight())) break block2;
                                    bl2 = true;
                                }
                            }
                        }
                        if (!bl2) break block2;
                        ++n;
                    }
                    if (((ClassWrapper)object7).getDynamicFieldInitializers().containsKey(object32)) break;
                    ((ClassWrapper)object7).getDynamicFieldInitializers().addWithKey(object8, object32);
                    object = object5.iterator();
                    while (true) {
                        if (!object.hasNext()) continue block2;
                        ((List)object.next()).remove(bl ? 0 : 1);
                    }
                    break;
                }
            }
        }
        InitializerProcessor.liftConstructor(classWrapper);
        if (DecompilerContext.getOption("hes")) {
            InitializerProcessor.hideEmptySuper(classWrapper);
        }
    }

    private static void liftConstructor(ClassWrapper classWrapper) {
        block0: for (MethodWrapper methodWrapper : classWrapper.getMethods()) {
            if (!"<init>".equals(methodWrapper.methodStruct.getName()) || methodWrapper.root == null) continue;
            Object object = InitializerProcessor.findFirstData(methodWrapper.root);
            if (object == null) {
                return;
            }
            int n = 0;
            object = ((Statement)object).getExprents();
            Iterator iterator = object.iterator();
            while (iterator.hasNext()) {
                Object object2 = (Exprent)iterator.next();
                int n2 = 0;
                if (((Exprent)object2).type == 2) {
                    object2 = (AssignmentExprent)object2;
                    if (object2.getLeft().type == 5 && object2.getRight().type == 12 && ((FieldExprent)(object2 = (FieldExprent)((AssignmentExprent)object2).getLeft())).getClassname().equals(classWrapper.getClassStruct().qualifiedName) && (object2 = classWrapper.getClassStruct().getField(((FieldExprent)object2).getName(), ((FieldExprent)object2).getDescriptor())) != null && (((StructField)object2).access_flags & 0x10) != 0) {
                        n2 = 1;
                    }
                } else if (n > 0 && ((Exprent)object2).type == 8 && InitializerProcessor.isInvocationInitConstructor((InvocationExprent)object2, methodWrapper, classWrapper, true)) {
                    object.add(0, (Exprent)object.remove(n));
                    n2 = 2;
                }
                if (n2 != true) continue block0;
                ++n;
            }
        }
    }

    private static void hideEmptySuper(ClassWrapper classWrapper) {
        for (MethodWrapper methodWrapper : classWrapper.getMethods()) {
            if (!"<init>".equals(methodWrapper.methodStruct.getName()) || methodWrapper.root == null) continue;
            Statement statement = InitializerProcessor.findFirstData(methodWrapper.root);
            if (statement == null || statement.getExprents().isEmpty()) {
                return;
            }
            Exprent exprent = (Exprent)statement.getExprents().get(0);
            if (exprent.type != 8 || !InitializerProcessor.isInvocationInitConstructor((InvocationExprent)(exprent = (InvocationExprent)exprent), methodWrapper, classWrapper, false) || !((InvocationExprent)exprent).getLstParameters().isEmpty()) continue;
            statement.getExprents().remove(0);
        }
    }

    private static boolean isExprentIndependent(Exprent object, MethodWrapper methodWrapper) {
        Object object2 = ((Exprent)object).getAllExprents(true);
        object2.add(object);
        object2 = object2.iterator();
        block4: while (object2.hasNext()) {
            object = (Exprent)object2.next();
            switch (((Exprent)object).type) {
                case 12: {
                    object = new VarVersionPaar((VarExprent)object);
                    if (methodWrapper.varproc.getExternvars().contains(object) || ((String)(object = methodWrapper.varproc.getVarName((VarVersionPaar)object))).equals("this") || ((String)object).endsWith(".this")) continue block4;
                    return false;
                }
                case 5: {
                    return false;
                }
            }
        }
        return true;
    }

    private static Statement findFirstData(Statement statement) {
        if (statement.getExprents() != null) {
            return statement;
        }
        if (statement.isLabeled()) {
            return null;
        }
        switch (statement.type) {
            case 2: 
            case 6: 
            case 10: 
            case 13: 
            case 15: {
                return InitializerProcessor.findFirstData(statement.getFirst());
            }
        }
        return null;
    }

    private static boolean isInvocationInitConstructor(InvocationExprent invocationExprent, MethodWrapper methodWrapper, ClassWrapper classWrapper, boolean bl) {
        if (invocationExprent.getFunctype() == 2 && invocationExprent.getInstance().type == 12) {
            Object object = (VarExprent)invocationExprent.getInstance();
            object = new VarVersionPaar((VarExprent)object);
            if ((String)methodWrapper.varproc.getThisvars().get(object) != null && (bl || !classWrapper.getClassStruct().qualifiedName.equals(invocationExprent.getClassname()))) {
                return true;
            }
        }
        return false;
    }
}

