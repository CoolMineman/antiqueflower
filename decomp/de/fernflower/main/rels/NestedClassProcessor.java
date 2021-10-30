/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.rels;

import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.main.collectors.VarNamesCollector;
import de.fernflower.main.rels.ClassWrapper;
import de.fernflower.main.rels.MethodWrapper;
import de.fernflower.main.rels.NestedClassProcessor$1;
import de.fernflower.main.rels.NestedClassProcessor$2;
import de.fernflower.main.rels.NestedClassProcessor$VarFieldPair;
import de.fernflower.modules.decompiler.exps.AssignmentExprent;
import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FieldExprent;
import de.fernflower.modules.decompiler.exps.InvocationExprent;
import de.fernflower.modules.decompiler.exps.VarExprent;
import de.fernflower.modules.decompiler.sforms.DirectGraph;
import de.fernflower.modules.decompiler.sforms.DirectNode;
import de.fernflower.modules.decompiler.stats.DoStatement;
import de.fernflower.modules.decompiler.stats.Statement;
import de.fernflower.modules.decompiler.vars.VarVersionPaar;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.StructField;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.attr.StructEnclosingMethodAttribute;
import de.fernflower.struct.gen.MethodDescriptor;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class NestedClassProcessor {
    /*
     * Could not resolve type clashes
     */
    public final void processClass(ClassesProcessor$ClassNode classesProcessor$ClassNode, ClassesProcessor$ClassNode classesProcessor$ClassNode2) {
        Object object3;
        if (classesProcessor$ClassNode2.nested.isEmpty()) {
            return;
        }
        Iterator iterator = classesProcessor$ClassNode2;
        ArrayList arrayList2 = this;
        Object object22 = new HashMap<String, Object>();
        int n = 0;
        for (Object object3 : ((ClassesProcessor$ClassNode)iterator).nested) {
            if ((((ClassesProcessor$ClassNode)object3).access & 8) != 0 || (((ClassesProcessor$ClassNode)object3).access & 0x200) != 0) continue;
            n |= ((ClassesProcessor$ClassNode)object3).type;
            HashMap hashMap = ((NestedClassProcessor)((Object)arrayList2)).getMaskLocalVars(((ClassesProcessor$ClassNode)object3).wrapper);
            if (hashMap.isEmpty()) {
                DecompilerContext.getLogger().writeMessage("Nested class " + object3.classStruct.qualifiedName + " has no constructor!", 3);
                continue;
            }
            ((HashMap)object22).put(object3.classStruct.qualifiedName, hashMap);
        }
        object3 = new HashMap();
        if (n != 1) {
            for (Object object4 : ((ClassesProcessor$ClassNode)iterator).wrapper.getMethods()) {
                if (((MethodWrapper)object4).root == null) continue;
                Object var6_7 = null;
                ((MethodWrapper)object4).getOrBuildGraph().iterateExprents(new NestedClassProcessor$1((HashMap)object22, (ClassesProcessor$ClassNode)((Object)iterator), (HashMap)object3, (MethodWrapper)object4));
            }
        }
        for (Object object4 : ((HashMap)object22).entrySet()) {
            ClassesProcessor$ClassNode classesProcessor$ClassNode3 = ((ClassesProcessor$ClassNode)((Object)iterator)).getClassNode((String)object4.getKey());
            arrayList2 = null;
            if (((HashMap)object3).containsKey(object4.getKey())) {
                for (Object object22 : ((HashMap)((HashMap)object3).get(object4.getKey())).values()) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList(object22);
                        continue;
                    }
                    NestedClassProcessor.mergeListSignatures(arrayList2, (List)object22, false);
                }
            }
            object22 = null;
            for (Object object5 : ((HashMap)object4.getValue()).values()) {
                if (object22 == null) {
                    object22 = new ArrayList(object5);
                    continue;
                }
                NestedClassProcessor.mergeListSignatures((List)object22, (List)object5, false);
            }
            if (arrayList2 == null) {
                arrayList2 = new ArrayList(object22);
                boolean bl = false;
                int n2 = 0;
                while (n2 < arrayList2.size()) {
                    if (arrayList2.get(n2) != null) {
                        if (bl) {
                            arrayList2.set(n2, null);
                        }
                        bl = true;
                    }
                    ++n2;
                }
            }
            NestedClassProcessor.mergeListSignatures(arrayList2, (List)object22, true);
            int n3 = 0;
            while (n3 < arrayList2.size()) {
                NestedClassProcessor$VarFieldPair nestedClassProcessor$VarFieldPair = (NestedClassProcessor$VarFieldPair)arrayList2.get(n3);
                if (nestedClassProcessor$VarFieldPair != null && nestedClassProcessor$VarFieldPair.keyfield.length() > 0) {
                    classesProcessor$ClassNode3.mapFieldsToVars.put(nestedClassProcessor$VarFieldPair.keyfield, nestedClassProcessor$VarFieldPair.varpaar);
                }
                ++n3;
            }
            for (Object object6 : ((HashMap)object4.getValue()).entrySet()) {
                NestedClassProcessor.mergeListSignatures((List)object6.getValue(), arrayList2, false);
                object22 = classesProcessor$ClassNode3.wrapper.getMethodWrapper("<init>", (String)object6.getKey());
                classesProcessor$ClassNode3.wrapper.getMethodWrapper("<init>", (String)object6.getKey()).signatureFields = new ArrayList();
                object6 = ((List)object6.getValue()).iterator();
                while (object6.hasNext()) {
                    object4 = (NestedClassProcessor$VarFieldPair)object6.next();
                    ((MethodWrapper)object22).signatureFields.add(object4 == null ? null : ((NestedClassProcessor$VarFieldPair)object4).varpaar);
                }
            }
        }
        this.checkNotFoundClasses(classesProcessor$ClassNode, classesProcessor$ClassNode2);
        for (ArrayList arrayList2 : classesProcessor$ClassNode2.nested) {
            if (((ClassesProcessor$ClassNode)arrayList2).type != 4 && ((ClassesProcessor$ClassNode)arrayList2).type != 1 || ((ClassesProcessor$ClassNode)arrayList2).simpleName != null) continue;
            DecompilerContext.getLogger().writeMessage("Nameless local or member class " + arrayList2.classStruct.qualifiedName + "!", 3);
            ((ClassesProcessor$ClassNode)arrayList2).simpleName = "NamelessClass" + ((Object)arrayList2).hashCode();
        }
        for (ArrayList arrayList2 : classesProcessor$ClassNode2.nested) {
            if (((ClassesProcessor$ClassNode)arrayList2).type == 1 && (((ClassesProcessor$ClassNode)arrayList2).access & 8) != 0) continue;
            this.insertLocalVars(classesProcessor$ClassNode2, (ClassesProcessor$ClassNode)((Object)arrayList2));
            if (((ClassesProcessor$ClassNode)arrayList2).type != 4) continue;
            this.setLocalClassDefinition((MethodWrapper)classesProcessor$ClassNode2.wrapper.getMethods().getWithKey(((ClassesProcessor$ClassNode)arrayList2).enclosingMethod), (ClassesProcessor$ClassNode)((Object)arrayList2));
        }
        for (ArrayList arrayList2 : classesProcessor$ClassNode2.nested) {
            this.processClass(classesProcessor$ClassNode, (ClassesProcessor$ClassNode)((Object)arrayList2));
        }
    }

    private void checkNotFoundClasses(ClassesProcessor$ClassNode classesProcessor$ClassNode, ClassesProcessor$ClassNode classesProcessor$ClassNode2) {
        for (Object object : new ArrayList(classesProcessor$ClassNode2.nested)) {
            StructEnclosingMethodAttribute structEnclosingMethodAttribute;
            if (((ClassesProcessor$ClassNode)object).type != 4 && ((ClassesProcessor$ClassNode)object).type != 2 || ((ClassesProcessor$ClassNode)object).enclosingMethod != null) continue;
            Set set = ((ClassesProcessor$ClassNode)object).enclosingClasses;
            if (set.size() == 1 && (structEnclosingMethodAttribute = (StructEnclosingMethodAttribute)((ClassesProcessor$ClassNode)object).classStruct.getAttributes().getWithKey("EnclosingMethod")) != null && structEnclosingMethodAttribute.getMethodName() != null && classesProcessor$ClassNode2.classStruct.qualifiedName.equals(structEnclosingMethodAttribute.getClassname()) && classesProcessor$ClassNode2.classStruct.getMethod(structEnclosingMethodAttribute.getMethodName(), structEnclosingMethodAttribute.getMethodDescriptor()) != null) {
                ((ClassesProcessor$ClassNode)object).enclosingMethod = InterpreterUtil.makeUniqueKey(structEnclosingMethodAttribute.getMethodName(), structEnclosingMethodAttribute.getMethodDescriptor());
                continue;
            }
            classesProcessor$ClassNode2.nested.remove(object);
            ((ClassesProcessor$ClassNode)object).parent = null;
            set.remove(classesProcessor$ClassNode2.classStruct.qualifiedName);
            boolean bl = !set.isEmpty();
            if (bl) {
                bl = NestedClassProcessor.insertNestedClass(classesProcessor$ClassNode, (ClassesProcessor$ClassNode)object);
            }
            if (bl) continue;
            if (((ClassesProcessor$ClassNode)object).type == 2) {
                DecompilerContext.getLogger().writeMessage("Unreferenced anonymous class " + object.classStruct.qualifiedName + "!", 3);
                continue;
            }
            if (((ClassesProcessor$ClassNode)object).type != 4) continue;
            DecompilerContext.getLogger().writeMessage("Unreferenced local class " + object.classStruct.qualifiedName + "!", 3);
        }
    }

    private static boolean insertNestedClass(ClassesProcessor$ClassNode classesProcessor$ClassNode, ClassesProcessor$ClassNode classesProcessor$ClassNode2) {
        Set set = classesProcessor$ClassNode2.enclosingClasses;
        LinkedList<ClassesProcessor$ClassNode> linkedList = new LinkedList<ClassesProcessor$ClassNode>();
        linkedList.add(classesProcessor$ClassNode);
        while (!linkedList.isEmpty()) {
            classesProcessor$ClassNode = (ClassesProcessor$ClassNode)linkedList.removeFirst();
            if (set.contains(classesProcessor$ClassNode.classStruct.qualifiedName)) {
                classesProcessor$ClassNode.nested.add(classesProcessor$ClassNode2);
                classesProcessor$ClassNode2.parent = classesProcessor$ClassNode;
                return true;
            }
            linkedList.addAll(classesProcessor$ClassNode.nested);
        }
        return false;
    }

    /*
     * WARNING - void declaration
     */
    private void insertLocalVars(ClassesProcessor$ClassNode classesProcessor$ClassNode, ClassesProcessor$ClassNode classesProcessor$ClassNode2) {
        MethodWrapper methodWrapper = (MethodWrapper)classesProcessor$ClassNode.wrapper.getMethods().getWithKey(classesProcessor$ClassNode2.enclosingMethod);
        for (MethodWrapper methodWrapper2 : classesProcessor$ClassNode2.wrapper.getMethods()) {
            Object object;
            Object object2;
            Object object3;
            Object object42;
            Object object5;
            if (methodWrapper2.root == null) continue;
            HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
            HashMap<Object, Object> hashMap2 = new HashMap<Object, Object>();
            HashMap<Integer, VarVersionPaar> hashMap3 = new HashMap<Integer, VarVersionPaar>();
            if (methodWrapper2.signatureFields != null) {
                int n = 0;
                boolean n2 = true;
                object5 = MethodDescriptor.parseDescriptor(methodWrapper2.methodStruct.getDescriptor());
                for (Object object42 : methodWrapper2.signatureFields) {
                    if (object42 != null) {
                        void entry;
                        object3 = new VarVersionPaar(methodWrapper2.counter.getCounterAndIncrement(2), 0);
                        hashMap3.put((int)entry, (VarVersionPaar)object3);
                        object2 = null;
                        VarType varType = null;
                        if (classesProcessor$ClassNode2.type != 1) {
                            object2 = methodWrapper.varproc.getVarName((VarVersionPaar)object42);
                            varType = methodWrapper.varproc.getVarType((VarVersionPaar)object42);
                            methodWrapper.varproc.setVarFinal((VarVersionPaar)object42);
                        }
                        if (((VarVersionPaar)object42).var == -1 || "this".equals(object2)) {
                            object2 = classesProcessor$ClassNode.simpleName == null ? "<VAR_NAMELESS_ENCLOSURE>" : String.valueOf(classesProcessor$ClassNode.simpleName) + ".this";
                            methodWrapper2.varproc.getThisvars().put(object3, classesProcessor$ClassNode.classStruct.qualifiedName);
                        }
                        hashMap.put(object3, object2);
                        hashMap2.put(object3, varType);
                    }
                    entry += object5.params[n++].stack_size;
                }
            }
            HashMap<String, VarVersionPaar> hashMap4 = new HashMap<String, VarVersionPaar>();
            for (Map.Entry entry : classesProcessor$ClassNode2.mapFieldsToVars.entrySet()) {
                object42 = new VarVersionPaar(methodWrapper2.counter.getCounterAndIncrement(2), 0);
                hashMap4.put((String)entry.getKey(), (VarVersionPaar)object42);
                object = (StructField)classesProcessor$ClassNode2.classStruct.getFields().getWithKey((String)entry.getKey());
                classesProcessor$ClassNode2.wrapper.getHideMembers().add(InterpreterUtil.makeUniqueKey(((StructField)object).getName(), ((StructField)object).getDescriptor()));
                object3 = null;
                object2 = null;
                if (classesProcessor$ClassNode2.type != 1) {
                    object3 = methodWrapper.varproc.getVarName((VarVersionPaar)entry.getValue());
                    object2 = methodWrapper.varproc.getVarType((VarVersionPaar)entry.getValue());
                    methodWrapper.varproc.setVarFinal((VarVersionPaar)entry.getValue());
                }
                if (((VarVersionPaar)entry.getValue()).var == -1 || "this".equals(object3)) {
                    object3 = classesProcessor$ClassNode.simpleName == null ? "<VAR_NAMELESS_ENCLOSURE>" : String.valueOf(classesProcessor$ClassNode.simpleName) + ".this";
                    methodWrapper2.varproc.getThisvars().put(object42, classesProcessor$ClassNode.classStruct.qualifiedName);
                }
                hashMap.put(object42, object3);
                hashMap2.put(object42, object2);
                classesProcessor$ClassNode2.this$0.put((String)entry.getKey(), object3);
            }
            HashSet hashSet = new HashSet(hashMap.values());
            hashSet.removeAll(methodWrapper2.setOuterVarNames);
            methodWrapper2.varproc.refreshVarNames(new VarNamesCollector(hashSet));
            methodWrapper2.setOuterVarNames.addAll(hashSet);
            object42 = hashMap.entrySet().iterator();
            while (object42.hasNext()) {
                object5 = (Map.Entry)object42.next();
                object = (VarVersionPaar)object5.getKey();
                object3 = (VarType)hashMap2.get(object);
                methodWrapper2.varproc.setVarName((VarVersionPaar)object, (String)object5.getValue());
                if (object3 == null) continue;
                methodWrapper2.varproc.setVarType((VarVersionPaar)object, (VarType)object3);
            }
            methodWrapper2.getOrBuildGraph().iterateExprents(new NestedClassProcessor$2(classesProcessor$ClassNode2, hashMap4, methodWrapper2, hashMap3));
        }
    }

    private HashMap getMaskLocalVars(ClassWrapper classWrapper) {
        HashMap hashMap = new HashMap();
        StructClass structClass = classWrapper.getClassStruct();
        for (StructMethod structMethod : structClass.getMethods()) {
            if (!"<init>".equals(structMethod.getName())) continue;
            MethodDescriptor methodDescriptor = MethodDescriptor.parseDescriptor(structMethod.getDescriptor());
            MethodWrapper methodWrapper = classWrapper.getMethodWrapper("<init>", structMethod.getDescriptor());
            DirectGraph directGraph = methodWrapper.getOrBuildGraph();
            if (directGraph == null) continue;
            ArrayList<NestedClassProcessor$VarFieldPair> arrayList = new ArrayList<NestedClassProcessor$VarFieldPair>();
            int n = 1;
            int n2 = 0;
            while (n2 < methodDescriptor.params.length) {
                String string = NestedClassProcessor.getEnclosingVarField(structClass, methodWrapper, directGraph, n);
                arrayList.add(string == null ? null : new NestedClassProcessor$VarFieldPair(string, new VarVersionPaar(-1, 0)));
                n += methodDescriptor.params[n2].stack_size;
                ++n2;
            }
            hashMap.put(structMethod.getDescriptor(), arrayList);
        }
        return hashMap;
    }

    private static String getEnclosingVarField(StructClass structClass, MethodWrapper methodWrapper, DirectGraph object2, int n) {
        String string = "";
        if (methodWrapper.varproc.getVarFinal(new VarVersionPaar(n, 0)) == 1) {
            return null;
        }
        boolean bl = DecompilerContext.getOption("nns");
        object2 = ((DirectGraph)object2).first;
        if (((DirectNode)object2).preds.isEmpty()) {
            for (Object object2 : ((DirectNode)object2).exprents) {
                StructField structField;
                if (((Exprent)object2).type != 2) continue;
                object2 = (AssignmentExprent)object2;
                if (object2.getRight().type != 12 || ((VarExprent)((AssignmentExprent)object2).getRight()).getIndex0() != n || object2.getLeft().type != 5 || (structField = structClass.getField(((FieldExprent)(object2 = (FieldExprent)((AssignmentExprent)object2).getLeft())).getName(), ((FieldExprent)object2).getDescriptor())) == null || !structClass.qualifiedName.equals(((FieldExprent)object2).getClassname()) || (structField.access_flags & 0x10) == 0 || (structField.access_flags & 0x1000) == 0 && !structField.getAttributes().containsKey("Synthetic") && (!bl || (structField.access_flags & 2) == 0)) continue;
                string = InterpreterUtil.makeUniqueKey(((FieldExprent)object2).getName(), ((FieldExprent)object2).getDescriptor());
                break;
            }
        }
        return string;
    }

    private static void mergeListSignatures(List list, List list2, boolean bl) {
        boolean bl2;
        NestedClassProcessor$VarFieldPair nestedClassProcessor$VarFieldPair;
        int n;
        for (n = 1; list.size() > n && list2.size() > n; ++n) {
            NestedClassProcessor$VarFieldPair nestedClassProcessor$VarFieldPair2 = (NestedClassProcessor$VarFieldPair)list.get(list.size() - n);
            nestedClassProcessor$VarFieldPair = (NestedClassProcessor$VarFieldPair)list2.get(list2.size() - n);
            bl2 = false;
            if (nestedClassProcessor$VarFieldPair2 == null || nestedClassProcessor$VarFieldPair == null) {
                bl2 = nestedClassProcessor$VarFieldPair2 == nestedClassProcessor$VarFieldPair;
            } else {
                bl2 = true;
                if (nestedClassProcessor$VarFieldPair2.keyfield.length() == 0) {
                    nestedClassProcessor$VarFieldPair2.keyfield = nestedClassProcessor$VarFieldPair.keyfield;
                } else if (nestedClassProcessor$VarFieldPair.keyfield.length() == 0) {
                    if (bl) {
                        nestedClassProcessor$VarFieldPair.keyfield = nestedClassProcessor$VarFieldPair2.keyfield;
                    }
                } else {
                    bl2 = nestedClassProcessor$VarFieldPair2.keyfield.equals(nestedClassProcessor$VarFieldPair.keyfield);
                }
            }
            if (!bl2) {
                list.set(list.size() - n, null);
                if (!bl) continue;
                list2.set(list2.size() - n, null);
                continue;
            }
            if (nestedClassProcessor$VarFieldPair2 == null) continue;
            if (nestedClassProcessor$VarFieldPair2.varpaar.var == -1) {
                nestedClassProcessor$VarFieldPair2.varpaar = nestedClassProcessor$VarFieldPair.varpaar;
                continue;
            }
            nestedClassProcessor$VarFieldPair.varpaar = nestedClassProcessor$VarFieldPair2.varpaar;
        }
        int n2 = 1;
        while (n2 <= list.size() - n) {
            list.set(n2, null);
            ++n2;
        }
        if (bl) {
            n2 = 1;
            while (n2 <= list2.size() - n) {
                list2.set(n2, null);
                ++n2;
            }
        }
        if (list.isEmpty()) {
            if (!list2.isEmpty() && bl) {
                list2.set(0, null);
                return;
            }
        } else {
            if (list2.isEmpty()) {
                list.set(0, null);
                return;
            }
            NestedClassProcessor$VarFieldPair nestedClassProcessor$VarFieldPair3 = (NestedClassProcessor$VarFieldPair)list.get(0);
            nestedClassProcessor$VarFieldPair = (NestedClassProcessor$VarFieldPair)list2.get(0);
            bl2 = false;
            if (nestedClassProcessor$VarFieldPair3 == null || nestedClassProcessor$VarFieldPair == null) {
                bl2 = nestedClassProcessor$VarFieldPair3 == nestedClassProcessor$VarFieldPair;
            } else {
                bl2 = true;
                if (nestedClassProcessor$VarFieldPair3.keyfield.length() == 0) {
                    nestedClassProcessor$VarFieldPair3.keyfield = nestedClassProcessor$VarFieldPair.keyfield;
                } else if (nestedClassProcessor$VarFieldPair.keyfield.length() == 0) {
                    if (bl) {
                        nestedClassProcessor$VarFieldPair.keyfield = nestedClassProcessor$VarFieldPair3.keyfield;
                    }
                } else {
                    bl2 = nestedClassProcessor$VarFieldPair3.keyfield.equals(nestedClassProcessor$VarFieldPair.keyfield);
                }
            }
            if (!bl2) {
                list.set(0, null);
                if (bl) {
                    list2.set(0, null);
                    return;
                }
            } else {
                if (nestedClassProcessor$VarFieldPair3.varpaar.var == -1) {
                    nestedClassProcessor$VarFieldPair3.varpaar = nestedClassProcessor$VarFieldPair.varpaar;
                    return;
                }
                nestedClassProcessor$VarFieldPair.varpaar = nestedClassProcessor$VarFieldPair3.varpaar;
            }
        }
    }

    private void setLocalClassDefinition(MethodWrapper methodWrapper, ClassesProcessor$ClassNode object) {
        Object object2;
        Statement statement = methodWrapper.root;
        object = new VarType(object.classStruct.qualifiedName, true);
        HashSet hashSet = new HashSet();
        if ((object2 = ((NestedClassProcessor)object2).getDefStatement(statement, (VarType)object, hashSet)) == null) {
            object2 = statement.getFirst();
        }
        object2 = (statement = NestedClassProcessor.findFirstBlock((Statement)object2, hashSet)) == null ? ((Statement)object2).getVarDefinitions() : (statement.getExprents() == null ? statement.getVarDefinitions() : statement.getExprents());
        int n = 0;
        hashSet = object2.iterator();
        while (hashSet.hasNext()) {
            if (NestedClassProcessor.searchForClass((Exprent)hashSet.next(), (VarType)object)) break;
            ++n;
        }
        hashSet = new VarExprent(methodWrapper.counter.getCounterAndIncrement(2), (VarType)object, methodWrapper.varproc);
        ((VarExprent)((Object)hashSet)).setDefinition();
        ((VarExprent)((Object)hashSet)).setClassdef();
        object2.add(n, hashSet);
    }

    private static Statement findFirstBlock(Statement statement, HashSet hashSet) {
        LinkedList<Statement> linkedList = new LinkedList<Statement>();
        linkedList.add(statement);
        while (!linkedList.isEmpty()) {
            statement = (Statement)linkedList.remove(0);
            if (!linkedList.isEmpty() && !hashSet.contains(statement)) continue;
            if (statement.isLabeled() && !linkedList.isEmpty()) {
                return statement;
            }
            if (statement.getExprents() != null) {
                return statement;
            }
            linkedList.clear();
            switch (statement.type) {
                case 15: {
                    linkedList.addAll(0, statement.getStats());
                    break;
                }
                case 2: 
                case 6: 
                case 10: 
                case 13: {
                    linkedList.add(statement.getFirst());
                    break;
                }
                default: {
                    return statement;
                }
            }
        }
        return null;
    }

    private Statement getDefStatement(Statement statement, VarType varType, HashSet hashSet) {
        List<Exprent> list = new ArrayList();
        Statement statement2 = null;
        if (statement.getExprents() == null) {
            int n = 0;
            for (Object object : statement.getSequentialObjects()) {
                if (object instanceof Statement) {
                    Statement statement3 = this.getDefStatement((Statement)(object = (Statement)object), varType, hashSet);
                    if (statement3 != null) {
                        if (n == 1) {
                            statement2 = statement;
                            break;
                        }
                        statement2 = statement3;
                        ++n;
                    }
                    if (((Statement)object).type != 5) continue;
                    object = (DoStatement)object;
                    list.addAll(((DoStatement)object).getInitExprentList());
                    list.addAll(((DoStatement)object).getConditionExprentList());
                    continue;
                }
                if (!(object instanceof Exprent)) continue;
                list.add((Exprent)object);
            }
        } else {
            list = statement.getExprents();
        }
        if (statement2 != statement) {
            for (Exprent exprent : list) {
                if (exprent == null || !NestedClassProcessor.searchForClass(exprent, varType)) continue;
                statement2 = statement;
                break;
            }
        }
        if (statement2 != null) {
            hashSet.add(statement);
        }
        return statement2;
    }

    private static boolean searchForClass(Exprent object, VarType varType) {
        Object object2 = ((Exprent)object).getAllExprents(true);
        object2.add(object);
        object = varType.value;
        Iterator iterator = object2.iterator();
        while (iterator.hasNext()) {
            object2 = (Exprent)iterator.next();
            boolean bl = false;
            switch (((Exprent)object2).type) {
                case 3: {
                    object2 = (ConstExprent)object2;
                    bl = VarType.VARTYPE_CLASS.equals(((ConstExprent)object2).getConsttype()) && ((String)object).equals(((ConstExprent)object2).getValue()) || varType.equals(((ConstExprent)object2).getConsttype());
                    break;
                }
                case 5: {
                    bl = ((String)object).equals(((FieldExprent)object2).getClassname());
                    break;
                }
                case 8: {
                    bl = ((String)object).equals(((InvocationExprent)object2).getClassname());
                    break;
                }
                case 10: {
                    object2 = ((Exprent)object2).getExprType();
                    bl = ((VarType)object2).type == 8 && ((String)object).equals(((VarType)object2).value);
                    break;
                }
                case 12: {
                    object2 = (VarExprent)object2;
                    if (!((VarExprent)object2).isDefinition() || !varType.equals(object2 = ((VarExprent)object2).getVartype()) && (((VarType)object2).arraydim <= 0 || !varType.value.equals(((VarType)object2).value))) break;
                    bl = true;
                }
            }
            if (!bl) continue;
            return true;
        }
        return false;
    }
}

