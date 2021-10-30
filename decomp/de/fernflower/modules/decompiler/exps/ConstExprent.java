/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.exps;

import de.fernflower.main.DecompilerContext;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.exps.Exprent;
import de.fernflower.modules.decompiler.exps.FieldExprent;
import de.fernflower.struct.gen.FieldDescriptor;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.InterpreterUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ConstExprent
extends Exprent {
    private static final HashMap escapes = new HashMap<Integer, String>();
    private VarType consttype;
    private Object value;
    private boolean boolPermitted;

    static {
        escapes.put(new Integer(8), "\\b");
        escapes.put(new Integer(9), "\\t");
        escapes.put(new Integer(10), "\\n");
        escapes.put(new Integer(12), "\\f");
        escapes.put(new Integer(13), "\\r");
        escapes.put(new Integer(34), "\\\"");
        escapes.put(new Integer(39), "\\'");
        escapes.put(new Integer(92), "\\\\");
    }

    public ConstExprent(int n, boolean bl) {
        this.type = 3;
        this.boolPermitted = bl;
        if (bl) {
            this.consttype = VarType.VARTYPE_BOOLEAN;
            if (n != 0 && n != 1) {
                this.consttype = this.consttype.copy();
                this.consttype.convinfo |= 1;
            }
        } else {
            this.consttype = n >= 0 && n <= 127 ? VarType.VARTYPE_BYTECHAR : (-128 <= n && n <= 127 ? VarType.VARTYPE_BYTE : (n >= 0 && n <= Short.MAX_VALUE ? VarType.VARTYPE_SHORTCHAR : (Short.MIN_VALUE <= n && n <= Short.MAX_VALUE ? VarType.VARTYPE_SHORT : (n >= 0 && n <= 65535 ? VarType.VARTYPE_CHAR : VarType.VARTYPE_INT))));
        }
        this.value = new Integer(n);
    }

    public ConstExprent(VarType varType, Object object) {
        this.type = 3;
        this.consttype = varType;
        this.value = object;
    }

    public final Exprent copy() {
        return new ConstExprent(this.consttype, this.value);
    }

    public final VarType getExprType() {
        return this.consttype;
    }

    public final int getExprentUse() {
        return 3;
    }

    public final List getAllExprents() {
        return new ArrayList();
    }

    public final String toJava(int n) {
        if (object.consttype.type != 13 && ((ConstExprent)object).value == null) {
            return ExprProcessor.getCastTypeName(((ConstExprent)object).consttype);
        }
        switch (object.consttype.type) {
            case 7: {
                return new Boolean((Integer)((ConstExprent)object).value != 0).toString();
            }
            case 1: {
                Object object = (Integer)((ConstExprent)object).value;
                String string = (String)escapes.get(object);
                if (string == null) {
                    string = (Integer)object >= 32 && (Integer)object < 127 ? String.valueOf((char)((Integer)object).intValue()) : InterpreterUtil.charToUnicodeLiteral((Integer)object);
                }
                return "'" + string + "'";
            }
            case 0: 
            case 4: 
            case 6: 
            case 15: 
            case 16: {
                Object object;
                n = (Integer)((ConstExprent)object).value;
                if (n == Integer.MAX_VALUE) {
                    object = "MAX_VALUE";
                } else if (n == Integer.MIN_VALUE) {
                    object = "MIN_VALUE";
                } else {
                    return ((ConstExprent)object).value.toString();
                }
                return new FieldExprent((String)object, "java/lang/Integer", true, null, FieldDescriptor.INTEGER_DESCRIPTOR).toJava(0);
            }
            case 5: {
                Object object;
                long l = (Long)((ConstExprent)object).value;
                if (l == Long.MAX_VALUE) {
                    object = "MAX_VALUE";
                } else if (l == Long.MIN_VALUE) {
                    object = "MIN_VALUE";
                } else {
                    return String.valueOf(((ConstExprent)object).value.toString()) + "L";
                }
                return new FieldExprent((String)object, "java/lang/Long", true, null, FieldDescriptor.LONG_DESCRIPTOR).toJava(0);
            }
            case 2: {
                Object object;
                double d = (Double)((ConstExprent)object).value;
                if (Double.isNaN(d)) {
                    object = "NaN";
                } else if (d == Double.POSITIVE_INFINITY) {
                    object = "POSITIVE_INFINITY";
                } else if (d == Double.NEGATIVE_INFINITY) {
                    object = "NEGATIVE_INFINITY";
                } else if (d == Double.MAX_VALUE) {
                    object = "MAX_VALUE";
                } else if (d == Double.MIN_VALUE) {
                    object = "MIN_VALUE";
                } else {
                    return String.valueOf(((ConstExprent)object).value.toString()) + "D";
                }
                return new FieldExprent((String)object, "java/lang/Double", true, null, FieldDescriptor.DOUBLE_DESCRIPTOR).toJava(0);
            }
            case 3: {
                Object object;
                float f = ((Float)((ConstExprent)object).value).floatValue();
                if (Float.isNaN(f)) {
                    object = "NaN";
                } else if (f == Float.POSITIVE_INFINITY) {
                    object = "POSITIVE_INFINITY";
                } else if (f == Float.NEGATIVE_INFINITY) {
                    object = "NEGATIVE_INFINITY";
                } else if (f == Float.MAX_VALUE) {
                    object = "MAX_VALUE";
                } else if (f == Float.MIN_VALUE) {
                    object = "MIN_VALUE";
                } else {
                    return String.valueOf(((ConstExprent)object).value.toString()) + "F";
                }
                return new FieldExprent((String)object, "java/lang/Float", true, null, FieldDescriptor.FLOAT_DESCRIPTOR).toJava(0);
            }
            case 13: {
                return "null";
            }
            case 8: {
                if (((ConstExprent)object).consttype.equals(VarType.VARTYPE_STRING)) {
                    return "\"" + ConstExprent.convertStringToJava(((ConstExprent)object).value.toString()) + "\"";
                }
                if (!((ConstExprent)object).consttype.equals(VarType.VARTYPE_CLASS)) break;
                Object object = ((ConstExprent)object).value.toString();
                object = ((String)object).startsWith("[") ? new VarType((String)object, false) : new VarType((String)object, true);
                return String.valueOf(ExprProcessor.getCastTypeName((VarType)object)) + ".class";
            }
        }
        throw new RuntimeException("invalid constant type");
    }

    private static String convertStringToJava(String object) {
        object = ((String)object).toCharArray();
        StringBuilder stringBuilder = new StringBuilder(((Object)object).length);
        Object object2 = object;
        int n = ((Object)object2).length;
        int n2 = 0;
        while (n2 < n) {
            Object object3 = object2[n2];
            switch (object3) {
                case 92: {
                    stringBuilder.append("\\\\");
                    break;
                }
                case 8: {
                    stringBuilder.append("\\b");
                    break;
                }
                case 9: {
                    stringBuilder.append("\\t");
                    break;
                }
                case 10: {
                    stringBuilder.append("\\n");
                    break;
                }
                case 12: {
                    stringBuilder.append("\\f");
                    break;
                }
                case 13: {
                    stringBuilder.append("\\r");
                    break;
                }
                case 34: {
                    stringBuilder.append("\\\"");
                    break;
                }
                case 39: {
                    stringBuilder.append("\\'");
                    break;
                }
                default: {
                    if (!DecompilerContext.getOption("asc") || object3 >= 32 && object3 < 127) {
                        stringBuilder.append((char)object3);
                        break;
                    }
                    stringBuilder.append(InterpreterUtil.charToUnicodeLiteral((int)object3));
                }
            }
            ++n2;
        }
        return stringBuilder.toString();
    }

    public final boolean equals(Object object) {
        if (object != null && object instanceof ConstExprent) {
            object = (ConstExprent)object;
            return InterpreterUtil.equalObjects(this.consttype, ((ConstExprent)object).consttype) && InterpreterUtil.equalObjects(this.value, ((ConstExprent)object).value);
        }
        return false;
    }

    public final boolean hasBooleanValue() {
        switch (n.consttype.type) {
            case 0: 
            case 1: 
            case 4: 
            case 6: 
            case 7: 
            case 15: 
            case 16: {
                Integer n = (Integer)((ConstExprent)n).value;
                return n == 0 || DecompilerContext.getOption("bto") && n == 1;
            }
        }
        return false;
    }

    public final boolean hasValueOne() {
        switch (this.consttype.type) {
            case 0: 
            case 1: 
            case 4: 
            case 6: 
            case 7: 
            case 15: 
            case 16: {
                return (Integer)this.value == 1;
            }
            case 5: {
                return ((Long)this.value).intValue() == 1;
            }
            case 2: {
                return ((Double)this.value).intValue() == 1;
            }
            case 3: {
                return ((Float)this.value).intValue() == 1;
            }
        }
        return false;
    }

    public static ConstExprent getZeroConstant(int n) {
        switch (n) {
            case 4: {
                return new ConstExprent(VarType.VARTYPE_INT, new Integer(0));
            }
            case 5: {
                return new ConstExprent(VarType.VARTYPE_LONG, new Long(0L));
            }
            case 2: {
                return new ConstExprent(VarType.VARTYPE_DOUBLE, new Double(0.0));
            }
            case 3: {
                return new ConstExprent(VarType.VARTYPE_FLOAT, new Float(0.0f));
            }
        }
        throw new RuntimeException("Invalid argument!");
    }

    public final VarType getConsttype() {
        return this.consttype;
    }

    public final void setConsttype(VarType varType) {
        this.consttype = varType;
    }

    public final Object getValue() {
        return this.value;
    }

    public final int getIntValue() {
        return (Integer)this.value;
    }

    public final boolean isBoolPermitted() {
        return this.boolPermitted;
    }
}

