/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.gen;

import de.fernflower.struct.gen.VarType;
import java.util.ArrayList;

public final class MethodDescriptor {
    public VarType[] params;
    public VarType ret;

    public static MethodDescriptor parseDescriptor(String stringArray) {
        MethodDescriptor methodDescriptor = new MethodDescriptor();
        ArrayList<String> arrayList = new ArrayList<String>();
        stringArray = stringArray.split("[()]");
        String string = stringArray[1];
        int n = -1;
        int n2 = 0;
        int n3 = string.length();
        while (n2 < n3) {
            switch (string.charAt(n2)) {
                case '[': {
                    if (n >= 0) break;
                    n = n2;
                    break;
                }
                case 'L': {
                    int n4 = string.indexOf(";", n2);
                    arrayList.add(string.substring(n < 0 ? n2 : n, n4 + 1));
                    n2 = n4;
                    n = -1;
                    break;
                }
                default: {
                    arrayList.add(string.substring(n < 0 ? n2 : n, n2 + 1));
                    n = -1;
                }
            }
            ++n2;
        }
        arrayList.add(stringArray[2]);
        methodDescriptor.params = new VarType[arrayList.size() - 1];
        int n5 = 0;
        while (n5 < arrayList.size() - 1) {
            methodDescriptor.params[n5] = new VarType((String)arrayList.get(n5));
            ++n5;
        }
        methodDescriptor.ret = new VarType((String)arrayList.get(n5));
        return methodDescriptor;
    }

    public final String getDescriptor() {
        String string = "(";
        int n = 0;
        while (n < this.params.length) {
            string = String.valueOf(string) + this.params[n].toString();
            ++n;
        }
        return String.valueOf(string) + ")" + this.ret.toString();
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof MethodDescriptor) {
            object = (MethodDescriptor)object;
            if (this.ret.equals(((MethodDescriptor)object).ret) && this.params.length == ((MethodDescriptor)object).params.length) {
                int n = 0;
                while (n < this.params.length) {
                    if (!this.params[n].equals(((MethodDescriptor)object).params[n])) {
                        return false;
                    }
                    ++n;
                }
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        return this.ret.hashCode();
    }
}

