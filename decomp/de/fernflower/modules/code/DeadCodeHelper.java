/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.code;

import de.fernflower.code.Instruction;
import de.fernflower.code.InstructionSequence;
import de.fernflower.code.cfg.BasicBlock;
import de.fernflower.code.cfg.ControlFlowGraph;
import de.fernflower.code.cfg.ExceptionRangeCFG;
import de.fernflower.main.DecompilerContext;
import de.fernflower.util.VBStyleCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public final class DeadCodeHelper {
    public static void removeDeadBlocks(ControlFlowGraph controlFlowGraph) {
        Object object;
        Object object2;
        LinkedList<Object> linkedList = new LinkedList<Object>();
        HashSet<Object> hashSet = new HashSet<Object>();
        linkedList.add(controlFlowGraph.getFirst());
        hashSet.add(controlFlowGraph.getFirst());
        while (!linkedList.isEmpty()) {
            object2 = (BasicBlock)linkedList.removeFirst();
            object = new ArrayList(((BasicBlock)object2).getSuccs());
            object.addAll(((BasicBlock)object2).getSuccExceptions());
            object = object.iterator();
            while (object.hasNext()) {
                object2 = (BasicBlock)object.next();
                if (hashSet.contains(object2)) continue;
                linkedList.add(object2);
                hashSet.add(object2);
            }
        }
        object2 = new HashSet(controlFlowGraph.getBlocks());
        ((AbstractSet)object2).removeAll(hashSet);
        object2 = ((HashSet)object2).iterator();
        while (object2.hasNext()) {
            object = (BasicBlock)object2.next();
            controlFlowGraph.removeBlock((BasicBlock)object);
        }
    }

    public static void removeEmptyBlocks(ControlFlowGraph controlFlowGraph) {
        boolean bl;
        VBStyleCollection vBStyleCollection = controlFlowGraph.getBlocks();
        block0: do {
            bl = false;
            int n = vBStyleCollection.size() - 1;
            while (n >= 0) {
                BasicBlock basicBlock = (BasicBlock)vBStyleCollection.get(n);
                if (DeadCodeHelper.removeEmptyBlock(controlFlowGraph, basicBlock, false)) {
                    bl = true;
                    continue block0;
                }
                --n;
            }
        } while (bl);
    }

    private static boolean removeEmptyBlock(ControlFlowGraph controlFlowGraph, BasicBlock basicBlock, boolean bl) {
        boolean bl2 = false;
        if (basicBlock.getSeq().isEmpty()) {
            if (basicBlock.getSuccs().size() > 1) {
                if (basicBlock.getPreds().size() > 1) {
                    throw new RuntimeException("ERROR: empty block with multiple predecessors and successors found");
                }
                if (!bl) {
                    throw new RuntimeException("ERROR: empty block with multiple successors found");
                }
            }
            Object object4 = new HashSet(controlFlowGraph.getLast().getPreds());
            if (basicBlock.getPredExceptions().isEmpty() && (!((HashSet)object4).contains(basicBlock) || basicBlock.getPreds().size() == 1)) {
                Object object2;
                Object object3;
                if (((HashSet)object4).contains(basicBlock) && (((BasicBlock)(object4 = (BasicBlock)basicBlock.getPreds().get(0))).getSuccs().size() != 1 || !((BasicBlock)object4).getSeq().isEmpty() && object4.getSeq().getLastInstr().group == 3)) {
                    return false;
                }
                object4 = controlFlowGraph.getExceptions();
                int n = object4.size() - 1;
                while (n >= 0) {
                    object3 = (ExceptionRangeCFG)object4.get(n);
                    object2 = ((ExceptionRangeCFG)object3).getProtectedRange();
                    if (object2.size() == 1 && object2.get(0) == basicBlock) {
                        if (DecompilerContext.getOption("rer")) {
                            basicBlock.removeSuccessorException(((ExceptionRangeCFG)object3).getHandler());
                            object4.remove(n);
                            bl2 = true;
                        } else {
                            return false;
                        }
                    }
                    --n;
                }
                HashSet hashSet = new HashSet(basicBlock.getSuccs());
                object3 = new HashSet(basicBlock.getPreds());
                if (bl) {
                    object2 = (BasicBlock)basicBlock.getPreds().get(0);
                    ((BasicBlock)object2).removeSuccessor(basicBlock);
                    Object var2_3 = null;
                    for (Object object4 : new ArrayList(basicBlock.getSuccs())) {
                        basicBlock.removeSuccessor((BasicBlock)object4);
                        ((BasicBlock)object2).addSuccessor((BasicBlock)object4);
                    }
                } else {
                    Iterator iterator = ((HashSet)object3).iterator();
                    while (iterator.hasNext()) {
                        object2 = (BasicBlock)iterator.next();
                        for (Object object4 : hashSet) {
                            ((BasicBlock)object2).replaceSuccessor(basicBlock, (BasicBlock)object4);
                        }
                    }
                }
                object2 = controlFlowGraph.getFinallyExits();
                if (((HashSet)object2).contains(basicBlock)) {
                    ((HashSet)object2).remove(basicBlock);
                    ((HashSet)object2).add((BasicBlock)((HashSet)object3).iterator().next());
                }
                if (controlFlowGraph.getFirst() == basicBlock) {
                    if (hashSet.size() != 1) {
                        throw new RuntimeException("multiple or no entry blocks!");
                    }
                    controlFlowGraph.setFirst((BasicBlock)hashSet.iterator().next());
                }
                controlFlowGraph.removeBlock(basicBlock);
                if (bl2) {
                    DeadCodeHelper.removeDeadBlocks(controlFlowGraph);
                }
            }
        }
        return bl2;
    }

    public static boolean isDominator(ControlFlowGraph controlFlowGraph, BasicBlock basicBlock, BasicBlock basicBlock2) {
        HashSet<BasicBlock> hashSet = new HashSet<BasicBlock>();
        if (basicBlock == basicBlock2) {
            return true;
        }
        LinkedList<BasicBlock> linkedList = new LinkedList<BasicBlock>();
        linkedList.add(basicBlock);
        while (!linkedList.isEmpty()) {
            BasicBlock basicBlock3;
            basicBlock = (BasicBlock)linkedList.remove(0);
            if (hashSet.contains(basicBlock)) continue;
            hashSet.add(basicBlock);
            if (basicBlock == controlFlowGraph.getFirst()) {
                return false;
            }
            int n = 0;
            while (n < basicBlock.getPreds().size()) {
                basicBlock3 = (BasicBlock)basicBlock.getPreds().get(n);
                if (!hashSet.contains(basicBlock3) && basicBlock3 != basicBlock2) {
                    linkedList.add(basicBlock3);
                }
                ++n;
            }
            n = 0;
            while (n < basicBlock.getPredExceptions().size()) {
                basicBlock3 = (BasicBlock)basicBlock.getPredExceptions().get(n);
                if (!hashSet.contains(basicBlock3) && basicBlock3 != basicBlock2) {
                    linkedList.add(basicBlock3);
                }
                ++n;
            }
        }
        return true;
    }

    public static void removeGotos(ControlFlowGraph controlFlowGraph) {
        for (BasicBlock basicBlock : controlFlowGraph.getBlocks()) {
            Instruction instruction = basicBlock.getLastInstruction();
            if (instruction == null || instruction.opcode != 167) continue;
            basicBlock.getSeq().removeInstruction(basicBlock.getSeq().length() - 1);
        }
        DeadCodeHelper.removeEmptyBlocks(controlFlowGraph);
    }

    /*
     * Unable to fully structure code
     */
    public static void incorporateValueReturns(ControlFlowGraph var0) {
        for (BasicBlock var1_2 : var0.getBlocks()) {
            block15: {
                var3_3 = var1_2.getSeq();
                var4_4 = var3_3.length();
                if (var4_4 <= 0 || var4_4 >= 3) continue;
                var5_8 = false;
                if (var3_3.getLastInstr().opcode < 172 || var3_3.getLastInstr().opcode > 177) break block15;
                if (var4_4 == 1) ** GOTO lbl-1000
                if (var3_3.getLastInstr().opcode == 177) break block15;
                switch (var3_3.getInstr((int)0).opcode) {
                    case 1: 
                    case 9: 
                    case 10: 
                    case 11: 
                    case 12: 
                    case 13: 
                    case 14: 
                    case 15: 
                    case 16: 
                    case 17: 
                    case 18: 
                    case 19: 
                    case 20: 
                    case 21: 
                    case 22: 
                    case 23: 
                    case 24: 
                    case 25: lbl-1000:
                    // 2 sources

                    {
                        var5_8 = true;
                    }
                }
            }
            if (!var5_8) continue;
            if (!var1_2.getPreds().isEmpty()) {
                var3_3 = new HashSet<E>();
                var4_5 = new HashSet<E>();
                var5_8 = true;
                for (Object var6_12 : var1_2.getPreds()) {
                    if (var5_8) {
                        var4_5.addAll(var6_12.getSuccExceptions());
                        var5_8 = false;
                    } else {
                        var4_5.retainAll(var6_12.getSuccExceptions());
                    }
                    var3_3.addAll(var6_12.getSuccExceptions());
                }
                var4_5.removeAll(var1_2.getSuccExceptions());
                var6_12 = (BasicBlock)var1_2.getPreds().get(0);
                var4_5 = var4_5.iterator();
                while (var4_5.hasNext()) {
                    var7_13 = (BasicBlock)var4_5.next();
                    var5_9 = null;
                    var0.getExceptionRange((BasicBlock)var7_13, (BasicBlock)var6_12).getProtectedRange().add(var1_2);
                    var1_2.addSuccessorException((BasicBlock)var7_13);
                }
                var7_13 = new HashSet<E>(var1_2.getSuccExceptions());
                var7_13.removeAll((Collection<?>)var3_3);
                for (Object var4_5 : var7_13) {
                    var3_3 = var0.getExceptionRange((BasicBlock)var4_5, var1_2);
                    if (var3_3.getProtectedRange().size() <= 1) continue;
                    var3_3.getProtectedRange().remove(var1_2);
                    var1_2.removeSuccessorException((BasicBlock)var4_5);
                }
            }
            if (var1_2.getPreds().size() != 1 || !var1_2.getPredExceptions().isEmpty() || (var3_3 = (BasicBlock)var1_2.getPreds().get(0)).getSuccs().size() != 1) continue;
            for (BasicBlock var4_6 : var3_3.getSuccExceptions()) {
                if (var1_2.getSuccExceptions().contains(var4_6)) continue;
                var6_12 = null;
                var0.getExceptionRange(var4_6, (BasicBlock)var3_3).getProtectedRange().add(var1_2);
                var1_2.addSuccessorException(var4_6);
            }
            for (BasicBlock var4_7 : new HashSet<E>(var1_2.getSuccExceptions())) {
                if (var3_3.getSuccExceptions().contains(var4_7) || (var6_12 = var0.getExceptionRange(var4_7, var1_2)).getProtectedRange().size() <= 1) continue;
                var6_12.getProtectedRange().remove(var1_2);
                var1_2.removeSuccessorException(var4_7);
            }
        }
    }

    public static void mergeBasicBlocks(ControlFlowGraph controlFlowGraph) {
        boolean bl;
        block0: do {
            bl = false;
            for (Object object : controlFlowGraph.getBlocks()) {
                BasicBlock basicBlock;
                InstructionSequence instructionSequence = ((BasicBlock)object).getSeq();
                if (((BasicBlock)object).getSuccs().size() != 1 || (basicBlock = (BasicBlock)((BasicBlock)object).getSuccs().get(0)) == controlFlowGraph.getLast() || !instructionSequence.isEmpty() && instructionSequence.getLastInstr().group == 3 || basicBlock.getPreds().size() != 1 || !basicBlock.getPredExceptions().isEmpty() || basicBlock == controlFlowGraph.getFirst()) continue;
                boolean bl2 = true;
                for (ExceptionRangeCFG exceptionRangeCFG : controlFlowGraph.getExceptions()) {
                    if (!(exceptionRangeCFG.getProtectedRange().contains(object) ^ exceptionRangeCFG.getProtectedRange().contains(basicBlock))) continue;
                    bl2 = false;
                    break;
                }
                if (!bl2) continue;
                object = basicBlock.getSeq();
                InstructionSequence instructionSequence2 = instructionSequence;
                int n = 0;
                while (n < ((InstructionSequence)object).length()) {
                    instructionSequence2.addInstruction(((InstructionSequence)object).getInstr(n), -1);
                    ++n;
                }
                basicBlock.getSeq().clear();
                DeadCodeHelper.removeEmptyBlock(controlFlowGraph, basicBlock, true);
                bl = true;
                continue block0;
            }
        } while (bl);
    }
}

