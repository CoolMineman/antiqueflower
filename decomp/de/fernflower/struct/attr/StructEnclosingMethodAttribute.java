/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.attr;

import de.fernflower.struct.attr.StructGeneralAttribute;
import de.fernflower.struct.consts.ConstantPool;
import de.fernflower.struct.consts.LinkConstant;

public final class StructEnclosingMethodAttribute
extends StructGeneralAttribute {
    private String classname;
    private String mtname;
    private String methodDescriptor;

    public final void initContent(ConstantPool object) {
        this.name = "EnclosingMethod";
        int n = (this.info[0] & 0xFF) << 8 | this.info[1] & 0xFF;
        int n2 = (this.info[2] & 0xFF) << 8 | this.info[3] & 0xFF;
        this.classname = (String)object.getPrimitiveConstant((int)n).value;
        if (n2 != 0) {
            object = ((ConstantPool)object).getLinkConstant(n2);
            this.mtname = ((LinkConstant)object).elementname;
            this.methodDescriptor = ((LinkConstant)object).descriptor;
        }
    }

    public final String getClassname() {
        return this.classname;
    }

    public final String getMethodDescriptor() {
        return this.methodDescriptor;
    }

    public final String getMethodName() {
        return this.mtname;
    }
}

