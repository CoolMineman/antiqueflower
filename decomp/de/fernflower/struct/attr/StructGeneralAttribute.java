/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.attr;

import de.fernflower.struct.attr.StructAnnDefaultAttribute;
import de.fernflower.struct.attr.StructAnnotationAttribute;
import de.fernflower.struct.attr.StructAnnotationParameterAttribute;
import de.fernflower.struct.attr.StructConstantValueAttribute;
import de.fernflower.struct.attr.StructEnclosingMethodAttribute;
import de.fernflower.struct.attr.StructExceptionsAttribute;
import de.fernflower.struct.attr.StructGenericSignatureAttribute;
import de.fernflower.struct.attr.StructInnerClassesAttribute;
import de.fernflower.struct.attr.StructLocalVariableTableAttribute;
import de.fernflower.struct.consts.ConstantPool;

public class StructGeneralAttribute {
    private int attribute_name_index;
    protected byte[] info;
    protected String name;

    public void initContent(ConstantPool constantPool) {
        this.name = (String)constantPool.getPrimitiveConstant((int)this.attribute_name_index).value;
    }

    public static StructGeneralAttribute getMatchingAttributeInstance(int n, String object) {
        if ("InnerClasses".equals(object)) {
            object = new StructInnerClassesAttribute();
        } else if ("ConstantValue".equals(object)) {
            object = new StructConstantValueAttribute();
        } else if ("Signature".equals(object)) {
            object = new StructGenericSignatureAttribute();
        } else if ("AnnotationDefault".equals(object)) {
            object = new StructAnnDefaultAttribute();
        } else if ("Exceptions".equals(object)) {
            object = new StructExceptionsAttribute();
        } else if ("EnclosingMethod".equals(object)) {
            object = new StructEnclosingMethodAttribute();
        } else if ("RuntimeVisibleAnnotations".equals(object) || "RuntimeInvisibleAnnotations".equals(object)) {
            object = new StructAnnotationAttribute();
        } else if ("RuntimeVisibleParameterAnnotations".equals(object) || "RuntimeInvisibleParameterAnnotations".equals(object)) {
            object = new StructAnnotationParameterAttribute();
        } else if ("LocalVariableTable".equals(object)) {
            object = new StructLocalVariableTableAttribute();
        } else {
            return null;
        }
        ((StructGeneralAttribute)object).attribute_name_index = n;
        return object;
    }

    public final void setInfo(byte[] byArray) {
        this.info = byArray;
    }

    public final String getName() {
        return this.name;
    }
}

