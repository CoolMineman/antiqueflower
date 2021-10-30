/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.attr;

import de.fernflower.struct.attr.StructGeneralAttribute;
import de.fernflower.struct.consts.ConstantPool;
import java.util.HashMap;

public final class StructLocalVariableTableAttribute
extends StructGeneralAttribute {
    private HashMap mapVarNames = new HashMap();

    public final void initContent(ConstantPool constantPool) {
        this.name = "LocalVariableTable";
        int n = (this.info[0] & 0xFF) << 8 | this.info[1] & 0xFF;
        int n2 = 6;
        int n3 = 0;
        while (n3 < n) {
            int n4 = (this.info[n2] & 0xFF) << 8 | this.info[n2 + 1] & 0xFF;
            int n5 = (this.info[n2 + 4] & 0xFF) << 8 | this.info[n2 + 5] & 0xFF;
            this.mapVarNames.put(n5, (String)constantPool.getPrimitiveConstant((int)n4).value);
            ++n3;
            n2 += 10;
        }
    }

    public final void addLocalVariableTable(StructLocalVariableTableAttribute structLocalVariableTableAttribute) {
        this.mapVarNames.putAll(structLocalVariableTableAttribute.mapVarNames);
    }

    public final HashMap getMapVarNames() {
        return this.mapVarNames;
    }
}

