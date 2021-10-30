/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.gen;

import de.fernflower.struct.gen.VarType;

public final class FieldDescriptor {
    public static final FieldDescriptor INTEGER_DESCRIPTOR = FieldDescriptor.parseDescriptor("Ljava/lang/Integer;");
    public static final FieldDescriptor LONG_DESCRIPTOR = FieldDescriptor.parseDescriptor("Ljava/lang/Long;");
    public static final FieldDescriptor FLOAT_DESCRIPTOR = FieldDescriptor.parseDescriptor("Ljava/lang/Float;");
    public static final FieldDescriptor DOUBLE_DESCRIPTOR = FieldDescriptor.parseDescriptor("Ljava/lang/Double;");
    public VarType type;
    public String descriptorString;

    private FieldDescriptor() {
    }

    public static FieldDescriptor parseDescriptor(String string) {
        FieldDescriptor fieldDescriptor = new FieldDescriptor();
        new FieldDescriptor().type = new VarType(string);
        fieldDescriptor.descriptorString = string;
        return fieldDescriptor;
    }

    public final String getDescriptor() {
        return this.type.toString();
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof FieldDescriptor) {
            object = (FieldDescriptor)object;
            return this.type.equals(((FieldDescriptor)object).type);
        }
        return false;
    }

    public final int hashCode() {
        return this.type.hashCode();
    }
}

