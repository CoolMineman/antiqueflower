/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.renamer;

import de.fernflower.struct.StructClass;
import java.util.ArrayList;
import java.util.List;

public final class ClassWrapperNode {
    private StructClass classStruct;
    private List subclasses = new ArrayList();

    public ClassWrapperNode(StructClass structClass) {
        this.classStruct = structClass;
    }

    public final void addSubclass(ClassWrapperNode classWrapperNode) {
        this.subclasses.add(classWrapperNode);
    }

    public final StructClass getClassStruct() {
        return this.classStruct;
    }

    public final List getSubclasses() {
        return this.subclasses;
    }
}

