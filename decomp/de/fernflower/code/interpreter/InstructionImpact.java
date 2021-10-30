/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.code.interpreter;

import de.fernflower.code.Instruction;
import de.fernflower.struct.consts.ConstantPool;
import de.fernflower.struct.consts.LinkConstant;
import de.fernflower.struct.consts.PrimitiveConstant;
import de.fernflower.struct.gen.DataPoint;
import de.fernflower.struct.gen.MethodDescriptor;
import de.fernflower.struct.gen.VarType;
import de.fernflower.util.ListStack;

public final class InstructionImpact {
    private static final int[][][] stack_impact;
    private static final int[] arr_type;

    static {
        int[][][] nArrayArray = new int[202][][];
        nArrayArray[0] = new int[2][];
        int[][] nArrayArray2 = new int[2][];
        nArrayArray2[1] = new int[]{5};
        nArrayArray[9] = nArrayArray2;
        int[][] nArrayArray3 = new int[2][];
        nArrayArray3[1] = new int[]{5};
        nArrayArray[10] = nArrayArray3;
        int[][] nArrayArray4 = new int[2][];
        nArrayArray4[1] = new int[]{3};
        nArrayArray[11] = nArrayArray4;
        int[][] nArrayArray5 = new int[2][];
        nArrayArray5[1] = new int[]{3};
        nArrayArray[12] = nArrayArray5;
        int[][] nArrayArray6 = new int[2][];
        nArrayArray6[1] = new int[]{3};
        nArrayArray[13] = nArrayArray6;
        int[][] nArrayArray7 = new int[2][];
        nArrayArray7[1] = new int[]{2};
        nArrayArray[14] = nArrayArray7;
        int[][] nArrayArray8 = new int[2][];
        nArrayArray8[1] = new int[]{2};
        nArrayArray[15] = nArrayArray8;
        int[][] nArrayArray9 = new int[2][];
        nArrayArray9[1] = new int[]{4};
        nArrayArray[16] = nArrayArray9;
        int[][] nArrayArray10 = new int[2][];
        nArrayArray10[1] = new int[]{4};
        nArrayArray[17] = nArrayArray10;
        int[][] nArrayArray11 = new int[2][];
        nArrayArray11[1] = new int[]{4};
        nArrayArray[21] = nArrayArray11;
        int[][] nArrayArray12 = new int[2][];
        nArrayArray12[1] = new int[]{5};
        nArrayArray[22] = nArrayArray12;
        int[][] nArrayArray13 = new int[2][];
        nArrayArray13[1] = new int[]{3};
        nArrayArray[23] = nArrayArray13;
        int[][] nArrayArray14 = new int[2][];
        nArrayArray14[1] = new int[]{2};
        nArrayArray[24] = nArrayArray14;
        nArrayArray[46] = new int[][]{{8, 4}, {4}};
        nArrayArray[47] = new int[][]{{8, 4}, {5}};
        nArrayArray[48] = new int[][]{{8, 4}, {3}};
        nArrayArray[49] = new int[][]{{8, 4}, {2}};
        nArrayArray[51] = new int[][]{{8, 4}, {4}};
        nArrayArray[52] = new int[][]{{8, 4}, {4}};
        nArrayArray[53] = new int[][]{{8, 4}, {4}};
        int[][] nArrayArray15 = new int[2][];
        nArrayArray15[0] = new int[]{4};
        nArrayArray[54] = nArrayArray15;
        int[][] nArrayArray16 = new int[2][];
        nArrayArray16[0] = new int[]{5};
        nArrayArray[55] = nArrayArray16;
        int[][] nArrayArray17 = new int[2][];
        nArrayArray17[0] = new int[]{3};
        nArrayArray[56] = nArrayArray17;
        int[][] nArrayArray18 = new int[2][];
        nArrayArray18[0] = new int[]{2};
        nArrayArray[57] = nArrayArray18;
        int[][] nArrayArray19 = new int[2][];
        nArrayArray19[0] = new int[]{8, 4, 4};
        nArrayArray[79] = nArrayArray19;
        int[][] nArrayArray20 = new int[2][];
        nArrayArray20[0] = new int[]{8, 4, 5};
        nArrayArray[80] = nArrayArray20;
        int[][] nArrayArray21 = new int[2][];
        nArrayArray21[0] = new int[]{8, 4, 3};
        nArrayArray[81] = nArrayArray21;
        int[][] nArrayArray22 = new int[2][];
        nArrayArray22[0] = new int[]{8, 4, 2};
        nArrayArray[82] = nArrayArray22;
        int[][] nArrayArray23 = new int[2][];
        nArrayArray23[0] = new int[]{8, 4, 8};
        nArrayArray[83] = nArrayArray23;
        int[][] nArrayArray24 = new int[2][];
        nArrayArray24[0] = new int[]{8, 4, 4};
        nArrayArray[84] = nArrayArray24;
        int[][] nArrayArray25 = new int[2][];
        nArrayArray25[0] = new int[]{8, 4, 4};
        nArrayArray[85] = nArrayArray25;
        int[][] nArrayArray26 = new int[2][];
        nArrayArray26[0] = new int[]{8, 4, 4};
        nArrayArray[86] = nArrayArray26;
        int[][] nArrayArray27 = new int[2][];
        nArrayArray27[0] = new int[]{11};
        nArrayArray[87] = nArrayArray27;
        int[][] nArrayArray28 = new int[2][];
        nArrayArray28[0] = new int[]{11, 11};
        nArrayArray[88] = nArrayArray28;
        nArrayArray[96] = new int[][]{{4, 4}, {4}};
        nArrayArray[97] = new int[][]{{5, 5}, {5}};
        nArrayArray[98] = new int[][]{{3, 3}, {3}};
        nArrayArray[99] = new int[][]{{2, 2}, {2}};
        nArrayArray[100] = new int[][]{{4, 4}, {4}};
        nArrayArray[101] = new int[][]{{5, 5}, {5}};
        nArrayArray[102] = new int[][]{{3, 3}, {3}};
        nArrayArray[103] = new int[][]{{2, 2}, {2}};
        nArrayArray[104] = new int[][]{{4, 4}, {4}};
        nArrayArray[105] = new int[][]{{5, 5}, {5}};
        nArrayArray[106] = new int[][]{{3, 3}, {3}};
        nArrayArray[107] = new int[][]{{2, 2}, {2}};
        nArrayArray[108] = new int[][]{{4, 4}, {4}};
        nArrayArray[109] = new int[][]{{5, 5}, {5}};
        nArrayArray[110] = new int[][]{{3, 3}, {3}};
        nArrayArray[111] = new int[][]{{2, 2}, {2}};
        nArrayArray[112] = new int[][]{{4, 4}, {4}};
        nArrayArray[113] = new int[][]{{5, 5}, {5}};
        nArrayArray[114] = new int[][]{{3, 3}, {3}};
        nArrayArray[115] = new int[][]{{2, 2}, {2}};
        nArrayArray[116] = new int[][]{{4}, {4}};
        nArrayArray[117] = new int[][]{{5}, {5}};
        nArrayArray[118] = new int[][]{{3}, {3}};
        nArrayArray[119] = new int[][]{{2}, {2}};
        nArrayArray[120] = new int[][]{{4, 4}, {4}};
        nArrayArray[121] = new int[][]{{5, 4}, {5}};
        nArrayArray[122] = new int[][]{{4, 4}, {4}};
        nArrayArray[123] = new int[][]{{5, 4}, {5}};
        nArrayArray[124] = new int[][]{{4, 4}, {4}};
        nArrayArray[125] = new int[][]{{5, 4}, {5}};
        nArrayArray[126] = new int[][]{{4, 4}, {4}};
        nArrayArray[127] = new int[][]{{5, 5}, {5}};
        nArrayArray[128] = new int[][]{{4, 4}, {4}};
        nArrayArray[129] = new int[][]{{5, 5}, {5}};
        nArrayArray[130] = new int[][]{{4, 4}, {4}};
        nArrayArray[131] = new int[][]{{5, 5}, {5}};
        nArrayArray[132] = new int[2][];
        nArrayArray[133] = new int[][]{{4}, {5}};
        nArrayArray[134] = new int[][]{{4}, {3}};
        nArrayArray[135] = new int[][]{{4}, {2}};
        nArrayArray[136] = new int[][]{{5}, {4}};
        nArrayArray[137] = new int[][]{{5}, {3}};
        nArrayArray[138] = new int[][]{{5}, {2}};
        nArrayArray[139] = new int[][]{{3}, {4}};
        nArrayArray[140] = new int[][]{{3}, {5}};
        nArrayArray[141] = new int[][]{{3}, {2}};
        nArrayArray[142] = new int[][]{{2}, {4}};
        nArrayArray[143] = new int[][]{{2}, {5}};
        nArrayArray[144] = new int[][]{{2}, {3}};
        nArrayArray[145] = new int[][]{{4}, {4}};
        nArrayArray[146] = new int[][]{{4}, {4}};
        nArrayArray[147] = new int[][]{{4}, {4}};
        nArrayArray[148] = new int[][]{{5, 5}, {4}};
        nArrayArray[149] = new int[][]{{3, 3}, {4}};
        nArrayArray[150] = new int[][]{{3, 3}, {4}};
        nArrayArray[151] = new int[][]{{2, 2}, {4}};
        nArrayArray[152] = new int[][]{{2, 2}, {4}};
        int[][] nArrayArray29 = new int[2][];
        nArrayArray29[0] = new int[]{4};
        nArrayArray[153] = nArrayArray29;
        int[][] nArrayArray30 = new int[2][];
        nArrayArray30[0] = new int[]{4};
        nArrayArray[154] = nArrayArray30;
        int[][] nArrayArray31 = new int[2][];
        nArrayArray31[0] = new int[]{4};
        nArrayArray[155] = nArrayArray31;
        int[][] nArrayArray32 = new int[2][];
        nArrayArray32[0] = new int[]{4};
        nArrayArray[156] = nArrayArray32;
        int[][] nArrayArray33 = new int[2][];
        nArrayArray33[0] = new int[]{4};
        nArrayArray[157] = nArrayArray33;
        int[][] nArrayArray34 = new int[2][];
        nArrayArray34[0] = new int[]{4};
        nArrayArray[158] = nArrayArray34;
        int[][] nArrayArray35 = new int[2][];
        nArrayArray35[0] = new int[]{4, 4};
        nArrayArray[159] = nArrayArray35;
        int[][] nArrayArray36 = new int[2][];
        nArrayArray36[0] = new int[]{4, 4};
        nArrayArray[160] = nArrayArray36;
        int[][] nArrayArray37 = new int[2][];
        nArrayArray37[0] = new int[]{4, 4};
        nArrayArray[161] = nArrayArray37;
        int[][] nArrayArray38 = new int[2][];
        nArrayArray38[0] = new int[]{4, 4};
        nArrayArray[162] = nArrayArray38;
        int[][] nArrayArray39 = new int[2][];
        nArrayArray39[0] = new int[]{4, 4};
        nArrayArray[163] = nArrayArray39;
        int[][] nArrayArray40 = new int[2][];
        nArrayArray40[0] = new int[]{4, 4};
        nArrayArray[164] = nArrayArray40;
        int[][] nArrayArray41 = new int[2][];
        nArrayArray41[0] = new int[]{8, 8};
        nArrayArray[165] = nArrayArray41;
        int[][] nArrayArray42 = new int[2][];
        nArrayArray42[0] = new int[]{8, 8};
        nArrayArray[166] = nArrayArray42;
        nArrayArray[167] = new int[2][];
        int[][] nArrayArray43 = new int[2][];
        nArrayArray43[1] = new int[]{9};
        nArrayArray[168] = nArrayArray43;
        nArrayArray[169] = new int[2][];
        int[][] nArrayArray44 = new int[2][];
        nArrayArray44[0] = new int[]{4};
        nArrayArray[170] = nArrayArray44;
        int[][] nArrayArray45 = new int[2][];
        nArrayArray45[0] = new int[]{4};
        nArrayArray[171] = nArrayArray45;
        int[][] nArrayArray46 = new int[2][];
        nArrayArray46[0] = new int[]{4};
        nArrayArray[172] = nArrayArray46;
        int[][] nArrayArray47 = new int[2][];
        nArrayArray47[0] = new int[]{5};
        nArrayArray[173] = nArrayArray47;
        int[][] nArrayArray48 = new int[2][];
        nArrayArray48[0] = new int[]{3};
        nArrayArray[174] = nArrayArray48;
        int[][] nArrayArray49 = new int[2][];
        nArrayArray49[0] = new int[]{2};
        nArrayArray[175] = nArrayArray49;
        int[][] nArrayArray50 = new int[2][];
        nArrayArray50[0] = new int[]{8};
        nArrayArray[176] = nArrayArray50;
        nArrayArray[177] = new int[2][];
        nArrayArray[190] = new int[][]{{8}, {4}};
        int[][] nArrayArray51 = new int[2][];
        nArrayArray51[0] = new int[]{8};
        nArrayArray[194] = nArrayArray51;
        int[][] nArrayArray52 = new int[2][];
        nArrayArray52[0] = new int[]{8};
        nArrayArray[195] = nArrayArray52;
        int[][] nArrayArray53 = new int[2][];
        nArrayArray53[0] = new int[]{8};
        nArrayArray[198] = nArrayArray53;
        int[][] nArrayArray54 = new int[2][];
        nArrayArray54[0] = new int[]{8};
        nArrayArray[199] = nArrayArray54;
        nArrayArray[200] = new int[2][];
        int[][] nArrayArray55 = new int[2][];
        nArrayArray55[1] = new int[]{9};
        nArrayArray[201] = nArrayArray55;
        stack_impact = nArrayArray;
        int[] nArray = new int[8];
        nArray[0] = 7;
        nArray[1] = 1;
        nArray[2] = 3;
        nArray[3] = 2;
        nArray[5] = 6;
        nArray[6] = 4;
        nArray[7] = 5;
        arr_type = nArray;
    }

