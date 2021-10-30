/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.gen;

import de.fernflower.util.InterpreterUtil;

public final class VarType {
    public static final VarType VARTYPE_UNKNOWN = new VarType(17);
    public static final VarType VARTYPE_INT = new VarType(4);
    public static final VarType VARTYPE_FLOAT = new VarType(3);
    public static final VarType VARTYPE_LONG = new VarType(5);
    public static final VarType VARTYPE_DOUBLE = new VarType(2);
    public static final VarType VARTYPE_BYTE = new VarType(0);
    public static final VarType VARTYPE_CHAR = new VarType(1);
    public static final VarType VARTYPE_SHORT = new VarType(6);
    public static final VarType VARTYPE_BOOLEAN = new VarType(7);
    public static final VarType VARTYPE_BYTECHAR = new VarType(15);
    public static final VarType VARTYPE_SHORTCHAR = new VarType(16);
    public static final VarType VARTYPE_NULL = new VarType(13, 0, null);
    public static final VarType VARTYPE_STRING;
    public static final VarType VARTYPE_CLASS;
    public static final VarType VARTYPE_OBJECT;
    public static final VarType VARTYPE_VOID;
    public int type;
    public int type_family;
    public int arraydim;
    public String value;
    public int stack_size;
    public int convinfo;

    static {
        new VarType(12);
        VARTYPE_STRING = new VarType(8, 0, "java/lang/String");
        VARTYPE_CLASS = new VarType(8, 0, "java/lang/Class");
        VARTYPE_OBJECT = new VarType(8, 0, "java/lang/Object");
        VARTYPE_VOID = new VarType(10);
    }

    public VarType(int n) {
        String string;
        this.type = n;
        this.arraydim = 0;
        switch (n) {
            case 0: {
                string = "B";
                break;
            }
            case 1: {
                string = "C";
                break;
            }
            case 2: {
                string = "D";
                break;
            }
            case 3: {
                string = "F";
                break;
            }
            case 4: {
                string = "I";
                break;
            }
            case 5: {
                string = "J";
                break;
            }
            case 6: {
                string = "S";
                break;
            }
            case 7: {
                string = "Z";
                break;
            }
            case 10: {
                string = "V";
                break;
            }
            case 12: {
                string = "G";
                break;
            }
            case 14: {
                string = "N";
                break;
            }
            case 9: {
                string = "A";
                break;
            }
            case 15: {
                string = "X";
                break;
            }
            case 16: {
                string = "Y";
                break;
            }
            case 17: {
                string = "U";
                break;
            }
            case 8: 
            case 13: {
                string = null;
                break;
            }
            default: {
                throw new RuntimeException("Invalid type");
            }
        }
        this.value = string;
        this.setStackSize(n);
        this.setFamily();
    }

    public VarType(int n, int n2) {
        this(n);
        this.arraydim = 1;
        this.setFamily();
    }

    public VarType(int n, int n2, String string) {
        this(n);
        this.arraydim = n2;
        this.value = string;
        this.setFamily();
    }

    public VarType(String string) {
        this(string, false);
    }

    public VarType(String string, boolean bl) {
        this.parseTypeString(string, bl);
        this.setStackSize(this.type);
        this.setFamily();
    }

    public final void decArrayDim() {
        if (this.arraydim > 0) {
            --this.arraydim;
            this.setFamily();
        }
    }

    public final String toString() {
        String string = "";
        int n = 0;
        while (n < this.arraydim) {
            string = String.valueOf(string) + "[";
            ++n;
        }
        string = this.type == 8 ? String.valueOf(string) + "L" + this.value + ";" : String.valueOf(string) + this.value;
        return string;
    }

    public final VarType copy() {
        VarType varType = new VarType(this.type, this.arraydim, this.value);
        new VarType(this.type, this.arraydim, this.value).convinfo = this.convinfo;
        return varType;
    }

    public final boolean isSuperset(VarType varType) {
        return this.equals(varType) || this.isStrictSuperset(varType);
    }

    public final boolean isStrictSuperset(VarType varType) {
        int n = varType.type;
        if (n == 17 && this.type != 17) {
            return true;
        }
        if (varType.arraydim > 0) {
            return this.equals(VARTYPE_OBJECT);
        }
        if (this.arraydim > 0) {
            return n == 13;
        }
        boolean bl = false;
        switch (this.type) {
            case 4: {
                bl = false | (n == 6 || n == 1);
            }
            case 6: {
                bl |= n == 0;
            }
            case 1: {
                bl |= n == 16;
            }
            case 0: 
            case 16: {
                bl |= n == 15;
            }
            case 15: {
                bl |= n == 7;
                break;
            }
            case 8: {
                if (n == 13) {
                    return true;
                }
                if (!this.equals(VARTYPE_OBJECT)) break;
                return n == 8 && !varType.equals(VARTYPE_OBJECT);
            }
        }
        return bl;
    }

