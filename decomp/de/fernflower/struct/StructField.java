/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct;

import de.fernflower.struct.consts.ConstantPool;
import de.fernflower.util.VBStyleCollection;

public final class StructField {
    public int access_flags;
    public int name_index;
    public int descriptor_index;
    private String name;
    private String descriptor;
    private VBStyleCollection attributes;

    public final void initStrings(ConstantPool stringArray, int n) {
        stringArray = stringArray.getClassElement(2, n, this.name_index, this.descriptor_index);
        this.name = stringArray[0];
        this.descriptor = stringArray[1];
    }

    public final VBStyleCollection getAttributes() {
        return this.attributes;
    }

    public final void setAttributes(VBStyleCollection vBStyleCollection) {
        this.attributes = vBStyleCollection;
    }

    public final String getDescriptor() {
        return this.descriptor;
    }

    public final String getName() {
        return this.name;
    }
}

