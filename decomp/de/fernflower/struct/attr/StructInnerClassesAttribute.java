/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.attr;

import de.fernflower.struct.attr.StructGeneralAttribute;
import de.fernflower.struct.consts.ConstantPool;
import java.util.ArrayList;
import java.util.List;

public final class StructInnerClassesAttribute
extends StructGeneralAttribute {
    private List classentries = new ArrayList();
    private List stringentries = new ArrayList();

    public final void initContent(ConstantPool constantPool) {
        this.name = "InnerClasses";
        int n = 2 + (((this.info[0] & 0xFF) << 8 | this.info[1] & 0xFF) << 3);
        int n2 = 2;
        while (n2 < n) {
            int[] nArray = new int[4];
            int n3 = 0;
            while (n3 < 4) {
                nArray[n3] = (this.info[n2] & 0xFF) << 8 | this.info[n2 + 1] & 0xFF;
                n2 += 2;
                ++n3;
            }
            this.classentries.add(nArray);
        }
        for (int[] nArray : this.classentries) {
            String[] stringArray = new String[3];
            String[] stringArray2 = stringArray;
            stringArray[0] = (String)constantPool.getPrimitiveConstant((int)nArray[0]).value;
            if (nArray[1] != 0) {
                stringArray2[1] = (String)constantPool.getPrimitiveConstant((int)nArray[1]).value;
            }
            if (nArray[2] != 0) {
                stringArray2[2] = (String)constantPool.getPrimitiveConstant((int)nArray[2]).value;
            }
            this.stringentries.add(stringArray2);
        }
    }

    public final List getClassentries() {
        return this.classentries;
    }

    public final List getStringentries() {
        return this.stringentries;
    }
}

