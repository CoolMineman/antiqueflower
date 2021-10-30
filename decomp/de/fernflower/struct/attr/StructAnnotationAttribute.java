/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.attr;

import de.fernflower.bx;
import de.fernflower.modules.decompiler.exps.AnnotationExprent;
import de.fernflower.modules.decompiler.exps.ConstExprent;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FieldExprent;
import de.fernflower.modules.decompiler.exps.NewExprent;
import de.fernflower.struct.attr.StructGeneralAttribute;
import de.fernflower.struct.consts.ConstantPool;
import de.fernflower.struct.consts.PrimitiveConstant;
import de.fernflower.struct.gen.FieldDescriptor;
import de.fernflower.struct.gen.VarType;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class StructAnnotationAttribute
extends StructGeneralAttribute {
    private List annotations;

    public final void initContent(ConstantPool constantPool) {
        super.initContent(constantPool);
        this.annotations = new ArrayList();
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(this.info, 2, this.info.length));
        int n = (this.info[0] & 0xFF) << 8 | this.info[1] & 0xFF;
        int n2 = 0;
        while (n2 < n) {
            this.annotations.add(StructAnnotationAttribute.parseAnnotation(dataInputStream, constantPool));
            ++n2;
        }
    }

    public static AnnotationExprent parseAnnotation(DataInputStream dataInputStream, ConstantPool constantPool) {
        try {
            Object object = (String)constantPool.getPrimitiveConstant((int)dataInputStream.readUnsignedShort()).value;
            object = new VarType((String)object);
            int n = dataInputStream.readUnsignedShort();
            ArrayList<String> arrayList = new ArrayList<String>();
            ArrayList<Exprent> arrayList2 = new ArrayList<Exprent>();
            int n2 = 0;
            while (n2 < n) {
                arrayList.add((String)constantPool.getPrimitiveConstant((int)dataInputStream.readUnsignedShort()).value);
                arrayList2.add(StructAnnotationAttribute.parseAnnotationElement(dataInputStream, constantPool));
                ++n2;
            }
            return new AnnotationExprent(((VarType)object).value, arrayList, arrayList2);
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
    }

    public static Exprent parseAnnotationElement(DataInputStream object, ConstantPool object2) {
        try {
            int n = ((DataInputStream)object).readUnsignedByte();
            switch (n) {
                case 101: {
                    Object var3_6 = null;
                    String string = (String)object2.getPrimitiveConstant((int)object.readUnsignedShort()).value;
                    object = (String)object2.getPrimitiveConstant((int)object.readUnsignedShort()).value;
                    object2 = FieldDescriptor.parseDescriptor(string);
                    return new FieldExprent((String)object, object2.type.value, true, null, (FieldDescriptor)object2);
                }
                case 99: {
                    Object var3_7 = null;
                    object = FieldDescriptor.parseDescriptor((String)((String)object2.getPrimitiveConstant((int)object.readUnsignedShort()).value)).type;
                    if (((VarType)object).arraydim <= 0) {
                        String string = null;
                        switch (((VarType)object).type) {
                            case 8: {
                                string = ((VarType)object).value;
                                break;
                            }
                            case 0: 
                            case 1: 
                            case 2: 
                            case 3: 
                            case 4: 
                            case 5: 
                            case 6: 
                            case 7: 
                            case 10: {
                                string = bx.e[((VarType)object).type];
                                break;
                            }
                            default: {
                                throw new RuntimeException("invalid class type!");
                            }
                        }
                        if (string != null) {
                            return new ConstExprent(VarType.VARTYPE_CLASS, string);
                        }
                    }
                    throw new RuntimeException("unknown annotation type!");
                }
                case 91: {
                    VarType varType;
                    n = ((DataInputStream)object).readUnsignedShort();
                    ArrayList<Exprent> arrayList = new ArrayList<Exprent>();
                    int n2 = 0;
                    while (n2 < n) {
                        arrayList.add(StructAnnotationAttribute.parseAnnotationElement((DataInputStream)object, (ConstantPool)object2));
                        ++n2;
                    }
                    if (arrayList.isEmpty()) {
                        varType = new VarType(8, 1, "java/lang/Object");
                    } else {
                        object = ((Exprent)arrayList.get(0)).getExprType();
                        varType = new VarType(((VarType)object).type, 1, ((VarType)object).value);
                    }
                    object = new NewExprent(varType, new ArrayList());
                    ((NewExprent)object).setDirectArrayInit();
                    ((NewExprent)object).setLstArrayElements(arrayList);
                    return object;
                }
                case 64: {
                    return StructAnnotationAttribute.parseAnnotation((DataInputStream)object, (ConstantPool)object2);
                }
            }
            object = ((ConstantPool)object2).getPrimitiveConstant(((DataInputStream)object).readUnsignedShort());
            switch (n) {
                case 66: {
                    return new ConstExprent(VarType.VARTYPE_BYTE, ((PrimitiveConstant)object).value);
                }
                case 67: {
                    return new ConstExprent(VarType.VARTYPE_CHAR, ((PrimitiveConstant)object).value);
                }
                case 68: {
                    return new ConstExprent(VarType.VARTYPE_DOUBLE, ((PrimitiveConstant)object).value);
                }
                case 70: {
                    return new ConstExprent(VarType.VARTYPE_FLOAT, ((PrimitiveConstant)object).value);
                }
                case 73: {
                    return new ConstExprent(VarType.VARTYPE_INT, ((PrimitiveConstant)object).value);
                }
                case 74: {
                    return new ConstExprent(VarType.VARTYPE_LONG, ((PrimitiveConstant)object).value);
                }
                case 83: {
                    return new ConstExprent(VarType.VARTYPE_SHORT, ((PrimitiveConstant)object).value);
                }
                case 90: {
                    return new ConstExprent(VarType.VARTYPE_BOOLEAN, ((PrimitiveConstant)object).value);
                }
                case 115: {
                    return new ConstExprent(VarType.VARTYPE_STRING, ((PrimitiveConstant)object).value);
                }
            }
            throw new RuntimeException("invalid element type!");
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
    }

    public final List getAnnotations() {
        return this.annotations;
    }
}

