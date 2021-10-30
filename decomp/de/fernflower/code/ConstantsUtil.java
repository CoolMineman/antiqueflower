/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.code;

import de.fernflower.code.IfInstruction;
import de.fernflower.code.Instruction;
import de.fernflower.code.instructions.LOOKUPSWITCH;
import de.fernflower.code.instructions.MULTIANEWARRAY;
import de.fernflower.code.optinstructions.ALOAD;
import de.fernflower.code.optinstructions.ANEWARRAY;
import de.fernflower.code.optinstructions.ASTORE;
import de.fernflower.code.optinstructions.BIPUSH;
import de.fernflower.code.optinstructions.CHECKCAST;
import de.fernflower.code.optinstructions.DLOAD;
import de.fernflower.code.optinstructions.DSTORE;
import de.fernflower.code.optinstructions.FLOAD;
import de.fernflower.code.optinstructions.FSTORE;
import de.fernflower.code.optinstructions.GETFIELD;
import de.fernflower.code.optinstructions.GETSTATIC;
import de.fernflower.code.optinstructions.GOTO;
import de.fernflower.code.optinstructions.GOTO_W;
import de.fernflower.code.optinstructions.IINC;
import de.fernflower.code.optinstructions.ILOAD;
import de.fernflower.code.optinstructions.INSTANCEOF;
import de.fernflower.code.optinstructions.INVOKEINTERFACE;
import de.fernflower.code.optinstructions.INVOKESPECIAL;
import de.fernflower.code.optinstructions.INVOKESTATIC;
import de.fernflower.code.optinstructions.INVOKEVIRTUAL;
import de.fernflower.code.optinstructions.ISTORE;
import de.fernflower.code.optinstructions.JSR;
import de.fernflower.code.optinstructions.JSR_W;
import de.fernflower.code.optinstructions.LDC;
import de.fernflower.code.optinstructions.LDC2_W;
import de.fernflower.code.optinstructions.LDC_W;
import de.fernflower.code.optinstructions.LLOAD;
import de.fernflower.code.optinstructions.LSTORE;
import de.fernflower.code.optinstructions.NEW;
import de.fernflower.code.optinstructions.NEWARRAY;
import de.fernflower.code.optinstructions.PUTFIELD;
import de.fernflower.code.optinstructions.PUTSTATIC;
import de.fernflower.code.optinstructions.RET;
import de.fernflower.code.optinstructions.SIPUSH;
import de.fernflower.code.optinstructions.TABLESWITCH;

public final class ConstantsUtil {
    private static String[] opcodeNames = new String[]{"nop", "aconst_null", "iconst_m1", "iconst_0", "iconst_1", "iconst_2", "iconst_3", "iconst_4", "iconst_5", "lconst_0", "lconst_1", "fconst_0", "fconst_1", "fconst_2", "dconst_0", "dconst_1", "bipush", "sipush", "ldc", "ldc_w", "ldc2_w", "iload", "lload", "fload", "dload", "aload", "iload_0", "iload_1", "iload_2", "iload_3", "lload_0", "lload_1", "lload_2", "lload_3", "fload_0", "fload_1", "fload_2", "fload_3", "dload_0", "dload_1", "dload_2", "dload_3", "aload_0", "aload_1", "aload_2", "aload_3", "iaload", "laload", "faload", "daload", "aaload", "baload", "caload", "saload", "istore", "lstore", "fstore", "dstore", "astore", "istore_0", "istore_1", "istore_2", "istore_3", "lstore_0", "lstore_1", "lstore_2", "lstore_3", "fstore_0", "fstore_1", "fstore_2", "fstore_3", "dstore_0", "dstore_1", "dstore_2", "dstore_3", "astore_0", "astore_1", "astore_2", "astore_3", "iastore", "lastore", "fastore", "dastore", "aastore", "bastore", "castore", "sastore", "pop", "pop2", "dup", "dup_x1", "dup_x2", "dup2", "dup2_x1", "dup2_x2", "swap", "iadd", "ladd", "fadd", "dadd", "isub", "lsub", "fsub", "dsub", "imul", "lmul", "fmul", "dmul", "idiv", "ldiv", "fdiv", "ddiv", "irem", "lrem", "frem", "drem", "ineg", "lneg", "fneg", "dneg", "ishl", "lshl", "ishr", "lshr", "iushr", "lushr", "iand", "land", "ior", "lor", "ixor", "lxor", "iinc", "i2l", "i2f", "i2d", "l2i", "l2f", "l2d", "f2i", "f2l", "f2d", "d2i", "d2l", "d2f", "i2b", "i2c", "i2s", "lcmp", "fcmpl", "fcmpg", "dcmpl", "dcmpg", "ifeq", "ifne", "iflt", "ifge", "ifgt", "ifle", "if_icmpeq", "if_icmpne", "if_icmplt", "if_icmpge", "if_icmpgt", "if_icmple", "if_acmpeq", "if_acmpne", "goto", "jsr", "ret", "tableswitch", "lookupswitch", "ireturn", "lreturn", "freturn", "dreturn", "areturn", "return", "getstatic", "putstatic", "getfield", "putfield", "invokevirtual", "invokespecial", "invokestatic", "invokeinterface", "xxxunusedxxx", "new", "newarray", "anewarray", "arraylength", "athrow", "checkcast", "instanceof", "monitorenter", "monitorexit", "wide", "multianewarray", "ifnull", "ifnonnull", "goto_w", "jsr_w"};
    private static Class[] opcodeClasses;

