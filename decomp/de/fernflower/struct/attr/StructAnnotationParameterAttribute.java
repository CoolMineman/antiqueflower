/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.attr;

import de.fernflower.modules.decompiler.exps.AnnotationExprent;
import de.fernflower.struct.attr.StructAnnotationAttribute;
import de.fernflower.struct.attr.StructGeneralAttribute;
import de.fernflower.struct.consts.ConstantPool;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class StructAnnotationParameterAttribute
extends StructGeneralAttribute {
    private List paramAnnotations;

    public final void initContent(ConstantPool constantPool) {
        super.initContent(constantPool);
        this.paramAnnotations = new ArrayList();
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(this.info));
        try {
            int n = dataInputStream.readUnsignedByte();
            int n2 = 0;
            while (n2 < n) {
                ArrayList<AnnotationExprent> arrayList = new ArrayList<AnnotationExprent>();
                int n3 = dataInputStream.readUnsignedShort();
                int n4 = 0;
                while (n4 < n3) {
                    arrayList.add(StructAnnotationAttribute.parseAnnotation(dataInputStream, constantPool));
                    ++n4;
                }
                this.paramAnnotations.add(arrayList);
                ++n2;
            }
            return;
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
    }

    public final List getParamAnnotations() {
        return this.paramAnnotations;
    }
}

