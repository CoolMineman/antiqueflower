/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.renamer;

import de.fernflower.main.DecompilerContext;
import de.fernflower.main.extern.IFernflowerLogger;
import de.fernflower.main.extern.IIdentifierRenamer;
import de.fernflower.modules.renamer.ClassWrapperNode;
import de.fernflower.modules.renamer.ConverterHelper;
import de.fernflower.modules.renamer.PoolInterceptor;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.StructContext;
import de.fernflower.struct.StructField;
import de.fernflower.struct.StructMethod;
import de.fernflower.struct.gen.FieldDescriptor;
import de.fernflower.struct.gen.MethodDescriptor;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.VBStyleCollection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IdentifierConverter {
    private StructContext context;
    private IIdentifierRenamer helper;
    private PoolInterceptor interceptor;
    private List rootClasses = new ArrayList();
    private List rootInterfaces = new ArrayList();
    private Map interfaceNameMaps = new HashMap();
    private Map g = new HashMap();
    private List h;
    private List i;

    public final void rename(StructContext structContext) {
        try {
            block6: {
                this.context = structContext;
                String string = (String)DecompilerContext.getProperty("urc");
                if (string != null) {
                    IFernflowerLogger iFernflowerLogger = DecompilerContext.getLogger();
                    iFernflowerLogger.writeMessage("User supplied renamer class " + string + " found. Loading...", 2, 0);
                    try {
                        this.helper = (IIdentifierRenamer)IdentifierConverter.class.getClassLoader().loadClass(string).newInstance();
                        iFernflowerLogger.writeMessage("Class " + string + " successfully loaded.", 2, 0);
                    }
                    catch (Exception exception) {
                        iFernflowerLogger.writeMessage("Class " + string + " couldn't be loaded.", 3, 0);
                        if (!iFernflowerLogger.getShowStacktrace()) break block6;
                        exception.printStackTrace();
                    }
                }
            }
            if (this.helper == null) {
                this.helper = new ConverterHelper();
            }
            this.interceptor = new PoolInterceptor(this.helper);
            this.buildInheritanceTree();
            this.h = IdentifierConverter.getReversePostOrderListIterative(this.rootClasses);
            this.i = IdentifierConverter.getReversePostOrderListIterative(this.rootInterfaces);
            this.d();
            this.renameClasses();
            this.renameInterfaces();
            this.b();
            DecompilerContext.setPoolInterceptor(this.interceptor);
            structContext.reloadContext();
            return;
        }
        catch (IOException iOException) {
            throw new RuntimeException("Renaming failed!");
        }
    }

    private void renameClasses() {
        Object object = new ArrayList(this.i);
        object.addAll(this.h);
        HashMap hashMap = new HashMap();
        Iterator iterator = object.iterator();
        while (iterator.hasNext()) {
            Set set;
            object = null;
            object = ((ClassWrapperNode)iterator.next()).getClassStruct();
            HashSet hashSet = new HashSet();
            if (((StructClass)object).superClass != null) {
                set = null;
                set = (Set)hashMap.get((String)object.superClass.value);
                if (set != null) {
                    hashSet.addAll(set);
                }
            }
            String[] stringArray = ((StructClass)object).getInterfaceNames();
            int n = stringArray.length;
            int n2 = 0;
            while (n2 < n) {
                set = stringArray[n2];
                hashSet.addAll(this.processExternalInterface((String)((Object)set), hashMap));
                ++n2;
            }
            if (hashSet.size() <= 1) continue;
            set = new HashSet(hashSet);
            set.retainAll(this.g.keySet());
            if (!set.isEmpty()) {
                hashSet.addAll((Collection)this.g.get(set.iterator().next()));
            }
            for (String string : hashSet) {
                this.g.put(string, hashSet);
            }
        }
    }

    private Set processExternalInterface(String string, HashMap hashMap) {
        HashSet<String> hashSet = (HashSet<String>)hashMap.get(string);
        if (hashSet == null) {
            hashSet = new HashSet<String>();
            StructClass structClass = this.context.getClass(string);
            if (structClass != null) {
                String[] stringArray = structClass.getInterfaceNames();
                int n = stringArray.length;
                int n2 = 0;
                while (n2 < n) {
                    String string2 = stringArray[n2];
                    hashSet.addAll(this.processExternalInterface(string2, hashMap));
                    ++n2;
                }
                if (structClass.isOwn()) {
                    hashSet.add(string);
                }
            }
            hashMap.put(string, hashSet);
        }
        return hashSet;
    }

    private Set renameClass(ClassWrapperNode object3, Map map, Set object2) {
        StructClass structClass = ((ClassWrapperNode)object3).getClassStruct();
        HashSet hashSet = new HashSet(Arrays.asList(structClass.getInterfaceNames()));
        HashSet hashSet2 = new HashSet(hashSet);
        hashSet.addAll(object2);
        for (Object object3 : ((ClassWrapperNode)object3).getSubclasses()) {
            hashSet2.addAll(this.renameClass((ClassWrapperNode)object3, map, hashSet));
        }
        object3 = new HashSet(hashSet);
        object3.addAll(hashSet2);
        map.put(structClass.qualifiedName, object3);
        return hashSet2;
    }

    private void b() {
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        for (ClassWrapperNode classWrapperNode : this.h) {
            Object object;
            StructClass structClass = classWrapperNode.getClassStruct();
            Object object2 = (Set)hashMap2.get(structClass.qualifiedName);
            if (object2 == null) {
                hashMap2 = new HashMap();
                this.renameClass(classWrapperNode, hashMap2, new HashSet());
                object2 = (Set)hashMap2.get(structClass.qualifiedName);
            }
            HashMap hashMap3 = new HashMap();
            HashMap hashMap4 = new HashMap();
            if (structClass.superClass != null) {
                object = null;
                object = (Map)hashMap.get((String)structClass.superClass.value);
                if (object != null) {
                    hashMap3.putAll(object);
                    hashMap4.putAll(object);
                }
            }
            object2 = object2.iterator();
            while (object2.hasNext()) {
                object = (String)object2.next();
                Map map = (Map)this.interfaceNameMaps.get(object);
                if (map != null) {
                    hashMap4.putAll(map);
                    continue;
                }
                if ((object = this.context.getClass((String)object)) == null) continue;
                hashMap4.putAll(this.renameAllClasses((StructClass)object));
            }
            this.renameClassIdentifiers(structClass, hashMap3, hashMap4);
            if (classWrapperNode.getSubclasses().isEmpty()) continue;
            hashMap.put(structClass.qualifiedName, hashMap3);
        }
    }

    private Map renameAllClasses(StructClass structClass) {
        HashMap hashMap = new HashMap();
        String[] stringArray = structClass.getInterfaceNames();
        int n = stringArray.length;
        int n2 = 0;
        while (n2 < n) {
            Object object = stringArray[n2];
            Map map = (Map)this.interfaceNameMaps.get(object);
            if (map != null) {
                hashMap.putAll(map);
            } else if ((object = this.context.getClass((String)object)) != null) {
                hashMap.putAll(this.renameAllClasses((StructClass)object));
            }
            ++n2;
        }
        this.renameClassIdentifiers(structClass, hashMap, hashMap);
        return hashMap;
    }

    private void renameInterfaces() {
        HashMap hashMap = new HashMap();
        Iterator iterator = this.i.iterator();
        while (iterator.hasNext()) {
            Object object;
            StructClass structClass = null;
            structClass = ((ClassWrapperNode)iterator.next()).getClassStruct();
            HashMap hashMap2 = new HashMap();
            Object object2 = structClass.getInterfaceNames();
            int n = ((String[])object2).length;
            int n2 = 0;
            while (n2 < n) {
                object = object2[n2];
                if ((object = (Map)hashMap.get(object)) != null) {
                    hashMap2.putAll(object);
                }
                ++n2;
            }
            object = (Set)this.g.get(structClass.qualifiedName);
            if (object != null) {
                Iterator iterator2 = object.iterator();
                while (iterator2.hasNext()) {
                    String string = (String)iterator2.next();
                    object2 = (Map)hashMap.get(string);
                    if (object2 == null) continue;
                    hashMap2.putAll(object2);
                }
            }
            this.renameClassIdentifiers(structClass, hashMap2, hashMap2);
            hashMap.put(structClass.qualifiedName, hashMap2);
        }
        this.interfaceNameMaps = hashMap;
    }

    private void d() {
        Object object = new ArrayList(this.i);
        object.addAll(this.h);
        Iterator iterator = object.iterator();
        while (iterator.hasNext()) {
            object = ((ClassWrapperNode)iterator.next()).getClassStruct();
            if (!((StructClass)object).isOwn()) continue;
            String string = ((StructClass)object).qualifiedName;
            String string2 = null;
            string2 = ConverterHelper.getSimpleClassName(string);
            if (this.helper.toBeRenamed(1, string2, null, null)) {
                do {
                    string2 = ConverterHelper.replaceSimpleClassName(string, this.helper.getNextClassname(string, ConverterHelper.getSimpleClassName(string)));
                } while (this.context.getClasses().containsKey(string2));
                this.interceptor.addName(string, string2);
            }
            if (((StructClass)object).superClass != null) {
                this.interceptor.getName(string, (String)object.superClass.value);
            }
            String[] stringArray = ((StructClass)object).getInterfaceNames();
            int n = stringArray.length;
            int n2 = 0;
            while (n2 < n) {
                object = stringArray[n2];
                this.interceptor.getName(string, (String)object);
                ++n2;
            }
        }
    }

    private void renameClassIdentifiers(StructClass structClass, Map map, Map map2) {
        Object object2;
        String string = structClass.qualifiedName;
        String string2 = this.interceptor.a(1, string, null, null);
        if (string2 == null) {
            string2 = string;
        }
        HashSet<String> hashSet = new HashSet<String>();
        for (Object object2 : structClass.getMethods()) {
            hashSet.add(((StructMethod)object2).getName());
        }
        object2 = structClass.getMethods();
        int n = 0;
        while (n < ((ArrayList)object2).size()) {
            Object object3 = (StructMethod)((ArrayList)object2).get(n);
            Iterator iterator = (String)((VBStyleCollection)object2).getKey(n);
            int n2 = ((StructMethod)object3).getAccessFlags();
            boolean bl = (n2 & 2) != 0;
            String string3 = ((StructMethod)object3).getName();
            if (!structClass.isOwn() || (n2 & 0x100) != 0) {
                if (!bl) {
                    map.put(iterator, string3);
                }
            } else if (this.helper.toBeRenamed(3, string, string3, ((StructMethod)object3).getDescriptor())) {
                if (bl || !map2.containsKey(iterator)) {
                    while (hashSet.contains(string3 = this.helper.getNextMethodname(string, string3, ((StructMethod)object3).getDescriptor()))) {
                    }
                    if (!bl) {
                        map.put(iterator, string3);
                    }
                } else {
                    string3 = (String)map2.get(iterator);
                }
                this.interceptor.addName(String.valueOf(string) + " " + ((StructMethod)object3).getName() + " " + ((StructMethod)object3).getDescriptor(), String.valueOf(string2) + " " + string3 + " " + this.buildNewDescriptor(false, ((StructMethod)object3).getDescriptor()));
            }
            ++n;
        }
        if (!structClass.isOwn()) {
            return;
        }
        HashSet<String> hashSet2 = new HashSet<String>();
        for (Object object3 : structClass.getFields()) {
            hashSet2.add(((StructField)object3).getName());
        }
        for (Object object3 : structClass.getFields()) {
            String string4;
            if (!this.helper.toBeRenamed(2, string, ((StructField)object3).getName(), ((StructField)object3).getDescriptor())) continue;
            while (hashSet2.contains(string4 = this.helper.getNextFieldname(string, ((StructField)object3).getName(), ((StructField)object3).getDescriptor()))) {
            }
            this.interceptor.addName(String.valueOf(string) + " " + ((StructField)object3).getName() + " " + ((StructField)object3).getDescriptor(), String.valueOf(string2) + " " + string4 + " " + this.buildNewDescriptor(true, ((StructField)object3).getDescriptor()));
        }
    }

    private String buildNewDescriptor(boolean bl, String string) {
        boolean bl2 = false;
        if (bl) {
            String string2;
            FieldDescriptor fieldDescriptor = FieldDescriptor.parseDescriptor(string);
            VarType varType = fieldDescriptor.type;
            if (varType.type == 8 && (string2 = this.interceptor.a(1, varType.value, null, null)) != null) {
                varType.value = string2;
                bl2 = true;
            }
            if (bl2) {
                return fieldDescriptor.getDescriptor();
            }
        } else {
            Object object;
            MethodDescriptor methodDescriptor = MethodDescriptor.parseDescriptor(string);
            VarType[] varTypeArray = methodDescriptor.params;
            int n = methodDescriptor.params.length;
            int n2 = 0;
            while (n2 < n) {
                String string3;
                object = varTypeArray[n2];
                if (((VarType)object).type == 8 && (string3 = this.interceptor.a(1, ((VarType)object).value, null, null)) != null) {
                    ((VarType)object).value = string3;
                    bl2 = true;
                }
                ++n2;
            }
            if (methodDescriptor.ret.type == 8 && (object = this.interceptor.a(1, methodDescriptor.ret.value, null, null)) != null) {
                methodDescriptor.ret.value = object;
                bl2 = true;
            }
            if (bl2) {
                return methodDescriptor.getDescriptor();
            }
        }
        return string;
    }

    private static List getReversePostOrderListIterative(List object) {
        ArrayList<Object> arrayList = new ArrayList<Object>();
        LinkedList<Object> linkedList = new LinkedList<Object>();
        LinkedList<Integer> linkedList2 = new LinkedList<Integer>();
        HashSet<Object> hashSet = new HashSet<Object>();
        Iterator iterator = object.iterator();
        while (iterator.hasNext()) {
            object = (ClassWrapperNode)iterator.next();
            linkedList.add(object);
            linkedList2.add(0);
        }
        while (!linkedList.isEmpty()) {
            object = (ClassWrapperNode)linkedList.getLast();
            int n = (Integer)linkedList2.removeLast();
            hashSet.add(object);
            List list = ((ClassWrapperNode)object).getSubclasses();
            while (n < list.size()) {
                ClassWrapperNode classWrapperNode = (ClassWrapperNode)list.get(n);
                if (!hashSet.contains(classWrapperNode)) {
                    linkedList2.add(n + 1);
                    linkedList.add(classWrapperNode);
                    linkedList2.add(0);
                    break;
                }
                ++n;
            }
            if (n != list.size()) continue;
            arrayList.add(0, object);
            linkedList.removeLast();
        }
        return arrayList;
    }

    private void buildInheritanceTree() {
        HashMap<String, ClassWrapperNode> hashMap = new HashMap<String, ClassWrapperNode>();
        HashMap hashMap2 = this.context.getClasses();
        ArrayList arrayList = new ArrayList();
        ArrayList<ClassWrapperNode> arrayList2 = new ArrayList<ClassWrapperNode>();
        block0: for (Object object : hashMap2.values()) {
            if (!((StructClass)object).isOwn()) continue;
            LinkedList<Object> linkedList = new LinkedList<Object>();
            LinkedList<ClassWrapperNode> linkedList2 = new LinkedList<ClassWrapperNode>();
            linkedList.add(object);
            linkedList2.add(null);
            while (!linkedList.isEmpty()) {
                object = (StructClass)linkedList.removeFirst();
                ClassWrapperNode classWrapperNode = (ClassWrapperNode)linkedList2.removeFirst();
                ClassWrapperNode classWrapperNode2 = (ClassWrapperNode)hashMap.get(((StructClass)object).qualifiedName);
                boolean bl = classWrapperNode2 == null;
                if (bl) {
                    classWrapperNode2 = new ClassWrapperNode((StructClass)object);
                    hashMap.put(((StructClass)object).qualifiedName, classWrapperNode2);
                }
                if (classWrapperNode != null) {
                    classWrapperNode2.addSubclass(classWrapperNode);
                }
                if (!bl) continue block0;
                boolean bl2 = (((StructClass)object).access_flags & 0x200) != 0;
                bl = false;
                if (bl2) {
                    String[] stringArray = ((StructClass)object).getInterfaceNames();
                    int n = stringArray.length;
                    int n2 = 0;
                    while (n2 < n) {
                        object = stringArray[n2];
                        if ((object = (StructClass)hashMap2.get(object)) != null) {
                            linkedList.add(object);
                            linkedList2.add(classWrapperNode2);
                            bl = true;
                        }
                        ++n2;
                    }
                } else if (((StructClass)object).superClass != null && (object = (StructClass)hashMap2.get((String)object.superClass.value)) != null) {
                    linkedList.add(object);
                    linkedList2.add(classWrapperNode2);
                    bl = true;
                }
                if (bl) continue;
                (bl2 ? arrayList2 : arrayList).add(classWrapperNode2);
            }
        }
        this.rootClasses = arrayList;
        this.rootInterfaces = arrayList2;
    }
}

