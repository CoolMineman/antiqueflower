/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct.consts;

import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.renamer.PoolInterceptor;
import de.fernflower.struct.consts.LinkConstant;
import de.fernflower.struct.consts.PooledConstant;
import de.fernflower.struct.consts.PrimitiveConstant;
import de.fernflower.struct.gen.FieldDescriptor;
import de.fernflower.struct.gen.MethodDescriptor;
import de.fernflower.struct.gen.VarType;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

public final class ConstantPool {
    private List pool = new ArrayList();
    private PoolInterceptor interceptor;

    public ConstantPool(DataInputStream dataInputStream) {
        int n = dataInputStream.readUnsignedShort();
        int[] nArray = new int[n];
        this.pool.add(null);
        int n2 = 1;
        while (n2 < n) {
            byte by = (byte)dataInputStream.readUnsignedByte();
            switch (by) {
                case 1: {
                    this.pool.add(new PrimitiveConstant(1, dataInputStream.readUTF()));
                    break;
                }
                case 3: {
                    this.pool.add(new PrimitiveConstant(3, new Integer(dataInputStream.readInt())));
                    break;
                }
                case 4: {
                    this.pool.add(new PrimitiveConstant(4, new Float(dataInputStream.readFloat())));
                    break;
                }
                case 5: {
                    this.pool.add(new PrimitiveConstant(5, new Long(dataInputStream.readLong())));
                    this.pool.add(null);
                    ++n2;
                    break;
                }
                case 6: {
                    this.pool.add(new PrimitiveConstant(6, new Double(dataInputStream.readDouble())));
                    this.pool.add(null);
                    ++n2;
                    break;
                }
                case 7: 
                case 8: {
                    this.pool.add(new PrimitiveConstant((int)by, dataInputStream.readUnsignedShort()));
                    nArray[n2] = 1;
                    break;
                }
                case 9: 
                case 10: 
                case 11: 
                case 12: {
                    this.pool.add(new LinkConstant(by, dataInputStream.readUnsignedShort(), dataInputStream.readUnsignedShort()));
                    nArray[n2] = by == 12 ? 1 : 2;
                }
            }
            ++n2;
        }
        n2 = 1;
        while (n2 < n) {
            if (nArray[n2] == 1) {
                ((PooledConstant)this.pool.get(n2)).resolveConstant(this);
            }
            ++n2;
        }
        n2 = 1;
        while (n2 < n) {
            if (nArray[n2] == 2) {
                ((PooledConstant)this.pool.get(n2)).resolveConstant(this);
            }
            ++n2;
        }
        this.interceptor = DecompilerContext.getPoolInterceptor();
    }

    public final String[] getClassElement(int n, int n2, int n3, int n4) {
        String string = (String)((PrimitiveConstant)this.getConstant((int)n2)).value;
        String string2 = (String)((PrimitiveConstant)this.getConstant((int)n3)).value;
        Object object = (String)((PrimitiveConstant)this.getConstant((int)n4)).value;
        if (((ConstantPool)this).interceptor != null) {
            if ((string = ((ConstantPool)this).interceptor.a(n, string, string2, (String)object)) != null) {
                string2 = string.split(" ")[1];
            }
            if ((this = ((ConstantPool)this).buildNewDescriptor(n == 2 ? 9 : 10, (String)object)) != null) {
                object = this;
            }
        }
        return new String[]{string2, object};
    }

    private PooledConstant getConstant(int n) {
        return (PooledConstant)this.pool.get(n);
    }

    public final PrimitiveConstant getPrimitiveConstant(int n) {
        PrimitiveConstant primitiveConstant = (PrimitiveConstant)((ConstantPool)this).getConstant(n);
        if (primitiveConstant != null && ((ConstantPool)this).interceptor != null && primitiveConstant.type == 7 && (this = ((ConstantPool)this).buildNewClassname((String)primitiveConstant.value)) != null) {
            primitiveConstant = new PrimitiveConstant(7, this);
        }
        return primitiveConstant;
    }

    public final LinkConstant getLinkConstant(int n) {
        LinkConstant linkConstant = (LinkConstant)((ConstantPool)((Object)string3)).getConstant(n);
        if (linkConstant != null && ((ConstantPool)string3).interceptor != null && (linkConstant.type == 9 || linkConstant.type == 10 || linkConstant.type == 11)) {
            String string = ((ConstantPool)((Object)string3)).buildNewClassname(linkConstant.classname);
            String string2 = ((ConstantPool)string3).interceptor.a(linkConstant.type == 9 ? 2 : 3, linkConstant.classname, linkConstant.elementname, linkConstant.descriptor);
            String string3 = ((ConstantPool)((Object)string3)).buildNewDescriptor(linkConstant.type, linkConstant.descriptor);
            if (string != null || string2 != null || string3 != null) {
                linkConstant = new LinkConstant(linkConstant.type, string == null ? linkConstant.classname : string, string2 == null ? linkConstant.elementname : string2.split(" ")[1], (String)(string3 == null ? linkConstant.descriptor : string3));
            }
        }
        return linkConstant;
    }

    private String buildNewClassname(String object) {
        object = new VarType((String)object, true);
        String string = ((ConstantPool)string).interceptor.a(1, ((VarType)object).value, null, null);
        if (string != null) {
            StringBuilder stringBuilder = new StringBuilder();
            if (((VarType)object).arraydim > 0) {
                int n = 0;
                while (n < ((VarType)object).arraydim) {
                    stringBuilder.append("[");
                    ++n;
                }
                stringBuilder.append("L" + (String)string + ";");
            } else {
                stringBuilder.append(string);
            }
            return stringBuilder.toString();
        }
        return null;
    }

    private String buildNewDescriptor(int n, String object) {
        boolean bl = false;
        if (n == 9) {
            String string;
            FieldDescriptor fieldDescriptor = FieldDescriptor.parseDescriptor((String)object);
            object = fieldDescriptor.type;
            if (((VarType)object).type == 8 && (string = this.buildNewClassname(((VarType)object).value)) != null) {
                ((VarType)object).value = string;
                bl = true;
            }
            if (bl) {
                return fieldDescriptor.getDescriptor();
            }
        } else {
            MethodDescriptor methodDescriptor = MethodDescriptor.parseDescriptor((String)object);
            VarType[] varTypeArray = methodDescriptor.params;
            int n2 = methodDescriptor.params.length;
            int n3 = 0;
            while (n3 < n2) {
                String string;
                object = varTypeArray[n3];
                if (((VarType)object).type == 8 && (string = this.buildNewClassname(((VarType)object).value)) != null) {
                    ((VarType)object).value = string;
                    bl = true;
                }
                ++n3;
            }
            if (methodDescriptor.ret.type == 8 && (object = this.buildNewClassname(methodDescriptor.ret.value)) != null) {
                methodDescriptor.ret.value = object;
                bl = true;
            }
            if (bl) {
                return methodDescriptor.getDescriptor();
            }
        }
        return null;
    }
}

