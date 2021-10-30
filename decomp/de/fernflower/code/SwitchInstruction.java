/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.code;

import de.fernflower.code.Instruction;
import de.fernflower.code.InstructionSequence;

public class SwitchInstruction
extends Instruction {
    private int[] destinations;
    private int[] values;
    private int defaultdest;

    public final void initInstruction(InstructionSequence instructionSequence) {
        int n = this.opcode == 170 ? 3 : 2;
        int n2 = this.getOperands().length - n;
        this.defaultdest = instructionSequence.getPointerByRelOffset(this.getOperand(0));
        int n3 = 0;
        if (this.opcode == 171) {
            n2 /= 2;
        } else {
            n3 = this.getOperand(1);
        }
        this.destinations = new int[n2];
        this.values = new int[n2];
        int n4 = 0;
        int n5 = 0;
        while (n4 < n2) {
            if (this.opcode == 171) {
                this.values[n4] = this.getOperand(n + n5);
                ++n5;
            } else {
                this.values[n4] = n3 + n5;
            }
            this.destinations[n4] = instructionSequence.getPointerByRelOffset(this.getOperand(n + n5));
            ++n4;
            ++n5;
        }
    }

    public final int[] getDestinations() {
        return this.destinations;
    }

    public final int getDefaultdest() {
        return this.defaultdest;
    }

    public final int[] getValues() {
        return this.values;
    }
}

