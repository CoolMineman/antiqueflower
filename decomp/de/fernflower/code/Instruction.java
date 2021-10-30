/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.code;

import de.fernflower.bx;
import de.fernflower.code.ConstantsUtil;
import de.fernflower.code.InstructionSequence;

public class Instruction
implements bx {
    public int opcode;
    public int group = 1;
    public boolean wide = false;
    private int[] operands = null;

    public final int operandsCount() {
        if (this.operands == null) {
            return 0;
        }
        return this.operands.length;
    }

    public final int getOperand(int n) {
        return this.operands[n];
    }

    public Instruction clone() {
        return ConstantsUtil.getInstructionInstance(this.opcode, this.wide, this.group, this.operands == null ? null : (int[])this.operands.clone());
    }

    public String toString() {
        String string = this.wide ? "@wide " : "";
        string = String.valueOf(string) + "@" + ConstantsUtil.getName(this.opcode);
        int n = this.operandsCount();
        int n2 = 0;
        while (n2 < n) {
            int n3 = this.operands[n2];
            string = n3 < 0 ? String.valueOf(string) + " -" + Integer.toHexString(-n3) : String.valueOf(string) + " " + Integer.toHexString(n3);
            ++n2;
        }
        return string;
    }

    public void initInstruction(InstructionSequence instructionSequence) {
    }

    public final int[] getOperands() {
        return this.operands;
    }

    public final void setOperands(int[] nArray) {
        this.operands = nArray;
    }
}

