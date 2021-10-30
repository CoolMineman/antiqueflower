/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.main;

import de.fernflower.main.rels.ClassWrapper;
import de.fernflower.modules.decompiler.exps.InvocationExprent;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.gen.VarType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ClassesProcessor$ClassNode {
    public int type = 0;
    public int access;
    public String simpleName;
    public StructClass classStruct;
    public ClassWrapper wrapper;
    public String enclosingMethod;
    public InvocationExprent superInvocation;
    public HashMap mapFieldsToVars = new HashMap();
    public HashMap this$0 = new HashMap();
    public VarType anonimousClassType;
    public List nested = new ArrayList();
    public Set enclosingClasses = new HashSet();
    public ClassesProcessor$ClassNode parent;

    public ClassesProcessor$ClassNode(StructClass structClass) {
        this.classStruct = structClass;
        this.simpleName = structClass.qualifiedName.substring(structClass.qualifiedName.lastIndexOf(47) + 1);
    }

    public final ClassesProcessor$ClassNode getClassNode(String string) {
        for (ClassesProcessor$ClassNode classesProcessor$ClassNode : classesProcessor$ClassNode.nested) {
            if (!string.equals(classesProcessor$ClassNode.classStruct.qualifiedName)) continue;
            return classesProcessor$ClassNode;
        }
        return null;
    }
}

