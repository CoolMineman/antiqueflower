/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main;

import de.fernflower.main.ClassWriter;
import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.main.collectors.CounterContainer;
import de.fernflower.main.collectors.ImportCollector;
import de.fernflower.main.rels.ClassWrapper;
import de.fernflower.main.rels.NestedClassProcessor;
import de.fernflower.main.rels.NestedMemberAccess;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.StructContext;
import de.fernflower.struct.attr.StructInnerClassesAttribute;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import net.minecraftforge.lex.fffixer.Util;

public final class ClassesProcessor {
    private HashMap mapRootClasses = new HashMap();

    public ClassesProcessor(StructContext structContext) {
        Object[] objectArray;
        Object object;
        Object object2;
        Object object3;
        String string;
        Object object4;
        Object object5;
        Object object6;
        HashSet<String> hashSet;
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        HashMap hashMap2 = new HashMap();
        HashMap hashMap3 = new HashMap();
        HashMap<String, Object> hashMap4 = new HashMap<String, Object>();
        boolean bl = DecompilerContext.getOption("din");
        for (StructClass object7 : structContext.getClasses().values()) {
            if (!object7.isOwn() || this.mapRootClasses.containsKey(object7.qualifiedName)) continue;
            if (bl && (hashSet = (StructInnerClassesAttribute)object7.getAttributes().getWithKey("InnerClasses")) != null) {
                int linkedList = 0;
                while (linkedList < ((StructInnerClassesAttribute)((Object)hashSet)).getClassentries().size()) {
                    object6 = (int[])((StructInnerClassesAttribute)((Object)hashSet)).getClassentries().get(linkedList);
                    object5 = (String[])((StructInnerClassesAttribute)((Object)hashSet)).getStringentries().get(linkedList);
                    object4 = new Object[4];
                    string = object5[0];
                    object4[2] = object6[1] == 0 ? (object6[2] == 0 ? 2 : 4) : 1;
                    object3 = object5[2];
                    object2 = (String)hashMap4.get(string);
                    if (object2 != null) {
                        object3 = object2;
                    } else if (object3 != null && DecompilerContext.getOption("ren") && (object = DecompilerContext.getPoolInterceptor().getHelper()).toBeRenamed(1, (String)object3, null, null)) {
                        object3 = object.getNextClassname(string, (String)object3);
                        hashMap4.put(string, object3);
                    }
                    object4[1] = object3;
                    object4[3] = object6[3];
                    object = null;
                    object = object6[1] != 0 ? object5[1] : object7.qualifiedName;
                    if (!string.equals(object)) {
                        StructClass structClass = (StructClass)structContext.getClasses().get(object);
                        object6 = structClass;
                        if (structClass != null && ((StructClass)object6).isOwn()) {
                            objectArray = (Object[])hashMap.get(string);
                            if (objectArray == null) {
                                hashMap.put(string, object4);
                            } else if (!InterpreterUtil.equalObjectArrays(objectArray, (Object[])object4)) {
                                DecompilerContext.getLogger().writeMessage("Inconsistent inner class entries for " + string + "!", 3);
                            }
                            object6 = (HashSet)hashMap2.get(object);
                            if (object6 == null) {
                                object6 = new HashSet();
                                hashMap2.put((Object[])object, object6);
                            }
                            ((HashSet)object6).add(string);
                            object6 = (HashSet)hashMap3.get(string);
                            if (object6 == null) {
                                object6 = new HashSet();
                                hashMap3.put(string, object6);
                            }
                            ((HashSet)object6).add(object);
                        }
                    }
                    ++linkedList;
                }
            }
            hashSet = new ClassesProcessor$ClassNode(object7);
            new ClassesProcessor$ClassNode(object7).access = object7.access_flags;
            this.mapRootClasses.put(object7.qualifiedName, hashSet);
        }
        if (bl) {
            for (Map.Entry entry : this.mapRootClasses.entrySet()) {
                if (hashMap.containsKey(entry.getKey())) continue;
                hashSet = new HashSet<String>();
                LinkedList<String> linkedList = new LinkedList<String>();
                linkedList.add((String)entry.getKey());
                hashSet.add((String)entry.getKey());
                while (!linkedList.isEmpty()) {
                    object6 = (String)linkedList.removeFirst();
                    object5 = (ClassesProcessor$ClassNode)this.mapRootClasses.get(object6);
                    object4 = (HashSet)hashMap2.get(object6);
                    if (object4 == null) continue;
                    object3 = ((HashSet)object4).iterator();
                    object3 = Util.sortComparable(object3);
                    while (object3.hasNext()) {
                        string = (String)object3.next();
                        if (hashSet.contains(string)) continue;
                        hashSet.add(string);
                        object2 = (ClassesProcessor$ClassNode)this.mapRootClasses.get(string);
                        if (object2 == null) {
                            DecompilerContext.getLogger().writeMessage("Nested class " + string + " missing!", 3);
                            continue;
                        }
                        object = (Object[])hashMap.get(string);
                        ((ClassesProcessor$ClassNode)object2).type = (Integer)object[2];
                        ((ClassesProcessor$ClassNode)object2).simpleName = (String)object[1];
                        ((ClassesProcessor$ClassNode)object2).access = (Integer)object[3];
                        if (((ClassesProcessor$ClassNode)object2).type == 2) {
                            object6 = ((ClassesProcessor$ClassNode)object2).classStruct;
                            ((ClassesProcessor$ClassNode)object2).access &= 0xFFFFFFF7;
                            int[] nArray = ((StructClass)object6).getInterfaces();
                            objectArray = nArray;
                            if (nArray.length > 0) {
                                if (objectArray.length > 1) {
                                    throw new RuntimeException("Inconsistent anonymous class definition: " + ((StructClass)object6).qualifiedName);
                                }
                                ((ClassesProcessor$ClassNode)object2).anonimousClassType = new VarType(((StructClass)object6).getInterface(0), true);
                            } else {
                                ((ClassesProcessor$ClassNode)object2).anonimousClassType = new VarType((String)object6.superClass.value, true);
                            }
                        } else if (((ClassesProcessor$ClassNode)object2).type == 4) {
                            ((ClassesProcessor$ClassNode)object2).access &= 0x410;
                        }
                        object5.nested.add(object2);
                        ((ClassesProcessor$ClassNode)object2).parent = object5;
                        ((ClassesProcessor$ClassNode)object2).enclosingClasses.addAll((Collection)hashMap3.get(string));
                        linkedList.add(string);
                    }
                }
            }
        }
    }

