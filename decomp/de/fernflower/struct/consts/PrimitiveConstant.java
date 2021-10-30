/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.consts;

import de.fernflower.struct.consts.ConstantPool;
import de.fernflower.struct.consts.PooledConstant;

public final class PrimitiveConstant
extends PooledConstant {
    private int index;
    public Object value;
    public boolean isArray;

    public PrimitiveConstant(int n, Object object) {
        this.type = n;
        this.value = object;
        this.initConstant();
    }

    public PrimitiveConstant(int n, int n2) {
        this.type = n;
        this.index = n2;
    }

    public final void resolveConstant(ConstantPool constantPool) {
        if (this.type == 7 || this.type == 8) {
            this.value = (String)constantPool.getPrimitiveConstant((int)this.index).value;
            this.initConstant();
        }
    }

    public final boolean equals(Object object) {
        if (object == null || !(object instanceof PrimitiveConstant)) {
            return false;
        }
        object = (PrimitiveConstant)object;
        return this.type == ((PrimitiveConstant)object).type && this.isArray == ((PrimitiveConstant)object).isArray && this.value.equals(((PrimitiveConstant)object).value);
    }

    private void initConstant() {
        if (this.type == 7) {
            String string = null;
            string = (String)this.value;
            this.isArray = string.length() > 0 && string.charAt(0) == '[';
        }
    }
}

