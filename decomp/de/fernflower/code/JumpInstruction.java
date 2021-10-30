/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.code;

import de.fernflower.code.Instruction;
import de.fernflower.code.InstructionSequence;

public class JumpInstruction
extends Instruction {
    public int destination;

    public final void initInstruction(InstructionSequence instructionSequence) {
        this.destination = instructionSequence.getPointerByRelOffset(this.getOperand(0));
    }
}