    public static void processSpecialInstructions(DataPoint object, Instruction object2, ConstantPool constantPool) {
        ListStack listStack = ((DataPoint)object).getStack();
        int[][] nArray = stack_impact[((Instruction)object2).opcode];
        if (nArray != null) {
            object = nArray[0];
            object2 = nArray[1];
            if (object != null) {
                int n = 0;
                int n2 = 0;
                while (n2 < ((Object)object).length) {
                    Object object3 = object[n2];
                    ++n;
                    if (object3 == 5 || object3 == 2) {
                        ++n;
                    }
                    ++n2;
                }
                listStack.removeMultiple(n);
            }
            if (object2 != null) {
                int n = 0;
                while (n < ((Object)object2).length) {
                    Object object4 = object2[n];
                    listStack.push(new VarType((int)object4));
                    if (object4 == 5 || object4 == 2) {
                        listStack.push(new VarType(12));
                    }
                    ++n;
                }
                return;
            }
        } else {
            listStack = ((DataPoint)object).getStack();
            switch (((Instruction)object2).opcode) {
                case 1: {
                    listStack.push(new VarType(13, 0, null));
                    return;
                }
                case 18: 
                case 19: 
                case 20: {
                    switch (constantPool.getPrimitiveConstant((int)object2.getOperand((int)0)).type) {
                        case 3: {
                            listStack.push(new VarType(4));
                            return;
                        }
                        case 4: {
                            listStack.push(new VarType(3));
                            return;
                        }
                        case 5: {
                            listStack.push(new VarType(5));
                            listStack.push(new VarType(12));
                            return;
                        }
                        case 6: {
                            listStack.push(new VarType(2));
                            listStack.push(new VarType(12));
                            return;
                        }
                        case 8: {
                            listStack.push(new VarType(8, 0, "java/lang/String"));
                            return;
                        }
                        case 7: {
                            listStack.push(new VarType(8, 0, "java/lang/Class"));
                        }
                    }
                    return;
                }
                case 25: {
                    object = ((DataPoint)object).getVariable(((Instruction)object2).getOperand(0));
                    if (object != null) {
                        listStack.push(object);
                        return;
                    }
                    listStack.push(new VarType(8, 0, null));
                    return;
                }
                case 50: {
                    object = (VarType)listStack.pop(2);
                    listStack.push(new VarType(((VarType)object).type, ((VarType)object).arraydim - 1, ((VarType)object).value));
                    return;
                }
                case 58: {
                    ((DataPoint)object).setVariable(((Instruction)object2).getOperand(0), (VarType)listStack.pop());
                    return;
                }
                case 89: 
                case 90: 
                case 91: {
                    int n = 88 - ((Instruction)object2).opcode;
                    listStack.insertByOffset(n, ((VarType)listStack.getByOffset(-1)).copy());
                    return;
                }
                case 92: 
                case 93: 
                case 94: {
                    int n = 90 - ((Instruction)object2).opcode;
                    listStack.insertByOffset(n, ((VarType)listStack.getByOffset(-2)).copy());
                    listStack.insertByOffset(n, ((VarType)listStack.getByOffset(-1)).copy());
                    return;
                }
                case 95: {
                    object = (VarType)listStack.pop();
                    listStack.insertByOffset(-1, object);
                    return;
                }
                case 180: {
                    listStack.pop();
                }
                case 178: {
                    object = constantPool.getLinkConstant(((Instruction)object2).getOperand(0));
                    object = new VarType(((LinkConstant)object).descriptor);
                    listStack.push(object);
                    if (((VarType)object).stack_size != 2) break;
                    listStack.push(new VarType(12));
                    return;
                }
                case 181: {
                    listStack.pop();
                }
                case 179: {
                    object = constantPool.getLinkConstant(((Instruction)object2).getOperand(0));
                    object = new VarType(((LinkConstant)object).descriptor);
                    listStack.pop(((VarType)object).stack_size);
                    return;
                }
                case 182: 
                case 183: 
                case 185: {
                    listStack.pop();
                }
                case 184: {
                    object = MethodDescriptor.parseDescriptor(constantPool.getLinkConstant((int)object2.getOperand((int)0)).descriptor);
                    int n = 0;
                    while (n < ((MethodDescriptor)object).params.length) {
                        listStack.pop(object.params[n].stack_size);
                        ++n;
                    }
                    if (object.ret.type == 10) break;
                    listStack.push(((MethodDescriptor)object).ret);
                    if (object.ret.stack_size != 2) break;
                    listStack.push(new VarType(12));
                    return;
                }
                case 187: {
                    object = constantPool.getPrimitiveConstant(((Instruction)object2).getOperand(0));
                    listStack.push(new VarType(8, 0, (String)((PrimitiveConstant)object).value));
                    return;
                }
                case 188: {
                    listStack.pop();
                    object = new VarType(arr_type[((Instruction)object2).getOperand(0) - 4]);
                    new VarType(arr_type[((Instruction)object2).getOperand(0) - 4]).arraydim = 1;
                    listStack.push(object);
                    return;
                }
                case 191: {
                    object = (VarType)listStack.pop();
                    listStack.clear();
                    listStack.push(object);
                    return;
                }
                case 192: 
                case 193: {
                    listStack.pop();
                    object = constantPool.getPrimitiveConstant(((Instruction)object2).getOperand(0));
                    listStack.push(new VarType(8, 0, (String)((PrimitiveConstant)object).value));
                    return;
                }
                case 189: 
                case 197: {
                    int n = ((Instruction)object2).opcode == 189 ? 1 : ((Instruction)object2).getOperand(1);
                    listStack.pop(n);
                    object = constantPool.getPrimitiveConstant(((Instruction)object2).getOperand(0));
                    if (((PrimitiveConstant)object).isArray) {
                        object = new VarType(8, 0, (String)((PrimitiveConstant)object).value);
                        ((VarType)object).arraydim += n;
                        listStack.push(object);
                        return;
                    }
                    listStack.push(new VarType(8, n, (String)((PrimitiveConstant)object).value));
                }
            }
        }
    }
}

