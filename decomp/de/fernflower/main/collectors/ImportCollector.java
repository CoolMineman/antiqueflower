/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main.collectors;

import de.fernflower.main.ClassesProcessor$ClassNode;
import de.fernflower.main.DecompilerContext;
import de.fernflower.struct.StructContext;
import de.fernflower.t;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public final class ImportCollector {
    private HashMap mapSimpleNames = new HashMap();
    private HashSet setNotImportedNames = new HashSet();
    private String currentPackageSlash = "";
    private String currentPackagePoint = "";

    public ImportCollector(ClassesProcessor$ClassNode object) {
        object = object.classStruct.qualifiedName;
        int n = ((String)object).lastIndexOf("/");
        if (n >= 0) {
            this.currentPackageSlash = ((String)object).substring(0, n);
            this.currentPackagePoint = this.currentPackageSlash.replace('/', '.');
            this.currentPackageSlash = String.valueOf(this.currentPackageSlash) + "/";
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final String getShortName(String string, boolean bl) {
        StructContext structContext;
        Object object = null;
        object = (ClassesProcessor$ClassNode)DecompilerContext.getClassprocessor().getMapRootClasses().get(string.replace('.', '/'));
        String string2 = null;
        if (object != null && ((ClassesProcessor$ClassNode)object).classStruct.isOwn()) {
            string2 = ((ClassesProcessor$ClassNode)object).simpleName;
            while (((ClassesProcessor$ClassNode)object).parent != null && ((ClassesProcessor$ClassNode)object).type == 1) {
                string2 = String.valueOf(object.parent.simpleName) + "." + string2;
                object = ((ClassesProcessor$ClassNode)object).parent;
            }
            if (((ClassesProcessor$ClassNode)object).type != 0) return string2;
            string = object.classStruct.qualifiedName.replace('/', '.');
        } else if (object == null || !((ClassesProcessor$ClassNode)object).classStruct.isOwn()) {
            string = string.replace('$', '.');
        }
        object = string;
        String string3 = "";
        int n = string.lastIndexOf(".");
        if (n >= 0) {
            object = string.substring(n + 1);
            string3 = string.substring(0, n);
        }
        if ((structContext = DecompilerContext.getStructcontext()).getClass(String.valueOf(this.currentPackageSlash) + (String)object) != null && !string3.equals(this.currentPackagePoint) || structContext.getClass((String)object) != null || this.mapSimpleNames.containsKey(object) && !string3.equals(this.mapSimpleNames.get(object))) {
            return string;
        }
        if (!this.mapSimpleNames.containsKey(object)) {
            this.mapSimpleNames.put(object, string3);
            if (!bl) {
                this.setNotImportedNames.add(object);
            }
        }
        if (string2 != null) return string2;
        return object;
    }

    public final void writeImports(BufferedWriter bufferedWriter) {
        Object object = new ArrayList(((ImportCollector)object2).mapSimpleNames.entrySet());
        Collections.sort(object, new t());
        ArrayList<Object> arrayList = new ArrayList<Object>();
        Iterator iterator = object.iterator();
        while (iterator.hasNext()) {
            object = (Map.Entry)iterator.next();
            if (((ImportCollector)object2).setNotImportedNames.contains(object.getKey()) || "java.lang".equals(object.getValue()) || ((String)object.getValue()).length() <= 0) continue;
            object = String.valueOf((String)object.getValue()) + "." + (String)object.getKey();
            arrayList.add(object);
        }
        for (Object object2 : arrayList) {
            bufferedWriter.write("import " + (String)object2 + ";");
            bufferedWriter.newLine();
        }
    }
}

