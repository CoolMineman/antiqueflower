/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.code;

import de.fernflower.code.ExceptionTable;
import de.fernflower.code.Instruction;
import de.fernflower.util.InterpreterUtil;
import de.fernflower.util.VBStyleCollection;

public abstract class InstructionSequence {
    protected VBStyleCollection collinstr = new VBStyleCollection();
    private int pointer = 0;
    protected ExceptionTable exceptionTable = new ExceptionTable();

    public InstructionSequence clone() {
        return null;
    }

    public final void clear() {
        this.collinstr.clear();
        this.pointer = 0;
        this.exceptionTable = new ExceptionTable();
    }

    public final void addInstruction(Instruction instruction, int n) {
        this.collinstr.addWithKey(instruction, n);
    }

    public final void a(Instruction instruction) {
        this.collinstr.addWithKeyAndIndex(0, instruction, -1);
    }

    public void removeInstruction(int n) {
        this.collinstr.remove(n);
    }

    public final Instruction getInstr(int n) {
        return (Instruction)this.collinstr.get(n);
    }

    public final Instruction getLastInstr() {
        return (Instruction)this.collinstr.getLast();
    }

    public final int getOffset(int n) {
        return (Integer)this.collinstr.getKey(n);
    }

    public final int getPointerByAbsOffset(int n) {
        Integer n2 = new Integer(n);
        if (this.collinstr.containsKey(n2)) {
            return this.collinstr.getIndexByKey(n2);
        }
        return -1;
    }

    public final int getPointerByRelOffset(int n) {
        Integer n2 = new Integer((Integer)this.collinstr.getKey(this.pointer) + n);
        if (this.collinstr.containsKey(n2)) {
            return this.collinstr.getIndexByKey(n2);
        }
        return -1;
    }

    public final int length() {
        return this.collinstr.size();
    }

    public final boolean isEmpty() {
        return this.collinstr.isEmpty();
    }

    public final void addToPointer() {
        this.pointer += -1;
    }

    public String toString() {
        return this.toString(0);
    }

    public final String toString(int n) {
        StringBuffer stringBuffer = new StringBuffer();
        int n2 = 0;
        while (n2 < this.collinstr.size()) {
            stringBuffer.append(InterpreterUtil.getIndentString(0));
            stringBuffer.append((Integer)this.collinstr.getKey(n2));
            stringBuffer.append(": ");
            stringBuffer.append(((Instruction)this.collinstr.get(n2)).toString());
            stringBuffer.append("\r\n");
            ++n2;
        }
        return stringBuffer.toString();
    }

    public final int getPointer() {
        return this.pointer;
    }

    public final void setPointer(int n) {
        this.pointer = n;
    }

    public final ExceptionTable getExceptionTable() {
        return this.exceptionTable;
    }
}

