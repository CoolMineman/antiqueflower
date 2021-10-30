/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.attr;

import de.fernflower.struct.attr.StructGeneralAttribute;
import de.fernflower.struct.consts.ConstantPool;

public final class StructGenericSignatureAttribute
extends StructGeneralAttribute {
    private String signature;

    public final void initContent(ConstantPool constantPool) {
        this.name = "Signature";
        this.signature = (String)constantPool.getPrimitiveConstant((int)((this.info[0] & 255) << 8 | this.info[1] & 255)).value;
    }

    public final String getSignature() {
        return this.signature;
    }
}

