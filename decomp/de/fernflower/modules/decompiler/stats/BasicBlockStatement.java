/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.stats;

import de.fernflower.code.Instruction;
import de.fernflower.code.SimpleInstructionSequence;
import de.fernflower.code.cfg.BasicBlock;
import de.fernflower.main.DecompilerContext;
import de.fernflower.main.collectors.CounterContainer;
import de.fernflower.modules.decompiler.ExprProcessor;
import de.fernflower.modules.decompiler.stats.Statement;

public final class BasicBlockStatement
extends Statement {
    private BasicBlock block;

    public BasicBlockStatement(BasicBlock object) {
        this.type = 8;
        this.block = object;
        this.id = ((BasicBlock)object).id;
        CounterContainer counterContainer = DecompilerContext.getCountercontainer();
        if (this.id >= counterContainer.getCounter()) {
            counterContainer.setCounter(0, this.id + 1);
        }
        if ((object = ((BasicBlock)object).getLastInstruction()) != null) {
            if (((Instruction)object).group == 2 && ((Instruction)object).opcode != 167) {
                this.lastBasicType = 0;
            } else if (((Instruction)object).group == 3) {
                this.lastBasicType = 1;
            }
        }
        this.buildMonitorFlags();
    }

    public final String toJava(int n) {
        return String.valueOf(ExprProcessor.listToJava(this.varDefinitions, n)) + ExprProcessor.listToJava(this.exprents, n);
    }

    public final Statement getSimpleCopy() {
        BasicBlock basicBlock = new BasicBlock(DecompilerContext.getCountercontainer().getCounterAndIncrement(0));
        SimpleInstructionSequence simpleInstructionSequence = new SimpleInstructionSequence();
        int n = 0;
        while (n < this.block.getSeq().length()) {
            simpleInstructionSequence.addInstruction(this.block.getSeq().getInstr(n).clone(), -1);
            ++n;
        }
        basicBlock.setSeq(simpleInstructionSequence);
        return new BasicBlockStatement(basicBlock);
    }

    public final BasicBlock getBlock() {
        return this.block;
    }
}