    public final void writeClass(StructClass object, BufferedWriter bufferedWriter) {
        ClassesProcessor$ClassNode classesProcessor$ClassNode = (ClassesProcessor$ClassNode)this.mapRootClasses.get(((StructClass)object).qualifiedName);
        if (classesProcessor$ClassNode.type != 0) {
            return;
        }
        try {
            int n;
            DecompilerContext.setImpcollector(new ImportCollector(classesProcessor$ClassNode));
            DecompilerContext.setCountercontainer(new CounterContainer());
            this.addClassnameToImport(classesProcessor$ClassNode, DecompilerContext.getImpcollector());
            this.initWrappers(classesProcessor$ClassNode);
            ClassWriter classWriter = null;
            new NestedClassProcessor().processClass(classesProcessor$ClassNode, classesProcessor$ClassNode);
            new NestedMemberAccess().propagateMemberAccess(classesProcessor$ClassNode);
            classWriter = new ClassWriter();
            StringWriter stringWriter = new StringWriter();
            classWriter.classToJava(classesProcessor$ClassNode, new BufferedWriter(stringWriter), 0);
            if (DecompilerContext.getOption("occ")) {
                bufferedWriter.write("// Decompiled by:       Fernflower v0.8.6");
                bufferedWriter.newLine();
                bufferedWriter.write("// Date:                " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()));
                bufferedWriter.newLine();
                bufferedWriter.write("// Copyright:           2008-2010, Stiver");
                bufferedWriter.newLine();
                bufferedWriter.write("// Home page:           http://www.reversed-java.com");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
            }
            if ((n = ((StructClass)object).qualifiedName.lastIndexOf("/")) >= 0) {
                object = ((StructClass)object).qualifiedName.substring(0, n).replaceAll("/", ".");
                bufferedWriter.write("package " + (String)object + ";");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
            }
            DecompilerContext.setProperty("CURRENT_CLASSNODE", classesProcessor$ClassNode);
            DecompilerContext.getImpcollector().writeImports(bufferedWriter);
            bufferedWriter.newLine();
            bufferedWriter.write(stringWriter.toString());
            bufferedWriter.flush();
        }
        finally {
            this.destroyWrappers(classesProcessor$ClassNode);
        }
    }

    private void initWrappers(ClassesProcessor$ClassNode classesProcessor$ClassNode2) {
        ClassWrapper classWrapper = new ClassWrapper(classesProcessor$ClassNode2.classStruct);
        classWrapper.init();
        classesProcessor$ClassNode2.wrapper = classWrapper;
        for (ClassesProcessor$ClassNode classesProcessor$ClassNode2 : classesProcessor$ClassNode2.nested) {
            this.initWrappers(classesProcessor$ClassNode2);
        }
    }

    private void addClassnameToImport(ClassesProcessor$ClassNode classesProcessor$ClassNode2, ImportCollector importCollector) {
        if (classesProcessor$ClassNode2.simpleName != null && classesProcessor$ClassNode2.simpleName.length() > 0) {
            importCollector.getShortName(classesProcessor$ClassNode2.type == 0 ? classesProcessor$ClassNode2.classStruct.qualifiedName : classesProcessor$ClassNode2.simpleName, false);
        }
        for (ClassesProcessor$ClassNode classesProcessor$ClassNode2 : classesProcessor$ClassNode2.nested) {
            this.addClassnameToImport(classesProcessor$ClassNode2, importCollector);
        }
    }

    private void destroyWrappers(ClassesProcessor$ClassNode classesProcessor$ClassNode2) {
        classesProcessor$ClassNode2.wrapper = null;
        classesProcessor$ClassNode2.classStruct.releaseResources();
        for (ClassesProcessor$ClassNode classesProcessor$ClassNode2 : classesProcessor$ClassNode2.nested) {
            this.destroyWrappers(classesProcessor$ClassNode2);
        }
    }

    public final HashMap getMapRootClasses() {
        return this.mapRootClasses;
    }
}

