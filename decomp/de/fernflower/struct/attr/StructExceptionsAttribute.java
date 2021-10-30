/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.attr;

import de.fernflower.struct.attr.StructGeneralAttribute;
import de.fernflower.struct.consts.ConstantPool;
import java.util.ArrayList;
import java.util.List;

public final class StructExceptionsAttribute
extends StructGeneralAttribute {
    private List throwsExceptions = new ArrayList();

    public final void initContent(ConstantPool constantPool) {
        this.name = "Exceptions";
        int n = 2 + (((this.info[0] & 0xFF) << 8 | this.info[1] & 0xFF) << 1);
        int n2 = 2;
        while (n2 < n) {
            int n3 = (this.info[n2] & 0xFF) << 8 | this.info[n2 + 1] & 0xFF;
            this.throwsExceptions.add(n3);
            n2 += 2;
        }
    }

    public final String getExcClassname(int n, ConstantPool constantPool) {
        return (String)constantPool.getPrimitiveConstant((int)((Integer)this.throwsExceptions.get((int)n)).intValue()).value;
    }

    public final List getThrowsExceptions() {
        return this.throwsExceptions;
    }
}

