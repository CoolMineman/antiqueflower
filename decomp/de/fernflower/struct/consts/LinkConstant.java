/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.consts;

import de.fernflower.struct.consts.ConstantPool;
import de.fernflower.struct.consts.PooledConstant;

public final class LinkConstant
extends PooledConstant {
    private int index1;
    private int index2;
    public String classname;
    public String elementname;
    public String descriptor;

    public LinkConstant(int n, String string, String string2, String string3) {
        this.type = n;
        this.classname = string;
        this.elementname = string2;
        this.descriptor = string3;
        this.initConstant();
    }

    public LinkConstant(int n, int n2, int n3) {
        this.type = n;
        this.index1 = n2;
        this.index2 = n3;
    }

    public final void resolveConstant(ConstantPool object) {
        if (this.type == 12) {
            this.elementname = (String)object.getPrimitiveConstant((int)this.index1).value;
            this.descriptor = (String)object.getPrimitiveConstant((int)this.index2).value;
        } else {
            this.classname = (String)object.getPrimitiveConstant((int)this.index1).value;
            object = ((ConstantPool)object).getLinkConstant(this.index2);
            this.elementname = ((LinkConstant)object).elementname;
            this.descriptor = ((LinkConstant)object).descriptor;
        }
        this.initConstant();
    }

    public final boolean equals(Object object) {
        if (object == null || !(object instanceof LinkConstant)) {
            return false;
        }
        object = (LinkConstant)object;
        if (this.type == ((LinkConstant)object).type && this.elementname.equals(((LinkConstant)object).elementname) && this.descriptor.equals(((LinkConstant)object).descriptor)) {
            if (this.type == 12) {
                return this.classname.equals(((LinkConstant)object).classname);
            }
            return true;
        }
        return false;
    }

    private void initConstant() {
        if (stringArray.type == 10 || stringArray.type == 11) {
            String string = null;
            String[] stringArray = stringArray.descriptor.split("[()]");
            string = stringArray[1];
            int n = 0;
            int n2 = string.length();
            while (n < n2) {
                if (string.charAt(n) == 'L') {
                    n = string.indexOf(";", n);
                }
                ++n;
            }
            "V".equals(stringArray[2]);
            if (!"D".equals(stringArray[2])) {
                "J".equals(stringArray[2]);
            }
            return;
        }
        if (stringArray.type == 9 && !"D".equals(stringArray.descriptor)) {
            "J".equals(stringArray.descriptor);
        }
    }
}

