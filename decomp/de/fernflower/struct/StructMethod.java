/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.struct;

import de.fernflower.bx;
import de.fernflower.code.ConstantsUtil;
import de.fernflower.code.ExceptionHandler;
import de.fernflower.code.ExceptionTable;
import de.fernflower.code.FullInstructionSequence;
import de.fernflower.code.Instruction;
import de.fernflower.code.InstructionSequence;
import de.fernflower.struct.StructClass;
import de.fernflower.struct.attr.StructGeneralAttribute;
import de.fernflower.struct.attr.StructLocalVariableTableAttribute;
import de.fernflower.struct.consts.ConstantPool;
import de.fernflower.util.DataInputFullStream;
import de.fernflower.util.VBStyleCollection;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public final class StructMethod
implements bx {
    private int name_index;
    private int descriptor_index;
    private static final int[] opr_iconst;
    private static final int[] opr_loadstore;
    private static final int[] opcs_load;
    private static final int[] opcs_store;
    private int accessFlags;
    private VBStyleCollection attributes;
    private int localVariables;
    private String name;
    private String descriptor;
    private InstructionSequence seq;
    private boolean containsCode = false;
    private boolean own;
    private StructClass classStruct;
    private boolean lazy;
    private boolean expanded;
    private int code_length = 0;
    private int code_fulllength = 0;

    static {
        int[] nArray = new int[7];
        nArray[0] = -1;
        nArray[2] = 1;
        nArray[3] = 2;
        nArray[4] = 3;
        nArray[5] = 4;
        nArray[6] = 5;
        opr_iconst = nArray;
        int[] nArray2 = new int[20];
        nArray2[1] = 1;
        nArray2[2] = 2;
        nArray2[3] = 3;
        nArray2[5] = 1;
        nArray2[6] = 2;
        nArray2[7] = 3;
        nArray2[9] = 1;
        nArray2[10] = 2;
        nArray2[11] = 3;
        nArray2[13] = 1;
        nArray2[14] = 2;
        nArray2[15] = 3;
        nArray2[17] = 1;
        nArray2[18] = 2;
        nArray2[19] = 3;
        opr_loadstore = nArray2;
        opcs_load = new int[]{21, 22, 23, 24, 25};
        opcs_store = new int[]{54, 55, 56, 57, 58};
    }

    public StructMethod(DataInputFullStream dataInputFullStream, boolean bl, StructClass structClass) {
        this(dataInputFullStream, true, bl, structClass);
    }

    private StructMethod(DataInputFullStream dataInputFullStream, boolean bl, boolean bl2, StructClass stringArray) {
        this.own = bl2;
        this.lazy = true;
        this.expanded = false;
        this.classStruct = stringArray;
        this.accessFlags = dataInputFullStream.readUnsignedShort();
        this.name_index = dataInputFullStream.readUnsignedShort();
        this.descriptor_index = dataInputFullStream.readUnsignedShort();
        ConstantPool constantPool = stringArray.getPool();
        int n = stringArray.this_class;
        stringArray = constantPool;
        Object object = this;
        stringArray = stringArray.getClassElement(3, n, ((StructMethod)object).name_index, ((StructMethod)object).descriptor_index);
        ((StructMethod)object).name = stringArray[0];
        ((StructMethod)object).descriptor = stringArray[1];
        object = new VBStyleCollection();
        int n2 = dataInputFullStream.readUnsignedShort();
        n = 0;
        while (n < n2) {
            int n3 = dataInputFullStream.readUnsignedShort();
            String string = null;
            String string2 = (String)constantPool.getPrimitiveConstant((int)n3).value;
            if ("Code".equals(string2)) {
                if (!this.own) {
                    dataInputFullStream.skip(8L);
                    dataInputFullStream.skip(dataInputFullStream.readInt());
                    dataInputFullStream.skip(8 * dataInputFullStream.readUnsignedShort());
                } else {
                    this.containsCode = true;
                    dataInputFullStream.skip(4L);
                    dataInputFullStream.readUnsignedShort();
                    this.localVariables = dataInputFullStream.readUnsignedShort();
                    this.code_length = dataInputFullStream.readInt();
                    dataInputFullStream.skip(this.code_length);
                    n3 = dataInputFullStream.readUnsignedShort();
                    this.code_fulllength = this.code_length + (n3 << 3) + 2;
                    dataInputFullStream.skip(n3 << 3);
                }
                n3 = dataInputFullStream.readUnsignedShort();
                int n4 = 0;
                while (n4 < n3) {
                    int n5 = dataInputFullStream.readUnsignedShort();
                    string = (String)constantPool.getPrimitiveConstant((int)n5).value;
                    StructMethod.readAttribute(dataInputFullStream, constantPool, (VBStyleCollection)object, n5, string);
                    ++n4;
                }
            } else {
                StructMethod.readAttribute(dataInputFullStream, constantPool, (VBStyleCollection)object, n3, string2);
            }
            ++n;
        }
        this.attributes = object;
    }

    private static void readAttribute(DataInputFullStream dataInputFullStream, ConstantPool constantPool, VBStyleCollection vBStyleCollection, int n, String object) {
        StructGeneralAttribute structGeneralAttribute = StructGeneralAttribute.getMatchingAttributeInstance(n, (String)object);
        if (structGeneralAttribute != null) {
            object = new byte[dataInputFullStream.readInt()];
            dataInputFullStream.readFull((byte[])object);
            structGeneralAttribute.setInfo((byte[])object);
            structGeneralAttribute.initContent(constantPool);
            object = structGeneralAttribute.getName();
            if ("LocalVariableTable".equals(object) && vBStyleCollection.containsKey(object)) {
                ((StructLocalVariableTableAttribute)vBStyleCollection.getWithKey(object)).addLocalVariableTable((StructLocalVariableTableAttribute)structGeneralAttribute);
                return;
            }
            vBStyleCollection.addWithKey(structGeneralAttribute, structGeneralAttribute.getName());
            return;
        }
        dataInputFullStream.skip(dataInputFullStream.readInt());
    }

    public final void expandData() {
        if (this.containsCode && this.lazy && !this.expanded) {
            byte[] byArray = this.classStruct.getLoader().loadBytecode(this, this.code_fulllength);
            this.seq = StructMethod.parseBytecode(new DataInputFullStream(new ByteArrayInputStream(byArray)), this.code_length, this.classStruct.getPool());
            this.expanded = true;
        }
    }

    public final void releaseResources() {
        if (this.containsCode && this.lazy && this.expanded) {
            this.seq = null;
            this.expanded = false;
        }
    }

    /*
     * Handled duff style switch with additional control
     * Enabled aggressive block sorting
     */
    private static InstructionSequence parseBytecode(DataInputFullStream dataInputFullStream, int n, ConstantPool constantPool) {
        int n2;
        int n3;
        int n4;
        VBStyleCollection vBStyleCollection = new VBStyleCollection();
        int n5 = 0;
        while (n5 < n) {
            int n6;
            n4 = n5;
            n3 = dataInputFullStream.readUnsignedByte();
            int n7 = 1;
            n2 = n3 == 196 ? 1 : 0;
            if (n2) {
                ++n5;
                n3 = dataInputFullStream.readUnsignedByte();
            }
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            if (n3 >= 2 && n3 <= 8) {
                arrayList.add(new Integer(opr_iconst[n3 - 2]));
                n3 = 16;
            } else if (n3 >= 26 && n3 <= 45) {
                arrayList.add(new Integer(opr_loadstore[n3 - 26]));
                n3 = opcs_load[(n3 - 26) / 4];
            } else if (n3 >= 59 && n3 <= 78) {
                arrayList.add(new Integer(opr_loadstore[n3 - 59]));
                n3 = opcs_store[(n3 - 59) / 4];
            } else {
                int n8 = 0;
                block15: do {
                    block0 : switch (n8 == 0 ? n3 : n8) {
                        case 16: {
                            arrayList.add(new Integer(dataInputFullStream.readByte()));
                            ++n5;
                            break;
                        }
                        case 18: 
                        case 188: {
                            arrayList.add(new Integer(dataInputFullStream.readUnsignedByte()));
                            ++n5;
                            break;
                        }
                        case 17: 
                        case 153: 
                        case 154: 
                        case 155: 
                        case 156: 
                        case 157: 
                        case 158: 
                        case 159: 
                        case 160: 
                        case 161: 
                        case 162: 
                        case 163: 
                        case 164: 
                        case 165: 
                        case 166: 
                        case 167: 
                        case 168: 
                        case 198: 
                        case 199: {
                            if (n3 != 17) {
                                n7 = 2;
                            }
                            arrayList.add(new Integer(dataInputFullStream.readShort()));
                            n5 += 2;
                            break;
                        }
                        case 19: 
                        case 20: 
                        case 178: 
                        case 179: 
                        case 180: 
                        case 181: 
                        case 182: 
                        case 183: 
                        case 184: 
                        case 187: 
                        case 189: 
                        case 192: 
                        case 193: {
                            arrayList.add(new Integer(dataInputFullStream.readUnsignedShort()));
                            n5 += 2;
                            if (n3 >= 178 && n3 <= 181) {
                                n7 = 5;
                                break;
                            }
                            if (n3 < 182 || n3 > 184) break block15;
                            n7 = 4;
                            break;
                        }
                        case 21: 
                        case 22: 
                        case 23: 
                        case 24: 
                        case 25: 
                        case 54: 
                        case 55: 
                        case 56: 
                        case 57: 
                        case 58: 
                        case 169: {
                            if (n2) {
                                arrayList.add(new Integer(dataInputFullStream.readUnsignedShort()));
                                n5 += 2;
                            } else {
                                arrayList.add(new Integer(dataInputFullStream.readUnsignedByte()));
                                ++n5;
                            }
                            if (n3 != 169) break block15;
                            n8 = 172;
                            continue block15;
                        }
                        case 132: {
                            if (n2) {
                                arrayList.add(new Integer(dataInputFullStream.readUnsignedShort()));
                                arrayList.add(new Integer(dataInputFullStream.readShort()));
                                n5 += 4;
                                break;
                            }
                            arrayList.add(new Integer(dataInputFullStream.readUnsignedByte()));
                            arrayList.add(new Integer(dataInputFullStream.readByte()));
                            n5 += 2;
                            break;
                        }
                        case 200: 
                        case 201: {
                            n3 = n3 == 201 ? 168 : 167;
                            arrayList.add(new Integer(dataInputFullStream.readInt()));
                            n7 = 2;
                            n5 += 4;
                            break;
                        }
                        case 185: {
                            arrayList.add(new Integer(dataInputFullStream.readUnsignedShort()));
                            arrayList.add(new Integer(dataInputFullStream.readUnsignedByte()));
                            dataInputFullStream.skip(1L);
                            n7 = 4;
                            n5 += 4;
                            break;
                        }
                        case 197: {
                            arrayList.add(new Integer(dataInputFullStream.readUnsignedShort()));
                            arrayList.add(new Integer(dataInputFullStream.readUnsignedByte()));
                            n5 += 3;
                            break;
                        }
                        case 170: {
                            dataInputFullStream.skip((4 - (n5 + 1) % 4) % 4);
                            n5 += (4 - (n5 + 1) % 4) % 4;
                            arrayList.add(new Integer(dataInputFullStream.readInt()));
                            n5 += 4;
                            int n9 = dataInputFullStream.readInt();
                            arrayList.add(new Integer(n9));
                            n5 += 4;
                            n6 = dataInputFullStream.readInt();
                            arrayList.add(new Integer(n6));
                            n5 += 4;
                            n7 = 0;
                            while (true) {
                                if (n7 >= n6 - n9 + 1) {
                                    n7 = 3;
                                    break block0;
                                }
                                arrayList.add(new Integer(dataInputFullStream.readInt()));
                                n5 += 4;
                                ++n7;
                            }
                        }
                        case 171: {
                            dataInputFullStream.skip((4 - (n5 + 1) % 4) % 4);
                            n5 += (4 - (n5 + 1) % 4) % 4;
                            arrayList.add(new Integer(dataInputFullStream.readInt()));
                            n5 += 4;
                            n7 = dataInputFullStream.readInt();
                            arrayList.add(new Integer(n7));
                            n5 += 4;
                            int n10 = 0;
                            while (true) {
                                if (n10 >= n7) {
                                    n7 = 3;
                                    break block0;
                                }
                                arrayList.add(new Integer(dataInputFullStream.readInt()));
                                n5 += 4;
                                arrayList.add(new Integer(dataInputFullStream.readInt()));
                                n5 += 4;
                                ++n10;
                            }
                        }
                        case 172: 
                        case 173: 
                        case 174: 
                        case 175: 
                        case 176: 
                        case 177: 
                        case 191: {
                            n7 = 6;
                            break;
                        }
                    }
                    break;
                } while (true);
            }
            int[] nArray = new int[arrayList.size()];
            n6 = 0;
            while (n6 < arrayList.size()) {
                nArray[n6] = (Integer)arrayList.get(n6);
                ++n6;
            }
            Instruction instruction = ConstantsUtil.getInstructionInstance(n3, n2 != 0, n7, nArray);
            vBStyleCollection.addWithKey(instruction, new Integer(n4));
            ++n5;
        }
        ArrayList<ExceptionHandler> arrayList = new ArrayList<ExceptionHandler>();
        n4 = dataInputFullStream.readUnsignedShort();
        n3 = 0;
        while (n3 < n4) {
            ExceptionHandler exceptionHandler = new ExceptionHandler();
            new ExceptionHandler().from = dataInputFullStream.readUnsignedShort();
            exceptionHandler.to = dataInputFullStream.readUnsignedShort();
            exceptionHandler.handler = dataInputFullStream.readUnsignedShort();
            n2 = dataInputFullStream.readUnsignedShort();
            if (n2) {
                exceptionHandler.exceptionClass = (String)constantPool.getPrimitiveConstant((int)n2).value;
            }
            arrayList.add(exceptionHandler);
            ++n3;
        }
        FullInstructionSequence fullInstructionSequence = new FullInstructionSequence(vBStyleCollection, new ExceptionTable(arrayList));
        int n11 = fullInstructionSequence.length() - 1;
        fullInstructionSequence.setPointer(n11);
        while (n11 >= 0) {
            Instruction instruction = fullInstructionSequence.getInstr(n11--);
            if (instruction.group != 1) {
                instruction.initInstruction(fullInstructionSequence);
            }
            fullInstructionSequence.addToPointer();
        }
        return fullInstructionSequence;
    }

    public final InstructionSequence getInstructionSequence() {
        return this.seq;
    }

    public final String getDescriptor() {
        return this.descriptor;
    }

    public final String getName() {
        return this.name;
    }

    public final int getAccessFlags() {
        return this.accessFlags;
    }

    public final int getLocalVariables() {
        return this.localVariables;
    }

    public final VBStyleCollection getAttributes() {
        return this.attributes;
    }

    public final StructClass getClassStruct() {
        return this.classStruct;
    }
}

