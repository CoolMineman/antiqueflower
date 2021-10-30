/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.attr;

import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.struct.attr.StructAnnotationAttribute;
import de.fernflower.struct.attr.StructGeneralAttribute;
import de.fernflower.struct.consts.ConstantPool;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public final class StructAnnDefaultAttribute
extends StructGeneralAttribute {
    private Exprent defaultValue;

    public final void initContent(ConstantPool constantPool) {
        this.name = "AnnotationDefault";
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(this.info));
        this.defaultValue = StructAnnotationAttribute.parseAnnotationElement(dataInputStream, constantPool);
    }

    public final Exprent getDefaultValue() {
        return this.defaultValue;
    }
}

