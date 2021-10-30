/*
 * Decompiled with CFR 0.151.
 */
package de.fernflower.modules.decompiler.deobfuscator;

import de.fernflower.code.Instruction;
import de.fernflower.code.InstructionSequence;
import de.fernflower.code.SimpleInstructionSequence;
import de.fernflower.code.cfg.BasicBlock;
import de.fernflower.code.cfg.ControlFlowGraph;
import de.fernflower.code.cfg.ExceptionRangeCFG;
import de.fernflower.modules.decompiler.decompose.GenericDominatorEngine;
import de.fernflower.modules.decompiler.deobfuscator.ExceptionDeobfuscator$1;
import de.fernflower.util.InterpreterUtil;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class ExceptionDeobfuscator {
    /*
     * WARNING - void declaration
     */
    public static void restorePopRanges(ControlFlowGraph controlFlowGraph) {
        ArrayList<Object[]> arrayList = new ArrayList<Object[]>();
        for (Object[] objectArray : controlFlowGraph.getExceptions()) {
            boolean bl = false;
            for (Object[] object : arrayList) {
                if (object[0] != objectArray.getHandler() || !InterpreterUtil.equalObjects(objectArray.getExceptionType(), object[1])) continue;
                ((HashSet)object[2]).addAll(objectArray.getProtectedRange());
                bl = true;
                break;
            }
            if (bl) continue;
            arrayList.add(new Object[]{objectArray.getHandler(), objectArray.getExceptionType(), new HashSet(objectArray.getProtectedRange()), objectArray});
        }
        for (Object[] objectArray : arrayList) {
            if (objectArray[1] == null) continue;
            BasicBlock basicBlock = (BasicBlock)objectArray[0];
            InstructionSequence instructionSequence = basicBlock.getSeq();
            Object object = null;
            if (instructionSequence.length() <= 0) continue;
            object = instructionSequence.getInstr(0);
            if (((Instruction)object).opcode != 87 && ((Instruction)object).opcode != 58) continue;
            HashSet hashSet = new HashSet((HashSet)objectArray[2]);
            for (Object[] objectArray2 : arrayList) {
                void var5_10;
                if (objectArray == objectArray2) continue;
                Object object2 = new HashSet((HashSet)objectArray2[2]);
                if (hashSet.contains(objectArray2[0]) || ((HashSet)object2).contains(basicBlock) || objectArray2[1] != null && !((AbstractCollection)object2).containsAll(hashSet)) continue;
                if (objectArray2[1] == null) {
                    ((AbstractCollection)object2).retainAll(hashSet);
                } else {
                    ((AbstractSet)object2).removeAll(hashSet);
                }
                if (((HashSet)object2).isEmpty()) continue;
                object2 = basicBlock;
                if (var5_10.length() > 1) {
                    object2 = new BasicBlock(++controlFlowGraph.last_id);
                    Object object32 = new SimpleInstructionSequence();
                    ((InstructionSequence)object32).addInstruction(((Instruction)object).clone(), -1);
                    ((BasicBlock)object2).setSeq((InstructionSequence)object32);
                    controlFlowGraph.getBlocks().addWithKey(object2, ((BasicBlock)object2).id);
                    object32 = new ArrayList();
                    object32.addAll(basicBlock.getPreds());
                    object32.addAll(basicBlock.getPredExceptions());
                    Iterator iterator = object32.iterator();
                    while (iterator.hasNext()) {
                        ((BasicBlock)iterator.next()).replaceSuccessor(basicBlock, (BasicBlock)object2);
                    }
                    for (Object object32 : controlFlowGraph.getExceptions()) {
                        if (((ExceptionRangeCFG)object32).getHandler() == basicBlock) {
                            ((ExceptionRangeCFG)object32).setHandler((BasicBlock)object2);
                            continue;
                        }
                        if (!((ExceptionRangeCFG)object32).getProtectedRange().contains(basicBlock)) continue;
                        ((BasicBlock)object2).addSuccessorException(((ExceptionRangeCFG)object32).getHandler());
                        ((ExceptionRangeCFG)object32).getProtectedRange().add(object2);
                    }
                    ((BasicBlock)object2).addSuccessor(basicBlock);
                    if (controlFlowGraph.getFirst() == basicBlock) {
                        controlFlowGraph.setFirst((BasicBlock)object2);
                    }
                    var5_10.removeInstruction(0);
                }
                ((BasicBlock)object2).addSuccessorException((BasicBlock)objectArray2[0]);
                ((ExceptionRangeCFG)objectArray2[3]).getProtectedRange().add(object2);
                basicBlock = ((ExceptionRangeCFG)objectArray[3]).getHandler();
                InstructionSequence instructionSequence2 = basicBlock.getSeq();
            }
        }
    }

    public static void insertEmptyExceptionHandlerBlocks(ControlFlowGraph controlFlowGraph) {
        HashSet<BasicBlock> hashSet = new HashSet<BasicBlock>();
        Iterator iterator = controlFlowGraph.getExceptions().iterator();
        while (iterator.hasNext()) {
            BasicBlock basicBlock = null;
            basicBlock = ((ExceptionRangeCFG)iterator.next()).getHandler();
            if (hashSet.contains(basicBlock)) continue;
            hashSet.add(basicBlock);
            BasicBlock basicBlock2 = new BasicBlock(++controlFlowGraph.last_id);
            controlFlowGraph.getBlocks().addWithKey(basicBlock2, basicBlock2.id);
            Object object2 = new ArrayList();
            object2.addAll(basicBlock.getPredExceptions());
            Iterator iterator2 = object2.iterator();
            while (iterator2.hasNext()) {
                ((BasicBlock)iterator2.next()).replaceSuccessor(basicBlock, basicBlock2);
            }
            for (Object object2 : controlFlowGraph.getExceptions()) {
                if (((ExceptionRangeCFG)object2).getHandler() == basicBlock) {
                    ((ExceptionRangeCFG)object2).setHandler(basicBlock2);
                    continue;
                }
                if (!((ExceptionRangeCFG)object2).getProtectedRange().contains(basicBlock)) continue;
                basicBlock2.addSuccessorException(((ExceptionRangeCFG)object2).getHandler());
                ((ExceptionRangeCFG)object2).getProtectedRange().add(basicBlock2);
            }
            basicBlock2.addSuccessor(basicBlock);
            if (controlFlowGraph.getFirst() != basicBlock) continue;
            controlFlowGraph.setFirst(basicBlock2);
        }
    }

    public static void removeEmptyRanges(ControlFlowGraph object) {
        object = ((ControlFlowGraph)object).getExceptions();
        int n = object.size() - 1;
        while (n >= 0) {
            ExceptionRangeCFG exceptionRangeCFG = (ExceptionRangeCFG)object.get(n);
            boolean bl = true;
            Iterator iterator = exceptionRangeCFG.getProtectedRange().iterator();
            while (iterator.hasNext()) {
                if (((BasicBlock)iterator.next()).getSeq().isEmpty()) continue;
                bl = false;
                break;
            }
            if (bl) {
                iterator = exceptionRangeCFG.getProtectedRange().iterator();
                while (iterator.hasNext()) {
                    ((BasicBlock)iterator.next()).removeSuccessorException(exceptionRangeCFG.getHandler());
                }
                object.remove(n);
            }
            --n;
        }
    }

    public static void removeCircularRanges(ControlFlowGraph object) {
        GenericDominatorEngine genericDominatorEngine = new GenericDominatorEngine(new ExceptionDeobfuscator$1((ControlFlowGraph)object));
        genericDominatorEngine.orderNodes();
        object = ((ControlFlowGraph)object).getExceptions();
        int n = object.size() - 1;
        while (n >= 0) {
            ExceptionRangeCFG exceptionRangeCFG = (ExceptionRangeCFG)object.get(n);
            BasicBlock basicBlock = exceptionRangeCFG.getHandler();
            List list = exceptionRangeCFG.getProtectedRange();
            if (list.contains(basicBlock)) {
                GenericDominatorEngine genericDominatorEngine2 = genericDominatorEngine;
                ExceptionRangeCFG exceptionRangeCFG2 = exceptionRangeCFG;
                ArrayList<BasicBlock> arrayList = new ArrayList<BasicBlock>();
                LinkedList<BasicBlock> linkedList = new LinkedList<BasicBlock>();
                HashSet<BasicBlock> hashSet = new HashSet<BasicBlock>();
                BasicBlock basicBlock2 = exceptionRangeCFG2.getHandler();
                linkedList.addFirst(basicBlock2);
                while (!linkedList.isEmpty()) {
                    BasicBlock basicBlock3 = (BasicBlock)linkedList.removeFirst();
                    hashSet.add(basicBlock3);
                    if (!exceptionRangeCFG2.getProtectedRange().contains(basicBlock3) || !genericDominatorEngine2.isDominator(basicBlock3, basicBlock2)) continue;
                    arrayList.add(basicBlock3);
                    Object object2 = new ArrayList(basicBlock3.getSuccs());
                    object2.addAll(basicBlock3.getSuccExceptions());
                    object2 = object2.iterator();
                    while (object2.hasNext()) {
                        basicBlock3 = (BasicBlock)object2.next();
                        if (hashSet.contains(basicBlock3)) continue;
                        linkedList.add(basicBlock3);
                    }
                }
                ArrayList<BasicBlock> arrayList2 = arrayList;
                if (arrayList2.size() < list.size() || list.size() == 1) {
                    for (BasicBlock basicBlock4 : arrayList2) {
                        basicBlock4.removeSuccessorException(basicBlock);
                        list.remove(basicBlock4);
                    }
                }
                if (list.isEmpty()) {
                    object.remove(n);
                }
            }
            --n;
        }
    }

    public static boolean hasObfuscatedExceptions(ControlFlowGraph object3) {
        HashSet<Object> hashSet;
        BasicBlock basicBlock = ((ControlFlowGraph)object3).getFirst();
        Object object2 = new HashMap();
        for (ExceptionRangeCFG exceptionRangeCFG : ((ControlFlowGraph)object3).getExceptions()) {
            hashSet = (HashSet<Object>)((HashMap)object2).get(exceptionRangeCFG.getHandler());
            if (hashSet == null) {
                hashSet = new HashSet();
                ((HashMap)object2).put(exceptionRangeCFG.getHandler(), hashSet);
            }
            hashSet.addAll(exceptionRangeCFG.getProtectedRange());
        }
        for (Map.Entry entry : ((HashMap)object2).entrySet()) {
            hashSet = new HashSet<Object>();
            for (Object object2 : (HashSet)entry.getValue()) {
                HashSet hashSet2 = new HashSet(((BasicBlock)object2).getPreds());
                hashSet2.removeAll((Collection)entry.getValue());
                if (hashSet2.isEmpty()) continue;
                hashSet.add(object2);
            }
            if (hashSet.isEmpty() || hashSet.size() <= 1 && !((HashSet)entry.getValue()).contains(basicBlock)) continue;
            return true;
        }
        return false;
    }
}

