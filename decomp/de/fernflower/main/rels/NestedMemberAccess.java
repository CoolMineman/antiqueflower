/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.rels;

import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.main.collectors.VarNamesCollector;
import de.fernflower.main.rels.MethodWrapper;
import de.fernflower.modules.decompiler.exps.AssignmentExprent;
import de.fernflower.modules.decompiler.exps.ExitExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FieldExprent;
import de.fernflower.modules.decompiler.exps.InvocationExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph;
import de.fernflower.modules.decompiler.sforms.DirectNode;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.gen.MethodDescriptor;
import de.fernflower.util.InterpreterUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public final class NestedMemberAccess {
    private boolean notSetSync;
    private HashMap mapMethodType = new HashMap();

    public final void propagateMemberAccess(ClassesProcessor$ClassNode classesProcessor$ClassNode) {
        if (classesProcessor$ClassNode.nested.isEmpty()) {
            return;
        }
        this.notSetSync = DecompilerContext.getOption("nns");
        this.computeMethodTypes(classesProcessor$ClassNode);
        this.eliminateStaticAccess(classesProcessor$ClassNode);
    }

    private void computeMethodTypes(ClassesProcessor$ClassNode classesProcessor$ClassNode) {
        for (Object object : classesProcessor$ClassNode.nested) {
            this.computeMethodTypes((ClassesProcessor$ClassNode)object);
        }
        for (Object object : classesProcessor$ClassNode.wrapper.getMethods()) {
            this.computeMethodType(classesProcessor$ClassNode, (MethodWrapper)object);
        }
    }

    private void computeMethodType(ClassesProcessor$ClassNode object, MethodWrapper methodWrapper) {
        int n = 1;
        if (methodWrapper.root != null) {
            Object object2 = methodWrapper.getOrBuildGraph();
            int n2 = methodWrapper.methodStruct.getAccessFlags();
            if (((n2 & 0x1000) != 0 || methodWrapper.methodStruct.getAttributes().containsKey("Synthetic") || this.notSetSync) && (n2 & 8) != 0 && ((DirectGraph)object2).nodes.size() == 2) {
                if (object2.first.exprents.size() == 1) {
                    Exprent exprent = (Exprent)object2.first.exprents.get(0);
                    int n3 = MethodDescriptor.parseDescriptor((String)methodWrapper.methodStruct.getDescriptor()).params.length;
                    Exprent exprent2 = exprent;
                    if (exprent.type == 4 && ((ExitExprent)(exprent = (ExitExprent)exprent)).getExittype() == 0 && ((ExitExprent)exprent).getValue() != null) {
                        exprent2 = ((ExitExprent)exprent).getValue();
                    }
                    switch (exprent2.type) {
                        case 5: {
                            exprent = (FieldExprent)exprent2;
                            if ((n3 != 1 || ((FieldExprent)exprent).isStatic()) && (n3 != 0 || !((FieldExprent)exprent).isStatic()) || !((FieldExprent)exprent).getClassname().equals(object.classStruct.qualifiedName) || !((FieldExprent)exprent).isStatic() && (exprent.getInstance().type != 12 || ((VarExprent)((FieldExprent)exprent).getInstance()).getIndex0() != 0)) break;
                            n = 2;
                            break;
                        }
                        case 12: {
                            if (n3 != 1 || ((VarExprent)exprent2).getIndex0() == 0) break;
                            n = 2;
                            break;
                        }
                        case 8: {
                            n = 4;
                            break;
                        }
                        case 2: {
                            AssignmentExprent assignmentExprent = (AssignmentExprent)exprent2;
                            if (assignmentExprent.getLeft().type != 5 || assignmentExprent.getRight().type != 12) break;
                            object2 = (FieldExprent)assignmentExprent.getLeft();
                            if ((n3 != 2 || ((FieldExprent)object2).isStatic()) && (n3 != 1 || !((FieldExprent)object2).isStatic()) || !((FieldExprent)object2).getClassname().equals(object.classStruct.qualifiedName) || !((FieldExprent)object2).isStatic() && (object2.getInstance().type != 12 || ((VarExprent)((FieldExprent)object2).getInstance()).getIndex0() != 0) || ((VarExprent)assignmentExprent.getRight()).getIndex0() != n3 - 1) break;
                            n = 3;
                        }
                    }
                    if (n == 4) {
                        n = 1;
                        exprent = (InvocationExprent)exprent2;
                        if (((InvocationExprent)exprent).isStatic() && ((InvocationExprent)exprent).getLstParameters().size() == n3 || !((InvocationExprent)exprent).isStatic() && exprent.getInstance().type == 12 && ((VarExprent)((InvocationExprent)exprent).getInstance()).getIndex0() == 0 && ((InvocationExprent)exprent).getLstParameters().size() == n3 - 1) {
                            boolean bl = true;
                            int n4 = 0;
                            while (n4 < ((InvocationExprent)exprent).getLstParameters().size()) {
                                object = (Exprent)((InvocationExprent)exprent).getLstParameters().get(n4);
                                if (((Exprent)object).type != 12 || ((VarExprent)object).getIndex0() != n4 + (((InvocationExprent)exprent).isStatic() ? 0 : 1)) {
                                    bl = false;
                                    break;
                                }
                                ++n4;
                            }
                            if (bl) {
                                n = 4;
                            }
                        }
                    }
                } else if (object2.first.exprents.size() == 2) {
                    Exprent exprent = (Exprent)object2.first.exprents.get(0);
                    object2 = (Exprent)object2.first.exprents.get(1);
                    if (exprent.type == 2 && ((Exprent)object2).type == 4) {
                        int n5 = MethodDescriptor.parseDescriptor((String)methodWrapper.methodStruct.getDescriptor()).params.length;
                        exprent = (AssignmentExprent)exprent;
                        if (exprent.getLeft().type == 5 && exprent.getRight().type == 12) {
                            FieldExprent fieldExprent = (FieldExprent)((AssignmentExprent)exprent).getLeft();
                            if ((n5 == 2 && !fieldExprent.isStatic() || n5 == 1 && fieldExprent.isStatic()) && fieldExprent.getClassname().equals(object.classStruct.qualifiedName) && (fieldExprent.isStatic() || fieldExprent.getInstance().type == 12 && ((VarExprent)fieldExprent.getInstance()).getIndex0() == 0) && ((VarExprent)((AssignmentExprent)exprent).getRight()).getIndex0() == n5 - 1 && ((ExitExprent)(object2 = (ExitExprent)object2)).getExittype() == 0 && ((ExitExprent)object2).getValue() != null && object2.getValue().type == 12 && ((VarExprent)((AssignmentExprent)exprent).getRight()).getIndex0() == n5 - 1) {
                                n = 3;
                            }
                        }
                    }
                }
            }
        }
        if (n != 1) {
            this.mapMethodType.put(methodWrapper, n);
            return;
        }
        this.mapMethodType.remove(methodWrapper);
    }

    private void eliminateStaticAccess(ClassesProcessor$ClassNode classesProcessor$ClassNode) {
        for (Object object : classesProcessor$ClassNode.wrapper.getMethods()) {
            if (((MethodWrapper)object).root == null) continue;
            boolean bl = false;
            Object object2 = ((MethodWrapper)object).getOrBuildGraph();
            HashSet<Object> hashSet = new HashSet<Object>();
            LinkedList<DirectNode> linkedList = new LinkedList<DirectNode>();
            linkedList.add(((DirectGraph)object2).first);
            while (!linkedList.isEmpty()) {
                object2 = (DirectNode)linkedList.removeFirst();
                if (hashSet.contains(object2)) continue;
                hashSet.add(object2);
                int n = 0;
                while (n < ((DirectNode)object2).exprents.size()) {
                    Exprent exprent = (Exprent)((DirectNode)object2).exprents.get(n);
                    bl |= this.replaceInvocations(classesProcessor$ClassNode, (MethodWrapper)object, exprent);
                    Exprent exprent2 = null;
                    if (exprent.type == 8) {
                        exprent2 = this.replaceAccessExprent(classesProcessor$ClassNode, (MethodWrapper)object, (InvocationExprent)exprent);
                    } else if (exprent.type == 5) {
                        exprent2 = this.a(classesProcessor$ClassNode, (MethodWrapper)object, (FieldExprent)exprent);
                    }
                    if (exprent2 != null) {
                        ((DirectNode)object2).exprents.set(n, exprent2);
                        bl = true;
                    }
                    ++n;
                }
                for (DirectNode directNode : ((DirectNode)object2).succs) {
                    linkedList.add(directNode);
                }
            }
            if (!bl) continue;
            this.computeMethodType(classesProcessor$ClassNode, (MethodWrapper)object);
        }
        for (Object object : classesProcessor$ClassNode.nested) {
            this.eliminateStaticAccess((ClassesProcessor$ClassNode)object);
        }
    }

    private boolean replaceInvocations(ClassesProcessor$ClassNode classesProcessor$ClassNode, MethodWrapper methodWrapper, Exprent exprent) {
        boolean bl;
        boolean bl2 = false;
        for (Exprent exprent2 : exprent.getAllExprents()) {
            bl2 |= this.replaceInvocations(classesProcessor$ClassNode, methodWrapper, exprent2);
        }
        block1: do {
            bl = false;
            for (Object object : exprent.getAllExprents()) {
                Exprent exprent3 = null;
                if (((Exprent)object).type == 8) {
                    exprent3 = this.replaceAccessExprent(classesProcessor$ClassNode, methodWrapper, (InvocationExprent)object);
                } else if (((Exprent)object).type == 5) {
                    exprent3 = this.a(classesProcessor$ClassNode, methodWrapper, (FieldExprent)object);
                }
                if (exprent3 == null) continue;
                exprent.replaceExprent((Exprent)object, exprent3);
                bl = true;
                bl2 = true;
                continue block1;
            }
        } while (bl);
        return bl2;
    }

    /*
     * Unable to fully structure code
     */
    private static boolean sameTree(ClassesProcessor$ClassNode var0, ClassesProcessor$ClassNode var1_1) {
        if (!var0.classStruct.qualifiedName.equals(var1_1.classStruct.qualifiedName)) ** GOTO lbl4
        return false;
lbl-1000:
        // 1 sources

        {
            var0 = var0.parent;
lbl4:
            // 2 sources

            ** while (var0.parent != null)
        }
lbl5:
        // 2 sources

        while (var1_1.parent != null) {
            var1_1 = var1_1.parent;
        }
        return var0 == var1_1;
    }

    private Exprent a(ClassesProcessor$ClassNode object, MethodWrapper methodWrapper, FieldExprent fieldExprent) {
        Object object2 = (ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get(fieldExprent.getClassname());
        if (object2 == null || ((ClassesProcessor$ClassNode)object2).mapFieldsToVars == null) {
            return null;
        }
        String string = InterpreterUtil.makeUniqueKey(fieldExprent.getName(), fieldExprent.getDescriptor());
        if (((ClassesProcessor$ClassNode)object2).mapFieldsToVars.containsKey(string) && (string = (String)((ClassesProcessor$ClassNode)object2).this$0.get(string)) != null && string.length() > 0) {
            if (!NestedMemberAccess.sameTree((ClassesProcessor$ClassNode)object, (ClassesProcessor$ClassNode)object2)) {
                return null;
            }
            if (!methodWrapper.setOuterVarNames.contains(string)) {
                object2 = new VarNamesCollector();
                ((VarNamesCollector)object2).addName(string);
                methodWrapper.varproc.refreshVarNames((VarNamesCollector)object2);
                methodWrapper.setOuterVarNames.add(string);
            }
            int n = methodWrapper.counter.getCounterAndIncrement(2);
            object = new VarExprent(n, fieldExprent.getExprType(), methodWrapper.varproc);
            methodWrapper.varproc.setVarName(new VarVersionPaar(n, 0), string);
            return object;
        }
        return null;
    }

    private Exprent replaceAccessExprent(ClassesProcessor$ClassNode object, MethodWrapper methodWrapper, InvocationExprent invocationExprent) {
        ClassesProcessor$ClassNode classesProcessor$ClassNode = (ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get(invocationExprent.getClassname());
        MethodWrapper methodWrapper2 = null;
        if (classesProcessor$ClassNode != null && classesProcessor$ClassNode.wrapper != null) {
            methodWrapper2 = classesProcessor$ClassNode.wrapper.getMethodWrapper(invocationExprent.getName(), invocationExprent.getStringDescriptor());
        }
        if (methodWrapper2 == null || !this.mapMethodType.containsKey(methodWrapper2)) {
            return null;
        }
        if (classesProcessor$ClassNode.classStruct.qualifiedName.equals(object.classStruct.qualifiedName) && methodWrapper2.methodStruct.getName().equals(methodWrapper.methodStruct.getName()) && methodWrapper2.methodStruct.getDescriptor().equals(methodWrapper.methodStruct.getDescriptor())) {
            return null;
        }
        int n = (Integer)this.mapMethodType.get(methodWrapper2);
        if (!NestedMemberAccess.sameTree((ClassesProcessor$ClassNode)object, classesProcessor$ClassNode)) {
            return null;
        }
        object = (Exprent)methodWrapper2.getOrBuildGraph().first.exprents.get(0);
        Object object2 = null;
        switch (n) {
            case 2: {
                Object object3 = (ExitExprent)object;
                if (object3.getValue().type == 12) {
                    object = (VarExprent)((ExitExprent)object3).getValue();
                    if (!methodWrapper.setOuterVarNames.contains(object3 = methodWrapper2.varproc.getVarName(new VarVersionPaar((VarExprent)object)))) {
                        object2 = new VarNamesCollector();
                        ((VarNamesCollector)object2).addName((String)object3);
                        methodWrapper.varproc.refreshVarNames((VarNamesCollector)object2);
                        methodWrapper.setOuterVarNames.add(object3);
                    }
                    int n2 = methodWrapper.counter.getCounterAndIncrement(2);
                    object = new VarExprent(n2, ((VarExprent)object).getVartype(), methodWrapper.varproc);
                    methodWrapper.varproc.setVarName(new VarVersionPaar(n2, 0), (String)object3);
                    object2 = object;
                    break;
                }
                object = (FieldExprent)((ExitExprent)object3).getValue().copy();
                if (!((FieldExprent)object).isStatic()) {
                    ((FieldExprent)object).replaceExprent(((FieldExprent)object).getInstance(), (Exprent)invocationExprent.getLstParameters().get(0));
                }
                object2 = object;
                break;
            }
            case 3: {
                if (((Exprent)object).type == 4) {
                    Object var0_3 = null;
                    object = (AssignmentExprent)((AssignmentExprent)((ExitExprent)object).getValue()).copy();
                } else {
                    object = (AssignmentExprent)((AssignmentExprent)object).copy();
                }
                FieldExprent fieldExprent = (FieldExprent)((AssignmentExprent)object).getLeft();
                if (fieldExprent.isStatic()) {
                    ((AssignmentExprent)object).replaceExprent(((AssignmentExprent)object).getRight(), (Exprent)invocationExprent.getLstParameters().get(0));
                } else {
                    ((AssignmentExprent)object).replaceExprent(((AssignmentExprent)object).getRight(), (Exprent)invocationExprent.getLstParameters().get(1));
                    fieldExprent.replaceExprent(fieldExprent.getInstance(), (Exprent)invocationExprent.getLstParameters().get(0));
                }
                object2 = object;
                break;
            }
            case 4: {
                if (((Exprent)object).type == 4) {
                    object = ((ExitExprent)object).getValue();
                }
                object2 = (InvocationExprent)((Exprent)object).copy();
                int n3 = 0;
                if (!((InvocationExprent)object2).isStatic()) {
                    ((InvocationExprent)object2).replaceExprent(((InvocationExprent)object2).getInstance(), (Exprent)invocationExprent.getLstParameters().get(0));
                    n3 = 1;
                }
                n = 0;
                while (n < ((InvocationExprent)object2).getLstParameters().size()) {
                    ((InvocationExprent)object2).replaceExprent((Exprent)((InvocationExprent)object2).getLstParameters().get(n), (Exprent)invocationExprent.getLstParameters().get(n + n3));
                    ++n;
                }
                break;
            }
        }
        if (object2 != null) {
            StructMethod structMethod;
            boolean bl = true;
            if (!(classesProcessor$ClassNode.type != 0 && (classesProcessor$ClassNode.access & 8) == 0 || ((structMethod = methodWrapper2.methodStruct).getAccessFlags() & 0x1000) != 0 || structMethod.getAttributes().containsKey("Synthetic"))) {
                bl = false;
            }
            if (bl) {
                classesProcessor$ClassNode.wrapper.getHideMembers().add(InterpreterUtil.makeUniqueKey(invocationExprent.getName(), invocationExprent.getStringDescriptor()));
            }
        }
        return object2;
    }
}

