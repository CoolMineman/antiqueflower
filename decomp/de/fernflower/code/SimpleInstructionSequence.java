/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.code;

import de.fernflower.code.InstructionSequence;
import de.fernflower.util.VBStyleCollection;

public final class SimpleInstructionSequence
extends InstructionSequence {
    public SimpleInstructionSequence() {
    }

    private SimpleInstructionSequence(VBStyleCollection vBStyleCollection) {
        this.collinstr = vBStyleCollection;
    }

    public final void removeInstruction(int n) {
        this.collinstr.remove(n);
    }
}

