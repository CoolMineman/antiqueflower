/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.code;

import de.fernflower.code.ExceptionHandler;
import de.fernflower.code.ExceptionTable;
import de.fernflower.code.InstructionSequence;
import de.fernflower.util.VBStyleCollection;

public final class FullInstructionSequence
extends InstructionSequence {
    public FullInstructionSequence(VBStyleCollection object3, ExceptionTable object2) {
        this.collinstr = object3;
        this.exceptionTable = object2;
        for (Object object3 : ((ExceptionTable)object2).getHandlers()) {
            ((ExceptionHandler)object2.next()).from_instr = this.getPointerByAbsOffset(((ExceptionHandler)object3).from);
            ((ExceptionHandler)object3).to_instr = this.getPointerByAbsOffset(((ExceptionHandler)object3).to);
            ((ExceptionHandler)object3).handler_instr = this.getPointerByAbsOffset(((ExceptionHandler)object3).handler);
        }
    }
}