    static {
        Class[] classArray = new Class[202];
        classArray[16] = BIPUSH.class;
        classArray[17] = SIPUSH.class;
        classArray[18] = LDC.class;
        classArray[19] = LDC_W.class;
        classArray[20] = LDC2_W.class;
        classArray[21] = ILOAD.class;
        classArray[22] = LLOAD.class;
        classArray[23] = FLOAD.class;
        classArray[24] = DLOAD.class;
        classArray[25] = ALOAD.class;
        classArray[54] = ISTORE.class;
        classArray[55] = LSTORE.class;
        classArray[56] = FSTORE.class;
        classArray[57] = DSTORE.class;
        classArray[58] = ASTORE.class;
        classArray[132] = IINC.class;
        classArray[167] = GOTO.class;
        classArray[168] = JSR.class;
        classArray[169] = RET.class;
        classArray[170] = TABLESWITCH.class;
        classArray[171] = LOOKUPSWITCH.class;
        classArray[178] = GETSTATIC.class;
        classArray[179] = PUTSTATIC.class;
        classArray[180] = GETFIELD.class;
        classArray[181] = PUTFIELD.class;
        classArray[182] = INVOKEVIRTUAL.class;
        classArray[183] = INVOKESPECIAL.class;
        classArray[184] = INVOKESTATIC.class;
        classArray[185] = INVOKEINTERFACE.class;
        classArray[187] = NEW.class;
        classArray[188] = NEWARRAY.class;
        classArray[189] = ANEWARRAY.class;
        classArray[192] = CHECKCAST.class;
        classArray[193] = INSTANCEOF.class;
        classArray[197] = MULTIANEWARRAY.class;
        classArray[200] = GOTO_W.class;
        classArray[201] = JSR_W.class;
        opcodeClasses = classArray;
    }

    public static String getName(int n) {
        return opcodeNames[n];
    }

    public static Instruction getInstructionInstance(int n, boolean bl, int n2, int[] nArray) {
        Instruction instruction = ConstantsUtil.getInstructionInstance(n);
        ConstantsUtil.getInstructionInstance(n).wide = bl;
        instruction.group = n2;
        instruction.setOperands(nArray);
        return instruction;
    }

    private static Instruction getInstructionInstance(int n) {
        try {
            Object object;
            object = n >= 153 && n <= 166 || n == 198 || n == 199 ? new IfInstruction() : ((object = opcodeClasses[n]) == null ? new Instruction() : (Instruction)((Class)object).newInstance());
            ((Instruction)object).opcode = n;
            return object;
        }
        catch (Exception exception) {
            return null;
        }
    }
}

