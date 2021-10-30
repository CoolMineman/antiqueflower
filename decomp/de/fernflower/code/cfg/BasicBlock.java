/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.code.cfg;

import de.fernflower.code.Instruction;
import de.fernflower.code.InstructionSequence;
import de.fernflower.code.SimpleInstructionSequence;
import de.fernflower.modules.decompiler.decompose.IGraphNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class BasicBlock
implements IGraphNode {
    public int id = 0;
    public int mark = 0;
    private InstructionSequence seq = new SimpleInstructionSequence();
    private List preds = new ArrayList();
    private List succs = new ArrayList();
    private List instrOldOffsets = new ArrayList();
    private List predExceptions = new ArrayList();
    private List succExceptions = new ArrayList();

    public BasicBlock() {
    }

    public BasicBlock(int n) {
        this.id = n;
    }

    public final Object clone() {
        BasicBlock basicBlock = new BasicBlock();
        new BasicBlock().id = this.id;
        basicBlock.seq = this.seq.clone();
        basicBlock.instrOldOffsets = new ArrayList(this.instrOldOffsets);
        return basicBlock;
    }

    public final Instruction getInstruction() {
        return this.seq.getInstr(0);
    }

    public final Instruction getLastInstruction() {
        if (this.seq.isEmpty()) {
            return null;
        }
        return this.seq.getLastInstr();
    }

    public final void addPredecessor(BasicBlock basicBlock) {
        this.preds.add(basicBlock);
    }

    public final void removePredecessor(BasicBlock basicBlock) {
        while (this.preds.remove(basicBlock)) {
        }
    }

    public final void addSuccessor(BasicBlock basicBlock) {
        this.succs.add(basicBlock);
        basicBlock.addPredecessor(this);
    }

    public final void removeSuccessor(BasicBlock basicBlock) {
        while (this.succs.remove(basicBlock)) {
        }
        basicBlock.removePredecessor(this);
    }

    public final void replaceSuccessor(BasicBlock basicBlock, BasicBlock basicBlock2) {
        int n = 0;
        while (n < this.succs.size()) {
            if (((BasicBlock)this.succs.get((int)n)).id == basicBlock.id) {
                this.succs.set(n, basicBlock2);
                basicBlock.removePredecessor(this);
                basicBlock2.addPredecessor(this);
            }
            ++n;
        }
        n = 0;
        while (n < this.succExceptions.size()) {
            if (((BasicBlock)this.succExceptions.get((int)n)).id == basicBlock.id) {
                this.succExceptions.set(n, basicBlock2);
                basicBlock.removePredecessorException(this);
                basicBlock2.addPredecessorException(this);
            }
            ++n;
        }
    }

    private void addPredecessorException(BasicBlock basicBlock) {
        this.predExceptions.add(basicBlock);
    }

    private void removePredecessorException(BasicBlock basicBlock) {
        while (this.predExceptions.remove(basicBlock)) {
        }
    }

    public final void addSuccessorException(BasicBlock basicBlock) {
        if (!this.succExceptions.contains(basicBlock)) {
            this.succExceptions.add(basicBlock);
            basicBlock.addPredecessorException(this);
        }
    }

    public final void removeSuccessorException(BasicBlock basicBlock) {
        while (this.succExceptions.remove(basicBlock)) {
        }
        basicBlock.removePredecessorException(this);
    }

    public final String toString() {
        boolean bl = false;
        BasicBlock basicBlock = this;
        return String.valueOf(basicBlock.id) + ":\r\n" + basicBlock.seq.toString(0);
    }

    public final boolean isSuccessor(BasicBlock basicBlock) {
        Iterator iterator = ((BasicBlock)iterator).succs.iterator();
        while (iterator.hasNext()) {
            if (((BasicBlock)iterator.next()).id != basicBlock.id) continue;
            return true;
        }
        return false;
    }

    public final void setInstrOldOffsets(List list) {
        this.instrOldOffsets = list;
    }

    public final List getPredecessors() {
        ArrayList arrayList = new ArrayList(this.preds);
        arrayList.addAll(this.predExceptions);
        return arrayList;
    }

    public final List getPreds() {
        return this.preds;
    }

    public final InstructionSequence getSeq() {
        return this.seq;
    }

    public final void setSeq(InstructionSequence instructionSequence) {
        this.seq = instructionSequence;
    }

    public final List getSuccs() {
        return this.succs;
    }

    public final List getSuccExceptions() {
        return this.succExceptions;
    }

    public final List getPredExceptions() {
        return this.predExceptions;
    }
}

