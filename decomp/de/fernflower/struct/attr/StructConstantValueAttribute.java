/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.attr;

import de.fernflower.struct.attr.StructGeneralAttribute;
import de.fernflower.struct.consts.ConstantPool;

public final class StructConstantValueAttribute
extends StructGeneralAttribute {
    private int index;

    public final void initContent(ConstantPool constantPool) {
        this.name = "ConstantValue";
        this.index = (this.info[0] & 0xFF) << 8 | this.info[1] & 0xFF;
    }

    public final int getIndex() {
        return this.index;
    }
}