    public static VarType getCommonMinType(VarType varType, VarType varType2) {
        if (varType.isSuperset(varType2)) {
            return varType2;
        }
        if (varType2.isSuperset(varType)) {
            return varType;
        }
        if (varType.type_family == varType2.type_family) {
            switch (varType.type_family) {
                case 2: {
                    if (varType.type == 1 && varType2.type == 6 || varType.type == 6 && varType2.type == 1) {
                        return VARTYPE_SHORTCHAR;
                    }
                    return VARTYPE_BYTECHAR;
                }
                case 6: {
                    return VARTYPE_NULL;
                }
            }
        }
        return null;
    }

    public static VarType getCommonSupertype(VarType varType, VarType varType2) {
        if (varType.isSuperset(varType2)) {
            return varType;
        }
        if (varType2.isSuperset(varType)) {
            return varType2;
        }
        if (varType.type_family == varType2.type_family) {
            switch (varType.type_family) {
                case 2: {
                    if (varType.type == 16 && varType2.type == 0 || varType.type == 0 && varType2.type == 16) {
                        return VARTYPE_SHORT;
                    }
                    return VARTYPE_INT;
                }
                case 6: {
                    return VARTYPE_OBJECT;
                }
            }
        }
        return null;
    }

    public static VarType getMinTypeInFamily(int n) {
        switch (n) {
            case 1: {
                return VARTYPE_BOOLEAN;
            }
            case 2: {
                return VARTYPE_BYTECHAR;
            }
            case 6: {
                return VARTYPE_NULL;
            }
            case 3: {
                return VARTYPE_FLOAT;
            }
            case 4: {
                return VARTYPE_LONG;
            }
            case 5: {
                return VARTYPE_DOUBLE;
            }
            case 0: {
                return VARTYPE_UNKNOWN;
            }
        }
        throw new RuntimeException("invalid type family!");
    }

    public final boolean equals(Object object) {
        if (object == null || !(object instanceof VarType)) {
            return false;
        }
        object = (VarType)object;
        return this.type == ((VarType)object).type && this.arraydim == ((VarType)object).arraydim && InterpreterUtil.equalObjects(this.value, ((VarType)object).value);
    }

    private void parseTypeString(String string, boolean bl) {
        int n = 0;
        while (n < string.length()) {
            switch (string.charAt(n)) {
                case '[': {
                    ++this.arraydim;
                    break;
                }
                case 'L': {
                    if (string.charAt(string.length() - 1) == ';') {
                        this.type = 8;
                        this.value = string.substring(n + 1, string.length() - 1);
                        return;
                    }
                }
                default: {
                    int n2;
                    VarType varType;
                    this.value = string.substring(n, string.length());
                    if (bl && n == 0 || this.value.length() > 1) {
                        varType = this;
                        n2 = 8;
                    } else {
                        varType = this;
                        switch (this.value.charAt(0)) {
                            case 'B': {
                                n2 = 0;
                                break;
                            }
                            case 'C': {
                                n2 = 1;
                                break;
                            }
                            case 'D': {
                                n2 = 2;
                                break;
                            }
                            case 'F': {
                                n2 = 3;
                                break;
                            }
                            case 'I': {
                                n2 = 4;
                                break;
                            }
                            case 'J': {
                                n2 = 5;
                                break;
                            }
                            case 'S': {
                                n2 = 6;
                                break;
                            }
                            case 'Z': {
                                n2 = 7;
                                break;
                            }
                            case 'V': {
                                n2 = 10;
                                break;
                            }
                            case 'G': {
                                n2 = 12;
                                break;
                            }
                            case 'N': {
                                n2 = 14;
                                break;
                            }
                            case 'A': {
                                n2 = 9;
                                break;
                            }
                            case 'X': {
                                n2 = 15;
                                break;
                            }
                            case 'Y': {
                                n2 = 16;
                                break;
                            }
                            case 'U': {
                                n2 = 17;
                                break;
                            }
                            default: {
                                throw new RuntimeException("Invalid type");
                            }
                        }
                    }
                    varType.type = n2;
                    return;
                }
            }
            ++n;
        }
    }

    /*
     * Unable to fully structure code
     */
    private void setStackSize(int var1_1) {
        block4: {
            if (this.arraydim <= 0) break block4;
            v0 = this;
            ** GOTO lbl-1000
        }
        v0 = this;
        if (var1_1 == 2 || var1_1 == 5) {
            v1 = 2;
        } else if (var1_1 == 10 || var1_1 == 12) {
            v1 = 0;
        } else lbl-1000:
        // 2 sources

        {
            v1 = 1;
        }
        v0.stack_size = v1;
    }

    private void setFamily() {
        if (this.arraydim > 0) {
            this.type_family = 6;
            return;
        }
        switch (this.type) {
            case 0: 
            case 1: 
            case 4: 
            case 6: 
            case 15: 
            case 16: {
                this.type_family = 2;
                return;
            }
            case 2: {
                this.type_family = 5;
                return;
            }
            case 3: {
                this.type_family = 3;
                return;
            }
            case 5: {
                this.type_family = 4;
                return;
            }
            case 7: {
                this.type_family = 1;
                return;
            }
            case 8: 
            case 13: {
                this.type_family = 6;
                return;
            }
        }
        this.type_family = 0;
    }
}

